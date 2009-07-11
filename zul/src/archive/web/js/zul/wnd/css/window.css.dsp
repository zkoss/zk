<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-window-modal-resize-faker,
.z-window-overlapped-resize-faker,
.z-window-popup-resize-faker,
.z-window-highlighted-resize-faker,
.z-window-resize-faker {
	position: absolute;
	border: 1px dashed #1854C2;
	overflow: hidden;
	z-index: 60000;
	left: 0;
	top: 0;
	background-color: #D7E6F7;
	filter: alpha(opacity=50); <%-- IE --%>
	opacity: .5;
}
.z-window-move-ghost {
	position: absolute;
	background: #D7E6F7;
	overflow: hidden;
	filter: alpha(opacity=65) !important; <%-- IE --%>
	opacity: .65 !important;
	cursor: move !important;
}
.z-window-move-ghost dl {
	border: 1px solid #538BA2;
	margin: 0; padding: 0;
	overflow: hidden;
	display: block;
	background: #D7E6F7;
	line-height: 0;
	font-size: 0;
}
.z-window-embedded, .z-window-modal, .z-window-overlapped, .z-window-popup, .z-window-highlighted {
	margin: 0; padding: 0; overflow: hidden; zoom: 1;
}
<%-- Top Left Corner --%>
.z-window-embedded-tl,
.z-window-modal-tl,
.z-window-highlighted-tl,
.z-window-overlapped-tl,
.z-window-popup-tl {
	background: transparent no-repeat 0 top;
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-corner.png')});
	margin-right: 5px;
	height: 5px;
	font-size: 0;
	line-height: 0;
	zoom: 1;
}
.z-window-embedded-tl {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-corner.png')});
}
.z-window-popup-tl {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-pop-corner.png')});
}
<%-- Top Right Corner --%>
.z-window-embedded-tr,
.z-window-modal-tr,
.z-window-highlighted-tr,
.z-window-overlapped-tr,
.z-window-popup-tr {
	background: transparent no-repeat right -10px;
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-corner.png')});
	position: relative;
	height: 5px;
	margin-right: -5px;
	font-size: 0;
	line-height:0;
	zoom: 1;
}
.z-window-embedded-tr {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-corner.png')});
}
.z-window-popup-tr {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-pop-corner.png')});
}
<%-- Header Left --%>
.z-window-embedded-hl,
.z-window-modal-hl,
.z-window-highlighted-hl,
.z-window-overlapped-hl,
.z-window-popup-hl {
	background: transparent no-repeat 0 0;
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-hl.png')});
	padding-left: 6px;
	zoom: 1;
}
.z-window-embedded-hl{
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-hl.png')});
}
.z-window-popup-hl {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-pop-hl.png')});
}
<%-- Header Right --%>
.z-window-embedded-hr,
.z-window-modal-hr,
.z-window-highlighted-hr,
.z-window-overlapped-hr,
 .z-window-popup-hr {
	background: transparent no-repeat right 0;
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-hr.png')});
	padding-right: 6px;
	zoom: 1;
}
.z-window-embedded-hr, .z-window-embedded-hr-noborder {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-hr.png')});
}
.z-window-popup-hr {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-pop-hr.png')});
}
<%-- Header Middle --%>
.z-window-embedded-hm,
.z-window-modal-hm,
.z-window-highlighted-hm,
.z-window-overlapped-hm,
.z-window-popup-hm {
	background: transparent repeat-x 0 0;
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-hm.png')});
	overflow: hidden;
	zoom: 1;
}
.z-window-embedded-hm {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-hm.png')});
}
.z-window-popup-hm {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-pop-hm.png')});
}
<%-- Header --%>
.z-window-modal-header, .z-window-popup-header, .z-window-highlighted-header,
	.z-window-overlapped-header, .z-window-embedded-header {
	overflow: hidden; zoom: 1; color: #222222; padding-bottom: 4px;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; font-weight: normal;
	cursor: default;
}
.z-window-modal-header, .z-window-popup-header, .z-window-highlighted-header,
	.z-window-overlapped-header {
	color: #FFFFFF;
}
.z-window-embedded-header a, .z-window-embedded-header a:visited, .z-window-embedded-header a:hover {
	color: #222222;
}
<%-- Caption and Toolbarbutton --%>
.z-window-modal-header a,
.z-window-modal-header a:visited,
.z-window-modal-header a:hover,
.z-window-modal-header .z-caption a,
.z-window-modal-header .z-caption a:visited,
.z-window-modal-header .z-caption a:hover,
.z-window-popup-header a,
.z-window-popup-header a:visited,
.z-window-popup-header a:hover,
.z-window-popup-header .z-caption a,
.z-window-popup-header .z-caption a:visited,
.z-window-popup-header .z-caption a:hover,
.z-window-highlighted-header a,
.z-window-highlighted-header a:visited,
.z-window-highlighted-header a:hover,
.z-window-highlighted-header .z-caption a,
.z-window-highlighted-header .z-caption a:visited,
.z-window-highlighted-header .z-caption a:hover,
.z-window-overlapped-header a,
.z-window-overlapped-header a:visited,
.z-window-overlapped-header a:hover,
.z-window-overlapped-header .z-caption a,
.z-window-overlapped-header .z-caption a:visited,
.z-window-overlapped-header .z-caption a:hover {
	color: #FFFFFF;
}
<%-- Body Content--%>
.z-window-embedded-cnt {
	margin: 0;
	padding: 3px;
	border: 1px solid #538BA2;
}
.z-window-embedded-cnt,
.z-window-embedded-body,
.z-window-overlapped-body,
.z-window-popup-body,
.z-window-highlighted-body,
.z-window-modal-body {
	overflow: hidden;
	zoom: 1;
}
.z-window-overlapped-cnt, .z-window-popup-cnt {
	margin: 0;
	padding: 4px;
	background: white;
	overflow: hidden;
	zoom: 1;
}
.z-window-popup-cnt {
	margin:0;
	padding: 2px;
	border: 1px solid #2c70a9;
}
.z-window-modal-cnt,
.z-window-highlighted-cnt,
.z-window-modal-cnt-noborder,
.z-window-highlighted-cnt-noborder,
.z-window-overlapped-cnt-noborder {
	margin: 0;
	padding: 2px;
	background: white;
	overflow: hidden;
	zoom: 1;
}
.z-window-modal-cnt-noborder,
.z-window-highlighted-cnt-noborder,
.z-window-embedded-cnt-noborder,
.z-window-overlapped-cnt-noborder,
.z-window-popup-cnt-noborder {
	border: 0;
	overflow: hidden;
	zoom: 1;
}
.z-window-popup-cnt-noborder {
	margin: 0;
	padding: 1px;
	background: white;
}
<%-- Center Left --%>
.z-window-modal-cl,
.z-window-highlighted-cl,
.z-window-overlapped-cl {
	background: transparent repeat-y 0 0;
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-clr.png')});
	padding-left: 6px;
	zoom: 1;
}
<%-- Center Right --%>
.z-window-modal-cr,
.z-window-highlighted-cr,
.z-window-overlapped-cr {
	background: transparent repeat-y right 0;
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-clr.png')});
	padding-right: 6px;
	zoom: 1;
}
<%-- Center Middle --%>
.z-window-modal-cm,
.z-window-highlighted-cm,
.z-window-overlapped-cm {
	padding: 0;
	margin: 0;
	border: 1px solid #0B5CA0;
	background: #5EABDB;
}
<%-- Bottom Left --%>
.z-window-modal-bl,
.z-window-highlighted-bl,
.z-window-overlapped-bl {
	background: transparent no-repeat 0 -5px;
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-corner.png')});
	height: 5px;
	margin-right: 5px;
	zoom: 1;
}
<%-- Bottom Right --%>
.z-window-modal-br,
.z-window-highlighted-br,
.z-window-overlapped-br {
	background: transparent no-repeat right bottom;
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-corner.png')});
	position: relative;
	height: 5px;
	margin-right: -5px;
	font-size: 0;
	line-height:0;
	zoom: 1;
}
<%-- Tools --%>
.z-window-embedded-icon,
.z-window-popup-icon,
.z-window-modal-icon,
.z-window-overlapped-icon,
.z-window-highlighted-icon {
	background: transparent no-repeat 0 0;
	height: 16px;
	width: 16px;
	overflow: hidden;
	float: right;
	cursor: pointer;
	margin-left: 2px;
}
.z-window-embedded-icon {
	background-image : url(${c:encodeURL('~./zul/img/wnd/ebd-btn.gif')});
}
.z-window-modal-icon,
.z-window-highlighted-icon,
.z-window-overlapped-icon {
	background-image : url(${c:encodeURL('~./zul/img/wnd/ol-btn.gif')});
}
.z-window-popup-icon {
	background-image : url(${c:encodeURL('~./zul/img/wnd/pop-btn.gif')});
}
.z-window-embedded-min, .z-window-modal-min, .z-window-overlapped-min,
	.z-window-popup-min, .z-window-highlighted-min {
	background-position: 0 0;
}
.z-window-embedded-min-over, .z-window-modal-min-over, .z-window-overlapped-min-over,
	.z-window-popup-min-over, .z-window-highlighted-min-over {
	background-position: -16px 0;
}
.z-window-embedded-max, .z-window-modal-max, .z-window-overlapped-max,
	.z-window-popup-max, .z-window-highlighted-max {
	background-position: 0 -16px;
}
.z-window-embedded-max-over, .z-window-modal-max-over, .z-window-overlapped-max-over,
	.z-window-popup-max-over, .z-window-highlighted-max-over {
	background-position: -16px -16px;
}
.z-window-embedded-maxd, .z-window-modal-maxd, .z-window-overlapped-maxd,
	.z-window-popup-maxd, .z-window-highlighted-maxd {
	background-position: 0 -32px;
}
.z-window-embedded-maxd-over, .z-window-modal-maxd-over, .z-window-overlapped-maxd-over,
	.z-window-popup-maxd-over, .z-window-highlighted-maxd-over {
	background-position: -16px -32px;
}
.z-window-embedded-close, .z-window-modal-close, .z-window-overlapped-close,
	.z-window-popup-close, .z-window-highlighted-close {
	background-position: 0 -48px;
}
.z-window-embedded-close-over, .z-window-modal-close-over, .z-window-overlapped-close-over,
	.z-window-popup-close-over, .z-window-highlighted-close-over {
	background-position: -16px -48px;
}

.z-messagebox {
	word-break: break-all;
	overflow:auto;
}
.z-messagebox-btn {
	min-width: 45pt;
	width: 100%;
}

.z-msgbox {
	display: -moz-inline-box;
	display: inline-block;
	background-repeat: no-repeat;
	vertical-align: top;
	cursor: pointer;
	border: 0;
	width: 32px;
	height: 32px;
}
.z-msgbox-question {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/question-btn.png')});
}
.z-msgbox-exclamation {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/warning-btn.png')});
}
.z-msgbox-information {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/info-btn.png')});
}
.z-msgbox-error {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/stop-btn.png')});
}

<%-- IE --%>
<c:if test="${c:isExplorer()}">
.z-messagebox-btn {
	width: 47pt;
	text-overflow: ellipsis;
}
</c:if>
<c:if test="${c:isGecko()}">
.z-messagebox-btn {
	width: 45pt;
}
</c:if>
<c:if test="${c:isOpera()}">
.z-messagebox-btn {
	width: 47pt;
}
</c:if>

<%-- IE 6 GIF  --%>
<c:if test="${c:browser('ie6-')}">
.z-window-modal-tl,
.z-window-modal-tr,
.z-window-modal-bl,
.z-window-modal-br,
.z-window-overlapped-tl,
.z-window-overlapped-tr,
.z-window-overlapped-bl,
.z-window-overlapped-br,
.z-window-highlighted-tl,
.z-window-highlighted-tr,
.z-window-highlighted-bl,
.z-window-highlighted-br {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-corner.gif')});
}
.z-window-embedded-tl,
.z-window-embedded-tr {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-corner.gif')});
}
.z-window-popup-tl,
.z-window-popup-tr {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-pop-corner.gif')});
}
.z-window-modal-hr,
.z-window-highlighted-hr,
.z-window-overlapped-hr,
 .z-window-popup-hr {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-hr.gif')});
}
.z-window-embedded-hr,
.z-window-embedded-hr-noborder {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-hr.gif')});
}
.z-window-popup-hr {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-pop-hr.gif')});
}
.z-window-modal-hl,
.z-window-highlighted-hl,
.z-window-overlapped-hl,
.z-window-popup-hl {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-hl.gif')});
}
.z-window-embedded-hl,
.z-window-embedded-hl-noborder {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-hl.gif')});
}
.z-window-popup-hl {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-pop-hl.gif')});
}
.z-window-modal-hm,
.z-window-highlighted-hm,
.z-window-overlapped-hm,
.z-window-popup-hm {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-hm.gif')});
}
.z-window-embedded-hm,
.z-window-embedded-hm-noborder {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-hm.gif')});
}
.z-window-popup-hm {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-pop-hm.gif')});
}
.z-window-modal-cl,
.z-window-highlighted-cl,
.z-window-overlapped-cl,
.z-window-modal-cr,
.z-window-highlighted-cr,
.z-window-overlapped-cr {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-clr.gif')});
}

.z-msgbox-question {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/question-btn.gif')});
}
.z-msgbox-exclamation {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/warning-btn.gif')});
}
.z-msgbox-information {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/info-btn.gif')});
}
.z-msgbox-error {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/stop-btn.gif')});
}
</c:if>
