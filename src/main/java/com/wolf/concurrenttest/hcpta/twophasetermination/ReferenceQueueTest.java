package com.wolf.concurrenttest.hcpta.twophasetermination;

import com.wolf.concurrenttest.mtadp.common.Utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * Description: gc时处理的引用队列
 * -XX:+PrintGCDetails
 * Created on 2021/9/26 9:31 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ReferenceQueueTest {
    public static void main(String[] args) throws InterruptedException {
        // 被回收掉的Reference，会被加入与之关联的queue中
        ReferenceQueue<Reference> queue = new ReferenceQueue<Reference>();
        Reference ref = new Reference();
        WeakReference<Reference> reference = new WeakReference<Reference>(ref, queue);
        ref = null;
        System.out.println("reference.get()==>" + reference.get());

        System.gc();
        Utils.slowly(1);  // make sure gc thread triggered
        // 阻塞方法
        java.lang.ref.Reference<? extends Reference> gcedRef = queue.remove();
        // 被垃圾回收后，会从队列中获取到WeakReference
        System.out.println("gcedRef==>" + gcedRef);
    }
}
