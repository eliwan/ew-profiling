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
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Wrapping JDBC connection which can be used to profile the time spent communicating with the database.
 */
public class ConnectionInvocationHandler implements InvocationHandler {

    private Connection delegate;

    /**
     * Constructor.
     *
     * @param delegate the "real" connection which is profiled.
     */
    public ConnectionInvocationHandler(Connection delegate) {
        this.delegate = delegate;
    }

    @Override
    // CHECKSTYLE THROWS_THROWABLE: OFF
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isGetterOrSetter(method)) {
            return method.invoke(delegate, args);
        } else {
            long start = System.currentTimeMillis();
            try {
                if ("prepareCall".equals(method.getName())) {
                    CallableStatement callableStatement = (CallableStatement) method.invoke(delegate, args);
                    return Proxy.newProxyInstance(callableStatement.getClass().getClassLoader(),
                            new Class[]{PreparedStatement.class},
                            new ProfilingInvocationHandler("CallableStatement.", callableStatement));
                } else if ("prepareStatement".equals(method.getName())) {
                    PreparedStatement preparedStatement = (PreparedStatement) method.invoke(delegate, args);
                    return Proxy.newProxyInstance(preparedStatement.getClass().getClassLoader(),
                            new Class[]{PreparedStatement.class},
                            new ProfilingInvocationHandler("PreparedStatement.", preparedStatement));
                } else if ("createStatement".equals(method.getName())) {
                    Statement statement = (Statement) method.invoke(delegate, args);
                    return Proxy.newProxyInstance(statement.getClass().getClassLoader(),
                            new Class[]{PreparedStatement.class},
                            new ProfilingInvocationHandler("Statement.", statement));
                }
                return method.invoke(delegate, args);
            } finally {
                ProfilingDriver.register("Connection." + method.getName(), System.currentTimeMillis() - start);
            }
        }
    }
    // CHECKSTYLE THROWS_THROWABLE: ON

    private boolean isGetterOrSetter(Method method) {
        try {
            String methodName = method.getName();
            return methodName.startsWith("get") || methodName.startsWith("is") || methodName.startsWith("set") || "clearWarnings".equals(methodName);
        } catch (Exception ex) {
            return false; // just in case, better be safe than sorry
        }
    }

}
