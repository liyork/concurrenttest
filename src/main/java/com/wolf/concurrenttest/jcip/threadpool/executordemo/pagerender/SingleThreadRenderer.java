package com.wolf.concurrenttest.jcip.threadpool.executordemo.pagerender;

import com.sun.scenario.effect.ImageData;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: rendering page elements sequentially
 * 需要等很长时间当所有内容被渲染，而且文本先渲染同时有个框x。下载图片会有I/O，而这时cpu空闲。
 * Created on 2021/7/1 10:10 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class SingleThreadRenderer extends AbstractRenderer {
    void renderPage(CharSequence source) {
        renderText(source);
        List<ImageData> imageData = new ArrayList<>();
        for (ImageInfo imageInfo : scanForimageInfo(source)) {
            imageData.add(imageInfo.downloadImage());
        }
        for (ImageData data : imageData) {
            renderImage(data);
        }
    }
}
