package com.wolf.concurrenttest.hcpta.singlethreadexec;

/**
 * Description: 吃面线程，无死锁
 * 同一时间只能有一个线程获取刀和叉
 * Created on 2021/9/23 6:27 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class EatNoodleThread1 extends Thread {
    private final String name;

    private final TablewarePair tablewarePair;

    public EatNoodleThread1(String name, TablewarePair tablewarePair) {
        this.name = name;
        this.tablewarePair = tablewarePair;
    }

    @Override
    public void run() {
        while (true) {
            this.eat();
        }
    }

    // 吃面过程
    private void eat() {
        synchronized (tablewarePair) {
            System.out.println(name + " take up " + tablewarePair.getLeftTool() + "(left)");

            System.out.println(name + " take up " + tablewarePair.getRightTool() + "(right)");
            System.out.println(name + " is eating now.");
            System.out.println(name + " put down " + tablewarePair.getRightTool() + "(right)");

            System.out.println(name + " put down " + tablewarePair.getLeftTool() + "(left)");
        }
    }

    public static void main(String[] args) {
        Tableware fork = new Tableware("fork");
        Tableware knife = new Tableware("knife");
        TablewarePair tablewarePair1 = new TablewarePair(fork, knife);
        TablewarePair tablewarePair2 = new TablewarePair(fork, knife);
        new EatNoodleThread1("A", tablewarePair1).start();
        new EatNoodleThread1("B", tablewarePair2).start();
    }
}
