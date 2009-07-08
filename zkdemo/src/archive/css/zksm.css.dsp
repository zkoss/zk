<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>

<c:set var="fontSizeM" value="10px" scope="request" if="${empty fontSizeM}"/>
<c:set var="fontSizeMS" value="9px" scope="request" if="${empty fontSizeMS}"/>
<c:set var="fontSizeS" value="9px" scope="request" if="${empty fontSizeS}"/>
<c:set var="fontSizeXS" value="8px" scope="request" if="${empty fontSizeXS}"/>

<c:include page="~./zul/css/norm.css.dsp"/>
${z:outDeviceCSSContent('ajax')}
