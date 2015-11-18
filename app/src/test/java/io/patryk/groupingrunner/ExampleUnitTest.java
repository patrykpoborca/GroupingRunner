package io.patryk.groupingrunner;

import org.junit.runner.RunWith;

import io.patryk.groupingrunner.runner.DoWith;
import io.patryk.groupingrunner.runner.GroupingRunner;
import io.patryk.groupingrunner.runner.ThenExecute;

import static org.junit.Assert.assertFalse;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(GroupingRunner.class)
public class ExampleUnitTest {
    private static final int initial_grouping = 0;

    @DoWith(description = "Put on Nikes,", grouping = initial_grouping)
    public void putOnSneakers(){
    }

    @ThenExecute(description =  "Run a Mile,", grouping = initial_grouping, order = 1)
    public void runMile(){
        assertFalse(true);
    }

}