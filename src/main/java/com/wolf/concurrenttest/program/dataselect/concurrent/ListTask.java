package com.wolf.concurrenttest.program.dataselect.concurrent;

import com.wolf.concurrenttest.program.dataselect.CensusData;
import com.wolf.concurrenttest.program.dataselect.Filter;
import com.wolf.concurrenttest.program.dataselect.FilterData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.RecursiveTask;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/03/07
 */
public class ListTask extends RecursiveTask<List<CensusData>> {

    private CensusData[] data;
    private int start, end, size;
    private TaskManager manager;
    private List<FilterData> filters;

    public ListTask(CensusData[] data, int start, int end, TaskManager manager, int size, List<FilterData> filters) {
        this.data = data;
        this.start = start;
        this.end = end;
        this.size = size;
        this.manager = manager;
        this.filters = filters;
    }

    @Override
    protected List<CensusData> compute() {

        ArrayList<CensusData> ret = new ArrayList<>();
        List<CensusData> tmp;

        if (end - start <= size) {
            for (int i = start; i < end; i++) {
                CensusData censusData = data[i];
                if (Filter.filter(censusData, filters)) {
                    ret.add(censusData);
                }
            }
        } else {
            int mid = (start + end) / 2;
            ListTask task1 = new ListTask(data, start, mid, manager, size, filters);
            ListTask task2 = new ListTask(data, mid, end, manager, size, filters);

            manager.addTask(task1);
            manager.addTask(task2);
            manager.deleteTask(this);

            task1.fork();
            task2.fork();
            task1.quietlyJoin();
            task2.quietlyJoin();

            manager.deleteTask(task1);
            manager.deleteTask(task2);

            try {
                tmp = task1.join();
                if (null != tmp) {
                    ret.addAll(tmp);
                }
                manager.deleteTask(task1);
            } catch (CancellationException e) {
                e.printStackTrace();
            }

            try {
                tmp = task2.join();
                if (null != tmp) {
                    ret.addAll(tmp);
                }
                manager.deleteTask(task2);
            } catch (CancellationException e) {
                e.printStackTrace();
            }
        }


        return null;
    }
}
