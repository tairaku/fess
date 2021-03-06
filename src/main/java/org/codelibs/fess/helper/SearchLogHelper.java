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

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Resource;

import jp.sf.fess.suggest.service.SuggestService;

import org.codelibs.core.util.DynamicProperties;
import org.codelibs.fess.db.exbhv.ClickLogBhv;
import org.codelibs.fess.db.exbhv.FavoriteLogBhv;
import org.codelibs.fess.db.exbhv.pmbean.FavoriteUrlCountPmb;
import org.codelibs.fess.db.exentity.ClickLog;
import org.codelibs.fess.db.exentity.SearchLog;
import org.codelibs.fess.db.exentity.customize.FavoriteUrlCount;
import org.codelibs.robot.util.LruHashMap;
import org.dbflute.cbean.result.ListResultBean;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.annotation.tiger.InitMethod;

public abstract class SearchLogHelper {

    @Resource
    protected DynamicProperties crawlerProperties;

    public long userCheckInterval = 5 * 60 * 1000;// 5 min

    public int userInfoCacheSize = 1000;

    protected volatile Queue<SearchLog> searchLogQueue = new ConcurrentLinkedQueue<SearchLog>();

    protected volatile Queue<ClickLog> clickLogQueue = new ConcurrentLinkedQueue<ClickLog>();

    protected Map<String, Long> userInfoCache;

    @Resource
    protected SuggestService suggestService;

    @InitMethod
    public void init() {
        userInfoCache = new LruHashMap<String, Long>(userInfoCacheSize);
    }

    public abstract void updateUserInfo(final String userCode);

    public void addSearchLog(final SearchLog searchLog) {
        searchLogQueue.add(searchLog);
    }

    public void addClickLog(final ClickLog clickLog) {
        clickLogQueue.add(clickLog);
    }

    public void storeSearchLog() {
        if (!searchLogQueue.isEmpty()) {
            final Queue<SearchLog> queue = searchLogQueue;
            searchLogQueue = new ConcurrentLinkedQueue<SearchLog>();
            processSearchLogQueue(queue);
        }

        if (!clickLogQueue.isEmpty()) {
            final Queue<ClickLog> queue = clickLogQueue;
            clickLogQueue = new ConcurrentLinkedQueue<ClickLog>();
            processClickLogQueue(queue);
        }
    }

    public int getClickCount(final String url) {
        final ClickLogBhv clickLogBhv = SingletonS2Container.getComponent(ClickLogBhv.class);
        return clickLogBhv.selectCount(cb -> {
            cb.query().setUrl_Equal(url);
        });
    }

    public long getFavoriteCount(final String url) {
        final FavoriteLogBhv favoriteLogBhv = SingletonS2Container.getComponent(FavoriteLogBhv.class);
        final FavoriteUrlCountPmb pmb = new FavoriteUrlCountPmb();
        pmb.setUrl(url);
        final ListResultBean<FavoriteUrlCount> list = favoriteLogBhv.outsideSql().selectList(pmb);

        long count = 0;
        if (!list.isEmpty()) {
            count = list.get(0).getCnt().longValue();
        }
        return count;
    }

    protected abstract void processSearchLogQueue(Queue<SearchLog> queue);

    protected abstract void processClickLogQueue(Queue<ClickLog> queue);
}
