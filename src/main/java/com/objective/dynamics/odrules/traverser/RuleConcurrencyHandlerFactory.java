package com.objective.dynamics.odrules.traverser;


import com.objective.dynamics.odrules.ExecutionType;
import com.objective.dynamics.odrules.RuleContainer;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 4:54 PM
 */
public interface RuleConcurrencyHandlerFactory {

    NullConcurrencyHandlerFactory NULL_CONCURRENCY_HANDLER_FACTORY =
            new NullConcurrencyHandlerFactory();

    ConcurrencyHandler concurrencyHandler(RuleContainer container, ExecutionType executionType);

    class NullConcurrencyHandlerFactory implements RuleConcurrencyHandlerFactory {
        @Override
        public ConcurrencyHandler concurrencyHandler(RuleContainer container, ExecutionType executionType) {
            return ConcurrencyHandler.CONCURRENCY_HANDLER;
        }
    }
}
