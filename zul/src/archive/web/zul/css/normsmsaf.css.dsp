<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<c:set var="fontSizeM" value="x-small" scope="request" if="${empty fontSizeM}"/>
<c:set var="fontSizeMS" value="9px" scope="request" if="${empty fontSizeMS}"/>
<c:set var="fontSizeS" value="xx-small" scope="request" if="${empty fontSizeS}"/>
<c:set var="fontSizeXS" value="xx-small" scope="request" if="${empty fontSizeXS}"/>

<c:include page="~./zul/css/normsaf.css.dsp"/>
