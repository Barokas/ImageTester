import com.applitools.ImageTester.ImageTester;
import com.applitools.eyes.images.Eyes;

public class TestBase {

    public void run(String folder) {
        String cmd = String.format("-k %s -f %s", System.getenv("APPLITOOLS_API_KEY"), folder);
        ImageTester.main(cmd.split(" "));
    }

    public Eyes getEyes() {
        return getEyes(false);
    }

    public Eyes getEyes(boolean disabled) {
        Eyes eyes = new Eyes();
        eyes.setIsDisabled(disabled);
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
        return eyes;
    }
}
