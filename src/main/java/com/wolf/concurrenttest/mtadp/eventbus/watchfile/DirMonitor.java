package com.wolf.concurrenttest.mtadp.eventbus.watchfile;

import com.wolf.concurrenttest.mtadp.eventbus.base.EventBus;

import java.io.IOException;
import java.nio.file.*;

/**
 * Description: 文件监控，收到事件转换成FileChangeEvent注册到eventbus上。
 * 相对于轮询，省去cpu占用。实时性高，但是可能不支持批量，轮询就会有批量效果，也许可以积攒多了再注册。
 *
 * @author 李超
 * @date 2019/02/12
 */
public class DirMonitor {

    private WatchService watchService;

    private final EventBus eventBus;

    private final Path path;

    private volatile boolean isRunning = false;

    public DirMonitor(EventBus eventBus, String path) {
        this.eventBus = eventBus;
        this.path = Paths.get(path);
    }

    public void startMonitor() throws IOException {

        watchService = FileSystems.getDefault().newWatchService();
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
        System.out.printf("the file [%s] is monitoring ... \n", path);
        isRunning = true;

        while (isRunning) {

            WatchKey watchKey = null;
            try {
                watchKey = watchService.take();
                watchKey.pollEvents().forEach(event -> {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path path = (Path) event.context();
                    Path child = DirMonitor.this.path.resolve(path);
                    eventBus.post(new ResourceChangeEvent(child, kind));
                });
            } catch (Exception e) {
                e.printStackTrace();
                this.isRunning = false;
            } finally {
                if (watchKey != null) {
                    watchKey.reset();
                }
            }
        }
    }

    public void stopMonitor() throws IOException {

        System.out.printf("the file [%s] monitor will be stop... \n", path);
        isRunning = false;
        watchService.close();
        eventBus.close();
        System.out.printf("the file [%s] monitor be stop done... \n", path);
    }
}
