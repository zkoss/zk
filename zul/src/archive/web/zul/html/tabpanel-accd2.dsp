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
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="tab" value="${self.linkedTab}"/>
<c:set var="look" value="${self.tabbox.tabLook}-"/>
<c:set var="lookaccd" value="${c:cat(look,'accd-')}"/>
<c:set var="hghStyle" value="height:${tab.height}" unless="${empty tab.height}"/>
<div class="${c:cat(look,'accd')}" id="${self.uuid}"><%-- self.outerAttrs/innerAttrs gen below --%>
	<c:if test="${!empty self.tabbox.panelSpacing and self.index!=0}">
			<div style="margin:0;display:list-item;width:100%;height:${self.tabbox.panelSpacing};"></div>
	</c:if>	
	<div id="${tab.uuid}"${tab.outerAttrs}${tab.innerAttrs} z.sel="${tab.selected}"  z.type="zul.tab2.Tab2" z.box="${tab.tabbox.uuid}" z.panel="${self.uuid}"  width="100%" border="0" cellpadding="0" cellspacing="0" z.disabled="${tab.disabled}">		
		<div align="left" class="${c:cat(lookaccd,'header')}" >
			<c:if test="${tab.closable}">
				<a id="${tab.uuid}!close"  class="${c:cat(lookaccd,'closebtn')}"></a>
			</c:if>		
			<a href="javascript:;" id="${tab.uuid}!a" class="${c:cat(lookaccd,'left-tr')}">	
				<em class="${c:cat(lookaccd,'right-tr')}">
					<span class="${c:cat(lookaccd,'inner')}">
						<span class="${c:cat(lookaccd,'text')}">${tab.imgTag}<c:out value="${tab.label}"/></span>
					</span>
				</em>
			</a>
		</div>
	</div>
	<div id="${self.uuid}!real"${self.outerAttrs}${self.innerAttrs}>
		<div id="${self.uuid}!cave" >
			<c:forEach var="child" items="${self.children}">${z:redraw(child, null)}</c:forEach>
		</div>
	</div>
</div>
