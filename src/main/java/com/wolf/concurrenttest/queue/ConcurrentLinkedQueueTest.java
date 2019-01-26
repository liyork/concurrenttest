package com.wolf.concurrenttest.queue;

import org.junit.Test;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:基于1.7分析
 * 当多个线程共享访问一个公共 collection 时，ConcurrentLinkedQueue 是一个恰当的选择
 * <p>
 * add执行过程分析:
 * 两个线程并发执行if (p.casNext(null, newNode)) {只能一个成功设定，这时head指向newnode而tail指向tail
 * 失败的线程和后来的线程争夺进入 else if (p == q)都拿到了head
 * 再执行p.casNext只有一个成功，但是成功线程并未执行casTail
 * 失败的线程只能不断失败执行p.casNext由于他只期盼next为null，只有当时p.casNext成功并设定了tail,然后他才能看到tail变化，取得tail
 * 然后后来的线程也进入p = (p != t && t != (t = tail)) ? t : q;一起竞争进行后续的p.casNext，只能一个成功，那么后续的还会和后续的进入else操作
 * <p>
 * 当tail在倒数第二个node时，都进入p = (p != t && t != (t = tail)) ? t : q;这时一个成功移动并执行了p.casNext，但是未执行casTail,
 * 另一个线程移动并执行p.casNext失败，再次循环p != t但是tail未改变，再次移动，执行casNext成功，更新了tail，然后第一个再更新tail向前了！这倒没事
 * 若上面第一个线程改了tail，那么这里就使用tail进行p.casNext，不更新tail
 * <p>
 *     通过隔节点更新tail，达到减少每次cas更新消耗，但多了读取，用2次读volatile换取1次cas写。
 *     弱一致性
 * <p>
 * 防止频繁移动head或tail，每隔两个节点做移动，第一次添加时p=t不移动tail，第二次添加时p!=t，移动tail
 * <p>
 * <p>
 * <p>
 * 似乎作者的两处cas使用ifelse各种状况围堵
 * <br/> Created on 2018/2/26 17:31
 *
 * @author 李超
 * @since 1.0.0
 */
public class ConcurrentLinkedQueueTest {

    public static void main(String[] args) {
        testSingleThread();
//        testMultiThread();
//        testIterator();
    }

    private static void testSingleThread() {
        //两个操作：尾部添加(cas)，修改tail指向(cas)，防止每次都cas修改tail采用隔一个元素修改一次
        ConcurrentLinkedQueue<Integer> concurrentLinkedQueue = new ConcurrentLinkedQueue<Integer>();

        //进入for (Node<E> t = tail, p = t;;) { 时t就被私有化局部变量定格了，后续p和q的变化也是局部。只有p.casNext和casTail会涉及并发修改共享内存
        concurrentLinkedQueue.add(1);//第一次p.casNext(null, newNode)后head指向新节点，tail以及tail的next都指向tail所在node(可能这个是unsafe的内部实现。)。这里若有并发则一个成功另一个执行下面的(p == q)
        concurrentLinkedQueue.add(2);//进入(p == q)，执行(t != (t = tail)) ? t : head; 表示tail改变了则选择新tail，未改变则使用head(这个只发生在第二次插入)，再循环则进入(q == null)添加为节点后casTail修改tail指向。tail自关联时从head找，否则再next
        concurrentLinkedQueue.add(3);//仅执行p.casNext
        concurrentLinkedQueue.add(4);//(p != t && t != (t = tail)) ? t : q， 表示移动且tail被修改则用新tail否则使用q(p.next)，再循环执行p.casNext和casTail

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                concurrentLinkedQueue.add(5);//仅执行p.casNext
            }
        });

        for (Integer integer : concurrentLinkedQueue) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(integer);
        }


        System.out.println(concurrentLinkedQueue.poll());//仅仅执行p.casItem，但更新完，head指向了下一个node，p和q都指向了第一个节点的item为null。。不仅改了item也改了next
        System.out.println(concurrentLinkedQueue.poll());

        concurrentLinkedQueue.add(6);//只操作尾部和头部没有关系

        System.out.println(concurrentLinkedQueue.poll());
        System.out.println(concurrentLinkedQueue.poll());

        executorService.shutdown();
    }


    //     poll执行过程分析:
//             if (item != null && p.casItem(item, null)) { 若head不为空则进行cas置空，
//  if (p != h) 若p移动过则更新head为新p，设置h自引用
//
//  else if ((q = p.next) == null) { 若第一个为空，第二个也为空，更新head为第二node，表示链表空
//
//  else if (p == q) 自引用了，重新计算，跳过死节点
//
//  else p = q;向后移动
//
//  执行顺序:
//  进入循环后，h则固定了，p会进行移动。
//
//  两线程并发执行if (item != null && p.casItem(item, null)) { 只有一个成功，由于没有移动，则不更新head
//                     失败的线程进入else p = q; 多线程争抢，都移动，然后再循环进入设定p.casItem，这时p变动了，若p的后面还有节点则设定后面节点为新head，否则设定p并更新h自引用
//                     若上面没有更新head，那么当前失败线程再循环执行else p = q移动后重新执行上面相同操作。
//
//  若一个线程执行 if (item != null && p.casItem(item, null)) { 失败，这时其他线程将下下节点设为head，而下一个节点则自引用，这个线程再执行else p = q后进入else if (p == q)就会重新跳跃重新找head
//                         上步中若只有两个节点，那么会更新head，链表空了。
    private static void testMultiThread() {
        ConcurrentLinkedQueue<Integer> concurrentLinkedQueue = new ConcurrentLinkedQueue<Integer>();
        Random random = new Random();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 2000; i++) {
                    try {
                        Thread.sleep(random.nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("add " + Thread.currentThread().getName());
                    concurrentLinkedQueue.add(i);
                }
            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 2000; i++) {
                    try {
                        Thread.sleep(random.nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("poll " + Thread.currentThread().getName());
                    concurrentLinkedQueue.poll();
                }
            }
        });

        executorService.shutdown();
    }


    /**
     * iteraotr过程：
     * 构造时创建Itr，找到nextNode用于hasNext。
     * <p>
     * 若第一次应该无并发问题，找到第一个item不为null的node并设定nextnode
     * 当调用next时，设定prednode和p，
     * 若item不为空则返回，
     * 若item为空继续找下个item不为null的node，设定新next，移除中间节点，移动p，下次循环不为空则返回
     * 若next为空表名没有节点了。
     * <p>
     * itr中的字段没有使用volatile，那么表名对于一个itr不会有多线程访问，并发问题只发生再获取next时，但是这时或者之前有人操作过链表。
     * <p>
     * lastRet用于itr的remove，每次next时将当前nextnode赋值给他
     */
    private static void testIterator() {
        ConcurrentLinkedQueue<Integer> concurrentLinkedQueue = new ConcurrentLinkedQueue<Integer>();

        concurrentLinkedQueue.add(1);
        concurrentLinkedQueue.add(2);
        concurrentLinkedQueue.add(3);
        concurrentLinkedQueue.add(4);
        concurrentLinkedQueue.add(5);

        Iterator<Integer> iterator = concurrentLinkedQueue.iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();//记录当前值a，游标移动，返回a
            System.out.println(next);
        }
    }


    @Test
    public void test() {

        int tail = 2;
        int t = tail;
        int head = 3;
        boolean b = t != (t = tail);//从左到右执行，先取得t值，然后t=tail，
        System.out.println(b);
        int i = b ? t : head;
        System.out.println(i);
    }
}
