package com.github.blacky.management;

import com.github.blacky.executor.JobExecutorMonitoring;
import com.github.blacky.utils.Utils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Slf4j
public class MonitoringThread extends Thread {
    private final JobExecutorMonitoring resource;
    private volatile int delay;
    private volatile TimeUnit unit;
    private volatile boolean run = true;
    private volatile boolean enabled = false;
    private final Semaphore mutex;
    private static final AtomicLong idGenerator = new AtomicLong(1);

    private static final int DEFAULT_DELAY = 1;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    public MonitoringThread(final JobExecutorMonitoring resource) {
        this.resource = resource;
        this.delay = DEFAULT_DELAY;
        this.unit = DEFAULT_TIME_UNIT;
        this.mutex = new Semaphore(0);
        setName("monitoring-" + idGenerator.getAndIncrement());
    }

    @Override
    public void run() {
        while (run) {
            try {
                mutex.acquire();
                if (enabled) {
                    Utils.printStats(log, resource.getStatistics());
                    Thread.sleep(unit.toMillis(delay));
                    mutex.release();
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
        log.info("MonitoringThread exited");
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public void setEnabled() {
        enabled = true;
        mutex.drainPermits();
        if (mutex.availablePermits() == 0) mutex.release(1);
    }

    public void setDisabled() {
        enabled = false;
        mutex.drainPermits();
    }

    public void shutdown() {
        this.run = false;
        this.enabled = false;
        this.mutex.drainPermits();
        if (mutex.availablePermits() == 0) this.mutex.release(1);
    }
}