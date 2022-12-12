package com.github.blacky.executor;

import com.github.blacky.model.Job;
import com.github.blacky.model.ScheduledJob;

public interface JobExecutorService {

    /**
     * Submit a one-time job and execute it immediately.
     */
    boolean execute(Job job);

    /**
     * Submit a job for periodic execution for a custom period of time.
     */
    boolean schedule(ScheduledJob job);

    /**
     * Stop a job.
     */
    boolean cancel(Job job);

    /**
     * Invoke shutdown of internals
     */
    void shutdown();

}
