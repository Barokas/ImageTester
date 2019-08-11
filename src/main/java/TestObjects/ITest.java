package TestObjects;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.images.Eyes;

public interface ITest {
    TestResults run(Eyes eyes) throws Exception;

    String appName();

    String name();

    RectangleSize viewport();

    boolean isEmpty();
}
