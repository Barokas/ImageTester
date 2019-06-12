package com.applitools.ImageTester.TestObjects;

import com.applitools.ImageTester.ImageTester;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.images.Eyes;
import com.applitools.ImageTester.Interfaces.ITestable;
import com.applitools.ImageTester.Patterns;
import com.applitools.ImageTester.StdoutReporter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static com.applitools.ImageTester.ImageTester.handleResultsDownload;

public class PDFPageStep implements Runnable{
    private static final Pattern pattern = Patterns.PDF;
    private String appName;
    private String testName;
    private RectangleSize viewportSize;
    private BatchInfo batch;
    private StdoutReporter reporter =new StdoutReporter("\t[%s] - %s\n");
    private Eyes eyes;
    private PDDocument document;
    private float dpi;
    private int PageNumber;
    private File file;
    private String password;

    public PDFPageStep(File file, String password, int pageNumber, float dpi, String appName_, String testName_, RectangleSize viewportSize_, BatchInfo batch){
        eyes = ImageTester.getConfiguredEyes();
        this.file =file;
        this.password = password;
        PageNumber=pageNumber;
        this.appName=appName_;
        this.testName=testName_;
        this.viewportSize=viewportSize_;
        this.batch=batch;
        this.dpi=dpi;
    }

    public void run(){
        try {
            if(null==this.document) {
                this.document = PDDocument.load(file, password);
            }

            PDFRenderer pdfRenderer = new PDFRenderer(document);
            eyes.setBatch(batch);
            eyes.open(appName,testName,viewportSize);


            BufferedImage bim = pdfRenderer.renderImageWithDPI(PageNumber, dpi);
            eyes.checkImage(bim);
            TestResults result = eyes.close(false);
            handleResultsDownload(result);
             reporter.onTestFinished("Batch: "+this.eyes.getBatch().getName()+" - "+testName, result);
//            reporter.onTestFinished(testName, result);
            this.document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
