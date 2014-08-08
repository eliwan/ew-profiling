/*
 * This file is part of ew-profiling, a library for in-app, runtime profiling.
 * Copyright (c) Eliwan bvba, Belgium, http://eliwan.be
 *
 * The software is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package be.eliwan.profiling.api;

/**
 * Allow registration of profiling data.
 */
public interface ProfilingSink {

    /**
     * Add a registration for a group.
     *
     * @param group group name
     * @param duration duration
     */
    void register(String group, long duration);

}
