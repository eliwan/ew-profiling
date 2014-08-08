/*
 * This file is part of ew-profiling, a library for in-app, runtime profiling.
 * Copyright (c) Eliwan bvba, Belgium, http://eliwan.be
 *
 * The software is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package be.eliwan.profiling.aop;

import be.eliwan.profiling.service.ProfilingContainer;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Interceptor which automatically handles the profiling for a method.
 */
public class MethodProfilingInterceptor implements MethodInterceptor {

    private ProfilingContainer profilingContainer;
    private String group;

    /**
     * Set the {@link ProfilingContainer} to use for the profiling.
     *
     * @param profilingContainer profiling container
     * @since 1.0.0
     */
    public void setProfilingContainer(ProfilingContainer profilingContainer) {
        this.profilingContainer = profilingContainer;
    }

    /**
     * Set the group to use when adding a registration for profiling.
     *
     * @param group group name
     * @since 1.0.0
     */
    public void setGroup(String group) {
        this.group = group;
    }

    // CHECKSTYLE THROWS_THROWABLE: OFF
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = invocation.proceed();
        profilingContainer.register(group, System.currentTimeMillis() - start);
        return result;
    }
    // CHECKSTYLE THROWS_THROWABLE: ON

}
