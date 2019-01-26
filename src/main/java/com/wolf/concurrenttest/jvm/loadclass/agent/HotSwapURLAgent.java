package com.wolf.concurrenttest.jvm.loadclass.agent;

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

/**
 * Description:
 * 模拟场景：
 * 系统启动后使用默认的app的classloader，然后如果想动态添加某些监控到指定类则通过动态配置修改needMonitorClass，然后使用自定义的
 * classloader去加载这个类，然后自己缓存起来。
 *
 * 可能真正要实现真正让引用无感知的监控，应该在应用加载类时如果遇到想要监控的地方，就要去重新loadclass，agentmain可以在defineclass之前
 * 操作，但是这个definclass是由用户应用决定的，也可能是默认的加载类方式，那么如何让应用classloader感知到需要重新加载这个类呢？
 * 把classloader重写了？启动就用咱们自己的classloader？
 * <br/> Created on 2018/1/22 9:44
 *
 * @author 李超
 * @since 1.0.0
 */
public class HotSwapURLAgent extends URLClassLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(HotSwapURLAgent.class);

    //动态通过配置中心更新，要监控的类
    private static HashMap<String, Integer> needMonitorClass = new HashMap<>();

    static {
        //模拟已经准备有个要监控
        needMonitorClass.put("com.wolf.test.agent.targetobj.TimeTest", 0);
    }


    boolean isNeedMonitor;
    //工程class类所在的路径
    public static String projectClassPath;

    public HotSwapURLAgent(String classPath) {
        super(getMyURLs(classPath));
        this.isNeedMonitor = true;
    }

    public HotSwapURLAgent(String classPath, String jarPath) {
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
        //使用单纯对的代理给父类，不能实现热替换
//       return super.loadClass(className, resolve);

        Class clazz = null;
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

        Class aClass = null;
        //对于某些指定类直接findclass不从缓存查
        //clazz = findLoadedClass(className);

        //第一次自定义加载，以后直接走缓存
        Integer isLoaded = needMonitorClass.get(className);
        if (isLoaded != null && isLoaded != 1) {
            aClass = customLoadClass(className, this);
            needMonitorClass.put(className, 1);
        } else {
            findLoadedClass(className);
        }

        return aClass;
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
        Class clazz = ((HotSwapURLAgent) cl).findClass(className);
        System.out.println("find class..className:" + className + ",clazz:" + clazz);
        if (resolve) {
            ((HotSwapURLAgent) cl).resolveClass(clazz);
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
     * @param name
     * @return .class文件的完整路径 (e.g. E:/A.class)
     */
    private String getClassCompletePath(String name) {
        String simpleName = name.replace(".", "/");
        return projectClassPath + "/" + simpleName + ".class";
    }
}