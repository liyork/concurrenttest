package com.wolf.concurrenttest.hcpta.thread;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/15 1:05 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class TryConcurrency {
    public static void main(String[] args) throws InterruptedException {
        browseNews();
        enjoyMusic();
    }

    // Browse the latest news
    private static void browseNews() throws InterruptedException {
        for (; ; ) {
            System.out.println("Uh-hub, the good news.");
            TimeUnit.SECONDS.sleep(1);
        }
    }

    // Listening and enjoy the music
    private static void enjoyMusic() throws InterruptedException {
        for (; ; ) {
            System.out.println("Un-hub, the nic music.");
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
