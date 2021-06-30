package com.wolf.concurrenttest.jcip;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 安全发布底层的不可变状态SafePoint
 * Created on 2021/6/28 6:48 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@ThreadSafe
public class PublishingVehicleTracker {
    private final Map<String, SafePoint> locations;
    private final Map<String, SafePoint> unmodifiableMap;

    public PublishingVehicleTracker(Map<String, SafePoint> locations) {
        this.locations = new ConcurrentHashMap<>(locations);
        this.unmodifiableMap = Collections.unmodifiableMap(this.locations);
    }

    // client不能添加和删除，操作map
    public Map<String, SafePoint> getLocations() {
        return unmodifiableMap;
    }

    public SafePoint getLocation(String id) {
        return locations.get(id);
    }

    public void setLocation(String id, int x, int y) {
        if (!locations.containsKey(id)) {
            throw new IllegalArgumentException("invalid vehicle name: " + id);
        }
        locations.get(id).set(x, y);
    }

    @ThreadSafe
    class SafePoint {
        @GuardedBy("this")
        private int x, y;

        // the private constructor exists to avoid the race condition that would occur if the copy constructor were implemented as this(p.x,p.y)
        private SafePoint(int[] a) {
            this(a[0], a[1]);
        }

        public SafePoint(SafePoint p) {
            this(p.get());
        }

        public SafePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // 一次全返回，避免单次获取x和y的不一致可能
        public synchronized int[] get() {
            return new int[]{x, y};
        }

        public synchronized void set(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
