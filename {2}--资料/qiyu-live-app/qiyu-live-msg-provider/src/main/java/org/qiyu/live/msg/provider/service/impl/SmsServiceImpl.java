package org.qiyu.live.msg.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperProxy;
import org.idea.qiyu.live.framework.redis.starter.key.MsgProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.utils.DESUtils;
import org.qiyu.live.msg.dto.MsgCheckDTO;
import org.qiyu.live.msg.enums.MsgSendResultEnum;
import org.qiyu.live.msg.provider.config.ApplicationProperties;
import org.qiyu.live.msg.provider.config.SmsTemplateIDEnum;
import org.qiyu.live.msg.provider.config.ThreadPoolManager;
import org.qiyu.live.msg.provider.dao.mapper.SmsMapper;
import org.qiyu.live.msg.provider.dao.po.SmsPO;
import org.qiyu.live.msg.provider.service.ISmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * @Author idea
 * @Date: Created in 17:30 2023/6/11
 * @Description
 */
@Service
public class SmsServiceImpl implements ISmsService {

    private static Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private MsgProviderCacheKeyBuilder msgProviderCacheKeyBuilder;
    @Resource
    private SmsMapper smsMapper;
    @Resource
    private ApplicationProperties applicationProperties;
    @Value("${spring.cloud.nacos.config.namespace}")
    private String namespace;

    @Override
    public MsgSendResultEnum sendLoginCode(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return MsgSendResultEnum.MSG_PARAM_ERROR;
        }
        //生成验证码，4位，6位（取它），有效期（30s，60s），同一个手机号不能重发，redis去存储验证码
        String codeCacheKey = msgProviderCacheKeyBuilder.buildSmsLoginCodeKey(phone);
        if (redisTemplate.hasKey(codeCacheKey)) {
            logger.warn("该手机号短信发送过于频繁，phone is {}", phone);
            return MsgSendResultEnum.SEND_FAIL;
        }
        int code = RandomUtils.nextInt(1000, 9999);
        redisTemplate.opsForValue().set(codeCacheKey, code, 60, TimeUnit.SECONDS);
        //发送验证码
        ThreadPoolManager.commonAsyncPool.execute(() -> {
            boolean sendStatus = sendSmsToCCP(phone, code);
            if (sendStatus) {
                insertOne(phone, code);
            }
        });
        //插入验证码发送记录
        return MsgSendResultEnum.SEND_SUCCESS;
    }

    @Override
    public MsgCheckDTO checkLoginCode(String phone, Integer code) {
        //参数校验
        if (StringUtils.isEmpty(phone) || code == null || code < 1000) {
            return new MsgCheckDTO(false, "参数异常");
        }
        //redis校验验证码
        String codeCacheKey = msgProviderCacheKeyBuilder.buildSmsLoginCodeKey(phone);
        Integer cacheCode = (Integer) redisTemplate.opsForValue().get(codeCacheKey);
        if (cacheCode == null || cacheCode < 1000) {
            return new MsgCheckDTO(false, "验证码已过期");
        }
        if (cacheCode.equals(code)) {
            redisTemplate.delete(codeCacheKey);
            return new MsgCheckDTO(true, "验证码校验成功");
        }
        return new MsgCheckDTO(false, "验证码校验失败");
    }

    @Override
    public void insertOne(String phone, Integer code) {
        SmsPO smsPO = new SmsPO();
        smsPO.setPhone(DESUtils.encrypt(phone));
        smsPO.setCode(code);
        smsMapper.insert(smsPO);
    }

    /**
     * 给容联云平台发送短信操作
     *
     * @param phone
     * @param code
     */
    private boolean sendSmsToCCP(String phone, Integer code) {
        logger.info("phone is {},code is {}", phone, code);
        //测试环境就不发送短信了，节省话费
        if(namespace.contains("test")){
            return true;
        }
        try {
            //生产环境请求地址：app.cloopen.com
            String serverIp = applicationProperties.getSmsServerIp();
            //请求端口
            String serverPort = String.valueOf(applicationProperties.getPort());
            //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
            String accountSId = applicationProperties.getAccountSId();
            String accountToken = applicationProperties.getAccountToken();
            //请使用管理控制台中已创建应用的APPID
            String appId = applicationProperties.getAppId();
            CCPRestSmsSDK sdk = new CCPRestSmsSDK();
            sdk.init(serverIp, serverPort);
            sdk.setAccount(accountSId, accountToken);
            sdk.setAppId(appId);
            sdk.setBodyType(BodyType.Type_JSON);
            //测试账号，所有短信都会往这里发送
            String to = applicationProperties.getTestPhone();
            String templateId = SmsTemplateIDEnum.SMS_LOGIN_CODE_TEMPLATE.getTemplateId();
            //测试开发支持的文案如下：【云通讯】您的验证码是{1}，请于{2}分钟内正确输入。其中{1}和{2}为短信模板参数。
            String[] datas = {String.valueOf(code), "1"};
            //可选 扩展码，四位数字 0~9999
            String subAppend = "1234";
            String reqId = UUID.randomUUID().toString();
            //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复
            HashMap<String, Object> result = sdk.sendTemplateSMS(to, templateId, datas, subAppend, reqId);
            if ("000000".equals(result.get("statusCode"))) {
                //正常返回输出data包体信息（map）
                HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
                Set<String> keySet = data.keySet();
                for (String key : keySet) {
                    Object object = data.get(key);
                    logger.info("key is {},object is {}", key, object);
                }
            } else {
                //异常返回输出错误码和错误信息
                logger.error("错误码:{},错误信息:{}", result.get("statusCode"), result.get("statusMsg"));
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("[sendSmsToCCP] error is ", e);
            throw new RuntimeException(e);
        } finally {
            return false;
        }
    }

}