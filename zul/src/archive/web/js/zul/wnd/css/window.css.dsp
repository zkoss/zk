<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- original --%>

.z-window-modal-shadow,
.z-window-overlapped-shadow,
.z-window-popup-shadow,
.z-window-embedded-shadow,
.z-window-highlighted-shadow {
	border-radius: 5px;
	box-shadow: 0 0 3px rgba(0, 0, 0, 0.5);
	-moz-border-radius: 5px;
	-moz-box-shadow: 0 0 3px rgba(0, 0, 0, 0.5);
	-webkit-border-radius: 5px;
	-webkit-box-shadow: 0 0 3px rgba(0, 0, 0, 0.5);
}
.z-window-modal-shadow,
.z-window-highlighted-shadow {
	box-shadow: 0 0 4px rgba(0, 0, 0, 0.7);
	-moz-box-shadow: 0 0 4px rgba(0, 0, 0, 0.7);
	-webkit-box-shadow: 0 0 4px rgba(0, 0, 0, 0.7);
}

.z-window-modal-resize-faker,
.z-window-overlapped-resize-faker,
.z-window-popup-resize-faker,
.z-window-embedded-resize-faker,
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
.z-window-modal-move-ghost,
.z-window-overlapped-move-ghost,
.z-window-popup-move-ghost,
.z-window-highlighted-move-ghost,
.z-window-move-ghost {
	position: absolute;
	background: #D7E6F7;
	overflow: hidden;
	filter: alpha(opacity=65) !important; <%-- IE --%>
	opacity: .65 !important;
	cursor: move !important;
}
.z-window-modal-move-ghost dl,
.z-window-overlapped-move-ghost dl,
.z-window-popup-move-ghost dl,
.z-window-highlighted-move-ghost dl,
.z-window-move-ghost dl {
	border: 1px solid #9F9F9F;
	margin: 0; padding: 0;
	overflow: hidden;
	display: block;
	background: #D7E6F7;
	line-height: 0;
	font-size: 0;
}
.z-window-embedded, 
.z-window-modal, 
.z-window-overlapped, 
.z-window-popup, 
.z-window-highlighted {
	margin: 0;
	padding: 0;
	overflow: hidden;
	zoom: 1;
}

.z-window-modal-tl-noborder,
.z-window-highlighted-tl-noborder,
.z-window-overlapped-tl-noborder,
.z-window-popup-tl-noborder,
.z-window-modal-bl-noborder,
.z-window-highlighted-bl-noborder,
.z-window-overlapped-bl-noborder,
.z-window-popup-bl-noborder {
	display: none;
}
<%-- Top Left Corner --%>
.z-window-embedded-tl,
.z-window-modal-tl,
.z-window-highlighted-tl,
.z-window-overlapped-tl,
.z-window-popup-tl {
	background: transparent no-repeat 0 top;
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-corner.png')});
	margin-right: 5px;
	height: 5px;
	font-size: 0;
	line-height: 0;
	zoom: 1;
}
.z-window-embedded-tl {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-corner.png')});
}
.z-window-popup-tl {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-pop-corner.png')});
}

<%-- Top Right Corner --%>
.z-window-embedded-tr,
.z-window-modal-tr,
.z-window-highlighted-tr,
.z-window-overlapped-tr,
.z-window-popup-tr {
	background: transparent no-repeat right -10px;
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-corner.png')});
	position: relative;
	height: 5px;
	margin-right: -5px;
	font-size: 0;
	line-height:0;
	zoom: 1;
}
.z-window-embedded-tr {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-corner.png')});
}
.z-window-popup-tr {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-pop-corner.png')});
}
<%-- Header Left --%>
.z-window-embedded-hl,
.z-window-modal-hl,
.z-window-highlighted-hl,
.z-window-overlapped-hl,
.z-window-popup-hl {
	background: transparent no-repeat 0 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-hl.png')});
	padding-left: 6px;
	zoom: 1;
}
<%-- Header Right --%>
.z-window-embedded-hr,
.z-window-modal-hr,
.z-window-highlighted-hr,
.z-window-overlapped-hr,
 .z-window-popup-hr {
	background: transparent no-repeat right 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-hr.png')});
	padding-right: 6px;
	zoom: 1;
}
.z-window-embedded-hr, .z-window-embedded-hr-noborder {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-hr.png')});
}
<%-- Header Middle --%>
.z-window-embedded-hm,
.z-window-modal-hm,
.z-window-highlighted-hm,
.z-window-overlapped-hm,
.z-window-popup-hm {
	background: transparent repeat-x 0 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-hm.png')});
	overflow: hidden;
	zoom: 1;
}
<%-- Header --%>
.z-window-modal-header, 
.z-window-popup-header, 
.z-window-highlighted-header,
.z-window-overlapped-header, 
.z-window-embedded-header {
	overflow: hidden; zoom: 1; color: #222222; padding-bottom: 4px;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM}; font-weight: normal;
	cursor: default;
}
.z-window-embedded-header a, 
.z-window-embedded-header a:visited, 
.z-window-embedded-header a:hover {
	color: #222222;
}
.z-window-modal-header-move,
.z-window-highlighted-header-move,
.z-window-overlapped-header-move,
.z-window-popup-header-move {
	cursor: move;
} 
<%-- Body Content--%>
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
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-clr.png')});
	padding-left: 6px;
	zoom: 1;
}
<%-- Center Right --%>
.z-window-modal-cr,
.z-window-highlighted-cr,
.z-window-overlapped-cr {
	background: transparent repeat-y right 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-clr.png')});
	padding-right: 6px;
	zoom: 1;
}
<%-- Center Middle --%>
<c:if test="${zk.ie < 8}">
.z-window-embedded-cm {
	zoom: 1; <%-- fixed for B50-3315594.zul --%>
}
</c:if>
.z-window-modal-cm,
.z-window-highlighted-cm,
.z-window-overlapped-cm {
	padding: 0;
	margin: 0;
	border: 1px solid #C5C5C5;
	background: white;
}
<%-- Bottom Left --%>
.z-window-modal-bl,
.z-window-highlighted-bl,
.z-window-overlapped-bl {
	background: transparent no-repeat 0 -5px;
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-corner.png')});
	height: 5px;
	margin-right: 5px;
	zoom: 1;
}
<%-- Bottom Right --%>
.z-window-modal-br,
.z-window-highlighted-br,
.z-window-overlapped-br {
	background: transparent no-repeat right bottom;
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-corner.png')});
	height: 5px;
	margin-right: -5px;
	font-size: 0;
	line-height:0;
	zoom: 1;
}

.z-messagebox-window .z-window-modal-cnt,
.z-messagebox-window .z-window-highlighted-cnt {
	padding: 17px;
	padding-bottom: 15px;
}
.z-messagebox .z-label {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	color: #363636;
}
.z-messagebox-btn {
	min-width: 48pt;
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
	background-image: url(${c:encodeThemeURL('~./zul/img/msgbox/question-btn.png')});
}
.z-msgbox-exclamation {
	background-image: url(${c:encodeThemeURL('~./zul/img/msgbox/warning-btn.png')});
}
.z-msgbox-information {
	background-image: url(${c:encodeThemeURL('~./zul/img/msgbox/info-btn.png')});
}
.z-msgbox-error {
	background-image: url(${c:encodeThemeURL('~./zul/img/msgbox/stop-btn.png')});
}
<%-- end of original --%>

<%-- Top, Bottom --%>
.z-window-overlapped-tl, .z-window-overlapped-tr,
.z-window-popup-tl, .z-window-popup-tr,
.z-window-overlapped-bl, .z-window-overlapped-br {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-corner.png')});
	height: 6px;
}
.z-window-embedded-tl, .z-window-embedded-tr,
.z-window-embedded-bl, .z-window-embedded-br {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-corner.png')});
}

.z-window-modal-tl, .z-window-highlighted-tl, 
.z-window-overlapped-tl, .z-window-popup-tl  {
	margin-right: 6px;
}
.z-window-modal-tr, .z-window-highlighted-tr, 
.z-window-overlapped-tr, .z-window-popup-tr  {
	background-position: right -12px;
	margin-right: -6px;
}
.z-window-modal-bl, 
.z-window-highlighted-bl, 
.z-window-overlapped-bl {
	background-position: 0 -6px;
	margin-right: 6px;
	height: 6px;
}
.z-window-modal-br, 
.z-window-highlighted-br, 
.z-window-overlapped-br {
	margin-right: -6px;
	height: 6px;
	position: relative; <%-- IE 6 --%>
}
<%-- Header --%>
.z-window-overlapped-hl,
.z-window-popup-hl {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-hl.png')});
}
.z-window-embedded-hl{
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-hl.png')});
}
.z-window-overlapped-hr,
.z-window-popup-hr {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-hr.png')});
}
.z-window-embedded-hr{
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-hr.png')});
}
.z-window-overlapped-hm,
.z-window-popup-hm {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-hm.png')});
}
.z-window-embedded-hm{
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-hm.png')});
}

.z-window-overlapped-cl, 
.z-window-overlapped-cr {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-clr.png')});
}
.z-window-embedded-cl, 
.z-window-embedded-cr {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-clr.png')});
}

.z-window-embedded-hm,
.z-window-overlapped-hm,
.z-window-popup-hm {
	padding-left: 1px;
}

.z-window-embedded-cm,
.z-window-overlapped-cm {
	border: solid #CCC 1px;
}
.z-window-popup-cnt {
	border: solid #ACACAC 1px;
	padding: 6px;
}
.z-window-embedded-header, 
.z-window-modal-header, 
.z-window-popup-header, 
.z-window-highlighted-header, 
.z-window-overlapped-header {
	color: #363636;
	padding-top: 3px;
	padding-bottom: 6px;
}



<%-- Embedded Mold Frame --%>

.z-window-embedded-cnt {
	margin: 0;
	padding: 4px;
	background: white;
	overflow: hidden;
	zoom: 1;
	border: 0;
}
.z-window-embedded-cl {
	background: transparent repeat-y 0 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-clr.png')});
	padding-left: 6px;
	zoom: 1;
}
.z-window-embedded-cr {
	background: transparent repeat-y right 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-clr.png')});
	padding-right: 6px;
	zoom: 1;
}
.z-window-embedded-cm {
	padding: 0;
	margin: 0;
}
.z-window-embedded-bl {
	background: transparent no-repeat 0 -5px;
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-corner.png')});
	height: 5px;
	margin-right: 5px;
	zoom: 1;
}
.z-window-embedded-br {
	background: transparent no-repeat right bottom;
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-corner.png')});
	position: relative;
	height: 5px;
	margin-right: -5px;
	font-size: 0;
	line-height:0;
	zoom: 1;
}
<c:if test="${zk.ie == 6}">
.z-window-embedded-br {
	bottom: 0;
	right: -5px;
}
</c:if>




<%-- Buttons --%>

.z-window-embedded-icon,
.z-window-popup-icon,
.z-window-modal-icon,
.z-window-overlapped-icon,
.z-window-highlighted-icon {
	background: transparent no-repeat 0 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-icon.png')});
	height: 17px;
	width: 28px;
	overflow: hidden;
	float: right;
	cursor: pointer;
	margin-left: 2px;
}

<%-- Button Background Position --%>

.z-window-embedded-min, 
.z-window-modal-min, 
.z-window-overlapped-min,
.z-window-popup-min, 
.z-window-highlighted-min {
	background-position: 0 0;
}
.z-window-embedded-min-over, 
.z-window-modal-min-over, 
.z-window-overlapped-min-over,
.z-window-popup-min-over, 
.z-window-highlighted-min-over {
	background-position: -28px 0;
}
.z-window-embedded-max, 
.z-window-modal-max, 
.z-window-overlapped-max,
.z-window-popup-max, 
.z-window-highlighted-max {
	background-position: 0 -17px;
}
.z-window-embedded-max-over, 
.z-window-modal-max-over, 
.z-window-overlapped-max-over,
.z-window-popup-max-over, 
.z-window-highlighted-max-over {
	background-position: -28px -17px;
}
.z-window-embedded-maxd, 
.z-window-modal-maxd, 
.z-window-overlapped-maxd,
.z-window-popup-maxd, 
.z-window-highlighted-maxd {
	background-position: 0 -34px;
}
.z-window-embedded-maxd-over, 
.z-window-modal-maxd-over, 
.z-window-overlapped-maxd-over,
.z-window-popup-maxd-over, 
.z-window-highlighted-maxd-over {
	background-position: -28px -34px;
}
.z-window-embedded-close, 
.z-window-modal-close, 
.z-window-overlapped-close,
.z-window-popup-close, 
.z-window-highlighted-close {
	background-position: 0 -51px;
}
.z-window-embedded-close-over, 
.z-window-modal-close-over, 
.z-window-overlapped-close-over,
.z-window-popup-close-over, 
.z-window-highlighted-close-over {
	background-position: -28px -51px;
}

<%-- IE --%>
<c:if test="${zk.ie > 0}">
.z-messagebox-btn {
	width: 50pt;
	text-overflow: ellipsis;
}
.z-messagebox-btn table {
	width: 100%;
}
</c:if>
<c:if test="${zk.gecko > 0}">
.z-messagebox-btn {
	width: 48pt;
}
</c:if>
<c:if test="${zk.opera > 0}">
.z-messagebox-btn {
	width: 50pt;
}
</c:if>

<%-- IE 6 GIF  --%>
<c:if test="${zk.ie == 6}">
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
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-corner.gif')});
}
.z-window-embedded-tl,
.z-window-embedded-tr {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-corner.gif')});
}
.z-window-popup-tl,
.z-window-popup-tr {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-pop-corner.gif')});
}
.z-window-modal-hl,
.z-window-highlighted-hl,
.z-window-overlapped-hl {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-hl.gif')});
}
.z-window-modal-hr,
.z-window-highlighted-hr,
.z-window-overlapped-hr {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-hr.gif')});
}
.z-window-embedded-hl,
.z-window-embedded-hl-noborder {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-hl.gif')});
}
.z-window-embedded-hr,
.z-window-embedded-hr-noborder {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-hr.gif')});
}
.z-window-modal-hm,
.z-window-highlighted-hm,
.z-window-overlapped-hm,
.z-window-popup-hm {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-hm.gif')});
}
.z-window-embedded-hm,
.z-window-embedded-hm-noborder {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-hm.gif')});
}
.z-window-popup-hm {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-pop-hm.gif')});
}
.z-window-modal-cl,       .z-window-modal-cr,
.z-window-highlighted-cl, .z-window-highlighted-cr, 
.z-window-overlapped-cl,  .z-window-overlapped-cr {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-clr.gif')});
}
.z-window-overlapped-tl, .z-window-overlapped-tr,
.z-window-popup-tl,      .z-window-popup-tr,
.z-window-overlapped-bl, .z-window-overlapped-br {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-corner.gif')});
}
.z-window-embedded-tl, .z-window-embedded-tr,
.z-window-embedded-bl, .z-window-embedded-br {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-corner.gif')});
}
.z-window-overlapped-hl,
.z-window-popup-hl {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-hl.gif')});
}
.z-window-embedded-hl{
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-hl.gif')});
}
.z-window-overlapped-hr,
.z-window-popup-hr {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-hr.gif')});
}
.z-window-embedded-hr{
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-hr.gif')});
}
.z-window-overlapped-hm,
.z-window-popup-hm {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-hm.gif')});
}
.z-window-embedded-hm{
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-hm.gif')});
}

.z-window-overlapped-cl, 
.z-window-overlapped-cr {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-ol-clr.gif')});
}
.z-window-embedded-cl, 
.z-window-embedded-cr {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-clr.gif')});
}
.z-window-embedded-bl,
.z-window-embedded-br {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-corner.gif')});
}
.z-window-embedded-icon,
.z-window-modal-icon,
.z-window-highlighted-icon,
.z-window-popup-icon,
.z-window-overlapped-icon {
	background-image: url(${c:encodeThemeURL('~./zul/img/wnd/wnd-icon.gif')});
}
.z-msgbox-question {
	background-image: url(${c:encodeThemeURL('~./zul/img/msgbox/question-btn.gif')});
}
.z-msgbox-exclamation {
	background-image: url(${c:encodeThemeURL('~./zul/img/msgbox/warning-btn.gif')});
}
.z-msgbox-information {
	background-image: url(${c:encodeThemeURL('~./zul/img/msgbox/info-btn.gif')});
}
.z-msgbox-error {
	background-image: url(${c:encodeThemeURL('~./zul/img/msgbox/stop-btn.gif')});
}
</c:if>
