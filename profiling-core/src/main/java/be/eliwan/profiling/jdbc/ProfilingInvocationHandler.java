/*
 * This file is part of ew-profiling, a library for in-app, runtime profiling.
 * Copyright (c) Eliwan bvba, Belgium, http://eliwan.be
 *
 * The software is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package be.eliwan.profiling.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Wrapping JDBC object which can be used to profile the time spent communicating with the database.
 */
public class ProfilingInvocationHandler implements InvocationHandler {

    private String groupPrefix;
    private Object delegate;

    /**
     * Constructor.
     *
     * @param groupPrefix group prefix
     * @param delegate the "real" prepared statement which is profiled.
     */
    public ProfilingInvocationHandler(String groupPrefix, Object delegate) {
        this.groupPrefix = groupPrefix;
        this.delegate = delegate;
    }

    @Override
    // CHECKSTYLE THROWS_THROWABLE: OFF
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return method.invoke(delegate, args);
        } finally {
            ProfilingDriver.register(groupPrefix + method.getName(), System.currentTimeMillis() - start);
        }
    }
    // CHECKSTYLE THROWS_THROWABLE: ON
}
