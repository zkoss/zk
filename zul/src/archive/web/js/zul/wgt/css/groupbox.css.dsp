<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-groupbox-cnt {
	overflow: hidden;
	padding: 5px;
	border: 1px solid #C5C5C5;
	border-top: none;
}
.z-groupbox-notitle {
	border-top: 1px solid #C5C5C5;
}
.z-groupbox-colpsd {
	padding-bottom: 0 !important; 
	border-width: 2px 0 0 0 !important;
}
.z-groupbox .z-caption {
	cursor: pointer;
}
.z-groupbox .z-caption-readonly {
	cursor: default;
}

.z-groupbox .z-groupbox-hl {
	background: no-repeat;
	background-position: 0 center;
	background-image: url(${c:encodeThemeURL('~./zul/img/layout/groupbox-y.gif')});
}
.z-groupbox-colpsd .z-groupbox-hl {
	background: none;
}
.z-groupbox-hm {
	background: no-repeat;
	background-position: 0 center;
	background-image: url(${c:encodeThemeURL('~./zul/img/layout/groupbox-x.gif')});
}
.z-groupbox .z-groupbox-hr {
	background: no-repeat;
	background-position: right center;
	background-image: url(${c:encodeThemeURL('~./zul/img/layout/groupbox-y.gif')});
}
.z-groupbox-colpsd .z-groupbox-hr {
	background: none;
}
.z-groupbox-header {
	padding-left: 10px;
	padding-right: 10px;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	font-weight: normal;
	border: 0 none;
}
.z-groupbox-header span {
	background-color: #FFFFFF;
	padding-left: 3px;
	padding-right: 3px;
}
.z-groupbox-title {
	cursor: pointer;
}
<%-- 3D --%>
.z-groupbox-3d {
	margin: 0;
	overflow: hidden;
	padding: 0;
}

.z-groupbox-3d-tl,
.z-groupbox-3d-tr {
	background: none;
	font-size:0;
	line-height:0;
	height: 0;
	margin-right: 0;
	zoom:1;
}
.z-groupbox-3d-tr {
	position:relative;
}
.z-groupbox-3d-hl {
	background: none;
	border-bottom: 0;
	padding-left: 0;
	position: relative;
	zoom: 1;
}
.z-groupbox-3d-hr {
	background: none;
	padding-right: 0;
	zoom: 1;
}
.z-groupbox-3d-hm {
	background:transparent repeat-x 0 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/layout/groupbox-hm.png')});
	overflow: hidden;
	border: 1px solid #C5C5C5;
	zoom: 1;
	cursor: pointer;
}
.z-groupbox-3d-hm-readonly {
	cursor: default;
}
.z-groupbox-3d-header {
	overflow: hidden;
	zoom: 1;
}
.z-groupbox-3d-hl .z-groupbox-3d-header {
	color: #363636;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	font-weight: normal;
	padding: 4px 5px;
	border: 0 none;
	background: transparent;
}
.z-groupbox-3d-cnt {<%-- content of 3d groupbox-new2 --%>
	border: 1px solid #C5C5C5;
	padding: 5px;
}

<c:if test="${zk.ie >= 7}">
.z-groupbox-3d-cnt {
	zoom: 1;
}
</c:if>
<%-- IE 6 GIF  --%>
<c:if test="${zk.ie == 6}">
.z-groupbox-3d-hm {
	background-image: url(${c:encodeThemeURL('~./zul/img/layout/groupbox-hm.gif')});
}
</c:if>