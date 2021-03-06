/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.codelibs.fess.entity;

import java.util.LinkedHashMap;
import java.util.Map;

public class FacetQueryView {
    protected String title;

    protected Map<String, String> queryMap = new LinkedHashMap<String, String>();

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Map<String, String> getQueryMap() {
        return queryMap;
    }

    public void addQuery(final String key, final String query) {
        queryMap.put(key, query);
    }

    @Override
    public String toString() {
        return "FacetQueryView [title=" + title + ", queryMap=" + queryMap + "]";
    }

}
