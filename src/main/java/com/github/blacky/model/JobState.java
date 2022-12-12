package com.github.blacky.model;

public enum JobState {
    /**
     * Initial state of any job after creation  
     */
    NEW,

    /**
     * Job was submitted to executor successfully.
     */
    SUBMITTED,

    /**
     * Job was scheduled for the next execution
     */
    SCHEDULED,

    /**
     * Job is executed or in pending state
     */
    RUNNING,
    
    /**
     * Final state: canceled.
     * Job was stopped externally.
     */
    CANCELED,
        
    /**
     * Final state: succeed.
     * Job was completed without exception.
     */
    SUCCEED,
    
    /**
     * Final state: failed.
     * Job was completed with exception.
     */
    FAILED,
    
    /**
     * Final state: rejected.
     * Job was rejected without execution.
     */
    REJECTED,

    /**
     * Job is in an unknown state.
     * Should never happen.
     */
    UNKNOWN
}
