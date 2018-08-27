package jp.co.atware.sonar.custommetric;

import org.sonar.api.Plugin;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.measures.Metric;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomMetricPlugin implements Plugin {
    private static final String CATEGORY = "Custom metrics";

    private static final String SUBCATEGORY = "Custom metric reader from file";

    @Override
    public void define(Context context) {
        final List<String> metricKeys = CustomMetrics.METRICS.stream()
                .map(Metric::getKey)
                .collect(Collectors.toList());

        final String metricsStr = String.join(",", metricKeys);
        final PropertyDefinition propertyDefinition = PropertyDefinition.builder(PluginProp.REPORT_FILE_PATH.value())
                .name("Report file path")
                .description("Relative report file path in source code. " +
                        "E.g. acceptance-test/build/reports/engine/performance.csv. " +
                        "CSV file format is '[metric key],[value]'. " +
                        "Available metrics are: " + metricsStr)
                .type(PropertyType.STRING)
                .category(CATEGORY)
                .subCategory(SUBCATEGORY)
                .index(0)
                .build();

        context.addExtensions(Arrays.asList(
                propertyDefinition,
                CustomMetrics.class,
                CustomReportSensor.class
        ));
    }
}
