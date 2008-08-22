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
<div id="${self.uuid}" name="${self.uuid}" z.type="zul.pg.Pg"${self.outerAttrs}${self.innerAttrs}>
<table cellspacing="0">
	<tbody>
		<tr>
			<td>
				<table id="${self.uuid}!tb_f" name="${self.uuid}!tb_f" cellspacing="0" cellpadding="0" border="0" class="${self.moldSclass}-btn">
					<tbody>
						<tr>
							<td class="${self.moldSclass}-btn-l"><i>&#160;</i></td>
							<td class="${self.moldSclass}-btn-m"><em unselectable="on"><button type="button" class="${self.moldSclass}-first"> </button></em></td>
							<td class="${self.moldSclass}-btn-r"><i>&#160;</i></td>
						</tr>
					</tbody>
				</table>
			</td>
			<td>
				<table id="${self.uuid}!tb_p" name="${self.uuid}!tb_p" cellspacing="0" cellpadding="0" border="0" class="${self.moldSclass}-btn">
					<tbody>
						<tr>
							<td class="${self.moldSclass}-btn-l"><i>&#160;</i></td>
							<td class="${self.moldSclass}-btn-m"><em unselectable="on"><button type="button" class="${self.moldSclass}-prev"> </button></em></td>
							<td class="${self.moldSclass}-btn-r"><i>&#160;</i></td>
						</tr>
					</tbody>
				</table>
			</td>
			<td><span class="${self.moldSclass}-sep"/></td>
			<td><span class="${self.moldSclass}-text"></span></td>
			<td><input id="${self.uuid}!real" name="${self.uuid}!real" type="text" class="${self.moldSclass}-inp" value="${self.activePage + 1}" size="3"/></td>
			<td><span class="${self.moldSclass}-text">/ ${self.pageCount}</span></td>
			<td><span class="${self.moldSclass}-sep"/></td>
			<td>
				<table id="${self.uuid}!tb_n" name="${self.uuid}!tb_n" cellspacing="0" cellpadding="0" border="0" class="${self.moldSclass}-btn">
					<tbody>
						<tr>
							<td class="${self.moldSclass}-btn-l"><i>&#160;</i></td>
							<td class="${self.moldSclass}-btn-m"><em unselectable="on"><button type="button" class="${self.moldSclass}-next"> </button></em></td>
							<td class="${self.moldSclass}-btn-r"><i>&#160;</i></td>
						</tr>
					</tbody>
				</table>
			</td>
			<td>
				<table id="${self.uuid}!tb_l" name="${self.uuid}!tb_l" cellspacing="0" cellpadding="0" border="0" class="${self.moldSclass}-btn">
					<tbody>
						<tr>
							<td class="${self.moldSclass}-btn-l"><i>&#160;</i></td>
							<td class="${self.moldSclass}-btn-m"><em unselectable="on"><button type="button" class="${self.moldSclass}-last"> </button></em></td>
							<td class="${self.moldSclass}-btn-r"><i>&#160;</i></td>
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