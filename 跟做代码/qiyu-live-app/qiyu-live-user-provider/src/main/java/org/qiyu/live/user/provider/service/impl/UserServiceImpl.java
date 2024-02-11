package org.qiyu.live.user.provider.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.idea.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.qiyu.live.user.dto.UserDTO;
import org.qiyu.live.user.provider.dao.mapper.IUserMapper;
import org.qiyu.live.user.provider.dao.po.UserPO;
import org.qiyu.live.user.provider.kafka.KafkaCodeConstants;
import org.qiyu.live.user.provider.kafka.KafkaObject;
import org.qiyu.live.user.provider.service.IUserService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<IUserMapper, UserPO> implements IUserService {
    
    @Resource
    private RedisTemplate<String, UserDTO> redisTemplate;
    
    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;
    
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public UserDTO getUserById(Long userId) {
        if(userId == null) {
            return null;
        }
        String key = userProviderCacheKeyBuilder.buildUserInfoKey(userId);
        UserDTO userDTO = redisTemplate.opsForValue().get(key);
        if(userDTO != null) {
            return userDTO;
        }
        userDTO = BeanUtil.copyProperties(baseMapper.selectById(userId), UserDTO.class);
        if(userDTO != null) {
            redisTemplate.opsForValue().set(key, userDTO, 30L, TimeUnit.MINUTES);
        }
        return userDTO;
    }

    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        if(userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        baseMapper.updateById(BeanUtil.copyProperties(userDTO, UserPO.class));
        //更改操作，删除缓存
        redisTemplate.delete(userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId()));
        //TODO 计划更改为canal实现延迟双删或双写
        KafkaObject kafkaObject = new KafkaObject(KafkaCodeConstants.USER_INFO_CODE, userDTO.getUserId().toString());
        kafkaTemplate.send("user-delete-cache", JSONUtil.toJsonStr(kafkaObject));
        log.info("Kafka发送延迟双删消息成功，用户ID：{}", userDTO.getUserId());
        return true;
    }

    @Override
    public boolean insertOne(UserDTO userDTO) {
        if(userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        baseMapper.insert(BeanUtil.copyProperties(userDTO, UserPO.class));
        return true;
    }

    @Override
    public Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList) {
        if(CollectionUtils.isEmpty(userIdList)) {
            return Collections.emptyMap();
        }
        //user的id都大于10000
        userIdList = userIdList.stream().filter(id -> id > 10000).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(userIdList)) {
            return Collections.emptyMap();
        }
        
        //先查询Redis缓存
        List<String> multiKeyList = userIdList.stream()
                .map(userId -> userProviderCacheKeyBuilder.buildUserInfoKey(userId)).collect(Collectors.toList());
        List<UserDTO> userDTOList = redisTemplate.opsForValue().multiGet(multiKeyList).stream().filter(x -> x != null).collect(Collectors.toList());
        //若Redis查询出来的数据数量和要查询的数量相等，直接返回
        if(!CollectionUtils.isEmpty(userDTOList) && userDTOList.size() == userIdList.size()) {
            return userDTOList.stream().collect(Collectors.toMap(UserDTO::getUserId, userDTO -> userDTO));
        }
        //不相等，去MySQL查询无缓存的数据
        List<Long> userIdInCacheList = userDTOList.stream().map(UserDTO::getUserId).collect(Collectors.toList());
        List<Long> userIdNotInCacheList = userIdList.stream().filter(userId -> !userIdInCacheList.contains(userId)).collect(Collectors.toList());
        //为了防止sharding-jdbc笛卡尔积路由，对id进行分组
        Map<Long, List<Long>> userIdMap = userIdNotInCacheList.stream().collect(Collectors.groupingBy(userId -> userId % 100));
        List<UserDTO> dbQueryList = new CopyOnWriteArrayList<>();
        userIdMap.values().parallelStream().forEach(queryUserIdList -> {
            dbQueryList.addAll(BeanUtil.copyToList(baseMapper.selectBatchIds(queryUserIdList), UserDTO.class));
        });
        //查询MySQL不为空，缓存进Redis
        if(!CollectionUtils.isEmpty(dbQueryList)) {
            Map<String, UserDTO> multiSaveMap = dbQueryList.stream().collect(Collectors.toMap(userDTO -> userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId()), x -> x));
            redisTemplate.opsForValue().multiSet(multiSaveMap);
            //mset不能设置过期时间，使用管道设置，减少网路IO
            redisTemplate.executePipelined(new SessionCallback<Object>() {
                @Override
                public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                    for (String key : multiSaveMap.keySet()) {
                        operations.expire((K) key, createRandomExpireTime(), TimeUnit.SECONDS);
                    }
                    return null;
                }
            });
            userDTOList.addAll(dbQueryList);
        }
        return userDTOList.stream().collect(Collectors.toMap(UserDTO::getUserId, userDTO -> userDTO));
    }
    
    //生成随机过期时间，单位：秒
    private long createRandomExpireTime() {
        return ThreadLocalRandom.current().nextLong(1000) + 60 * 30;//30min + 1000s
    }
}
