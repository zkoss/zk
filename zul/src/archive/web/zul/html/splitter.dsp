<%--
splitter.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  8 15:11:44     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" zk_type="zul.splt.Splt"${self.outerAttrs}${self.innerAttrs}><img id="${self.uuid}!btn" style="display:none" src="${c:encodeURL('~./zul/img/splt/colps-l.gif')}"/></div>