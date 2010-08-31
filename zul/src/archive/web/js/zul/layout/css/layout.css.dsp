<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
.z-hlayout, .z-vlayout {
	overflow: hidden;
}
.z-hlayout {
	white-space: nowrap;
}
.z-hlayout-inner {
	display:-moz-inline-box;
	display: inline-block;
	position: relative;
	vertical-align: middle;
	zoom: 1;
}
.z-vlayout-inner {
	position: relative;
	zoom: 1;
}
<c:if test="${c:isExplorer() and not c:browser('ie8')}">
.z-hlayout, .z-vlayout {
	position: relative;
}
.z-hlayout-inner {
	display: inline;
}
</c:if>