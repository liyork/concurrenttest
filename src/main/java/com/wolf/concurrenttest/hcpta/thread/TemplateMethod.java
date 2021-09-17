package com.wolf.concurrenttest.hcpta.thread;

/**
 * Description: print类似Thread.start，wrapPrint类似run，
 * 好处是，程序结构由父类控制，是final不允许重写，子类实现具体逻辑任务
 * Created on 2021/9/16 6:13 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TemplateMethod {
    public final void print(String message) {
        System.out.println("############");
        wrapPrint(message);
        System.out.println("############");
    }

    protected void wrapPrint(String message) {

    }

    public static void main(String[] args) {
        TemplateMethod t1 = new TemplateMethod() {
            @Override
            protected void wrapPrint(String message) {
                System.out.println("*" + message + "*");
            }
        };
        t1.print("hello thread");

        TemplateMethod t2 = new TemplateMethod() {
            @Override
            protected void wrapPrint(String message) {
                System.out.println("+" + message + "+");
            }
        };
        t2.print("hello thread");
    }
}
