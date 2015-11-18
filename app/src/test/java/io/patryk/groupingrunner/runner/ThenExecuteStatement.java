package io.patryk.groupingrunner.runner;

import org.junit.AssumptionViolatedException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class ThenExecuteStatement extends Statement{
    private final FrameworkMethod testMethod;
    private final Object target;
    private final String executionDescription;
    private String didWithDescription;

    public ThenExecuteStatement(FrameworkMethod testMethod, Object target, String executionDescription) {
        this.testMethod = testMethod;
        this.target = target;
        this.executionDescription = executionDescription;
    }

    @Override
    public void evaluate() throws Throwable {
        try {
            testMethod.invokeExplosively(target);
        } catch (AssumptionViolatedException e) {
            System.out.println(didWithDescription + " " + executionDescription);
            throw e;

        } catch (Throwable e) {
            System.out.println(didWithDescription + " " + executionDescription);
            throw e;
        }
    }

    public void setDidWithDescription(String didWithDescription) {
        this.didWithDescription = didWithDescription;
    }
}
