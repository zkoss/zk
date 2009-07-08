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
div.z-error .btn {
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
	width: 100%;
	height: 100%;
	position: absolute;
	z-index: 32000;
	top: 0;
	left: 0;
	filter: alpha(opacity=60);
	opacity: .6;
	zoom: 1;
	text-align: center;
	background: #E0E1E3;
}
.noscript p {
	background: white;
	font-weight: bold;
	color: black;
	margin: 10% 15%;
	padding: 10px 0;
	border: 1px solid black;
	filter: alpha(opacity=100);
	opacity: 1;
}

.z-label, .z-radio-cnt, .z-checkbox-cnt, .z-slider-pp, input.button, input.file,
.z-loading, .z-errbox {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; font-weight: normal;
}
.z-upload-icon {
	overflow: hidden;
	background-image: url(${c:encodeURL('~./zk/img/prgmeter.png')});
}
.z-modal-mask {
	width: 100%;
	height: 100%;
	position: absolute;
	top: 0; left: 0;
	filter: alpha(opacity=60); <%-- IE --%>
	opacity: .6;
	zoom: 1;
	background: #E0E1E3;
}
.z-loading {
	background-color: #6eadff;
	position: absolute;
	cursor: wait;
	padding:3px;
	white-space: nowrap;
	border: 1px solid #83B5F7;
	z-index: 31000;
	left: 0; top: 0;
}
.z-loading-indicator {
	background-color: #FFFFFF;
	color: #102B6D;
	border:1px solid #83B5F7;
	white-space: nowrap;
	padding:6px;
}
.z-apply-loading-icon,
.z-loading-icon {
	height: 16px;
	width: 16px;
	display:-moz-inline-box; vertical-align:top; display:inline-block;
	background: transparent no-repeat center;
	background-image: url(${c:encodeURL('~./zk/img/progress2.gif')});
}

.z-apply-mask {
	width: 100%;
	height: 100%;
	background: #E0E1E3;
	position: absolute;
	z-index: 89000;
	top: 0;
	left: 0;
	filter: alpha(opacity=60);
	opacity: .6;
	zoom: 1;
}
.z-apply-loading-indicator {
	border:1px solid #A6C5DC;
	background-color: #FFF;
	color: #102B6D;
	white-space: nowrap;
	padding: 2px;
	font: normal ${fontSizeM} ${fontFamilyT};
	cursor: wait;
}
.z-apply-loading {
	position: absolute;
	background-color: #CEDFEC;
	border: 1px solid #99C6E9;
	z-index: 89500;
	padding: 3px;
	cursor: wait;
	overflow: hidden;
	white-space: nowrap;
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
.z-dd-stackup {
	height: 100%;
	width: 100%;
	position: absolute;
	left: 0;
	top: 0;
	z-index: 16800;
	background-image: url(${c:encodeURL('~./img/spacer.gif')});
}
<%-- Fix float issue for CSS --%>
.z-clear {
	clear: both;
	height: 1px;
	width: 1px;
	line-height: 0;
	font-size: 0;
	overflow: hidden;
}
<%-- Shadow --%>
.z-shadow {
	position: absolute;
	display: none;
	left: 0;
	top: 0;
	overflow: hidden;
}
.z-shadow-wrapper{
	padding-bottom:6px;
	height:100%;
}
.z-shadow .z-shadow-cl{
	background: transparent  repeat-y 0 0;
	background-image: url(${c:encodeURL('~./zul/img/shadow-cl.png')});
	padding-left: 6px;
	overflow: hidden;
	height:100%;
	zoom: 1;
}
.z-shadow .z-shadow-cr{
	background: transparent  repeat-y right;
	background-image: url(${c:encodeURL('~./zul/img/shadow-cr.png')});
	padding-right: 6px;
	overflow: hidden;
	height:100%;
	zoom: 1;
}
.z-shadow .z-shadow-cm {
	background: transparent repeat 0 0;
	background-image: url(${c:encodeURL('~./zul/img/shadow-m.png')});
	overflow: hidden;
	height:100%;
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
.z-dragged {
	background: #E0EAF0; color: #888;
}
.z-drag-over {
	background: #ADD2FF;
}
span.z-drop-allow, span.z-drop-disallow {
	background-repeat: no-repeat;
	vertical-align: top;
	display: -moz-inline-box;
	display: inline-block;
	width: 16px;
	min-height: 16px;
	height: 16px;
}
span.z-drop-allow {
	background: transparent no-repeat 0 -64px;
	background-image: url(${c:encodeURL('~./zul/img/misc/drag.png')});
}
span.z-drop-disallow {
	background: transparent no-repeat 0 -80px;
	background-image: url(${c:encodeURL('~./zul/img/misc/drag.png')});
}
div.z-drop-ghost {
	border:1px solid #6699CE;
}
div.z-drop-cnt {
	width: 120px;
	height: 18px;
	background-image: url(${c:encodeURL('~./zul/img/misc/drop-bg.gif')});
	padding: 2px;
	font-size: ${fontSizeM};
	font-weight: normal;
	font-family: ${fontFamilyC};
}
<%-- ZK Fileupload --%>
.z-fileupload-img {
	width: 16px;
	padding-top: 4px;
}
.z-fileupload-add {
	cursor: pointer;
	background: transparent no-repeat 0 -23px;
	background-image: url(${c:encodeURL('~./zul/img/misc/fileupload.gif')});
	width: 16px;
	height: 17px;
}
.z-fileupload-delete {
	cursor: pointer;
	background: transparent no-repeat 0 0;
	background-image: url(${c:encodeURL('~./zul/img/misc/fileupload.gif')});
	width: 16px;
	height: 17px;
}
<%-- ZK Massagebox --%>
.z-msgbox{
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
.z-msgbox-imformation {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/info-btn.png')});
}
.z-msgbox-error {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/stop-btn.png')});
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
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowD.png')});
}
.z-arrow-l {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowL.png')});
}
.z-arrow-ld {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowLD.png')});
}
.z-arrow-lu {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowLU.png')});
}
.z-arrow-rd {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowRD.png')});
}
.z-arrow-ru {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowRU.png')});
}
.z-arrow-r {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowR.png')});
}
.z-arrow-u {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowU.png')});
}
.z-errbox-close {
	background-image: url(${c:encodeURL('~./zul/img/errbox/close.gif')});
}
.z-errbox-close-over {
	background-image: url(${c:encodeURL('~./zul/img/errbox/close-over.gif')});
}
.z-errbox.z-popup .z-popup-tl,
.z-errbox.z-popup .z-popup-tr,
.z-errbox.z-popup .z-popup-bl,
.z-errbox.z-popup .z-popup-br {
	background-image:url(${c:encodeURL('~./zul/img/errbox/pp-corner.png')});
}
.z-errbox.z-popup .z-popup-cm {
	background-color : #FDF2E7;
	background-image: url(${c:encodeURL('~./zul/img/errbox/pp-cm.png')});
}
.z-errbox.z-popup .z-popup-cl,
.z-errbox.z-popup .z-popup-cr {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pp-clr.png')});
}
<%-- Progressmeter --%>
div.z-progressmeter {
	background: #E0E8F3 repeat-x scroll 0 0 ;
	background-image: url(${c:encodeURL('~./zk/img/prgmeter_bg.png')});
	border:1px solid #6F9CDB;
	text-align: left;
	height: 17px;
}
span.z-progressmeter-img {
	display: -moz-inline-box;
	display: inline-block;
	background: #A4C6F2 repeat-x scroll left center;
	background-image: url(${c:encodeURL('~./zk/img/prgmeter.png')});
	height: 17px;
	line-height: 0;
	font-size: 0;
}
.z-messagebox {
	word-break: break-all;
	overflow:auto;
}
.z-messagebox-btn {
	min-width: 45pt;
	width: 100%;
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
<%-- Separator --%>
<c:include page="~./zul/css/cmps/separator.css.dsp"/>
<%-- Toolbar and Toolbarbutton --%>
<c:include page="~./zul/css/cmps/toolbar.css.dsp"/>
<%-- Slider --%>
<c:include page="~./zul/css/cmps/slider.css.dsp"/>
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
<%-- Borderlayout --%>
<c:include page="~./zul/css/cmps/layout.css.dsp"/>
<%-- Fileupload --%>
<c:include page="~./zul/css/cmps/fileupload.css.dsp"/>

<%-- IE --%>
<c:if test="${c:isExplorer()}">
<c:choose>
<c:when  test="${!empty c:property('org.zkoss.zul.theme.enableZKPrefix')}">
.zk img	{
	hspace: 0; vspace: 0
}
.zk option {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeXS}; font-weight: normal;
}
</c:when>
<c:otherwise>
img	{
	hspace: 0; vspace: 0
}
option {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeXS}; font-weight: normal;
}
</c:otherwise>
</c:choose>

.z-messagebox-btn {
	width: 47pt;
	text-overflow: ellipsis;
}
<%-- Widget.css.dsp --%>
.z-textbox-disd *, .z-decimalbox-disd *, .z-intbox-disd *, .z-longbox-disd *, .z-doublebox-disd * {
	filter: alpha(opacity=60);
}

<%-- shadow, tree.css.dsp, listbox.css.dsp, and grid.css.dsp --%>
<c:if test="${c:browser('ie6-')}">
.z-shadow {
	background: #888; zoom: 1; display: none;
	filter: progid:DXImageTransform.Microsoft.Blur(PixelRadius=4, MakeShadow=true, ShadowOpacity=0.30)
}

div.z-listbox, div.z-tree, div.z-dottree, div.z-filetree, div.z-vfiletree, div.z-grid {
	position:relative; <%-- Bug 1914215 and Bug 1914054 --%>
}
</c:if>
div.z-tree-header, div.z-dottree-header, div.z-filetree-header, div.z-vfiletree-header,
div.z-listbox-header, div.z-grid-header, div.z-tree-footer, div.z-listbox-footer,
	div.z-grid-footer {<%-- always used. --%>
	position:relative;
	<%-- Bug 1712708 and 1926094:  we have to specify position:relative --%>
}
div.z-tree-header th.z-treecol, div.z-tree-header th.z-auxheader,
div.z-dottree-header th.z-treecol, div.z-dottree-header th.z-auxheader,
div.z-filetree-header th.z-treecol, div.z-filetree-header th.z-auxheader,
div.z-vfiletree-header th.z-treecol, div.z-vfiletree-header th.z-auxheader,
div.z-listbox-header th.z-listheader, div.z-listbox-header th.z-auxheader,
div.z-grid-header th.z-column, div.z-grid-header th.z-auxheader {
	text-overflow: ellipsis;
}
div.z-treecol-cnt, div.z-dottreecol-cnt, div.z-filetreecol-cnt, div.z-vfiletreecol-cnt,
div.z-listheader-cnt, div.z-column-cnt, .z-auxheader-cnt {
	white-space: nowrap;
	<%-- Bug #1839960  --%>
}
div.z-footer-cnt, div.z-row-cnt, div.z-group-cnt, div.z-groupfoot-cnt, div.z-column-cnt,
div.z-treefooter-cnt, div.z-treecell-cnt, div.z-treecol-cnt,
div.z-dottreefooter-cnt, div.z-dottreecell-cnt, div.z-dottreecol-cnt,
div.z-filetreefooter-cnt, div.z-filetreecell-cnt, div.z-filetreecol-cnt,
div.z-vfiletreefooter-cnt, div.z-vfiletreecell-cnt, div.z-vfiletreecol-cnt,
.z-auxheader-cnt, div.z-listfooter-cnt, div.z-listcell-cnt, div.z-listheader-cnt {
	position: relative;
	<%-- Bug #1825896  --%>
}
div.z-row-cnt, div.z-group-cnt, div.z-groupfoot-cnt,div.z-listcell-cnt,
div.z-treecell-cnt, div.z-dottreecell-cnt, div.z-filetreecell-cnt, div.z-vfiletreecell-cnt {
	width: 100%;
}
div.z-tree-body, div.z-dottree-body, div.z-filetree-body, div.z-vfiletree-body, div.z-listbox-body, div.z-grid-body {<%-- always used. --%>
	position: relative;
	<%-- Bug 1766244: we have to specify position:relative with overflow:auto --%>
}
tr.z-grid-faker, tr.z-listbox-faker, tr.z-tree-faker, tr.z-dottree-faker, tr.z-filetree-faker, tr.z-vfiletree-faker {
	position: absolute; top: -1000px; left: -1000px;<%-- fixed a white line for IE --%>
}
span.z-tree-root-open, span.z-tree-root-close, span.z-tree-tee-open, span.z-tree-tee-close,
span.z-tree-last-open, span.z-tree-last-close, span.z-tree-tee, span.z-tree-vbar, span.z-tree-last, span.z-tree-spacer,
span.z-dottree-root-open, span.z-dottree-root-close, span.z-dottree-tee-open, span.z-dottree-tee-close,
span.z-dottree-last-open, span.z-dottree-last-close, span.z-dottree-tee, span.z-dottree-vbar, span.z-dottree-last, span.z-dottree-spacer,
span.z-filetree-root-open, span.z-filetree-root-close, span.z-filetree-tee-open, span.z-filetree-tee-close,
span.z-filetree-last-open, span.z-filetree-last-close, span.z-filetree-tee, span.z-filetree-vbar, span.z-filetree-last, span.z-filetree-spacer,
span.z-vfiletree-root-open, span.z-vfiletree-root-close, span.z-vfiletree-tee-open, span.z-vfiletree-tee-close,
span.z-vfiletree-last-open, span.z-vfiletree-last-close, span.z-vfiletree-tee, span.z-vfiletree-vbar, span.z-vfiletree-last, span.z-vfiletree-spacer {
	height: 18px;
}

<%-- combo.css.dsp --%>
.z-combobox-pp .z-comboitem-inner {<%--description--%>
	padding-left: 5px;
}
.z-calendar-calyear td, .z-datebox-calyear td {
	color: black; <%-- 1735084 --%>
}

<%-- Append New --%>
<c:if test="${c:isExplorer7()}">
.z-panel-tm {
	overflow: visible;
}
</c:if>
.z-panel-header {
	zoom: 1;
}
.z-panel-btm {
	position: relative;
}
<%-- groupbox.css.dsp --%>
<c:if test="${c:isExplorer7()}">
.z-groupbox-body {
	zoom: 1;
}
</c:if>


<%-- IE 6 GIF  --%>
<c:if test="${c:browser('ie6-')}">
<%-- norm.css.dsp --%>
span.z-drop-allow,
span.z-drop-disallow {
	background-image: url(${c:encodeURL('~./zul/img/misc/drag.gif')});
}
.z-msgbox-question {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/question-btn.gif')});
}
.z-msgbox-exclamation {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/warning-btn.gif')});
}
.z-msgbox-imformation {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/info-btn.gif')});
}
.z-msgbox-error {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/stop-btn.gif')});
}
<%-- error box --%>
.z-arrow-d {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowD.gif')});
}
.z-arrow-l {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowL.gif')});
}
.z-arrow-ld {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowLD.gif')});
}
.z-arrow-lu {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowLU.gif')});
}
.z-arrow-rd {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowRD.gif')});
}
.z-arrow-ru {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowRU.gif')});
}
.z-arrow-r {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowR.gif')});
}
.z-arrow-u {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowU.gif')});
}
.z-errbox.z-popup .z-popup-tl,
.z-errbox.z-popup .z-popup-tr,
.z-errbox.z-popup .z-popup-bl,
.z-errbox.z-popup .z-popup-br {
	background-image:url(${c:encodeURL('~./zul/img/errbox/pp-corner.gif')});
}
.z-errbox.z-popup .z-popup-cm {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pp-cm.gif')});
}
.z-errbox.z-popup .z-popup-cl,
.z-errbox.z-popup .z-popup-cr {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pp-clr.gif')});
}
<%-- grid.css.dsp --%>
.z-columns-menu-grouping .z-menu-item-img {
	background-image:  url(${c:encodeURL('~./zul/img/grid/menu-group.gif')});
}
.z-columns-menu-asc .z-menu-item-img {
	background-image:  url(${c:encodeURL('~./zul/img/grid/menu-arrowup.gif')});
}
.z-columns-menu-dsc .z-menu-item-img {
	background-image:  url(${c:encodeURL('~./zul/img/grid/menu-arrowdown.gif')});
}
<%-- listbox.css.dsp --%>
tr.z-listitem td.z-listitem-focus {
	background-image: url(${c:encodeURL('~./zul/img/common/focusd.gif')});
}
<%-- paging.css.dsp --%>
.z-paging-btn .z-paging-next,
.z-paging-btn .z-paging-prev,
.z-paging-btn .z-paging-last,
.z-paging-btn .z-paging-first {	
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-btn.gif')}) !important;
}
<%-- panel.css.dsp --%>
.z-panel-tl,
.z-panel-tr,
.z-panel-bl,
.z-panel-br {
	background-image:url(${c:encodeURL('~./zul/img/wnd/panel-corner.gif')});
}
<%-- popup.css.dsp --%>
.z-popup .z-popup-tl,
.z-popup .z-popup-tr,
.z-popup .z-popup-bl,
.z-popup .z-popup-br {
	background-image:url(${c:encodeURL('~./zul/img/popup/pp-corner.gif')});
}
.z-popup .z-popup-cl,
.z-popup .z-popup-cr {
	background-image: url(${c:encodeURL('~./zul/img/popup/pp-clr.gif')});
}
.z-popup .z-popup-cm {
	background-image: url(${c:encodeURL('~./zul/img/popup/pp-cm.gif')});
}
<%-- slider.css.dsp --%>
.z-slider-sphere-hor,
.z-slider-scale,
.z-slider-hor,
.z-slider-sphere-hor-center,
.z-slider-scale-center,
.z-slider-hor-center {
	background-image:url(${c:encodeURL('~./zul/img/slider/slider-bg.gif')});
}
.z-slider-sphere-hor-btn,
.z-slider-scale-btn,
.z-slider-hor-btn {
    background-image : url(${c:encodeURL('~./zul/img/slider/slider-square.gif')});
}
.z-slider-scale-btn {
	background-image : url(${c:encodeURL('~./zul/img/slider/slider-scale.gif')});
}
.z-slider-sphere-ver,
.z-slider-ver,
.z-slider-sphere-ver-center,
.z-slider-ver-center {
	background-image:url(${c:encodeURL('~./zul/img/slider/slider-bg-ver.gif')});
}

.z-slider-sphere-ver-btn,
.z-slider-ver-btn {
	background-image : url(${c:encodeURL('~./zul/img/slider/slider-v-square.gif')});
}
.z-slider-sphere-hor-btn {
	background-image : url(${c:encodeURL('~./zul/img/slider/slider-circle.gif')});
}
.z-slider-sphere-ver-btn {
	background-image : url(${c:encodeURL('~./zul/img/slider/slider-v-circle.gif')});
}
<%-- tabbox.css.dsp --%>
.z-tab-hl,
.z-tab-hr {
	background-image: url(${c:encodeURL('~./zul/img/tab/tab-corner.gif')});
}
.z-tab-hm {
	background-image: url(${c:encodeURL('~./zul/img/tab/tab-hm.gif')});
}
.z-tab-ver-hl,
.z-tab-ver-hl .z-tab-ver-hr {
	background-image: url(${c:encodeURL('~./zul/img/tab/tab-v-corner.gif')});
}
.z-tab-ver .z-tab-ver-hm {
	background-image: url(${c:encodeURL('~./zul/img/tab/tab-v-hm.png')});
}
.z-tab-accordion-tl,
.z-tab-accordion-tr {
	background-image: url(${c:encodeURL('~./zul/img/tab/accd-corner.gif')});
}
<%-- tree.css.dsp --%>
tr.z-treerow td.z-treerow-focus {
	background-image: url(${c:encodeURL('~./zul/img/common/focusd.gif')});
}
span.z-vfiletree-ico,span.z-vfiletree-firstspacer {
	background-image: url(${c:encodeURL('~./zul/img/tree/vfolder-toggle.gif')});
}
span.z-vfiletree-tee, span.z-vfiletree-last {
	background-image: url(${c:encodeURL('~./zul/img/tree/ventity.gif')});
}
<%-- window.css.dsp --%>
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

.z-groupbox-tl {
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-corner.gif')});
}
.z-groupbox-tr{
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-corner.gif')});
}

.z-groupbox-hl {
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-hl.gif')});
}
.z-groupbox-hr {
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-hr.gif')});
}
.z-groupbox-hm {
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-hm.gif')});
}
</c:if>
</c:if>

<%-- Gecko --%>
<c:if test="${c:isGecko()}">
.z-messagebox-btn {
	width: 45pt;
}
<%-- combo.css.dsp --%>
span.z-combobox-btn, span.z-datebox-btn, span.z-bandbox-btn, span.z-timebox-btn,
span.z-spinner-btn {<%-- button at the right edge --%>
	margin: 0; padding: 0;
}
<%-- tree.css.dsp, grid.css.dsp, and listbox.css.dsp --%>
.z-word-wrap div.z-treecell-cnt, .z-word-wrap div.z-treefooter-cnt, 
.z-word-wrap div.z-treecol-cnt,
.z-word-wrap div.z-row-cnt, 
.z-word-wrap div.z-group-cnt,
.z-word-wrap div.z-groupfoot-cnt,
.z-word-wrap div.z-footer-cnt, .z-word-wrap div.z-column-cnt,
.z-word-wrap div.z-listcell-cnt, .z-word-wrap div.z-listfooter-cnt,
.z-word-wrap div.z-listheader-cnt {
	overflow: hidden;
	-moz-binding: url(${c:encodeURL('~./zk/wordwrap.xml#wordwrap')});
}
span.z-word-wrap {<%-- label use only --%>
	display: block;
}
</c:if>

<%-- Opera --%>
<c:if test="${c:isOpera()}">
option {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeXS}; font-weight: normal;
}

<%-- Append New --%>
.z-messagebox-btn {
	width: 47pt;
}
</c:if>
