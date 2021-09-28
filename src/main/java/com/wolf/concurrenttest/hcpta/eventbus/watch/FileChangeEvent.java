package com.wolf.concurrenttest.hcpta.eventbus.watch;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Description: 对WatchEvent.Kind和Path的包装
 * Created on 2021/9/27 11:17 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FileChangeEvent {
    private final Path path;
    private final WatchEvent.Kind<?> kind;

    public FileChangeEvent(Path path, WatchEvent.Kind<?> kind) {
        this.path = path;
        this.kind = kind;
    }

    public Path getPath() {
        return path;
    }

    public WatchEvent.Kind<?> getKind() {
        return kind;
    }
}
