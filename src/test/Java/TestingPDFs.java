import com.applitools.ImageTester.Interfaces.*;
import com.applitools.ImageTester.SuiteBuilder;
import com.applitools.ImageTester.TestObjects.PDFTest;
import com.applitools.eyes.TestResults;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestingPDFs extends TestBase {
    @Test
    public void runCmdSingle() {
        run("src/test/TestFiles/PDFs/Lorem1.pdf");
    }

    @Test
    public void runProgrammaticSingle() {
        PDFTest pdfTest = new PDFTest(
                "src/test/TestFiles/PDFs/Lorem1.pdf",
                "ImageTester Test suite",
                250, null);
        pdfTest.run(getEyes());
    }

    @Test
    public void runProgrammaticSingleReporting() {
        List<TestResults> resultsList = new ArrayList<TestResults>();
        IResultsReporter reporter = new IResultsReporter() {
            @Override
            public void onTestFinished(String testName, TestResults result) {
                resultsList.add(result);
            }

            @Override
            public void onBatchFinished(String batchName) {
                //TODO
            }
        };
        PDFTest pdfTest = new PDFTest(
                "src/test/TestFiles/PDFs/Lorem1.pdf",
                "ImageTester Test suite",
                250,
                null,
                reporter);
        pdfTest.run(getEyes());

        Assert.assertTrue(resultsList.size() == 1);
    }

    @Test
    public void runProgrammaticFolderReporting() throws IOException {
        List<TestResults> resultsList = new ArrayList<TestResults>();
        IResultsReporter reporter = new IResultsReporter() {
            @Override
            public void onTestFinished(String testName, TestResults result) {
                resultsList.add(result);
            }

            @Override
            public void onBatchFinished(String batchName) {
                //TODO
            }
        };

        SuiteBuilder builder = new SuiteBuilder(
                "src/test/TestFiles/PDFs",
                "ImageTester Test suite",
                null,
                reporter);

        ITestable suite = builder.build();
        suite.run(getEyes());

        Assert.assertTrue(resultsList.size() == 2);
    }
}
