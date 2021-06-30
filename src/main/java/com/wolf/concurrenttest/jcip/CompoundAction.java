package com.wolf.concurrenttest.jcip;

import java.util.Vector;

/**
 * Description: 复合动作，需要视情况进行上锁保护
 * Created on 2021/6/29 9:14 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class CompoundAction {
    class UsageError {
        // compound actions on a vector that may produce confusing results
        // between the call to size and subsequent call to get in getLast, the vector shrank and the index computed in teh first step is no longer valid
        public Object getLast(Vector list) {
            int lastIndex = list.size() - 1;
            return list.get(lastIndex);
        }

        public void deleteLast(Vector list) {
            int lastIndex = list.size() - 1;
            list.remove(lastIndex);
        }

        // may throw ConcurrentModificationException
        public void iterator(Vector list) {
            for (int i = 0; i < list.size(); i++) {
                System.out.println(list.get(i));
            }
        }

    }

    // using client-side locking
    class UsageCorrect {
        public Object getLast(Vector list) {
            synchronized (list) {
                int lastIndex = list.size() - 1;
                return list.get(lastIndex);
            }
        }

        public void deleteLast(Vector list) {
            synchronized (list) {
                int lastIndex = list.size() - 1;
                list.remove(lastIndex);
            }
        }

        // 虽然解决并发问题，but also prevent other threads from accessing it at all during this time
        // 也可以进行克隆并遍历，不过有性能开销，依赖很多元素：集合大小，对每个元素的操作，频繁度，吞吐量等
        // 注意底层隐藏的遍历细节，若是，可能会有并发问题
        public void iterator(Vector list) {
            synchronized (list) {
                for (int i = 0; i < list.size(); i++) {
                    System.out.println(list.get(i));
                }
            }
        }
    }
}
