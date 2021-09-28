package com.wolf.concurrenttest.hcpta.eventbus.watch;

import com.wolf.concurrenttest.hcpta.eventbus.Subscribe;

/**
 * Description: 接受文件目录变化的Subscriber
 * Created on 2021/9/27 11:18 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FileChangeListener {

    @Subscribe
    public void onChange(FileChangeEvent event) {
        System.out.printf("%s-%s\n", event.getPath(), event.getKind());
    }
}
