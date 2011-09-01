<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-toolbar {
	border-color: #B1CBD5; border-style: solid; border-width: 0 0 1px 0; display: block;
	padding: 2px;
	background: #DAF3FF repeat-x 0 center;
	background-image: url(${c:encodeURL('~./zul/img/common/bar-bg.png')});
	position: relative; zoom: 1;
}
.z-caption .z-toolbar {
	background: none; border: 0;
}
.z-toolbar-tabs-body, .z-toolbar-tabs-body span,
.z-toolbar-body, .z-toolbar-body span {
	font-size: ${fontSizeS};
}
.z-toolbar a, .z-toolbar a:visited, .z-toolbar a:hover {
	font-family: ${fontFamilyT};
	font-size: ${fontSizeS}; font-weight: normal; color: black;
	background: #D0DEF0; border: 1px solid #D0DEF0;
	text-decoration: none;
}
.z-toolbar a:hover {
	border-color: #f8fbff #aca899 #aca899 #f8fbff;
}
.z-caption .z-toolbar a:hover {
	text-decoration: underline;
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
	vertical-align: middle;:
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

<c:if test="${c:isExplorer() and not c:browser('ie8')}">
.z-toolbarbutton {
	display: inline;
}

<c:if test="${c:browser('ie6-')}">
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
.z-toolbarbutton-disd ${c:isExplorer() ? '*': ''} { <%-- bug 3022237 --%>
	opacity: .5;
	-moz-opacity: .5;
	filter: alpha(opacity=50);
}