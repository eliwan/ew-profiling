/*
 * This file is part of ew-profiling, a library for in-app, runtime profiling.
 * Copyright (c) Eliwan bvba, Belgium, http://eliwan.be
 *
 * The software is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package be.eliwan.profiling.service;

import com.lmax.disruptor.EventFactory;

/**
 * Value as used by the disruptor for future aggregation.
 */
public class Registration {

    /** Object factory to pre-fill the ring buffer. */
    public static final EventFactory<Registration> FACTORY = new EventFactory<Registration>() {
        public Registration newInstance() {
            return new Registration();
        }
    };

    private String group;
     private long duration;

    /**
     * Get group for registration.
     *
     * @return group name
     */
    public String getGroup() {
        return group;
    }

    /**
     * Set group for registration.
     *
     * @param group group name
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Get duration for registration.
     *
     * @return duration duration
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Set duration for registration.
     *
     * @param duration duration
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }
}
