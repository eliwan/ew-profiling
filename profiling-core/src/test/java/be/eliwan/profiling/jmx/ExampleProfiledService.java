/*
 * This file is part of ew-profiling, a library for in-app, runtime profiling.
 * Copyright (c) Eliwan bvba, Belgium, http://eliwan.be
 *
 * The software is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package be.eliwan.profiling.jmx;

import be.eliwan.profiling.service.ProfilingContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Example service which is bound for the BindJmxTest.
 */
@Component
public class ExampleProfiledService {

    @Autowired
    private ExampleService exampleService;

    @Autowired
    private ProfilingContainer profiling;

    public void doSomething(int count) {
        long start = System.currentTimeMillis();
        exampleService.doSomething(count);
        profiling.register("x", System.currentTimeMillis() - start);
    }

}
