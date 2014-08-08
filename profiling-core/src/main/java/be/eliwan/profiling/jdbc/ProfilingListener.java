/*
 * This file is part of ew-profiling, a library for in-app, runtime profiling.
 * Copyright (c) Eliwan bvba, Belgium, http://eliwan.be
 *
 * The software is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package be.eliwan.profiling.jdbc;

/**
 * Implement this class and register in {@link ProfilingDriver} to register your profiling information.
 */
public interface ProfilingListener {

    /**
     * Register a duration in milliseconds for running a JDBC method.
     *
     * @param group indication of type of command.
     * @param durationMillis duration in milliseconds
     */
    void register(String group, long durationMillis);

}
