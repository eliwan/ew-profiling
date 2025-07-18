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
    private String query;

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

    /**
     * Constructor.
     *
     * @param groupPrefix group prefix
     * @param delegate the "real" prepared statement which is profiled.
     * @param query query
     */
    public ProfilingInvocationHandler(String groupPrefix, Object delegate, String query) {
        this.groupPrefix = groupPrefix;
        this.delegate = delegate;
        this.query = query;
    }

    @Override
    // CHECKSTYLE THROWS_THROWABLE: OFF
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isGetterOrSetter(method)) {
            return method.invoke(delegate, args);
        } else {
            String profilingQuery = extractQuery(method, args);
            long start = System.currentTimeMillis();
            try {
                return method.invoke(delegate, args);
            } finally {
                long duration = System.currentTimeMillis() - start;
                ProfilingDriver.register(groupPrefix + method.getName(), duration);
                if (null != profilingQuery) {
                    ProfilingDriver.registerQuery(groupPrefix + method.getName(), profilingQuery, duration);
                }
            }
        }
    }
    // CHECKSTYLE THROWS_THROWABLE: ON

    private String extractQuery(Method method, Object[] args) {
        String res = null;
        String methodName = method.getName();
        if (("execute".equals(methodName) || "executeQuery".equals(methodName) || "executeUpdate".equals(methodName) || "executeBatch".equals(methodName))) {
            res = query;
            if (null != args && 1 == args.length && args[0] instanceof String) {
                res = (String) args[0];
            }
        }
        return res;
    }

    private boolean isGetterOrSetter(Method method) {
        try {
            String methodName = method.getName();
            return (methodName.startsWith("get") && !("getResultSet".equals(methodName) || "getMoreResults".equals(methodName)))
                    || methodName.startsWith("is") || methodName.startsWith("set");
        } catch (Exception ex) {
            return false; // just in case, better be safe than sorry
        }
    }

}
