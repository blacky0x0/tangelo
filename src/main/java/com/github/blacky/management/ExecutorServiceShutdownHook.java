package com.github.blacky.management;

import com.github.blacky.executor.JobExecutorMonitoring;
import com.github.blacky.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class ExecutorServiceShutdownHook extends Thread {

    private final ThreadPoolExecutor executor;
    private final JobExecutorMonitoring resource;
    private static final AtomicLong idGenerator = new AtomicLong(1);

    public ExecutorServiceShutdownHook(final ThreadPoolExecutor executor, 
                                       final JobExecutorMonitoring resource) {
        this.executor = executor;
        this.resource = resource;
        setName("shutdown-hook-" + idGenerator.getAndIncrement());
    }

    public void run() {
        log.info("ExecutorService is shutting down...");
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
        while (true) {
            try {
                log.info("Waiting for the service to terminate...");
                if (executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    break;
                }
                break;
            } catch (InterruptedException e) {
                log.error("{}", e.getMessage(), e);
                break;
            }
        }
        Utils.printStats(log, resource.getStatistics());
        log.info("ExecutorService stopped");
    }
}
