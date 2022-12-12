package com.github.blacky.executor;

import com.github.blacky.management.ExecutorServiceShutdownHook;
import com.github.blacky.management.JobSystemStatistics;
import com.github.blacky.management.MonitoringThread;
import com.github.blacky.model.Job;
import com.github.blacky.model.JobState;
import com.github.blacky.model.JobType;
import com.github.blacky.model.ScheduledJob;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class JobSystem implements JobExecutorService, JobExecutorMonitoring {

    private static final AtomicLong idGenerator = new AtomicLong(1);
    private static final int DEFAULT_CONCURRENT_JOB_LIMIT = 1;
    private static final Object dummy = new Object();
    private static final Future<?> dummyFuture = CompletableFuture.completedFuture(dummy);

    private final ScheduledThreadPoolExecutor executor;
    private final MonitoringThread monitoringThread;
    private final ConcurrentHashMap<Job, Future<?>> map = new ConcurrentHashMap<>();

    public JobSystem() {
        this(DEFAULT_CONCURRENT_JOB_LIMIT);
    }

    public JobSystem(int concurrentJobLimit) {
        ThreadFactory factory = new ThreadFactoryBuilder()
                .setNameFormat("job-system-" + idGenerator.getAndIncrement() + "-%d")
                .setDaemon(false)
                .setPriority(Thread.MAX_PRIORITY)
                .build();
        executor = new ScheduledThreadPoolExecutor(concurrentJobLimit, factory);
        monitoringThread = new MonitoringThread(this);
        monitoringThread.setDaemon(true);
        monitoringThread.start();
        Runtime.getRuntime().addShutdownHook(new ExecutorServiceShutdownHook(executor, this));
    }

    public static JobSystem newSingleThreadExecutor() {
        return new JobSystem(1);
    }

    public static JobSystem newFixedThreadExecutor(int concurrentJobLimit) {
        return new JobSystem(concurrentJobLimit);
    }

    @Override
    public boolean execute(Job job) {
        log.info("One-time job with id={} received", job.getId());
        try {
            Future<?> future = executor.submit(decorate(job));
            job.setState(JobState.SUBMITTED);
            map.put(job, future);
            log.trace("One-time job with id={} submitted", job.getId());
        } catch (RejectedExecutionException | NullPointerException e) {
            job.setState(JobState.REJECTED);
            map.put(job, dummyFuture);
            log.error("Job with id={} REJECTED", job.getId());
            log.error("Cause: {}", e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean schedule(ScheduledJob job) {
        log.info("Periodic job with id={} received", job.getId());
        try {
            Future<?> future = executor.scheduleAtFixedRate(decorate(job), job.getInitialDelay(), job.getPeriod(), job.getTimeUnit());
            job.setState(JobState.SUBMITTED);
            map.put(job, future);
            log.trace("Periodic job with id={} submitted", job.getId());
        } catch (RejectedExecutionException | NullPointerException e) {
            job.setState(JobState.REJECTED);
            map.put(job, dummyFuture);
            log.error("Job with id={} REJECTED", job.getId());
            log.error("Cause: {}", e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean cancel(Job job) {
        Future<?> future = map.get(job);
        if (future != null) {
            if (future.cancel(true)) {
                job.setState(JobState.CANCELED);
                log.info("Job with id={} CANCELED", job.getId());
                return true;
            }
        }
        return false;
    }

    @Override
    public void shutdown() {
        executor.shutdown();
        monitoringThread.shutdown();
    }

    @Override
    public void enableMonitoring(int delay, TimeUnit unit) {
        monitoringThread.setDelay(delay);
        monitoringThread.setUnit(unit);
        monitoringThread.setEnabled();
    }

    @Override
    public void enableMonitoring() {
        monitoringThread.setEnabled();
    }

    @Override
    public void disableMonitoring() {
        monitoringThread.setDisabled();
    }

    @Override
    public JobSystemStatistics getStatistics() {
        return JobSystemStatistics.builder()
                .poolSize(executor.getPoolSize())
                .corePoolSize(executor.getCorePoolSize())
                .jobsCount(map.size())
                .oneTimeJobsCount(map.keySet().stream().filter(k -> k.getType() == JobType.ONE_TIME).count())
                .periodicalJobsCount(map.keySet().stream().filter(k -> k.getType() == JobType.PERIODIC).count())
                .taskCount(executor.getTaskCount())
                .activeCount(executor.getActiveCount())
                .queueSize(executor.getQueue().size())
                .jobsScheduledCount(map.keySet().stream().filter(k -> k.getState() == JobState.SCHEDULED).count())
                .jobsCanceledCount(map.keySet().stream().filter(k -> k.getState() == JobState.CANCELED).count())
                .jobsSucceedCount(map.keySet().stream().filter(k -> k.getState() == JobState.SUCCEED).count())
                .jobsFailedCount(map.keySet().stream().filter(k -> k.getState() == JobState.FAILED).count())
                .jobsRejectedCount(map.keySet().stream().filter(k -> k.getState() == JobState.REJECTED).count())
                .isShutdown(executor.isShutdown())
                .isTerminated(executor.isTerminated())
                .build();
    }

    protected Runnable decorate(Job job) {
        return () -> {
            try {
                if (job.getState() == JobState.FAILED) return;
                job.setState(JobState.RUNNING);
                log.trace("Job with id={} RUNNING", job.getId());

                job.run();

                switch (job.getType()) {
                    case ONE_TIME:
                        job.setState(JobState.SUCCEED);
                        log.info("Job with id={} SUCCEED", job.getId());
                        break;
                    case PERIODIC:
                        job.setState(JobState.SUCCEED);
                        log.info("Job with id={} SUCCEED", job.getId());
                        job.setState(JobState.SCHEDULED);
                        log.trace("Job with id={} SCHEDULED", job.getId());
                }
            } catch (Exception e) {
                job.setState(JobState.FAILED);
                log.error("Job with id={} FAILED", job.getId());
                log.error("Cause: {}", e.getMessage(), e);
            }
        };
    }

}
