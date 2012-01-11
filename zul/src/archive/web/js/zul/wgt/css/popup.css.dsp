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
