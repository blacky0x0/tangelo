package com.github.blacky.model;

import java.util.concurrent.TimeUnit;

public interface ScheduledJob extends Job {
    
    long getInitialDelay();
    long getPeriod();
    TimeUnit getTimeUnit();
    
}
