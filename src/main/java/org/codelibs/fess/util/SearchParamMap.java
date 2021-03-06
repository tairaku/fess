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

import java.util.HashMap;

import org.codelibs.core.util.StringUtil;

public class SearchParamMap extends HashMap<String, String[]> {

    private static final long serialVersionUID = 1L;

    public static final String CLASS_NAME = "org.codelibs.fess.util.SearchParamMap";

    @Override
    public String[] get(final Object key) {
        if (CLASS_NAME.equals(key)) {
            return StringUtil.EMPTY_STRINGS;
        }
        return super.get(key);
    }

}
