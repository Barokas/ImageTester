import TestObjects.FolderTest;
import TestObjects.TestBase;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.RectangleSize;
import lib.Config;
import lib.EyesFactory;
import lib.Logger;
import lib.TestExecutor;
import org.junit.Test;

import java.io.File;

public class TestFolders extends Base {
    @Test
    public void testDefThreadFolderA() throws Exception {
        Config conf = new Config();
        conf.appName = "FolderTestsApp";
        conf.viewport = new RectangleSize(1, 1);
        TestBase folderTest = new FolderTest(new File("src/test/TestData/a"), conf);
        EyesFactory factory = new EyesFactory("1.0", conf.logger).apiKey(System.getenv("APPLITOOLS_API_KEY"));
        folderTest.run(factory.build());
    }

    @Test
    public void testThreadCTXFolderA() {
        Config conf = new Config();
        conf.appName = "FolderTestsApp";
        conf.viewport = new RectangleSize(1, 1);
        TestBase folderTest = new FolderTest(new File("src/test/TestData/a"), conf);
        EyesFactory factory = new EyesFactory("1.0", conf.logger).apiKey(System.getenv("APPLITOOLS_API_KEY"));
        TestExecutor executor = new TestExecutor(3, factory, conf);
        executor.enqueue(folderTest, null);
        executor.join();
    }

    @Test
    public void testMultiHeirarchyThreadsTest() {
        suiteRun("FolderTestsApp", "src/test/TestData/b");
    }

    @Test
    public void testSingleImage() {
        suiteRun("FolderTestsApp", "src/test/TestData/b/c/googleforgoogle.png");
    }

    @Test(expected = RuntimeException.class)
    public void testPDFTest() {
        suiteRun("FolderTestsApp", "src/test/TestData/b/c/JustPDF/Lorem2.pdf");
        suiteRun("FolderTestsApp", "src/test/TestData/b/c/JustPDF/Lorem1.pdf");
    }

    @Test
    public void testPDFPageRange() {
    }

    @Test
    public void testFlatBatch() {
        Config conf = new Config();
        conf.appName = "FlatbatchApp";
        conf.viewport = new RectangleSize(1, 1);
        conf.logger = new Logger(System.out, true);
        conf.flatBatch = new BatchInfo("Flat Batch");
        EyesFactory factory = new EyesFactory("1.0.0.1", conf.logger).apiKey(System.getenv("APPLITOOLS_API_KEY"));
        TestExecutor executor = new TestExecutor(3, factory, conf);
        Suite suite = Suite.create(new File("src/test/TestData/b"), conf, executor);
        suite.run();
    }

    @Test
    public void testPostscript() {
        suiteRun("FolderTestsApp", "src/test/TestData/b/c/JustPostscript/Lorem2.ps");
    }
}
