package com.wolf.concurrenttest.program.dataselect.concurrent;

import com.wolf.concurrenttest.program.dataselect.CensusData;
import com.wolf.concurrenttest.program.dataselect.FilterData;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/03/07
 */
public class ConcurrentSearch {

    public static CensusData findAny(CensusData[] data, List<FilterData> filters, int size) {

        TaskManager manager = new TaskManager();
        IndividualTask task = new IndividualTask(data, 0, data.length, manager, size, filters);

        ForkJoinPool.commonPool().execute(task);
        try {
            CensusData result = task.join();
            if (result != null) {
                System.out.println("Find Any Result: " + result.getCitizenship());
                return result;
            }
        } catch (Exception e) {
            System.err.println("findAny has finished with an error: " + task.getException().getMessage());
        }

        return null;
    }

    public static List<CensusData> findAll(CensusData[] data, List<FilterData> filters, int size) {

        List<CensusData> results;
        TaskManager manager = new TaskManager();
        ListTask task = new ListTask(data, 0, data.length, manager, size, filters);
        ForkJoinPool.commonPool().execute(task);
        try {
            results = task.join();
            return results;
        } catch (Exception e) {
            System.err.println("findAll has finished with an error: " + task.getException().getMessage());
        }

        return null;
    }
}
