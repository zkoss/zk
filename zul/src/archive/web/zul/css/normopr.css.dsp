<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<c:include page="~./zul/css/norm.css.dsp"/>

<c:choose>
<c:when  test="${!empty c:property('org.zkoss.zul.theme.enableZKPrefix')}">
.zk option {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeXS}; font-weight: normal;
}
</c:when>
<c:otherwise>
option {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeXS}; font-weight: normal;
}
</c:otherwise>
</c:choose>

<%-- Append New --%>
.z-messagebox-btn {
	width: 47pt;
}