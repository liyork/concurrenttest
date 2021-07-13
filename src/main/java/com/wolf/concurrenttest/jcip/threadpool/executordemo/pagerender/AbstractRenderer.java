package com.wolf.concurrenttest.jcip.threadpool.executordemo.pagerender;

import com.sun.scenario.effect.ImageData;

import java.util.List;

/**
 * Description: 仅为公用方法
 * Created on 2021/7/1 10:33 PM
 *
 * @author 李超
 * @version 0.0.1
 */
public class AbstractRenderer {
    protected void renderImage(ImageData data) {
    }

    protected List<ImageInfo> scanForimageInfo(CharSequence source) {
        return null;
    }

    protected void renderText(CharSequence future) {
    }

    protected class ImageInfo {
        public ImageData downloadImage() {
            return null;
        }
    }
}
