package com.wolf.concurrenttest.mat;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Description:
 * Created on 2021/9/14 6:58 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class MoClassLoader extends URLClassLoader {
    private final String loaderName;

    MoClassLoader(String loaderName, URL[] urls) {
        super(urls);
        this.loaderName = loaderName;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = findLoadedClass(name);
        if (clazz == null) {
            try {
                return findClass(name);
            } catch (ClassNotFoundException e) {
                return super.loadClass(name, resolve);
            }
        }
        return clazz;
    }

    @Override
    public String toString() {
        return loaderName;
    }
}
