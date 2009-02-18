<%--
tabpanel-accd.dsp

{{IS_NOTE
	Purpose:
		A accordion-type tabpanel.
	Description:
		
	History:
		Tue Sep 27 15:02:30     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="tab" value="${self.linkedTab}"/>
<c:set var="tabzcs" value="${tab.zclass}-"/>
<div class="${self.zclass}-outer" id="${self.uuid}"><%-- self.outerAttrs/innerAttrs gen below --%>
	<c:if test="${!empty self.tabbox.panelSpacing and self.index!=0}">
			<div style="margin:0;display:list-item;width:100%;height:${self.tabbox.panelSpacing};"></div>
	</c:if>
	<div id="${tab.uuid}"${tab.outerAttrs}${tab.innerAttrs} z.type="zul.tab2.Tab2">
		<div align="left" class="${tabzcs}header" >
			<div class="${tabzcs}tl">
				<div class="${tabzcs}tr"></div>
			</div>
			<div class="${tabzcs}hl">
				<div class="${tabzcs}hr">
					<div class="${tabzcs}hm">
						<c:if test="${tab.closable}">
							<a id="${tab.uuid}!close"  class="${tabzcs}close"></a>
						</c:if>					
						<span class="${tabzcs}text">${tab.imgTag}<c:out value="${tab.label}"/></span>
					</div>
				</div>
			</div>			
		</div>
	</div>
	<div id="${self.uuid}!real"${self.outerAttrs}${self.innerAttrs}>
		<div id="${self.uuid}!cave">
			<c:forEach var="child" items="${self.children}">${z:redraw(child, null)}</c:forEach>
		</div>
	</div>
</div>
