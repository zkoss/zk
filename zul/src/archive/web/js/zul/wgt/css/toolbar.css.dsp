<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-toolbar {
	border-color: #B1CBD5; border-style: solid; border-width: 0 0 1px 0; display: block;
	padding: 2px;
	background: #DAF3FF repeat-x 0 center;
	background-image: url(${c:encodeURL('~./zul/img/common/bar-bg.png')});
	position: relative; zoom: 1;
}
.z-caption .z-toolbar, .z-caption .z-toolbarbutton {
	background: none; border: 0;
}
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
.z-toolbar-button {
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	font-weight: normal;
}
.z-caption .z-toolbar a:hover {
	text-decoration: underline;
}

<%-- toolbar horizontal alignment --%>
.z-toolbar-start {
	float: left; clear: none;
}
.z-toolbar-center {
	text-align: center;
	margin: 0 auto;
}
.z-toolbar-end {
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

.z-toolbar-panel .z-toolbar-panel-start .z-toolbar-panel-cnt{
	clear: none;
	float: left;
}

.z-toolbar-panel .z-toolbar-panel-center{
	text-align: center;
}
.z-toolbar-panel .z-toolbar-panel-center .z-toolbar-panel-cnt{
	margin: 0 auto;
}

.z-toolbar-panel .z-toolbar-panel-end .z-toolbar-panel-cnt{
	clear: none;
	float: right;
}

<%-- Toolbar Button--%>
.z-toolbar-button-disd {
	color: #C5CACB !important; cursor: default!important;
}
.z-toolbar-button-disd:visited, .z-toolbar-button-disd:hover {
	text-decoration: none !important; cursor: default !important;;
	border-color: #D0DEF0 !important;
}