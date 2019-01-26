package com.wolf.concurrenttest.actualcombat;

/**
 * ThisEscape
 * <p/>
 * Implicitly allowing the this reference to escape
 * this逸出条件：构造器中有内部类，把内部类发布出去。
 * 解决方案：构造完后再暴露内部类，就是别在未构造好前就把本身给别人用
 * 构造方法中执行thread.start也同样会有问题，也可以先构造，再调用一个方法执行start
 *
 * @author Brian Goetz and Tim Peierls
 */
public class ThisEscape {
    private int id;

    private String name;

    //构造中将this引用逸出了(即内部类拥有ThisEscape的引用，可以调用内部类的onEvent方法暴露内部属性，而这个属性可能是在构造函数未完成时)
    public ThisEscape(EventSource source) {
        id = 1;
        source.registerListener(new EventListener() {
            public void onEvent(Event e) {
                System.out.println("id:" + id);
                System.out.println("name:" + name);
            }
        });

        //模拟耗时操作
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        name = "xxxx";
    }


    interface EventSource {
        void registerListener(EventListener e);
    }

    interface EventListener {
        void onEvent(Event e);
    }

    interface Event {
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);//休息一秒，让id赋值
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                eventListener[0].onEvent(null);
            }
        }).start();

        new ThisEscape(source);

    }


    class ThisSafe {

        public final int id;
        public final String name;
        private final EventListener listener;

        private ThisSafe() {
            id = 1;
            listener = new EventListener() {
                public void onEvent(Event e) {
                    System.out.println("id: " + ThisSafe.this.id);
                    System.out.println("name: " + ThisSafe.this.name);
                }
            };
            name = "flysqrlboy";
        }

        public ThisSafe getInstance(EventSource source) {
            ThisSafe safe = new ThisSafe();
            source.registerListener(safe.listener);
            return safe;
        }


    }
}

