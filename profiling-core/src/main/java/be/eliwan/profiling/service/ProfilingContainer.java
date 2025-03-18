/*
 * This file is part of ew-profiling, a library for in-app, runtime profiling.
 * Copyright (c) Eliwan bvba, Belgium, http://eliwan.be
 *
 * The software is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package be.eliwan.profiling.service;

import be.eliwan.profiling.api.GroupData;
import be.eliwan.profiling.api.ProfilingBean;
import be.eliwan.profiling.api.ProfilingData;
import be.eliwan.profiling.api.ProfilingSink;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Bean for building and reading profiling data.
 */
public class ProfilingContainer implements ProfilingBean, ProfilingSink {

    private static final OneContainer CLEAR = new OneContainer(0, 0);
    private static final GroupDataComparator GROUP_DATA_COMPARATOR = new GroupDataComparator();

    private int ringSize = 128; // must be a power of two
    private ThreadFactory threadFactory;
    private Disruptor<Registration> disruptor;
    private RingBuffer<Registration> ringBuffer;

    private final Map<String, OneContainerContainer> groupData = new ConcurrentHashMap<>();
    private OneContainer total = CLEAR;

    /**
     * Set ring size. Should be done before starting the ProfilingContainer. Otherwise the default value may be used.
     *
     * @param ringSize ring size
     */
    public void setRingSize(int ringSize) {
        this.ringSize = ringSize;
    }

    /**
     * Set up the disruptor service to have a single consumer which aggregates the data.
     */
    @PostConstruct
    public void start() {
        threadFactory = Executors.defaultThreadFactory();
        disruptor = new Disruptor<>(Registration.FACTORY,
                ringSize,
                threadFactory,
                ProducerType.MULTI,
                new BlockingWaitStrategy());
        disruptor.handleEventsWith(new ContainerEventHandler());
        ringBuffer = disruptor.start();
    }

    /**
     * Stop processing incoming data.
     */
    @PreDestroy
    public void shutdown() {
        disruptor.shutdown();
    }

    @Override
    public void clear() {
        register("", -1);
    }

    @Override
    public void register(String group, long duration) {
        if (null == group) {
            group = "";
        }
        final long sequence = ringBuffer.next();
        final Registration registration = ringBuffer.get(sequence);

        registration.setGroup(group);
        registration.setDuration(duration);

        ringBuffer.publish(sequence);
    }

    @Override
    public List<GroupData> getGroupData() {
        List<GroupData> result = new ArrayList<>();
        boolean done = false;
        while (!done) {
            try {
                for (Map.Entry<String, OneContainerContainer> entry : groupData.entrySet()) {
                    OneContainer gv = entry.getValue().getValue();
                    GroupData gd = new GroupContainer(entry.getKey(), gv.getInvocationCount(), gv.getTotalRunTime());
                    result.add(gd);
                }
                done = true;
            } catch (ConcurrentModificationException cme) {
                result.clear();
            }
        }
        Collections.sort(result, GROUP_DATA_COMPARATOR);
        return result;
    }

    @Override
    public ProfilingData getTotal() {
        return total;
    }

    /**
     * Handler which reads the Registration messages and merges in the current profiling data.
     */
    private class ContainerEventHandler implements EventHandler<Registration> {

        public void onEvent(final Registration registration, final long sequence, final boolean endOfBatch)
                throws Exception {
            String group = registration.getGroup();
            long duration = registration.getDuration();
            if (duration < 0) {
                // clear data
                groupData.clear();
                total = CLEAR;
            } else {
                total = new OneContainer(total.getInvocationCount() + 1, total.getTotalRunTime() + duration);
                OneContainerContainer container = groupData.get(group);
                if (null == container) {
                    container = new OneContainerContainer();
                    container.setValue(new OneContainer(1, duration));
                    groupData.put(group, container);
                } else {
                    OneContainer gd = container.getValue();
                    container.setValue(new OneContainer(gd.getInvocationCount() + 1, gd.getTotalRunTime() + duration));
                }
            }
        }
    }

    /**
     * Comparator for {@link GroupData} instances.
     */
    private static class GroupDataComparator implements Comparator<GroupData> {

        public int compare(GroupData left, GroupData right) {
            return left.getGroup().compareTo(right.getGroup());
        }
    }

    /**
     * Container which contains a {@link OneContainer}.
     */
    private static class OneContainerContainer {

        private OneContainer value;

        /**
         * Get contained OneContainer.
         *
         * @return oneContainer
         */
        public OneContainer getValue() {
            return value;
        }

        /**
         * Set contained OneContainer.
         *
         * @param value oneContainer
         */
        public void setValue(OneContainer value) {
            this.value = value;
        }
    }
}
