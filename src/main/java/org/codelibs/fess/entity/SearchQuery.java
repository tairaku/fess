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

import java.util.ArrayList;
import java.util.List;

import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.Constants;

public class SearchQuery {
    private String query;

    private final List<String> filterQueryList = new ArrayList<String>();

    private final List<SortField> sortFieldList = new ArrayList<SortField>();

    private String minimumShouldMatch;

    private String defType;

    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    public SearchQuery query(final String query) {
        setQuery(query);
        return this;
    }

    public boolean queryExists() {
        return StringUtil.isNotBlank(query);
    }

    public void addSortField(final String field, final String order) {
        if (StringUtil.isNotBlank(field) && (Constants.ASC.equals(order) || Constants.DESC.equals(order))) {
            final SortField sortField = new SortField();
            sortField.setField(field);
            sortField.setOrder(order);
            sortFieldList.add(sortField);
        }
    }

    public SearchQuery sortField(final String field, final String order) {
        addSortField(field, order);
        return this;
    }

    public SortField[] getSortFields() {
        return sortFieldList.toArray(new SortField[sortFieldList.size()]);
    }

    public void addFilterQuery(final String fq) {
        filterQueryList.add(fq);
    }

    public boolean hasFilterQueries() {
        return !filterQueryList.isEmpty();
    }

    public String[] getFilterQueries() {
        return filterQueryList.toArray(new String[filterQueryList.size()]);
    }

    public String getMinimumShouldMatch() {
        return minimumShouldMatch;
    }

    public void setMinimumShouldMatch(final String minimumShouldMatch) {
        this.minimumShouldMatch = minimumShouldMatch;
    }

    public String getDefType() {
        return defType;
    }

    public void setDefType(final String defType) {
        this.defType = defType;
    }

    public static class SortField {
        private String field;

        private String order;

        public String getField() {
            return field;
        }

        public void setField(final String field) {
            this.field = field;
        }

        public String getOrder() {
            return order;
        }

        public void setOrder(final String order) {
            this.order = order;
        }

        @Override
        public String toString() {
            return "SortField [field=" + field + ", order=" + order + "]";
        }
    }

    @Override
    public String toString() {
        return "SearchQuery [query=" + query + ", filterQueryList=" + filterQueryList + ", sortFieldList=" + sortFieldList
                + ", minimumShouldMatch=" + minimumShouldMatch + "]";
    }
}
