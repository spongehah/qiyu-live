package org.qiyu.live.gift.interfaces;


public interface ISkuStockInfoRpc {

    /**
     * 根据stuId更新库存值
     */
    boolean decrStockNumBySkuId(Long skuId, Integer num);

    /**
     * 预热库存信息：将库存存入到Redis
     */
    boolean prepareStockInfo(Long anchorId);

    /**
     * 从Redis中查询缓存的库存值
     */
    Integer queryStockNum(Long skuId);

    /**
     * 同步库存数据到MySQL
     */
    boolean syncStockNumToMySQL(Long anchor);
}
