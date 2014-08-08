/*
 * This file is part of ew-profiling, a library for in-app, runtime profiling.
 * Copyright (c) Eliwan bvba, Belgium, http://eliwan.be
 *
 * The software is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package be.eliwan.profiling.api;

/**
 * Profiling information.
 */
public interface ProfilingData {

    /**
     * Get number of invocations.
     *
     * @return number of invocations
     */
    long getInvocationCount();

    /**
     * Get total time spent for the invocations.
     *
     * @return total run time
     */
    long getTotalRunTime();

    /**
     * Get average run time per invocation.
     *
     * @return average run time
     */
    double getAverageRunTime();

}
