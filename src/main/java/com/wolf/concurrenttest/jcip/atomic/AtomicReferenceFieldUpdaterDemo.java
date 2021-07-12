package com.wolf.concurrenttest.jcip.atomic;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Description: ConcurrentLinkedQueue用volatile+AtomicReferenceFieldUpdater
 * the atomic field updater classes represent a reflection-based "view" of an existing volatile field
 * so that CAS can be used on existing volatile fields.
 * for frequently allocated, short-lived objects like queue link nodes, eliminating the creation of an AtomicaReference
 * for each Node is significant enough to reduce the cost of insertion operations.
 * Created on 2021/7/12 1:23 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AtomicReferenceFieldUpdaterDemo {
    private static class Node<E> {
        final E item;
        volatile Node<E> next;

        public Node(E item) {
            this.item = item;
        }
    }

    private static AtomicReferenceFieldUpdater<Node, Node> updater = AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "next");

    public static void main(String[] args) {
        Node<Integer> node = new Node<>(1);
        Node<Integer> next = new Node<>(2);
        node.next = next;
        Node<Integer> node2 = new Node<>(3);
        updater.compareAndSet(node, next, node2);

        System.out.println(node.next.item);
    }
}
