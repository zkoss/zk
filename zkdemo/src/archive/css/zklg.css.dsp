<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>

<c:set var="fontSizeM" value="15px" scope="request" if="${empty fontSizeM}"/>
<c:set var="fontSizeMS" value="13px" scope="request" if="${empty fontSizeMS}"/>
<c:set var="fontSizeS" value="13px" scope="request" if="${empty fontSizeS}"/>
<c:set var="fontSizeXS" value="12px" scope="request" if="${empty fontSizeXS}"/>

<c:include page="~./zul/css/norm.css.dsp"/>
${z:outDeviceCSSContent('ajax')}