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

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.crud.CommonConstants;
import org.codelibs.fess.dict.DictionaryExpiredException;
import org.codelibs.fess.dict.DictionaryFile;
import org.codelibs.fess.dict.DictionaryFile.PagingList;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.dict.userdict.UserDictFile;
import org.codelibs.fess.dict.userdict.UserDictItem;
import org.codelibs.fess.pager.UserDictPager;
import org.seasar.framework.beans.util.Beans;

public class UserDictService {
    @Resource
    protected DictionaryManager dictionaryManager;

    public List<UserDictItem> getUserDictList(final String dictId, final UserDictPager userDictPager) {
        final UserDictFile userDictFile = getUserDictFile(dictId);

        final int pageSize = userDictPager.getPageSize();
        final PagingList<UserDictItem> userDictList =
                userDictFile.selectList((userDictPager.getCurrentPageNumber() - 1) * pageSize, pageSize);

        // update pager
        Beans.copy(userDictList, userDictPager).includes(CommonConstants.PAGER_CONVERSION_RULE).execute();
        userDictList.setPageRangeSize(5);
        userDictPager.setPageNumberList(userDictList.createPageNumberList());

        return userDictList;

    }

    public UserDictFile getUserDictFile(final String dictId) {
        final DictionaryFile<?> dictionaryFile = dictionaryManager.getDictionaryFile(dictId);
        if (dictionaryFile instanceof UserDictFile) {
            return (UserDictFile) dictionaryFile;
        }
        throw new DictionaryExpiredException();
    }

    public UserDictItem getUserDict(final String dictId, final Map<String, String> paramMap) {
        final UserDictFile userDictFile = getUserDictFile(dictId);

        final String idStr = paramMap.get("id");
        if (StringUtil.isNotBlank(idStr)) {
            try {
                final long id = Long.parseLong(idStr);
                return userDictFile.get(id);
            } catch (final NumberFormatException e) {
                // ignore
            }
        }

        return null;
    }

    public void store(final String dictId, final UserDictItem userDictItem) {
        final UserDictFile userDictFile = getUserDictFile(dictId);

        if (userDictItem.getId() == 0) {
            userDictFile.insert(userDictItem);
        } else {
            userDictFile.update(userDictItem);
        }
    }

    public void delete(final String dictId, final UserDictItem userDictItem) {
        final UserDictFile userDictFile = getUserDictFile(dictId);
        userDictFile.delete(userDictItem);
    }
}
