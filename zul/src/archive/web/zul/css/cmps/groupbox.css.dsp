<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-fieldset legend{
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
}
.z-fieldset-cnt {
	overflow: hidden;
}
.z-fieldset-collapsed {
	padding-bottom: 0 !important; border-width: 2px 0 0 0 !important;
}
.z-fieldset-collapsed .z-fieldset-cnt {
	visibility: hidden; position: absolute; left: -1000px; top: -1000px;
}

<%-- 3D --%>
.z-groupbox-cnt {<%-- content of 3d groupbox --%>
	border: 1px solid #B2CCD9; padding: 5px;
}
.z-groupbox-bm {
	background:transparent url(${c:encodeURL('~./img/shdmd.gif')}) repeat-x 0 0;
	height: 6px; font-size: 0; line-height: 0; zoom: 1;
}
.z-groupbox-bl {
	background:transparent url(${c:encodeURL('~./img/shdlf.gif')}) no-repeat 0 bottom;
	padding-left: 6px; zoom: 1;
}
.z-groupbox-br {
	background:transparent url(${c:encodeURL('~./img/shdrg.gif')}) no-repeat right bottom;
	padding-right: 6px; zoom: 1;
}
.z-groupbox-header {
	overflow: hidden; zoom: 1;
}
.z-groupbox-tl .z-groupbox-header {
	color: #373737;
	font-family: ${fontFamilyT};font-size: ${fontSizeM};
	font-weight: normal;
	padding: 5px 0 4px 0;
	border: 0 none; background: transparent;
}
.z-groupbox-tm {
	background: transparent url(${c:encodeURL('~./zul/img/groupbox/groupbox-tb.png')}) repeat-x 0 0;
	overflow: hidden;
}
.z-groupbox-tl {
	background: transparent url(${c:encodeURL('~./zul/img/groupbox/groupbox-corners.png')}) no-repeat 0 0;
	padding-left: 6px; zoom: 1; border-bottom: 1px solid #B2CCD9;
}
.z-groupbox-tr {
	background: transparent url(${c:encodeURL('~./zul/img/groupbox/groupbox-corners.png')}) no-repeat right 0;
	zoom: 1; padding-right: 6px;
}
