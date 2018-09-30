package com.objective.dynamics.odrules;

import com.objective.dynamics.odrules.rules.impl.ExecutionPath;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 1:56 PM
 */
public interface RuleManager {

    void execute(RuleContext context);

    ExecutionPath getExecutionPath();
}
