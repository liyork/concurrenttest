package com.wolf.concurrenttest.productandconsumer.manytomany.uselockcondition;

/**
 * <p> Description:
 * <p/>
 * Date: 2016/6/12
 * Time: 15:12
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
public class Consumer extends Thread {
    // 每次消费的产品数量
    private int num;

    // 所在放置的仓库
    private Storage storage;

    public Consumer(Storage storage) {
        this.storage = storage;
    }

    public void run() {
        while(true) {
            consume(num);
        }

    }

    public void consume(int num) {
        try {
            storage.consume(num);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }
}