package jp.co.atware.sonar.custommetric;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.measure.Measure;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class CustomReportSensorTest {
    private static final String BASE_DIR = "tmp";

    private File file;

    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        final String reportFilePath = "acceptance-test/build/reports/engine/performance.csv";
        file = new File(BASE_DIR, reportFilePath);
        file.getParentFile().mkdirs();
        final PrintWriter printWriter = new PrintWriter(file);
        printWriter.println("metricId,value");
        printWriter.println(INTEGER_METRIC.key() + ",80");
        printWriter.println(MILLIS_METRIC.key() + ",90");
        printWriter.println(PERCENT_METRIC.key() + ",70.05");
        printWriter.flush();
        printWriter.close();

        configuration = new MapSettings()
                .setProperty(PluginProp.REPORT_FILE_PATH.value(), reportFilePath)
                .asConfig();
    }

    @After
    public void tearDown() throws Exception {
        file.delete();
    }

    @Test
    public void execute_WithMatchingPattern_ExpectValueRegistered() throws Exception {
        final SensorContextTester contextTester = SensorContextTester.create(new File(BASE_DIR));

        final CustomReportSensor sut = new CustomReportSensor(
                Arrays.asList(INTEGER_METRIC, MILLIS_METRIC, PERCENT_METRIC), configuration);
        sut.execute(contextTester);

        final Measure<Integer> intMeasure = contextTester.measure(contextTester.module().key(), INTEGER_METRIC.key());
        final Measure<Long> millisMeasure = contextTester.measure(contextTester.module().key(), MILLIS_METRIC.key());
        final Measure<Double> percentMeasure = contextTester.measure(contextTester.module().key(),
                PERCENT_METRIC.key());
        assertEquals((Integer) 80, intMeasure.value());
        assertEquals((Long) 90L, millisMeasure.value());
        assertEquals((Double) 70.05, percentMeasure.value());
    }


    static final Metric<Integer> INTEGER_METRIC = new Metric.Builder(
            "int_metric", "Integer metric", Metric.ValueType.INT)
            .setDirection(Metric.DIRECTION_WORST)
            .setQualitative(true)
            .setDomain(CoreMetrics.DOMAIN_RELIABILITY)
            .create();

    static final Metric<Long> MILLIS_METRIC = new Metric.Builder(
            "millis_metric", "Integer metric", Metric.ValueType.MILLISEC)
            .setDirection(Metric.DIRECTION_WORST)
            .setQualitative(true)
            .setDomain(CoreMetrics.DOMAIN_RELIABILITY)
            .create();

    static final Metric<Double> PERCENT_METRIC = new Metric.Builder(
            "double_metric", "Double metric", Metric.ValueType.PERCENT)
            .setDirection(Metric.DIRECTION_WORST)
            .setQualitative(false)
            .setDomain(CoreMetrics.DOMAIN_RELIABILITY)
            .create();
}