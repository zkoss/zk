<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
.z-hlayout, .z-vlayout {
	overflow: hidden;
<c:if test="${zk.ie < 8}">
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
<c:if test="${not (zk.ie == 6)}">
	white-space: normal; <%-- Bug ZK-477 --%>
</c:if>
<c:if test="${zk.ie < 8}">
	display: inline;
</c:if>
}

<c:set var="dchild" value=">" unless="${zk.ie == 6}"/>
.z-valign-bottom ${dchild} .z-hlayout-inner {
	vertical-align: bottom;
}
.z-valign-top ${dchild} .z-hlayout-inner {
	vertical-align: top;
}
.z-valign-middle ${dchild} .z-hlayout-inner {
	vertical-align: middle;
}

.z-vlayout-inner {
	position: relative;
	zoom: 1;
}
