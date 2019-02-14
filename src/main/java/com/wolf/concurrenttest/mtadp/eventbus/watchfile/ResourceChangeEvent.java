package com.wolf.concurrenttest.mtadp.eventbus.watchfile;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/12
 */
public class ResourceChangeEvent {

    private final Path path;

    private final WatchEvent.Kind<?> kind;

    public ResourceChangeEvent(Path path, WatchEvent.Kind<?> kind) {
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
