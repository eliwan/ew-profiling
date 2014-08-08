/*
 * This file is part of ew-profiling, a library for in-app, runtime profiling.
 * Copyright (c) Eliwan bvba, Belgium, http://eliwan.be
 *
 * The software is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package be.eliwan.profiling.jmx;

import org.springframework.stereotype.Component;

/**
 * Example service which is bound for the BindJmxTest.
 */
@Component
public class ExampleService {

    public void doSomething(int count) {
        try {
            Thread.sleep(count * 100);
        } catch (InterruptedException ie) {
            // ignore, nothing to do
        }
    }

}
