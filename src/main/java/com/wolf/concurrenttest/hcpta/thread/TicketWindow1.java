package com.wolf.concurrenttest.hcpta.thread;

/**
 * Description: 无论TicketWindow1被实例化多少次，只用保证index唯一即可，改成static。
 * 若调大号码1000则出问题，线程安全
 * Created on 2021/9/16 6:20 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TicketWindow1 extends Thread {
    private final String name;

    private static final int MAX = 50;

    private static int index = 1;

    public TicketWindow1(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        while (index <= MAX) {
            System.out.println("柜台：" + name + " 当前的号码是：" + (index++));
        }
    }

    public static void main(String[] args) {
        TicketWindow1 t1 = new TicketWindow1("1号出号机");
        t1.start();

        TicketWindow1 t2 = new TicketWindow1("2号出号机");
        t2.start();

        TicketWindow1 t3 = new TicketWindow1("3号出号机");
        t3.start();

        TicketWindow1 t4 = new TicketWindow1("4号出号机");
        t4.start();

    }

}
