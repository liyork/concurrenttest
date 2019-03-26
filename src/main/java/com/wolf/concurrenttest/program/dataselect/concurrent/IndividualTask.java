package com.wolf.concurrenttest.program.dataselect.concurrent;

import com.wolf.concurrenttest.program.dataselect.CensusData;
import com.wolf.concurrenttest.program.dataselect.Filter;
import com.wolf.concurrenttest.program.dataselect.FilterData;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.RecursiveTask;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/03/06
 */
public class IndividualTask extends RecursiveTask<CensusData> {

    private CensusData[] data;
    private int start, end, size;
    private TaskManager manager;
    private List<FilterData> filters;

    public IndividualTask(CensusData[] data, int start,
                          int end, TaskManager manager,
                          int size, List<FilterData> filters) {
        this.data = data;
        this.start = start;
        this.end = end;
        this.manager = manager;
        this.size = size;
        this.filters = filters;
    }

    @Override
    protected CensusData compute() {

        if (end - start <= size) {
            //响应中断
            for (int i = start; i < end && !Thread.currentThread().isInterrupted(); i++) {
                CensusData censusData = data[i];
                if (Filter.filter(censusData, filters)) {
                    System.out.println("Found: " + i);
                    manager.cancelTasks(this);
                    return censusData;
                }
            }

            return null;
        } else {
            int mid = (start + end) / 2;
            IndividualTask task1 = new IndividualTask(data, start, mid, manager, size, filters);
            IndividualTask task2 = new IndividualTask(data, mid, end, manager, size, filters);

            manager.addTask(task1);
            manager.addTask(task2);
            manager.deleteTask(this);

            //异步发送任务
            task1.fork();
            task2.fork();
            //join()启动之后，如果任务撤销，将抛出异常，或者在方法内部抛出一个未校验异常，
            //quietlyJoin()方法则不抛出任何异常
            task1.quietlyJoin();
            task2.quietlyJoin();

//            manager.deleteTask(task1);
//            manager.deleteTask(task2);

            try {
                CensusData res = task1.join();
                if (res != null) {
                    return res;
                }
                manager.deleteTask(task1);
            } catch (CancellationException e) {//抛出其他异常则直接传播，CancellationException则忽略
                e.printStackTrace();
            }

            try {
                CensusData res = task2.join();
                if (res != null) {
                    return res;
                }
                manager.deleteTask(task2);
            } catch (CancellationException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
