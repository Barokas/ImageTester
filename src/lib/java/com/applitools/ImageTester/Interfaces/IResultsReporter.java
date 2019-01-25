package lib.java.com.applitools.ImageTester.Interfaces;

import com.applitools.eyes.TestResults;

public interface IResultsReporter {
    void onTestFinished(String testName, TestResults result);
    void onBatchFinished(String batchName);
}
