<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-panel-move-ghost {
	overflow: hidden; position: absolute; filter: alpha(opacity=65) !important; <%-- IE --%>
	background: #CBDDF3; opacity: .65 !important; cursor: move !important;
}
.z-panel-move-ghost ul {
	margin: 0; padding: 0; overflow: hidden; font-size: 0; line-height: 0;
	border: 1px solid #B1CBD5; display: block; background: #cbddf3;
}
.z-panel-move-ghost ul {
	border-top-width: 0;
}
.z-panel-move-block {
	border: 2px dashed #B1CBD5;
}

.z-panel {
	border-style: solid; border-color: #B1CBD5; border-width: 0; overflow: hidden;
}
.z-panel-header {
	overflow: hidden; zoom: 1; color: #15428b; font: bold ${fontSizeM} ${fontFamilyT};
	padding: 5px 3px 4px 5px; border: 1px solid #B1CBD5; line-height: 15px; 
	background:transparent url(${c:encodeURL('~./zul/img/panel/panel-tb.png')}) repeat-x 0 -1px;
}
.z-panel-children {
	border: 1px solid #B1CBD5; border-top: 0 none; overflow: hidden; background: white;
	position: relative;
}
.z-panel-bbar .z-toolbar {
	border: 1px solid #B1CBD5; border-top: 0 none; overflow: hidden; padding: 2px;
}
.z-panel-tbar .z-toolbar {
	border: 1px solid #B1CBD5; border-top: 0 none; overflow: hidden; padding: 2px;
}
.z-panel-tbar .z-toolbar {
	border-top: 1px solid #B1CBD5; border-bottom: 0 none;
}
.z-panel-children-noheader, .z-panel-cm .z-panel-children {
	border-top: 1px solid #B1CBD5;
}
.z-panel-header {
	overflow: hidden; zoom: 1;
}
.z-panel-tl .z-panel-header {
	color: #15428b; font: bold ${fontSizeM} ${fontFamilyT}; padding: 5px 0 4px 0;
	border: 0 none; background: transparent;
}
.z-panel-tm {
	background: transparent url(${c:encodeURL('~./zul/img/panel/panel-tb.png')}) repeat-x 0 0;
	overflow: hidden;
}
.z-panel-tl {
	background: transparent url(${c:encodeURL('~./zul/img/panel/panel-corners.png')}) no-repeat 0 0;
	padding-left: 6px; zoom: 1; border-bottom: 1px solid #B1CBD5;
}
.z-panel-tr {
	background: transparent url(${c:encodeURL('~./zul/img/panel/panel-corners.png')}) no-repeat right 0;
	zoom: 1; padding-right: 6px;
}
.z-panel-header-noheader {
	border-bottom: 0px;
}
.z-panel-header-noheader .z-panel-tm {
	height: 6px; font-size: 0; line-height: 0;
}
.z-panel-bm {
	background: transparent url(${c:encodeURL('~./zul/img/panel/panel-tb.png')}) repeat-x 0 bottom;
	zoom: 1;
}
.z-panel-bl {
	background: transparent url(${c:encodeURL('~./zul/img/panel/panel-corners.png')}) no-repeat 0 bottom;
	padding-left: 6px; zoom: 1;
}
.z-panel-br {
	background: transparent url(${c:encodeURL('~./zul/img/panel/panel-corners.png')}) no-repeat right bottom;
	padding-right: 6px; zoom: 1;
}
.z-panel-cm {
	border: 0 none; padding: 0; margin: 0; font: normal ${fontSizeM} ${fontFamilyT};
	padding-top: 6px; background: #D8ECF7;
}
.z-panel-cm .z-panel-children {
	border:1px solid #B1CBD5; background: transparent;
}
.z-panel-cl {
	background: #fff url(${c:encodeURL('~./zul/img/panel/panel-lr.gif')}) repeat-y 0 0;
	padding-left: 6px; zoom: 1;
}
.z-panel-cr {
	background: transparent url(${c:encodeURL('~./zul/img/panel/panel-lr.gif')}) repeat-y right 0;
	padding-right: 6px; zoom: 1;
}
.z-panel-bm .z-panel-fbar {
	padding-bottom: 6px;
}
.z-panel-nofbar .z-panel-bm {
	height: 6px; font-size: 0; line-height: 0;
}
.z-panel-children, .z-panel-body {
	overflow: hidden; zoom: 1;
}
.z-panel-noborder .z-panel-children-noborder {
	border-width: 0;
}
.z-panel-noborder .z-panel-header-noborder{
	border-width: 0; border-bottom: 1px solid #B1CBD5;
}
.z-panel-noborder .z-panel-tbar-noborder .z-toolbar {
	border-width: 0; border-bottom: 1px solid #B1CBD5;
}
.z-panel-noborder .z-panel-bbar-noborder .z-toolbar{
	border-width: 0; border-top: 1px solid #B1CBD5;
}
<%-- Panel Tool --%>
.z-panel-tool {
	overflow: hidden; width: 15px; height: 15px; float: right; cursor: pointer;
	background: transparent url(${c:encodeURL('~./zul/img/panel/tool-btn.gif')}) no-repeat;
	margin-left: 2px;
}
.z-panel-toggle {
	background-position: 0 -60px;
}
.z-panel-toggle-over {
	background-position: -15px -60px;
}
.z-panel-collapsed .z-panel-toggle {
	background-position: 0 -75px;
}
.z-panel-collapsed {
	height:auto !important;
}
.z-panel-collapsed .z-panel-toggle-over {
	background-position: -15px -75px;
}
.z-panel-close {
	background-position: 0 0;
}
.z-panel-close-over {
	background-position: -15px 0;
}
.z-panel-minimize {
	background-position: 0 -15px;
}
.z-panel-minimize-over {
	background-position: -15px -15px;
}
.z-panel-maximize {
	background-position: 0 -30px;
}
.z-panel-maximize-over {
	background-position: -15px -30px;
}
.z-panel-maximized {
	background-position: 0 -45px;
}
.z-panel-maximized-over {
	background-position: -15px -45px;
}