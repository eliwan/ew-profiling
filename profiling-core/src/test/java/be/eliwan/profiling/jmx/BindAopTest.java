/*
 * This file is part of ew-profiling, a library for in-app, runtime profiling.
 * Copyright (c) Eliwan bvba, Belgium, http://eliwan.be
 *
 * The software is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package be.eliwan.profiling.jmx;

import be.eliwan.profiling.service.OneContainer;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Test to verify that the AOP binding works (building on from BindJMXTest).
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/jmxAopContext.xml" })
public class BindAopTest {

    @Autowired
    private ExampleService service;

    @Autowired
    private MBeanServer mBeanServer;

    @Test
    public void testAopJmx() throws Exception {
        ObjectName mbean = new ObjectName("bean:name=profilingAopTest");

        mBeanServer.invoke(mbean, "clear", new Object[]{}, new String[]{});

        service.doSomething(1);
        service.doSomething(2);
        service.doSomething(3);

        System.out.println("" + mBeanServer.getAttribute(mbean, "Total"));
        Assert.assertEquals(3, ((OneContainer) mBeanServer.getAttribute(mbean, "Total")).getInvocationCount());
        System.out.println("" + mBeanServer.getAttribute(mbean, "GroupData"));

        //Thread.sleep(1000000000); // use this to test whether you can connect using JConsole
    }

}
