<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><tiles:insert template="/WEB-INF/view/common/admin/layout.jsp"
	flush="true">
	<tiles:put name="title">
		<bean:message key="labels.dict_userdict_configuration" />
	</tiles:put>
	<tiles:put name="header" value="/WEB-INF/view/common/admin/header.jsp" />
	<tiles:put name="footer" value="/WEB-INF/view/common/admin/footer.jsp" />
	<tiles:put name="menu" value="/WEB-INF/view/common/admin/menu.jsp" />
	<tiles:put name="menuType" value="dict" />
	<tiles:put name="headerScript" type="string"></tiles:put>
	<tiles:put name="body" type="string">

		<h3>
			<bean:message key="labels.dict_userdict_title" />
		</h3>

		<%-- Message: BEGIN --%>
		<div>
			<html:messages id="msg" message="true">
				<div class="alert-message info"><bean:write name="msg" ignore="true" /></div>
			</html:messages>
			<html:errors />
		</div>
		<%-- Message: END --%>

		<%-- List: BEGIN --%>
		<div class="list-table">
			<div>
				<ul class="pills">
					<li><s:link href="../index">
							<bean:message key="labels.dict_list_link" />
						</s:link></li>
					<li class="active"><a href="#"><bean:message
								key="labels.dict_userdict_list_link" /></a></li>
					<li><s:link href="createpage?dictId=${f:u(dictId)}">
							<bean:message key="labels.dict_userdict_link_create" />
						</s:link></li>
					<li><s:link href="downloadpage?dictId=${f:u(dictId)}">
							<bean:message key="labels.dict_userdict_link_download" />
						</s:link></li>
					<li><s:link href="uploadpage?dictId=${f:u(dictId)}">
							<bean:message key="labels.dict_userdict_link_upload" />
						</s:link></li>
				</ul>
			</div>

			<c:if test="${userDictPager.allRecordCount == 0}">
				<p class="alert-message warning">
					<bean:message key="labels.list_could_not_find_crud_table" />
				</p>
			</c:if>
			<c:if test="${userDictPager.allRecordCount > 0}">
				<table class="bordered-table zebra-striped">
					<thead>
						<tr>
							<th style="text-align: left;"><bean:message
									key="labels.dict_userdict_token" /></th>
							<th style="text-align: left;"><bean:message
									key="labels.dict_userdict_reading" /></th>
							<th style="text-align: center; width: 200px;">&nbsp;</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="data" varStatus="s" items="${userDictItemItems}">
							<tr class="${s.index % 2 == 0 ? 'row1' : 'row2'}">
								<td>${f:h(data.token)}</td>
								<td>${f:h(data.reading)}</td>
								<td style="text-align: center;"><s:link
										href="confirmpage/${f:u(dictId)}/4/${f:u(data.id)}">
										<bean:message key="labels.dict_link_details" />
									</s:link> <s:link href="editpage/${f:u(dictId)}/2/${f:u(data.id)}">
										<bean:message key="labels.dict_link_edit" />
									</s:link> <s:link href="deletepage/${f:u(dictId)}/3/${f:u(data.id)}">
										<bean:message key="labels.dict_link_delete" />
									</s:link></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				<%-- Page Navigation: BEGIN --%>
				<div class="row center">
					<div class="pagination">
						<ul>
							<c:if test="${userDictPager.existPrePage}">
								<li class="prev"><s:link
										href="list/${f:u(dictId)}/${userDictPager.currentPageNumber - 1}">
										<bean:message key="labels.dict_link_prev_page" />
									</s:link></li>
							</c:if>
							<c:if test="${!userDictPager.existPrePage}">
								<li class="prev disabled"><a href="#"><bean:message
											key="labels.dict_link_prev_page" /></a></li>
							</c:if>
							<c:forEach var="p" varStatus="s"
								items="${userDictPager.pageNumberList}">
								<li
									<c:if test="${p == userDictPager.currentPageNumber}">class="active"</c:if>>
									<s:link href="list/${f:u(dictId)}/${p}">${p}</s:link>
								</li>
							</c:forEach>
							<c:if test="${userDictPager.existNextPage}">
								<li class="next"><s:link
										href="list/${f:u(dictId)}/${userDictPager.currentPageNumber + 1}">
										<bean:message key="labels.dict_link_next_page" />
									</s:link></li>
							</c:if>
							<c:if test="${!userDictPager.existNextPage}">
								<li class="next disabled"><a href="#"><bean:message
											key="labels.dict_link_next_page" /></a></li>
							</c:if>
						</ul>
					</div>

					<div>
						<span><bean:message key="labels.pagination_page_guide_msg"
								arg0="${f:h(userDictPager.currentPageNumber)}"
								arg1="${f:h(userDictPager.allPageCount)}"
								arg2="${f:h(userDictPager.allRecordCount)}" /></span>
					</div>
				</div>
				<%-- Page Navigation: END --%>
			</c:if>
		</div>
		<%-- List: END --%>

	</tiles:put>
</tiles:insert>
