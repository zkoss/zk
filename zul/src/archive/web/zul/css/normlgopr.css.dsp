<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<c:set var="fontSizeM" value="medium" scope="request" if="${empty fontSizeM}"/>
<c:set var="fontSizeS" value="small" scope="request" if="${empty fontSizeS}"/>
<c:set var="fontSizeXS" value="x-small" scope="request" if="${empty fontSizeXS}"/>

<c:include page="~./zul/css/normopr.css.dsp"/>
