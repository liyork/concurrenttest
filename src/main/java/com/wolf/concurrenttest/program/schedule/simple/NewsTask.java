package com.wolf.concurrenttest.program.schedule.simple;

import java.util.Date;
import java.util.List;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/22
 */
public class NewsTask implements Runnable {

    private String name;
    private String url;
    private NewsBuffer buffer;

    public NewsTask(String name, String url, NewsBuffer buffer) {
        this.name = name;
        this.url = url;
        this.buffer = buffer;
    }

    @Override
    public void run() {

        System.out.println(name + " : Running. " + new Date());
        RSSDataCapturer capturer = new RSSDataCapturer(name);
        List<CommonInformationItem> items = capturer.load(url);

        for (CommonInformationItem item : items) {
            buffer.add(item);
        }
    }

    public String getName() {
        return this.name;
    }
}
