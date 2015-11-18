package io.patryk.groupingrunner.runner;

import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.Fail;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupingRunner extends BlockJUnit4ClassRunner {

    private final Map<Integer, DoWithStatement> doWithMap = new HashMap<>(1);
    private final Map<Integer, Statement> thenExecuteBanner = new HashMap<>(1);

    public GroupingRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected boolean isIgnored(FrameworkMethod child) {
        return super.isIgnored(child);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> executeMethods = getTestClass().getAnnotatedMethods(ThenExecute.class);
        executeMethods = new ArrayList<>(executeMethods);
        Collections.sort(executeMethods, new Comparator<FrameworkMethod>() {
            @Override
            public int compare(FrameworkMethod lhs, FrameworkMethod rhs) {
                ThenExecute leftAnnotation = lhs.getMethod().getAnnotation(ThenExecute.class);
                ThenExecute rightAnnotation = rhs.getMethod().getAnnotation(ThenExecute.class);
                long leftGrouping = leftAnnotation.grouping() * 100 + leftAnnotation.order();
                long rightGrouping = rightAnnotation.grouping() * 100 + rightAnnotation.order();
                return leftGrouping < rightGrouping ? -1 : 1;
            }
        });

        return Collections.unmodifiableList(executeMethods);
    }


    @Override
    protected Statement methodBlock(FrameworkMethod method) {
        Object test;
        try {
            //Used to create more readable stacktrace
            test = new ReflectiveCallable() {
                @Override
                protected Object runReflectiveCall() throws Throwable {
                    return createTest();
                }
            }.run();
        } catch (Throwable e) {
            return new Fail(e);
        }
        ThenExecute exec = method.getAnnotation(ThenExecute.class);
        ThenExecuteStatement thenStatement = new ThenExecuteStatement(method, test, exec.description());
        thenStatement.setDidWithDescription(getDoWithDescription(exec.grouping()).get(0).getAnnotation(DoWith.class).description());
        Statement statement = doWith(thenStatement, method, test);
        return statement;
    }

    private Statement doWith(Statement statement, FrameworkMethod method, Object test) {
        ThenExecute exec = method.getAnnotation(ThenExecute.class);
        List<FrameworkMethod> filteredMethods = getDoWithDescription(exec.grouping());

        DoWithStatement doWithStatement;
        if(!doWithMap.containsKey(exec.grouping())){
             doWithStatement = new DoWithStatement(filteredMethods, test, method, statement, exec.description());
            doWithMap.put(exec.grouping(), doWithStatement);
        }
        else{
            doWithStatement = doWithMap.get(exec.grouping());
            doWithStatement.setNext(statement);
        }
        return doWithStatement;
    }

    private List<FrameworkMethod> getDoWithDescription(int grouping){
        List<FrameworkMethod> doWithMethods = getTestClass().getAnnotatedMethods(DoWith.class);
        List<FrameworkMethod> filteredMethods = new ArrayList<>(doWithMethods.size());
        for(int i=0; i < doWithMethods.size(); i++){
            if(doWithMethods.get(i).getAnnotation(DoWith.class).grouping() == grouping){
                filteredMethods.add(doWithMethods.get(i));
            }
        }
        return filteredMethods;
    }
}
