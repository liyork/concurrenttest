package com.wolf.concurrenttest.jcip;

/**
 * Description: 展示this逸出(Implicitly allowing the this reference to escape)
 * this逸出条件：
 * + 构造器中有内部类，把内部类发布出去，而这时可能当前对象还未构造完成。方案：构造完后再暴露内部类，就是别在未构造好前就把本身给别人用
 * + 构造方法中执行thread.start也同样会有问题，也可以先构造，再调用一个方法执行start
 * 方案：
 * 使用私有构造并提供工厂方法，避免不合适的构造方法导致的this溢出
 * using a factory method to prevent the this Reference from Escaping During Construction
 * Created on 2021/6/27 8:28 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class ThisEscape {
    private int id;

    private String name;
    private final EventListener listener;

    // 错误：
    // 构造中将this引用逸出了(即内部类拥有ThisEscape的引用，可以调用内部类的onEvent方法暴露内部属性，而这个属性可能是在构造函数未完成时)
    // 直接通过构造方法中构造EventListener并用source注册时，就将this暴露给外面了，而这时this还没有构造完成
    public ThisEscape(EventSource source) {
        id = 1;
        this.listener = new EventListener() {
            public void onEvent(String event) {
                System.out.println("doSomething on event");
                System.out.println("id:" + id);
                System.out.println("name:" + name);
            }
        };
        source.registerListener(listener);

        //模拟耗时操作
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 由于提前暴露了this，导致这里的name可能未正确初始化，进而导致外界使用了错误的对象
        name = "xxxx";
    }

    private ThisEscape() {
        this.listener = new EventListener() {
            public void onEvent(String event) {
                System.out.println("doSomething on event");
            }
        };
    }

    public static void main(String[] args) {
        final EventListener[] eventListener = new EventListener[1];
        EventSource source = new EventSource() {
            @Override
            public void registerListener(EventListener e) {
                eventListener[0] = e;
            }
        };

        //构造线程，模拟直接暴露ThisEscape内部属性，模拟并发时，同时访问对象属性。
        new Thread(() -> {
            try {
                Thread.sleep(1000);//休息一秒，让id赋值
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            eventListener[0].onEvent(null);
        }).start();

        new ThisEscape(source);
    }

    // 正确方式:外界通过工厂方法
    // 内部先构造ThisEscape，然后EventListener也完成，this中的属性被保护并完成了正确的构造
    // 再调用source(参数)进行注册，这时外面看到的this就是一个完全构造好的，不会有中间状态
    public static ThisEscape newInstance(EventSource source) {
        ThisEscape thisEscape = new ThisEscape();
        source.registerListener(thisEscape.listener);
        return thisEscape;
    }

    private static class EventSource {
        public void registerListener(EventListener listener) {
        }
    }

    abstract class EventListener {
        public abstract void onEvent(String event);
    }
}
