<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:include page="~./zul/css/norm.css.dsp"/>

td.slider-bkr, td.slider-bkl, td.slidersph-bkr, td.slidersph-bkl {
	display: none; <%-- Bug 1825822 --%>
}

<%-- Append New --%>
option {
	font-family: Verdana, Tahoma, Arial, serif;
	font-size: xx-small; font-weight: normal;
}
