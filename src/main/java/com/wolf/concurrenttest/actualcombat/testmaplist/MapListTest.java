package com.wolf.concurrenttest.actualcombat.testmaplist;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 * <br/> Created on 2017/4/26 13:51
 *
 * @author 李超
 * @since 1.0.0
 */
public class MapListTest {

    private List<IInterceptor> preInterceptors = new ArrayList<IInterceptor>();

//    private Map<String, List<String>> interceptorCache = new HashMap<String, List<String>>();
    private Map<String, List<String>> interceptorCache = new ConcurrentHashMap<>();

    MapListTest() {
        preInterceptors.add(new LogInterceptor1());
        preInterceptors.add(new LogInterceptor2());
        preInterceptors.add(new LogInterceptor3());
    }

    //量一上来，还真是有缓存的快！！！
    public void preHandleWithCache(String aid) {

        for(IInterceptor interceptor : preInterceptors) {
            if(isMatch(interceptor, aid)) {
                boolean result = interceptor.preHandle();
                if(!result) {
                    throw new RuntimeException(interceptor.getClass().getName() + "前置处理拒绝了该请求");
                }
            }
        }
    }

    private Map<String, Map<String,String>> interceptorCache2 = new ConcurrentHashMap<>();
    //量一上来，还真是有缓存的快！！！
    public void preHandleWithCache2(String aid) {

        for(IInterceptor interceptor : preInterceptors) {
            if(isMatch2(interceptor, aid)) {
                boolean result = interceptor.preHandle();
                if(!result) {
                    throw new RuntimeException(interceptor.getClass().getName() + "前置处理拒绝了该请求");
                }
            }
        }
    }


    public void preHandleNoCache(String aid) {

        for(IInterceptor interceptor : preInterceptors) {
            if(canHandleByInterceptor(interceptor, aid)) {
                boolean result = interceptor.preHandle();
                if(!result) {
                    throw new RuntimeException(interceptor.getClass().getName() + "前置处理拒绝了该请求");
                }
            }
        }
    }

    private boolean isMatch(IInterceptor interceptor, String aid) {
        if(matchInCache(interceptor, aid)) {
//            System.out.println("hit the target ...");
            return true;
        } else {
            if(canHandleByInterceptor(interceptor, aid)) {
                putInCache(interceptor, aid);
                return true;
            } else {
                return false;
            }

        }
    }

    private boolean isMatch2(IInterceptor interceptor, String aid) {
        if(matchInCache2(interceptor, aid)) {
//            System.out.println("hit the target ...");
            return true;
        } else {
            if(canHandleByInterceptor(interceptor, aid)) {
                putInCache2(interceptor, aid);
                return true;
            } else {
                return false;
            }

        }
    }

    private boolean matchInCache(IInterceptor interceptor, String aid) {
        String interceptorName = interceptor.getClass().getName();
        if(CollectionUtils.isEmpty(interceptorCache.get(aid))) {
            return false;
        }

        return interceptorCache.get(aid).contains(interceptorName);
    }

    private boolean matchInCache2(IInterceptor interceptor, String aid) {
        String interceptorName = interceptor.getClass().getName();
        if(MapUtils.isEmpty(interceptorCache2.get(aid))) {
            return false;
        }

        return interceptorCache2.get(aid).containsKey(interceptorName);
    }

    private boolean canHandleByInterceptor(IInterceptor interceptor, String aid) {
        InterceptorDesc desc = interceptor.getClass().getAnnotation(InterceptorDesc.class);
        Pattern p = Pattern.compile(desc.regex());
        Matcher m = p.matcher(aid);
        return m.find();
    }

    private void putInCache(IInterceptor interceptor, String aid) {
        String interceptorName = interceptor.getClass().getName();

        List<String> interceptorNames = interceptorCache.get(aid);
        if(interceptorNames == null) {
            interceptorNames = new ArrayList<String>();
            interceptorCache.put(aid, interceptorNames);
        }

        interceptorNames.add(interceptorName);
    }
    private void putInCache2(IInterceptor interceptor, String aid) {
        String interceptorName = interceptor.getClass().getName();

        Map<String,String> interceptorNames = interceptorCache2.get(aid);
        if(interceptorNames == null) {
            interceptorNames = new ConcurrentHashMap<>();
            interceptorCache2.put(aid, interceptorNames);
        }

        interceptorNames.put(interceptorName,interceptorName);
    }

    public static void main(String[] args) {

        final MapListTest mapListTest = new MapListTest();

        ExecutorService executorService = Executors.newFixedThreadPool(50);

        final List<String> aids = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            aids.add("/xxx/yyy/"+i);
        }

        int count = 500000;
        final CountDownLatch countDownLatch = new CountDownLatch(count);
        final Random random = new Random();

        long start = System.currentTimeMillis();
        for(int i = 0; i < count; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    int index = random.nextInt(100);
//                    mapListTest.preHandleWithCache(aids.get(index));
                    mapListTest.preHandleWithCache2(aids.get(index));
                    countDownLatch.countDown();
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();

        long end = System.currentTimeMillis();

        System.out.println(end-start);
    }

}
