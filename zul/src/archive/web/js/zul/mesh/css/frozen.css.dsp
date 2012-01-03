<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
.z-frozen, .z-frozen-body, .z-frozen-inner {
	overflow: hidden;
}
.z-frozen {
	background-image: url(${c:encodeThemeURL('~./zul/img/common/bar-bg.png')});
}
.z-frozen-body {
	float: left;
}
.z-frozen-inner {
	overflow-x: scroll;
	float: right;
	<c:if test="${zk.ie > 0}">
		<%-- Bug 3244126: scroll bar cannot click then move with IE --%>
		padding-top: 1px;
		margin-top: -1px;
	</c:if>
}
.z-frozen-inner div {
	height: 100%;
}