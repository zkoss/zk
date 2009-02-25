<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
.z-panel-move-ghost {
	position: absolute;
	background: #D7E6F7;
	overflow: hidden;
	filter: alpha(opacity=65) !important; <%-- IE --%>
	opacity: .65 !important;
	cursor: move !important;
}
.z-panel-move-ghost dl {
	border: 1px solid #B1CBD5;
	margin: 0; padding: 0;
	overflow: hidden;
	display: block;
	background: #D7E6F7;
	line-height: 0;
	font-size: 0;
}
.z-panel-move-block {
	border: 2px dashed #B1CBD5;
}
.z-panel {
	overflow: hidden;
}
.z-panel-children {
 	background: white;
	border: 1px solid #b1cbd5;
	border-top: 0;
	overflow: hidden;
	position: relative;
}
.z-panel-bbar .z-toolbar {
	border: 1px solid #b1cbd5;
	padding: 2px;
	border-top: 0 none;
	overflow: hidden;
}
.z-panel-tbar .z-toolbar {
	border: 1px solid #b1cbd5;
	padding: 2px;
	border-top: 0 none;
	overflow: hidden;
}
.z-panel-tbar.z-panel-noheader .z-toolbar {
	border-top: 1px solid #b1cbd5;
	border-bottom: 0;
	overflow: hidden;
}
.z-panel-cm .z-panel-tbar .z-toolbar {
	border-top: 1px solid #b1cbd5;
	border-bottom: 0 none;
}
.z-panel-children-noheader,
.z-panel-cm .z-panel-children {
	border-top: 1px solid #b1cbd5;
}
<%-- Top Left --%>
.z-panel-tl {
	background: transparent no-repeat 0 top;
	background-image: url(${c:encodeURL('~./zul/img/wnd/panel-corner.png')});
	margin-right: 5px;
	height: 5px;
	font-size: 0;
	line-height: 0;
	zoom: 1;
}
<%-- Top Right --%>
.z-panel-tr {
	background: transparent no-repeat right -10px;
	background-image: url(${c:encodeURL('~./zul/img/wnd/panel-corner.png')});
	position: relative;
	height: 5px;
	margin-right: -5px;
	font-size: 0;
	line-height:0;
	zoom: 1;
}
.z-panel-noheader {
	border-bottom: 0px;
}
<%-- Header --%>
.z-panel-header {
	overflow: hidden;
	color: #0F3B82;
	font: normal ${fontSizeM} ${fontFamilyT};
	padding: 5px 3px 4px 5px;
	border: 1px solid #b1cbd5;
	line-height: 15px;
	background:transparent repeat-x 0 -1px;
	background-image: url(${c:encodeURL('~./zul/img/wnd/panel-hm.png')});
	font-weight:bold;
	zoom: 1;
}
.z-panel-body,
.z-panel-children,
.z-panel-header {
	overflow: hidden;
	zoom: 1;
}
.z-panel-hm .z-panel-header {
	color: #0F3B82;
	padding: 0;
	padding-bottom: 4px;
	background: transparent;
	border: 0 none;
	font: normal ${fontSizeM} ${fontFamilyT};
	font-weight:bold;
}
<%-- Header Left --%>
.z-panel-hl {
	background: transparent no-repeat 0 0;
	background-image: url(${c:encodeURL('~./zul/img/wnd/panel-hl.png')});
	padding-left: 6px;
	border-bottom: 1px solid #A7DCF9;
	zoom: 1;
}
<%-- Header Right --%>
.z-panel-hr {
	background: transparent no-repeat right 0;
	background-image: url(${c:encodeURL('~./zul/img/wnd/panel-hr.png')});
	padding-right: 6px;
	zoom: 1;
}
<%-- Header Middle --%>
.z-panel-hm {
	background: transparent repeat-x 0 0;
	background-image: url(${c:encodeURL('~./zul/img/wnd/panel-hm.png')});
	overflow: hidden;
	zoom: 1;
}
<%-- Center Left --%>
.z-panel-cm {
	border: 0;
	margin: 0;
	background: #e4f0fa;
	padding-top: 6px;
}
.z-panel-cm.z-panel-noheader {
	padding: 0;
}
.z-panel-cm .z-panel-children {
	background: transparent;
	border: 1px solid #b1cbd5;
}
<%-- Center Left, Footer Left --%>
.z-panel-cl,
.z-panel-fl {
	background: transparent repeat-y 0 0;
	background-image: url(${c:encodeURL('~./zul/img/wnd/panel-clr.png')});
	padding-left: 6px;
	zoom: 1;
}
<%-- Center Right, Footer Right --%>
.z-panel-cr,
.z-panel-fr {
	background: transparent repeat-y right 0;
	background-image: url(${c:encodeURL('~./zul/img/wnd/panel-clr.png')});
	padding-right: 6px;
	zoom: 1;
}
<%-- Footer Middle --%>
.z-panel-fm {
	background: #e4f0fa;
	overflow: hidden;
}
<%-- Bottom Left --%>
.z-panel-bl {
	background: transparent no-repeat 0 -5px;
	background-image: url(${c:encodeURL('~./zul/img/wnd/panel-corner.png')});
	height: 5px;
	margin-right: 5px;
	zoom: 1;
}
<%-- Bottom Right --%>
.z-panel-br {
	background: transparent no-repeat right bottom;
	background-image: url(${c:encodeURL('~./zul/img/wnd/panel-corner.png')});
	position: relative;
	height: 5px;
	margin-right: -5px;
	font-size: 0;
	line-height:0;
	zoom: 1;
}
.z-panel-nofbar {
	display: none;
}
.z-panel-noborder .z-panel-children-noborder {
	border-width: 0;
}
.z-panel-noborder .z-panel-header-noborder {
	border-width: 0;
	border-bottom: 1px solid #b1cbd5;
}
.z-panel-noborder .z-panel-tbar-noborder .z-toolbar {
	border-width: 0;
	border-bottom: 1px solid #b1cbd5;
}
.z-panel-noborder .z-panel-bbar-noborder .z-toolbar {
	border-width: 0;
	border-top: 1px solid #b1cbd5;
}
<%-- Panel Tool --%>
.z-panel-icon {
	overflow: hidden; width: 15px; height: 15px; float: right; cursor: pointer;
	background-color : transparent;
	background-image : url(${c:encodeURL('~./zul/img/wnd/btn.gif')});
	background-position : 0 0;
	background-repeat : no-repeat;
	margin-left: 2px;
}
.z-panel-toggle {
	background-position: 0 -60px;
}
.z-panel-toggle-over {
	background-position: -15px -60px;
}
.z-panel-colpsd .z-panel-toggle {
	background-position: 0 -75px;
}
.z-panel-colpsd {
	height:auto !important;
}
.z-panel-colpsd .z-panel-toggle-over {
	background-position: -15px -75px;
}
.z-panel-close {
	background-position: 0 0;
}
.z-panel-close-over {
	background-position: -15px 0;
}
.z-panel-min {
	background-position: 0 -15px;
}
.z-panel-min-over {
	background-position: -15px -15px;
}
.z-panel-max {
	background-position: 0 -30px;
}
.z-panel-max-over {
	background-position: -15px -30px;
}
.z-panel-maxd {
	background-position: 0 -45px;
}
.z-panel-maxd-over {
	background-position: -15px -45px;
}