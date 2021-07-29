package com.wolf.concurrenttest.underjvm.memorypartition;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Description: 测试方法区oom
 * javac -cp ./com/wolf/concurrenttest/underjvm/cglib-3.3.0.jar com/wolf/concurrenttest/underjvm/JavaMethodAreaOOM.java
 * java的cp要从这个类所在com的上面目录开始
 * java -cp .:./com/wolf/concurrenttest/underjvm/cglib-3.3.0.jar:./com/wolf/concurrenttest/underjvm/asm-9.1.jar -XX:MaxMetaspaceSize=10m com.wolf.concurrenttest.underjvm.memorypartition.JavaMethodAreaOOM
 * Created on 2021/7/17 1:20 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class JavaMethodAreaOOM {
    public static void main(String[] args) {
        while (true) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(OOMObject.class);
            enhancer.setUseCache(false);
            enhancer.setCallback(new MethodInterceptor() {
                @Override
                public Object intercept(Object obj, Method method, Object[] objects, MethodProxy proxy) throws Throwable {
                    return proxy.invokeSuper(obj, args);
                }
            });
            enhancer.create();
        }
    }

    static class OOMObject {

    }
}
