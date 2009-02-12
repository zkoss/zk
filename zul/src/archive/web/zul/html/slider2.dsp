<%--
slider2.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 6 21:06:03     2008, Created by robbiecheng
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="zcls" value="${self.zclass}"/>
<c:if test='${self.mold == "scale"}'><div id="${self.uuid}" class="${zcls}-tick"></c:if>
<div id="${self.uuid}${self.mold == 'scale' ? '!real' : ''}"${self.outerAttrs}${self.innerAttrs} z.type="zul.sld.Sld">
	<div class="${zcls}-end">
		<div id="${self.uuid}!inner" class="${zcls}-center">
			<div id="${self.uuid}!btn" class="${zcls}-btn"></div>
		</div>
	</div>
</div>
<c:if test='${self.mold == "scale"}'></div></c:if>