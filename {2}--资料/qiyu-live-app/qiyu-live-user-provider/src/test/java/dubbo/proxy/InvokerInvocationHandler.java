package dubbo.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Author idea
 * @Date: Created in 15:28 2023/4/19
 * @Description
 */
public class InvokerInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (methodName.equals("getUserInfo")) {
            return args[0] + "-test";
        }
        return "no match";
    }
}
