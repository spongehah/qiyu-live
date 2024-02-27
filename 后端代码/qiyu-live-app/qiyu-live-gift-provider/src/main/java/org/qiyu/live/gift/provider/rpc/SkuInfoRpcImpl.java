package org.qiyu.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.idea.qiyu.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.qiyu.live.bank.dto.PayProductDTO;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.gift.dto.SkuDetailInfoDTO;
import org.qiyu.live.gift.dto.SkuInfoDTO;
import org.qiyu.live.gift.interfaces.ISkuInfoRpc;
import org.qiyu.live.gift.provider.service.IAnchorShopInfoService;
import org.qiyu.live.gift.provider.service.ISkuInfoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@DubboService
public class SkuInfoRpcImpl implements ISkuInfoRpc {

    @Resource
    private ISkuInfoService skuInfoService;
    @Resource
    private IAnchorShopInfoService anchorShopInfoService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public List<SkuInfoDTO> queryByAnchorId(Long anchorId) {
        String cacheKey = cacheKeyBuilder.buildSkuDetailInfoMap(anchorId);
        List<SkuInfoDTO> skuInfoDTOS = redisTemplate.opsForHash().values(cacheKey).stream().map(x -> (SkuInfoDTO) x).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(skuInfoDTOS)) {
            if (skuInfoDTOS.get(0).getSkuId() == null) {
                return Collections.emptyList();
            }
            return skuInfoDTOS;
        }
        List<Long> skuIdList = anchorShopInfoService.querySkuIdsByAnchorId(anchorId);
        if (CollectionUtils.isEmpty(skuIdList)) {
            return Collections.emptyList();
        }
        skuInfoDTOS = ConvertBeanUtils.convertList(skuInfoService.queryBySkuIds(skuIdList), SkuInfoDTO.class);
        if (CollectionUtils.isEmpty(skuInfoDTOS)) {
            redisTemplate.opsForHash().put(cacheKey, -1, new PayProductDTO());
            redisTemplate.expire(cacheKey, 1L, TimeUnit.MINUTES);
            return Collections.emptyList();
        }
        // 使用Redis进行缓存
        Map<String, SkuInfoDTO> skuInfoMap = skuInfoDTOS.stream().collect(Collectors.toMap(x -> String.valueOf(x.getSkuId()), x -> x));
        redisTemplate.opsForHash().putAll(cacheKey, skuInfoMap);
        redisTemplate.expire(cacheKey, 30L, TimeUnit.MINUTES);
        return skuInfoDTOS;
    }

    @Override
    public SkuDetailInfoDTO queryBySkuId(Long skuId, Long anchorId) {
        String cacheKey = cacheKeyBuilder.buildSkuDetailInfoMap(anchorId);
        SkuInfoDTO skuInfoDTO = (SkuInfoDTO) redisTemplate.opsForHash().get(cacheKey, String.valueOf(skuId));
        if (skuInfoDTO != null) {
            return ConvertBeanUtils.convert(skuInfoDTO, SkuDetailInfoDTO.class);
        }
        skuInfoDTO = ConvertBeanUtils.convert(skuInfoService.queryBySkuId(skuId), SkuInfoDTO.class);
        if (skuInfoDTO != null) {
            redisTemplate.opsForHash().put(cacheKey, String.valueOf(skuId), skuInfoDTO);
        }
        return ConvertBeanUtils.convert(skuInfoDTO, SkuDetailInfoDTO.class);
    }
}
