<%--
popup.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Mon Jun  5 11:03:53     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="zcls" value="${self.zclass}"/>
<div id="${self.uuid}" z.type="zul.widget.Pop"${self.outerAttrs}${self.innerAttrs}>
	<div class="${zcls}-tl">
		<div class="${zcls}-tr">
		</div>
	</div>

	<div id="${self.uuid}!bwrap" class="${zcls}-cl">
		<div class="${zcls}-cr">
			<div class="${zcls}-cm">
				<div id="${self.uuid}!cave" class="${zcls}-cnt">
					<c:forEach var="child" items="${self.children}">
						${z:redraw(child, null)}
					</c:forEach>
				</div>
			</div>
		</div>
	</div>

	<div class="${zcls}-bl">
		<div class="${zcls}-br">
		</div>
	</div>
</div>