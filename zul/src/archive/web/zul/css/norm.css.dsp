<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontSizeM')}"/>
<c:set var="fontSizeM" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontSizeMS')}"/>
<c:set var="fontSizeMS" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontSizeS')}"/>
<c:set var="fontSizeS" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontSizeXS')}"/>
<c:set var="fontSizeXS" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontFamilyT')}"/>
<c:set var="fontFamilyT" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontFamilyC')}"/>
<c:set var="fontFamilyC" value="${val}" scope="request" unless="${empty val}"/>

<c:set var="fontSizeM" value="12px" scope="request" if="${empty fontSizeM}"/>
<c:set var="fontSizeMS" value="11px" scope="request" if="${empty fontSizeMS}"/>
<c:set var="fontSizeS" value="11px" scope="request" if="${empty fontSizeS}"/>
<c:set var="fontSizeXS" value="10px" scope="request" if="${empty fontSizeXS}"/>

<c:set var="fontFamilyT" value="Verdana, Tahoma, Arial, Helvetica, sans-serif"
	scope="request" if="${empty fontFamilyT}"/><%-- title --%>
<c:set var="fontFamilyC" value="Verdana, Tahoma, Arial, Helvetica, sans-serif"
	scope="request" if="${empty fontFamilyC}"/><%-- content --%>

html, body {height:100%}

<c:if test="${empty c:property('org.zkoss.zul.theme.browserDefault')}">
body {
	margin: 0px; padding: 0px 5px;
}
</c:if>

<%-- paragraphs --%>
img {border: 0;}

<%-- DSP --%>
a.gamma {color: #000; text-decoration: none;}
a.gamma:hover {color: #000; text-decoration: underline;}
tr.gamma {background: #F4F4F4;}
td.gamma {background: #F4F4F4;}

<%-- ZK JavaScript debug box --%>
div.z-error {
	position:absolute; z-index:99000;
	width:550px; border:1px solid #963; background-color:#fcc090
}
div.z-error a {
	color: #555; text-decoration: none; font-size: ${fontSizeS};
	background-color: #ffd8a8; padding: 1px 3px;
	border: 1px solid #766;
	border-left: 1px solid #a89a9a; border-top: 1px solid #a89a9a;
}
.z-error-msg {
	border: 1px inset; background-color: #fc9;
}
div.z-log {
	text-align:right; width:50%; right:10px; bottom:5px;
	position:absolute; z-index: 99000;
}
div.z-log textarea {
	width: 100%
}
div.z-log button {
	font-size: ${fontSizeXS};
}
.z-debug-domtree {
	width:80%; right:10px; bottom:5px;
	position:absolute; z-index: 99000; 
	overflow: auto; color: #7D9196;
	height: 300px; background: white;
	padding: 2px; border: 1px solid gray;
}
.z-debug-domtree .z-debug-domtree-header {
	overflow: hidden; zoom: 1; color: #403E39; font: normal ${fontSizeM} ${fontFamilyT};
	padding: 5px 3px 4px 5px; border: 1px solid #999884; line-height: 15px; 
	background:transparent url(${c:encodeURL('~./zk/img/debug/hd-gray.png')}) repeat-x 0 -1px;
	font-weight:bold;
}
.z-debug-domtree .z-debug-domtree-body {
	border: 1px solid #999884;
	border-top: 0px;
}
.z-debug-domtree-close {
	overflow: hidden; width: 15px; height: 15px; float: right; cursor: pointer;
	background-color : transparent;
	background-image : url(${c:encodeURL('~./zk/img/debug/tool-btn.gif')});
	background-position : 0 0;
	background-repeat : no-repeat;
	margin-left: 2px;
}
.z-debug-domtree-close-over {
	background-position: -15px 0;
}
<%-- General --%>
.noscript {<%-- the content of noscript --%>
	position: absolute; z-index: 22000;
	top: 0; left: 0; width: 100%; height: 100%;
	filter: alpha(opacity=60); opacity: .6; hasLayout: -1;
	background: #E0E1E3; text-align: center;
}
.noscript p {
	background: white; color: black; font-weight: bold;
	margin: 10% 15%; padding: 10px 0; border: 1px solid black;
	filter: alpha(opacity=100); opacity: 1;
}

.z-label, .z-radio-cnt, .z-checkbox-cnt, .z-slider-pp, input.button, input.file, 
.z-loading {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; font-weight: normal;
}
.z-modal-mask {
	position: absolute;
	top: 0; left: 0; width: 100%; height: 100%;
	filter: alpha(opacity=60); <%-- IE --%>
	opacity: .6;
	hasLayout: -1;<%-- not a layout element in IE --%>
	background: #E0E1E3; <%-- #dae4f5/#e1eaf7/e3ecf7 --%>
}
.z-loading {
	position: absolute; z-index: 99001; background-color: #6eadff; cursor: wait;
	white-space: nowrap; border: 1px solid #83B5F7; padding:3px;
	left: 0; top: 0;
}
.z-loading-indicator {
	color: #102B6D; border:1px solid #83B5F7; background-color: #FFF; 
	white-space: nowrap; padding:6px;
}
.z-apply-loading-icon, .z-loading-icon {
	background: transparent url(${c:encodeURL('~./zk/img/progress2.gif')}) no-repeat center;
	width: 16px; height: 16px;
	display:-moz-inline-box; vertical-align:top; display:inline-block;
}

.z-apply-mask {
	position: absolute; z-index: 89000; top: 0; left: 0; width: 100%; height: 100%;
	filter: alpha(opacity=60); opacity: .6;
	hasLayout: -1; background: #E0E1E3; <%-- #dae4f5/#e1eaf7/e3ecf7 --%>
}
.z-apply-loading-indicator {
	color: #102B6D; border:1px solid #A6C5DC; background-color: #FFF; 
	white-space: nowrap; padding: 2px;
	font: normal ${fontSizeM} ${fontFamilyT};
	cursor: wait;
}
.z-apply-loading {
	position: absolute; z-index: 89500; background-color: #CEDFEC; border: 1px solid #99C6E9;
	padding: 3px; overflow: hidden; white-space: nowrap; cursor: wait;
}
.z-inline-block { <%-- used with label/checkbox and others to ensure the dimension --%>
	display:-moz-inline-box; vertical-align:top;<%-- vertical-align: make it looks same in diff browsers --%>
	display:inline-block;
}
.z-word-wrap {
	word-wrap: break-word;
}
.z-overflow-hidden {
	overflow: hidden;
}
.z-dd-overlay {
	width: 100%; height: 100%; display: block; position: absolute; left: 0; top: 0;
	background-image: url(${c:encodeURL('~./img/spacer.gif')}); z-index: 10000;
}
.z-hidden-offset {
	visibility: hidden !important; position: absolute !important;
	left: -10000px !important; top: -10000px !important;
}
.z-repaint {
	zoom: 1; background-color: transparent; -moz-outline: none;
}
<%-- Fix float issue for CSS --%>
.z-clear {
	clear: both; height: 0; overflow: hidden; line-height: 0; font-size: 0;
}
<%-- Shadow --%>
.z-shadow {
	display: none; position: absolute; overflow: hidden; left: 0; top: 0;
}
.z-shadow * {
	overflow: hidden;
	padding: 0; border: 0; margin:0 ; clear: none; zoom: 1;
}
.z-shadow .z-shadow-tm,.z-shadow .z-shadow-bm {
	height: 6px; float: left;
}
.z-shadow .z-shadow-tl, .z-shadow .z-shadow-tr, .z-shadow .z-shadow-bl, .z-shadow .z-shadow-br {
	width: 6px; height: 6px; float: left;
}
.z-shadow .z-shadow-c {
	width:100%;
}
.z-shadow .z-shadow-cl, .z-shadow .z-shadow-cr {
	width: 6px; float: left; height: 100%;
}
.z-shadow .z-shadow-cm {
	float: left; height: 100%; background: transparent url(${c:encodeURL('~./zul/img/shadow-m.png')});
}
.z-shadow .z-shadow-t, .z-shadow .z-shadow-b {
	height: 6px; overflow: hidden; width: 100%;
}
.z-shadow .z-shadow-cl {
	background:transparent url(${c:encodeURL('~./zul/img/shadow-lr.png')}) repeat-y 0 0;
}
.z-shadow .z-shadow-cr {
	background:transparent url(${c:encodeURL('~./zul/img/shadow-lr.png')}) repeat-y -6px 0;
}
.z-shadow .z-shadow-tl {
	background:transparent url(${c:encodeURL('~./zul/img/shadow.png')}) no-repeat 0 0;
}
.z-shadow .z-shadow-tm {
	background:transparent url(${c:encodeURL('~./zul/img/shadow.png')}) repeat-x 0 -30px;
}
.z-shadow .z-shadow-tr {
	background:transparent url(${c:encodeURL('~./zul/img/shadow.png')}) repeat-x 0 -18px;
}
.z-shadow .z-shadow-bl {
	background:transparent url(${c:encodeURL('~./zul/img/shadow.png')}) no-repeat 0 -12px;
}
.z-shadow .z-shadow-bm {
	background:transparent url(${c:encodeURL('~./zul/img/shadow.png')}) repeat-x 0 -36px;
}
.z-shadow .z-shadow-br {
	background:transparent url(${c:encodeURL('~./zul/img/shadow.png')}) repeat-x 0 -6px;
}

<%-- Drag-Drop --%>
span.z-drop-allow, span.z-drop-disallow {
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
	width: 16px; min-height: 16px; height: 16px;
}
span.z-drop-allow {
	background-image: url(${c:encodeURL('~./zul/img/grid/drop-yes.gif')});
}
span.z-drop-disallow {
	background-image: url(${c:encodeURL('~./zul/img/grid/drop-no.gif')});
}
div.z-drop-ghost {
	border:1px solid #6593CF;
}
div.z-drop-cnt {
	background-image: url(${c:encodeURL('~./zul/img/grid/drop-bg.gif')});
	width:120px;height:18px;
	padding:2px;
	font-size: ${fontSizeM};
	font-weight: normal; font-family: ${fontFamilyC};
}
<%-- ZK Fileupload --%>
.z-fileupload-add {
	background-image: url(${c:encodeURL('~./zul/img/fileupload/add.png')});
	width: 16px;
	height: 16px;	
}
.z-fileupload-delete {
	background-image: url(${c:encodeURL('~./zul/img/fileupload/delete.png')});
	width: 16px;
	height: 16px;	
}
<%-- ZK Massagebox --%>
.z-msgbox{
	display:-moz-inline-box; display:inline-block;
	background-repeat: no-repeat;
	vertical-align: top; cursor: pointer; border: 0;
	width: 32px; height: 32px;
}
.z-msgbox-question {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/question2.png')});
}
.z-msgbox-exclamation {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/warning2.png')});
}
.z-msgbox-imformation {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/information2.png')});
}
.z-msgbox-error {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/error2.png')});
}

<%-- ZK error message box --%>
.z-errbox-center {
	padding: 2px 3px;
}
.z-errbox-left {
	background-repeat: no-repeat;
	cursor: pointer; border: 0;
	padding-left: 17px;
}
.z-errbox-right {
	background-repeat: no-repeat;
	cursor: pointer; border: 0;
	padding-right: 17px;
	background-position: right 0px;
}
.z-arrow-d {
	background-image: url(${c:encodeURL('~./zul/img/arrowD.png')});
}
.z-arrow-l {
	background-image: url(${c:encodeURL('~./zul/img/arrowL.png')});
}
.z-arrow-ld {
	background-image: url(${c:encodeURL('~./zul/img/arrowLD.png')});
}
.z-arrow-lu {
	background-image: url(${c:encodeURL('~./zul/img/arrowLU.png')});
}
.z-arrow-rd {
	background-image: url(${c:encodeURL('~./zul/img/arrowRD.png')});
}
.z-arrow-ru {
	background-image: url(${c:encodeURL('~./zul/img/arrowRU.png')});
}
.z-arrow-u {
	background-image: url(${c:encodeURL('~./zul/img/arrowU.png')});
}
.z-errbox-close {
	background-image: url(${c:encodeURL('~./zul/img/errbox/close.gif')});
}
.z-errbox-close-over {
	background-image: url(${c:encodeURL('~./zul/img/errbox/close-over.gif')});
}
.z-errbox.z-popup .z-popup-tm {
	background-image : url(${c:encodeURL('~./zul/img/errbox/pp-tb.png')});
}
.z-errbox.z-popup .z-popup-tl {
	background-image : url(${c:encodeURL('~./zul/img/errbox/pp-corners.png')});
}
.z-errbox.z-popup .z-popup-tr {
	background-image : url(${c:encodeURL('~./zul/img/errbox/pp-corners.png')});
}
.z-errbox.z-popup .z-popup-cm {
    background-color : #FFCD9B;
	background-image : url(${c:encodeURL('~./zul/img/errbox/pp-tb.png')});
}
.z-errbox.z-popup .z-popup-cl {
	background-image : url(${c:encodeURL('~./zul/img/errbox/pp-l.png')});
}
.z-errbox.z-popup .z-popup-cr {
	background-image : url(${c:encodeURL('~./zul/img/errbox/pp-r.png')});
}
.z-errbox.z-popup .z-popup-bm {
	background-image : url(${c:encodeURL('~./zul/img/errbox/pp-tb.png')});
}
.z-errbox.z-popup .z-popup-bl {
	background-image : url(${c:encodeURL('~./zul/img/errbox/pp-corners.png')});
}
.z-errbox.z-popup .z-popup-br {
	background-image : url(${c:encodeURL('~./zul/img/errbox/pp-corners.png')});
}
<%-- Progressmeter --%>
div.z-progressmeter {
	background:#E0E8F3 url(${c:encodeURL('~./zk/img/prgmeter_bg.gif')}) repeat-x scroll 0 0 ;
	border:1px solid #7FA9E4;
	text-align: left;
	height: 17px;
}
span.z-progressmeter-img {
	display:-moz-inline-box; display:inline-block;
	background:#9CBFEE url(${c:encodeURL('~./zk/img/prgmeter.gif')}) repeat-x scroll left center;
	height: 17px; font-size:0;
}
.z-messagebox {
	word-break: break-all; overflow:auto;
}
.z-messagebox-btn {
	min-width: 45pt; width: 100%;
}
<%-- Auxheader --%>
.z-auxheader-cnt {
	font-size: ${fontSizeM}; font-weight: normal;
	font-family: ${fontFamilyT};
	border: 0; margin: 0; padding: 0; overflow: hidden;
}
.z-word-wrap .z-auxheader-cnt {
	word-wrap: break-word;
}
<%-- Window --%>
<c:include page="~./zul/css/cmps/window.css.dsp"/>
<%-- Caption --%>
<c:include page="~./zul/css/cmps/caption.css.dsp"/>
<%-- Groupbox --%>
<c:include page="~./zul/css/cmps/groupbox.css.dsp"/>
<%-- Separator --%>
<c:include page="~./zul/css/cmps/separator.css.dsp"/>
<%-- Toolbar and Toolbarbutton --%>
<c:include page="~./zul/css/cmps/toolbar.css.dsp"/>
<%-- Slider --%>
<c:include page="~./zul/css/cmps/slider.css.dsp"/>
<%-- Button --%>
<c:include page="~./zul/css/cmps/button.css.dsp"/>
<%-- Paging --%>
<c:include page="~./zul/css/cmps/paging.css.dsp"/>
<%-- Panel --%>
<c:include page="~./zul/css/cmps/panel.css.dsp"/>
<%-- Combobox, Bandbox, Timebox, Datebox, and Spinner --%>
<c:include page="~./zul/css/cmps/combo.css.dsp"/>
<%-- Calendar and Datebox --%>
<c:include page="~./zul/css/cmps/calendar.css.dsp"/>
<%-- Widget (Textbox, Intbox, Longbox, and so on) --%>
<c:include page="~./zul/css/cmps/widget.css.dsp"/>
<%-- Box, Splitter, Vbox, and Hbox --%>
<c:include page="~./zul/css/cmps/box.css.dsp"/>
<%-- Popup --%>
<c:include page="~./zul/css/cmps/popup.css.dsp"/>
<%-- Menu --%>
<c:include page="~./zul/css/cmps/menu.css.dsp"/>
<%-- Grid --%>
<c:include page="~./zul/css/cmps/grid.css.dsp"/>
<%-- Listbox --%>
<c:include page="~./zul/css/cmps/listbox.css.dsp"/>
<%-- Tree --%>
<c:include page="~./zul/css/cmps/tree.css.dsp"/>
<%-- Tabbox --%>
<c:include page="~./zul/css/cmps/tabbox.css.dsp"/>
