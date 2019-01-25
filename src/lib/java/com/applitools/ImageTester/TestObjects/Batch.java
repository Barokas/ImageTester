package lib.java.com.applitools.ImageTester.TestObjects;

import com.applitools.ImageTester.ImageTester;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.images.Eyes;
import lib.java.com.applitools.ImageTester.Interfaces.IResultsReporter;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

public class Batch extends TestUnit {
    private BatchInfo batch_;
    private Queue<Test> tests_ = new LinkedList<Test>();
    private IResultsReporter reporter_;

    public Batch(File file, IResultsReporter reporter) {
        super(file);
        reporter_ = reporter;
    }

    public Batch(BatchInfo batch, IResultsReporter reporter) {
        super(batch.getName());
        batch_ = batch;
        reporter_ = reporter;
    }

    // Original

//    public void run(Eyes eyes) {
//        batch_ = batch_ == null ? new BatchInfo(name()) : batch_;
//        eyes.setBatch(batch_);
//        System.out.printf("Batch: %s\n", name());
//        for (Test test : tests_) {
//            try {
//                test.run(eyes);
//            } finally {
//                test.dispose();
//            }
//        }
//        reporter_.onBatchFinished(batch_.getName());
//        eyes.setBatch(null);
//    }

    public void run(Eyes eyes) {
        batch_ = batch_ == null ? new BatchInfo(name()) : batch_;
//        eyes.setBatch(batch_);
        System.out.printf("Batch: %s\n", name());
        for (Test test : tests_) {
            try {
//                test.run(eyes);
                test.setEyes(ImageTester.getConfiguredEyes());
                test.setBatch(batch_);
//          This line is working properly
//            new Thread(test).start();

                // This is the added line that isn't working
                ImageTester.parallelRunsHandler.addRunnable(test);

            } finally {
                test.dispose();
            }
        }
        reporter_.onBatchFinished(batch_.getName());
        eyes.setBatch(null);
    }

    public void addTest(Test test) {
        tests_.add(test);
    }

    public void dispose() {
        if (tests_ == null) return;
        for (Test test : tests_) {
            test.dispose();
        }
    }

    @Override
    public void run() {
        run(eyes);
    }
}
