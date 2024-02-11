package org.qiyu.live.msg.provider.config;

/**
 * @Author idea
 * @Date: Created in 09:08 2023/6/19
 * @Description
 */
public enum SmsTemplateIDEnum {

    SMS_LOGIN_CODE_TEMPLATE("1","登录验证码短信模版");

    String templateId;
    String desc;

    SmsTemplateIDEnum(String templateId, String desc) {
        this.templateId = templateId;
        this.desc = desc;
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getDesc() {
        return desc;
    }
}
