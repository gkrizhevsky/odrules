package com.objective.dynamics.components.thread.impl;

import java.util.concurrent.ThreadFactory;

/**
 * @author Gennady Krizhevsky
 * Date: 2018-06-28
 * Time: 9:26 AM
 */
public class WorkerThreadFactory implements ThreadFactory {

    private String baseThreadName;
    private int counter = 0;

    WorkerThreadFactory(String baseThreadName) {
        this.baseThreadName = baseThreadName;
    }

    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, getNewThreadName());
        thread.setDaemon(true);
        return thread;
    }

    private String getNewThreadName() {
        if (++counter == Integer.MAX_VALUE) {
            counter = 0;
        }
        return baseThreadName + "#" + counter;
    }

}
