<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
.z-anchorlayout,
.z-anchorlayout-body,
.z-anchorchildren {
	overflow: hidden;
}

.z-anchorchildren {
    float: left; padding: 0; margin: 0;
}

<c:if test="${c:isExplorer()}">
.z-anchorlayout,
.z-anchorchildren {
    zoom: 1;
}
</c:if>