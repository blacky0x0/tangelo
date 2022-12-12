package com.github.blacky.model;

import lombok.Getter;
import lombok.ToString;

import static com.github.blacky.utils.Utils.getUniqueJobId;

@Getter
@ToString
public abstract class AbstractJob implements Job {

    protected final long id = getUniqueJobId();
    protected volatile JobState state = JobState.NEW;
    protected final JobType type;

    protected AbstractJob(JobType type) {
        this.type = type;
    }

    public void setState(JobState state) {
        this.state = state;
    }

}
