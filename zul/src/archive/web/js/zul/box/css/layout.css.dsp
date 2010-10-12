<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
.z-hlayout, .z-vlayout {
	overflow: hidden;
<c:if test="${c:isExplorer() and not c:browser('ie8')}">
	position: relative;
</c:if>
}
.z-hlayout {
	white-space: nowrap;
}
.z-hlayout-inner {
	display:-moz-inline-box;
	display: inline-block;
	position: relative;
	vertical-align: top;
	zoom: 1;
<c:if test="${c:isExplorer() and not c:browser('ie8')}">
	display: inline;
</c:if>
}
.z-vlayout-inner {
	position: relative;
	zoom: 1;
}
