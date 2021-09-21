package com.wolf.concurrenttest.hcpta.classloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Description: 破坏双亲委派机制
 * + 系统原生的class，交给其委托代理。
 * + 其他类class，有当前加载器加载，没有则尝试用父类那套
 * Created on 2021/9/21 2:58 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class BrokerDelegateClassLoader extends ClassLoader {
    // 默认的class根目录
    public final static Path DEFAULT_CLASS_DIR = Paths.get("/Users/chaoli/intellijWrkSpace/concurrenttest/src/main/java");
    // 程序使用的class根目录
    private final Path classDir;

    public BrokerDelegateClassLoader() {
        super();
        this.classDir = DEFAULT_CLASS_DIR;
    }

    public BrokerDelegateClassLoader(String classDir) {
        super();
        this.classDir = Paths.get(classDir);
    }

    public BrokerDelegateClassLoader(String classDir, ClassLoader parent) {
        super(parent);
        this.classDir = Paths.get(classDir);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classBytes = this.readClassBytes(name);
        if (null == classBytes || classBytes.length == 0) {
            throw new ClassNotFoundException("Can not load the class " + name);
        }
        // 依据byte[]定义class
        // 类名、class二进制字节数组、字节数组偏移量、从偏移量开始读取多长的byte数据
        return this.defineClass(name, classBytes, 0, classBytes.length);
    }

    // 读取class文件的二进制数据内容
    private byte[] readClassBytes(String name) throws ClassNotFoundException {
        // 将包分隔符转换为文件路径分隔符
        String classPath = name.replace(".", "/");
        Path classFullPath = classDir.resolve(Paths.get(classPath + ".class"));
        if (!classFullPath.toFile().exists()) {
            throw new ClassNotFoundException("The class " + name + " not found");
        }
        // 将文件内容读入内存
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Files.copy(classFullPath, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new ClassNotFoundException("load the class " + name + " occur error", e);
        }
    }

    @Override
    public String toString() {
        return "BrokerDelegateClassLoader";
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // 1 依据类的全路径名称进行加锁，确保每个类在多线程下只被加载一次
        synchronized (getClassLoadingLock(name)) {
            // 2 看当前类加载器的缓存
            Class<?> klass = findLoadedClass(name);
            // 首次加载
            if (klass == null) {
                // 4 交给系统类加载器
                if (name.startsWith("java.") || name.startsWith("javax")) {
                    try {
                        klass = getSystemClassLoader().loadClass(name);
                    } catch (Exception e) {
                        // ignore
                    }
                } else {
                    // 5 用当前自定义的类加载器进行加载
                    try {
                        klass = this.findClass(name);
                    } catch (ClassNotFoundException e) {
                        // ignore
                    }
                    // 6 当前自定义类加载器没有找到
                    if (klass == null) {
                        // 交给父加载器
                        if (getParent() != null) {
                            klass = getParent().loadClass(name);
                        } else {  // 交给系统类加载器
                            klass = getSystemClassLoader().loadClass(name);
                        }
                    }
                }
            }
            // 7 以上都加载不到则异常
            if (null == klass) {
                throw new ClassNotFoundException("The class " + name + " not found");
            }
            if (resolve) {
                resolveClass(klass);
            }
            return klass;
        }
    }
}
