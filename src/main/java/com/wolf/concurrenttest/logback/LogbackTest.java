package com.wolf.concurrenttest.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 使用logback-test.xml
 * 优先打印子logger符合的，然后看additivity决定是否打印到父logger
 *
 * @author 李超
 * @date 2019/08/27
 */
public class LogbackTest {

    private static final Logger logger = LoggerFactory.getLogger(LogbackTest.class);

    public static void main(String[] args) {
        logger.debug("debug xxxxx");
        logger.info("info xxxxx");
        logger.warn("warn xxxxx");
        logger.error("error xxxxx");
    }
}
