package au.gov.dto.dibp.appointments.monitoring;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.endpoint.SystemPublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    @Bean
    public ServletRegistrationBean systemPublicMetricsServlet(SystemPublicMetrics publicMetrics) {
        return new ServletRegistrationBean(createMetricsServlet(publicMetrics), "/monitoring/system");
    }

    private MetricsServlet createMetricsServlet(PublicMetrics publicMetrics) {
        CollectorRegistry collectorRegistry = CollectorRegistry.defaultRegistry;
        MetricRegistry metricRegistry = new MetricRegistry();
        for (Metric<?> metric : publicMetrics.metrics()) {
            Counter counter = metricRegistry.counter(metric.getName());
            counter.dec(counter.getCount());
            counter.inc(Double.valueOf(metric.getValue().toString()).longValue());
        }
        DropwizardExports dropwizardExports = new DropwizardExports(metricRegistry);
//        List<Collector.MetricFamilySamples> metricFamilySamples = dropwizardExports.collect();

        collectorRegistry.register(dropwizardExports);
        return new MetricsServlet(collectorRegistry);
    }
}
