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
<div id="${self.uuid}" class="z-pagingbar" z.type="zul.pg.Pg"${self.outerAttrs}${self.innerAttrs}>
<table cellspacing="0">
	<tbody>
		<tr>
			<td><table id="${self.uuid}!tb_f" cellspacing="0" cellpadding="0" border="0" class="z-btn z-btn-icon" style="width: auto;"><tbody><tr><td class="z-btn-l"><i>&#160;</i></td><td class="z-btn-m"><em unselectable="on"><button type="button" class="z-btn-text z-pbar-page-first"> </button></em></td><td class="z-btn-r"><i>&#160;</i></td></tr></tbody></table></td>
			<td><table id="${self.uuid}!tb_p" cellspacing="0" cellpadding="0" border="0" class="z-btn z-btn-icon" style="width: auto;"><tbody><tr><td class="z-btn-l"><i>&#160;</i></td><td class="z-btn-m"><em unselectable="on"><button type="button" class="z-btn-text z-pbar-page-prev"> </button></em></td><td class="z-btn-r"><i>&#160;</i></td></tr></tbody></table></td>
			<td><span class="ypb-sep"/></td>
			<td><span class="ypb-text">Page</span></td>
			<td><input type="text" class="z-pbar-page-number" value="${self.activePage + 1}" size="3" id="${self.uuid}!real" style="height: 14px;" onChange=""/></td>
			<td><span class="ypb-text">of ${self.pageCount}</span></td>
			<td><span class="ypb-sep"/></td>
			<td><table id="${self.uuid}!tb_n" cellspacing="0" cellpadding="0" border="0" class="z-btn z-btn-icon" style="width: auto;"><tbody><tr><td class="z-btn-l"><i>&#160;</i></td><td class="z-btn-m"><em unselectable="on"><button type="button" class="z-btn-text z-pbar-page-next"> </button></em></td><td class="z-btn-r"><i>&#160;</i></td></tr></tbody></table></td>
			<td><table id="${self.uuid}!tb_l" cellspacing="0" cellpadding="0" border="0" class="z-btn z-btn-icon" style="width: auto;"><tbody><tr><td class="z-btn-l"><i>&#160;</i></td><td class="z-btn-m"><em unselectable="on"><button type="button" class="z-btn-text z-pbar-page-last"> </button></em></td><td class="z-btn-r"><i>&#160;</i></td></tr></tbody></table></td>
		</tr>
	</tbody>
</table>
<c:if test="${self.detailed}">
${self.infoTags}
</c:if>
</div>