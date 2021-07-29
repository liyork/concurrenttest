package com.wolf.concurrenttest.jcip.threadpool.executordemo.pagerender;

import com.wolf.concurrenttest.jcip.stdlib.FutureTaskDemo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Description: waiting for image download with future
 * <p>
 * Created on 2021/7/1 10:33 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class FutureRenderer extends AbstractRenderer {
    private final ExecutorService executorService = null;

    void renderPage(CharSequence source) {
        final List<ImageInfo> imageInfos = scanForimageInfo(source);
        Callable<List<ImageData>> task = () -> {
            List<ImageData> result = new ArrayList<>();
            for (ImageInfo imageInfo : imageInfos) {
                result.add(imageInfo.downloadImage());
            }
            return result;
        };

        // 提交图片下载task(I/O-bound)，与renderText并行执行(CPU-bound)，
        Future<List<ImageData>> future = executorService.submit(task);// return a future describing the task's execution
        renderText(source);

        try {
            List<ImageData> imageData = future.get();// 这里不需要等待所有都下载完，可以各自下载
            for (ImageData data : imageData) {
                renderImage(data);
            }
        } catch (InterruptedException e) {
            // re-assert the thread's interrupted status
            Thread.currentThread().interrupt();
            future.cancel(true);
        } catch (ExecutionException e) {
            throw FutureTaskDemo.launderThrowable(e.getCause());
        }
    }
}
