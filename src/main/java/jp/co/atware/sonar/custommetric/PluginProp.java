package jp.co.atware.sonar.custommetric;

public enum PluginProp {
    REPORT_FILE_PATH("custom.metrics.reportFilePath");

    private final String value;

    PluginProp(String value) {
        this.value = value;
    }

    public String value() {
       return this.value;
    }
}
