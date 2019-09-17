package lib;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.RectangleSize;

public class Config {
    public RectangleSize viewport;
    public String appName = "ImageTester";
    public float DocumentConversionDPI = 250;
    public boolean splitSteps = false;
    public String pages = null;
    public String pdfPass = null;
    public boolean includePageNumbers = false;
    public Logger logger = new Logger();
    public EyesUtilitiesConfig eyesUtilsConf;
    public BatchInfo flatBatch = null;
    public String testName = null;

    public void setViewport(String viewport) {
        if (viewport == null) return;
        String[] dims = viewport.split("x");
        if (dims.length != 2)
            throw new RuntimeException("invalid viewport-size, make sure the call is -vs <width>x<height>");
        this.viewport = new RectangleSize(
                Integer.parseInt(dims[0]),
                Integer.parseInt(dims[1]));
    }
}
