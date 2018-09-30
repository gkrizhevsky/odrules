package com.objective.dynamics.odrules;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 4:20 PM
 */
public class RuleRuntimeException extends RuntimeException {


    private String prependMessage;

    private RuleContainer ruleContainer;
    private Rule rule;

    public RuleRuntimeException() {
    }

    public RuleRuntimeException(Throwable cause) {
        super(cause);
    }

    public RuleRuntimeException(String message) {
        super(message);
    }

    public RuleRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuleRuntimeException setRuleContainer(RuleContainer rule) {
        this.ruleContainer = rule;
        return this;
    }

    public RuleRuntimeException setRule(Rule rule) {
        if (rule != null) {
            this.rule = rule;
        }
        return this;
    }

    public RuleRuntimeException setPrependMessage(String prependMessage) {
        if (prependMessage != null) {
            this.prependMessage = prependMessage;
        }
        return this;
    }

    @Override
    public String getMessage() {
        String prependedMessage = prependMessage == null ? "" : prependMessage + ". ";
        if (rule != null) {
            prependedMessage += "Rule '" + rule.getName() + "' failed. ";
            RuleContainer parentContainer = rule.getParentContainer();
            if (parentContainer != null) {
                prependedMessage += "Parent Container: '" + parentContainer.getName() + "'. ";
            }
        }

        if (ruleContainer != null) {
            prependedMessage = "Rule Container '" + ruleContainer.getName() + "' failed. ";
        }

        return prependedMessage + super.getMessage();
    }
}
