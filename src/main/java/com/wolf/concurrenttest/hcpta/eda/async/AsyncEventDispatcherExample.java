package com.wolf.concurrenttest.hcpta.eda.async;

import com.wolf.concurrenttest.hcpta.eda.Event;
import com.wolf.concurrenttest.hcpta.eda.EventDispatcherExample;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Created on 2021/9/28 9:13 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AsyncEventDispatcherExample {
    static class AsyncResultEventHandler extends AsyncChannel {

        @Override
        public void handle(Event message) {
            EventDispatcherExample.ResultEvent resultEvent = (EventDispatcherExample.ResultEvent) message;
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("The result is:" + resultEvent.getResult());
        }
    }

    static class AsyncInputEventHandler extends AsyncChannel {

        private final AsyncEventDispatcher dispatcher;

        public AsyncInputEventHandler(AsyncEventDispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        @Override
        protected void handle(Event message) {
            EventDispatcherExample.InputEvent inputEvent = (EventDispatcherExample.InputEvent) message;
            System.out.printf("X:%d, Y:%d\n" + inputEvent.getX(), inputEvent.getY());
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int result = inputEvent.getX() + inputEvent.getY();
            dispatcher.dispatch(new EventDispatcherExample.ResultEvent(result));
        }
    }

    public static void main(String[] args) {
        AsyncEventDispatcher dispatcher = new AsyncEventDispatcher();
        dispatcher.registerChannel(EventDispatcherExample.InputEvent.class, new AsyncInputEventHandler(dispatcher));
        dispatcher.registerChannel(EventDispatcherExample.ResultEvent.class, new AsyncResultEventHandler());

        dispatcher.dispatch(new EventDispatcherExample.InputEvent(1, 2));
    }
}
