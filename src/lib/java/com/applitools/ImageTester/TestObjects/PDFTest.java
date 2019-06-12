package com.applitools.ImageTester.TestObjects;

import com.applitools.ImageTester.ImageTester;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.images.Eyes;
import com.applitools.ImageTester.Interfaces.IResultsReporter;
import com.applitools.ImageTester.PDFUtilities;
import com.applitools.ImageTester.Patterns;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by yanir on 30/01/2016.
 */
public class PDFTest extends Test {
    private static final Pattern pattern = Patterns.PDF;
    private float dpi_;
    private String pdfPassword;
    private String pages_;
    private boolean includePagesInTestName_;

    public void setIncludePagesInTestName_(boolean includePagesInTestName){
        this.includePagesInTestName_=includePagesInTestName;
    }

    public void setPages(String pages, boolean includePagesInTestName) throws IOException {
        this.pages_ = pages;
        this.includePagesInTestName_ = includePagesInTestName;
    }

    public PDFTest(File file, String appname) {
        this(file, appname, 300f, null);
    }

    public PDFTest(File file, String appname, float dpi, RectangleSize viewport) {
        this(file, appname, dpi, viewport, null);
    }

    public PDFTest(String file, String appname, float dpi, RectangleSize viewport) {
        this(new File(file), appname, dpi, viewport, null);
    }

    public PDFTest(String file, String appname, float dpi, RectangleSize viewport, IResultsReporter reporter) {
        this(new File(file), appname, dpi, viewport, reporter);
    }

    public PDFTest(File file, String appname, float dpi, RectangleSize viewport, IResultsReporter reporter) {
        super(file, appname, viewport, reporter);
        this.dpi_ = dpi;
    }

    public List<PDFPageStep> getPDFPageSteps() throws IOException {
        if (ImageTester.isPDFParallelPerPage){
            PDFUtilities pdfUtilities = new PDFUtilities(file_,pdfPassword,dpi_);
            setIncludePagesInTestName_(false);
            return pdfUtilities.getPDFPerPage(pages_,appname_, name(), viewportSize_, this.getBatch());
        }
        return null;
    }


    @Override
    public void run(Eyes eyes) {
        Exception ex = null;
        TestResults result = null;
        if(this.getBatch() == null){
            BatchInfo batchInfo = new BatchInfo(name());
            this.setBatch(batchInfo);

        } else{
            eyes.setBatch(this.getBatch());
        }


        try {

                // If all pages in the document will be a single test
            if (!ImageTester.isPDFParallelPerPage) {
                PDDocument document = PDDocument.load(file_, pdfPassword);
                PDFUtilities pdfUtilities = new PDFUtilities(document,dpi_);
                eyes.open(appname_, name(), viewportSize_);
                pdfUtilities.checkPDF(eyes, "Page-", pages_);
                result = eyes.close(false);
                reporter_.onTestFinished("Batch: "+this.getBatch().getName()+" - "+name(), result);
                handleResultsDownload(result);
                document.close();
            }
            else{ // If running each page in the PDF in parallel
                PDFUtilities pdfUtilities = new PDFUtilities(file_,pdfPassword,dpi_);
                setIncludePagesInTestName_(false);
                pdfUtilities.checkPDFPerPage(pages_,appname_, name(), viewportSize_, this.getBatch());

            }

        } catch (IOException e) {
            ex = e;
            System.out.printf("Error closing test %s \nPath: %s \nReason: %s \n", e.getMessage());

        } catch (Exception e) {
            System.out.printf("Oops, something went wrong while processing the file %s! \n", file_.getName());
            System.out.print(e);
            e.printStackTrace();

            ex = e;
        } finally {
            if (ex != null) ex.printStackTrace();
            eyes.abortIfNotClosed();
        }
    }


    public static boolean supports(File file) {
        return pattern.matcher(file.getName()).matches();
    }

    protected void setDpi(float dpi) {
        this.dpi_ = dpi;
    }

    public String getPdfPassword() {
        return pdfPassword;
    }

    public void setPdfPassword(String pdfPassword) {
        this.pdfPassword = pdfPassword;
    }


    @Override
    public String name() {
        String pagesText = "";
        if (pages_ != null && includePagesInTestName_)
            pagesText = " pages [" + pages_ + "]";
        return file_ == null ? name_ + pagesText : file_.getName() + pagesText;
    }
}
