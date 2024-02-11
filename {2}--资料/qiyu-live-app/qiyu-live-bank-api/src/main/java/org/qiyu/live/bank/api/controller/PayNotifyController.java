package org.qiyu.live.bank.api.controller;

import jakarta.annotation.Resource;
import org.qiyu.live.bank.api.service.IPayNotifyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 处理支付回调的逻辑
 *
 * @Author idea
 * @Date: Created in 21:49 2023/8/19
 * @Description
 */
@RestController
@RequestMapping("/payNotify")
public class PayNotifyController {

    @Resource
    private IPayNotifyService payNotifyService;

    @PostMapping("/wxNotify")
    public String wxNotify(@RequestParam("param") String param) {
        return payNotifyService.notifyHandler(param);
    }

}
