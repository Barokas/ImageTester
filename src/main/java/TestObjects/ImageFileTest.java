package TestObjects;

import com.applitools.eyes.TestResults;
import com.applitools.eyes.images.Eyes;
import lib.Config;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageFileTest extends TestBase {
    public ImageFileTest(File file, Config conf) {
        super(file, conf);
    }

    @Override
    public TestResults run(Eyes eyes) throws Exception {
        eyes.open(appName(), name(), viewport());
        BufferedImage image = ImageIO.read(file());
        eyes.checkImage(image, name());
        image = null;
        return eyes.close();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
