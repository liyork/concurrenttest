package com.wolf.concurrenttest.jcip.deadlock;

import net.jcip.annotations.GuardedBy;

import java.util.HashSet;
import java.util.Set;

/**
 * CooperatingDeadlock  同步方法太大，容易产生死锁
 * Lock-ordering deadlock between cooperating objects
 * 不容易发现多类之间协作用锁导致死锁的场景
 * setLocation和getImage可能获取的是同一个锁
 * a线程调用setLocation+notifyAvailable获取锁，b线程调用getImage+getLocation，产生死锁。
 */
public class CooperatingDeadlock {
    // Warning: deadlock-prone!
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

        public synchronized void setLocation(Point location) {
            this.location = location;
            if (location.equals(destination)) dispatcher.notifyAvailable(this);
        }
    }

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

        public synchronized Image getImage() {
            Image image = new Image();
            for (Taxi t : taxis)
                image.drawMarker(t.getLocation());
            return image;
        }
    }

    class Image {
        public void drawMarker(Point p) {
        }
    }
}
