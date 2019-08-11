package BatchObjects;

import com.applitools.eyes.BatchInfo;
import lib.TestExecutor;

public interface IBatch {
    boolean isEmpty();

    void run(TestExecutor executor);

    void run(TestExecutor executor, BatchInfo overrideBatch);
}
