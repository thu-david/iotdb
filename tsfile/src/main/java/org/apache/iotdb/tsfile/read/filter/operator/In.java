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
package org.apache.iotdb.tsfile.read.filter.operator;

import org.apache.iotdb.tsfile.file.metadata.statistics.Statistics;
import org.apache.iotdb.tsfile.read.filter.basic.Filter;
import org.apache.iotdb.tsfile.read.filter.factory.FilterSerializeId;
import org.apache.iotdb.tsfile.read.filter.factory.FilterType;
import org.apache.iotdb.tsfile.utils.ReadWriteIOUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * in clause.
 *
 * @param <T> comparable data type
 */
public class In<T extends Comparable<T>> implements Filter, Serializable {

  private static final long serialVersionUID = 8572705136773595399L;

  protected Set<T> values;

  protected boolean not;

  protected FilterType filterType;

  public In() {}

  public In(Set<T> values, FilterType filterType, boolean not) {
    this.values = values;
    this.filterType = filterType;
    this.not = not;
  }

  @Override
  public boolean satisfy(Statistics statistics) {
    return true;
  }

  @Override
  public boolean allSatisfy(Statistics statistics) {
    return false;
  }

  @Override
  public boolean satisfy(long time, Object value) {
    Object v = filterType == FilterType.TIME_FILTER ? time : value;
    return this.values.contains(v) != not;
  }

  @Override
  public boolean satisfyStartEndTime(long startTime, long endTime) {
    return true;
  }

  @Override
  public boolean containStartEndTime(long startTime, long endTime) {
    return false;
  }

  @Override
  public Filter copy() {
    return new In<>(new HashSet<>(values), filterType, not);
  }

  @Override
  public void serialize(DataOutputStream outputStream) {
    try {
      outputStream.write(getSerializeId().ordinal());
      outputStream.write(filterType.ordinal());
      ReadWriteIOUtils.write(not, outputStream);
      ReadWriteIOUtils.write(values.size(), outputStream);
      for (T value : values) {
        ReadWriteIOUtils.writeObject(value, outputStream);
      }
    } catch (IOException ignored) {
      // ignored
    }
  }

  @Override
  public void deserialize(ByteBuffer buffer) {
    filterType = FilterType.values()[buffer.get()];
    not = ReadWriteIOUtils.readBool(buffer);
    int size = ReadWriteIOUtils.readInt(buffer);
    values = new HashSet<>(size);
    for (int i = 0; i < size; i++) {
      values.add((T) ReadWriteIOUtils.readObject(buffer));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof In)) {
      return false;
    }
    In<?> in = (In<?>) o;
    return in.filterType == filterType && in.values.equals(values) && in.not == not;
  }

  @Override
  public int hashCode() {
    return Objects.hash(values, not, filterType);
  }

  @Override
  public String toString() {
    List<T> valueList = new ArrayList<>(values);
    Collections.sort(valueList);
    return filterType + (not ? " not in " : " in ") + valueList;
  }

  @Override
  public FilterSerializeId getSerializeId() {
    return FilterSerializeId.IN;
  }

  @Override
  public Filter reverse() {
    return new In<>(new HashSet<>(values), filterType, !not);
  }

  public Set<T> getValues() {
    return values;
  }
}
