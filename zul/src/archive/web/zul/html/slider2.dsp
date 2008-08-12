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
<div id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} z.type="zul.sld.Sld" 
	class="z-slider ${'horizontal' == self.orient ? 'z-slider-horz': 'z-slider-vert'}">
	<div class="z-slider-end">
		<div id="${self.uuid}!inner" class="z-slider-inner">
			<div id="${self.uuid}!btn" class="z-slider-thumb">
			</div>
			<a class="z-slider-focus" href="#" tabindex="-1" hidefocus="on">
			</a>
		</div>
	</div>
</div>