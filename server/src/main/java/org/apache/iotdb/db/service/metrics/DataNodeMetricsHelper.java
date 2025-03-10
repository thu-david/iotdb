/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.db.service.metrics;

import org.apache.iotdb.commons.concurrent.DataNodeThreadModule;
import org.apache.iotdb.commons.concurrent.ThreadName;
import org.apache.iotdb.commons.concurrent.ThreadPoolMetrics;
import org.apache.iotdb.commons.conf.IoTDBConstant;
import org.apache.iotdb.commons.service.metric.MetricService;
import org.apache.iotdb.commons.service.metric.PerformanceOverviewMetrics;
import org.apache.iotdb.db.mpp.metric.DataExchangeCostMetricSet;
import org.apache.iotdb.db.mpp.metric.DataExchangeCountMetricSet;
import org.apache.iotdb.db.mpp.metric.DriverSchedulerMetricSet;
import org.apache.iotdb.db.mpp.metric.QueryExecutionMetricSet;
import org.apache.iotdb.db.mpp.metric.QueryPlanCostMetricSet;
import org.apache.iotdb.db.mpp.metric.QueryRelatedResourceMetricSet;
import org.apache.iotdb.db.mpp.metric.QueryResourceMetricSet;
import org.apache.iotdb.db.mpp.metric.SeriesScanCostMetricSet;
import org.apache.iotdb.metrics.metricsets.UpTimeMetrics;
import org.apache.iotdb.metrics.metricsets.cpu.CpuUsageMetrics;
import org.apache.iotdb.metrics.metricsets.disk.DiskMetrics;
import org.apache.iotdb.metrics.metricsets.jvm.JvmMetrics;
import org.apache.iotdb.metrics.metricsets.logback.LogbackMetrics;
import org.apache.iotdb.metrics.metricsets.net.NetMetrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataNodeMetricsHelper {
  /** Bind predefined metric sets into DataNode. */
  public static void bind() {
    MetricService.getInstance().addMetricSet(new UpTimeMetrics());
    MetricService.getInstance().addMetricSet(new JvmMetrics());
    MetricService.getInstance().addMetricSet(ThreadPoolMetrics.getInstance());
    MetricService.getInstance().addMetricSet(new LogbackMetrics());
    MetricService.getInstance().addMetricSet(FileMetrics.getInstance());
    MetricService.getInstance().addMetricSet(CompactionMetrics.getInstance());
    MetricService.getInstance().addMetricSet(new ProcessMetrics());
    MetricService.getInstance().addMetricSet(new SystemMetrics(true));
    MetricService.getInstance().addMetricSet(new DiskMetrics(IoTDBConstant.DN_ROLE));
    MetricService.getInstance().addMetricSet(new NetMetrics(IoTDBConstant.DN_ROLE));
    initCpuMetrics();
    MetricService.getInstance().addMetricSet(WritingMetrics.getInstance());

    // bind query related metrics
    MetricService.getInstance().addMetricSet(QueryPlanCostMetricSet.getInstance());
    MetricService.getInstance().addMetricSet(SeriesScanCostMetricSet.getInstance());
    MetricService.getInstance().addMetricSet(QueryExecutionMetricSet.getInstance());
    MetricService.getInstance().addMetricSet(QueryResourceMetricSet.getInstance());
    MetricService.getInstance().addMetricSet(DataExchangeCostMetricSet.getInstance());
    MetricService.getInstance().addMetricSet(DataExchangeCountMetricSet.getInstance());
    MetricService.getInstance().addMetricSet(DriverSchedulerMetricSet.getInstance());
    MetricService.getInstance().addMetricSet(new QueryRelatedResourceMetricSet());

    // bind performance overview related metrics
    MetricService.getInstance().addMetricSet(PerformanceOverviewMetrics.getInstance());
  }

  private static void initCpuMetrics() {
    List<String> threadModules = new ArrayList<>();
    Arrays.stream(DataNodeThreadModule.values()).forEach(x -> threadModules.add(x.toString()));
    List<String> pools = new ArrayList<>();
    Arrays.stream(ThreadName.values()).forEach(x -> pools.add(x.name()));
    MetricService.getInstance()
        .addMetricSet(
            new CpuUsageMetrics(
                threadModules,
                pools,
                x -> ThreadName.getModuleTheThreadBelongs(x).toString(),
                x -> ThreadName.getThreadPoolTheThreadBelongs(x).name()));
  }
}
