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

package org.apache.iotdb.commons.conf;

import org.apache.iotdb.commons.enums.HandleSystemErrorStrategy;
import org.apache.iotdb.commons.exception.BadNodeUrlException;
import org.apache.iotdb.commons.utils.NodeUrlUtils;
import org.apache.iotdb.confignode.rpc.thrift.TGlobalConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

public class CommonDescriptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(CommonDescriptor.class);

  private final CommonConfig config = new CommonConfig();

  private CommonDescriptor() {}

  public static CommonDescriptor getInstance() {
    return CommonDescriptorHolder.INSTANCE;
  }

  private static class CommonDescriptorHolder {

    private static final CommonDescriptor INSTANCE = new CommonDescriptor();

    private CommonDescriptorHolder() {
      // empty constructor
    }
  }

  public CommonConfig getConfig() {
    return config;
  }

  public void initCommonConfigDir(String systemDir) {
    config.setUserFolder(systemDir + File.separator + "users");
    config.setRoleFolder(systemDir + File.separator + "roles");
    config.setProcedureWalFolder(systemDir + File.separator + "procedure");
  }

  public void loadCommonProps(Properties properties) {
    config.setAuthorizerProvider(
        properties.getProperty("authorizer_provider_class", config.getAuthorizerProvider()).trim());
    // if using org.apache.iotdb.db.auth.authorizer.OpenIdAuthorizer, openID_url is needed.
    config.setOpenIdProviderUrl(
        properties.getProperty("openID_url", config.getOpenIdProviderUrl()).trim());
    config.setAdminName(properties.getProperty("admin_name", config.getAdminName()).trim());

    config.setAdminPassword(
        properties.getProperty("admin_password", config.getAdminPassword()).trim());
    config.setEncryptDecryptProvider(
        properties
            .getProperty(
                "iotdb_server_encrypt_decrypt_provider", config.getEncryptDecryptProvider())
            .trim());

    config.setEncryptDecryptProviderParameter(
        properties.getProperty(
            "iotdb_server_encrypt_decrypt_provider_parameter",
            config.getEncryptDecryptProviderParameter()));

    String[] tierTTLStr = new String[config.getTierTTLInMs().length];
    for (int i = 0; i < tierTTLStr.length; ++i) {
      tierTTLStr[i] = String.valueOf(config.getTierTTLInMs()[i]);
    }
    tierTTLStr =
        properties
            .getProperty("default_ttl_in_ms", String.join(IoTDBConstant.TIER_SEPARATOR, tierTTLStr))
            .split(IoTDBConstant.TIER_SEPARATOR);
    long[] tierTTL = new long[tierTTLStr.length];
    for (int i = 0; i < tierTTL.length; ++i) {
      tierTTL[i] = Long.parseLong(tierTTLStr[i]);
      if (tierTTL[i] < 0) {
        tierTTL[i] = Long.MAX_VALUE;
      }
    }
    config.setTierTTLInMs(tierTTL);

    config.setSyncDir(properties.getProperty("dn_sync_dir", config.getSyncDir()).trim());

    config.setWalDirs(
        properties
            .getProperty("dn_wal_dirs", String.join(",", config.getWalDirs()))
            .trim()
            .split(","));

    config.setRpcThriftCompressionEnabled(
        Boolean.parseBoolean(
            properties
                .getProperty(
                    "cn_rpc_thrift_compression_enable",
                    String.valueOf(config.isRpcThriftCompressionEnabled()))
                .trim()));

    config.setConnectionTimeoutInMS(
        Integer.parseInt(
            properties
                .getProperty(
                    "cn_connection_timeout_ms", String.valueOf(config.getConnectionTimeoutInMS()))
                .trim()));

    config.setSelectorNumOfClientManager(
        Integer.parseInt(
            properties
                .getProperty(
                    "cn_selector_thread_nums_of_client_manager",
                    String.valueOf(config.getSelectorNumOfClientManager()))
                .trim()));

    config.setCoreClientNumForEachNode(
        Integer.parseInt(
            properties
                .getProperty(
                    "cn_core_client_count_for_each_node_in_client_manager",
                    String.valueOf(config.getCoreClientNumForEachNode()))
                .trim()));

    config.setMaxClientNumForEachNode(
        Integer.parseInt(
            properties
                .getProperty(
                    "cn_max_client_count_for_each_node_in_client_manager",
                    String.valueOf(config.getMaxClientNumForEachNode()))
                .trim()));

    config.setConnectionTimeoutInMS(
        Integer.parseInt(
            properties
                .getProperty(
                    "dn_connection_timeout_ms", String.valueOf(config.getConnectionTimeoutInMS()))
                .trim()));

    config.setRpcThriftCompressionEnabled(
        Boolean.parseBoolean(
            properties
                .getProperty(
                    "dn_rpc_thrift_compression_enable",
                    String.valueOf(config.isRpcThriftCompressionEnabled()))
                .trim()));

    config.setSelectorNumOfClientManager(
        Integer.parseInt(
            properties
                .getProperty(
                    "dn_selector_thread_nums_of_client_manager",
                    String.valueOf(config.getSelectorNumOfClientManager()))
                .trim()));

    config.setCoreClientNumForEachNode(
        Integer.parseInt(
            properties
                .getProperty(
                    "dn_core_client_count_for_each_node_in_client_manager",
                    String.valueOf(config.getCoreClientNumForEachNode()))
                .trim()));

    config.setMaxClientNumForEachNode(
        Integer.parseInt(
            properties
                .getProperty(
                    "dn_max_client_count_for_each_node_in_client_manager",
                    String.valueOf(config.getMaxClientNumForEachNode()))
                .trim()));

    config.setHandleSystemErrorStrategy(
        HandleSystemErrorStrategy.valueOf(
            properties
                .getProperty(
                    "handle_system_error", String.valueOf(config.getHandleSystemErrorStrategy()))
                .trim()));

    config.setDiskSpaceWarningThreshold(
        Double.parseDouble(
            properties
                .getProperty(
                    "disk_space_warning_threshold",
                    String.valueOf(config.getDiskSpaceWarningThreshold()))
                .trim()));

    config.setTimestampPrecision(
        properties.getProperty("timestamp_precision", config.getTimestampPrecision()).trim());

    String endPointUrl =
        properties.getProperty(
            "target_ml_node_endpoint",
            NodeUrlUtils.convertTEndPointUrl(config.getTargetMLNodeEndPoint()));

    loadPipeProps(properties);
    try {
      config.setTargetMLNodeEndPoint(NodeUrlUtils.parseTEndPointUrl(endPointUrl));
    } catch (BadNodeUrlException e) {
      LOGGER.warn(
          "Illegal target MLNode endpoint url format in config file: {}, use default configuration.",
          endPointUrl);
    }
  }

  private void loadPipeProps(Properties properties) {
    config.setPipeHardlinkTsFileDirName(
        properties.getProperty(
            "pipe_hardlink_tsfile_dir_name", config.getPipeHardlinkTsFileDirName()));

    config.setPipeSubtaskExecutorMaxThreadNum(
        Integer.parseInt(
            properties.getProperty(
                "pipe_subtask_executor_max_thread_num",
                Integer.toString(config.getPipeSubtaskExecutorMaxThreadNum()))));
    if (config.getPipeSubtaskExecutorMaxThreadNum() <= 0) {
      config.setPipeSubtaskExecutorMaxThreadNum(5);
    }
    config.setPipeSubtaskExecutorBasicCheckPointIntervalByConsumedEventCount(
        Integer.parseInt(
            properties.getProperty(
                "pipe_subtask_executor_basic_check_point_interval_by_consumed_event_count",
                String.valueOf(
                    config.getPipeSubtaskExecutorBasicCheckPointIntervalByConsumedEventCount()))));
    config.setPipeSubtaskExecutorBasicCheckPointIntervalByTimeDuration(
        Long.parseLong(
            properties.getProperty(
                "pipe_subtask_executor_basic_check_point_interval_by_time_duration",
                String.valueOf(
                    config.getPipeSubtaskExecutorBasicCheckPointIntervalByTimeDuration()))));
    config.setPipeSubtaskExecutorPendingQueueMaxBlockingTimeMs(
        Long.parseLong(
            properties.getProperty(
                "pipe_subtask_executor_pending_queue_max_blocking_time_ms",
                String.valueOf(config.getPipeSubtaskExecutorPendingQueueMaxBlockingTimeMs()))));

    config.setPipeCollectorAssignerDisruptorRingBufferSize(
        Integer.parseInt(
            properties.getProperty(
                "pipe_collector_assigner_disruptor_ring_buffer_size",
                String.valueOf(config.getPipeCollectorAssignerDisruptorRingBufferSize()))));
    config.setPipeCollectorMatcherCacheSize(
        Integer.parseInt(
            properties.getProperty(
                "pipe_collector_matcher_cache_size",
                String.valueOf(config.getPipeCollectorMatcherCacheSize()))));
    config.setPipeCollectorPendingQueueCapacity(
        Integer.parseInt(
            properties.getProperty(
                "pipe_collector_pending_queue_capacity",
                String.valueOf(config.getPipeCollectorPendingQueueCapacity()))));
    config.setPipeCollectorPendingQueueTabletLimit(
        Integer.parseInt(
            properties.getProperty(
                "pipe_collector_pending_queue_tablet_limit",
                String.valueOf(config.getPipeCollectorPendingQueueTabletLimit()))));

    config.setPipeConnectorReadFileBufferSize(
        Integer.parseInt(
            properties.getProperty(
                "pipe_connector_read_file_buffer_size",
                String.valueOf(config.getPipeConnectorReadFileBufferSize()))));
    config.setPipeConnectorRetryIntervalMs(
        Long.parseLong(
            properties.getProperty(
                "pipe_connector_retry_interval_ms",
                String.valueOf(config.getPipeConnectorRetryIntervalMs()))));
    config.setPipeConnectorPendingQueueSize(
        Integer.parseInt(
            properties.getProperty(
                "pipe_connector_pending_queue_size",
                String.valueOf(config.getPipeConnectorPendingQueueSize()))));
    config.setPipeConnectorSessionId(
        Long.parseLong(
            properties.getProperty(
                "pipe_connector_session_id", String.valueOf(config.getPipeConnectorSessionId()))));

    config.setPipeHeartbeatLoopCyclesForCollectingPipeMeta(
        Integer.parseInt(
            properties.getProperty(
                "pipe_heartbeat_loop_cycles_for_collecting_pipe_meta",
                String.valueOf(config.getPipeHeartbeatLoopCyclesForCollectingPipeMeta()))));
    config.setPipeMetaSyncerInitialSyncDelayMinutes(
        Long.parseLong(
            properties.getProperty(
                "pipe_meta_syncer_initial_sync_delay_minutes",
                String.valueOf(config.getPipeMetaSyncerInitialSyncDelayMinutes()))));
    config.setPipeMetaSyncerSyncIntervalMinutes(
        Long.parseLong(
            properties.getProperty(
                "pipe_meta_syncer_sync_interval_minutes",
                String.valueOf(config.getPipeMetaSyncerSyncIntervalMinutes()))));
  }

  public void loadGlobalConfig(TGlobalConfig globalConfig) {
    config.setDiskSpaceWarningThreshold(globalConfig.getDiskSpaceWarningThreshold());
  }
}
