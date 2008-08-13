<%--
slider.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 6 21:06:03     2008, Created by robbiecheng
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} z.type="zul.sld.Sld">
	<div class="${self.moldSclass}-end">
		<div id="${self.uuid}!inner" class="${self.moldSclass}-inner">
			<div id="${self.uuid}!btn" class="${self.moldSclass}-thumb"></div>
			<a class="${self.moldSclass}-focus" href="#" tabindex="-1" hidefocus="on"></a>
		</div>
	</div>
</div>