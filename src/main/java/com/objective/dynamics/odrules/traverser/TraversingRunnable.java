package com.objective.dynamics.odrules.traverser;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 2:45 PM
 */
public interface TraversingRunnable extends Runnable {

    boolean isStopRequested();

    void requestStop();
}
