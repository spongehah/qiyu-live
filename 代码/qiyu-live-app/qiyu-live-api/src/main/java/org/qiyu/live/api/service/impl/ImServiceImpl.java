package org.qiyu.live.api.service.impl;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.qiyu.live.api.service.ImService;
import org.qiyu.live.api.vo.resp.ImConfigVO;
import org.qiyu.live.im.constants.AppIdEnum;
import org.qiyu.live.im.interfaces.ImTokenRpc;
import org.qiyu.live.web.starter.context.QiyuRequestContext;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ImServiceImpl implements ImService {

    @DubboReference
    private ImTokenRpc imTokenRpc;
    @Resource
    private DiscoveryClient discoveryClient;

    // 这里是通过DiscoveryClient获取Nacos中的注册信息，我们还可以通过在ImCoreServer中写一个rpc方法，和构建Router功能是一样，获取我们在启动参数中的添加的DUBBO注册ip
    @Override
    public ImConfigVO getImConfig() {
        ImConfigVO imConfigVO = new ImConfigVO();
        imConfigVO.setToken(imTokenRpc.createImLoginToken(QiyuRequestContext.getUserId(), AppIdEnum.QIYU_LIVE_BIZ.getCode()));
        // 获取到在Nacos中注册的对应服务名的实例集合
        List<ServiceInstance> serverInstanceList = discoveryClient.getInstances("qiyu-live-im-core-server");
        // 打乱集合顺序
        Collections.shuffle(serverInstanceList);
        ServiceInstance serviceInstance = serverInstanceList.get(0);
        imConfigVO.setTcpImServerAddress(serviceInstance.getHost() + ":8085");
        imConfigVO.setWsImServerAddress(serviceInstance.getHost() + ":8086");
        return imConfigVO;
    }
}
