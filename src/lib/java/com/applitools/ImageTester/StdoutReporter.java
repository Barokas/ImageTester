package lib.java.com.applitools.ImageTester;

import com.applitools.eyes.TestResults;
import lib.java.com.applitools.ImageTester.Interfaces.IResultsReporter;

public class StdoutReporter implements IResultsReporter {

    private final String template_;

    public StdoutReporter(String template) {
        template_ = template;
    }

    @Override
    public void onTestFinished(String testName, TestResults result) {
        String status = null;
        if (result == null) return;
        if (result.getSteps() > 0) {
            if (result.isNew()) status = "New";
            else if (result.isPassed()) status = "Passed";
            else if (result.isAborted()) status = "Aborted";
            else status = "Mismatch";
        } else
            status = "Empty";

        System.out.printf(template_, status, testName);
        if (result.isDifferent())
            System.out.printf("\t + Result url: %s \n", result.getUrl());
    }

    @Override
    public void onBatchFinished(String batchName) {
        //TODO
    }


}
