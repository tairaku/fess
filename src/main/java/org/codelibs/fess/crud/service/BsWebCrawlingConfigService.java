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

package org.codelibs.fess.crud.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.crud.CrudMessageException;
import org.codelibs.fess.db.cbean.WebCrawlingConfigCB;
import org.codelibs.fess.db.exbhv.WebCrawlingConfigBhv;
import org.codelibs.fess.db.exentity.WebCrawlingConfig;
import org.codelibs.fess.pager.WebCrawlingConfigPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsWebCrawlingConfigService {

    @Resource
    protected WebCrawlingConfigBhv webCrawlingConfigBhv;

    public BsWebCrawlingConfigService() {
        super();
    }

    public List<WebCrawlingConfig> getWebCrawlingConfigList(final WebCrawlingConfigPager webCrawlingConfigPager) {

        final PagingResultBean<WebCrawlingConfig> webCrawlingConfigList = webCrawlingConfigBhv.selectPage(cb -> {
            cb.paging(webCrawlingConfigPager.getPageSize(), webCrawlingConfigPager.getCurrentPageNumber());
            setupListCondition(cb, webCrawlingConfigPager);
        });

        // update pager
        Beans.copy(webCrawlingConfigList, webCrawlingConfigPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        webCrawlingConfigPager.setPageNumberList(webCrawlingConfigList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return webCrawlingConfigList;
    }

    public WebCrawlingConfig getWebCrawlingConfig(final Map<String, String> keys) {
        final WebCrawlingConfig webCrawlingConfig = webCrawlingConfigBhv.selectEntity(cb -> {
            cb.query().setId_Equal(Long.parseLong(keys.get("id")));
            setupEntityCondition(cb, keys);
        }).orElse(null);//TODO
        if (webCrawlingConfig == null) {
            // TODO exception?
            return null;
        }

        return webCrawlingConfig;
    }

    public void store(final WebCrawlingConfig webCrawlingConfig) throws CrudMessageException {
        setupStoreCondition(webCrawlingConfig);

        webCrawlingConfigBhv.insertOrUpdate(webCrawlingConfig);

    }

    public void delete(final WebCrawlingConfig webCrawlingConfig) throws CrudMessageException {
        setupDeleteCondition(webCrawlingConfig);

        webCrawlingConfigBhv.delete(webCrawlingConfig);

    }

    protected void setupListCondition(final WebCrawlingConfigCB cb, final WebCrawlingConfigPager webCrawlingConfigPager) {

        if (webCrawlingConfigPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(webCrawlingConfigPager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final WebCrawlingConfigCB cb, final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final WebCrawlingConfig webCrawlingConfig) {
    }

    protected void setupDeleteCondition(final WebCrawlingConfig webCrawlingConfig) {
    }
}