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

import java.util.Arrays;

import org.seasar.struts.annotation.IntegerType;
import org.seasar.struts.annotation.Maxbytelength;

public class FacetInfo {
    @Maxbytelength(maxbytelength = 255)
    public String[] field;

    @Maxbytelength(maxbytelength = 255)
    public String[] query;

    @Maxbytelength(maxbytelength = 1000)
    public String prefix;

    @IntegerType
    public String limit;

    @IntegerType
    public String minCount;

    @Maxbytelength(maxbytelength = 255)
    public String sort;

    @Maxbytelength(maxbytelength = 10)
    public String missing;

    @Override
    public String toString() {
        return "FacetInfo [field=" + Arrays.toString(field) + ", query=" + Arrays.toString(query) + ", prefix=" + prefix + ", limit="
                + limit + ", minCount=" + minCount + ", sort=" + sort + ", missing=" + missing + "]";
    }
}
