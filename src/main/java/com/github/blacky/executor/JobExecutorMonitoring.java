package com.github.blacky.executor;

import com.github.blacky.management.JobSystemStatistics;

import java.util.concurrent.TimeUnit;

public interface JobExecutorMonitoring {

    void enableMonitoring(int delay, TimeUnit unit);

    void enableMonitoring();

    void disableMonitoring();

    JobSystemStatistics getStatistics();

}
