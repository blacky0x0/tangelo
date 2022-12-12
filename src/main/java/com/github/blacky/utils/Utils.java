package com.github.blacky.utils;

import com.github.blacky.management.JobSystemStatistics;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicLong;

public final class Utils {

    static final AtomicLong JOB_UNIQUE_ID = new AtomicLong(1);
    static final String STATS_TEMPLATE = "Pool: [{}/{}] Jobs: [{}={}+{}], Tasks: {}, Running: {}, Queue: {}, " +
            "Scheduled: {}, Canceled: {}, Succeed: {}, Failed: {}, Rejected: {}, " +
            "isShutdown: {}, isTerminated: {}";

    public static long getUniqueJobId() {
        if (JOB_UNIQUE_ID.get() == Long.MAX_VALUE) JOB_UNIQUE_ID.set(1);
        return JOB_UNIQUE_ID.getAndIncrement();
    }

    public static void printStats(final Logger log, final JobSystemStatistics stats) {
        log.info(STATS_TEMPLATE,
                stats.getPoolSize(),
                stats.getCorePoolSize(),
                stats.getJobsCount(),
                stats.getOneTimeJobsCount(),
                stats.getPeriodicalJobsCount(),
                stats.getTaskCount(),
                stats.getActiveCount(),
                stats.getQueueSize(),
                stats.getJobsScheduledCount(),
                stats.getJobsCanceledCount(),
                stats.getJobsSucceedCount(),
                stats.getJobsFailedCount(),
                stats.getJobsRejectedCount(),
                stats.isShutdown(),
                stats.isTerminated()
        );
    }
}
