package dubbo.provider;

import org.apache.dubbo.config.*;
import org.qiyu.live.user.interfaces.IUserRpc;
import org.qiyu.live.user.provider.rpc.UserRpcImpl;


public class DubboProvider {

    private static final String REGISTER_ADDRESS = "nacos://127.0.0.1:8848?namespace=qiyu-live-test&&username=qiyu&&password=qiyu";
    private static RegistryConfig registryConfig;
    private static ApplicationConfig applicationConfig;

    public static void initConfig() {
        registryConfig = new RegistryConfig();
        applicationConfig = new ApplicationConfig();
        registryConfig.setAddress(REGISTER_ADDRESS);
        applicationConfig.setName("dubbo-test-application");
        applicationConfig.setRegistry(registryConfig);
    }

    public void initProvider() {
        ProtocolConfig dubboProtocolConfig = new ProtocolConfig();
        dubboProtocolConfig.setPort(9090);
        dubboProtocolConfig.setName("dubbo");
        ServiceConfig<IUserRpc> serviceConfig = new ServiceConfig<>();
        serviceConfig.setInterface(IUserRpc.class);
        serviceConfig.setProtocol(dubboProtocolConfig);
        serviceConfig.setApplication(applicationConfig);
        serviceConfig.setRegistry(registryConfig);
        serviceConfig.setRef(new UserRpcImpl());
        //核心
        serviceConfig.export();
        System.out.println("服务暴露");
    }

    public static void main(String[] args) {
        initConfig();
        DubboProvider dubboTest = new DubboProvider();
        dubboTest.initProvider();
        for (; ; ) {
        }
    }
}






