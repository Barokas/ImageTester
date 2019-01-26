package lib.java.com.applitools.ImageTester;

import com.applitools.ImageTester.ImageTester;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.images.Eyes;
import lib.java.com.applitools.ImageTester.TestObjects.Batch;
//import lib.java.com.applitools.ImageTester.TestObjects.PDFPageStep;
import lib.java.com.applitools.ImageTester.TestObjects.PDFPageStep;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * Created by liranbarokas on 25/12/2018.
 */
public class PDFUtilities {
    private File file;
    private String password;
    private PDDocument document;
    private float dpi=300;


    public PDFUtilities(File file, String pdfPassword){
        try {
            document=PDDocument.load(file, pdfPassword);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public PDFUtilities(File file, String pdfPassword, float dpi_){
        this.file=file;
        this.password = pdfPassword;
        this.dpi=dpi_;
        try {
            this.document=PDDocument.load(file, pdfPassword);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PDFUtilities(File file){
        try {
            document=PDDocument.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public PDFUtilities(PDDocument document_){
        document=document_;
    }

    public PDFUtilities(PDDocument document_,float dpi_){
        document=document_;
        dpi=dpi_;
    }

    public void checkPDF(Eyes eyes, String StepNamePrefix , String printPages) throws IOException {
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        List<Integer> pages = setPagesList(document, printPages);
        for (int i = 0; i < pages.size(); i++) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(pages.get(i) - 1, dpi);
            eyes.checkImage(bim, String.format(StepNamePrefix+"%s", pages.get(i)));
        }

    }

    public List<PDFPageStep> getPDFPerPage(String printPages, String AppName, String TestName, RectangleSize viewportSize, BatchInfo batch) throws IOException {
        List<Integer> pages = setPagesList(document, printPages);
        List<PDFPageStep> pdfPageSteps = new LinkedList<>();
        for(Integer page:pages){
            String testName = TestName + " page "+ page.toString();
            pdfPageSteps.add(new PDFPageStep(file,password,(page-1),dpi, AppName, testName, viewportSize, batch));
        }
        return pdfPageSteps;
    }


    public void checkPDFPerPage(String printPages, String AppName, String TestName, RectangleSize viewportSize, BatchInfo batch) throws IOException {
        List<Integer> pages = setPagesList(document, printPages);
        for(Integer page:pages){
            String testName = TestName + " page "+ page.toString();
            ImageTester.parallelRunsHandler.addRunnable(new PDFPageStep(file,password,(page-1),dpi, AppName, testName, viewportSize, batch));
        }
    }


    public void checkPDF(Eyes eyes, String printPages) throws IOException {
        checkPDF(eyes,"Page-",printPages);
    }

    public void checkPDF(Eyes eyes) throws IOException {
        checkPDF(eyes,"Page-",null);
    }



    public static List<Integer> setPagesList(PDDocument document, String pages) throws IOException {
        if (pages != null) return parsePagesToList(pages);
        else {
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                list.add(page + 1);
            }
            return list;
        }
    }

    protected static List<Integer> parsePagesToList(String input) {
        if (input == null) return null;
        ArrayList<Integer> pagesToInclude = new ArrayList<Integer>();
        String[] inputPages = input.split(",");
        for (int i = 0; i < inputPages.length; i++) {
            if (inputPages[i].contains("-")) {
                int left = Integer.valueOf(inputPages[i].split("-")[0]);
                int right = Integer.valueOf(inputPages[i].split("-")[1]);
                if (left <= right) {
                    for (int j = left; j <= right; j++) {
                        pagesToInclude.add(j);
                    }
                } else {
                    for (int j = left; j >= right; j--) {
                        pagesToInclude.add(j);
                    }
                }
            } else {
                pagesToInclude.add(Integer.valueOf(inputPages[i]));
            }
        }
        return pagesToInclude;

    }
}
