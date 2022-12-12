package com.github.blacky.executor;

import com.github.blacky.model.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class JobSystemTest {

    @Test
    void shouldChangeOneTimeJobStates() throws InterruptedException {
        Job job = new AbstractJob(JobType.ONE_TIME) {
            @SneakyThrows
            @Override
            public void run() {
                TimeUnit.MILLISECONDS.sleep(1200);
            }
        };
        assertEquals(job.getState(), JobState.NEW);

        JobSystem system = new JobSystem(1);
        assertTrue(system.execute(job));
        TimeUnit.MILLISECONDS.sleep(400);
        assertEquals(job.getState(), JobState.RUNNING);
        TimeUnit.MILLISECONDS.sleep(1200);
        assertEquals(job.getState(), JobState.SUCCEED);
    }

    @Test
    void shouldCancelOneTimeJob() {
        Job job1 = new AbstractJob(JobType.ONE_TIME) {
            @SneakyThrows
            @Override
            public void run() {
                TimeUnit.MILLISECONDS.sleep(1200);
            }
        };        
        Job job2 = new AbstractJob(JobType.ONE_TIME) {
            @Override
            public void run() {}
        };
        assertEquals(job2.getState(), JobState.NEW);

        JobSystem system = new JobSystem(1);
        assertTrue(system.execute(job1));
        assertTrue(system.execute(job2));
        assertTrue(system.cancel(job2));
        assertEquals(job2.getState(), JobState.CANCELED);
    }

    @Test
    void shouldFailOneTimeJob() throws InterruptedException {
        Job job = new AbstractJob(JobType.ONE_TIME) {
            @SneakyThrows
            @Override
            public void run() {
                int a = 0 / 0;
            }
        };        
        assertEquals(job.getState(), JobState.NEW);

        JobSystem system = new JobSystem(1);
        assertTrue(system.execute(job));
        TimeUnit.MILLISECONDS.sleep(400);
        assertEquals(job.getState(), JobState.FAILED);
    }


    @Test
    void shouldRejectOneTimeJob() {
        Job job = new AbstractJob(JobType.ONE_TIME) {
            @Override
            public void run() {}
        };
        assertEquals(job.getState(), JobState.NEW);

        JobSystem system = new JobSystem(1);
        system.shutdown();
        assertFalse(system.execute(job));
        assertEquals(job.getState(), JobState.REJECTED);
    }


    @Test
    void shouldChangePeriodicJobStates() throws InterruptedException {
        ScheduledJob job = new AbstractPeriodicJob(0, 400, TimeUnit.MILLISECONDS) {
            @SneakyThrows
            @Override
            public void run() {
                TimeUnit.MILLISECONDS.sleep(1200);
            }
        };
        assertEquals(job.getState(), JobState.NEW);

        JobSystem system = new JobSystem(1);
        assertTrue(system.schedule(job));
        TimeUnit.MILLISECONDS.sleep(400);
        assertEquals(job.getState(), JobState.RUNNING);
        assertEquals(1, system.getStatistics().getTaskCount());
        TimeUnit.MILLISECONDS.sleep(1200);
        assertEquals(job.getState(), JobState.RUNNING);
        assertEquals(2, system.getStatistics().getTaskCount());
    }

    @Test
    void shouldCancelPeriodicJob() {
        ScheduledJob job1 = new AbstractPeriodicJob(0, 400, TimeUnit.MILLISECONDS) {
            @SneakyThrows
            @Override
            public void run() {
                TimeUnit.MILLISECONDS.sleep(1200);
            }
        };
        ScheduledJob job2 = new AbstractPeriodicJob(0, 400, TimeUnit.MILLISECONDS) {
            @Override
            public void run() {}
        };
        assertEquals(job2.getState(), JobState.NEW);

        JobSystem system = new JobSystem(1);
        assertTrue(system.execute(job1));
        assertTrue(system.execute(job2));
        assertTrue(system.cancel(job2));
        assertEquals(job2.getState(), JobState.CANCELED);
    }

    @Test
    void shouldFailPeriodicJob() throws InterruptedException {
        ScheduledJob job = new AbstractPeriodicJob(0, 400, TimeUnit.MILLISECONDS) {
            @SneakyThrows
            @Override
            public void run() {
                int a = 0 / 0;
            }
        };
        assertEquals(job.getState(), JobState.NEW);

        JobSystem system = new JobSystem(1);
        assertTrue(system.execute(job));
        TimeUnit.MILLISECONDS.sleep(400);
        assertEquals(job.getState(), JobState.FAILED);
    }

    @Test
    void shouldRejectPeriodicJob() {
        ScheduledJob job = new AbstractPeriodicJob(0, 400, TimeUnit.MILLISECONDS) {
            @Override
            public void run() {}
        };
        assertEquals(job.getState(), JobState.NEW);

        JobSystem system = new JobSystem(1);
        system.shutdown();
        assertFalse(system.execute(job));
        assertEquals(job.getState(), JobState.REJECTED);
    }
}