package com.wolf.concurrenttest.stdlibtest;

import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * Description: update an object reference-boolean pair
 * that is used by some algorithms to let a node remain in a list while being marked as deleted
 * Created on 2021/7/12 1:48 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AtomicMarkableReferenceTest {
    public static void main(String[] args) {
        AtomicMarkableReference<Integer> reference = new AtomicMarkableReference<Integer>(1, true);

        Integer reference1 = reference.getReference();
        System.out.println(reference1);

        reference.compareAndSet(1, 2, true, false);
        reference1 = reference.getReference();
        System.out.println(reference1);
    }
}
