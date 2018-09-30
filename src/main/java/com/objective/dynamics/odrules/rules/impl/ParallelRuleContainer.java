package com.objective.dynamics.odrules.rules.impl;

import com.objective.dynamics.odrules.ExecutionType;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 2:51 PM
 */
public class ParallelRuleContainer extends AbstractRuleContainer {

    private int numberOfThreads;

    public ParallelRuleContainer(String name) {
        super(name, ExecutionType.PARALLEL);
    }

    @Override
    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public ParallelRuleContainer setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        return this;
    }


}
