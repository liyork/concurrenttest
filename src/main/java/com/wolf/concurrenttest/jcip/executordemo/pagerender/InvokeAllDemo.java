package com.wolf.concurrenttest.jcip.executordemo.pagerender;

import com.sun.org.apache.xalan.internal.xsltc.runtime.InternalRuntimeError;

import java.util.*;
import java.util.concurrent.*;

/**
 * Description: 演示InvokeAll，批量调用，返回一批future
 * Created on 2021/7/2 9:41 AM
 *
 * @author 李超
 * @version 0.0.1
 */
public class InvokeAllDemo {
    class QuoteTask implements Callable<TravelQuote> {
        private final TravalCompany company;
        private final TravalInfo travelInfo;

        public QuoteTask(TravalCompany company, TravalInfo travelInfo) {
            this.company = company;
            this.travelInfo = travelInfo;
        }

        public TravelQuote call() throws Exception {
            return company.solicitQuote(travelInfo);
        }

        public TravelQuote getFailureQuote(Throwable cause) {
            return null;
        }

        public TravelQuote getTimeoutQuote(CancellationException e) {
            return null;
        }
    }

    class TravelQuote {
    }

    class TravalCompany {
        public TravelQuote solicitQuote(TravalInfo travelInfo) {
            return null;
        }
    }

    class TravalInfo {
    }

    private ExecutorService exec;

    public List<TravelQuote> getRankedTravelQuotes(
            TravalInfo travelInfo, Set<TravalCompany> companies,
            Comparator<TravelQuote> ranking, long time, TimeUnit unit)
            throws InternalRuntimeError, InterruptedException {
        List<QuoteTask> tasks = new ArrayList<>();
        for (TravalCompany company : companies) {
            tasks.add(new QuoteTask(company, travelInfo));
        }

        // 顺序对应
        List<Future<TravelQuote>> futures = exec.invokeAll(tasks, time, unit);

        ArrayList<TravelQuote> quotes = new ArrayList<>(tasks.size());
        Iterator<QuoteTask> taskIter = tasks.iterator();
        for (Future<TravelQuote> future : futures) {
            QuoteTask task = taskIter.next();
            try {
                quotes.add(future.get());
            } catch (ExecutionException e) {
                quotes.add(task.getFailureQuote(e.getCause()));
            } catch (CancellationException e) {
                quotes.add(task.getTimeoutQuote(e));
            }
        }
        Collections.sort(quotes, ranking);
        return quotes;
    }
}
