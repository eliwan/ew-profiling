/*
 * This file is part of ew-profiling, a library for in-app, runtime profiling.
 * Copyright (c) Eliwan bvba, Belgium, http://eliwan.be
 *
 * The software is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package be.eliwan.profiling.service;

import be.eliwan.profiling.api.GroupData;

/**
 * Set of profiling data for a specific group.
 */
public class GroupContainer extends OneContainer implements GroupData {

    private static final long serialVersionUID = 100;

    private final String group;

    /**
     * Construct container for given group, number of invocations and total run time.
     *
     * @param group group name
     * @param invocationCount invocation count
     * @param totalRunTime total run time
     */
    public GroupContainer(String group, long invocationCount, long totalRunTime) {
        super(invocationCount, totalRunTime);
        this.group = group;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return "GroupContainer{" +
                "group='" + group + "\', " + super.toString() +
                '}';
    }
}
