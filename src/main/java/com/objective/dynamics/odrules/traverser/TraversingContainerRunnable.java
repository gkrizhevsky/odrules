package com.objective.dynamics.odrules.traverser;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.objective.dynamics.components.exception.ExceptionHandler;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 3:20 PM
 */
public class TraversingContainerRunnable implements TraversingRunnable {

    private static final Logger logger = Logger.getLogger(TraversingContainerRunnable.class.getName());

    private ExceptionHandler exceptionHandler;
    private ConcurrencyHandler handler;
    private int level;

    public TraversingContainerRunnable(ExceptionHandler exceptionHandler, ConcurrencyHandler handler, int level) {
        this.exceptionHandler = exceptionHandler;
        this.handler = handler;
        this.level = level;
    }

    @Override
    public void run() {
        getLogger().info("Enter " + getClass().getSimpleName() + ":run for container: " + handler.getContainerName());
        try {
            if (isStopRequested()) {
                getLogger().info("Stop stop was requested: no execution will be performed.");
                return;
            }

            if (exceptionHandler != null && exceptionHandler.hasExceptions()) {
                getLogger().info("Exceptions detected: runnable will stop.");
                return;
            }

            try {
                handler.executeSyncBlock();
            } catch (Throwable e) {
                getLogger().log(Level.SEVERE, "Cannot traverse container", e);
                exceptionHandler.addException(e);
            }
        } finally {
            getLogger().info("Exit " + getClass().getSimpleName() + ":run for container: " + handler.getContainerName());
        }
    }


    @Override
    public boolean isStopRequested() {
        return handler.isStopRequested();
    }

    @Override
    public void requestStop() {
        handler.stop();
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public static Logger getLogger() {
        return logger;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) +
                "{" +
                "handler=" + handler +
                ", level=" + level +
                '}';
    }
}
