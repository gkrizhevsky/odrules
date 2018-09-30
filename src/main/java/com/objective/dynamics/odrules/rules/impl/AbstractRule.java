package com.objective.dynamics.odrules.rules.impl;


import com.objective.dynamics.odrules.Rule;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 1:10 PM
 */
public abstract class AbstractRule extends AbstractRuleItem implements Rule {

    protected AbstractRule(String name) {
        super(name);
    }


}
