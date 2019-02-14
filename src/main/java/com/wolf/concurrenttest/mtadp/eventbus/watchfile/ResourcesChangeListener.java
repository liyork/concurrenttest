package com.wolf.concurrenttest.mtadp.eventbus.watchfile;

import com.wolf.concurrenttest.mtadp.eventbus.base.Registry;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/12
 */
public class ResourcesChangeListener {

    @Registry
    public void onChange(ResourceChangeEvent event) {

        System.out.printf("%s->%s\n", event.getPath(), event.getKind());
    }
}
