package com.wolf.concurrenttest.mtadp.eda.arch.use;

import com.wolf.concurrenttest.mtadp.eda.arch.async.AsyncEventRouter;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/13
 */
public class UserChatTest {

    public static void main(String[] args) {

        AsyncEventRouter eventRouter = new AsyncEventRouter();
        eventRouter.registerChannel(UserOnlineEvent.class, new UserOnlineEventChannel());
        eventRouter.registerChannel(UserOfflineEvent.class, new UserOfflineEventChannel());
        eventRouter.registerChannel(UserChatEvent.class, new UserChatEventChannel());

        Thread[] threads = new Thread[3];

        IntStream.range(0, 3).forEach(i -> {
            UserChatTask userChatTask = new UserChatTask(new User("x" + i), eventRouter);
            Thread thread = new Thread(userChatTask, "x" + i);
            thread.start();
            threads[i] = thread;
        });

        Arrays.stream(threads).forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        eventRouter.shutdown();
    }

    static class UserChatTask implements Runnable {

        private final User user;

        private final AsyncEventRouter eventRouter;

        public UserChatTask(User user, AsyncEventRouter eventRouter) {

            this.user = user;
            this.eventRouter = eventRouter;
        }

        @Override
        public void run() {

            try {
                eventRouter.route(new UserOnlineEvent(user));
                for (int i = 0; i < 5; i++) {
                    UserChatEvent userChatEvent = new UserChatEvent(user, Thread.currentThread().getName() + "-hello=" + i);
                    eventRouter.route(userChatEvent);
                    TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                eventRouter.route(new UserOfflineEvent(user));
            }
        }
    }
}
