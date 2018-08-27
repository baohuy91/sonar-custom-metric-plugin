package jp.co.atware.sonar.custommetric;

import org.sonar.api.batch.measure.Metric;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.measure.NewMeasure;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class CustomReportSensor implements Sensor {
    private static final Logger LOGGER = Loggers.get(CustomReportSensor.class);

    private final List<? extends Metric> metrics;

    private final Configuration configuration;

    public CustomReportSensor(Configuration configuration) {
        this(CustomMetrics.METRICS, configuration);
    }

    /**
     * For testing purpose only
     */
    CustomReportSensor(List<? extends Metric> metrics, Configuration configuration) {
        this.metrics = metrics;
        this.configuration = configuration;
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("Read metrics from custom reports");
    }

    @Override
    public void execute(SensorContext context) {
        final Optional<String> reportFilePathOpt = configuration.get(PluginProp.REPORT_FILE_PATH.value());
        if (!reportFilePathOpt.isPresent())
            return;

        final File baseDir = context.fileSystem().baseDir();
        final File reportFile = new File(baseDir, reportFilePathOpt.get());
        if (reportFile.exists())
            saveMeasures(reportFile, context);
    }

    private void saveMeasures(File f, SensorContext context) {
        try (final FileReader fileReader = new FileReader(f)) {
            final BufferedReader reader = new BufferedReader(fileReader);
            String l;
            while ((l = reader.readLine()) != null) {
                final String[] metric = l.split(",");
                final String key = metric[0];
                final String value = metric[1];
                saveMeasure(key, value, context);
            }
        } catch (IOException e) {
            LOGGER.error("Can not read input file", e);
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.error("Metric report file is malformed. Required: [key],[value]", e);
        }
    }

    private void saveMeasure(String key, String value, SensorContext context) {
        for (Metric metric : metrics) {
            if (!metric.key().equals(key))
                continue;

            final NewMeasure newMeasure = context.newMeasure()
                    .forMetric(metric)
                    .on(context.module());
            if (Integer.class.equals(metric.valueType())) {
                newMeasure.withValue(Integer.valueOf(value));
            } else if (Long.class.equals(metric.valueType())) {
                newMeasure.withValue(Long.valueOf(value));
            } else if (Double.class.equals(metric.valueType())) {
                newMeasure.withValue(Double.valueOf(value));
            } else if (Boolean.class.equals(metric.valueType())) {
                newMeasure.withValue(Boolean.valueOf(value));
            } else {
                newMeasure.withValue(value);
            }
            newMeasure.save();
        }
    }
}
