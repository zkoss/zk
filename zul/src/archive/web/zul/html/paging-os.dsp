<%--
paging-os.dsp

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
<div id="${self.uuid}" z.type="zul.pg.PgOS"${self.outerAttrs}${self.innerAttrs}>${self.innerTags}</div>