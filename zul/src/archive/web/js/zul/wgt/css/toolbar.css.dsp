<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-toolbar {
	border-color: #C5C5C5; 
	border-style: solid; 
	border-width: 0 0 1px; 
	display: block;
	padding: 2px;
	background: repeat-x 0 -1px #EEEEEE;
	background-image: url(${c:encodeThemeURL('~./zul/img/common/toolbar-hm.png')});
	position: relative; zoom: 1;
}
.z-caption .z-toolbar {
	background: none; border: 0;
}
.z-toolbar-tabs-body, .z-toolbar-tabs-body span,
.z-toolbar-body, .z-toolbar-body span {
	font-size: ${fontSizeS};
}
<%-- toolbar horizontal alignment --%>
.z-toolbar-start,
.z-toolbar-panel .z-toolbar-panel-start .z-toolbar-panel-cnt {
	float: left; clear: none;
}
.z-toolbar-center,
.z-toolbar-panel .z-toolbar-panel-center .z-toolbar-panel-cnt {
	text-align: center;
	margin: 0 auto;
}
.z-toolbar-end,
.z-toolbar-panel .z-toolbar-panel-end .z-toolbar-panel-cnt {
	float: right; clear: none;
}
<%-- Toolbar Panel Mold--%>
.z-toolbar-panel {
	padding: 5px;
}

.z-toolbar-panel .z-toolbar-panel-body .z-toolbar-panel-hor,
.z-toolbar-panel .z-toolbar-panel-body .z-toolbar-panel-ver {
	border: 0;
	padding: 0;
}
.z-toolbar-panel .z-toolbar-panel-cnt .z-toolbar-panel-hor{
	padding: 3px;
}
.z-toolbar-panel .z-toolbar-panel-cnt .z-toolbar-panel-ver{
	padding: 1px;
}

.z-toolbar-panel .z-toolbar-panel-center{
	text-align: center;
}



<%-- default --%>
.z-toolbarbutton .z-toolbarbutton-tl, 
.z-toolbarbutton .z-toolbarbutton-tr, 
.z-toolbarbutton .z-toolbarbutton-bl, 
.z-toolbarbutton .z-toolbarbutton-br{
	background-image: url(${c:encodeThemeURL('~./zul/img/button/toolbarbtn-corner.gif')});
}
.z-toolbarbutton .z-toolbarbutton-tm, 
.z-toolbarbutton .z-toolbarbutton-bm  {
	background-image: url(${c:encodeThemeURL('~./zul/img/button/toolbarbtn-x.gif')});
}
.z-toolbarbutton .z-toolbarbutton-cl, 
.z-toolbarbutton .z-toolbarbutton-cr {
	background-image: url(${c:encodeThemeURL('~./zul/img/button/toolbarbtn-y.gif')});
}
.z-toolbarbutton .z-toolbarbutton-cm {
	background-image: url(${c:encodeThemeURL('~./zul/img/button/toolbarbtn-ctr.gif')});
}

.z-toolbarbutton .z-toolbarbutton-tl,
.z-toolbarbutton .z-toolbarbutton-tr,
.z-toolbarbutton .z-toolbarbutton-cl,
.z-toolbarbutton .z-toolbarbutton-cr {
	background-repeat: no-repeat;
	background-position: -4px 0;
	width: 4px; padding: 0; margin: 0;
}
.z-toolbarbutton .z-toolbarbutton-tl,
.z-toolbarbutton .z-toolbarbutton-tr {
	height: 4px;
}
.z-toolbarbutton .z-toolbarbutton-tl {
	background-position: 0 0;
}
.z-toolbarbutton .z-toolbarbutton-cl {
	background-position: 0 0; text-align: right;
}
.z-toolbarbutton .z-toolbarbutton-tm {
	background-repeat: repeat-x;
	background-position: 0 0;
}
.z-toolbarbutton .z-toolbarbutton-tr {
	background-position: -4px 0;
}
.z-toolbarbutton .z-toolbarbutton-cm {
	margin: 0; overflow: hidden;
	vertical-align: middle;
	text-align: center;
	padding: 0 5px;
	background-repeat: repeat-x;
	background-position: 0 0;
	white-space: nowrap;
}
.z-toolbarbutton .z-toolbarbutton-bl,
.z-toolbarbutton .z-toolbarbutton-br {
	background-repeat: no-repeat;
	background-position: 0 -4px;
	width: 4px; height: 4px;  padding: 0; margin: 0;
}
.z-toolbarbutton .z-toolbarbutton-bm {
	background-repeat: repeat-x;
	background-position: 0 -4px;
	height: 4px;
}
.z-toolbarbutton .z-toolbarbutton-br {
	background-position: -4px -4px;
}
<%-- mouseover --%>
.z-toolbarbutton-over .z-toolbarbutton-tl,
.z-toolbarbutton-over .z-toolbarbutton-cl {
	background-position: -8px 0;
}
.z-toolbarbutton-over .z-toolbarbutton-tm {
	background-position: 0 -8px;
}
.z-toolbarbutton-over .z-toolbarbutton-tr,
.z-toolbarbutton-over .z-toolbarbutton-cr {
	background-position: -12px 0;
}
.z-toolbarbutton-over .z-toolbarbutton-cm {
	background-position: 0 -500px;
}
.z-toolbarbutton-over .z-toolbarbutton-bl {
	background-position: -8px -4px;
}
.z-toolbarbutton-over .z-toolbarbutton-bm {
	background-position:0 -12px;
}
.z-toolbarbutton-over .z-toolbarbutton-br {
	background-position: -12px -4px;
}



<%-- Toolbarbutton --%>
.z-toolbarbutton {
	display:-moz-inline-box;
	display: inline-block;
	position: relative;
	cursor: pointer;
	margin: 0 2px;
	vertical-align: middle;	
	padding: 1px 0;
	zoom: 1;
}
.z-toolbarbutton-over {
	border-top: 1px solid #7EAAC6;
	border-bottom: 1px solid #7EAAC6;
	padding: 0;
}
.z-toolbarbutton-body {
	position: relative;
	margin: 0 -1px;
	padding: 0 1px;
	vertical-align: middle;
	zoom: 1;
}
.z-toolbarbutton-over .z-toolbarbutton-body {
	border-left: 1px solid #7EAAC6;
	border-right: 1px solid #7EAAC6;
	padding: 0;
}
.z-toolbarbutton-cnt {
	padding: 2px 2px;
	position: relative;
	zoom: 1;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeS};
	font-weight: normal;
}
.z-toolbarbutton-over .z-toolbarbutton-cnt {
	background-image: url(${c:encodeThemeURL('~./zul/img/button/toolbarbtn-ctr.gif')});
	background-position: 0 -500px;
}

<c:if test="${zk.ie < 8}">
.z-toolbarbutton {
	display: inline;
}

<c:if test="${zk.ie == 6}">
.z-toolbarbutton,
.z-toolbarbutton-body,
.z-toolbarbutton-cnt {
	display: inline;
	position: relative;
}
.z-toolbarbutton-body {
	float: left;
}
</c:if>
</c:if>
.z-toolbarbutton-disd * {
	color:gray !important;
	cursor:default !important;
}
.z-toolbarbutton-disd ${zk.ie > 0 ? '*': ''} { <%-- bug 3022237 --%>
	opacity: .5;
	-moz-opacity: .5;
	filter: alpha(opacity=50);
}
