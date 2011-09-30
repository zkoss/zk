<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-fieldset legend{
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; 
	font-weight: normal;
}
.z-fieldset-cnt {
	overflow: hidden;
}
.z-fieldset-colpsd {
	padding-bottom: 0 !important; 
	border-width: 2px 0 0 0 !important;
}
.z-fieldset-colpsd .z-fieldset-cnt {
	position: absolute; 
	left: -1000px; 
	top: -1000px;
}
.z-fieldset .z-caption {
	cursor: pointer;
}
.z-fieldset .z-caption-readonly {
	cursor: default;
}

<%-- 3D --%>
.z-groupbox {
	margin: 0;
	overflow: hidden;
	padding: 0;
}

.z-groupbox-tl,
.z-groupbox-tr {
	background: none;
	font-size:0;
	line-height:0;
	height: 0;
	margin-right: 0;
	zoom:1;
}
.z-groupbox-tr {
	position:relative;
}
.z-groupbox-hl {
	background: none;
	border-bottom: 0;
	padding-left: 0;
	position: relative;
	zoom: 1;
}
.z-groupbox-hr {
	background: none;
	padding-right: 0;
	zoom: 1;
}
.z-groupbox-hm {
	background:transparent repeat-x 0 0;
	background-image:url(${c:encodeURL('~./zul/img/layout/borderlayout-hm.png')});
	overflow: hidden;
	border: 1px solid #C5C5C5;
	zoom: 1;
}
.z-groupbox-hm {
	cursor: pointer;
}
.z-groupbox-hm-readonly {
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
	padding: 4px 5px;
	border: 0 none;
	background: transparent;
}

.z-groupbox-cnt {<%-- content of 3d groupbox-new2 --%>
	border: 1px solid #C5C5C5;
	padding: 5px;
}
.z-groupbox-bl, 
.z-groupbox-br, 
.z-groupbox-bm {
	font-size: 0; 
	line-height: 0;
	zoom: 1;
}
.z-groupbox-bl {
	background: none;
	padding-left: 0;
	height: 0;
}
.z-groupbox-br {
	background: none;
	padding-right: 0;
	height: 0;
}
.z-groupbox-bm {
	background: none;
	height: 0;
}

<c:if test="${c:isExplorer7()}">
.z-groupbox-cnt {
	zoom: 1;
}
</c:if>
<%-- IE 6 GIF  --%>
<c:if test="${c:browser('ie6-')}">
.z-groupbox-hm {
	background-image:url(${c:encodeURL('~./zul/img/layout/borderlayout-hm.gif')});
}
</c:if>