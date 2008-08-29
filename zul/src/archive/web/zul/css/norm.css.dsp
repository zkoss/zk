<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<c:set var="fontSizeM" value="small" scope="request" if="${empty fontSizeM}"/>
<c:set var="fontSizeS" value="x-small" scope="request" if="${empty fontSizeS}"/>
<c:set var="fontSizeXS" value="xx-small" scope="request" if="${empty fontSizeXS}"/>

html, body {height:100%}

<c:if test="${empty c:getProperty('org.zkoss.zul.theme.browserDefault')}">
body {
	margin: 0px; padding: 0px 5px;
}
</c:if>

<%-- paragraphs --%>
<c:choose>
<c:when test="${!empty c:getProperty('org.zkoss.zul.theme.enableZKPrefix')}">
.zk p, .zk div, .zk span, .zk label, .zk a, .zk input, .zk textarea,
.zk button, .zk input.button, .zk input.file {
	font-family: Verdana, Tahoma, Arial, serif;
	font-size: ${fontSizeM}; font-weight: normal;
}
.zk legend {
	font-family: Tahoma, Garamond, Century, Arial, serif;
	font-size: ${fontSizeM}; font-weight: normal;
}
.zk th {
	font-family: Tahoma, Garamond, Century, Arial, serif;
	font-weight: bold; 
}
.zk thead tr {
	font-family: Tahoma, Garamond, Century, Arial, serif;
	font-weight: bold;
}
.zk img {border: 0;}
</c:when>
<c:otherwise>
p, div, span, label, a, input, textarea,
button, input.button, input.file {
	font-family: Verdana, Tahoma, Arial, serif;
	font-size: ${fontSizeM}; font-weight: normal;
}
legend {
	font-family: Tahoma, Garamond, Century, Arial, serif;
	font-size: ${fontSizeM}; font-weight: normal;
}
th {
	font-family: Tahoma, Garamond, Century, Arial, serif;
	font-weight: bold; 
}
thead tr {
	font-family: Tahoma, Garamond, Century, Arial, serif;
	font-weight: bold;
}
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
em.z {
	font-style: normal; font-weight: normal;
	font-family: Verdana, Tahoma, Arial, serif;
	font-size: ${fontSizeM};
}
i.z {
	display: block; width: 1px !important; overflow: hidden;
	font-size: 1px !important; line-height: 1px !important;
}
button.z {
	padding:0 !important; margin:0 !important; border:0 !important;
	background: transparent !important;
	font-size: 1px !important; width: 1px !important;
	height: ${c:isGecko() ? 0: 1}px !important;
	<c:if test="${c:isSafari()}"><%-- remove browser's focus effect --%>
	position:absolute; left:0; top:-5px;
	</c:if>
}

.z-modal-mask {
	position: absolute; z-index: 20000;
	top: 0; left: 0; width: 100%; height: 100%;
	filter: alpha(opacity=60); <%-- IE --%>
	opacity: .6;
	hasLayout: -1;<%-- not a layout element in IE --%>
	background: #E0E1E3; <%-- #dae4f5/#e1eaf7/e3ecf7 --%>
}
.z-loading {
	position: absolute; z-index: 21000; background-color: #A8CAF8; cursor: wait;
	white-space: nowrap; border: 1px solid #83B5F7; padding:3px;
}
.z-loading-indicator {
	color: #102B6D; border:1px solid #83B5F7; background-color: #FFF; 
	white-space: nowrap; padding:6px;
}
.z-apply-loading-icon, .z-loading-icon {
	background: transparent url(${c:encodeURL('~./zk/img/progress2.gif')}) no-repeat center;
	width: 16px; height: 16px;
}

.z-apply-mask {
	position: absolute; z-index: 89000; top: 0; left: 0; width: 100%; height: 100%;
	filter: alpha(opacity=60); opacity: .6;
	hasLayout: -1; background: #E0E1E3; <%-- #dae4f5/#e1eaf7/e3ecf7 --%>
}
.z-apply-loading-indicator {
	color: #102B6D; border:1px solid #A6C5DC; background-color: #FFF; 
	white-space: nowrap; padding: 2px; font: normal 11px tahoma, arial, helvetica, sans-serif;
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
	font-size:13px;
	font-weight: normal; font-family: Tahoma, Garamond, Century, Arial, serif;
}

<%-- ZK error message box --%>
div.z-errbox {
	margin: 0; padding: 1px; border: 1px outset; cursor: pointer;
	background: #E8E0D8; position: absolute; z-index: 88000;
}

<%-- Progressmeter --%>
div.z-progressmeter {	
	background:#E0E8F3 url(${c:encodeURL('~./zk/img/prgmeter_bg.gif')}) repeat-x scroll 0 0 ;
	border:1px solid #7FA9E4;
	text-align: left;
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
	font-size: ${fontSizeM}; font-weight: normal; font-family: Tahoma, Garamond, Century, Arial, serif;
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
