package com.wolf.concurrenttest.program.schedule.simple;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Description: 读写之前的缓存
 *
 * @author 李超
 * @date 2019/02/22
 */
public class NewsBuffer {

    private LinkedBlockingQueue<CommonInformationItem> buffer;
    private ConcurrentHashMap<String, String> storedItems;

    public NewsBuffer() {
        buffer = new LinkedBlockingQueue<>();
        storedItems = new ConcurrentHashMap<>();
    }

    public void add(CommonInformationItem item) {

        storedItems.compute(item.getId(), (id, oldSource) -> {
            if (oldSource == null) {
                System.out.println("NewsBuffer.add ...oldSource == null ");
                buffer.add(item);
                return item.getSource();
            } else {
                System.out.println("Item " + item.getId() + " has been processed before");
                return oldSource;
            }
        });

    }

    public CommonInformationItem get() throws InterruptedException {
        return buffer.take();
    }
}
