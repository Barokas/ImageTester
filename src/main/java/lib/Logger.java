package lib;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.Level;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Logger {
    private final PrintStream out_;
    private boolean debug_;
    private final SimpleDateFormat dateFormatter_ = new SimpleDateFormat("HH:mm:ss");
    Calendar calendar = Calendar.getInstance();

    public Logger() {
        this(System.out, false);
    }

    public Logger(PrintStream out, boolean debug) {
        this.out_ = out;
        this.debug_ = debug;
        // This part disables log4j warnings
        org.apache.log4j.Logger.getRootLogger().setLevel(Level.OFF);
    }

    public void setDebug(boolean debug) {
        this.debug_ = debug;
    }

    public void setDebug() {
        setDebug(true);
    }

    public void printProgress(int curr, int total) {
        out_.printf("[%s/%s] ", curr, total);
    }

    private void printPrefix() {
        if (debug_)
            out_.printf("[%s] [%s] ", dateFormatter_.format(calendar.getTime()), Thread.currentThread().getName());
    }

    public void reportDebug(String format, Object... args) {
        if (!debug_) return;
        printPrefix();
        out_.printf(format, args);
    }

    public void reportDiscovery(File file) {
        if (!debug_) return;
        printPrefix();
        if (file.isDirectory())
            out_.printf("Discovering folder %s \n", file.getAbsolutePath());
        else
            out_.printf("Enqueuing file %s \n", file.getAbsolutePath());
    }

    public void reportResult(ExecutorResult result) {
        printPrefix();
        if (debug_) out_.printf("[%d Msec] ", TimeUnit.NANOSECONDS.toMillis(result.runTimeNs));
        out_.printf("Test finished, %s \n", result.testResult);
    }

    public void reportException(Throwable e) {
        reportException(e, null);
    }

    public void reportException(Throwable e, String filename) {
        printPrefix();
        if (filename != null && !filename.isEmpty())
            out_.printf("File: %s \n", filename);

        switch (e.getClass().getSimpleName()) {
            case "FileNotFoundException":
                out_.printf("The file was not found \n");
                break;
            case "IOException":
                out_.printf("Error, Please check that the file is accessible, readable and not exclusively locked. ");
                out_.printf("%s\n", e.getMessage());
                break;
            case "DocumentException":
            case "RendererException":
                out_.printf("Unable to process document, %s \n", e.getMessage());
                break;
            case "UnsatisfiedLinkError":
                out_.printf("Error, Please make sure tesseract and ghostscript are installed and in path! ");
                out_.printf("%s\n", e.getMessage());
                break;
            default:
                out_.printf("Unexpected error, %s, %s \n", e.getClass().getName(), e.getMessage());
                break;
        }

        if (debug_) {
            e.printStackTrace(out_);
        }
    }

    public void printVersion(String cur_ver) {
        out_.printf("ImageTester version %s \n", cur_ver);
    }

    public void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ImageTester [-k <api-key>] [options]", options);
    }
}
