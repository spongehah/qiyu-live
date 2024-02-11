package org.qiyu.live.api.vo.resp;

/**
 * 返回给客户端使用的im地址和认证token
 *
 * @Author idea
 * @Date: Created in 10:49 2023/7/26
 * @Description
 */
public class ImConfigVO {

    private String token;
    private String wsImServerAddress;
    private String tcpImServerAddress;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getWsImServerAddress() {
        return wsImServerAddress;
    }

    public void setWsImServerAddress(String wsImServerAddress) {
        this.wsImServerAddress = wsImServerAddress;
    }

    public String getTcpImServerAddress() {
        return tcpImServerAddress;
    }

    public void setTcpImServerAddress(String tcpImServerAddress) {
        this.tcpImServerAddress = tcpImServerAddress;
    }
}
