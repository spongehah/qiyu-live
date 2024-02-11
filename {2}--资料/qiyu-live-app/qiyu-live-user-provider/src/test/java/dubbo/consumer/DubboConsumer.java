package dubbo.consumer;

import org.apache.dubbo.config.*;
import org.qiyu.live.user.interfaces.IUserRpc;


public class DubboConsumer {

    private static final String REGISTER_ADDRESS = "nacos://127.0.0.1:8848?namespace=qiyu-live-test&&username=qiyu&&password=qiyu";
    private static RegistryConfig registryConfig;
    private static ApplicationConfig applicationConfig;
    private IUserRpc userRpc;

    public static void initConfig() {
        registryConfig = new RegistryConfig();
        applicationConfig = new ApplicationConfig();
        registryConfig.setAddress(REGISTER_ADDRESS);
        applicationConfig.setName("dubbo-test-application");
        applicationConfig.setRegistry(registryConfig);
    }

    public void initConsumer() {
        ReferenceConfig<IUserRpc> userRpcReferenceConfig = new ReferenceConfig<>();
        userRpcReferenceConfig.setApplication(applicationConfig);
        userRpcReferenceConfig.setRegistry(registryConfig);
        userRpcReferenceConfig.setLoadbalance("random");
        userRpcReferenceConfig.setInterface(IUserRpc.class);
        userRpc = userRpcReferenceConfig.get();
    }
    

    public static void main(String[] args) throws InterruptedException {
        initConfig();
        DubboConsumer dubboTest = new DubboConsumer();
        dubboTest.initConsumer();
        for (;;){
            Thread.sleep(3000);
        }
    }
}






