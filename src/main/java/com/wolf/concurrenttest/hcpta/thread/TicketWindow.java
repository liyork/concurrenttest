package com.wolf.concurrenttest.hcpta.thread;

/**
 * Description: 每一个窗口所出号码都从1到50，原因是每一个线程的逻辑执行单元都不一样，4个线程，票号都从
 * 0到50，没有任何交互。
 * Created on 2021/9/16 6:20 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TicketWindow extends Thread {
    // 柜台名称
    private final String name;

    // 最多受理50笔业务
    private static final int MAX = 50;

    private int index = 1;

    public TicketWindow(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        while (index <= MAX) {
            System.out.println("柜台：" + name + " 当前的号码是：" + (index++));
        }
    }

    public static void main(String[] args) {
        TicketWindow t1 = new TicketWindow("1号出号机");
        t1.start();

        TicketWindow t2 = new TicketWindow("2号出号机");
        t2.start();

        TicketWindow t3 = new TicketWindow("3号出号机");
        t3.start();

        TicketWindow t4 = new TicketWindow("4号出号机");
        t4.start();

    }

}
