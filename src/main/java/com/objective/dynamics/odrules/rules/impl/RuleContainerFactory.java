package com.objective.dynamics.odrules.rules.impl;


import com.objective.dynamics.odrules.ExecutionType;
import com.objective.dynamics.odrules.RuleContainer;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 2:50 PM
 */
public class RuleContainerFactory {


    public static RuleContainer newContainer(String name, int numberOfThreads) {
        if (numberOfThreads > 0) {
            return newParallelContainer(name, numberOfThreads);
        } else {
            return newSequenceContainer(name);
        }
    }

    public static RuleContainer newSequenceContainer(String name) {
        return newContainer(ExecutionType.SEQUENCE, name, 0);
    }

    public static RuleContainer newParallelContainer(String name, int numberOfThreads) {
        return newContainer(ExecutionType.PARALLEL, name, numberOfThreads);
    }


    private static RuleContainer newContainer(ExecutionType executionType, String name, int numberOfThreads) {
        if (executionType == null || executionType == ExecutionType.SEQUENCE) {
            return new SequenceRuleContainer(name);
        } else if (executionType == ExecutionType.PARALLEL) {
            return new ParallelRuleContainer(name).setNumberOfThreads(numberOfThreads);
        } else {
            throw new IllegalArgumentException("Unsupported executionType: " + executionType);
        }
    }

}

