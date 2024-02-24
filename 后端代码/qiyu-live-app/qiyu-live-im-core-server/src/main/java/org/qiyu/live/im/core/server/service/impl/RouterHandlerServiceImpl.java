package org.qiyu.live.im.core.server.service.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.idea.qiyu.live.framework.redis.starter.id.RedisSeqIdHelper;
import org.idea.qiyu.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import org.qiyu.live.im.constants.ImMsgCodeEnum;
import org.qiyu.live.im.core.server.common.ChannelHandlerContextCache;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.core.server.service.IMsgAckCheckService;
import org.qiyu.live.im.core.server.service.IRouterHandlerService;
import org.qiyu.live.im.dto.ImMsgBody;
import org.springframework.stereotype.Service;

@Service
public class RouterHandlerServiceImpl implements IRouterHandlerService {
    
    @Resource
    private IMsgAckCheckService iMsgAckCheckService;
    @Resource
    private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private RedisSeqIdHelper redisSeqIdHelper;

    @Override
    public void onReceive(ImMsgBody imMsgBody) {
        // 设置消息的唯一标识msgId
        Long msgId = redisSeqIdHelper.nextId(cacheKeyBuilder.buildImAckMsgIdKey());
        imMsgBody.setMsgId(String.valueOf(msgId));
        if (sendMsgToClient(imMsgBody)) {
            //收到相应给客户端B的数据时，记录下客户端B还未发送ACK的消息(第一次记录)
            iMsgAckCheckService.recordMsgAck(imMsgBody, 1);
            //发送延时消息，进行未接收到消息的重复消费
            iMsgAckCheckService.sendDelayMsg(imMsgBody);
        }
    }

    /**
     * 对发送消息给客户端做一下封装，方便外界只调用这部分代码
     */
    @Override
    public boolean sendMsgToClient(ImMsgBody imMsgBody) {
        // 需要进行消息通知的userId
        Long userId = imMsgBody.getUserId();
        ChannelHandlerContext ctx = ChannelHandlerContextCache.get(userId);
        // 消息到达时，对应客户端未下线
        if (ctx != null) {
            ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(imMsgBody));
            //给目标客户端回写消息
            ctx.writeAndFlush(respMsg);
            return true;
        }
        return false;
    }
}
