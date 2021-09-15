package com.wolf.concurrenttest.mat;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Description:
 * -Xmx32M
 * -XX:+HeapDumpOnOutOfMemoryError
 * -XX:HeapDumpPath=/Users/chaoli/Downloads/dump/LeakClassLoader.hprof
 * Created on 2021/9/14 7:03 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class LeakClassLoader {
    public static void main(String[] args) throws Exception {
        List<Object> leaks = new LinkedList<>();

        URL url = MO.class.getProtectionDomain().getCodeSource().getLocation();
        String moClassName = MO.class.getName();
        String leakClassName = Leak.class.getName();

        int count = 0;
        while (true) {
            // 每次构建新的classloader然后加载并实例化两个类
            ClassLoader newLoader = new MoClassLoader("NewMoLoader1", new URL[]{url});
            Class<?> newMoClass = newLoader.loadClass(moClassName);
            newMoClass.newInstance();
            Class<?> newLeakClass = newLoader.loadClass(leakClassName);
            leaks.add(newLeakClass.newInstance());
            System.out.println("Add leak times: " + ++count);
        }
    }


    public static class Leak {
    }

    public static class MO {
        private static final byte[] bs = new byte[1024 * 1024 * 5];
    }
}
