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

package org.codelibs.fess.helper;

/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.InvalidQueryException;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.MoreLikeThisInfo;
import org.codelibs.fess.entity.SearchQuery;
import org.codelibs.fess.entity.SearchQuery.SortField;
import org.codelibs.fess.util.QueryUtil;
import org.codelibs.fess.util.SearchParamMap;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.seasar.struts.util.RequestUtil;

public class QueryHelper implements Serializable {

    private static final String SCORE_FIELD = "score";

    private static final String INURL_FIELD = "inurl";

    private static final String NOT_ = "NOT ";

    private static final String AND = "AND";

    private static final String OR = "OR";

    private static final String NOT = "NOT";

    private static final String _TO_ = " TO ";

    private static final String _OR_ = " OR ";

    private static final String _AND_ = " AND ";

    private static final String DEFAULT_OPERATOR = _AND_;

    private static final long serialVersionUID = 1L;

    @Binding(bindingType = BindingType.MAY)
    @Resource
    protected RoleQueryHelper roleQueryHelper;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected FieldHelper fieldHelper;

    @Binding(bindingType = BindingType.MAY)
    @Resource
    protected KeyMatchHelper keyMatchHelper;

    protected Set<String> apiResponseFieldSet;

    protected Set<String> highlightFieldSet = new HashSet<>();

    protected String[] responseFields;

    protected String[] cacheResponseFields;

    protected String[] responseDocValuesFields;

    protected String[] highlightingFields;

    protected String[] searchFields;

    protected String[] facetFields;

    protected String sortPrefix = "sort:";

    protected String[] supportedSortFields;

    protected String[] supportedMltFields;

    protected String[] supportedAnalysisFields;

    protected int highlightSnippetSize = 5;

    protected String shards;

    protected boolean useBigram = true;

    protected String additionalQuery;

    protected int maxFilterQueriesForRole = Integer.MAX_VALUE;

    protected int timeAllowed = -1;

    protected Map<String, String[]> requestParameterMap = new HashMap<String, String[]>();

    protected String additionalGeoQuery;

    protected Map<String, String> fieldLanguageMap = new HashMap<String, String>();

    protected int maxSearchResultOffset = 100000;

    protected List<SortField> defaultSortFieldList = new ArrayList<SortField>();

    protected String highlightingPrefix = "hl_";

    protected String minimumShouldMatch = "100%";

    protected String defType = "edismax";

    protected FacetInfo defaultFacetInfo;

    protected MoreLikeThisInfo defaultMoreLikeThisInfo;

    protected GeoInfo defaultGeoInfo;

    protected String defaultQueryLanguage;

    protected Map<String, String[]> additionalQueryParamMap = new HashMap<String, String[]>();

    protected Map<String, String> fieldBoostMap = new HashMap<String, String>();

    @InitMethod
    public void init() {
        if (responseFields == null) {
            responseFields =
                    new String[] { SCORE_FIELD, fieldHelper.idField, fieldHelper.docIdField, fieldHelper.boostField,
                            fieldHelper.contentLengthField, fieldHelper.hostField, fieldHelper.siteField, fieldHelper.lastModifiedField,
                            fieldHelper.mimetypeField, fieldHelper.filetypeField, fieldHelper.createdField, fieldHelper.titleField,
                            fieldHelper.digestField, fieldHelper.urlField, fieldHelper.clickCountField, fieldHelper.favoriteCountField,
                            fieldHelper.configIdField, fieldHelper.langField, fieldHelper.hasCacheField };
        }
        if (cacheResponseFields == null) {
            cacheResponseFields =
                    new String[] { SCORE_FIELD, fieldHelper.idField, fieldHelper.docIdField, fieldHelper.boostField,
                            fieldHelper.contentLengthField, fieldHelper.hostField, fieldHelper.siteField, fieldHelper.lastModifiedField,
                            fieldHelper.mimetypeField, fieldHelper.filetypeField, fieldHelper.createdField, fieldHelper.titleField,
                            fieldHelper.digestField, fieldHelper.urlField, fieldHelper.clickCountField, fieldHelper.favoriteCountField,
                            fieldHelper.configIdField, fieldHelper.langField, fieldHelper.cacheField };
        }
        if (responseDocValuesFields == null) {
            responseDocValuesFields = new String[] { fieldHelper.clickCountField, fieldHelper.favoriteCountField };
        }
        if (highlightingFields == null) {
            highlightingFields = new String[] { fieldHelper.contentField };
        }
        if (searchFields == null) {
            searchFields =
                    new String[] { INURL_FIELD, fieldHelper.urlField, fieldHelper.docIdField, fieldHelper.hostField,
                            fieldHelper.titleField, fieldHelper.contentField, fieldHelper.contentLengthField,
                            fieldHelper.lastModifiedField, fieldHelper.mimetypeField, fieldHelper.filetypeField, fieldHelper.labelField,
                            fieldHelper.segmentField, fieldHelper.clickCountField, fieldHelper.favoriteCountField, fieldHelper.langField };
        }
        if (facetFields == null) {
            facetFields =
                    new String[] { fieldHelper.urlField, fieldHelper.hostField, fieldHelper.titleField, fieldHelper.contentField,
                            fieldHelper.contentLengthField, fieldHelper.lastModifiedField, fieldHelper.mimetypeField,
                            fieldHelper.filetypeField, fieldHelper.labelField, fieldHelper.segmentField };
        }
        if (supportedSortFields == null) {
            supportedSortFields =
                    new String[] { fieldHelper.createdField, fieldHelper.contentLengthField, fieldHelper.lastModifiedField,
                            fieldHelper.clickCountField, fieldHelper.favoriteCountField };
        }
        if (supportedMltFields == null) {
            supportedMltFields = new String[] { fieldHelper.contentField, "content_ja" };
        }
        if (supportedAnalysisFields == null) {
            supportedAnalysisFields = new String[] { fieldHelper.contentField, "content_ja" };
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.helper.QueryHelper#build(java.lang.String)
     */
    public SearchQuery build(final String query, final boolean envCondition) {
        String q;
        if (envCondition && additionalQuery != null && StringUtil.isNotBlank(query)) {
            q = query + " " + additionalQuery;
        } else {
            q = query;
        }

        final SearchQuery searchQuery = buildQuery(q);
        if (!searchQuery.queryExists()) {
            searchQuery.query(StringUtil.EMPTY);
        }

        if (!envCondition) {
            return searchQuery;
        }

        if (keyMatchHelper != null) {
            final List<String> docIdQueryList = keyMatchHelper.getDocIdQueryList();
            if (docIdQueryList != null && searchQuery.queryExists()) {
                final String originalQuery = searchQuery.getQuery();
                final StringBuilder queryBuf = new StringBuilder(originalQuery.length() + 100);
                queryBuf.append(originalQuery);
                for (final String docIdQuery : docIdQueryList) {
                    queryBuf.append(_OR_);
                    queryBuf.append(docIdQuery);
                }
                searchQuery.setQuery(queryBuf.toString());
            }
        }

        if (roleQueryHelper != null) {
            final Set<String> roleSet = roleQueryHelper.build();
            if (roleSet.size() > maxFilterQueriesForRole) {
                // add query
                final String sq = searchQuery.getQuery();
                final StringBuilder queryBuf = new StringBuilder(255);
                final boolean hasQueries = sq.contains(_AND_) || sq.contains(_OR_);
                if (hasQueries) {
                    queryBuf.append('(');
                }
                queryBuf.append(sq);
                if (hasQueries) {
                    queryBuf.append(')');
                }
                queryBuf.append(_AND_);
                if (roleSet.size() > 1) {
                    queryBuf.append('(');
                }
                queryBuf.append(getRoleQuery(roleSet));
                if (roleSet.size() > 1) {
                    queryBuf.append(')');
                }
                searchQuery.query(queryBuf.toString());
            } else if (!roleSet.isEmpty()) {
                // add filter query
                searchQuery.addFilterQuery(getRoleQuery(roleSet));
            }
        }
        return searchQuery;
    }

    private String getRoleQuery(final Set<String> roleList) {
        final StringBuilder queryBuf = new StringBuilder(255);
        boolean isFirst = true;
        for (final String role : roleList) {
            if (isFirst) {
                isFirst = false;
            } else {
                queryBuf.append(_OR_);

            }
            queryBuf.append(fieldHelper.roleField);
            queryBuf.append(':');
            queryBuf.append(QueryUtil.escapeValue(role));
        }
        return queryBuf.toString();
    }

    protected SearchQuery buildQuery(final String query) {
        final Map<String, String> sortFieldMap = new LinkedHashMap<String, String>();
        final List<String> highLightQueryList = new ArrayList<String>();
        final Map<String, List<String>> fieldLogMap = new HashMap<String, List<String>>();
        final SearchQuery searchQuery = new SearchQuery();

        final String q = buildQuery(query, sortFieldMap, highLightQueryList, fieldLogMap);
        String solrQuery;
        if (q == null || "()".equals(q)) {
            solrQuery = StringUtil.EMPTY;
        } else {
            solrQuery = unbracketQuery(q);
        }
        searchQuery.setQuery(solrQuery);

        searchQuery.setMinimumShouldMatch(minimumShouldMatch);
        searchQuery.setDefType(defType);

        for (final Map.Entry<String, String> entry : sortFieldMap.entrySet()) {
            searchQuery.addSortField(entry.getKey(), entry.getValue());
        }
        // set queries to request for HighLight
        final HttpServletRequest request = RequestUtil.getRequest();
        if (request != null) {
            request.setAttribute(Constants.HIGHLIGHT_QUERIES, highLightQueryList.toArray(new String[highLightQueryList.size()]));
            request.setAttribute(Constants.FIELD_LOGS, fieldLogMap);
        }
        return searchQuery;
    }

    protected String unbracketQuery(final String query) {
        if (query.startsWith("(") && query.endsWith(")")) {
            int count = 0;
            int depth = 0;
            int escape = 0;
            for (int i = 0; i < query.length(); i++) {
                final char c = query.charAt(i);
                if (c == '\\') {
                    escape++;
                } else {
                    if (c == '(' && escape % 2 == 0) {
                        if (depth == 0) {
                            count++;
                        }
                        depth++;
                    } else if (c == ')' && escape % 2 == 0) {
                        depth--;
                    }
                    escape = 0;
                }
            }
            if (depth == 0 && count == 1) {
                return unbracketQuery(query.substring(1, query.length() - 1));
            }
        }
        return query;

    }

    protected String buildQuery(final String query, final Map<String, String> sortFieldMap, final List<String> highLightQueryList,
            final Map<String, List<String>> fieldLogMap) {
        final List<QueryPart> queryPartList = splitQuery(query, sortFieldMap, highLightQueryList, fieldLogMap);
        if (queryPartList == null || queryPartList.isEmpty()) {
            return null;
        }

        final StringBuilder queryBuf = new StringBuilder(255);
        final List<String> notOperatorList = new ArrayList<String>();
        final String defaultOperator = getDefaultOperator();
        String operator = defaultOperator;
        boolean notOperatorFlag = false;
        int queryOperandCount = 0;
        int contentOperandCount = 0;
        final String queryLanguage = getQueryLanguage();
        for (final QueryPart queryPart : queryPartList) {
            if (queryPart.isParsed()) {
                if (queryBuf.length() > 0) {
                    queryBuf.append(operator);
                }
                queryBuf.append(queryPart.getValue());
                continue;
            }
            final String value = queryPart.getValue();
            boolean nonPrefix = false;
            // check prefix
            for (final String field : searchFields) {
                String prefix = field + ":";
                if (value.startsWith(prefix) && value.length() != prefix.length()) {
                    if (queryBuf.length() > 0 && !notOperatorFlag) {
                        queryBuf.append(operator);
                    }
                    boolean isInUrl = false;
                    final String targetWord = value.substring(prefix.length());
                    if (INURL_FIELD.equals(field)) {
                        prefix = fieldHelper.urlField + ":";
                        isInUrl = true;
                    }
                    String fieldLogWord;
                    if (notOperatorFlag) {
                        final StringBuilder buf = new StringBuilder(100);
                        buf.append(prefix);
                        if (isInUrl) {
                            buf.append('*');
                        }
                        appendQueryValue(buf, targetWord, isInUrl ? false : useBigram);
                        if (isInUrl) {
                            buf.append('*');
                        }
                        notOperatorList.add(buf.toString());
                        notOperatorFlag = false;
                        fieldLogWord = NOT_ + targetWord;
                    } else {
                        queryBuf.append(prefix);
                        if (isInUrl) {
                            queryBuf.append('*');
                        }
                        appendQueryValue(queryBuf, targetWord, isInUrl ? false : useBigram);
                        if (isInUrl) {
                            queryBuf.append('*');
                        }
                        queryOperandCount++;
                        fieldLogWord = targetWord;
                    }
                    appendFieldBoostValue(queryBuf, field, targetWord);

                    nonPrefix = true;
                    operator = defaultOperator;
                    if (highlightFieldSet.contains(field)) {
                        highLightQueryList.add(targetWord);
                    }
                    if (fieldLogMap != null) {
                        addFieldLogValue(fieldLogMap, field, fieldLogWord);
                    }
                    break;
                }
            }

            // sort
            if (value.startsWith(sortPrefix) && value.length() != sortPrefix.length()) {
                final String[] sortFieldPairs = value.substring(sortPrefix.length()).split(",");
                for (final String sortFieldPairStr : sortFieldPairs) {
                    final String[] sortFieldPair = sortFieldPairStr.split("\\.");
                    if (isSupportedSortField(sortFieldPair[0])) {
                        if (sortFieldPair.length == 1) {
                            sortFieldMap.put(sortFieldPair[0], Constants.ASC);
                        } else {
                            sortFieldMap.put(sortFieldPair[0], sortFieldPair[1]);
                        }
                    }
                }
                continue;
            }

            if (!nonPrefix) {
                if (AND.equals(value)) {
                    operator = _AND_;
                } else if (OR.equals(value)) {
                    operator = _OR_;
                } else if (NOT.equals(value)) {
                    notOperatorFlag = true;
                } else if (notOperatorFlag) {
                    final StringBuilder buf = new StringBuilder(100);

                    buildContentQueryWithLang(buf, value, queryLanguage);
                    notOperatorList.add(buf.toString());

                    operator = defaultOperator;
                    notOperatorFlag = false;
                    highLightQueryList.add(value);

                    if (fieldLogMap != null) {
                        addFieldLogValue(fieldLogMap, fieldHelper.contentField, NOT_ + value);
                    }
                } else {
                    // content
                    if (queryBuf.length() > 0) {
                        queryBuf.append(operator);
                    }
                    buildContentQueryWithLang(queryBuf, value, queryLanguage);
                    contentOperandCount++;

                    operator = defaultOperator;
                    highLightQueryList.add(value);

                    if (fieldLogMap != null) {
                        addFieldLogValue(fieldLogMap, fieldHelper.contentField, value);
                    }

                    if (keyMatchHelper != null) {
                        keyMatchHelper.addSearchWord(value);
                    }
                }
            }
        }

        StringBuilder searchQueryBuf = new StringBuilder(255);
        if (queryBuf.length() > 0) {
            searchQueryBuf.append(queryBuf.toString());
            operator = defaultOperator;
        } else {
            operator = StringUtil.EMPTY;
        }
        if (!notOperatorList.isEmpty()) {
            final String q = searchQueryBuf.toString();
            searchQueryBuf = new StringBuilder(255);
            final int count = queryOperandCount + contentOperandCount;
            if (count > 1) {
                searchQueryBuf.append('(');
            }
            searchQueryBuf.append(q);
            if (count > 1) {
                searchQueryBuf.append(')');
            }
            for (final String notOperator : notOperatorList) {
                searchQueryBuf.append(operator);
                searchQueryBuf.append(NOT_);
                searchQueryBuf.append(notOperator);
                operator = defaultOperator;
            }
        }

        return searchQueryBuf.toString();
    }

    private void addFieldLogValue(final Map<String, List<String>> fieldLogMap, final String field, final String targetWord) {
        List<String> logList = fieldLogMap.get(field);
        if (logList == null) {
            logList = new ArrayList<String>();
            fieldLogMap.put(field, logList);
        }
        logList.add(targetWord);
    }

    protected void appendQueryValue(final StringBuilder buf, final String query, final boolean useBigram) {
        // check reserved
        boolean reserved = false;
        for (final String element : Constants.RESERVED) {
            if (element.equals(query)) {
                reserved = true;
                break;
            }
        }

        if (reserved) {
            buf.append('\\');
            buf.append(query);
            return;
        }

        String value = query;
        if (useBigram && value.length() == 1 && !StringUtils.isAsciiPrintable(value)) {
            // if using bigram, add ?
            value = value + '?';
        }

        String fuzzyValue = null;
        String proximityValue = null;
        String caretValue = null;
        final int tildePos = value.lastIndexOf('~');
        final int caretPos = value.indexOf('^');
        if (tildePos > caretPos) {
            if (tildePos > 0) {
                final String tildeValue = value.substring(tildePos);
                if (tildeValue.length() > 1) {
                    final StringBuilder buf1 = new StringBuilder();
                    final StringBuilder buf2 = new StringBuilder();
                    boolean isComma = false;
                    for (int i = 1; i < tildeValue.length(); i++) {
                        final char c = tildeValue.charAt(i);
                        if (c >= '0' && c <= '9') {
                            if (isComma) {
                                buf2.append(c);
                            } else {
                                buf1.append(c);
                            }
                        } else if (c == '.') {
                            if (isComma) {
                                break;
                            } else {
                                isComma = true;
                            }
                        } else {
                            break;
                        }
                    }
                    if (buf1.length() == 0) {
                        fuzzyValue = "~";
                    } else {
                        final int intValue = Integer.parseInt(buf1.toString());
                        if (intValue <= 0) {
                            // fuzzy
                            buf1.append('.').append(buf2.toString());
                            fuzzyValue = '~' + buf1.toString();
                        } else {
                            // proximity
                            proximityValue = '~' + Integer.toString(intValue);
                        }
                    }
                } else {
                    fuzzyValue = "~";
                }

                value = value.substring(0, tildePos);
            }
        } else {
            if (caretPos > 0) {
                caretValue = value.substring(caretPos);
                value = value.substring(0, caretPos);
            }
        }
        if (value.startsWith("[") && value.endsWith("]")) {
            appendRangeQueryValue(buf, value, '[', ']');
        } else if (value.startsWith("{") && value.endsWith("}")) {
            // TODO function
            appendRangeQueryValue(buf, value, '{', '}');
        } else {
            if (proximityValue == null) {
                buf.append(QueryUtil.escapeValue(value));
            } else {
                buf.append('"').append(QueryUtil.escapeValue(value)).append('"');
            }
        }

        if (fuzzyValue != null) {
            buf.append(fuzzyValue);
        } else if (proximityValue != null) {
            buf.append(proximityValue);
        } else if (caretValue != null) {
            buf.append(caretValue);
        }
    }

    protected void appendRangeQueryValue(final StringBuilder buf, final String value, final char prefix, final char suffix) {
        final String[] split = value.substring(1, value.length() - 1).split(_TO_);
        if (split.length == 2 && split[0].length() > 0 && split[1].length() > 0) {
            final String value1 = split[0].trim();
            final String value2 = split[1].trim();
            if ("*".equals(value1) && "*".equals(value2)) {
                throw new InvalidQueryException("errors.invalid_query_str_range", "Invalid range: " + value);
            }
            buf.append(prefix);
            buf.append(QueryUtil.escapeRangeValue(value1));
            buf.append(_TO_);
            buf.append(QueryUtil.escapeRangeValue(value2));
            buf.append(suffix);
        } else {
            throw new InvalidQueryException("errors.invalid_query_str_range", "Invalid range: " + value);
        }
    }

    private boolean isSupportedSortField(final String field) {
        for (final String f : supportedSortFields) {
            if (f.equals(field)) {
                return true;
            }
        }
        return false;
    }

    protected List<QueryPart> splitQuery(final String query, final Map<String, String> sortFieldMap, final List<String> highLightQueryList,
            final Map<String, List<String>> fieldLogMap) {
        final List<QueryPart> valueList = new ArrayList<QueryPart>();
        StringBuilder buf = new StringBuilder();
        boolean quoted = false;
        int parenthesis = 0;
        int squareBracket = 0;
        int curlyBracket = 0;
        char oldChar = 0;
        for (int i = 0; i < query.length(); i++) {
            final char c = query.charAt(i);
            if (oldChar == '\\' && (c == '"' || c == '(' || c == '{' || c == '[')) {
                buf.append(c);
            } else {
                if (oldChar == '\\') {
                    buf.append('\\');
                }
                switch (c) {
                case '(':
                    buf.append(c);
                    if (!quoted && squareBracket == 0 && curlyBracket == 0) {
                        parenthesis++;
                    }
                    break;
                case ')':
                    buf.append(c);
                    if (!quoted && squareBracket == 0 && curlyBracket == 0) {
                        parenthesis--;
                    }
                    break;
                case '[':
                    buf.append(c);
                    if (!quoted && parenthesis == 0 && curlyBracket == 0) {
                        squareBracket++;
                    }
                    break;
                case ']':
                    buf.append(c);
                    if (!quoted && parenthesis == 0 && curlyBracket == 0) {
                        squareBracket--;
                    }
                    break;
                case '{':
                    buf.append(c);
                    if (!quoted && parenthesis == 0 && squareBracket == 0) {
                        curlyBracket++;
                    }
                    break;
                case '}':
                    buf.append(c);
                    if (!quoted && parenthesis == 0 && squareBracket == 0) {
                        curlyBracket--;
                    }
                    break;
                case '"':
                    if (parenthesis == 0 && curlyBracket == 0 && squareBracket == 0) {
                        quoted ^= true;
                    } else {
                        buf.append(c);
                    }
                    break;
                case '\\':
                    break;
                case ' ':
                case '\u3000':
                    if (quoted || curlyBracket > 0 || squareBracket > 0 || parenthesis > 0) {
                        buf.append(c);
                    } else {
                        if (buf.length() > 0) {
                            addQueryPart(buf.toString(), valueList, sortFieldMap, highLightQueryList, fieldLogMap);
                        }
                        buf = new StringBuilder();
                    }
                    break;
                default:
                    buf.append(c);
                    break;
                }
            }
            oldChar = c;
        }
        if (oldChar == '\\') {
            buf.append('\\');
        }
        if (quoted) {
            throw new InvalidQueryException("errors.invalid_query_quoted", "Invalid quoted: " + query);
        } else if (curlyBracket > 0) {
            throw new InvalidQueryException("errors.invalid_query_curly_bracket", "Invalid curly bracket: " + query);
        } else if (squareBracket > 0) {
            throw new InvalidQueryException("errors.invalid_query_square_bracket", "Invalid square bracket: " + query);
        } else if (parenthesis > 0) {
            throw new InvalidQueryException("errors.invalid_query_parenthesis", "Invalid parenthesis: " + query);
        }
        if (buf.length() > 0) {
            addQueryPart(buf.toString(), valueList, sortFieldMap, highLightQueryList, fieldLogMap);
        }
        return valueList;
    }

    private void addQueryPart(final String str, final List<QueryPart> valueList, final Map<String, String> sortFieldMap,
            final List<String> highLightQueryList, final Map<String, List<String>> fieldLogMap) {
        if (str.startsWith("[") || str.startsWith("{")) {
            valueList.add(new QueryPart(str.trim()));
        } else if (str.startsWith("(") && str.endsWith(")") && str.length() > 2) {
            final String q = str.substring(1, str.length() - 1);
            if (sortFieldMap != null && highLightQueryList != null) {
                final String innerQuery = buildQuery(q, sortFieldMap, highLightQueryList, fieldLogMap);
                if (StringUtil.isNotBlank(innerQuery)) {
                    valueList.add(new QueryPart("(" + innerQuery + ")", true));
                }
            } else {
                // facet query
                final String innerQuery = buildFacetQuery(q);
                if (StringUtil.isNotBlank(innerQuery)) {
                    valueList.add(new QueryPart("(" + innerQuery + ")", true));
                }
            }
        } else {
            valueList.add(new QueryPart(str.trim()));
        }
    }

    public boolean isFacetField(final String field) {
        if (StringUtil.isBlank(field)) {
            return false;
        }
        boolean flag = false;
        for (final String f : facetFields) {
            if (field.equals(f)) {
                flag = true;
            }
        }
        return flag;
    }

    public String buildFacetQuery(final String query) {
        final String q = buildFacetQueryInternal(query);
        String solrQuery;
        if (q == null || "()".equals(q)) {
            solrQuery = StringUtil.EMPTY;
        } else {
            solrQuery = unbracketQuery(q);
        }
        return solrQuery;
    }

    protected String buildFacetQueryInternal(final String query) {
        final List<QueryPart> queryPartList = splitQuery(query, null, null, null);
        if (queryPartList.isEmpty()) {
            return StringUtil.EMPTY;
        }

        final StringBuilder queryBuf = new StringBuilder(255);
        final List<String> notOperatorList = new ArrayList<String>();
        final String defaultOperator = getDefaultOperator();
        String operator = defaultOperator;
        boolean notOperatorFlag = false;
        int queryOperandCount = 0;
        int contentOperandCount = 0;
        final String queryLanguage = getQueryLanguage();
        for (final QueryPart queryPart : queryPartList) {
            if (queryPart.isParsed()) {
                if (queryBuf.length() > 0) {
                    queryBuf.append(operator);
                }
                queryBuf.append(queryPart.getValue());
                continue;
            }
            final String value = queryPart.getValue();
            boolean nonPrefix = false;
            // check prefix
            for (final String field : facetFields) {
                final String prefix = field + ":";
                if (value.startsWith(prefix) && value.length() != prefix.length()) {
                    if (queryBuf.length() > 0) {
                        queryBuf.append(operator);
                    }
                    final String targetWord = value.substring(prefix.length());
                    if (notOperatorFlag) {
                        final StringBuilder buf = new StringBuilder(100);
                        buf.append(prefix);
                        appendQueryValue(buf, targetWord, useBigram);
                        notOperatorList.add(buf.toString());
                        notOperatorFlag = false;
                    } else {
                        queryBuf.append(prefix);
                        appendQueryValue(queryBuf, targetWord, useBigram);
                        queryOperandCount++;
                    }
                    nonPrefix = true;
                    operator = defaultOperator;
                    break;
                }
            }

            // sort
            if (value.startsWith(sortPrefix) && value.length() != sortPrefix.length()) {
                // skip
                continue;
            }

            if (!nonPrefix) {
                if (AND.equals(value)) {
                    operator = _AND_;
                } else if (OR.equals(value)) {
                    operator = _OR_;
                } else if (NOT.equals(value)) {
                    notOperatorFlag = true;
                } else if (notOperatorFlag) {
                    final StringBuilder buf = new StringBuilder(100);

                    buildContentQueryWithLang(buf, value, queryLanguage);
                    notOperatorList.add(buf.toString());

                    operator = defaultOperator;
                    notOperatorFlag = false;
                } else {
                    // content
                    if (queryBuf.length() > 0) {
                        queryBuf.append(operator);
                    }
                    buildContentQueryWithLang(queryBuf, value, queryLanguage);
                    contentOperandCount++;

                    operator = defaultOperator;
                }
            }
        }

        StringBuilder searchQueryBuf = new StringBuilder(255);
        if (queryBuf.length() > 0) {
            searchQueryBuf.append(queryBuf.toString());
            operator = defaultOperator;
        } else {
            operator = StringUtil.EMPTY;
        }
        if (!notOperatorList.isEmpty()) {
            final String q = searchQueryBuf.toString();
            searchQueryBuf = new StringBuilder(255);
            final int count = queryOperandCount + contentOperandCount;
            if (count > 1) {
                searchQueryBuf.append('(');
            }
            searchQueryBuf.append(q);
            if (count > 1) {
                searchQueryBuf.append(')');
            }
            for (final String notOperator : notOperatorList) {
                searchQueryBuf.append(operator);
                searchQueryBuf.append(NOT_);
                searchQueryBuf.append(notOperator);
                operator = defaultOperator;
            }
        }

        return searchQueryBuf.toString();
    }

    protected void buildContentQueryWithLang(final StringBuilder buf, final String value, final String queryLanguage) {
        buf.append('(');
        buf.append(fieldHelper.titleField).append(':');
        appendQueryValue(buf, value, useBigram);
        appendFieldBoostValue(buf, fieldHelper.titleField, value);
        buf.append(_OR_);
        buf.append(fieldHelper.contentField).append(':');
        appendQueryValue(buf, value, useBigram);
        appendFieldBoostValue(buf, fieldHelper.contentField, value);
        if (StringUtil.isNotBlank(queryLanguage)) {
            final String languageField = "content_" + queryLanguage;
            buf.append(_OR_);
            buf.append(languageField);
            buf.append(':');
            appendQueryValue(buf, value, false);
            appendFieldBoostValue(buf, languageField, value);
        }
        buf.append(')');
    }

    protected String getQueryLanguage() {
        final String[] supportedLanguages = systemHelper.getSupportedLanguages();
        if (supportedLanguages.length == 0) {
            return null;
        }
        if (defaultQueryLanguage != null) {
            return defaultQueryLanguage;
        }
        final HttpServletRequest request = RequestUtil.getRequest();
        if (request == null) {
            return null;
        }
        final Locale locale = request.getLocale();
        if (locale == null) {
            return null;
        }
        final String language = locale.getLanguage();
        final String country = locale.getCountry();
        if (StringUtil.isNotBlank(language)) {
            if (StringUtil.isNotBlank(country)) {
                final String lang = language + "_" + country;
                for (final String value : supportedLanguages) {
                    if (value.equals(lang)) {
                        final String fieldLang = fieldLanguageMap.get(value);
                        if (fieldLang == null) {
                            return value;
                        } else {
                            return fieldLang;
                        }
                    }
                }
            }
            for (final String value : supportedLanguages) {
                if (value.equals(language)) {
                    final String fieldLang = fieldLanguageMap.get(value);
                    if (fieldLang == null) {
                        return value;
                    } else {
                        return fieldLang;
                    }
                }
            }
        }
        return null;
    }

    public boolean isFacetSortValue(final String sort) {
        return "count".equals(sort) || "index".equals(sort);
    }

    public String buildOptionQuery(final SearchParamMap optionMap) {
        if (optionMap == null) {
            return StringUtil.EMPTY;
        }

        final StringBuilder buf = new StringBuilder();

        final String[] qs = optionMap.get(Constants.OPTION_QUERY_Q);
        if (qs != null) {
            for (final String q : qs) {
                if (StringUtil.isNotBlank(q)) {
                    buf.append(' ');
                    buf.append(q);
                }
            }
        }

        final String[] cqs = optionMap.get(Constants.OPTION_QUERY_CQ);
        if (cqs != null) {
            for (final String cq : cqs) {
                if (StringUtil.isNotBlank(cq)) {
                    buf.append(' ');
                    char split = 0;
                    final List<QueryPart> partList = splitQuery(cq.indexOf('"') >= 0 ? cq : "\"" + cq + "\"", null, null, null);
                    for (final QueryPart part : partList) {
                        if (split == 0) {
                            split = ' ';
                        } else {
                            buf.append(split);
                        }
                        final String value = part.getValue();
                        buf.append('"');
                        buf.append(value);
                        buf.append('"');
                    }
                }
            }
        }

        final String[] oqs = optionMap.get(Constants.OPTION_QUERY_OQ);
        if (oqs != null) {
            for (final String oq : oqs) {
                if (StringUtil.isNotBlank(oq)) {
                    buf.append(' ');
                    final List<QueryPart> partList = splitQuery(oq, null, null, null);
                    final boolean append = partList.size() > 1 && optionMap.size() > 1;
                    if (append) {
                        buf.append('(');
                    }
                    String split = null;
                    for (final QueryPart part : partList) {
                        if (split == null) {
                            split = _OR_;
                        } else {
                            buf.append(split);
                        }
                        final String value = part.getValue();
                        final boolean hasSpace = value.matches(".*\\s.*");
                        if (hasSpace) {
                            buf.append('"');
                        }
                        buf.append(value);
                        if (hasSpace) {
                            buf.append('"');
                        }
                    }
                    if (append) {
                        buf.append(')');
                    }
                }
            }
        }

        final String[] nqs = optionMap.get(Constants.OPTION_QUERY_NQ);
        if (nqs != null) {
            for (final String nq : nqs) {
                if (StringUtil.isNotBlank(nq)) {
                    buf.append(' ');
                    String split = StringUtil.EMPTY;
                    final List<QueryPart> partList = splitQuery(nq, null, null, null);
                    for (final QueryPart part : partList) {
                        buf.append(split);
                        if (split.length() == 0) {
                            split = " ";
                        }
                        buf.append(NOT_);
                        final String value = part.getValue();
                        final boolean hasSpace = value.matches(".*\\s.*");
                        if (hasSpace) {
                            buf.append('"');
                        }
                        buf.append(value);
                        if (hasSpace) {
                            buf.append('"');
                        }
                    }
                }
            }
        }

        return buf.toString().trim();
    }

    public void setApiResponseFields(final String[] fields) {
        apiResponseFieldSet = new HashSet<String>();
        for (final String field : fields) {
            apiResponseFieldSet.add(field);
        }
    }

    public boolean isApiResponseField(final String field) {
        if (apiResponseFieldSet == null) {
            return true;
        }
        return apiResponseFieldSet.contains(field);
    }

    /**
     * @return the responseFields
     */
    public String[] getResponseFields() {
        return responseFields;
    }

    /**
     * @param responseFields the responseFields to set
     */
    public void setResponseFields(final String[] responseFields) {
        this.responseFields = responseFields;
    }

    public String[] getCacheResponseFields() {
        return cacheResponseFields;
    }

    public void setCacheResponseFields(final String[] cacheResponseFields) {
        this.cacheResponseFields = cacheResponseFields;
    }

    public String[] getResponseDocValuesFields() {
        return responseDocValuesFields;
    }

    public void setResponseDocValuesFields(final String[] responseDocValuesFields) {
        this.responseDocValuesFields = responseDocValuesFields;
    }

    /**
     * @return the highlightingFields
     */
    public String[] getHighlightingFields() {
        return highlightingFields;
    }

    /**
     * @param highlightingFields the highlightingFields to set
     */
    public void setHighlightingFields(final String[] highlightingFields) {
        this.highlightingFields = highlightingFields;
    }

    /**
     * @return the supportedFields
     */
    public String[] getSearchFields() {
        return searchFields;
    }

    /**
     * @param supportedFields the supportedFields to set
     */
    public void setSearchFields(final String[] supportedFields) {
        searchFields = supportedFields;
    }

    /**
     * @return the facetFields
     */
    public String[] getFacetFields() {
        return facetFields;
    }

    /**
     * @param facetFields the facetFields to set
     */
    public void setFacetFields(final String[] facetFields) {
        this.facetFields = facetFields;
    }

    /**
     * @return the sortPrefix
     */
    public String getSortPrefix() {
        return sortPrefix;
    }

    /**
     * @param sortPrefix the sortPrefix to set
     */
    public void setSortPrefix(final String sortPrefix) {
        this.sortPrefix = sortPrefix;
    }

    /**
     * @return the supportedSortFields
     */
    public String[] getSupportedSortFields() {
        return supportedSortFields;
    }

    /**
     * @param supportedSortFields the supportedSortFields to set
     */
    public void setSupportedSortFields(final String[] supportedSortFields) {
        this.supportedSortFields = supportedSortFields;
    }

    public void addHighlightField(final String field) {
        highlightFieldSet.add(field);
    }

    /**
     * @return the highlightSnippetSize
     */
    public int getHighlightSnippetSize() {
        return highlightSnippetSize;
    }

    /**
     * @param highlightSnippetSize the highlightSnippetSize to set
     */
    public void setHighlightSnippetSize(final int highlightSnippetSize) {
        this.highlightSnippetSize = highlightSnippetSize;
    }

    /**
     * @return the shards
     */
    public String getShards() {
        return shards;
    }

    /**
     * @param shards the shards to set
     */
    public void setShards(final String shards) {
        this.shards = shards;
    }

    /**
     * @return the useBigram
     */
    public boolean isUseBigram() {
        return useBigram;
    }

    /**
     * @param useBigram the useBigram to set
     */
    public void setUseBigram(final boolean useBigram) {
        this.useBigram = useBigram;
    }

    /**
     * @return the additionalQuery
     */
    public String getAdditionalQuery() {
        return additionalQuery;
    }

    /**
     * @param additionalQuery the additionalQuery to set
     */
    public void setAdditionalQuery(final String additionalQuery) {
        this.additionalQuery = additionalQuery;
    }

    public int getMaxFilterQueriesForRole() {
        return maxFilterQueriesForRole;
    }

    public void setMaxFilterQueriesForRole(final int maxFilterQuerysForRole) {
        maxFilterQueriesForRole = maxFilterQuerysForRole;
    }

    /**
     * @return the timeAllowed
     */
    public int getTimeAllowed() {
        return timeAllowed;
    }

    /**
     * @param timeAllowed the timeAllowed to set
     */
    public void setTimeAllowed(final int timeAllowed) {
        this.timeAllowed = timeAllowed;
    }

    public void addRequestParameter(final String name, final String... values) {
        requestParameterMap.put(name, values);
    }

    public void addRequestParameter(final String name, final String value) {
        if (value != null) {
            requestParameterMap.put(name, new String[] { value });
        }
    }

    public Set<Entry<String, String[]>> getRequestParameterSet() {
        return requestParameterMap.entrySet();
    }

    public String getAdditionalGeoQuery() {
        return additionalGeoQuery;
    }

    public void setAdditionalGeoQuery(final String additionalGeoQuery) {
        this.additionalGeoQuery = additionalGeoQuery;
    }

    public void addFieldLanguage(final String lang, final String fieldLang) {
        fieldLanguageMap.put(lang, fieldLang);
    }

    public int getMaxSearchResultOffset() {
        return maxSearchResultOffset;
    }

    public void setMaxSearchResultOffset(final int maxSearchResultOffset) {
        this.maxSearchResultOffset = maxSearchResultOffset;
    }

    public void addDefaultSortField(final String fieldName, final String order) {
        final SortField sortField = new SortField();
        sortField.setField(fieldName);
        sortField.setOrder(Constants.ASC.equalsIgnoreCase(order) ? Constants.ASC : Constants.DESC);
        defaultSortFieldList.add(sortField);
    }

    public boolean hasDefaultSortFields() {
        return !defaultSortFieldList.isEmpty();
    }

    public SortField[] getDefaultSortFields() {
        return defaultSortFieldList.toArray(new SortField[defaultSortFieldList.size()]);
    }

    public void setHighlightingPrefix(final String highlightingPrefix) {
        this.highlightingPrefix = highlightingPrefix;
    }

    public String getHighlightingPrefix() {
        return highlightingPrefix;
    }

    public String[] getSupportedMltFields() {
        return supportedMltFields;
    }

    public void setSupportedMltFields(final String[] supportedMltFields) {
        this.supportedMltFields = supportedMltFields;
    }

    public String getMoreLikeThisField(final String[] fields) {
        if (fields == null || fields.length == 0) {
            return null;
        }
        final List<String> list = new ArrayList<String>();
        for (final String field : fields) {
            if (StringUtil.isNotBlank(field)) {
                for (final String f : field.split(",")) {
                    final String value = f.trim();
                    for (final String supported : supportedMltFields) {
                        if (supported.equals(value)) {
                            list.add(value);
                        }
                    }
                }
            }
        }
        if (list.isEmpty()) {
            return null;
        }
        return StringUtils.join(list, ',');
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

    public boolean isAnalysisFieldName(final String fieldName) {
        for (final String f : supportedAnalysisFields) {
            if (f.endsWith(fieldName)) {
                return true;
            }
        }
        return false;
    }

    public FacetInfo getDefaultFacetInfo() {
        return defaultFacetInfo;
    }

    public void setDefaultFacetInfo(final FacetInfo defaultFacetInfo) {
        this.defaultFacetInfo = defaultFacetInfo;
    }

    public MoreLikeThisInfo getDefaultMoreLikeThisInfo() {
        return defaultMoreLikeThisInfo;
    }

    public void setDefaultMoreLikeThisInfo(final MoreLikeThisInfo defaultMoreLikeThisInfo) {
        this.defaultMoreLikeThisInfo = defaultMoreLikeThisInfo;
    }

    public GeoInfo getDefaultGeoInfo() {
        return defaultGeoInfo;
    }

    public void setDefaultGeoInfo(final GeoInfo defaultGeoInfo) {
        this.defaultGeoInfo = defaultGeoInfo;
    }

    public String getDefaultQueryLanguage() {
        return defaultQueryLanguage;
    }

    public void setDefaultQueryLanguage(final String defaultQueryLanguage) {
        this.defaultQueryLanguage = defaultQueryLanguage;
    }

    public Map<String, String[]> getQueryParamMap() {
        if (additionalQueryParamMap.isEmpty()) {
            return additionalQueryParamMap;
        }

        final HttpServletRequest request = RequestUtil.getRequest();
        final Map<String, String[]> queryParamMap = new HashMap<String, String[]>();
        for (final Map.Entry<String, String[]> entry : additionalQueryParamMap.entrySet()) {
            final String[] values = entry.getValue();
            final String[] newValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                final String value = values[i];
                if (value.length() > 1 && value.charAt(0) == '$' && request != null) {
                    final String param = request.getParameter(value.substring(1));
                    if (StringUtil.isNotBlank(param)) {
                        newValues[i] = param;
                    } else {
                        newValues[i] = StringUtil.EMPTY;
                    }
                } else {
                    newValues[i] = value;
                }
            }
            queryParamMap.put(entry.getKey(), newValues);
        }

        return queryParamMap;
    }

    public void addQueryParam(final String key, final String[] values) {
        additionalQueryParamMap.put(key, values);
    }

    public void addFieldBoost(final String field, final String value) {
        try {
            Float.parseFloat(value);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException(value + " was not number.", e);
        }
        fieldBoostMap.put(field, value);
    }

    protected String getDefaultOperator() {
        final HttpServletRequest request = RequestUtil.getRequest();
        if (request != null) {
            final String defaultOperator = (String) request.getAttribute(Constants.DEFAULT_OPERATOR);
            if (AND.equalsIgnoreCase(defaultOperator)) {
                return _AND_;
            } else if (OR.equalsIgnoreCase(defaultOperator)) {
                return _OR_;
            }
        }
        return DEFAULT_OPERATOR;
    }

    protected void appendFieldBoostValue(final StringBuilder buf, final String field, final String value) {
        if (fieldBoostMap.containsKey(field) && value.indexOf('^') == -1 && value.indexOf('~') == -1) {
            buf.append('^').append(fieldBoostMap.get(field));
        }
    }

    public static class QueryPart {
        protected String value;

        protected boolean parsed;

        public QueryPart(final String value) {
            this(value, false);
        }

        public QueryPart(final String value, final boolean parsed) {
            this.value = value;
            this.parsed = parsed;
        }

        /**
         * @return the parsed
         */
        public boolean isParsed() {
            return parsed;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "QueryPart [value=" + value + ", parsed=" + parsed + "]";
        }
    }

}
