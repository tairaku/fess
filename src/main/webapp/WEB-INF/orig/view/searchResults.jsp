<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%-- query matched some document --%>
<div id="subheader" class="row">
	<div class="span12">
		<p>
			<bean:message key="labels.search_result_status"
				arg0="${f:h(displayQuery)}" arg1="${f:h(allRecordCount)}"
				arg2="${f:h(currentStartRecordNumber)}"
				arg3="${f:h(currentEndRecordNumber)}" />
			<c:if test="${execTime!=null}">
				<bean:message key="labels.search_result_time"
					arg0="${f:h(execTime)}" />
			</c:if>
		</p>
	</div>
</div>
<c:if test="${partialResults}">
	<div class="alert">
		<p>
			<bean:message key="errors.process_time_is_exceeded" />
		</p>
	</div>
</c:if>
<div id="result" class="row content">
	<input type="hidden" id="queryId" value="${f:u(queryId)}" />
	<input type="hidden" id="rt" value="${f:u(rt)}" />
	<div class="span8">
		<ol>
			<c:forEach var="doc" varStatus="s" items="${documentItems}">
				<li id="result${s.index}">
					<h3 class="title ellipsis">
						<a class="link" href="${doc.urlLink}" data-uri="${doc.urlLink}" data-id="${doc.docId}">${f:h(doc.contentTitle)}</a>
					</h3>
					<div class="body">
						<div class="description">${doc.contentDescription}</div>
						<div class="site ellipsis">
							<cite>${f:h(doc.sitePath)}</cite>
							<c:if test="${doc.hasCache_s_s=='true'}">
								<a href="cache?docId=${doc.docId}${appendHighlightQueries}" class="cache"><bean:message
										key="labels.search_result_cache" /></a>
							</c:if>
						</div>
						<div class="more visible-phone">
							<a href="#result${s.index}"><bean:message key="labels.search_result_more" /></a>
						</div>
						<div class="info">
							<c:if test="${doc.created!=null && doc.created!=''}">
								<c:set var="hasInfo" value="true"/>
								<bean:message key="labels.search_result_created" />
								<fmt:formatDate value="${fe:parseDate(doc.created)}" type="BOTH" />
							</c:if>
							<c:if test="${doc.lastModified!=null && doc.lastModified!=''}">
								<c:if test="${hasInfo}"><span class="br-phone"></span><span class="hidden-phone">-</span></c:if><c:set var="hasInfo" value="true"/>
								<bean:message key="labels.search_result_lastModified" />
								<fmt:formatDate value="${fe:parseDate(doc.lastModified)}" type="BOTH" />
							</c:if>
							<c:if test="${doc.contentLength!=null && doc.contentLength!=''}">
								<c:if test="${hasInfo}"><span class="br-phone"></span><span class="hidden-phone">-</span></c:if><c:set var="hasInfo" value="true"/>
								<bean:message key="labels.search_result_size"
									arg0="${f:h(doc.contentLength)}" />
							</c:if>
							<c:if test="${searchLogSupport}">
								<c:if test="${hasInfo}"><span class="br-phone"></span><span class="hidden-phone">-</span></c:if><c:set var="hasInfo" value="true"/>
								<bean:message key="labels.search_click_count"
									arg0="${f:h(doc.clickCount_l_x_dv)}" />
							</c:if>
							<c:if test="${favoriteSupport}">
								<c:if test="${hasInfo}"><span class="br-phone"></span><span class="hidden-phone">-</span></c:if><c:set var="hasInfo" value="true"/>
								<a href="#${doc.docId}" class="favorite"><bean:message
										key="labels.search_result_favorite" /> (${f:h(doc.favoriteCount_l_x_dv)})</a>
								<span class="favorited"><bean:message
										key="labels.search_result_favorited"/> <span class="favorited-count">(${f:h(doc.favoriteCount_l_x_dv)})</span></span>
							</c:if>
						</div>
					</div>
				</li>
			</c:forEach>
		</ol>
	</div>
	<div class="span4 visible-desktop visible-tablet">
		<%-- Side Content --%>
		<c:if test="${screenShotSupport}">
			<div id="screenshot"></div>
		</c:if>
		<c:if test="${facetResponse != null}">
			<div class="well span3">
				<ul class="nav nav-list">
					<c:forEach var="fieldData" items="${facetResponse.fieldList}">
						<c:if test="${fieldData.name == 'label' && fieldData.valueCountMap.size() > 0}">
					<li class="nav-header"><bean:message key="label.facet_label_title" /></li>
							<c:forEach var="countEntry" items="${fieldData.valueCountMap}">
								<c:if test="${countEntry.value != 0 && fe:labelexists(countEntry.key)}">
					<li><s:link
							href="search?query=${f:u(query)}&additional=label:${f:u(countEntry.key)}${pagingQuery}${fe:facetQuery()}${fe:mltQuery()}${fe:geoQuery()}">
							${f:h(fe:label(countEntry.key))} (${f:h(countEntry.value)})</s:link></li>
								</c:if>
							</c:forEach>
						</c:if>
					</c:forEach>
					<c:forEach var="facetQueryView" items="${fe:facetQueryViewList()}">
					<li class="nav-header"><bean:message key="${facetQueryView.title}" /></li>
						<c:forEach var="queryEntry" items="${facetQueryView.queryMap}">
								<c:if test="${facetResponse.queryCountMap[queryEntry.value] != 0}">
					<li><s:link
							href="search?query=${f:u(query)}&additional=${f:u(queryEntry.value)}${pagingQuery}${fe:facetQuery()}${fe:mltQuery()}${fe:geoQuery()}">
							<bean:message key="${queryEntry.key}" /> (${f:h(facetResponse.queryCountMap[queryEntry.value])})</s:link></li>
								</c:if>
						</c:forEach>
					</c:forEach>
				</ul>
				<c:if test="${!empty additional}">
				<ul class="nav nav-list">
					<li class="reset">
						<s:link
							href="search?query=${f:u(query)}"><bean:message key="label.facet_label_reset" /></s:link>
					</li>
				</ul>
				</c:if>
			</div>
		</c:if>
	</div>
</div>
<div class="row center">
	<div id="subfooter" class="pagination">
		<ul>
			<c:if test="${existPrevPage}">
				<li class="prev"><s:link
						href="prev?query=${f:u(query)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}${pagingQuery}${fe:facetQuery()}${fe:mltQuery()}${fe:geoQuery()}">
						<bean:message key="labels.prev_page" />
					</s:link></li>
			</c:if>
			<c:if test="${!existPrevPage}">
				<li class="prev disabled"><a href="#"><bean:message
							key="labels.prev_page" /></a></li>
			</c:if>
			<c:forEach var="pageNumber" varStatus="s" items="${pageNumberList}">
				<li
					<c:if test="${pageNumber < currentPageNumber - 2 || pageNumber > currentPageNumber + 2}">class="hidden-phone"</c:if>
					<c:if test="${pageNumber == currentPageNumber && pageNumber >= currentPageNumber - 2 && pageNumber <= currentPageNumber + 2}">class="active"</c:if>
					>
					<s:link
						href="move?query=${f:u(query)}&pn=${f:u(pageNumber)}&num=${f:u(pageSize)}${pagingQuery}${fe:facetQuery()}${fe:mltQuery()}${fe:geoQuery()}">${f:h(pageNumber)}</s:link>
				</li>
			</c:forEach>
			<c:if test="${existNextPage}">
				<li class="next"><s:link
						href="next?query=${f:u(query)}&pn=${f:u(currentPageNumber)}&num=${f:u(pageSize)}${pagingQuery}${fe:facetQuery()}${fe:mltQuery()}${fe:geoQuery()}">
						<bean:message key="labels.next_page" />
					</s:link></li>
			</c:if>
			<c:if test="${!existNextPage}">
				<li class="next disabled"><a href="#"><bean:message
							key="labels.next_page" /></a></li>
			</c:if>
		</ul>
	</div>
</div>
