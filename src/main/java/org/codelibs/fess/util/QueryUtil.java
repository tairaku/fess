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

package org.codelibs.fess.util;

import org.codelibs.fess.Constants;

public class QueryUtil {
    protected QueryUtil() {
        // nothing
    }

    public static String escapeValue(final String value) {
        final String escapedValue = Constants.SOLR_FIELD_RESERVED_PATTERN.matcher(value).replaceAll("\\\\$1");
        if (escapedValue.length() > 1) {
            final char c = escapedValue.charAt(0);
            if (c == '*' || c == '?') {
                return "\\" + escapedValue;
            }
        }
        return escapedValue;
    }

    public static String escapeRangeValue(final String value) {
        final String escapedValue = Constants.SOLR_RANGE_FIELD_RESERVED_PATTERN.matcher(value).replaceAll("\\\\$1");
        if (escapedValue.length() > 1) {
            final char c = escapedValue.charAt(0);
            if (c == '*' || c == '?') {
                return "\\" + escapedValue;
            }
        }
        return escapedValue;
    }
}
