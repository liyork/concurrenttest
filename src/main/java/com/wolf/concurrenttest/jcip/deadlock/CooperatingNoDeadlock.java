package com.wolf.concurrenttest.jcip.deadlock;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.HashSet;
import java.util.Set;

/**
 * CooperatingNoDeadlock  缩小同步代码块保证仅有的操作共享数据
 * 可能丢失原子性，不过这里可接受。要是需要则用其他技术，如构造并发对象以便单线程能执行这个代码路径
 * Using open calls to avoiding deadlock between cooperating objects
 */
class CooperatingNoDeadlock {
    @ThreadSafe
    class Taxi {
        @GuardedBy("this")
        private Point location, destination;
        private final Dispatcher dispatcher;

        public Taxi(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public synchronized Point getLocation() {
            return location;
        }

        public void setLocation(Point location) {
            boolean reachedDestination;
            synchronized (this) {// 仅在操作共享数据时上锁
                this.location = location;
                reachedDestination = location.equals(destination);
            }
            if (reachedDestination) dispatcher.notifyAvailable(this);
        }

    }

    @ThreadSafe
    class Dispatcher {
        @GuardedBy("this")
        private final Set<Taxi> taxis;
        @GuardedBy("this")
        private final Set<Taxi> availableTaxis;

        public Dispatcher() {
            taxis = new HashSet<Taxi>();
            availableTaxis = new HashSet<Taxi>();
        }

        public synchronized void notifyAvailable(Taxi taxi) {
            availableTaxis.add(taxi);
        }

        public Image getImage() {
            Set<Taxi> copy;
            synchronized (this) {// 拷贝数据后释放锁
                copy = new HashSet<>(taxis);
            }
            Image image = new Image();
            for (Taxi t : copy)
                image.drawMarker(t.getLocation());
            return image;
        }
    }

    class Image {
        public void drawMarker(Point p) {
        }
    }

}