<%@ page contentType="text/html;charset=UTF-8" %><%--
iframe.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 21 11:15:37     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%>
<iframe id="${requestScope.arg.self.uuid}" frameborder="0"${requestScope.arg.self.outerAttrs}${requestScope.arg.self.innerAttrs}>
Your browser doesn't support inline frames.
</iframe>
