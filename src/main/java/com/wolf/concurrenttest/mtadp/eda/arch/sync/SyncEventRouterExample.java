package com.wolf.concurrenttest.mtadp.eda.arch.sync;

import com.wolf.concurrenttest.mtadp.eda.arch.InputEvent;
import com.wolf.concurrenttest.mtadp.eda.arch.ResultEvent;

/**
 * Description: 每个事件都有对应的handler处理，需要提前注册到Router上，便于Router进行dispatch
 *
 * 不同数据的处理过程之间根本无须直到彼此的存在，一切都由EventRouter控制，是一种松耦合的设计。扩展性也非常强，channel可以
 * 容易扩展和替换。EventRouter统一负责event的调配，在消息到达channel之前可以进行很多过滤、数据验证、权限控制、数据增强等。
 *
 * @author 李超
 * @date 2019/02/13
 */
public class SyncEventRouterExample {

    static class SyncInputEventHandler implements Channel<InputEvent> {

        private final SyncEventRouter syncEventRouter;

        public SyncInputEventHandler(SyncEventRouter syncEventRouter) {

            this.syncEventRouter = syncEventRouter;
        }

        @Override
        public void dispatch(InputEvent message) {

            System.out.printf("X:%d,Y:%d\n", message.getX(), message.getY());
            int result = message.getX() + message.getY();
            syncEventRouter.route(new ResultEvent(result));//产生新事件并请求syncEventRouter路由到channel
        }
    }

    static class SyncResultEventHandler implements Channel<ResultEvent> {

        @Override
        public void dispatch(ResultEvent message) {

            System.out.println("the result is:" + message.getResult());
        }
    }

    public static void main(String[] args) {

        SyncEventRouter eventRouter = new SyncEventRouter();
        eventRouter.registerChannel(InputEvent.class, new SyncInputEventHandler(eventRouter));
        eventRouter.registerChannel(ResultEvent.class, new SyncResultEventHandler());
        eventRouter.route(new InputEvent(1, 2));
    }
}
