package com.wolf.concurrenttest.jcip.performancescalability;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Description: 长时间持有锁问题，以及改进
 * Created on 2021/7/8 1:45 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class HoldLockLong {
    // holding a lock longer than necessary
    @ThreadSafe
    class AttributeStore {
        @GuardedBy("this")
        private final Map<String, String> attributes = new HashMap<>();

        public synchronized boolean userLocationMatches(String name, String regexp) {
            String key = "users." + name + ".location";
            String location = attributes.get(key);
            if (location == null) {
                return false;
            } else {
                return Pattern.matches(regexp, location);
            }
        }
    }

    // removes an impediment to scalability because the amount of serialized code is reduced.
    // reducing lock duration
    @ThreadSafe
    class BetterAttributeStore {
        @GuardedBy("this")
        private final Map<String, String> attributes = new HashMap<>();

        // constructing the key string and processing the regular expression do not access shared state,
        // they need not be executed with the lock held
        public boolean userLocationMatches(String name, String regexp) {
            String key = "users." + name + ".location";
            String location;
            synchronized (this) {
                location = attributes.get(key);
            }
            if (location == null) {
                return false;
            } else {
                return Pattern.matches(regexp, location);
            }
        }
    }

}
