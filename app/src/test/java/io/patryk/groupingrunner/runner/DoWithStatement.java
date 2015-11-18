package io.patryk.groupingrunner.runner;

import org.junit.AssumptionViolatedException;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.List;

public class DoWithStatement extends Statement {
    private final FrameworkMethod method;
    private final List<FrameworkMethod> methods;
    private final Object test;
    private final String description;
    private Statement next;
    boolean computed = false;

    public DoWithStatement(List<FrameworkMethod> filteredMethods, Object test, FrameworkMethod method, Statement next, String description) {
        this.methods = filteredMethods;
        this.test = test;
        this.description = description;
        this.method = method;
        computed = false;
        this.next = next;
    }

    public void setNext(Statement next) {
        this.next = next;
    }

    @Override
    public void evaluate() throws Throwable {
        if(!computed){
            for(FrameworkMethod doWithMethod : methods){
                try {
                    doWithMethod.invokeExplosively(test);
                } catch (AssumptionViolatedException e) {
                    System.out.println(getDescription(doWithMethod));
                    throw e;

                } catch (Throwable e) {
                    System.out.println(getDescription(doWithMethod));
                    throw e;
                }
            }
            computed = true;
        }

        this.next.evaluate();
    }

    public String getDescription(FrameworkMethod method) {
        return method.getAnnotation(DoWith.class).description();
    }
}
