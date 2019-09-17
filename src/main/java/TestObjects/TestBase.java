package TestObjects;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.images.Eyes;
import lib.Config;
import lib.Logger;
import lib.Utils;

import java.io.File;

public abstract class TestBase implements ITest {
    private final File file_;
    private final Config conf_;
    protected String fileNameProperty =null;

    public TestBase(File file, Config conf) {
        this.file_ = file;
        this.conf_ = conf;
    }

    public File file() {
        return file_;
    }

    public Config config() {
        return conf_;
    }

    public String appName() {
        return this.conf_.appName;
    }

    public RectangleSize viewport() {
        return this.conf_.viewport;
    }

    public String name() {
        if(null!=this.config().testName){
            this.fileNameProperty=file_.getName();
            return this.config().testName;
        }
        return file_.getName();
    }

    public TestResults runSafe(Eyes eyes) {
        try {
            TestResults res = run(eyes);
            Utils.handleResultsDownload(conf_.eyesUtilsConf, res);
            return res;
        } catch (Exception e) {
            logger().reportException(e);
        } finally {
            eyes.abortIfNotClosed();
        }

        return null;
    }

    public Logger logger() {
        return conf_.logger;
    }
}
