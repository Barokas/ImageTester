package lib.java.com.applitools.ImageTester;


import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.RectangleSize;
import lib.java.com.applitools.ImageTester.Interfaces.IResultsReporter;
import lib.java.com.applitools.ImageTester.Interfaces.ITestable;
import lib.java.com.applitools.ImageTester.TestObjects.*;
import org.apache.commons.io.comparator.NameFileComparator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class SuiteBuilder {
    private File rootFolder_;
    private String appname_;
    private RectangleSize viewport_;
    private EyesUtilitiesConfig eyesUtilitiesConfig_;
    private float pdfdpi_ = 250;
    private String pdfPassword_;
    private String pages_;
    private IResultsReporter reporter_;
    private boolean includePagesInTestName_;



    public String getPages() {
        return pages_;
    }

    public void setPages(String steps, boolean includePagesInTestName) {
        this.pages_ = steps;
        this.includePagesInTestName_ = includePagesInTestName;
    }

    public EyesUtilitiesConfig getEyesUtilitiesConfig() {
        return eyesUtilitiesConfig_;
    }

    public void setEyesUtilitiesConfig(EyesUtilitiesConfig eyesUtilitiesConfig) {
        this.eyesUtilitiesConfig_ = eyesUtilitiesConfig;
    }


    public SuiteBuilder(String rootFolder, String appname, RectangleSize viewport, IResultsReporter reporter) {
        this.rootFolder_ = new File(rootFolder);
        this.appname_ = appname;
        this.viewport_ = viewport;
        this.reporter_ = reporter;
    }

    public ITestable build() throws IOException {
        return build(rootFolder_);
    }

    public void setDpi(float dpi) {
        this.pdfdpi_ = dpi;
    }

    private ITestable build(File curr) throws IOException {
        String jenkinsJobName = System.getenv("JOB_NAME");
        String jenkinsApplitoolsBatchId = System.getenv("APPLITOOLS_BATCH_ID");
        Batch jenkinsBatch = null;

        if ((jenkinsJobName != null) && (jenkinsApplitoolsBatchId != null)) {
            BatchInfo batch = new BatchInfo(jenkinsJobName);
            batch.setId(jenkinsApplitoolsBatchId);
            jenkinsBatch = new Batch(batch, reporter_);
        }
        ITestable unit = build(curr, jenkinsBatch);
        if (unit instanceof ImageStep) {
            ImageStep step = (ImageStep) unit;
            Test test = new Test(step.getFile(), appname_, viewport_, reporter_);
            test.setEyesUtilitiesConfig(eyesUtilitiesConfig_);
            test.addStep(step);
            if (step.hasRegionFile())
                test.addSteps(step.getRegions());
            unit = test;
        }
        if (unit instanceof Test && jenkinsBatch != null) {
            jenkinsBatch.addTest((Test) unit);
            return jenkinsBatch;
        } else {
            return unit;
        }
    }

    private ITestable build(File curr, Batch flatBatch) throws IOException {
        if (!curr.exists()) {
            System.out.printf(String.format("The folder %s doesn't exists\n", curr.getAbsolutePath()));
            return null;
        }

        if (appname_ == null) appname_ = "ImageTester";

        if (curr.isFile()) {
            if (PDFTest.supports(curr)) {
                PDFTest pdftest = new PDFTest(curr, appname_, pdfdpi_, viewport_, reporter_);
                pdftest.setEyesUtilitiesConfig(eyesUtilitiesConfig_);
                pdftest.setPages(pages_, includePagesInTestName_);
                pdftest.setPdfPassword(pdfPassword_);
                return pdftest;
            }
            if (PostscriptTest.supports(curr)) {
                PostscriptTest postScriptest = new PostscriptTest(curr, appname_);
                postScriptest.setEyesUtilitiesConfig(eyesUtilitiesConfig_);
                return postScriptest;
            }
            return ImageStep.supports(curr) ? new ImageStep(curr) : null;
        }

        Test currTest = null;
        Batch currBatch = flatBatch;
        Suite currSuite = null;

        File[] files = curr.listFiles();
        Arrays.sort(files, NameFileComparator.NAME_COMPARATOR);

        for (File file : files) {
            ITestable unit = build(file, flatBatch);
            if (unit instanceof ImageStep) {
                if (currTest == null) currTest = new Test(curr, appname_, viewport_, reporter_);
                currTest.setEyesUtilitiesConfig(eyesUtilitiesConfig_);
                ImageStep step = (ImageStep) unit;
                if (step.hasRegionFile())
                    currTest.addSteps(step.getRegions());
                else
                    currTest.addStep(step);
            } else if (unit instanceof Test) {
                if (currBatch == null) currBatch = new Batch(curr, reporter_);
                currBatch.addTest((Test) unit);
            } else if (flatBatch == null)
                if (unit instanceof Batch) {
                    if (currSuite == null) currSuite = new Suite(curr);
                    currSuite.addBatch((Batch) unit);
                } else if (unit instanceof Suite) {
                    Suite suite = (Suite) unit;
                    if (currSuite == null) currSuite = new Suite(curr);
                    suite.portBatchesTo(currSuite);
                    if (suite.hasOrphanTest()) {
                        if (currBatch == null) currBatch = new Batch(curr, reporter_);
                        suite.portTestTo(currBatch);
                    }
                } else {
                    //SKIP
                }
        }

        if (flatBatch == null) {
            //Simple cases
            if (currTest == null && currBatch == null && currSuite == null) return null;
            if (currTest != null && currBatch == null && currSuite == null) return currTest;
            if (currTest == null && currBatch != null && currSuite == null) return currBatch;
            if (currTest == null && currBatch == null && currSuite != null) return currSuite;

            //The complicated case
            if (currSuite == null) currSuite = new Suite(curr);
            if (currBatch != null) currSuite.addBatch(currBatch);
            if (currTest != null) currSuite.setTest(currTest);

            return currSuite;
        } else if (currTest != null)
            return currTest;

        return currBatch;
    }

    public String getPdfPassword() {
        return pdfPassword_;
    }

    public void setPdfPassword(String pdfPassword) {
        this.pdfPassword_ = pdfPassword;
    }
}
