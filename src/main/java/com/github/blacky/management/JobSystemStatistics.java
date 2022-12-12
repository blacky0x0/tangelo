package com.github.blacky.management;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class JobSystemStatistics {
    private final int poolSize;
    private final int corePoolSize;
    private final long jobsCount;
    private final long oneTimeJobsCount;
    private final long periodicalJobsCount;
    private final long taskCount;
    private final int activeCount;
    private final int queueSize;
    private final long jobsScheduledCount;
    private final long jobsCanceledCount;
    private final long jobsSucceedCount;
    private final long jobsFailedCount;
    private final long jobsRejectedCount;
    private final boolean isShutdown;
    private final boolean isTerminated;
}
