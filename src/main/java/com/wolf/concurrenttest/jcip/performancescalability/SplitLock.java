package com.wolf.concurrenttest.jcip.performancescalability;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.Set;

/**
 * Description: 切分锁，不同业务用不同锁，不相互影响。
 * Created on 2021/7/8 6:41 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class SplitLock {
    // Candidate for lock splitting
    @ThreadSafe
    class ServerStatus {
        @GuardedBy("this")
        public final Set<String> users;
        @GuardedBy("this")
        public final Set<String> queries;

        public ServerStatus(Set<String> users, Set<String> queries) {
            this.users = users;
            this.queries = queries;
        }

        public synchronized void addUser(String u) {
            users.add(u);
        }

        public synchronized void addQuery(String q) {
            queries.add(q);
        }

        public synchronized void removeUser(String u) {
            users.remove(u);
        }

        public synchronized void removeQuery(String q) {
            queries.remove(q);
        }
    }

    // refactored to use split locks
    // guard each with a separate lock
    // each new finer-grained lock will see less locking traffic than teh original coarser lock would have
    @ThreadSafe
    class ServerStatus2 {
        @GuardedBy("this")
        public final Set<String> users;
        @GuardedBy("this")
        public final Set<String> queries;

        public ServerStatus2(Set<String> users, Set<String> queries) {
            this.users = users;
            this.queries = queries;
        }

        public void addUser(String u) {
            synchronized (users) {
                users.add(u);
            }
        }

        public void addQuery(String q) {
            synchronized (queries) {
                queries.add(q);
            }
        }

        public void removeUser(String u) {
            synchronized (users) {
                users.remove(u);
            }
        }

        public void removeQuery(String q) {
            synchronized (queries) {
                queries.remove(q);
            }
        }
    }
}
