<%--
fileupload.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu May 10 16:06:58     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
<iframe frameborder="0" src="${c:encodeURL('~./zul/html/fileuploaddlg.dsp')}" width="100%">
</iframe>
</div>
