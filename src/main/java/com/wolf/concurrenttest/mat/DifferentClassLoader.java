package com.wolf.concurrenttest.mat;

import java.net.URL;

/**
 * Description:
 * Created on 2021/9/14 6:59 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class DifferentClassLoader {
    public static void main(String[] args) throws Exception {
        MO mo = new MO();
        URL url = MO.class.getProtectionDomain().getCodeSource().getLocation();
        String className = MO.class.getName();

        ClassLoader newLoader = new MoClassLoader("NewMoLoader1", new URL[]{url});
        Class<?> newMoClass = newLoader.loadClass(className);
        Object newMO = newMoClass.newInstance();

        ClassLoader newLoader2 = new MoClassLoader("NewMoLoader2", new URL[]{url});
        Class<?> newMoClass2 = newLoader2.loadClass(className);
        Object newMO2 = newMoClass2.newInstance();

        System.out.println("MO class: " + mo.getClass().getName() + ", loader: " + mo.getClass().getClassLoader());
        System.out.println("newMO class: " + newMO.getClass().getName() + ", loader: " + newMO.getClass().getClassLoader());
        System.out.println("new MO2 class: " + newMO2.getClass().getName() + ", loader: " + newMO2.getClass().getClassLoader());

        Thread.sleep(1000000000);
    }

    public static class MO {
    }
}
