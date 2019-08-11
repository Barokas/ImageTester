package TestObjects;

import com.applitools.eyes.TestResults;
import com.applitools.eyes.images.Eyes;
import lib.Config;
import lib.Patterns;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class FolderTest extends TestBase {
    private final FilenameFilter imageFilesFilter = (dir, name) -> Patterns.IMAGE.matcher(name).matches();
    private final File[] steps_;

    public FolderTest(File folder, Config conf) {
        super(folder, conf);
        if (!folder.isDirectory())
            throw new RuntimeException("FolderTest object can't process non folder object");
        this.steps_ = folder.listFiles(imageFilesFilter);
    }

    public TestResults run(Eyes eyes) throws Exception {
        eyes.open(appName(), name(), viewport());
        for (File img : steps_) {
            try {
                BufferedImage image = ImageIO.read(img);
                eyes.checkImage(image, img.getName());
                image = null;
            } catch (IOException e) {
                logger().reportException(e, img.getAbsolutePath());
            }
        }
        return eyes.close(false);
    }

    public boolean isEmpty() {
        return steps_.length == 0;
    }
}
