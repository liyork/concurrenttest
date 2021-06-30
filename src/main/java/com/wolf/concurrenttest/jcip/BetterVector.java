package com.wolf.concurrenttest.jcip;

import java.util.Vector;

/**
 * Description: Extending Vector to have a Put-if-absent Method，可能因为源码可能不再手上，只能扩展
 * 不过也很脆弱，若是基类变了使用lock，则子类可能违背了原有语义
 * Created on 2021/6/28 9:43 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class BetterVector<E> extends Vector<E> {
    public synchronized boolean putIfAbsent(E x) {
        boolean absent = !contains(x);
        if (absent) {
            add(x);
        }
        return absent;
    }
}
