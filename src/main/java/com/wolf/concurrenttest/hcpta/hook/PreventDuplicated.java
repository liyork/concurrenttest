package com.wolf.concurrenttest.hcpta.hook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * kill pid
 * kill -1 pid
 * Created on 2021/9/19 11:53 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class PreventDuplicated {
    private final static String LOCK_PATH = "/Users/chaoli/test/locks";
    private final static String LOCK_FILE = ".lock";
    private final static String PERMISSIONS = "rw-------";

    public static void main(String[] args) throws IOException {
        // 注入hook线程，在程序退出时删除lock文件
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("the program received kill SIGNAL");
            getLockFile().toFile().delete();
        }));

        // 检查是否存在.lock文件
        checkRunning();

        for (; ; ) {
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println("program is running");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void checkRunning() throws IOException {
        Path path = getLockFile();
        if (path.toFile().exists()) {
            throw new RuntimeException("the program already running");
        }
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString(PERMISSIONS);
        Files.createFile(path, PosixFilePermissions.asFileAttribute(perms));
    }

    private static Path getLockFile() {
        return Paths.get(LOCK_PATH, LOCK_FILE);
    }
}
