package hokumei.sys.matchfriends.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@SpringBootTest
public class DynamicProxyTest {

    interface AService{
        void showName();
    }
    /**
     * 内部类
     */
    static class AServiceImpl implements AService{
        @Override
        public void showName(){
            System.out.println("Hello,World");
        }
    }

   @SneakyThrows
   @Test
   public void test() {
        AServiceImpl service = new AServiceImpl();
        ProxySample1 proxySample = new ProxySample1(service);
        Method method = service.getClass().getDeclaredMethod("showName");
        proxySample.invoke(service,method,null);
   }

    @SneakyThrows
    @Test
    public void test2() {
        // 设置变量可以保存动态代理类，默认名称以 $Proxy0 格式命名
        // System.getProperties().setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        // 1. 创建被代理的对象，AService接口的实现类
        AServiceImpl AServiceImpl = new AServiceImpl();
        // 2. 获取对应的 ClassLoader
        ClassLoader classLoader = AServiceImpl.getClass().getClassLoader();
        // 3. 获取所有接口的Class，这里的AServiceImpl只实现了一个接口AService，
        Class[] interfaces = AServiceImpl.getClass().getInterfaces();
        // 4. 创建一个将传给代理类的调用请求处理器，处理所有的代理对象上的方法调用
        //     这里创建的是一个自定义的日志处理器，须传入实际的执行对象 AServiceImpl
        InvocationHandler logHandler = new ProxySample1(AServiceImpl);
        /*
		   5.根据上面提供的信息，创建代理对象 在这个过程中，
               a.JDK会通过根据传入的参数信息动态地在内存中创建和.class 文件等同的字节码
               b.然后根据相应的字节码转换成对应的class，
               c.然后调用newInstance()创建代理实例
		 */
        AService proxy = (AService) Proxy.newProxyInstance(classLoader, interfaces, logHandler);
        // 调用代理的方法
        proxy.showName();
    }
}


