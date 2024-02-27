package org.qiyu.live.common.interfaces.topic;


public class GiftProviderTopicNames {

    /**
     * 移除礼物信息的缓存
     */
    public static final String REMOVE_GIFT_CACHE = "remove_gift_cache";

    /**
     * 发送礼物消息
     */
    public static final String SEND_GIFT = "send_gift";
    /**
     * 用户红包雨抢红包消息topic
     */
    public static final String RECEIVE_RED_PACKET = "receive-red-packet";
    /**
     * 回滚未支付订单库存的topic
     */
    public static final String ROLL_BACK_STOCK = "rollback-stock";
    /**
     * 开启直播时同步商品库存到Redis中的topic
     */
    public static final String START_LIVING_ROOM = "start-living-room";
}
