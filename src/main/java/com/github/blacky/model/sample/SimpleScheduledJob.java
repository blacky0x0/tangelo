package com.github.blacky.model.sample;

import com.github.blacky.model.JobState;
import com.github.blacky.model.JobType;
import com.github.blacky.model.ScheduledJob;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static com.github.blacky.utils.Utils.getUniqueJobId;

/**
 * An example of scheduled job 
 */
@Builder
@Slf4j
@Getter
@Setter
@ToString
public class SimpleScheduledJob implements ScheduledJob {
    
    private final long id = getUniqueJobId();
    @Builder.Default
    private volatile JobState state = JobState.NEW;
    private final JobType type = JobType.PERIODIC;
    private final long initialDelay;
    @Builder.Default
    private final long period = 1;
    @Builder.Default
    private final TimeUnit timeUnit = TimeUnit.SECONDS;

    @SneakyThrows
    @Override
    public void run() {
        log.trace("Job with id={} does some work...", getId());
        TimeUnit.MILLISECONDS.sleep(100);
    }
}
