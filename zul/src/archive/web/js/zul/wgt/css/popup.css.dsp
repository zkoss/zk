<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-popup {
	position: absolute;
	top: 0;
	left: 0;
	visibility: hidden;
	border: 0 none;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	font-weight: normal;
	margin:0;
	overflow:hidden;
	padding:0;
}
.z-popup .z-popup-tl {
	background:transparent no-repeat scroll 0 top;
	background-image:url(${c:encodeURL('~./zul/img/popup/pp-corner.png')});
	font-size:0;
	height:5px;
	line-height:0;
	margin-right:5px;
	zoom:1;
}
.z-popup .z-popup-tr {
	background:transparent no-repeat scroll right -10px;
	background-image:url(${c:encodeURL('~./zul/img/popup/pp-corner.png')});
	font-size:0;
	height:5px;
	line-height:0;
	margin-right:-5px;
	position:relative;
	zoom:1;
}
.z-popup .z-popup-cm {
	background: #EDF6FC repeat-x 0 0px;
	background-image: url(${c:encodeURL('~./zul/img/popup/pp-cm.png')});
	padding:4px 10px;
	overflow: hidden;
	zoom: 1;
}
.z-popup .z-popup-cl {
	background: transparent  repeat-y 0 0;
	background-image: url(${c:encodeURL('~./zul/img/popup/pp-clr.png')});
	padding-left: 6px;
	overflow: hidden;
	zoom: 1;
}
.z-popup .z-popup-cr {
	background: transparent  repeat-y right;
	background-image: url(${c:encodeURL('~./zul/img/popup/pp-clr.png')});
	padding-right: 6px;
	overflow: hidden;
	zoom: 1;
}

.z-popup .z-popup-bl {
	background:transparent no-repeat scroll 0 -5px;
	background-image:url(${c:encodeURL('~./zul/img/popup/pp-corner.png')});
	height:5px;
	margin-right:5px;
	zoom: 1;
}
.z-popup .z-popup-br {
	background:transparent no-repeat scroll right -15px;
	background-image:url(${c:encodeURL('~./zul/img/popup/pp-corner.png')});
	font-size:0;
	height:5px;
	line-height:0;
	margin-right:-5px;
	position:relative;
	zoom: 1;
}
.z-popup .z-popup-cnt {
	margin: 0 !important;
	line-height: 14px;
	color: #444;
	padding: 0;
	zoom: 1;
}

<%-- IE 6 GIF  --%>
<c:if test="${c:browser('ie6-')}">
.z-popup .z-popup-tl,
.z-popup .z-popup-tr,
.z-popup .z-popup-bl,
.z-popup .z-popup-br {
	background-image:url(${c:encodeURL('~./zul/img/popup/pp-corner.gif')});
}
.z-popup .z-popup-cl,
.z-popup .z-popup-cr {
	background-image: url(${c:encodeURL('~./zul/img/popup/pp-clr.gif')});
}
.z-popup .z-popup-cm {
	background-image: url(${c:encodeURL('~./zul/img/popup/pp-cm.gif')});
}
</c:if>
