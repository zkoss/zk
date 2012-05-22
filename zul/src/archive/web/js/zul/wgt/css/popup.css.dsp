<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-popup, .z-popup-plain {
	position: absolute;
	top: 0;
	left: 0;
	border: 0 none;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	font-weight: normal;
	margin:0;
	overflow:hidden;
	padding:0;
	border-radius: 1px;
	-moz-border-radius: 1px;
	-webkit-border-radius: 1px;
	box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.35);
	-moz-box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.35);
	-webkit-box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.35);
}
.z-popup .z-popup-cl {
	background: transparent repeat-x 0 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/popup/popup-bg.png')});
	background-position: 0 -2px;
	border: 1px solid #CFCFCF;
	padding-left: 0;
}
.z-popup .z-popup-cnt {
	margin: 0 !important;
	line-height: 14px;
	color: #555555;
	padding: 5px 7px 5px 7px;
	zoom: 1;
}

<%-- IE 6 GIF  --%>
<c:if test="${zk.ie == 6}">
.z-popup .z-popup-cl {
	background-image: url(${c:encodeThemeURL('~./zul/img/popup/popup-bg.gif')});
}
</c:if>

<%-- notification --%>
.z-notification {
	position: absolute;
	top: 0;
	left: 0;
	border: 0 none;
	margin: 0;
	padding: 0;
	background: none;
}
.z-notification .z-notification-cl,
.z-notification .z-notification-cnt {
	width: 250px;
	height: 100px;
}
.z-notification-ref .z-notification-cl,
.z-notification-ref .z-notification-cnt {
	width: 125px;
	height: 50px;
}
.z-notification .z-notification-cl {
	padding: 15px;
	padding-left: 55px;
	border-radius: 10px;
	-moz-border-radius: 10px;
	box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.35);
	-moz-box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.35);
	background: transparent no-repeat 15px 15px;
}
.z-notification-ref .z-notification-cl {
	overflow: hidden;
	padding: 5px;
	padding-left: 55px;
	border-radius: 5px;
	-moz-border-radius: 5px;
	background-position: 17px 18px;
}
.z-notification .z-notification-cnt {
	overflow: hidden;
	zoom: 1;
	
	margin: 0 !important;
	background: none;
	font-family: ${fontFamilyC};
	font-size: 16px;
	font-weight: normal;
}
.z-notification-ref .z-notification-cnt {
	display: table-cell;
	vertical-align: middle;
	font-size: ${fontSizeM};
}
.z-notification .z-notification-pointer {
	position: absolute;
	display: none;
	width: 0;
	height: 0;
	border: 10px solid transparent;
}

<%-- notification: base style --%>
.z-notification .z-notification-cl {
	background-color: ${zk.ie <= 8 ? "#444444" : "rgba(51, 51, 51, 0.9)"}; <%-- bug ZK-1135: IE8 does not support rgba --%>
	color: #FFFFFF;
}
.z-notification .z-notification-pointer-l,
.z-notification .z-notification-pointer-r,
.z-notification .z-notification-pointer-u,
.z-notification .z-notification-pointer-d {
	border: 10px solid transparent;
}
.z-notification .z-notification-pointer-l {
	border-right-color: ${zk.ie < 8 ? "#444444" : "rgba(51, 51, 51, 0.9)"};
}
.z-notification .z-notification-pointer-r {
	border-left-color: ${zk.ie < 8 ? "#444444" : "rgba(51, 51, 51, 0.9)"};
}
.z-notification .z-notification-pointer-u {
	border-bottom-color: ${zk.ie < 8 ? "#444444" : "rgba(51, 51, 51, 0.9)"};
}
.z-notification .z-notification-pointer-d {
	border-top-color: ${zk.ie < 8 ? "#444444" : "rgba(51, 51, 51, 0.9)"};
}

<%-- bug ZK-1135: IE8 does not support rgba --%>
<c:set var="infoColor" value="${zk.ie <= 8 ? '#3CA7B1' : 'rgba(33, 155, 166, 0.88)'}" />
<c:set var="warningColor" value="${zk.ie <= 8 ? '#ED5A33' : 'rgba(234, 67, 23, 0.88)'}" />
<c:set var="errorColor" value="${zk.ie <= 8 ? '#C61F23' : 'rgba(190, 0, 5, 0.88)'}" />

<%-- notification: info --%>
.z-notification-info .z-notification-cl {
	background-color: ${infoColor};
	background-image: url(${c:encodeThemeURL('~./zul/img/popup/notif-info.png')});
}
<c:if test="${zk.ie == 6}">
.z-notification-info .z-notification-cl {
	background-image: url(${c:encodeThemeURL('~./zul/img/popup/notif-info.gif')});
}
</c:if>
.z-notification-info .z-notification-pointer-l {
	border-right-color: ${infoColor};
}
.z-notification-info .z-notification-pointer-r {
	border-left-color: ${infoColor};
}
.z-notification-info .z-notification-pointer-u {
	border-bottom-color: ${infoColor};
}
.z-notification-info .z-notification-pointer-d {
	border-top-color: ${infoColor};
}

<%-- notification: warning --%>
.z-notification-warning .z-notification-cl {
	background-color: ${warningColor};
	background-image: url(${c:encodeThemeURL('~./zul/img/popup/notif-warning.png')});
}
<c:if test="${zk.ie == 6}">
.z-notification-warning .z-notification-cl {
	background-image: url(${c:encodeThemeURL('~./zul/img/popup/notif-warning.gif')});
}
</c:if>
.z-notification-warning .z-notification-pointer-l {
	border-right-color: ${warningColor};
}
.z-notification-warning .z-notification-pointer-r {
	border-left-color: ${warningColor};
}
.z-notification-warning .z-notification-pointer-u {
	border-bottom-color: ${warningColor};
}
.z-notification-warning .z-notification-pointer-d {
	border-top-color: ${warningColor};
}

<%-- notification: error --%>
.z-notification-error .z-notification-cl {
	background-color: ${errorColor};
	background-image: url(${c:encodeThemeURL('~./zul/img/popup/notif-error.png')});
}
<c:if test="${zk.ie == 6}">
.z-notification-error .z-notification-cl {
	background-image: url(${c:encodeThemeURL('~./zul/img/popup/notif-error.gif')});
}
</c:if>
.z-notification-error .z-notification-pointer-l {
	border-right-color: ${errorColor};
}
.z-notification-error .z-notification-pointer-r {
	border-left-color: ${errorColor};
}
.z-notification-error .z-notification-pointer-u {
	border-bottom-color: ${errorColor};
}
.z-notification-error .z-notification-pointer-d {
	border-top-color: ${errorColor};
}
