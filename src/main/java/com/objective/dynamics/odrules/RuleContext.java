package com.objective.dynamics.odrules;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 1:57 PM
 */
public interface RuleContext {

    RuleContainer withRootContainer();

    RuleContext addItem(String name, Object item);

    <T> T getItem(String name);

}
