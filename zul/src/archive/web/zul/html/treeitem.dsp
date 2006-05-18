<%--
treeitem.dsp

{{IS_NOTE
	$Id: treeitem.dsp,v 1.3 2006/03/10 08:31:47 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu Jul  7 16:11:48     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
${u:redraw(self.treerow, null)}
${u:redraw(self.treechildren, null)}
