package com.wolf.concurrenttest.underjvm.classload;

import java.io.IOException;
import java.io.InputStream;

/**
 * Description: 类加载器与instanceof演示
 * Created on 2021/7/26 8:55 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ClassLoaderTest {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        ClassLoader myLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                try {
                    String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
                    InputStream is = getClass().getResourceAsStream(fileName);
                    if (is == null) {
                        return super.loadClass(name);
                    }
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return defineClass(name, b, 0, b.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException(name);
                }
            }
        };
        Object obj = myLoader.loadClass("com.wolf.concurrenttest.underjvm.classload.ClassLoaderTest").newInstance();

        System.out.println(obj.getClass());
        // 两个类加载器不同，前者由自定义类加载器，后者由虚拟机的应用程序类加载器加载
        System.out.println(obj instanceof com.wolf.concurrenttest.underjvm.classload.ClassLoaderTest);
    }
}
