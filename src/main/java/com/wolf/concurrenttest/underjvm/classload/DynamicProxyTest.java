package com.wolf.concurrenttest.underjvm.classload;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Description: 动态代理示例
 * Created on 2021/7/28 1:12 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class DynamicProxyTest {
    interface IHello {
        void sayHello();
    }

    static class Hello implements IHello {
        @Override
        public void sayHello() {
            System.out.println("hello world");
        }
    }

    static class DynamicProxy implements InvocationHandler {

        Object originalObj;

        // sun.misc.ProxyGenerator::generateProxyClass()生成字节码，在运行时产生一个描述代理类的字节码byte[]数组。
        Object bind(Object originalObj) {
            this.originalObj = originalObj;
            // 返回一个实现了IHello接口，且代理了new Helle()实例行为的对象
            return Proxy.newProxyInstance(originalObj.getClass().getClassLoader(), originalObj.getClass().getInterfaces(), this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("welcome");
            return method.invoke(originalObj, args);
        }
    }

    public static void main(String[] args) {
        // 生成运行时产生的代理类，$Proxy0.class
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        IHello hello = (IHello) new DynamicProxy().bind(new Hello());
        hello.sayHello();
    }

}
