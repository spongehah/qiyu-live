package org.qiyu.live.id.generate.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.id.generate.interfaces.IdGenerateRpc;
import org.qiyu.live.id.generate.service.IdGenerateService;

/**
 * @Author idea
 * @Date: Created in 19:57 2023/5/25
 * @Description
 */
@DubboService
public class IdGenerateRpcImpl implements IdGenerateRpc {

    @Resource
    private IdGenerateService idGenerateService;

    @Override
    public Long getSeqId(Integer id) {
        return idGenerateService.getSeqId(id);
    }

    @Override
    public Long getUnSeqId(Integer id) {
        return idGenerateService.getUnSeqId(id);
    }
}
