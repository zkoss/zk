<%--
calendar.dsp

{{IS_NOTE
	$Id: calendar.dsp,v 1.3 2006/05/08 02:36:39 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Mon Apr 24 17:13:31     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<table id="${self.uuid}" zk_type="zul.html.db.Cal"${self.outerAttrs}${self.innerAttrs}></table>