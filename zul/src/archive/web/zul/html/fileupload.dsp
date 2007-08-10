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
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
<c:set var="hgh" value="${self.number*16+30}pt" if="${self.number>3}"/>
<iframe style="width:100%;height:${hgh}" frameborder="0" src="${c:encodeURL('~./zul/html/fileuploaddlg.dsp')}?dtid=${self.desktop.id}&amp;uuid=${self.uuid}&amp;max=${self.number}">
</iframe>
</div>
