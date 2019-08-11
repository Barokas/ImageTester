package lib;

import com.applitools.eyes.FileLogger;
import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.images.Eyes;
import org.apache.commons.lang3.StringUtils;

public class EyesFactory {
    private final String version;
    private String apiKey;
    private String serverUrl;
    private String matchLevel;
    private String[] proxy;
    private String branch;
    private String parentBranch;
    private String envName;
    private String logFilename;
    private String hostOs;
    private String hostApp;
    private boolean saveFailed;

    public EyesFactory(String ver) {
        this.version = ver;
    }

    public Eyes build() throws RuntimeException {
        Eyes eyes = new Eyes() {
            @Override
            public String getBaseAgentId() {
                return String.format("ImageTester/%s [%s]", version, super.getBaseAgentId());
            }
        };
        eyes.setApiKey(this.apiKey);
        eyes.setSaveFailedTests(saveFailed);

        if (StringUtils.isNotBlank(this.serverUrl))
            eyes.setServerUrl(this.serverUrl);
        if (StringUtils.isNotBlank(this.matchLevel))
            eyes.setMatchLevel(Utils.parseEnum(MatchLevel.class, this.matchLevel));
        if (StringUtils.isNotBlank(this.branch))
            eyes.setBranchName(this.branch);
        if (StringUtils.isNotBlank(this.parentBranch))
            eyes.setParentBranchName(this.parentBranch);
        if (StringUtils.isNotBlank(this.envName))
            eyes.setBaselineEnvName(this.envName);
        if (StringUtils.isNotBlank(this.hostOs))
            eyes.setHostOS(this.hostOs);
        if (StringUtils.isNotBlank(this.hostApp))
            eyes.setHostApp(this.hostApp);

        if (StringUtils.isNotBlank(this.logFilename))
            eyes.setLogHandler(new FileLogger(this.logFilename, true, true));


        if (this.proxy != null && this.proxy.length > 0)
            if (proxy.length == 1)
                eyes.setProxy(new ProxySettings(proxy[0]));
            else if (proxy.length == 3) {
                eyes.setProxy(new ProxySettings(proxy[0], proxy[1], proxy[2]));
            } else
                throw new RuntimeException("Proxy setting are invalid");

        if (StringUtils.isNotBlank(this.parentBranch) && StringUtils.isBlank(this.branch))
            throw new RuntimeException("Parent Branches (pb) should be combined with branches (br).");

        return eyes;
    }

    public EyesFactory apiKey(String key) {
        this.apiKey = key;
        return this;
    }

    public EyesFactory serverUrl(String uri) {
        this.serverUrl = uri;
        return this;
    }

    public EyesFactory matchLevel(String ml) {
        this.matchLevel = ml;
        return this;
    }

    public EyesFactory proxy(String[] proxy) {
        this.proxy = proxy;
        return this;
    }

    public EyesFactory branch(String branch) {
        this.branch = branch;
        return this;
    }

    public EyesFactory parentBranch(String parentBranch) {
        this.parentBranch = parentBranch;
        return this;
    }

    public EyesFactory baselineEnvName(String envName) {
        this.envName = envName;
        return this;
    }

    public EyesFactory logFile(String filename) {
        this.logFilename = filename;
        return this;
    }

    public EyesFactory hostOs(String os) {
        this.hostOs = os;
        return this;
    }

    public EyesFactory hostApp(String app) {
        this.hostApp = app;
        return this;
    }

    public EyesFactory saveFaliedTests(boolean saveFailed) {
        this.saveFailed = saveFailed;
        return this;
    }
}
