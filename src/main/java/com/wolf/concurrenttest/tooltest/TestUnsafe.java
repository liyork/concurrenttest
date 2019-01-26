package com.wolf.concurrenttest.tooltest;

/**
 * Description:自己测试，根本不可能有第一次cas之后出现，head指向第一个节点，tail还指向最初节点并且next还是最初节点！也就根本不会遇到else if (p == q)
 * <br/> Created on 15/06/2018 8:46 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class TestUnsafe<E> {

    public static void main(String[] args) {
        TestUnsafe<String> testUnsafe = new TestUnsafe<String>();
        testUnsafe.offer("a1");
        testUnsafe.offer("a2");
        testUnsafe.offer("a3");
        testUnsafe.offer("a4");

    }


    private transient volatile TestUnsafe.Node<E> head;

    private transient volatile TestUnsafe.Node<E> tail;

    /**
     * Creates a {@code ConcurrentLinkedQueue} that is initially empty.
     */
    public TestUnsafe() {
        head = tail = new TestUnsafe.Node<E>(null);
    }


    public boolean offer(E e) {
        final TestUnsafe.Node<E> newNode = new TestUnsafe.Node<E>(e);

        for (TestUnsafe.Node<E> t = tail, p = t;;) {
            TestUnsafe.Node<E> q = p.next;
            if (q == null) {
                // p is last node
                if (p.casNext(null, newNode)) {
                    // Successful CAS is the linearization point
                    // for e to become an element of this queue,
                    // and for newNode to become "live".
                    if (p != t) // hop two nodes at a time
                        casTail(t, newNode);  // Failure is OK.
                    return true;
                }
                // Lost CAS race to another thread; re-read next
            }
            else if (p == q)
                // We have fallen off list.  If tail is unchanged, it
                // will also be off-list, in which case we need to
                // jump to head, from which all live nodes are always
                // reachable.  Else the new tail is a better bet.
                p = (t != (t = tail)) ? t : head;
            else
                // Check for tail updates after two hops.
                p = (p != t && t != (t = tail)) ? t : q;
        }
    }

    private boolean casTail(TestUnsafe.Node<E> cmp, TestUnsafe.Node<E> val) {
        return UNSAFE.compareAndSwapObject(this, tailOffset, cmp, val);
    }



    private static class Node<E> {
        volatile E item;
        volatile TestUnsafe.Node<E> next;

        /**
         * Constructs a new node.  Uses relaxed write because item can
         * only be seen after publication via casNext.
         */
        Node(E item) {
            UNSAFE.putObject(this, itemOffset, item);
        }

        boolean casItem(E cmp, E val) {
            return UNSAFE.compareAndSwapObject(this, itemOffset, cmp, val);
        }

        void lazySetNext(TestUnsafe.Node<E> val) {
            UNSAFE.putOrderedObject(this, nextOffset, val);
        }

        boolean casNext(TestUnsafe.Node<E> cmp, TestUnsafe.Node<E> val) {
            return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
        }

        // Unsafe mechanics

        private static final sun.misc.Unsafe UNSAFE;
        private static final long itemOffset;
        private static final long nextOffset;

        static {
            try {
                UNSAFE = sun.misc.Unsafe.getUnsafe();
                Class<?> k = TestUnsafe.Node.class;
                itemOffset = UNSAFE.objectFieldOffset
                        (k.getDeclaredField("item"));
                nextOffset = UNSAFE.objectFieldOffset
                        (k.getDeclaredField("next"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    // Unsafe mechanics

    private static final sun.misc.Unsafe UNSAFE;
    private static final long headOffset;
    private static final long tailOffset;
    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class<?> k = java.util.concurrent.ConcurrentLinkedQueue.class;
            headOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("head"));
            tailOffset = UNSAFE.objectFieldOffset
                    (k.getDeclaredField("tail"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
