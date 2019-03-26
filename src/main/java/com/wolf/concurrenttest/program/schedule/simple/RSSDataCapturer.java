package com.wolf.concurrenttest.program.schedule.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/22
 */
public class RSSDataCapturer {

    private static AtomicInteger count = new AtomicInteger(1);

    public RSSDataCapturer(String name) {
    }

    public List<CommonInformationItem> load(String url) {

        System.out.println(Thread.currentThread().getName() + " load ...");

        ArrayList<CommonInformationItem> commonInformationItems = new ArrayList<>();
        int seq = count.getAndIncrement();
        CommonInformationItem commonInformationItem = new CommonInformationItem("id" + seq, "source:" + seq, "fileName" + seq);
        commonInformationItems.add(commonInformationItem);

        return commonInformationItems;
    }
}
