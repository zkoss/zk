<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<c:if test="${empty fontSizeM}">
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontSizeM')}"/>
<c:set var="fontSizeM" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="fontSizeM" value="12px" scope="request" if="${empty fontSizeM}"/>
</c:if>
<c:if test="${empty fontSizeMS}">
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontSizeMS')}"/>
<c:set var="fontSizeMS" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="fontSizeMS" value="11px" scope="request" if="${empty fontSizeMS}"/>
</c:if>
<c:if test="${empty fontSizeS}">
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontSizeS')}"/>
<c:set var="fontSizeS" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="fontSizeS" value="11px" scope="request" if="${empty fontSizeS}"/>
</c:if>
<c:if test="${empty fontSizeXS}">
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontSizeXS')}"/>
<c:set var="fontSizeXS" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="fontSizeXS" value="10px" scope="request" if="${empty fontSizeXS}"/>
</c:if>

<c:if test="${empty fontFamilyT}"><%-- title --%>
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontFamilyT')}"/>
<c:set var="fontFamilyT" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="fontFamilyT" value="arial, sans-serif"
	scope="request" if="${empty fontFamilyT}"/>
</c:if>
<c:if test="${empty fontFamilyC}"><%-- content --%>
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontFamilyC')}"/>
<c:set var="fontFamilyC" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="fontFamilyC" value="arial, sans-serif"
	scope="request" if="${empty fontFamilyC}"/>
</c:if>

.z-debug {
	width:80%; right:10px; bottom:5px;
	position:absolute; z-index: 99000; 
	overflow: auto; color: #7D9196;
	height: 300px; background: white;
	padding: 2px; border: 1px solid gray;
}
.z-debug .z-debug-header {
	overflow: hidden; zoom: 1; color: #403E39; font: normal ${fontSizeM} ${fontFamilyT};
	padding: 5px 3px 4px 5px; border: 1px solid #999884; line-height: 15px; 
	background:transparent url(${c:encodeURL('~./zk/img/debug/hd-gray.png')}) repeat-x 0 -1px;
	font-weight:bold;
}
.z-debug .z-debug-body {
	border: 1px solid #999884;
	border-top: 0;
}
.z-debug-close {
	overflow: hidden; width: 15px; height: 15px; float: right; cursor: pointer;
	background-color : transparent;
	background-image : url(${c:encodeURL('~./zk/img/debug/tool-btn.gif')});
	background-position : 0 0;
	background-repeat : no-repeat;
	margin-left: 2px;
}
.z-debug-close-over {
	background-position: -15px 0;
}
