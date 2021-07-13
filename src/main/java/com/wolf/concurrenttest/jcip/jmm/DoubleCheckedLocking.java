package com.wolf.concurrenttest.jcip.jmm;

import net.jcip.annotations.NotThreadSafe;

/**
 * Description: double-checked-locking anti-pattern
 * 由于早期的synchronized很慢，才有此思路，不过现在SafeLazyInitialization提供同样的好的方式
 * but at the time, synchronization was slow and, more importantly, not completely understood:
 * the exclusion aspects were well enough understood, but the visiblity aspects were not.
 * 思路是将常用路径不加锁，特殊初始化路径加锁，不过未正确同步可能引发看到部分构造对象，后来jmm中用volatile可以解决
 * Created on 2021/7/13 12:40 PM
 *
 * @author 李超
 * @version 0.0.1
 */
@NotThreadSafe
public class DoubleCheckedLocking {
    private static Resource resource;

    public static Resource getResource() {
        if (resource == null) {
            synchronized (DoubleCheckedLocking.class) {
                if (resource == null) {
                    resource = new Resource();
                }
            }
        }
        return resource;
    }
}
