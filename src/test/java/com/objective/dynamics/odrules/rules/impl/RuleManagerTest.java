package com.objective.dynamics.odrules.rules.impl;

import static com.objective.dynamics.odrules.rules.impl.RuleContainerFactory.newSequenceContainer;

import org.junit.Before;
import org.junit.Test;

import com.objective.dynamics.odrules.RuleManager;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 2:21 PM
 */
public class RuleManagerTest {

    private RuleManager ruleManager;

    @Before
    public void setup() {
        ruleManager = new RuleManagerImpl();


    }

    @Test
    public void executeRulesSimple() {

        final SimpleRuleContext ruleContext = new SimpleRuleContext.Builder()
                .setRootContainer("root", 0)
                .build();

        ruleContext.withRootContainer()
                .addContainer(newSequenceContainer("childContainer01"))
                .addRule(new AbstractRule("rule-01") {
                    @Override
                    public void execute() {

                    }
                })
                .addRule(new AbstractRule("rule-02") {
                    @Override
                    public void execute() {

                    }
                });


        /*
        newSequenceContainer("root")
                                .addContainer(newSequenceContainer("childContainer01"))
                                .addRule(new AbstractRule("rule-01") {
                                    @Override
                                    public void execute() {

                                    }
                                })
                                .addRule(new AbstractRule("rule-02") {
                                    @Override
                                    public void execute() {

                                    }
                                })
         */
        ruleManager.execute(ruleContext);


        System.out.println("---------------------------------------------------------");
        System.out.println("-----------------   ExecutionPath  ----------------------");
        System.out.println("---------------------------------------------------------");
        System.out.println(ruleManager.getExecutionPath());

    }




}
