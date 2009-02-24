<%--
paging.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Thu Aug 17 17:59:43     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="zcls" value="${self.zclass}"/>
<div id="${self.uuid}" name="${self.uuid}" z.type="zul.pg.Pg"${self.outerAttrs}${self.innerAttrs}>
<table cellspacing="0">
	<tbody>
		<tr>
			<td>
				<table id="${self.uuid}!tb_f" name="${self.uuid}!tb_f" cellspacing="0" cellpadding="0" border="0" class="${zcls}-btn">
					<tbody>
						<tr>
							<td><div><button type="button" class="${zcls}-first"> </button></div></td>
						</tr>
					</tbody>
				</table>
			</td>
			<td>
				<table id="${self.uuid}!tb_p" name="${self.uuid}!tb_p" cellspacing="0" cellpadding="0" border="0" class="${zcls}-btn">
					<tbody>
						<tr>
							<td><div><button type="button" class="${zcls}-prev"> </button></div></td>
						</tr>
					</tbody>
				</table>
			</td>
			<td><span class="${zcls}-sep"/></td>
			<td><span class="${zcls}-text"></span></td>
			<td><input id="${self.uuid}!real" name="${self.uuid}!real" type="text" class="${zcls}-inp" value="${self.activePage + 1}" size="3"/></td>
			<td><span class="${zcls}-text">/ ${self.pageCount}</span></td>
			<td><span class="${zcls}-sep"/></td>
			<td>
				<table id="${self.uuid}!tb_n" name="${self.uuid}!tb_n" cellspacing="0" cellpadding="0" border="0" class="${zcls}-btn">
					<tbody>
						<tr>
							<td><div><button type="button" class="${zcls}-next"> </button></div></td>
						</tr>
					</tbody>
				</table>
			</td>
			<td>
				<table id="${self.uuid}!tb_l" name="${self.uuid}!tb_l" cellspacing="0" cellpadding="0" border="0" class="${zcls}-btn">
					<tbody>
						<tr>
							<td><div><button type="button" class="${zcls}-last"> </button></div></td>
						</tr>
					</tbody>
				</table>
			</td>
		</tr>
	</tbody>
</table>
<c:if test="${self.detailed}">
${self.infoTags}
</c:if>
</div>