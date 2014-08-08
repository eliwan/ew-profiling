/*
 * This file is part of ew-profiling, a library for in-app, runtime profiling.
 * Copyright (c) Eliwan bvba, Belgium, http://eliwan.be
 *
 * The software is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package be.eliwan.profiling.service;

import be.eliwan.profiling.api.ProfilingData;

import java.io.Serializable;

/**
 * Set of profiling data.
 */
public class OneContainer implements ProfilingData, Serializable {

    private static final long serialVersionUID = 100;

    private final long invocationCount;
    private final long totalRunTime;

    /**
     * Construct container for given number of invocations and total run time.
     *
     * @param invocationCount invocation count
     * @param totalRunTime total run time
     */
    public OneContainer(long invocationCount, long totalRunTime) {
        this.invocationCount = invocationCount;
        this.totalRunTime = totalRunTime;
    }

    @Override
    public long getInvocationCount() {
        return invocationCount;
    }

    @Override
    public long getTotalRunTime() {
        return totalRunTime;
    }

    @Override
    public double getAverageRunTime() {
        if (invocationCount > 0) {
            return ((double) totalRunTime) / invocationCount;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "OneContainer{" +
                "invocationCount=" + invocationCount +
                ", totalRunTime=" + totalRunTime +
                '}';
    }
}
