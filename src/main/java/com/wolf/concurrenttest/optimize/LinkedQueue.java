package com.wolf.concurrenttest.optimize;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.atomic.AtomicReference;

/**
 * LinkedQueue
 * <p/>
 * Insertion in the Michael-Scott nonblocking queue algorithm
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class LinkedQueue<E> {

    private static class Node<E> {
        final E item;
        final AtomicReference<LinkedQueue.Node<E>> next;

        public Node(E item, LinkedQueue.Node<E> next) {
            this.item = item;
            this.next = new AtomicReference<LinkedQueue.Node<E>>(next);
        }
    }

    private final LinkedQueue.Node<E> dummy = new LinkedQueue.Node<E>(null, null);
    private final AtomicReference<LinkedQueue.Node<E>> head = new AtomicReference<LinkedQueue.Node<E>>(dummy);
    private final AtomicReference<LinkedQueue.Node<E>> tail = new AtomicReference<LinkedQueue.Node<E>>(dummy);

    //放入分为两步：1.将尾node的next指向新节点(中间过程)2.将tail引用指向尾node(稳定过程)
    public boolean put(E item) {
        LinkedQueue.Node<E> newNode = new LinkedQueue.Node<E>(item, null);
        while(true) {
            LinkedQueue.Node<E> curTail = tail.get();
            LinkedQueue.Node<E> tailNext = curTail.next.get();
            if(curTail == tail.get()) {
                if(tailNext != null) {
                    // Queue in intermediate state, advance tail
                    tail.compareAndSet(curTail, tailNext);
                } else {
                    // In quiescent state, try inserting new node
                    if(curTail.next.compareAndSet(null, newNode)) {
                        // Insertion succeeded, try advancing tail
                        tail.compareAndSet(curTail, newNode);
                        return true;
                    }
                }
            }
        }
    }
}
