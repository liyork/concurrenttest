package com.wolf.concurrenttest.jvm.loadclass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * 对于一个class，如果有变化而classloader内部有缓存则会不去重新加载class到方法区，所以得使用另一个classloader去加载变化的类返回。
 * <p>
 * 如果仅仅加载某些路径下的class文件则重写findclass就行，但是本例想要动态更新指定类，所以需要每次都构造新的classloader，原来的结构不能实现。
 * <p>
 * 如果每次都使用新的classloader那么会不会内存紧张？该classloader实例会在所有这个实例装载的的类全部被回收后才被回收
 * <p>
 * 对于热加载的话，只能重新创建一个ClassLoader，然后再去加载已经被加载过的class文件，否则用原来classloader则会提示
 * java.lang.LinkageError attempted duplicate class definition
 * <p>
 * -verbose:class
 * <p>
 * <p>
 * 释放时要彻底释放类中所有东西，线程，被引用地方
 * <br/> Created on 2018/1/22 9:44
 *
 * @author 李超
 * @since 1.0.0
 */
public class HotSwapURLClassLoader extends URLClassLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(HotSwapURLClassLoader.class);

    //缓存加载class文件的最后最新修改时间
    public static Map<String, Long> cacheLastModifyTimeMap = new HashMap<String, Long>();

    boolean isNeedMonitor;
    //工程class类所在的路径
    public static String projectClassPath;

    public HotSwapURLClassLoader(String classPath) {
        super(getMyURLs(classPath));
        this.isNeedMonitor = true;
    }

    public HotSwapURLClassLoader(String classPath, String jarPath) {
        super(getMyURLs(classPath, jarPath));
        this.isNeedMonitor = false;
    }

    //指定classloader要加载的根路径
    private static URL[] getMyURLs(String... paths) {
        List<URL> urls = new ArrayList<>();
        try {
            projectClassPath = paths[0];
            for (String path : paths) {
                urls.add(new File(path).toURI().toURL());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        URL[] result = urls.toArray(new URL[paths.length]);
        return result;
    }

    /**
     * 重写loadClass，不采用双亲委托机制
     * "java."开头的包中类还是会由系统默认ClassLoader加载
     */
    @Override
    public Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        Class clazz;
        //查看自己缓存
        clazz = findLoadedClass(className);
        if (clazz != null) {
            //如果class类被修改过，则重新加载
            if (isModify(className)) {
                LOGGER.debug("new classloader is created..");
                HotSwapURLClassLoader hcl = new HotSwapURLClassLoader("D:\\intellijWrkSpace\\violin\\violin-test\\target\\classes");
                Class clazz2 = customLoadClass(className, hcl);
                //类加载器、类都不一样，那么class结构在方法区是否被覆盖还是两份?
                LOGGER.debug("clazz is reload,newclass == oldclass, {}", clazz == clazz2);
                clazz = clazz2;
            }

            if (resolve) {
                resolveClass(clazz);
            }

            return (clazz);
        }

        //如果类的包名为"java."开始，则有系统默认加载器AppClassLoader加载
        if (className.startsWith("java.")) {
            try {
                //得到系统默认的加载cl，即AppClassLoader
                ClassLoader system = ClassLoader.getSystemClassLoader();
                clazz = system.loadClass(className);
                if (clazz != null) {
                    if (resolve) {
                        resolveClass(clazz);
                    }
                    return (clazz);
                }
            } catch (ClassNotFoundException e) {
                // Ignore
            }
        }

        return customLoadClass(className, this);
    }

    /**
     * 自定义加载
     *
     * @param className
     * @param cl
     * @return
     * @throws ClassNotFoundException
     */
    public Class customLoadClass(String className, ClassLoader cl) throws ClassNotFoundException {
        return customLoadClass(className, false, cl);
    }

    /**
     * 自定义加载
     *
     * @param className
     * @param resolve
     * @return
     * @throws ClassNotFoundException
     */
    public Class customLoadClass(String className, boolean resolve, ClassLoader cl) throws ClassNotFoundException {
        Class clazz = ((HotSwapURLClassLoader) cl).findClass(className);
        if (resolve) {
            ((HotSwapURLClassLoader) cl).resolveClass(clazz);
        }

        if (isNeedMonitor) {
            //缓存加载class文件的最后修改时间
            long lastModifyTime = getClassLastModifyTime(className);
            cacheLastModifyTimeMap.put(className, lastModifyTime);
        }

        return clazz;
    }

    /**
     * @param name
     * @return .class文件最新的修改时间
     */
    private long getClassLastModifyTime(String name) {
        String path = getClassCompletePath(name);
        File file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException(new FileNotFoundException(name));
        }
        return file.lastModified();
    }

    /**
     * 判断这个文件跟上次比是否修改过
     *
     * @param name
     * @return
     */
    private boolean isModify(String name) {
        long lastmodify = getClassLastModifyTime(name);
        long previousModifyTime = cacheLastModifyTimeMap.get(name);
        if (lastmodify > previousModifyTime) {
            return true;
        }
        return false;
    }

    /**
     * @param name
     * @return .class文件的完整路径 (e.g. E:/A.class)
     */
    private String getClassCompletePath(String name) {
        String simpleName = name.replace(".", "/");
        return projectClassPath + "/" + simpleName + ".class";
    }
}