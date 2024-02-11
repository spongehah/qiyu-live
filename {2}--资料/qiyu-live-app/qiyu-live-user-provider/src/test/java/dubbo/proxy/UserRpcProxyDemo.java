package dubbo.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Author idea
 * @Date: Created in 15:24 2023/4/19
 * @Description
 */
public class UserRpcProxyDemo implements UserRpcService {

    //实例化时，注入的InvokerInvocationHandler
    private InvocationHandler handler;
    private Method[] methods = new Method[2];

    //注入InvocationHandler
    public UserRpcProxyDemo(InvocationHandler invocationHandler) throws NoSuchMethodException {
        this.handler = invocationHandler;
        Method getUserInfoMethod = UserRpcService.class.getMethod("getUserInfo", String.class);
        methods[0] = getUserInfoMethod;
    }

    @Override
    public String getUserInfo(String name) {
        try {
            return (String) this.handler.invoke(this,methods[0], new String[]{name});
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws NoSuchMethodException {
        UserRpcProxyDemo demoService = new UserRpcProxyDemo(new InvokerInvocationHandler());
        String result = demoService.getUserInfo("idea");
        System.out.println(result);
    }

}
