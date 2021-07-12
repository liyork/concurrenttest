package com.wolf.concurrenttest.jcip.atomic;

import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Description: non-blocking stack using Treiber's Algorithm
 * 栈上每个节点只引用一个其他节点，且被唯一引用
 * cas用于处理处理并发栈
 * 取数据到内存，cas设定新值
 * hope/cas+retry
 * Created on 2021/7/12 9:21 AM
 *
 * @author 李超
 * @version 0.0.1
 */
@ThreadSafe
public class ConcurrentStack<E> {
    AtomicReference<Node<E>> top = new AtomicReference<>();

    public void push(E item) {
        Node<E> newHead = new Node<>(item);
        Node<E> oldHead;
        do {
            // 先设定nex
            oldHead = top.get();
            newHead.next = oldHead;
            // cas设定新头，成功则退出，失败则重试
        } while (!top.compareAndSet(oldHead, newHead));
    }

    public E pop() {
        Node<E> oldHead;
        Node<E> newHead;
        do {
            // 获取头结点
            oldHead = top.get();
            if (oldHead == null) {
                return null;
            }
            // 内存获取新头
            newHead = oldHead.next;
            // cas设定新头，成功则返回否则重试
        } while (!top.compareAndSet(oldHead, newHead));
        return oldHead.item;
    }

    private class Node<E> {
        public final E item;
        public Node<E> next;

        public Node(E item) {
            this.item = item;
        }
    }
}
