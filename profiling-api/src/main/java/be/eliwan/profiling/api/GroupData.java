/*
 * This file is part of ew-profiling, a library for in-app, runtime profiling.
 * Copyright (c) Eliwan bvba, Belgium, http://eliwan.be
 *
 * The software is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package be.eliwan.profiling.api;

/**
 * Profiling information for a specific group.
 */
public interface GroupData extends ProfilingData {

    /**
     * Get group for this set of profiling data.
     *
     * @return group name
     */
    String getGroup();

}
