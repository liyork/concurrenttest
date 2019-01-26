package com.wolf.concurrenttest.thread;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Description:
 * Collections.unmodifiableList 只不过包装了一下，但是修改原数组还是会影响unmodifiableList的
 *  ImmutableList.copyOf是真正的不可变list
 * <br/> Created on 2018/2/26 16:55
 *
 * @author 李超
 * @since 1.0.0
 */
public class IteratorMultiThreadTest {

    @Test
    public void testSingleThreadRemoveError() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(2);
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer integer = iterator.next();
            if (integer == 2)
                list.remove(integer);//modCount++
        }
    }

    @Test
    public void testSingleThreadRemoveRight() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(2);
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer integer = iterator.next();
            if (integer == 2)
                iterator.remove();//expectedModCount = modCount;
        }
    }

    //expectedModCount线程私有，而modCount属于arraylist的公有变量
    @Test
    public void testMultiThreadError() throws InterruptedException {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(1);//modCount++
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        Thread thread1 = new Thread(() -> {
            Iterator<Integer> iterator = list.iterator();//new Itr()，
            while (iterator.hasNext()) {
                Integer integer = iterator.next();
                System.out.println(integer);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread thread2 = new Thread(() -> {
            Iterator<Integer> iterator = list.iterator();
            while (iterator.hasNext()) {
                Integer integer = iterator.next();
                if (integer == 2)
                    iterator.remove();
            }
        });
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }
}
