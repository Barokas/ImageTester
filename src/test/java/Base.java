import com.applitools.eyes.RectangleSize;
import lib.Config;
import lib.EyesFactory;
import lib.Logger;
import lib.TestExecutor;

import java.io.File;

public class Base {
    public void suiteRun(String app, String file) {
        Config conf = new Config();
        conf.appName = app;
        conf.viewport = new RectangleSize(1, 1);
        conf.logger = new Logger(System.out, true);
        EyesFactory factory = new EyesFactory("1.0", conf.logger).apiKey(System.getenv("APPLITOOLS_API_KEY"));
        TestExecutor executor = new TestExecutor(3, factory, conf);
        Suite suite = Suite.create(new File(file), conf, executor);
        suite.run();
    }
}
