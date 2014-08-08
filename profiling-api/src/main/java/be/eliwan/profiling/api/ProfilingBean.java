/*
 * This file is part of ew-profiling, a library for in-app, runtime profiling.
 * Copyright (c) Eliwan bvba, Belgium, http://eliwan.be
 *
 * The software is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package be.eliwan.profiling.api;

import java.util.List;

/**
 * Interface which should be published using JMX to allow getting the profiling info.
 */
public interface ProfilingBean {

    /**
     * Clear data to remove old totals.
     */
    void clear();

    /**
     * Get totals across all the groups.
     *
     * @return profiling data for all groups
     */
    ProfilingData getTotal();

    /**
     * Get data for the groups. The groups are sorted alphabetically.
     *
     * @return sorted set of data for the individual groups
     */
    List<GroupData> getGroupData();
}
