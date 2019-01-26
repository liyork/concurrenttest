package com.wolf.concurrenttest.productandconsumer.inturn;

/**
 * Description:消息进来直接放给阻塞队列，然后继续处理新的消息，后续的工作由splitworker切分后，每个队列有一个线程操作。
 * <br/> Created on 14/03/2018 10:34 AM
 *
 * @author 李超
 * @since 1.0.0
 */
public class FacadeSelectorTest {

    public static void main(String[] args) {
        FacadeSelector facadeSelector = new FacadeSelector();
        facadeSelector.start();

        for (int i = 0; i < 100; i++) {
            facadeSelector.connect(i);
        }

    }
}
