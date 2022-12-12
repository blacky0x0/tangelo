package com.github.blacky.model.sample;

import com.github.blacky.model.Job;
import com.github.blacky.model.JobState;
import com.github.blacky.model.JobType;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static com.github.blacky.utils.Utils.getUniqueJobId;

/**
 * An example of one-time job
 */
@Slf4j
@Getter
@Setter
@ToString
public class SimpleJob implements Job {

    private final long id = getUniqueJobId();
    private volatile JobState state = JobState.NEW;
    private final JobType type = JobType.ONE_TIME;

    @SneakyThrows
    @Override
    public void run() {
        log.trace("Job with id={} does some work...", getId());
        TimeUnit.MILLISECONDS.sleep(100);
    }
}
