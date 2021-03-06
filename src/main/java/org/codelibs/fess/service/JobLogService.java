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

package org.codelibs.fess.service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.crud.service.BsJobLogService;
import org.codelibs.fess.db.cbean.JobLogCB;
import org.codelibs.fess.db.exentity.JobLog;
import org.codelibs.fess.pager.JobLogPager;
import org.codelibs.fess.util.ComponentUtil;

public class JobLogService extends BsJobLogService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    protected void setupListCondition(final JobLogCB cb, final JobLogPager jobLogPager) {
        super.setupListCondition(cb, jobLogPager);

        // setup condition
        cb.query().addOrderBy_StartTime_Desc();
        cb.query().addOrderBy_EndTime_Desc();

        // search

    }

    @Override
    protected void setupEntityCondition(final JobLogCB cb, final Map<String, String> keys) {
        super.setupEntityCondition(cb, keys);

        // setup condition

    }

    @Override
    protected void setupStoreCondition(final JobLog jobLog) {
        super.setupStoreCondition(jobLog);

        // setup condition

    }

    @Override
    protected void setupDeleteCondition(final JobLog jobLog) {
        super.setupDeleteCondition(jobLog);

        // setup condition

    }

    public void deleteBefore(final int days) {
        final LocalDateTime targetTime = ComponentUtil.getSystemHelper().getCurrentTime().minusDays(days);
        jobLogBhv.varyingQueryDelete(cb -> {
            cb.query().setEndTime_LessThan(targetTime);
        }, op -> op.allowNonQueryDelete());
    }

    public void deleteByJobStatus(final List<String> jobStatusList) {
        jobLogBhv.varyingQueryDelete(cb -> {
            cb.query().setJobStatus_InScope(jobStatusList);
        }, op -> op.allowNonQueryDelete());
    }

}
