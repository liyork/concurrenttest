package com.wolf.concurrenttest.mtadp.eda.arch.async;

import com.wolf.concurrenttest.mtadp.eda.arch.InputEvent;
import com.wolf.concurrenttest.mtadp.eda.arch.ResultEvent;
import com.wolf.concurrenttest.mtadp.eda.arch.sync.Event;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/13
 */
public class AsyncEventDispatcherExample {

    static class AsyncInputEventHandler extends AsyncChannel {

        private final AsyncEventRouter dispatcher;

        public AsyncInputEventHandler(AsyncEventRouter dispatcher) {
            this.dispatcher = dispatcher;
        }

        @Override
        protected void handle(Event event) {

            InputEvent inputEvent = (InputEvent) event;
            System.out.printf("X:%d,Y:%d\n", inputEvent.getX(), inputEvent.getY());
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int result = inputEvent.getY() + inputEvent.getY();
            dispatcher.route(new ResultEvent(result));
        }
    }

    static class AsyncResultEventHandler extends AsyncChannel {

        @Override
        protected void handle(Event event) {

            ResultEvent resultEvent = (ResultEvent) event;
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("the result is:" + resultEvent.getResult());
        }
    }

    public static void main(String[] args) throws InterruptedException {

        AsyncEventRouter dispatcher = new AsyncEventRouter();
        dispatcher.registerChannel(InputEvent.class, new AsyncInputEventHandler(dispatcher));
        dispatcher.registerChannel(ResultEvent.class, new AsyncResultEventHandler());
        dispatcher.route(new InputEvent(1, 2));

        TimeUnit.SECONDS.sleep(6);
        dispatcher.shutdown();
    }
}
