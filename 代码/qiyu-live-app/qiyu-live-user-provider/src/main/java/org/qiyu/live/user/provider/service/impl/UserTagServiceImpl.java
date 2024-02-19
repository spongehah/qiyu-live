package org.qiyu.live.user.provider.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.qiyu.live.user.constants.UserTagFieldNameConstants;
import org.qiyu.live.user.constants.UserTagsEnum;
import org.qiyu.live.user.dto.UserTagDTO;
import org.qiyu.live.user.provider.dao.mapper.IUserTagMapper;
import org.qiyu.live.user.provider.dao.po.UserTagPo;
import org.qiyu.live.user.provider.kafka.KafkaCodeConstants;
import org.qiyu.live.user.provider.kafka.KafkaObject;
import org.qiyu.live.user.provider.service.IUserTagService;
import org.qiyu.live.user.utils.TagInfoUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class UserTagServiceImpl extends ServiceImpl<IUserTagMapper, UserTagPo> implements IUserTagService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    // private StringRedisTemplate stringRedisTemplate;
    
    @Resource(name = "redisTemplate")
    private RedisTemplate<String, UserTagDTO> userTagDTORedisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;
    
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Override
    public boolean setTag(Long userId, UserTagsEnum userTagsEnum) {
        boolean updateStatus = baseMapper.setTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
        if (updateStatus) {//为true说明是有记录且是第一次设置（我们的sql语句是当前没有设置该tag才进行设置，即第一次设置）
            //更改操作，删除缓存
            userTagDTORedisTemplate.delete(userProviderCacheKeyBuilder.buildTagInfoKey(userId));
            //TODO 计划更改为canal实现延迟双删或双写
            KafkaObject kafkaObject = new KafkaObject(KafkaCodeConstants.USER_TAG_INFO_CODE, userId.toString());
            kafkaTemplate.send("user-delete-cache", JSONUtil.toJsonStr(kafkaObject));
            return true;
        }
        //没成功：说明是没此行记录，或者重复设置
        UserTagPo userTagPo = baseMapper.selectById(userId);
        if(userTagPo != null) {//重复设置，直接返回false
            return false;
        }
        //无记录，插入
        //使用Redis的setnx命令构建分布式锁（目前有很多缺陷）
        String lockKey = userProviderCacheKeyBuilder.buildTagLockKey(userId);
        // SimpleRedisLock lock = new SimpleRedisLock(lockKey, stringRedisTemplate);
        try {
            // boolean isLock = lock.tryLock(3L);
            Boolean isLock = redisTemplate.opsForValue().setIfAbsent(lockKey, "-1", Duration.ofSeconds(3L));
            if(BooleanUtil.isFalse(isLock)) {//说明有其他线程正在进行插入
                return false;
            }
            userTagPo = new UserTagPo();
            userTagPo.setUserId(userId);
            baseMapper.insert(userTagPo);
        } finally {
            // lock.unlock();
            redisTemplate.delete(lockKey);
        }
        System.out.println("设置标签册成功！");
        //插入后再修改返回
        return baseMapper.setTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
    }

    @Override
    public boolean cancelTag(Long userId, UserTagsEnum userTagsEnum) {
        boolean cancelStatus = baseMapper.cancelTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
        if(!cancelStatus) {
            return false;
        }
        //更改操作，删除缓存
        userTagDTORedisTemplate.delete(userProviderCacheKeyBuilder.buildTagInfoKey(userId));
        //TODO 计划更改为canal实现延迟双删或双写
        KafkaObject kafkaObject = new KafkaObject(KafkaCodeConstants.USER_TAG_INFO_CODE, userId.toString());
        kafkaTemplate.send("user-delete-cache", JSONUtil.toJsonStr(kafkaObject));
        return true;
    }

    @Override
    public boolean containTag(Long userId, UserTagsEnum userTagsEnum) {
        UserTagDTO userTagDTO = this.queryTagInfoFromRedisCache(userId);
        if (userTagDTO == null) {
            return false;
        }
        String fieldName = userTagsEnum.getFieldName();
        if (fieldName.equals(UserTagFieldNameConstants.TAT_INFO_01)) {
            return TagInfoUtils.isContain(userTagDTO.getTagInfo01(), userTagsEnum.getTag());
        } else if (fieldName.equals(UserTagFieldNameConstants.TAT_INFO_02)) {
            return TagInfoUtils.isContain(userTagDTO.getTagInfo02(), userTagsEnum.getTag());
        } else if (fieldName.equals(UserTagFieldNameConstants.TAT_INFO_03)) {
            return TagInfoUtils.isContain(userTagDTO.getTagInfo03(), userTagsEnum.getTag());
        }
        return false;
    }

    /**
     * 从Redis中查询缓存的用户标签
     * @param userId
     * @return
     */
    private UserTagDTO queryTagInfoFromRedisCache(Long userId) {
        String key = userProviderCacheKeyBuilder.buildTagInfoKey(userId);
        UserTagDTO userTagDTO = userTagDTORedisTemplate.opsForValue().get(key);
        if(userTagDTO != null) {
            return userTagDTO;
        }
        UserTagPo userTagPo = baseMapper.selectById(userId);
        if(userTagPo == null) {
            return null;
        }
        userTagDTO = BeanUtil.copyProperties(userTagPo, UserTagDTO.class);
        userTagDTORedisTemplate.opsForValue().set(key, userTagDTO, 30L, TimeUnit.MINUTES);
        return userTagDTO;
    }
}
