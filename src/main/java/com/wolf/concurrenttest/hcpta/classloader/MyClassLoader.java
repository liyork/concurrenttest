package com.wolf.concurrenttest.hcpta.classloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Description: 从classDir下加载类
 * Created on 2021/9/21 9:20 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class MyClassLoader extends ClassLoader {  // 自定义类加载器必须是ClassLoader的直接或间接子类
    // 默认的class根目录
    public final static Path DEFAULT_CLASS_DIR = Paths.get("/Users/chaoli/intellijWrkSpace/concurrenttest/src/main/java");
    // 程序使用的class根目录
    private final Path classDir;

    public MyClassLoader() {
        super();
        this.classDir = DEFAULT_CLASS_DIR;
    }

    public MyClassLoader(String classDir) {
        super();
        this.classDir = Paths.get(classDir);
    }

    public MyClassLoader(String classDir, ClassLoader parent) {
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
        return "My ClassLoader";
    }
}
