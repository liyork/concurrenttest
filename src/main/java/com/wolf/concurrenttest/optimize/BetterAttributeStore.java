package com.wolf.concurrenttest.optimize;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * BetterAttributeStore
 * <p/>
 * Reducing lock duration
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class BetterAttributeStore {
    @GuardedBy("this")
    private final Map<String, String> attributes = new HashMap<String, String>();//由于只有一个状态共享，这里可以使用concurrenthashmap

    public synchronized boolean bedUserLocationMatches(String name, String regexp) {
        String key = "users." + name + ".location";
        String location = attributes.get(key);
        if(location == null) return false;
        else return Pattern.matches(regexp, location);
    }

    //降低锁代码块，即持有时间。只有访问或设定共享对象时才用锁
    public boolean goodUserLocationMatches(String name, String regexp) {
        String key = "users." + name + ".location";
        String location;
        synchronized(this) {
            location = attributes.get(key);
        }
        if(location == null) return false;
        else return Pattern.matches(regexp, location);
    }
}
