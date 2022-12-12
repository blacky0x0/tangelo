package com.github.blacky;

import com.github.blacky.executor.JobSystem;
import com.github.blacky.model.ScheduledJob;
import com.github.blacky.model.sample.SimpleJob;
import com.github.blacky.model.sample.SimpleScheduledJob;

import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String[] args) throws InterruptedException {
        /* Code Sample Fragment */
        SimpleJob job1 = new SimpleJob();
        ScheduledJob job2 = SimpleScheduledJob.builder().period(2).timeUnit(TimeUnit.SECONDS).build();
        JobSystem jobSystem = JobSystem.newFixedThreadExecutor(1);
        jobSystem.enableMonitoring(1, TimeUnit.SECONDS);
        jobSystem.execute(job1);
        jobSystem.schedule(job2);
        jobSystem.shutdown();
        TimeUnit.SECONDS.sleep(4);
    }
}
