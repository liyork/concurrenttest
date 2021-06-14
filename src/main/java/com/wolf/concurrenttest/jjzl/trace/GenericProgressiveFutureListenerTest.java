package com.wolf.concurrenttest.jjzl.trace;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericProgressiveFutureListener;
import io.netty.util.concurrent.ProgressiveFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 针对写半包场景可以用此类进行精准监控，
 * Created on 2021/6/7 7:04 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class GenericProgressiveFutureListenerTest implements GenericProgressiveFutureListener {
    private static final Logger logger = LoggerFactory.getLogger(GenericProgressiveFutureListenerTest.class);

    @Override
    public void operationProgressed(ProgressiveFuture future, long progress, long total) throws Exception {
        logger.info("GenericProgressiveFutureListenerTest operationProgressed progress:{}, total:{}", progress, total);
    }

    @Override
    public void operationComplete(Future future) throws Exception {
        logger.info("GenericProgressiveFutureListenerTest operationComplete");
    }
}
