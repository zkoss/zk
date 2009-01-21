<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-popup {
	position: absolute; top: 0; left: 0; visibility: hidden; border: 0;
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
}
.z-popup .z-popup-tm {
	background: transparent url(${c:encodeURL('~./zul/img/popup/pp-tb.png')}) repeat-x 0 0;
	overflow: hidden; zoom: 1; font-size: 0; line-height: 0; height: 8px;
}
.z-popup .z-popup-tl {
	background: transparent url(${c:encodeURL('~./zul/img/popup/pp-corners.png')}) no-repeat 0 0;
	padding-left: 8px; overflow: hidden; zoom: 1;
}
.z-popup .z-popup-tr {
	background:transparent url(${c:encodeURL('~./zul/img/popup/pp-corners.png')}) no-repeat right -8px;
	overflow: hidden; zoom: 1; padding-right: 8px;
}
.z-popup .z-popup-cm {
	background: #E5F3FB url(${c:encodeURL('~./zul/img/popup/pp-tb.png')}) repeat-x 0 -16px;
	padding:2px 3px; overflow: hidden; zoom: 1;
}
.z-popup .z-popup-cl {
	background: transparent url(${c:encodeURL('~./zul/img/popup/pp-l.png')}) repeat-y 0;
	padding-left: 4px; overflow: hidden; zoom: 1;
}
.z-popup .z-popup-cr {
	background: transparent url(${c:encodeURL('~./zul/img/popup/pp-r.png')}) repeat-y right;
	padding-right: 4px; overflow: hidden; zoom: 1;
}
.z-popup .z-popup-bm {
	background: transparent url(${c:encodeURL('~./zul/img/popup/pp-tb.png')}) repeat-x 0 -8px;
	height: 8px; overflow: hidden; zoom: 1;
}
.z-popup .z-popup-bl {
	background: transparent url(${c:encodeURL('~./zul/img/popup/pp-corners.png')}) no-repeat 0 -16px;
	zoom: 1; padding-left: 8px;
}
.z-popup .z-popup-br {
	background: transparent url(${c:encodeURL('~./zul/img/popup/pp-corners.png')}) no-repeat right -24px;
	zoom:1; padding-right: 8px;
}
.z-popup .z-popup-cnt {
	margin: 0 !important; line-height: 14px; color: #444; padding: 0;
}