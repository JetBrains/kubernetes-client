/**
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.kubernetes.client.informers.cache;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.utils.ReflectUtils;
import io.fabric8.kubernetes.client.utils.Utils;

import java.util.Collections;
import java.util.List;

/**
 * It basically saves and indexes all the entries.
 *
 * @param <T> type for cache object
 */
public interface Cache<T> extends Indexer<T> {

  // NAMESPACE_INDEX is the default index function for caching objects
  public static final String NAMESPACE_INDEX = "namespace";

  /**
   * It's is a convenient default KeyFunc which know show to make keys for API
   * objects which implement HasMetadata interface. The key uses the format
   * namespace/name unless namespace is empty, then it's just name
   *
   * @param obj specific object
   * @return the key
   */
  public static String metaNamespaceKeyFunc(Object obj) {
    try {
      if( obj == null ) {
        return "";
      }
      ObjectMeta metadata;
      if(obj instanceof String) {
        return (String) obj;
      } else if (obj instanceof ObjectMeta) {
        metadata = (ObjectMeta) obj;
      } else {
        metadata = ReflectUtils.objectMetadata(obj);
        if (metadata == null) {
          throw new RuntimeException("Object is bad :" + obj);
        }
      }

      return namespaceKeyFunc(metadata.getNamespace(), metadata.getName());
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Default index function that indexes based on an object's namespace and name.
   *
   * @see #metaNamespaceKeyFunc
   */
  public static String namespaceKeyFunc(String objectNamespace, String objectName) {
    if (Utils.isNullOrEmpty(objectNamespace)) {
      return objectName;
    }
    return objectNamespace + "/" + objectName;
  }

  /**
   * It is a default index function that indexes based on an object's namespace
   *
   * @param obj the specific object
   * @return the indexed value
   */
  public static List<String> metaNamespaceIndexFunc(Object obj) {
    try {
      ObjectMeta metadata = ReflectUtils.objectMetadata(obj);
      return metadata == null ? Collections.emptyList() : Collections.singletonList(metadata.getNamespace());
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }
}

