package com.wolf.concurrenttest.thread;


import com.wolf.concurrenttest.common.TakeTimeUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Description: jvm即将退出时(没有非守护线程)，触发hook，进行资源释放操作，如关闭文件句柄、socket连接、数据库connection等，不要
 * 执行时间太长。
 * <p>
 * kill -9 pid 不会触发
 * kill pid/kill -1 pid 会触发
 * 使用intellij的stop按钮会触发
 * jvm中没有用户线程，自动触发
 * <br/> Created on 2017/6/26 9:43
 *
 * @author 李超
 * @since 1.0.0
 */
public class HookTest {

    public static void main(String[] args) throws IOException, InterruptedException {
//        testHook();
        testPreventDuplicated();
    }

    private static void testHook() {

        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new Thread(
                () -> {
                    System.out.printf("maxMemory : %.2fM\n", runtime.maxMemory() * 1.0 / 1024 / 1024);
                    System.out.printf("totalMemory : %.2fM\n", runtime.totalMemory() * 1.0 / 1024 / 1024);
                    System.out.printf("freeMemory : %.2fM\n", runtime.freeMemory() * 1.0 / 1024 / 1024);
                }));

        //注册多个
        runtime.addShutdownHook(new Thread(
                () -> {
                    System.out.println(1111);
                }));

        TakeTimeUtils.simulateLongTimeOperation(50000000);
    }

    //进程重复创建问题实践
    public static void testPreventDuplicated() throws IOException, InterruptedException {

        String lockPath = "/Users/lichao30/tmp/locks";
        String lockFile = ".lock";
        String permissions = "rw-------";
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString(permissions);

        Path dir = Paths.get(lockPath);
        Files.createDirectories(dir, PosixFilePermissions.asFileAttribute(perms));

        Runtime.getRuntime().addShutdownHook(new Thread(
                () -> {
                    System.out.println("the program will exit.");
                    Paths.get(lockPath, lockFile).toFile().delete();
                }
        ));

        Path path = Paths.get(lockPath, lockFile);
        if (path.toFile().exists()) {
            throw new RuntimeException("the program already running.");
        }

        Files.createFile(path, PosixFilePermissions.asFileAttribute(perms));

        while (true) {
            TimeUnit.MILLISECONDS.sleep(100);
            System.out.println("program is running.");
        }
    }
}
