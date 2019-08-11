package lib;

import TestObjects.IDisposable;
import TestObjects.TestBase;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.images.Eyes;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestExecutor {
    private final Logger logger_;
    private final BatchInfo flatBatch;
    private ExecutorService executorService_;
    private ThreadLocal<Eyes> thEyes_;
    private Queue<Future<ExecutorResult>> results_ = new LinkedList<>();

    public TestExecutor(int threads, EyesFactory eyesFactory, Config conf) {
        this.executorService_ = Executors.newFixedThreadPool(threads);
        this.thEyes_ = ThreadLocal.withInitial(() -> eyesFactory.build());
        this.logger_ = conf.logger;
        this.flatBatch = conf.flatBatch;
    }

    public void enqueue(TestBase test, BatchInfo batch) {
        Future<ExecutorResult> f = executorService_.submit(() -> {
            long startTime = System.nanoTime();
            Eyes eyes = thEyes_.get();
            eyes.setBatch(flatBatch != null ? flatBatch : batch);
            TestResults result = test.runSafe(eyes);
            eyes.abortIfNotClosed();
            eyes.setBatch(null);
            if (test instanceof IDisposable)
                ((IDisposable) test).dispose();
            long endTime = System.nanoTime();

            return new ExecutorResult(result, (endTime - startTime));
        });

        results_.add(f);
    }

    public void join() {
        while (!results_.isEmpty()) {
            try {
                ExecutorResult result = results_.remove().get();
                logger_.reportResult(result);
            } catch (Exception e) {
                logger_.reportException(e);
            }

        }
    }
}
