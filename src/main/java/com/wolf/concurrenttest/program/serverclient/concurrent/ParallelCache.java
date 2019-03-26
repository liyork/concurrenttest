package com.wolf.concurrenttest.program.serverclient.concurrent;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 缓存命令结果
 *
 * @author 李超
 * @date 2019/02/20
 */
public class ParallelCache {

    private final ConcurrentHashMap<String, CacheItem> cache;

    private final CleanCacheTask task;

    private final Thread thread;

    private static int MAX_LIVING_TIME_MILLIS = 600_000;

    public ParallelCache() {
        cache = new ConcurrentHashMap();
        task = new CleanCacheTask(this);
        thread = new Thread(task, "clean thread");
        thread.start();
    }

    public void put(String command, String response) {
        CacheItem cacheItem = new CacheItem(command, response);
        cache.put(command, cacheItem);
    }

    public String get(String command) {
        CacheItem cacheItem = cache.get(command);
        if (cacheItem == null) {
            return null;
        }

        cacheItem.setAccessData(new Date());
        return cacheItem.getResponse();
    }

    public void cleanCache() {

        Date revisionDate = new Date();
        Iterator<CacheItem> iterator = cache.values().iterator();
        while (iterator.hasNext()) {
            CacheItem item = iterator.next();
            if (revisionDate.getTime() - item.getAccessDate().getTime() > MAX_LIVING_TIME_MILLIS) {
                iterator.remove();
            }
        }
    }

    public void shutdown() {
        thread.interrupt();
    }

    public int getItemCount() {
        return cache.size();
    }
}
