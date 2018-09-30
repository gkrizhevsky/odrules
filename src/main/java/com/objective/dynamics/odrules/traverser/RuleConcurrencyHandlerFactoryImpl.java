package com.objective.dynamics.odrules.traverser;


import static com.objective.dynamics.components.exception.impl.ExceptionHandlerImpl.assertNotNull;

import java.util.logging.Logger;

import com.objective.dynamics.odrules.ExecutionType;
import com.objective.dynamics.odrules.RuleContainer;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 4:52 PM
 */
public class RuleConcurrencyHandlerFactoryImpl implements RuleConcurrencyHandlerFactory {

    private Logger logger = Logger.getLogger(getClass().getName());

    private static final int ABSOLUTE_MAX_NUMBER_OF_THREADS_PER_CONTAINER = 16;
    private static final int DEFAULT_MAX_NUMBER_OF_THREADS_PER_CONTAINER = 4;
    private int defaultNumberOfThreadsPerContainer = DEFAULT_MAX_NUMBER_OF_THREADS_PER_CONTAINER;

    @Override
    public ConcurrencyHandler concurrencyHandler(RuleContainer container, ExecutionType executionType) {
        assertNotNull("container", container);

        int numberOfThreads =
                resolveNumberOfThreadsPerContainer(container, executionType);

        ConcurrencyHandlerImpl concurrencyHandler =
                new ConcurrencyHandlerImpl(numberOfThreads, container);

        getLogger().info("  Created " + concurrencyHandler);
        return concurrencyHandler;
    }

    private int resolveNumberOfThreadsPerContainer(RuleContainer container, ExecutionType executionType) {
        int numberOfThreadsPerContainer;
        if (executionType == ExecutionType.SEQUENCE) {
            numberOfThreadsPerContainer = 0;
        } else {
            if (container.getNumberOfThreads() > 0) {
                numberOfThreadsPerContainer = container.getNumberOfThreads();
            } else {
                numberOfThreadsPerContainer = getDefaultNumberOfThreadsPerContainer();
            }
        }
        return numberOfThreadsPerContainer;
    }

    @SuppressWarnings("unused")
    public int getDefaultNumberOfThreadsPerContainer() {
        return defaultNumberOfThreadsPerContainer;
    }

    @SuppressWarnings("unused")
    public void setDefaultNumberOfThreadsPerContainer(int defaultNumberOfThreadsPerContainer) {
        if (defaultNumberOfThreadsPerContainer > 0
                && defaultNumberOfThreadsPerContainer <= ABSOLUTE_MAX_NUMBER_OF_THREADS_PER_CONTAINER) {
            this.defaultNumberOfThreadsPerContainer = defaultNumberOfThreadsPerContainer;
        }
    }

    public Logger getLogger() {
        return logger;
    }
}
