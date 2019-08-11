import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.MatchLevel;
import lib.*;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class ImageTester {
    private static final String cur_ver = "1.0.0.0";
    private static final String eyes_utils = "EyesUtilities.jar";

    private static boolean eyes_utils_enabled = false;

    public static void main(String[] args) {
        PrintStream out = System.out;
        eyes_utils_enabled = new File(eyes_utils).exists();
        CommandLineParser parser = new DefaultParser();
        Options options = getOptions();
        Logger logger = new Logger();
        try {
            CommandLine cmd = parser.parse(options, args);
            // Eyes factory
            EyesFactory factory
                    = new EyesFactory(cur_ver)
                    .apiKey(cmd.getOptionValue("k", System.getenv("APPLITOOLS_API_KEY")))
                    .serverUrl(cmd.getOptionValue("s", null))
                    .matchLevel(cmd.getOptionValue("ml", null))
                    .proxy(cmd.getOptionValues("p"))
                    .branch(cmd.getOptionValue("br", null))
                    .parentBranch(cmd.getOptionValue("pb", null))
                    .baselineEnvName(cmd.getOptionValue("bn", null))
                    .logFile(cmd.getOptionValue("lf", null))
                    .hostOs(cmd.getOptionValue("os", null))
                    .hostApp(cmd.getOptionValue("ap"))
                    .saveFaliedTests(cmd.hasOption("as"));

            Config config = new Config();
            config.splitSteps = cmd.hasOption("st");
            config.logger = logger;
            config.logger.setDebug(cmd.hasOption("debug"));
            config.appName = cmd.getOptionValue("a", "ImageTester");
            config.DocumentConversionDPI = Float.valueOf(cmd.getOptionValue("di", "250"));
            config.pdfPass = cmd.getOptionValue("pp", null);
            config.pages = cmd.getOptionValue("sp", null);
            config.includePageNumbers = cmd.hasOption("pn");
            config.setViewport(cmd.getOptionValue("vs", null));

            String ciJobName = System.getenv("JOB_NAME");
            String ciApplitoolsBatchId = System.getenv("APPLITOOLS_BATCH_ID");
            if (Utils.ne(ciJobName, ciApplitoolsBatchId)) {
                config.flatBatch = new BatchInfo(ciJobName);
                config.flatBatch.setId(ciApplitoolsBatchId);
            } else if (cmd.hasOption("fb"))
                config.flatBatch = new BatchInfo(cmd.getOptionValue("fb"));

            File root = new File(cmd.getOptionValue("f", "."));
            int maxThreads = Integer.parseInt(cmd.getOptionValue("th", "3"));

            // Tests executor
            TestExecutor executor = new TestExecutor(maxThreads, factory, config);

            // Suite
            Suite suite = Suite.create(root.getCanonicalFile(), config, executor);

            // EyesUtilities config
            if (eyes_utils_enabled)
                config.eyesUtilsConf = new EyesUtilitiesConfig(cmd);

            if (suite == null) {
                System.out.printf("Nothing to test!\n");
                System.exit(0);
            }

            suite.run();

        } catch (ParseException e) {
            out.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ImageTester [-k <api-key>] [options]", options);
        } catch (IOException e) {
            logger.reportException(e);
            System.exit(-1);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ImageTester [-k <api-key>] [options]", options);
        }
    }

    //k,a,f,p,s,ml,bd,pb,bn,vs,lf,as,os,ap,di,sp,pn,pp,th
    private static Options getOptions() {
        Options options = new Options();

        options.addOption(Option.builder("k")
                .longOpt("apiKey")
                .desc("Applitools api key")
                .hasArg().argName("apikey")
                .build());

        options.addOption(Option.builder("a")
                .longOpt("AppName")
                .desc("Set own application name, default: ImageTester")
                .hasArg()
                .argName("name")
                .build());

        options.addOption(Option.builder("f")
                .longOpt("folder")
                .desc("Set the root folder to start the analysis, default: \\.")
                .hasArg()
                .argName("path")
                .build());

        options.addOption(Option.builder("p")
                .longOpt("proxy")
                .desc("Set proxy address")
                .numberOfArgs(3)
                .optionalArg(true)
                .valueSeparator(';') //; and not : to avoid split of the http: part.
                .argName("url [;user;password]")
                .build()
        );

        options.addOption(Option.builder("s")
                .longOpt("server")
                .desc("Set Applitools server url")
                .hasArg()
                .argName("url")
                .build()
        );

        options.addOption(Option.builder("ml")
                .longOpt("matchLevel")
                .desc(String.format("Set match level to one of [%s], default = Strict", Utils.getEnumValues(MatchLevel.class)))
                .hasArg()
                .argName("level")
                .build());

        options.addOption(Option.builder("br")
                .longOpt("branch")
                .desc("Set branch name")
                .hasArg()
                .argName("name")
                .build());

        options.addOption(Option.builder("pb")
                .longOpt("parentBranch")
                .desc("Set parent branch name, optional when working with branches")
                .hasArg()
                .argName("name")
                .build());

        options.addOption(Option.builder("bn")
                .longOpt("baseline")
                .desc("Set baseline name")
                .hasArg()
                .argName("name")
                .build());

        options.addOption(Option.builder("vs")
                .longOpt("viewportsize")
                .desc("Declare viewport size identifier <width>x<height> ie. 1000x600, if not set,default will be the first image in every test")
                .hasArg()
                .argName("size")
                .build());

        options.addOption(Option.builder("lf")
                .longOpt("logFile")
                .desc("Specify Applitools log-file")
                .hasArg()
                .argName("file")
                .build());

        options.addOption(Option.builder("as")
                .longOpt("autoSave")
                .desc("Automatically save failed tests. Waring, might save buggy baselines without human inspection. ")
                .hasArg(false)
                .build());

        options.addOption(Option.builder("os")
                .longOpt("hostOs")
                .desc("Set OS identifier for the screens under test")
                .hasArg()
                .argName("os")
                .build());

        options.addOption(Option.builder("ap")
                .longOpt("hostApp")
                .desc("Set Host-app identifier for the screens under test")
                .hasArg()
                .argName("app")
                .build());
        options.addOption(Option.builder("di")
                .longOpt("dpi")
                .desc("PDF conversion dots per inch parameter default value 300")
                .hasArg()
                .argName("Dpi")
                .build());
        options.addOption(Option.builder("sp")
                .longOpt("selectedPages")
                .desc("Document pages to validate, default is the entire document")
                .hasArg()
                .argName("Pages")
                .build());
        options.addOption(Option.builder("pn")
                .longOpt("pageNumbers")
                .desc("Include page numbers on document with selected pages (sp)")
                .hasArg(false)
                .build());
        options.addOption(Option.builder("st")
                .longOpt("split")
                .desc("Split tests to single-step tests")
                .hasArg(false)
                .build());
        options.addOption(Option.builder("debug")
                .hasArg(false)
                .desc("Turn on debug prints")
                .build());
        options.addOption(Option.builder("pp")
                .longOpt("PDFPassword")
                .desc("PDF Password")
                .hasArg()
                .argName("Password")
                .build());
        options.addOption(Option.builder("th")
                .longOpt("threads")
                .desc("Specify how many threads will be running the suite")
                .argName("units")
                .build());
        options.addOption(Option.builder("fb")
                .longOpt("flatbatch")
                .desc("Aggregate all test results in a single batch (aka flat-batch)")
                .argName("name")
                .build());
        if (eyes_utils_enabled) {
            System.out.printf("%s is integrated, extra features are available. \n", eyes_utils);
            EyesUtilitiesConfig.injectOptions(options);
        }
        return options;
    }
}
