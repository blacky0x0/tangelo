package com.github.blacky.model;

public interface Job extends Runnable {

    long getId();
    JobState getState();
    
    /**
     * Primarily, the state should be set and maintained by {@link com.github.blacky.executor.JobSystem}
     */
    void setState(JobState jobState);
    JobType getType();
    
}
