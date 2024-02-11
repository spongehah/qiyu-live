package org.qiyu.live.msg.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author idea
 * @Date: Created in 09:05 2023/6/19
 * @Description
 */
@ConfigurationProperties(prefix = "qiyu.sms.ccp")
@Configuration
public class ApplicationProperties {

    private String smsServerIp;
    private Integer port;
    private String accountSId;
    private String accountToken;
    private String appId;
    private String testPhone;

    public String getSmsServerIp() {
        return smsServerIp;
    }

    public void setSmsServerIp(String smsServerIp) {
        this.smsServerIp = smsServerIp;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getAccountSId() {
        return accountSId;
    }

    public void setAccountSId(String accountSId) {
        this.accountSId = accountSId;
    }

    public String getAccountToken() {
        return accountToken;
    }

    public void setAccountToken(String accountToken) {
        this.accountToken = accountToken;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTestPhone() {
        return testPhone;
    }

    public void setTestPhone(String testPhone) {
        this.testPhone = testPhone;
    }

    @Override
    public String toString() {
        return "ApplicationProperties{" +
                "smsServerIp='" + smsServerIp + '\'' +
                ", port=" + port +
                ", accountSId='" + accountSId + '\'' +
                ", accountToken='" + accountToken + '\'' +
                ", testPhone='" + testPhone + '\'' +
                ", appId='" + appId + '\'' +
                '}';
    }

}
