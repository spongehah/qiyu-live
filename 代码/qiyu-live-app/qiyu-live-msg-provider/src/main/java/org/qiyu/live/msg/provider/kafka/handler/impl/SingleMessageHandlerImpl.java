package org.qiyu.live.msg.provider.kafka.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.im.constants.AppIdEnum;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.interfaces.ImRouterRpc;
import org.qiyu.live.msg.provider.dto.MessageDTO;
import org.qiyu.live.msg.provider.enums.ImMsgBizCodeEum;
import org.qiyu.live.msg.provider.kafka.handler.MessageHandler;
import org.springframework.stereotype.Component;

@Component
public class SingleMessageHandlerImpl implements MessageHandler {
    
    @DubboReference
    private ImRouterRpc routerRpc; 
    
    @Override
    public void onMsgReceive(ImMsgBody imMsgBody) {
        int bizCode = imMsgBody.getBizCode();
        // 直播间的聊天消息
        if (bizCode == ImMsgBizCodeEum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode()) {
            MessageDTO messageDTO = JSON.parseObject(imMsgBody.getData(), MessageDTO.class);
            //还不是直播间业务，暂时不做过多的处理

            ImMsgBody respMsgBody = new ImMsgBody();
            //这里的userId设置的是objectId，因为是发送给对方客户端
            respMsgBody.setUserId(messageDTO.getObjectId());
            respMsgBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
            respMsgBody.setBizCode(ImMsgBizCodeEum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("senderId", messageDTO.getUserId());
            jsonObject.put("content", messageDTO.getContent());
            respMsgBody.setData(jsonObject.toJSONString());
            //将消息推送给router进行转发给im服务器
            routerRpc.sendMsg(respMsgBody);
        }
    }
}
