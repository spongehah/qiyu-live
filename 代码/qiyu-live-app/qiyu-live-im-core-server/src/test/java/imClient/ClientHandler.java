package imClient;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.qiyu.live.im.constants.ImMsgCodeEnum;
import org.qiyu.live.im.core.server.common.ImMsg;
import org.qiyu.live.im.dto.ImMsgBody;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ImMsg imMsg = (ImMsg) msg;
        if (imMsg.getCode() == ImMsgCodeEnum.IM_BIZ_MSG.getCode()) {
            //是业务消息，就要发回ACK
            ImMsgBody imMsgBody = JSON.parseObject(new String(imMsg.getBody()), ImMsgBody.class);
            ImMsgBody ackBody = new ImMsgBody();
            ackBody.setUserId(imMsgBody.getUserId());
            ackBody.setAppId(imMsgBody.getAppId());
            ackBody.setMsgId(imMsgBody.getMsgId());
            ImMsg ackMsg = ImMsg.build(ImMsgCodeEnum.IM_ACK_MSG.getCode(), JSON.toJSONString(ackBody));
            ctx.writeAndFlush(ackMsg);
        }
        System.out.println("【服务端响应数据】 result is " + new String(imMsg.getBody()));
    }
}
