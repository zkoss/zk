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
<c:choose>
<c:when test="${!empty c:property('org.zkoss.zul.theme.enableZKPrefix')}">
.zk p, .zk div, .zk span, .zk label, .zk a, .zk input, .zk textarea,
.zk button, .zk input.button, .zk input.file {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; font-weight: normal;
}
.zk legend {
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM}; font-weight: normal;
}
.zk th {
	font-family: ${fontFamilyT};
	font-weight: bold; 
}
.zk thead tr {
	font-family: ${fontFamilyT};
	font-weight: bold;
}
.zk img {border: 0;}
</c:when>
<c:otherwise>
img {border: 0;}
</c:otherwise>
</c:choose>

<%-- DSP --%>
a.gamma {color: #000000; text-decoration: none;}
a.gamma:hover {color: #000000; text-decoration: underline;}
tr.gamma {background: #F4F4F4;}
td.gamma {background: #F4F4F4;}

<%-- ZK JavaScript debug box --%>
div.z-debugbox {
	border: 1px solid #77c;	position: absolute;
	width: 60%; z-index: 99000; background: white;
}

<%-- General --%>
.noscript {<%-- the content of noscript --%>
	position: absolute; z-index: 32000;
	top: 0; left: 0; width: 100%; height: 100%;
	filter: alpha(opacity=60); opacity: .6; zoom: 1;
	background: #E0E1E3; text-align: center;
}
.noscript p {
	background: white; color: black; font-weight: bold;
	margin: 10% 15%; padding: 10px 0; border: 1px solid black;
	filter: alpha(opacity=100); opacity: 1;
}

.z-label, .z-radio-cnt, .z-checkbox-cnt, .z-slider-pp, input.button, input.file, 
.z-loading,.z-errbox {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; font-weight: normal;
}
.z-modal-mask {
	position: absolute; z-index: 30000;
	top: 0; left: 0; width: 100%; height: 100%;
	filter: alpha(opacity=60); <%-- IE --%>
	opacity: .6;
	zoom: 1;
	background: #E0E1E3;
}
.z-loading {
	position: absolute; z-index: 31000; background-color: #6eadff; cursor: wait;
	white-space: nowrap; border: 1px solid #83B5F7; padding:3px;
}
.z-loading-indicator {
	color: #102B6D; border:1px solid #83B5F7; background-color: #FFF; 
	white-space: nowrap; padding:6px;
}
.z-apply-loading-icon, .z-loading-icon {
	background: transparent no-repeat center;
	background-image: url(${c:encodeURL('~./zk/img/progress2.gif')});
	width: 16px; height: 16px;
}

.z-apply-mask {
	position: absolute; z-index: 89000; top: 0; left: 0; width: 100%; height: 100%;
	filter: alpha(opacity=60); opacity: .6;
	zoom: 1; background: #E0E1E3;
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
<%-- Fix float issue for CSS --%>
.z-clear {
	clear: both; height: 0; overflow: hidden; line-height: 0; font-size: 0;
}
<%-- Shadow --%>
.z-shadow {
	display: none; position: absolute; overflow: hidden; left: 0; top: 0;
}

.z-shadow .z-shadow-cl{
	background: transparent  repeat-y 0 0;
	background-image: url(${c:encodeURL('~./zul/img/shadow-cl.png')});
	padding-left: 6px;
	overflow: hidden;
	zoom: 1;
}
.z-shadow .z-shadow-cr{
	background: transparent  repeat-y right;
	background-image: url(${c:encodeURL('~./zul/img/shadow-cr.png')});
	padding-right: 6px;
	overflow: hidden;
	zoom: 1;
}
.z-shadow .z-shadow-cm {
	background: transparent repeat 0 0;
	background-image: url(${c:encodeURL('~./zul/img/shadow-m.png')});
	overflow: hidden;
	zoom: 1;
}

.z-shadow .z-shadow-tl {
	background:transparent no-repeat scroll 0 top;	
	font-size:0;
	height:6px;
	line-height:0;
	margin-right:6px;
	zoom:1;
}
.z-shadow .z-shadow-tr {
	background:transparent no-repeat scroll right -6px;
	font-size:0;
	height:6px;
	line-height:0;
	margin-right:-6px;
	position:relative;
	zoom:1;
}
.z-shadow .z-shadow-bl {
	background:transparent no-repeat scroll 0 top;	
	font-size:0;
	height:6px;
	line-height:0;
	margin-right:6px;
	zoom:1;
}
.z-shadow .z-shadow-br {
	background:transparent no-repeat scroll right -6px;
	font-size:0;
	height:6px;
	line-height:0;
	margin-right:-6px;
	position:relative;
	zoom:1;
}
.z-shadow .z-shadow-tl,
.z-shadow .z-shadow-tr{
	background-image:url(${c:encodeURL('~./zul/img/shadow-tlr.png')});
}

.z-shadow .z-shadow-bl,
.z-shadow .z-shadow-br{
	background-image:url(${c:encodeURL('~./zul/img/shadow-blr.png')});
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
	border:1px solid #6699CE;
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
	background-image: url(${c:encodeURL('~./zul/img/msgbox/QuestionmarkButton-32x32.png')});
}
.z-msgbox-exclamation {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/WarningTriangle-32x32.png')});
}
.z-msgbox-imformation {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/InfoButton-32x32.png')});
}
.z-msgbox-error {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/StopSign-32x32.png')});
}

<%-- ZK error message box --%>
div.z-errbox {
	margin: 0; padding: 1px; border: 1px outset #DD7870; cursor: pointer;
	background: #ECAEA9; position: absolute; z-index: 88000;
}

<%-- Progressmeter --%>
div.z-progressmeter {
	background: #E0E8F3 repeat-x scroll 0 0 ;
	background-image: url(${c:encodeURL('~./zk/img/prgmeter_bg.png')});
	border: 1px solid #6F9CDB;
	text-align: left;
	height: 17px;
}
span.z-progressmeter-img {
	background: #A4C6F2 repeat-x scroll left center;
	background-image: url(${c:encodeURL('~./zk/img/prgmeter.png')});
	height: 17px;
	font-size: 0;
	display: -moz-inline-box; 
	display: inline-block;
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
