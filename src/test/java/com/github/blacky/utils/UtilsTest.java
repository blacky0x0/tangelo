package com.github.blacky.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest {

    @Test
    void testIdSequence() {
        Utils.JOB_UNIQUE_ID.set(1);
        assertEquals(1, Utils.getUniqueJobId());
        assertEquals(2, Utils.getUniqueJobId());
        assertEquals(3, Utils.getUniqueJobId());
    }

    @Test
    void testIdOverflow() {
        Utils.JOB_UNIQUE_ID.set(Long.MAX_VALUE - 1);
        assertEquals(Long.MAX_VALUE - 1, Utils.getUniqueJobId());
        assertEquals(1, Utils.getUniqueJobId());
        assertEquals(2, Utils.getUniqueJobId());
    }
}