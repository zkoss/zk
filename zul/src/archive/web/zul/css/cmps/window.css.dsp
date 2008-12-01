<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-window-resize-proxy {
	border: 1px dashed #1854C2; position: absolute; overflow: hidden; left: 0; 
	top: 0; z-index: 50000; background-color: #CBDDF3; filter: alpha(opacity=50); <%-- IE --%>
	opacity: .5;
}
.z-window-move-ghost {
	overflow: hidden; position: absolute; filter: alpha(opacity=65) !important; <%-- IE --%>
	background: #CBDDF3; opacity: .65 !important; cursor: move !important;
}
.z-window-move-ghost .z-window-popup-tl {
	border-bottom: 0;
}
.z-window-move-ghost ul {
	margin: 0; padding: 0; overflow: hidden; font-size: 0; line-height: 0;
	border: 1px solid #538BA2; display: block; background: #cbddf3;
}
.z-window-embedded, .z-window-modal, .z-window-overlapped, .z-window-popup, .z-window-highlighted {
	margin: 0; padding: 0; overflow: hidden; zoom: 1;
}
<%-- Header --%>
.z-window-embedded-tl, .z-window-embedded-tl-noborder {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-l.png')}) no-repeat 0 0;
	padding-left: 6px; zoom: 1;
}
.z-window-modal-tl, .z-window-highlighted-tl, .z-window-overlapped-tl, .z-window-popup-tl,
.z-window-modal-tl-noborder, .z-window-highlighted-tl-noborder, .z-window-overlapped-tl-noborder, .z-window-popup-tl-noborder {
	padding-left: 6px; zoom: 1;
}
.z-window-modal-tl, .z-window-highlighted-tl, .z-window-overlapped-tl,
.z-window-modal-tl-noborder, .z-window-highlighted-tl-noborder, .z-window-overlapped-tl-noborder, .z-window-popup-tl-noborder {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-l-ol.png')}) no-repeat 0 0;
}
.z-window-popup-tl {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-l-pop.png')}) no-repeat 0 0;
}
.z-window-embedded-tm, .z-window-embedded-tm-noborder {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-m.png')}) repeat-x 0 0;
	overflow: hidden; zoom: 1;
}
.z-window-modal-tm, .z-window-highlighted-tm, .z-window-overlapped-tm, .z-window-popup-tm,
.z-window-modal-tm-noborder, .z-window-highlighted-tm-noborder, .z-window-overlapped-tm-noborder, .z-window-popup-tm-noborder {
	overflow: hidden; zoom: 1;
}
.z-window-modal-tm, .z-window-highlighted-tm, .z-window-overlapped-tm, 
.z-window-modal-tm-noborder, .z-window-highlighted-tm-noborder, .z-window-overlapped-tm-noborder, .z-window-popup-tm-noborder {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-m-ol.png')}) repeat-x 0 0;
}
.z-window-popup-tm {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-m-pop.png')}) repeat-x 0 0;
}
.z-window-embedded-tr, .z-window-embedded-tr-noborder {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-r.png')}) no-repeat right 0;
	padding-right: 6px;
}
.z-window-modal-tr, .z-window-highlighted-tr, .z-window-overlapped-tr, .z-window-popup-tr,
.z-window-modal-tr-noborder, .z-window-highlighted-tr-noborder, .z-window-overlapped-tr-noborder, .z-window-popup-tr-noborder {
	padding-right: 6px;
}
.z-window-modal-tr, .z-window-highlighted-tr, .z-window-overlapped-tr,
.z-window-modal-tr-noborder, .z-window-highlighted-tr-noborder, .z-window-overlapped-tr-noborder, .z-window-popup-tr-noborder {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-r-ol.png')}) no-repeat right 0;
}
.z-window-popup-tr {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-r-pop.png')}) no-repeat right 0;
}
.z-window-modal-tm-noheader, .z-window-highlighted-tm-noheader,
	.z-window-overlapped-tm-noheader, .z-window-popup-tm-noheader {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-m-ol.png')}) repeat-x 0 0;
	overflow: hidden; zoom: 1; font-size: 0pt; height: 5px; line-height: 0pt;
}
.z-window-embedded-tl, .z-window-embedded-tl-noborder {
	border-bottom: 1px solid #538BA2;
}
.z-window-popup-tl {
	border-bottom: 1px solid #0B5CA0;
}
.z-window-modal-header, .z-window-popup-header, .z-window-highlighted-header,
	.z-window-overlapped-header, .z-window-embedded-header {
	overflow: hidden; zoom: 1; color: #222222; padding: 5px 0 4px 0;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; font-weight: normal;
}
.z-window-modal-header, .z-window-popup-header, .z-window-highlighted-header,
	.z-window-overlapped-header {
	color: #FFFFFF;
}
.z-window-embedded-header a, .z-window-embedded-header a:visited, .z-window-embedded-header a:hover {
	color: #222222;
}
<%-- Caption and Toolbarbutton --%>
.z-window-modal-header a, .z-window-modal-header a:visited, .z-window-modal-header a:hover,
.z-window-modal-header .z-caption a, .z-window-modal-header .z-caption a:visited, .z-window-modal-header .z-caption a:hover,
.z-window-popup-header a, .z-window-popup-header a:visited, .z-window-popup-header a:hover,
.z-window-popup-header .z-caption a, .z-window-popup-header .z-caption a:visited, .z-window-popup-header .z-caption a:hover,
.z-window-highlighted-header a, .z-window-highlighted-header a:visited, .z-window-highlighted-header a:hover,
.z-window-highlighted-header .z-caption a, .z-window-highlighted-header .z-caption a:visited, .z-window-highlighted-header .z-caption a:hover,
.z-window-overlapped-header a, .z-window-overlapped-header a:visited, .z-window-overlapped-header a:hover,
.z-window-overlapped-header .z-caption a, .z-window-overlapped-header .z-caption a:visited, .z-window-overlapped-header .z-caption a:hover {
	color: #FFFFFF;
}
<%-- Body --%>
.z-window-embedded-cnt {
	margin: 0; padding: 3px; border: 1px solid #538BA2;
}
.z-window-embedded-cnt, .z-window-embedded-body, .z-window-overlapped-body,
	.z-window-popup-body, .z-window-highlighted-body, .z-window-modal-body {
	overflow: hidden; zoom: 1;
}
.z-window-overlapped-cnt, .z-window-popup-cnt {
	margin: 0; padding: 4px; overflow: hidden; zoom: 1; background: white;
}
.z-window-popup-cnt {
	border:1px solid #2c70a9; padding: 2px; margin:0;
}
.z-window-modal-cnt, .z-window-highlighted-cnt, .z-window-modal-cnt-noborder,
	.z-window-highlighted-cnt-noborder, .z-window-overlapped-cnt-noborder {
	margin: 0; padding: 2px; background: white; overflow: hidden; zoom: 1;
}
.z-window-modal-cnt-noborder, .z-window-highlighted-cnt-noborder, .z-window-embedded-cnt-noborder,
	.z-window-overlapped-cnt-noborder, .z-window-popup-cnt-noborder {
	border: 0; overflow: hidden; zoom: 1;
}
.z-window-popup-cnt-noborder {
	margin: 0; padding: 1px; background: white;
}
.z-window-modal-cl, .z-window-highlighted-cl, .z-window-overlapped-cl, .z-window-popup-cl {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-lr-ol.png')}) repeat-y 0 0; 
	padding-left: 6px; zoom: 1;
}
.z-window-popup-cm, .z-window-modal-cm, .z-window-highlighted-cm, .z-window-overlapped-cm {
	border:1px solid #0B5CA0; padding: 0; margin:0 ; background: #5EABDB;
}
.z-window-popup-cm-noborder, .z-window-modal-cm-noborder, .z-window-highlighted-cm-noborder,
	.z-window-overlapped-cm-noborder {
	border: 0; padding: 0; margin:0 ; background: #5EABDB;
}
.z-window-modal-cr, .z-window-highlighted-cr, .z-window-overlapped-cr, .z-window-popup-cr {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-lr-ol.png')}) repeat-y right 0;
	padding-right: 6px; zoom: 1;
}
<%-- Footer --%>
.z-window-modal-bl, .z-window-highlighted-bl, .z-window-overlapped-bl, .z-window-popup-bl {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-l-ol.png')}) no-repeat 0 bottom; 
	padding-left: 6px; zoom: 1;
}
.z-window-modal-bm, .z-window-highlighted-bm, .z-window-overlapped-bm, .z-window-popup-bm {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-m-ol.png')}) repeat-x 0 bottom;
	zoom: 1; font-size: 0pt; height: 5px; line-height: 0pt;
}
.z-window-modal-br, .z-window-highlighted-br, .z-window-overlapped-br, .z-window-popup-br {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-r-ol.png')}) no-repeat right bottom;
	padding-right: 6px; zoom: 1;
}
<%-- Tools --%>
.z-window-embedded-tool {
	overflow: hidden; width: 15px; height: 15px; float: right; cursor: pointer;
	background-color : transparent;
	background-image : url(${c:encodeURL('~./zul/img/panel/tool-btn.gif')});
	background-position : 0 0;
	background-repeat : no-repeat;
	margin-left: 2px;
}
.z-window-modal-tool, .z-window-overlapped-tool,
	.z-window-popup-tool, .z-window-highlighted-tool {
	overflow: hidden; width: 15px; height: 15px; float: right; cursor: pointer;
	background-color : transparent;
	background-image : url(${c:encodeURL('~./zul/img/panel/tool-btn-ol.gif')});
	background-position : 0 0;
	background-repeat : no-repeat;
	margin-left: 2px;
}
.z-window-embedded-close, .z-window-modal-close, .z-window-overlapped-close,
	.z-window-popup-close, .z-window-highlighted-close {
	background-position: 0 0;
}
.z-window-embedded-close-over, .z-window-modal-close-over, .z-window-overlapped-close-over,
	.z-window-popup-close-over, .z-window-highlighted-close-over {
	background-position: -15px 0;
}
.z-window-embedded-minimize, .z-window-modal-minimize, .z-window-overlapped-minimize,
	.z-window-popup-minimize, .z-window-highlighted-minimize {
	background-position: 0 -15px;
}
.z-window-embedded-minimize-over, .z-window-modal-minimize-over, .z-window-overlapped-minimize-over,
	.z-window-popup-minimize-over, .z-window-highlighted-minimize-over {
	background-position: -15px -15px;
}
.z-window-embedded-maximize, .z-window-modal-maximize, .z-window-overlapped-maximize,
	.z-window-popup-maximize, .z-window-highlighted-maximize {
	background-position: 0 -30px;
}
.z-window-embedded-maximize-over, .z-window-modal-maximize-over, .z-window-overlapped-maximize-over,
	.z-window-popup-maximize-over, .z-window-highlighted-maximize-over {
	background-position: -15px -30px;
}
.z-window-embedded-maximized, .z-window-modal-maximized, .z-window-overlapped-maximized,
	.z-window-popup-maximized, .z-window-highlighted-maximized {
	background-position: 0 -45px;
}
.z-window-embedded-maximized-over, .z-window-modal-maximized-over, .z-window-overlapped-maximized-over,
	.z-window-popup-maximized-over, .z-window-highlighted-maximized-over {
	background-position: -15px -45px;
}
