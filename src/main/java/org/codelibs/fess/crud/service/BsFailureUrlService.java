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
import org.codelibs.fess.db.cbean.FailureUrlCB;
import org.codelibs.fess.db.exbhv.FailureUrlBhv;
import org.codelibs.fess.db.exentity.FailureUrl;
import org.codelibs.fess.pager.FailureUrlPager;
import org.dbflute.cbean.result.PagingResultBean;
import org.seasar.framework.beans.util.Beans;

public abstract class BsFailureUrlService {

    @Resource
    protected FailureUrlBhv failureUrlBhv;

    public BsFailureUrlService() {
        super();
    }

    public List<FailureUrl> getFailureUrlList(final FailureUrlPager failureUrlPager) {

        final PagingResultBean<FailureUrl> failureUrlList = failureUrlBhv.selectPage(cb -> {
            cb.paging(failureUrlPager.getPageSize(), failureUrlPager.getCurrentPageNumber());
            setupListCondition(cb, failureUrlPager);
        });

        // update pager
        Beans.copy(failureUrlList, failureUrlPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        failureUrlPager.setPageNumberList(failureUrlList.pageRange(op -> {
            op.rangeSize(5);
        }).createPageNumberList());

        return failureUrlList;
    }

    public FailureUrl getFailureUrl(final Map<String, String> keys) {
        final FailureUrl failureUrl = failureUrlBhv.selectEntity(cb -> {
            cb.query().setId_Equal(Long.parseLong(keys.get("id")));
            setupEntityCondition(cb, keys);
        }).orElse(null);
        if (failureUrl == null) {
            // TODO exception?
            return null;
        }

        return failureUrl;
    }

    public void store(final FailureUrl failureUrl) throws CrudMessageException {
        setupStoreCondition(failureUrl);

        failureUrlBhv.insertOrUpdate(failureUrl);

    }

    public void delete(final FailureUrl failureUrl) throws CrudMessageException {
        setupDeleteCondition(failureUrl);

        failureUrlBhv.delete(failureUrl);

    }

    protected void setupListCondition(final FailureUrlCB cb, final FailureUrlPager failureUrlPager) {

        if (failureUrlPager.id != null) {
            cb.query().setId_Equal(Long.parseLong(failureUrlPager.id));
        }
        // TODO Long, Integer, String supported only.
    }

    protected void setupEntityCondition(final FailureUrlCB cb, final Map<String, String> keys) {
    }

    protected void setupStoreCondition(final FailureUrl failureUrl) {
    }

    protected void setupDeleteCondition(final FailureUrl failureUrl) {
    }
}