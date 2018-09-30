package com.objective.dynamics.odrules.rules.impl;


import com.objective.dynamics.odrules.ExecutionType;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 2:51 PM
 */
public class SequenceRuleContainer extends AbstractRuleContainer {

    public SequenceRuleContainer(String name) {
        super(name, ExecutionType.SEQUENCE);
    }

    @Override
    public int getNumberOfThreads() {
        return 0;
    }


}
