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
package org.apache.iotdb.cluster.utils.nodetool.function;

import org.apache.iotdb.cluster.rpc.thrift.Node;
import org.apache.iotdb.cluster.utils.nodetool.ClusterMonitorMBean;

import io.airlift.airline.Command;

import java.util.Map;
import java.util.Map.Entry;

import static org.apache.iotdb.cluster.utils.nodetool.Printer.msgPrintln;

@Command(name = "status", description = "Print status of all nodes")
public class Status extends NodeToolCmd {

  public static final int LIVE = 0;
  public static final int OFFLINE = 1;
  public static final int JOINING = 2;
  public static final int LEAVING = 3;

  @Override
  public void execute(ClusterMonitorMBean proxy) {
    Map<Node, Integer> statusMap = proxy.getAllNodeStatus();
    if (statusMap == null) {
      msgPrintln(BUILDING_CLUSTER_INFO);
      return;
    }
    msgPrintln(String.format("%-30s  %10s", "Node", "Status"));
    for (Entry<Node, Integer> entry : statusMap.entrySet()) {
      Node node = entry.getKey();
      Integer statusNum = entry.getValue();
      String status;
      if (statusNum.equals(LIVE)) {
        status = "on";
      } else if (statusNum.equals(OFFLINE)) {
        status = "off";
      } else if (statusNum.equals(JOINING)) {
        status = "joining";
      } else {
        status = "leaving";
      }
      msgPrintln(String.format("%-30s->%10s", nodeToString(node), status));
    }
  }
}
