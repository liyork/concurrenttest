package com.wolf.concurrenttest.jcip;

import net.jcip.annotations.ThreadSafe;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Description: 将线程安全委托给线程安全类
 * Created on 2021/6/28 1:45 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@ThreadSafe
public class DelegatingVehicleTracker {
    private final ConcurrentMap<String, Point> locations;// 用于并发更新，保证线程安全
    private final Map<String, Point> unmodifiableMap;// 因为locations是可变的，所以多出一个属性返回给client，表明此map不可变。而本类内部仍然是可以动的，因为localtions变动则再get也会有影响

    public DelegatingVehicleTracker(Map<String, Point> points) {
        locations = new ConcurrentHashMap<>(points);
        unmodifiableMap = Collections.unmodifiableMap(locations);
    }

    public Map<String, Point> getLocations() {
        return unmodifiableMap;
    }

    // returning a Static Copy of the Location Set Instead of a "live" One
    //public Map<String, Point> getLocations() {
    //    return Collections.unmodifiableMap(new HashMap<>(locations));
    //}

    public Point getLocation(String id) {
        return unmodifiableMap.get(id);
    }

    public void setLocation(String id, int x, int y) {
        if (locations.replace(id, new Point(x, y)) == null) {
            throw new IllegalArgumentException("invalid vehicle name: " + id);
        }
    }

    class Point {// 不可变，线程安全
        public final int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
