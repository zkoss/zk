<%--
fisheye.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul  8 14:31:46 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zkex.zul.fisheye.Fisheye"${self.outerAttrs}${self.innerAttrs}><img id="${self.uuid}!img" src="${c:encodeURL(self.image)}"  class="${self.sclass}-image"/><div id="${self.uuid}!label" style="display:none;" class="${self.sclass}-label"><c:out value="${self.label}"/></div></div>