package com.wolf.concurrenttest.jcip.atomic;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Insertion in the Michael-Scott nonblocking queue algorithm
 * it must support fast access to both the head and the tail, to do this, it maintains separate head and tail pointers.
 * two pointers refer to the node at the tail: the next pointer of the current last element, and the tail pointer.
 * to insert a new element successfully, both of these pointers must be updated atomically.
 * separate CAS operations are required to update the two pointers, and if the first succeeds but the second one fails the queue is left in an inconsistent state.
 * and, even if both operations succeed, another thread could try to access the queue between the first and the second.
 * building a non-blocking algorithm for a linked queue required a plan for both these situations.
 * need several tricks to develop this plan:
 * + first is to ensure that the data structure is always in a consistent stat, even in the middle of an multi-step update.
 * to make the algorithm non-blocking, we must ensure that the failure of a thread does no prevent other threads from making progress.
 * the second trick is to make sure that if B arrives to find the data structure in the middle of an update by A,
 * enough information is already embodied in the data structure for B to finish the update for A.
 * if B "helps" A by finishing A's operation, B can proceed with its own operation without waiting for A.
 * when A gets around to finish its operation, it will find that B already did the job for it.
 * the tail pointer always refers to the sentinel(if the queue is empty), the last element in the queue, or(in the case that an operation is in mid-update) the second-to-last element.
 * <p>
 * inserting a new element involves updating two pointers.
 * + the first links the new node to the end of list by updating the next pointer of the current last element
 * + the second swings the tail pointer around to point to the new last element.
 * between these two operations, the queue is in the intermediate state(先更新了next，而tail还没有指向到最终newnode)
 * the key observation that enables both of the required tricks is that
 * if the queue is in the quiescent state, the next field of the link node pointed to by tail is null, and if it is in the intermediate state, tail.next is non-null.静止时tail.next为空，中间时tail.next不为空
 * so any thread can immediately tell the state of the queue by examining tail.next.所以任意线程可以通过tail.next状态进行判断queue的状态
 * further, if the queue is in the intermediate state, it can be restored to the quiescent state by advancing the tail pointer forward one node,只要前进tail指向即可保持静止
 * finishing the operation for whichever thread is in the middle of inserting an element.对于任意线程处于中间状态时，都以完成此操作
 * 协助+最终一致
 */
@ThreadSafe
public class LinkedQueue<E> {

    // 但链表，向后指向
    private static class Node<E> {
        final E item;
        final AtomicReference<Node<E>> next;

        public Node(E item, Node<E> next) {
            this.item = item;
            this.next = new AtomicReference<>(next);
        }
    }

    private final Node<E> dummy = new Node<>(null, null);
    private final AtomicReference<Node<E>> head = new AtomicReference<>(dummy);
    private final AtomicReference<Node<E>> tail = new AtomicReference<>(dummy);

    //放入分为两步：1.将尾node的next指向新节点(中间过程)2.将tail引用指向尾node(稳定过程)
    public boolean put(E item) {
        Node<E> newNode = new Node<>(item, null);
        while (true) {
            Node<E> curTail = tail.get();
            Node<E> tailNext = curTail.next.get();
            if (curTail == tail.get()) {// 当前tail没变
                if (tailNext != null) {// 检查队列是否处于中间状态，判断当前tail之后是否还有元素，若是则表明其他线程已经在插入过程中c和d之间
                    // Queue in intermediate state, advance tail
                    tail.compareAndSet(curTail, tailNext);// 与其等待过程中线程执行完，当前线程帮助其前进tail完成状态转换，然后重复检查这种场景，直到发现队列静止
                } else {// tail之后还是null，tail现在是最后节点，处于静止状态
                    // In quiescent state, try inserting new node，
                    if (curTail.next.compareAndSet(null, newNode)) {// C，可能失败，如多线程同时插入，不过没事，重试即可
                        // Insertion succeeded, try advancing tail
                        tail.compareAndSet(curTail, newNode);// D，可以由任意线程执行(如B处)，所以不看返回值，下面直接return true
                        return true;
                    }
                }
            }
        }
    }
}
