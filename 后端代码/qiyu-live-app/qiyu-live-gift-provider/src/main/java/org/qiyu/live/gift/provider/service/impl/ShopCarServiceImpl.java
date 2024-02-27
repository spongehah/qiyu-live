package org.qiyu.live.gift.provider.service.impl;

import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.ShopCarItemRespDTO;
import org.qiyu.live.gift.dto.ShopCarReqDTO;
import org.qiyu.live.gift.dto.ShopCarRespDTO;
import org.qiyu.live.gift.dto.SkuInfoDTO;
import org.qiyu.live.gift.provider.dao.po.SkuInfoPO;
import org.qiyu.live.gift.provider.service.IShopCarService;
import org.qiyu.live.gift.provider.service.ISkuInfoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShopCarServiceImpl implements IShopCarService {
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private ISkuInfoService skuInfoService;

    /**
     * 因为是以直播间为维度的购物车，所以不需要持久化，用缓存即可
     */
    @Override
    public Boolean addCar(ShopCarReqDTO shopCarReqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(shopCarReqDTO.getUserId(), shopCarReqDTO.getRoomId());
        redisTemplate.opsForHash().put(cacheKey, String.valueOf(shopCarReqDTO.getSkuId()), 1);
        return true;
    }

    @Override
    public Boolean removeFromCar(ShopCarReqDTO shopCarReqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(shopCarReqDTO.getUserId(), shopCarReqDTO.getRoomId());
        redisTemplate.opsForHash().delete(cacheKey, String.valueOf(shopCarReqDTO.getSkuId()));
        return true;
    }

    @Override
    public Boolean clearShopCar(ShopCarReqDTO shopCarReqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(shopCarReqDTO.getUserId(), shopCarReqDTO.getRoomId());
        redisTemplate.delete(cacheKey);
        return true;
    }

    @Override
    public Boolean addCarItemNum(ShopCarReqDTO shopCarReqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(shopCarReqDTO.getUserId(), shopCarReqDTO.getRoomId());
        redisTemplate.opsForHash().increment(cacheKey, String.valueOf(shopCarReqDTO.getSkuId()), 1);
        return true;
    }

    @Override
    public ShopCarRespDTO getCarInfo(ShopCarReqDTO shopCarReqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(shopCarReqDTO.getUserId(), shopCarReqDTO.getRoomId());
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(cacheKey);
        if (CollectionUtils.isEmpty(entries)) {
            return new ShopCarRespDTO();
        }
        Map<Long, Integer> skuCountMap = new HashMap<>(entries.size());
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            skuCountMap.put(Long.valueOf((String) entry.getKey()), (Integer) entry.getValue());
        }
        List<Long> skuIdList = new ArrayList<>(skuCountMap.keySet());
        List<SkuInfoPO> skuInfoPOS = skuInfoService.queryBySkuIds(skuIdList);
        ShopCarRespDTO shopCarRespDTO = new ShopCarRespDTO();
        shopCarRespDTO.setRoomId(shopCarReqDTO.getRoomId());
        shopCarRespDTO.setUserId(shopCarReqDTO.getUserId());
        List<ShopCarItemRespDTO> itemList = new ArrayList<>();
        skuInfoPOS.forEach(skuInfoPO -> {
            ShopCarItemRespDTO item = new ShopCarItemRespDTO();
            item.setSkuInfoDTO(ConvertBeanUtils.convert(skuInfoPO, SkuInfoDTO.class));
            item.setCount(skuCountMap.get(skuInfoPO.getSkuId()));
            itemList.add(item);
        });
        shopCarRespDTO.setSkuCarItemRespDTODTOS(itemList);
        return shopCarRespDTO;
    }

}
