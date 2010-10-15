<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-fieldset legend{
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
}
.z-fieldset-cnt {
	overflow: hidden;
}
.z-fieldset-colpsd {
	padding-bottom: 0 !important; border-width: 2px 0 0 0 !important;
}
.z-fieldset-colpsd .z-fieldset-cnt {
	position: absolute; left: -1000px; top: -1000px;
}
.z-fieldset .z-caption:hover {
	cursor: pointer;
}
.z-fieldset .z-caption-readonly:hover {
	cursor: default;
}

<%-- 3D --%>
.z-groupbox{
	margin:0;
	overflow:hidden;
	padding:0;
}

.z-groupbox-tl,
.z-groupbox-tr {
	background:transparent no-repeat 0 top;
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-corner.png')});
	font-size:0;
	height:5px;
	line-height:0;
	margin-right:5px;
	zoom:1;
}
.z-groupbox-tr {
	background-position: right -5px;
	margin-right:-5px;
	position:relative;
}

.z-groupbox-hl {
	background:transparent repeat-y 0 0;
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-hl.png')});
	border-bottom:1px solid #B2CCD9;
	padding-left:6px;
	zoom: 1;
}
.z-groupbox-hr {
	background:transparent repeat-y right 0;
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-hr.png')});
	padding-right:6px;
	zoom: 1;
}
.z-groupbox-hm {
	background:transparent repeat-x 0 0;
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-hm.png')});
	overflow:hidden;
	zoom: 1;
}
.z-groupbox-hm:hover {
	cursor: pointer;
}
.z-groupbox-hm-readonly:hover {
	cursor: default;
}
.z-groupbox-header {
	overflow: hidden;
	zoom: 1;
}
.z-groupbox-hl .z-groupbox-header {
	color: #373737;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	font-weight: normal;
	padding: 0 0 4px 0;
	border: 0 none;
	background: transparent;
}

.z-groupbox-cnt {<%-- content of 3d groupbox-new2 --%>
	border: 1px solid #B2CCD9;
	padding: 5px;
<c:if test="${c:isExplorer7()}">
	zoom: 1;
</c:if>
}

.z-groupbox-bl {
	background: transparent no-repeat 0 bottom;
	background-image: url(${c:encodeURL('~./img/shdlf.gif')});
	padding-left: 6px; height: 6px; font-size: 0; line-height: 0;
	zoom: 1;
}
.z-groupbox-br {
	background: transparent no-repeat right bottom;
	background-image: url(${c:encodeURL('~./img/shdrg.gif')});
	padding-right: 6px; height: 6px; font-size: 0; line-height: 0;
	zoom: 1;
}
.z-groupbox-bm {
	background: transparent repeat-x 0 0;
	background-image: url(${c:encodeURL('~./img/shdmd.gif')});
	height: 6px;
	font-size: 0;
	line-height: 0;
	zoom: 1;
}

<%-- IE 6 GIF  --%>
<c:if test="${c:browser('ie6-')}">
.z-groupbox-tl {
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-corner.gif')});
}
.z-groupbox-tr{
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-corner.gif')});
}

.z-groupbox-hl {
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-hl.gif')});
}
.z-groupbox-hr {
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-hr.gif')});
}
.z-groupbox-hm {
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-hm.gif')});
}
</c:if>