/*
 * Copyright (C) 2026 Contentful GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.contentful.java.cma.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Webhook filter constraint. Mirrors the JSON shape Contentful expects in the
 * {@code filters} array of a webhook definition. Currently supports the
 * {@code equals} operator. The wire format is positional and heterogeneous:
 * {@code [{"doc": "<path>"}, "<value>"]}.
 */
public class CMAWebhookFilter {

    // Field name is the JSON property Contentful expects; do not rename without @SerializedName.
    private List<Object> equals;

    private CMAWebhookFilter() {
    }

    /**
     * Build an {@code equals} constraint matching the given JSON-pointer-like
     * document path against a string value.
     *
     * @param path  e.g. {@code sys.environment.sys.id}
     * @param value value to compare against
     */
    public static CMAWebhookFilter equals(String path, String value) {
        Map<String, String> doc = new LinkedHashMap<>();
        doc.put("doc", path);

        CMAWebhookFilter filter = new CMAWebhookFilter();
        filter.equals = new ArrayList<>();
        filter.equals.add(doc);
        filter.equals.add(value);
        return filter;
    }

    public List<Object> getEquals() {
        return equals == null ? null : new ArrayList<>(equals);
    }

    @Override public String toString() {
        return "CMAWebhookFilter { equals = " + equals + " }";
    }
}
