package jp.co.atware.sonar.custommetric;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.Arrays;
import java.util.List;

/**
 * Define custom metric here
 */
public class CustomMetrics implements Metrics {
	// This is sample metrics, define your own metric here. Check test for more example metrics
	static final List<Metric> METRICS = Arrays.asList(
	);

	@Override
	public List<Metric> getMetrics() {
		return METRICS;
	}
}
