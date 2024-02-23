package org.qiyu.live.msg.provider.kafka.handler.impl;

import com.alibaba.fastjson2.JSON;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.im.constants.AppIdEnum;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.interfaces.ImRouterRpc;
import org.qiyu.live.living.interfaces.dto.LivingRoomReqDTO;
import org.qiyu.live.living.interfaces.rpc.ILivingRoomRpc;
import org.qiyu.live.msg.provider.dto.MessageDTO;
import org.qiyu.live.im.router.constants.ImMsgBizCodeEnum;
import org.qiyu.live.msg.provider.kafka.handler.MessageHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SingleMessageHandlerImpl implements MessageHandler {
    
    @DubboReference
    private ImRouterRpc routerRpc; 
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;
    
    @Override
    public void onMsgReceive(ImMsgBody imMsgBody) {
        int bizCode = imMsgBody.getBizCode();
        // 直播间的聊天消息
        if (bizCode == ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode()) {
            //一个人发送，n个人接收
            //根据roomId去调用rpc方法查询直播间在线userId
            MessageDTO messageDTO = JSON.parseObject(imMsgBody.getData(), MessageDTO.class);
            Integer roomId = messageDTO.getRoomId();
            LivingRoomReqDTO reqDTO = new LivingRoomReqDTO();
            reqDTO.setRoomId(roomId);
            reqDTO.setAppId(imMsgBody.getAppId());
            //自己不用发
            List<Long> userIdList = livingRoomRpc.queryUserIdsByRoomId(reqDTO).stream().filter(x -> !x.equals(imMsgBody.getUserId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(userIdList)) {
                System.out.println("[SingleMessageHandlerImpl] 要转发的userIdList为空");
                return;
            }
            
            List<ImMsgBody> respMsgBodies = new ArrayList<>();
            userIdList.forEach(userId -> {
                ImMsgBody respMsgBody = new ImMsgBody();
                respMsgBody.setAppId(AppIdEnum.QIYU_LIVE_BIZ.getCode());
                respMsgBody.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_IM_CHAT_MSG_BIZ.getCode());
                respMsgBody.setData(JSON.toJSONString(messageDTO));
                //设置发送目标对象的id
                respMsgBody.setUserId(userId);
                respMsgBodies.add(respMsgBody);
            });
            //将消息推送给router进行转发给im服务器
            routerRpc.batchSendMsg(respMsgBodies);
        }
    }
}
