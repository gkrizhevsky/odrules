package com.objective.dynamics.odrules.rules.impl;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.objective.dynamics.odrules.RuleManager;
import com.objective.dynamics.odrules.rules.impl.model.Applicant;
import com.objective.dynamics.odrules.rules.impl.model.SuggestedRole;

/**
 * @author Gennady Krizhevsky
 * Date: 29/09/18
 * Time: 2:21 PM
 */
public class SuggestApplicantTest {

    private static final String APPLICANT = "applicant";
    private static final String SUGGESTED_ROLE = "suggestedRole";
    private RuleManager ruleManager;

    @Before
    public void setup() {
        ruleManager = new RuleManagerImpl();
    }

    @Test
    public void whenCriteriaMatching_ThenSuggestManagerRole() {
        Applicant applicant = new Applicant("Davis", 37, 160000.0, 11);
        final SuggestedRole suggestedRole = new SuggestedRole();
        fireRules(applicant, suggestedRole);
        assertEquals("Manager", suggestedRole.getRole());
    }

    @Test
    public void whenCriteriaMatching_ThenSuggestSeniorDeveloperRole() {
        Applicant applicant = new Applicant("John", 37, 120000.0, 8);
        SuggestedRole suggestedRole = new SuggestedRole();
        fireRules(applicant, suggestedRole);
        assertEquals("Senior developer", suggestedRole.getRole());
    }

    @Test
    public void whenCriteriaMatching_ThenSuggestDeveloperRole() {
        Applicant applicant = new Applicant("Davis", 37, 80000.0, 3);
        SuggestedRole suggestedRole = new SuggestedRole();
        fireRules(applicant, suggestedRole);
        assertEquals("Developer", suggestedRole.getRole());
    }

    @Test
    public void whenCriteriaNotMatching_ThenNoRole() {
        Applicant applicant = new Applicant("John", 37, 1200000.0, 5);
        SuggestedRole suggestedRole = new SuggestedRole();
        fireRules(applicant, suggestedRole);
        assertNull(suggestedRole.getRole());
    }

    private SimpleRuleContext fireRules(Applicant applicant, SuggestedRole suggestedRole) {

        final SimpleRuleContext ruleContext = new SimpleRuleContext.Builder()
                .setRootContainer("root", 0)
                .build();

        ruleContext
                .addItem(APPLICANT, applicant)
                .addItem(SUGGESTED_ROLE, suggestedRole)
                .withRootContainer()
                .addRule(new AbstractRule("SuggestManagerRole") {
                    @Override
                    public void execute() {
                        Applicant applicant = getRuleContext().getItem(APPLICANT);
                        if (applicant.getCurrentSalary() > 100000 && applicant.getCurrentSalary() <= 250000) {
                            suggestedRole.setRole("Manager");
                        }
                    }
                })
                .addRule(new AbstractRule("SuggestSeniorDeveloperRole") {
                    @Override
                    public void execute() {
                        Applicant applicant = getRuleContext().getItem(APPLICANT);
                        if (applicant.getExperienceInYears() > 5 && applicant.getExperienceInYears() <= 10 &&
                                applicant.getCurrentSalary() > 50000 && applicant.getCurrentSalary() <= 150000) {
                            suggestedRole.setRole("Senior developer");
                        }
                    }
                })
                .addRule(new AbstractRule("SuggestDeveloperRole") {
                    @Override
                    public void execute() {
                        Applicant applicant = getRuleContext().getItem(APPLICANT);
                        if (applicant.getExperienceInYears() > 0 && applicant.getExperienceInYears() <= 5 &&
                                applicant.getCurrentSalary() > 20000 && applicant.getCurrentSalary() <= 100000) {
                            suggestedRole.setRole("Developer");
                        }
                    }
                })
        ;


        ruleManager.execute(ruleContext);
        System.out.println(suggestedRole);
        System.out.println("---------------------------------------------------------");
        System.out.println("-----------------   ExecutionPath  ----------------------");
        System.out.println("---------------------------------------------------------");
        System.out.println(ruleManager.getExecutionPath());

        return ruleContext;
    }


}
