package com.objective.dynamics.odrules;

import java.util.List;

import com.objective.dynamics.components.exception.ExceptionHandler;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 1:51 PM
 */
public interface RuleContainer extends RuleItem {

    int getNumberOfThreads();

    ExceptionHandler getRootExceptionHandler();

    List<RuleItem> getRuleItems();

    ExecutionType getExecutionType();

    RuleContainer addRule(Rule rule);

    RuleContainer addContainer(RuleContainer container);

    void validate();
}
