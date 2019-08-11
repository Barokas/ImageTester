package TestObjects;

import com.applitools.eyes.TestResults;
import com.applitools.eyes.images.Eyes;
import lib.Config;
import lib.Utils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PdfFileTest extends DocumentTestBase {

    public PdfFileTest(File file, Config conf) {
        super(file, conf);
    }

    public TestResults run(Eyes eyes) throws Exception {
        PDDocument document = null;
        try {
            document = PDDocument.load(file(), config().pdfPass);
            if (pageList_ == null || pageList_.isEmpty())
                pageList_ = Utils.generateRanage(document.getNumberOfPages() + 1, 1);
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            eyes.open(appName(), name(), viewport());
            for (Integer page : pageList_) {
                try {
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(page - 1, config().DocumentConversionDPI);
                    eyes.checkImage(bim, String.format("Page-%s", page));
                } catch (IOException e) {
                    logger().reportException(e, file().getAbsolutePath());
                }
            }
            return eyes.close(false);
        } finally {
            try {
                if (document != null)
                    document.close();
            } catch (Exception e) {
                //Do nothing
            }
        }
    }
}
