package com.wolf.concurrenttest.hcpta.twophasetermination;

import java.lang.ref.WeakReference;

/**
 * Description:
 * Created on 2021/9/26 9:28 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class WeakReferenceTest {
    public static void main(String[] args) {
        Reference ref = new Reference();
        WeakReference<Reference> reference = new WeakReference<Reference>(ref);
        // 设定null，则只有弱引用指向堆中创建的Reference实例
        ref = null;
        System.gc();
    }
}
