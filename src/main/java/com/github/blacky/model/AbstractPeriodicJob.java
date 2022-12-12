package com.github.blacky.model;

import lombok.Getter;
import lombok.ToString;

import java.util.concurrent.TimeUnit;

@Getter
@ToString
public abstract class AbstractPeriodicJob extends AbstractJob implements ScheduledJob {

    private final long initialDelay;
    private final long period;
    private final TimeUnit timeUnit;

    protected AbstractPeriodicJob(long initialDelay, long period, TimeUnit timeUnit) {
        super(JobType.PERIODIC);
        this.initialDelay = initialDelay;
        this.period = period;
        this.timeUnit = timeUnit;
    }
}
