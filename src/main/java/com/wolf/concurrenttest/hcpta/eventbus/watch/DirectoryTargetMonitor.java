package com.wolf.concurrenttest.hcpta.eventbus.watch;

import com.wolf.concurrenttest.hcpta.eventbus.EventBus;

import java.nio.file.*;

/**
 * Description:
 * Created on 2021/9/27 11:11 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class DirectoryTargetMonitor {
    private WatchService watchService;
    private final EventBus eventBus;
    private final Path path;
    private volatile boolean start = false;

    public DirectoryTargetMonitor(final EventBus eventBus, final String path) {
        this(eventBus, path, "");
    }

    public DirectoryTargetMonitor(final EventBus eventBus, final String path, String... morePaths) {
        this.eventBus = eventBus;
        this.path = Paths.get(path, morePaths);
    }

    public void startMonitor() throws Exception {
        this.watchService = FileSystems.getDefault().newWatchService();
        // 为路径注册感兴趣事件
        this.path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_CREATE);
        System.out.printf("The directory [%s] is monitoring... \n", path);
        this.start = true;
        while (start) {
            WatchKey watchKey = null;
            try {
                // 有事件发生时，会返回对应的key
                watchKey = watchService.take();
                watchKey.pollEvents().forEach(event -> {
                    WatchEvent.Kind<?> kind = event.kind();
                    // 返回的是cc.txt
                    Path path = (Path) event.context();
                    // 解析成全路径
                    Path child = DirectoryTargetMonitor.this.path.resolve(path);
                    eventBus.post(new FileChangeEvent(child, kind));
                });
            } catch (Exception e) {
                this.start = false;
            } finally {
                if (watchKey != null) {
                    watchKey.reset();
                }
            }
        }
    }

    public void stopMonitor() throws Exception {
        System.out.printf("The directory [%s] monitor will be stop... \n", path);
        Thread.currentThread().interrupt();
        this.start = false;
        this.watchService.close();
        System.out.printf("The directory [%s] monitor will be stop done. \n", path);
    }

    public static void main(String[] args) {
        Path path = Paths.get("/Users/chaoli/test");
        Path child = Paths.get("a.txt");
        Path resolve = path.resolve(child);
        System.out.println(resolve);
    }
}
