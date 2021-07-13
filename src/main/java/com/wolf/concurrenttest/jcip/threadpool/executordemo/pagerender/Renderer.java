package com.wolf.concurrenttest.jcip.threadpool.executordemo.pagerender;

import com.sun.scenario.effect.ImageData;
import com.wolf.concurrenttest.jcip.stdlib.FutureTaskDemo;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Description: Using CompletionService to Render Page Elements
 * + 由于FutureRenderer中仅简单的分配两种任务，但是要是线程多了呢，如何分配task
 * + 直接分配可能任务会有不同大小，还会带来协调开销
 * 方案：
 * 创建separate task for downloading each image and execute them in a thread pool, 池+多线程，就能消除下载作为整体慢的问题
 * by fetching results from the CompletionService
 * > CompletionService combines the functionality of an Executor and a BlockingQueue方便将task提交然后获取任务结果
 * <p>
 * Created on 2021/7/2 6:48 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class Renderer extends AbstractRenderer {
    private final ExecutorService executor;

    public Renderer(ExecutorService executor) {
        this.executor = executor;
    }

    void renderPage(CharSequence source) {
        final List<ImageInfo> info = scanForimageInfo(source);
        ExecutorCompletionService<ImageData> completionService = new ExecutorCompletionService<>(executor);
        for (ImageInfo imageInfo : info) {
            completionService.submit(() -> imageInfo.downloadImage());
        }
        renderText(source);

        try {
            for (int t = 0, n = info.size(); t < n; t++) {
                Future<ImageData> f = completionService.take();
                ImageData imageData = f.get();
                renderImage(imageData);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw FutureTaskDemo.launderThrowable(e.getCause());
        }
    }
}
