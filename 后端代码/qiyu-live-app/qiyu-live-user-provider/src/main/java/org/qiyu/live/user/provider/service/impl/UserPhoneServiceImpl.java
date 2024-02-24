package org.qiyu.live.user.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.id.RedisSeqIdHelper;
import org.idea.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.enums.CommonStatusEnum;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.common.interfaces.utils.DESUtils;
import org.qiyu.live.user.dto.UserDTO;
import org.qiyu.live.user.dto.UserLoginDTO;
import org.qiyu.live.user.dto.UserPhoneDTO;
import org.qiyu.live.user.provider.dao.mapper.IUserPhoneMapper;
import org.qiyu.live.user.provider.dao.po.UserPhonePO;
import org.qiyu.live.user.provider.service.IUserPhoneService;
import org.qiyu.live.user.provider.service.IUserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserPhoneServiceImpl implements IUserPhoneService {

    @Resource
    private IUserPhoneMapper userPhoneMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    @Resource
    private RedisSeqIdHelper redisSeqIdHelper;

    @Resource
    private IUserService userService;

    @Override
    public UserLoginDTO login(String phone) {
        // 参数校验
        if (StringUtils.isEmpty(phone)) {
            return null;
        }
        // 是否注册通过
        UserPhoneDTO userPhoneDTO = this.queryByPhone(phone);
        // 如果注册过，创建token并返回
        if (userPhoneDTO != null) {
            return UserLoginDTO.loginSuccess(userPhoneDTO.getUserId(),"");
        }
        // 没注册过，就注册登录
        return this.registerAndLogin(phone);
    }

    /**
     * 注册新手机号用户
     *
     * @return
     */
    private UserLoginDTO registerAndLogin(String phone) {
        Long userId = redisSeqIdHelper.nextId("user");
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setNickName("旗鱼用户-" + userId);
        // 插入用户表
        userService.insertOne(userDTO);
        UserPhonePO userPhonePO = new UserPhonePO();
        userPhonePO.setUserId(userId);
        userPhonePO.setPhone(DESUtils.encrypt(phone));
        userPhonePO.setStatus(CommonStatusEnum.VALID_STATUS.getCode());
        userPhoneMapper.insert(userPhonePO);
        // 需要删除空值对象，因为我们查询有无对应用户的时候，缓存了空对象，这里我们创建了就可以删除了
        redisTemplate.delete(userProviderCacheKeyBuilder.buildUserPhoneObjKey(phone));
        // return UserLoginDTO.loginSuccess(userId, this.createAndSaveLoginToken(userId));
        return UserLoginDTO.loginSuccess(userId, null);
    }

    private String createAndSaveLoginToken(Long userId) {
        String token = UUID.randomUUID().toString();
        String redisKey = userProviderCacheKeyBuilder.buildUserLoginTokenKey(token);
        redisTemplate.opsForValue().set(redisKey, userId, 30L, TimeUnit.DAYS);
        return token;
    }

    @Override
    public UserPhoneDTO queryByPhone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return null;
        }
        String redisKey = userProviderCacheKeyBuilder.buildUserPhoneObjKey(phone);
        UserPhoneDTO userPhoneDTO = (UserPhoneDTO) redisTemplate.opsForValue().get(redisKey);
        if (userPhoneDTO != null) {
            if (userPhoneDTO.getUserId() == null) {// 缓存穿透校验
                return null;
            }
            return userPhoneDTO;
        }
        // 没有缓存，从数据库查询
        userPhoneDTO = this.queryByPhoneFromDB(phone);
        if (userPhoneDTO != null) {
            userPhoneDTO.setPhone(DESUtils.decrypt(userPhoneDTO.getPhone()));
            redisTemplate.opsForValue().set(redisKey, userPhoneDTO, 30L, TimeUnit.MINUTES);
            return userPhoneDTO;
        }
        // 缓存穿透：缓存空对象
        redisTemplate.opsForValue().set(redisKey, new UserPhoneDTO(), 1L, TimeUnit.MINUTES);
        return null;
    }

    private UserPhoneDTO queryByPhoneFromDB(String phone) {
        LambdaQueryWrapper<UserPhonePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPhonePO::getPhone, DESUtils.encrypt(phone)).eq(UserPhonePO::getStatus, CommonStatusEnum.VALID_STATUS.getCode()).last("limit 1");
        return ConvertBeanUtils.convert(userPhoneMapper.selectOne(queryWrapper), UserPhoneDTO.class);
    }

    @Override
    public List<UserPhoneDTO> queryByUserId(Long userId) {
        // 参数校验
        if (userId == null) {
            return Collections.emptyList();
        }
        String redisKey = userProviderCacheKeyBuilder.buildUserPhoneListKey(userId);
        List<Object> userPhoneList = redisTemplate.opsForList().range(redisKey, 0, -1);
        // Redis有缓存
        if (!CollectionUtils.isEmpty(userPhoneList)) {
            if (((UserPhoneDTO) userPhoneList.get(0)).getUserId() == null) {// 缓存穿透校验
                return Collections.emptyList();
            }
            return userPhoneList.stream().map(x -> (UserPhoneDTO) x).collect(Collectors.toList());
        }
        // 没有缓存，查询MySQL
        List<UserPhoneDTO> userPhoneDTOS = this.queryByUserIdFromDB(userId);
        if (!CollectionUtils.isEmpty(userPhoneDTOS)) {
            userPhoneDTOS.stream().forEach(x -> x.setPhone(DESUtils.decrypt(x.getPhone())));
            redisTemplate.opsForList().leftPushAll(redisKey, userPhoneDTOS.toArray());
            redisTemplate.expire(redisKey, 30L, TimeUnit.MINUTES);
            return userPhoneDTOS;
        }
        // 缓存穿透：缓存空对象
        redisTemplate.opsForList().leftPush(redisKey, new UserPhoneDTO());
        redisTemplate.expire(redisKey, 1L, TimeUnit.MINUTES);
        return Collections.emptyList();
    }

    private List<UserPhoneDTO> queryByUserIdFromDB(Long userId) {
        LambdaQueryWrapper<UserPhonePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPhonePO::getUserId, userId).eq(UserPhonePO::getStatus, CommonStatusEnum.VALID_STATUS.getCode());
        return ConvertBeanUtils.convertList(userPhoneMapper.selectList(queryWrapper), UserPhoneDTO.class);
    }
}
