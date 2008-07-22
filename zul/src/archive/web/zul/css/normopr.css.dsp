<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<c:include page="~./zul/css/norm.css.dsp"/>

<c:choose>
<c:when  test="${!empty c:getProperty('org.zkoss.zul.theme.enableZKPrefix')}">
.zk option {
	font-family: Verdana, Tahoma, Arial, serif;
	font-size: ${fontSizeXS}; font-weight: normal;
}
</c:when>
<c:otherwise>
option {
	font-family: Verdana, Tahoma, Arial, serif;
	font-size: ${fontSizeXS}; font-weight: normal;
}
</c:otherwise>
</c:choose>

td.slider-bkr, td.slider-bkl, td.slidersph-bkr, td.slidersph-bkl {
	display: none; <%-- Bug 1825822 --%>
}

<%-- Append New --%>
.messagebox-btn {
	width: 47pt;
}