<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

html, body {height:100%}

<c:if test="${empty c:getProperty('org.zkoss.zul.theme.browserDefault')}">
body {
	margin: 0px; padding: 0px 5px;
}
</c:if>

<%-- paragraphs --%>

<c:choose>
<c:when  test="${!empty c:getProperty('org.zkoss.zul.theme.enableZKPrefix')}">
.zk p, .zk div, .zk span, .zk label, .zk a, .zk input, .zk textarea,
.zk button, .zk input.button, .zk input.file {
	font-family: Verdana, Tahoma, Arial, serif;
	font-size: small; font-weight: normal;
}
.zk legend {
	font-family: Tahoma, Garamond, Century, Arial, serif;
	font-size: small; font-weight: normal;
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
	font-size: small; font-weight: normal;
}
legend {
	font-family: Tahoma, Garamond, Century, Arial, serif;
	font-size: small; font-weight: normal;
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

<%-- General --%>
.z-form-disd {
	color: gray !important; cursor: default !important; opacity: .6; -moz-opacity: .6; filter: alpha(opacity=60);
}
.z-form-disd * {
	color: gray !important; cursor: default !important;
}
.text, .comboboxinp, .dateboxinp, .bandboxinp, .timeboxinp, .spinnerinp {<%--sclass + "inp"--%>
	background: #FFF url(${c:encodeURL('~./zul/img/grid/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #7F9DB9;
}
input.z-form-focus, .z-form-focus input {
	border: 1px solid #90BCE6;
}
.text-invalid {
	background: #FFF url(${c:encodeURL('~./zul/img/grid/text-bg-invalid.gif')}) repeat-x 0 0;
	border: 1px solid #DD7870;
}
.readonly, .text-disd {
	background: #ECEAE4;
}

.inline-block { <%-- used with label/checkbox and others to ensure the dimension --%>
	display:-moz-inline-box; vertical-align:top;<%-- vertical-align: make it looks same in diff browsers --%>
	display:inline-block;
}
.word-wrap, .word-wrap div.cell-inner, .word-wrap div.foot-cell-inner, .word-wrap div.head-cell-inner {
	word-wrap: break-word;
}
.overflow-hidden {
	overflow: hidden;
}
.dd-overlay {
	width: 100%; height: 100%; display: block; position: absolute; left: 0; top: 0;
	background-image: url(${c:encodeURL('~./img/spacer.gif')}); z-index: 10000;
}
.rz-win-proxy {
	border: 1px dashed #1854C2; position: absolute; overflow: hidden; left: 0; 
	top: 0; z-index: 50000; background-color: #CBDDF3; filter: alpha(opacity=50); <%-- IE --%>
	opacity: .5;
}
.move-win-ghost {
	overflow: hidden; position: absolute; filter: alpha(opacity=65) !important; <%-- IE --%>
	background: #CBDDF3; opacity: .65 !important; cursor: move !important;
}
.move-win-ghost ul {
	margin: 0; padding: 0; overflow: hidden; font-size: 0; line-height: 0;
	border: 1px solid #99bbe8; display: block; background: #cbddf3;
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
.z-shadow .ztm,.z-shadow .zbm {
	height: 6px; float: left;
}
.z-shadow .ztl, .z-shadow .ztr, .z-shadow .zbl, .z-shadow .zbr {
	width: 6px; height: 6px; float: left;
}
.z-shadow .zc {
	width:100%;
}
.z-shadow .zcl, .z-shadow .zcr {
	width: 6px; float: left; height: 100%;
}
.z-shadow .zcm {
	float: left; height: 100%; background: transparent url(${c:encodeURL('~./zul/img/shadow-m.png')});
}
.z-shadow .zt, .z-shadow .zb {
	height: 6px; overflow: hidden; width: 100%;
}
.z-shadow .zcl {
	background:transparent url(${c:encodeURL('~./zul/img/shadow-lr.png')}) repeat-y 0 0;
}
.z-shadow .zcr {
	background:transparent url(${c:encodeURL('~./zul/img/shadow-lr.png')}) repeat-y -6px 0;
}
.z-shadow .ztl {
	background:transparent url(${c:encodeURL('~./zul/img/shadow.png')}) no-repeat 0 0;
}
.z-shadow .ztm {
	background:transparent url(${c:encodeURL('~./zul/img/shadow.png')}) repeat-x 0 -30px;
}
.z-shadow .ztr {
	background:transparent url(${c:encodeURL('~./zul/img/shadow.png')}) repeat-x 0 -18px;
}
.z-shadow .zbl {
	background:transparent url(${c:encodeURL('~./zul/img/shadow.png')}) no-repeat 0 -12px;
}
.z-shadow .zbm {
	background:transparent url(${c:encodeURL('~./zul/img/shadow.png')}) repeat-x 0 -36px;
}
.z-shadow .zbr {
	background:transparent url(${c:encodeURL('~./zul/img/shadow.png')}) repeat-x 0 -6px;
}
<%-- ZK --%>
<%-- groupbox caption --%>
.caption input, .caption td {
	font-size: x-small;
}
.caption td.caption {
	font-size: small;
}
.caption button {
	font-size: xx-small; font-weight: normal;
	padding-top: 0; padding-bottom: 0; margin-top: 0; margin-bottom: 0;
}
.caption a, .caption a:visited {
	font-size: x-small; font-weight: normal; color: black; background: none;
	text-decoration: none;
}
.fieldset-bwrap {
	overflow: hidden;
}
.fieldset-collapsed {
	padding-bottom: 0 !important; border-width: 2px 0 0 0 !important;
}
.fieldset-collapsed .fieldset-bwrap {
	visibility: hidden; position: absolute; left: -1000px; top: -1000px;
}
<%-- window title/caption --%>
div.embedded, div.modal, div.overlapped, div.popup, div.highlighted, div.wndcyan {
	margin: 0; padding: 0;
}

div.wc-embedded-none, div.wc-wndcyan-none {
	margin: 0; padding: 0;
}
div.wc-embedded, div.wc-wndcyan {
	margin: 0; padding: 3px; border: 1px solid #6082ac;
}
div.wc-wndcyan {
	background: white;
}
div.wc-modal, div.wc-modal-none, div.wc-highlighted, div.wc-highlighted-none {
	margin: 0; padding: 2px; background: #f0f0d8;
}
div.wc-overlapped, div.wc-overlapped-none {
	margin: 0; padding: 2px; background: white;
}
div.wc-modal, div.wc-highlighted, div.wc-overlapped {
	margin: 0; padding: 4px; border: 2px solid #1854c2;
}
div.wc-popup, div.wc-popup-none {
	margin: 0; padding: 1px; background: white;
}
div.wc-popup {
	border: 1px solid #1854c2;
}

div.wc-embedded-none, div.wc-wndcyan-none,
div.wc-modal-none, div.wc-highlighted-none,
div.wc-overlapped-none, div.wc-popup-none {
	border: 0; overflow: hidden;
}
<%-- Window - trendy look-and-feel --%>
div.z-close-btn, div.z-close-btn-over {
	overflow: hidden; width: 17px; height: 17px; float: right; cursor: pointer;
	background: transparent url(${c:encodeURL('~./zul/img/close-off.gif')}) no-repeat;
	margin-left: 2px;
}
div.z-close-btn-over {
	background: transparent url(${c:encodeURL('~./zul/img/close-on.gif')}) no-repeat;
}
div.wc-bwrap div.wc-modal, div.wc-bwrap div.wc-modal-none, div.wc-bwrap div.wc-highlighted,
	div.wc-bwrap div.wc-highlighted-none {
	margin: 0; padding: 2px; background: white;
}
div.wt-modal, div.wt-popup, div.wt-highlighted, div.wt-overlapped, div.wt-embedded {
	overflow: hidden; zoom: 1; color: #222222; padding: 5px 0 4px 0;
}
div.wt-modal a, div.wt-modal a:visited, div.wt-modal a:hover,
	div.wt-popup a,	div.wt-popup a:visited, div.wt-popup a:hover, 
	div.wt-highlighted a, div.wt-highlighted a:visited, div.wt-highlighted a:hover,
	div.wt-overlapped a, div.wt-overlapped a:visited, div.wt-overlapped a:hover, 
	div.wt-embedded a, div.wt-embedded a:visited, div.wt-embedded a:hover {
	color: #222222;
}
div.wc-bwrap div.wc-embedded, div.wc-bwrap div.wc-wndcyan {
	margin: 0; padding: 3px; border: 1px solid #8DB2E3;
}
div.wc-bwrap div.wc-modal, div.wc-bwrap div.wc-highlighted, div.wc-bwrap div.wc-overlapped, div.wc-bwrap div.wc-popup {
	margin: 0; padding: 4px; border: 0; overflow: hidden;
}
<%-- Header --%>
div.lwt-popup, div.lwt-modal, div.lwt-highlighted, div.lwt-overlapped, div.lwt-embedded {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-l.png')}) no-repeat 0 0;
	padding-left: 5px; zoom: 1;
}
div.mwt-popup, div.mwt-modal, div.mwt-highlighted, div.mwt-overlapped, div.mwt-embedded {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-m.png')}) repeat-x 0 0;
	overflow: hidden; zoom: 1;
}
div.rwt-popup, div.rwt-modal, div.rwt-highlighted, div.rwt-overlapped, div.rwt-embedded {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-r.png')}) no-repeat right 0;
	padding-right: 5px;
}
div.mwt-popup-notitle, div.mwt-modal-notitle, div.mwt-highlighted-notitle, div.mwt-overlapped-notitle<%-- unnecessary, div.mwt-embedded--%> {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-m.png')}) repeat-x 0 0;
	overflow: hidden; zoom: 1; font-size: 0pt; height: 5px; line-height: 0pt;
}
<%-- Body --%>
div.wc-bwrap {
	overflow: hidden;
}
div.lwc-popup, div.lwc-modal, div.lwc-highlighted, div.lwc-overlapped, div.lwc-popup-none,
	div.lwc-modal-none, div.lwc-highlighted-none, div.lwc-overlapped-none {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wcp-lr.png')}) repeat-y 0 0; 
	padding-left: 5px; zoom: 1;
}
div.mwc-popup, div.mwc-modal, div.mwc-highlighted, div.mwc-overlapped {
	border:1px solid #99bbe8; padding: 0; margin:0 ; background: #dfe8f6;
}
div.mwc-popup-none, div.mwc-modal-none, div.mwc-highlighted-none, div.mwc-overlapped-none {
	border: 0; padding: 0; margin:0 ; background: #dfe8f6;
}
div.rwc-popup, div.rwc-modal, div.rwc-highlighted, div.rwc-overlapped, div.rwc-popup-none,
	div.rwc-modal-none, div.rwc-highlighted-none, div.rwc-overlapped-none {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wcp-lr.png')}) repeat-y right 0;
	padding-right: 5px; zoom: 1;
}
<%-- Footer --%>
div.lwt-embedded {
	border-bottom: 1px solid #99bbe8;
}
div.lwb-popup, div.lwb-modal, div.lwb-highlighted, div.lwb-overlapped {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-l.png')}) no-repeat 0 bottom; 
	padding-left: 5px; zoom: 1;
}
div.mwb-popup, div.mwb-modal, div.mwb-highlighted, div.mwb-overlapped {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-m.png')}) repeat-x 0 bottom;
	zoom: 1; font-size: 0pt; height: 5px; line-height: 0pt;
}
div.rwb-popup, div.rwb-modal, div.rwb-highlighted, div.rwb-overlapped {
	background: transparent url(${c:encodeURL('~./zul/img/wnd2/wtp-r.png')}) no-repeat right bottom;
	padding-right: 5px; zoom: 1;
}
<%-- Window - v30 look-and-feel --%>
td.lwt-embedded, td.mwt-embedded, td.rwt-embedded,
td.lwt-popup, td.rwt-popup, td.mwt-popup,
td.lwt-modal, td.mwt-modal, td.rwt-modal,
td.lwt-highlighted, td.mwt-highlighted, td.rwt-highlighted,
td.lwt-overlapped, td.mwt-overlapped, td.rwt-overlapped,
td.lwt-wndcyan, td.mwt-wndcyan, td.rwt-wndcyan {
	font-size: small;
	padding: 4px 0 3px 0; margin: 0 0 2px 0;
	color: white;
}
td.lwt-embedded {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wte-l.gif')}); background-repeat: no-repeat;
	width: 5px;
}
td.mwt-embedded {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wte-m.gif')}); background-repeat: repeat-x;
}
td.rwt-embedded {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wte-r.gif')}); background-repeat: no-repeat;
	width: 5px;
}
td.lwt-popup {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wtp-l.gif')}); background-repeat: no-repeat;
	width: 5px;
}
td.mwt-popup {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wtp-m.gif')}); background-repeat: repeat-x;
}
td.rwt-popup {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wtp-r.gif')}); background-repeat: no-repeat;
	width: 5px;
}

td.lwt-modal, td.lwt-highlighted, td.lwt-overlapped {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wto-l.gif')}); background-repeat: no-repeat;
	width: 7px;
}
td.mwt-modal, td.mwt-highlighted, td.mwt-overlapped {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wto-m.gif')}); background-repeat: repeat-x;
}
td.rwt-modal, td.rwt-highlighted, td.rwt-overlapped {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wto-r.gif')}); background-repeat: no-repeat;
	width: 7px;
}

td.lwt-wndcyan {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wtcyan-l.gif')}); background-repeat: no-repeat;
	width: 5px;
}
td.mwt-wndcyan {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wtcyan-m.gif')}); background-repeat: repeat-x;
}
td.rwt-wndcyan {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wtcyan-r.gif')}); background-repeat: no-repeat;
	width: 5px;
}
<%-- Window - v30 - End--%>

.title a, .title a:visited {
	color: white;
}
.caption a:hover, .title a:hover {
	text-decoration: underline;
}

div.modal_mask {
	position: absolute; z-index: 20000;
	top: 0; left: 0; width: 100%; height: 100%;
	filter: alpha(opacity=60); <%-- IE --%>
	opacity: .6;
	hasLayout: -1;<%-- not a layout element in IE --%>
	background: #E0E1E3; <%-- #dae4f5/#e1eaf7/e3ecf7 --%>
}
div.z-loading {
	position: absolute; z-index: 21000; background-color: #A8CAF8; cursor: wait;
	white-space: nowrap; border: 1px solid #83B5F7; padding:3px;
}
div.z-loading-indicator {
	color: #102B6D; border:1px solid #83B5F7; background-color: #FFF; 
	white-space: nowrap; padding:6px;
}
.z-apply-loading-icon, .z-loading-icon {
	background: transparent url(${c:encodeURL('~./zk/img/progress2.gif')}) no-repeat center;
	width: 16px; height: 16px;
}

div.z-apply-mask {
	position: absolute; z-index: 89000; top: 0; left: 0; width: 100%; height: 100%;
	filter: alpha(opacity=60); opacity: .6;
	hasLayout: -1; background: #E0E1E3; <%-- #dae4f5/#e1eaf7/e3ecf7 --%>
}
div.z-apply-loading-indicator {
	color: #102B6D; border:1px solid #A6C5DC; background-color: #FFF; 
	white-space: nowrap; padding: 2px; font: normal 11px tahoma, arial, helvetica, sans-serif;
	cursor: wait;
}
div.z-apply-loading {
	position: absolute; z-index: 89500; background-color: #CEDFEC; border: 1px solid #99C6E9;
	padding: 3px; overflow: hidden; white-space: nowrap; cursor: wait;
}
<%-- ZK separator --%>
<c:choose>
	<c:when test="${empty c:getProperty('org.zkoss.zul.Separator.spaceWithMargin')}">
	<%-- 3.0.4 and later --%>
div.hsep, div.hsep-bar {
	height: 7px; overflow: hidden; line-height: 0pt; font-size: 0pt;
}
span.vsep, span.vsep-bar {
	display:-moz-inline-box; display: inline-block;
	width: 10px; overflow: hidden;
}
div.hsep-bar {
	background-image: url(${c:encodeURL('~./img/dot.gif')});
	background-position: center left; background-repeat: repeat-x;
}
span.vsep-bar {
	background-image: url(${c:encodeURL('~./img/dot.gif')});
	background-position: top center; background-repeat: repeat-y;
}
	</c:when>
	<c:otherwise>
	<%-- backward compatible with 3.0.3 and earlier --%>
div.hsep, div.hsep-bar {
	display: block; width: 100%; padding: 0; margin: 2pt 0; font-size: 0;
}
div.vsep, div.vsep-bar {
	display: inline; margin: 0 1pt; padding: 0;
}
div.hsep-bar {
	border-top: 1px solid #888;
}
div.vsep-bar {
	border-left: 1px solid #666; margin-left: 2pt;
}
	</c:otherwise>
</c:choose>

<%-- ZK toolbar and toolbarbutton --%>
.toolbar {
	border-color: #a9bfd3; border-style: solid; border-width: 0 0 1px 0; display: block;
	padding: 2px; background: #D0DEF0 url(${c:encodeURL('~./zul/img/button/tb-bg.gif')}) repeat-x top left;
	position: relative; zoom: 1;
}
.caption .toolbar, .caption .toolbarbutton {
	background: none; border: 0;
}

.toolbar a, .toolbar a:visited, .toolbar a:hover {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: x-small; font-weight: normal; color: black;
	background: #D0DEF0; border: 1px solid #D0DEF0;
	text-decoration: none;
}
.toolbar a:hover {
	border-color: #f8fbff #aca899 #aca899 #f8fbff;
}

<%-- toolbar used in groupbox --%>
.caption .toolbar a, .caption .toolbar a:visited, .caption .toolbar a:hover {
	background: none; border: 0; color: black;
}
.caption .toolbar a:hover {
	text-decoration: underline;
}

<%-- toolbar used in window --%>
.title .toolbar a, .title .toolbar a:visited, .title .toolbar a:hover {
	background: none; border: 0; color: white;
}
.title a:hover {
	text-decoration: underline;
}

<%-- toolbar used in embedded window --%>
td.mwt-embedded, td.mwt-embedded a,
td.mwt-embedded a:visited, td.mwt-embedded a:hover {
	color: #222222;
}
td.lwt-embedded, td.mwt-embedded, td.rwt-embedded {
	border-bottom: 1px solid #8aa3c1;
}

<%-- Toolbar Panel Mold--%>
.toolbar-panel {
	padding: 5px;
}
.toolbar-panel .toolbar-panel-body td {
	border: 0; padding: 0;
}
.toolbar-panel .toolbar-panel-end table, .toolbar-end {
	float: right; clear: none;
}
.toolbar-panel .toolbar-panel-start table, .toolbar-start {
	float: left; clear: none;
}
.toolbar-panel .toolbar-panel-center, .toolbar-center {
	text-align: center;
}
.toolbar-center {
	display: table;
}
.toolbar-panel .toolbar-panel-center table, .toolbar-center {
	margin: 0 auto;
}
.toolbar-panel table td.toolbar-panel-h {
	padding: 3px;
}
.toolbar-panel table td.toolbar-panel-v {
	padding: 1px;
}
<%-- ZK tree, listbox, grid --%>
div.listbox, div.tree, div.grid {<%-- depends sclass --%>
	background: #DAE7F6; border: 1px solid #7F9DB9; overflow: hidden;
}
div.tree-head, div.listbox-head, div.grid-head, div.tree-head tr, div.listbox-head tr,
	div.grid-head tr, div.tree-foot, 
	div.listbox-foot, div.grid-foot {<%-- always used. --%>
	border: 0; overflow: hidden; width: 100%;
}

div.tree-head tr, div.listbox-head tr, div.grid-head tr {<%-- always used. --%>
	background-image: url(${c:encodeURL('~./zul/img/grid/s_hd.gif')});
}
div.tree-head th, div.listbox-head th, div.grid-head th
	<%-- ,tr.listbox-fake th ,tr.tree-fake th ,tr.grid-fake th// Header's width excluds its style by default--%> {
	overflow: hidden; border: 1px solid;
	border-color: #DAE7F6 #9EB6CE #9EB6CE #DAE7F6;
	white-space: nowrap; padding: 2px;
	font-size: small; font-weight: normal;
}
div.listbox-head th.sort div.head-cell-inner, div.grid-head th.sort div.head-cell-inner
	<%-- ,tr.listbox-fake th.sort div ,tr.tree-fake th.sort div ,tr.grid-fake th.sort div 
	// Header's width excluds its style by default--%> {
	cursor: pointer; padding-right: 9px;
	background:transparent url(${c:encodeURL('~./zul/img/sort/v_hint.gif')});
	background-position: 99% center;
	background-repeat: no-repeat;
}
div.listbox-head th.sort-asc div.head-cell-inner, div.grid-head th.sort-asc div.head-cell-inner
	<%-- ,tr.listbox-fake th.sort-asc div ,tr.tree-fake th.sort-asc div ,tr.grid-fake th.sort-asc div 
	// Header's width excluds its style by default--%> {
	cursor: pointer; padding-right: 9px;
	background:transparent url(${c:encodeURL('~./zul/img/sort/v_asc.gif')});
	background-position: 99% center;
	background-repeat: no-repeat;
}
div.listbox-head th.sort-dsc div.head-cell-inner, div.grid-head th.sort-dsc div.head-cell-inner
	<%-- ,tr.listbox-fake th.sort-dsc div ,tr.tree-fake th.sort-dsc div ,tr.grid-fake th.sort-dsc div 
	// Header's width excluds its style by default--%> {
	cursor: pointer; padding-right: 9px;
	background:transparent url(${c:encodeURL('~./zul/img/sort/v_dsc.gif')});
	background-position: 99% center;
	background-repeat: no-repeat;
}
div.head-cell-inner {
	font-size: small; font-weight: normal; font-family: Tahoma, Garamond, Century, Arial, serif;
}
div.tree-body, div.listbox-body, div.grid-body {<%-- always used. --%>
	background: white; border: 0; overflow: auto; width: 100%;
}
div.listbox-pgi, div.grid-pgi {
	border-top: 1px solid #AAB; overflow: hidden;
}
div.listbox-pgi-t, div.grid-pgi-t {
	border-bottom: 1px solid #AAB; overflow: hidden;
}
div.tree-body td, div.listbox-body td, div.tree-foot td, div.listbox-foot td {
	cursor: pointer; padding: 0 2px;
	font-size: small; font-weight: normal; overflow: hidden; 
}

div.listbox-foot, div.grid-foot, div.tree-foot{<%-- always used --%>
	background: #DAE7F6; border-top: 1px solid #9EB6CE;
}

div.foot-cell-inner, div.cell-inner, div.head-cell-inner{
	border: 0; margin: 0; padding: 0;
}
div.foot-cell-inner, div.head-cell-inner{
	overflow: hidden;
}
<%-- faker uses only for grid/listbox/tree --%>
tr.listbox-fake, tr.listbox-fake th, tr.listbox-fake div, tr.tree-fake, tr.tree-fake th, tr.tree-fake div,
	tr.grid-fake, tr.grid-fake th, tr.grid-fake div {
	border-top: 0 !important; border-bottom: 0 !important; margin-top: 0 !important;
	margin-bottom: 0 !important; padding-top: 0 !important;	padding-bottom: 0 !important;
	height: 0px !important; <%-- these above css cannot be overrided--%>
	border-left: 0; border-right: 0; margin-left: 0; margin-right: 0; padding-left: 0;
	padding-right: 0;
}
td.gc {
	padding: 2px; overflow: hidden; 
}
div.gc {
	font-size: small; font-weight: normal; color: black;
}
td.hbox-sp {
	width: 0.3em; padding: 0; margin: 0;
}
tr.vbox-sp {
	height: 0.3em; padding: 0; margin: 0;
}
tr.item, tr.item a, tr.item a:visited {
	font-size: small; font-weight: normal; color: black;
	text-decoration: none;
}
tr.item a:hover {
	text-decoration: underline;
}

tr.grid td.gc {
	background: white; border-top: none; border-left: 1px solid white;
	border-right: 1px solid #CCC; border-bottom: 1px solid #DDD;
}

tr.odd td.gc, tr.odd {
	background: #EAF2F0;<%--#E8EFEA--%>
}
tr.seld, td.seld {
	background: #b3c8e8; border: 1px solid #6f97d2;
}
tr.disd, td.disd, tr.disd div.cell-inner, td.disd div.cell-inner, tr.disd a, td.disd a, 
	a.disd, .text-disd {
	color: #C5CACB !important; cursor: default!important;
}
a.disd:visited, a.disd:hover { 
	text-decoration: none !important;
	cursor: default !important;;
	border-color: #D0DEF0 !important;
}
tr.overd, td.overd {<%-- item onmouseover --%>
	background: #D3EFFA;
}
tr.overseld, td.overseld {<%-- item selected and onmouseover --%>
	background: #82D5F8;
}
td.focusd {
	background-image: url(${c:encodeURL('~./zul/img/focusd.gif')});
	background-repeat: no-repeat;
}

<%-- The style for treeitem's paging
span.treeitem-paging {
	background-image: url(${c:encodeURL('~./zul/img/bgbtnbk.gif')});
	background-repeat: no-repeat;
	border: 1px solid #7f9db9;
}
--%>

<%-- tree icons --%>
span.tree-root-open, span.tree-tee-open, span.tree-last-open {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/open.png')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.tree-root-close, span.tree-tee-close, span.tree-last-close {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/close.png')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.tree-tee, span.tree-vbar, span.tree-last, span.tree-spacer, span.checkmark-spacer {
	width: 18px; min-height: 18px; height: 100%;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}

span.dottree-root-open {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/root-open.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.dottree-root-close {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/root-close.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.dottree-tee-open {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/tee-open.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.dottree-tee-close {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/tee-close.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.dottree-last-open {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/last-open.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.dottree-last-close {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/last-close.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.dottree-tee {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/tee.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.dottree-vbar {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/vbar.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.dottree-last {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/last.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.dottree-spacer {
	width: 18px; min-height: 18px; height: 100%;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
<%-- ZK group, listgroup --%>
td.listgroup-cell, td.group-cell{
	padding-top: 2px; border-bottom: 2px solid #84A6D4;
}
td.listgroup-cell div.cell-inner, td.group-cell div.cell-inner {
	color:#3764a0; font: bold 11px tahoma, arial, helvetica, sans-serif;
	padding: 4px 2px; width: auto;
}
.group-cell {
	color:#3764a0; font: bold 11px tahoma, arial, helvetica, sans-serif;
}
<%-- ZK groupfooter, listgroupfooter --%>
.groupfooter, .listgroupfooter{
	background-color:#F4F4F4;
}
.groupfooter-cell, td.listgroupfooter-cell div.cell-inner{
	font-weight: bold;
}
<%-- ZK tab. --%>
.tab, .tab a, a.tab {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: x-small; font-weight: normal; color: #300030;
}
.tab a, .tab a:visited, a.tab, a.tab:visited {
	text-decoration: none;
}
.tab a:hover, a.tab:hover {
	text-decoration: underline;
}
.tabsel, .tabsel a, a.tabsel {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: x-small; font-weight: bold; color: #500060;
}
.tabsel a, .tabsel a:visited, a.tabsel, a.tabsel:visited {
	text-decoration: none;
}
.tabsel a:hover, a.tabsel:hover {
	text-decoration: underline;
}

.tabdis, .tabdis a, a.tabdis {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: x-small; font-weight: normal; color: #AAAAAA;
}
.tabdis a, .tabdis a:visited, a.tabdis, a.tabdis:visited {
	text-decoration: none;
	cursor: default;
}
.tabdis a:hover, a.tabdis:hover {
	text-decoration: none;
	cursor: default;
}
.tabdissel, .tabdissel a, a.tabdissel {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: x-small; font-weight: bold; color: #999999;
}
.tabdissel a, .tabdissel a:visited, a.tabdissel, a.tabdissel:visited {
	text-decoration: none;
	cursor: default;
}
.tabdissel a:hover, a.tabdissel:hover {
	text-decoration: none;
	cursor: default;
}


div.gc-default {<%-- content of 3d groupbox --%>
	border: 1px solid #5C6C7C; padding: 5px;
}
div.tabpanel, div.tabpanel-accordion {<%-- horz, accd: tabpanel --%>
	border-left: 1px solid #5C6C7C; border-right: 1px solid #5C6C7C; 
	border-bottom: 1px solid #5C6C7C; padding: 5px;
}
td.vtabpanels {<%-- vert tabpanels --%>
	border-top: 1px solid #5C6C7C; border-right: 1px solid #5C6C7C; 
	border-bottom: 1px solid #5C6C7C;
}
div.vtabpanel {<%-- vert tabpanel --%>
	padding: 5px;
}
table.vtabsi {<%--remove it if you prefer to use the minimal width--%>
	width: 100%;
}

td.tab-3d-first1 {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-first1.gif')});
	width: 1px; height: 3px;
}
td.tab-3d-first2 {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-first2.gif')});
	width: 3px; height: 3px;
}
td.tab-3d-last1 {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-last1.gif')});
	width: 3px; height: 3px;
}
td.tab-3d-last2 {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-last2.gif')});
	width: 1px; height: 3px;
}

td.tab-3d-tl-sel, td.tabaccd-3d-tl-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-tl-sel.gif')});
	width: 5px; height: 5px;
}
td.tab-3d-tl-uns, td.tabaccd-3d-tl-uns, td.groupbox-3d-tl {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-tl-uns.gif')});
	width: 5px; height: 5px;
}
td.tab-3d-tm-sel, td.tabaccd-3d-tm-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-tm-sel.gif')});
	height: 5px;
}
td.tab-3d-tm-uns, td.tabaccd-3d-tm-uns, td.groupbox-3d-tm {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-tm-uns.gif')});
	height: 5px;
}
td.tab-3d-tr-sel, td.tabaccd-3d-tr-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-tr-sel.gif')});
	width: 5px; height: 5px;
}
td.tab-3d-tr-uns, td.tabaccd-3d-tr-uns, td.groupbox-3d-tr {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-tr-uns.gif')});
	width: 5px; height: 5px;
}

td.tab-3d-ml-sel, td.tabaccd-3d-ml-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-ml-sel.gif')});
	width: 5px;
}
td.tab-3d-ml-uns, td.tabaccd-3d-ml-uns, td.groupbox-3d-ml {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-ml-uns.gif')});
	width: 5px;
}
td.tab-3d-mm-sel, td.tabaccd-3d-mm-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-mm-sel.gif')});
}
td.tab-3d-mm-uns, td.tabaccd-3d-mm-uns, td.groupbox-3d-mm {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-mm-uns.gif')});
}
td.tab-3d-mr-sel, td.tabaccd-3d-mr-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-mr-sel.gif')});
	width: 5px;
}
td.tab-3d-mr-uns, td.tabaccd-3d-mr-uns, td.groupbox-3d-mr {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-mr-uns.gif')});
	width: 5px;
}

td.tab-3d-bl-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-bl-sel.gif')});
	width: 5px; height: 3px;
}
td.tab-3d-bl-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-bl-uns.gif')});
	width: 5px; height: 3px;
}
td.tab-3d-bm-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-bm-sel.gif')});
	height: 3px;
}
td.tab-3d-bm-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-bm-uns.gif')});
	height: 3px;
}
td.tab-3d-br-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-br-sel.gif')});
	width: 5px; height: 3px;
}
td.tab-3d-br-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-br-uns.gif')});
	width: 5px; height: 3px;
}

tr.tab-3d-m, tr.tab-v3d-m, tr.tabaccd-3d-m, tr.groupbox-3d-m {
	height: 22px;
}

td.tabaccd-3d-b, td.groupbox-3d-b {
	background-image: url(${c:encodeURL('~./zul/img/tab/3d-b.gif')});
	height: 1px;
}

td.tab-v3d-first1 {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-first1.gif')});
	width: 3px; height: 1px;
}
td.tab-v3d-first2 {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-first2.gif')});
	width: 3px; height: 3px;
}
td.tab-v3d-last1 {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-last1.gif')});
	width: 3px; height: 3px;
}
td.tab-v3d-last2 {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-last2.gif')});
	width: 3px; height: 1px;
}

td.tab-v3d-tl-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-tl-sel.gif')});
	width: 5px; height: 5px;
}
td.tab-v3d-tl-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-tl-uns.gif')});
	width: 5px; height: 5px;
}
td.tab-v3d-tm-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-tm-sel.gif')});
	height: 5px;
}
td.tab-v3d-tm-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-tm-uns.gif')});
	height: 5px;
}
td.tab-v3d-tr-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-tr-sel.gif')});
	width: 3px; height: 5px;
}
td.tab-v3d-tr-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-tr-uns.gif')});
	width: 3px; height: 5px;
}

td.tab-v3d-ml-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-ml-sel.gif')});
	width: 5px;
}
td.tab-v3d-ml-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-ml-uns.gif')});
	width: 5px;
}
td.tab-v3d-mm-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-mm-sel.gif')});
}
td.tab-v3d-mm-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-mm-uns.gif')});
}
td.tab-v3d-mr-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-mr-sel.gif')});
	width: 3px;
}
td.tab-v3d-mr-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-mr-uns.gif')});
	width: 3px;
}

td.tab-v3d-bl-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-bl-sel.gif')});
	width: 5px; height: 5px;
}
td.tab-v3d-bl-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-bl-uns.gif')});
	width: 5px; height: 5px;
}
td.tab-v3d-bm-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-bm-sel.gif')});
	height: 5px;
}
td.tab-v3d-bm-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-bm-uns.gif')});
	height: 5px;
}
td.tab-v3d-br-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-br-sel.gif')});
	width: 3px; height: 5px;
}
td.tab-v3d-br-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/v3d-br-uns.gif')});
	width: 3px; height: 5px;
}

<%-- shadow effect --%>
td.groupbox-3d-shdl {
	background-image: url(${c:encodeURL('~./img/shdlf.gif')});
	width: 6px; height: 6px;
}
td.groupbox-3d-shdm {
	background-image: url(${c:encodeURL('~./img/shdmd.gif')});
}
td.groupbox-3d-shdr {
	background-image: url(${c:encodeURL('~./img/shdrg.gif')});
	width: 6px; height: 6px;
}

<%-- ZK slider --%>
span.slider-btn {
	background-image: url(${c:encodeURL('~./zul/img/slider/btn.gif')});
	background-repeat: no-repeat;
	width: 13px; height: 17px;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
td.slider-bk {
	background-image: url(${c:encodeURL('~./zul/img/slider/bk.gif')});
	height: 17px;
}
td.slider-bkl {
	background-image: url(${c:encodeURL('~./zul/img/slider/bkl.gif')});
	width: 4px; height: 17px;
}
td.slider-bkr {
	background-image: url(${c:encodeURL('~./zul/img/slider/bkr.gif')});
	width: 4px; height: 17px;
}

span.slidersph-btn {
	background-image: url(${c:encodeURL('~./zul/img/slider/btnsph.gif')});
	background-repeat: no-repeat;
	width: 17px; height: 20px;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
td.slidersph-bk {
	background-image: url(${c:encodeURL('~./zul/img/slider/bksph.gif')});
	height: 20px;
}
td.slidersph-bkl {
	background-image: url(${c:encodeURL('~./zul/img/slider/bklsph.gif')});
	width: 4px; height: 20px;
}
td.slidersph-bkr {
	background-image: url(${c:encodeURL('~./zul/img/slider/bkrsph.gif')});
	width: 4px; height: 20px;
}

<%-- Button - trendy look-and-feel (unready for 3.5.0) --%>
.z-btn {
	font: normal 11px tahoma, verdana, helvetica; cursor: pointer; white-space: nowrap;
}
.z-btn button {
	border: 0 none; background: transparent; font: normal 11px tahoma, verdana, helvetica;
	padding-left: 3px; padding-right: 3px; cursor: pointer; margin: 0; overflow: visible;
	width: auto; -moz-outline: 0 none; outline: 0 none;
}
<c:if test="${c:isExplorer()}">
<%-- IE only --%>
.z-btn button {
	padding-top: 2px;
}
</c:if>
<c:if test="${c:isGecko()}">
<%-- Firefox only --%>
.z-btn button {
	padding-left: 0; padding-right: 0;
}
</c:if>
.z-btn-icon .z-btn-m .z-btn-text {
	background-position: center; background-repeat: no-repeat; height: 16px; width: 16px;
	cursor: pointer; white-space: nowrap; padding: 0;
}
.z-btn-icon .z-btn-m {
	padding: 1px;
}
.z-btn em {
	font-style: normal; font-weight: normal;
}
.z-btn-text-icon .z-btn-m .z-btn-text {
	background-position: 0 2px; background-repeat: no-repeat; padding-left: 18px;
	padding-top: 3px; padding-bottom: 2px; padding-right:0;
}
.z-btn-l, .z-btn-r {
	font-size: 1px; line-height: 1px;
}
.z-btn-l {
	width: 3px; height: 21px; background: url(${c:encodeURL('~./zul/img/button/btn-side.gif')}) no-repeat 0 0;}
.z-btn-r {
	width: 3px; height: 21px; background: url(${c:encodeURL('~./zul/img/button/btn-side.gif')}) no-repeat 0 -21px;
}
.z-btn-l i,.z-btn-r i {
	display: block; width: 3px; overflow: hidden; font-size: 1px; line-height: 1px;
}
.z-btn-m {
	background: url(${c:encodeURL('~./zul/img/button/btn-side.gif')}) repeat-x 0 -42px;
	vertical-align: middle; text-align: center; padding: 0 5px; cursor: pointer;
	white-space: nowrap;
}
.z-btn-over .z-btn-l {
	background-position: 0 -63px;
}
.z-btn-over .z-btn-r {
	background-position: 0 -84px;
}
.z-btn-over .z-btn-m {
	background-position: 0 -105px;
}
.z-btn-click .z-btn-m, .z-menu-btn-seld .z-btn-m {
	background-position: 0 -126px;
}
.disd z-btn * {
	color: gray!important; cursor: default!important;
}
<%-- Menu - trendy look-and-feel --%>
.z-menu-btn .z-btn-m {
	padding-right: 2px!important;
}
.z-menu-btn .z-btn-m em {
	display: block; background: transparent url(${c:encodeURL('~./zul/img/button/tb-btn-arrow.gif')}) no-repeat right 0;
	padding-right: 10px; min-height: 16px;
}
.z-btn-text-icon .z-menu-btn .z-btn-m em {
	display: block; background: transparent url(${c:encodeURL('~./zul/img/button/tb-btn-arrow.gif')}) no-repeat right 3px;
	padding-right: 10px;
}
.z-menubar-vertical .z-menu-btn .z-btn-m em {
	display: block; background: transparent url(${c:encodeURL('~./zul/img/button/tb-btn-arrow-ver.gif')}) no-repeat right 0;
	padding-right: 10px; min-height: 16px;
}
.z-menubar-vertical .z-btn-text-icon .z-menu-btn .z-btn-m em {
	display: block; background: transparent url(${c:encodeURL('~./zul/img/button/tb-btn-arrow-ver.gif')}) no-repeat right 3px;
	padding-right: 10px;
}
.z-menubar, .z-paging {
	border-color: #a9bfd3; border-style: solid; border-width: 0 0 1px 0; display: block;
	padding: 2px; background: #D0DEF0 url(${c:encodeURL('~./zul/img/button/tb-bg.gif')}) repeat-x top left;
	position: relative; zoom: 1;
}
.z-menubar .z-item-disd .z-btn-icon, .z-menupopup .z-item-disd .z-btn-icon, .z-paging .z-item-disd .z-btn-icon {
	opacity: .35; -moz-opacity: .35; filter: alpha(opacity=35);
}
.z-menubar td, .z-paging td {
	vertical-align: middle;
}
.z-menubar .z-menu-btn td {
	border: 0 !important;
}
.z-menubar td, .z-menubar span, .z-menubar input, .z-menubar div, .z-menubar select,
	.z-menubar label, .z-paging td, .z-paging span, .z-paging input,
	.z-paging div, .z-paging select, .z-paging label {
	white-space: nowrap; font: normal 11px tahoma, arial, helvetica, sans-serif;
}
.z-menubar .z-item-disd, .z-menupopup .z-item-disd, .z-paging .z-item-disd {
	color: gray !important; cursor: default !important; opacity: .5; -moz-opacity: .5; filter: alpha(opacity=50);
}
.z-menubar .z-item-disd *, .z-menupopup .z-item-disd *, .z-paging .z-item-disd * {
	color: gray !important; cursor: default !important;
}
.z-menubar .z-btn-l, .z-paging .z-btn-l {
	background: none;
}
.z-menubar .z-btn-r, .z-paging .z-btn-r {
	background: none;
}
.z-menubar .z-btn-m, .z-paging .z-btn-m {
	background: none; padding: 0;
}
.z-menubar .z-btn-over .z-btn-l, .z-paging .z-btn-over .z-btn-l {
	background: url(${c:encodeURL('~./zul/img/button/tb-btn-side.gif')}) no-repeat 0 0;
}
.z-menubar .z-btn-over .z-btn-r, .z-paging .z-btn-over .z-btn-r {
	background: url(${c:encodeURL('~./zul/img/button/tb-btn-side.gif')}) no-repeat 0 -21px;
}
.z-menubar .z-btn-over .z-btn-m, .z-paging .z-btn-over .z-btn-m {
	background: url(${c:encodeURL('~./zul/img/button/tb-btn-side.gif')}) repeat-x 0 -42px;
}
.z-menubar .z-btn-click .z-btn-l, .z-menubar .z-btn-pressed .z-btn-l,
	.z-menubar .z-menu-btn-seld .z-btn-l, .z-paging .z-btn-click .z-btn-l {
	background: url(${c:encodeURL('~./zul/img/button/tb-btn-side.gif')}) no-repeat 0 -63px;
}
.z-menubar .z-btn-click .z-btn-r, .z-menubar .z-btn-pressed .z-btn-r,
	.z-menubar .z-menu-btn-seld .z-btn-r, .z-paging .z-btn-click .z-btn-r {
	background: url(${c:encodeURL('~./zul/img/button/tb-btn-side.gif')}) no-repeat 0 -84px;
}
.z-menubar .z-btn-click .z-btn-m, .z-menubar .z-btn-pressed .z-btn-m,
	.z-menubar .z-menu-btn-seld .z-btn-m, .z-paging .z-btn-click .z-btn-m {
	background: url(${c:encodeURL('~./zul/img/button/tb-btn-side.gif')}) repeat-x 0 -105px;
}
.z-menubar .z-menu-btn .z-btn-m em {
	padding-right: 8px;
}
.z-menu-list li {
	text-decoration: none; font: normal 11px tahoma, arial, sans-serif; white-space: nowrap;
	display: block; padding: 1px;
}
a.z-menu-item {
	text-decoration: none; font: normal 11px tahoma, arial, sans-serif; white-space: nowrap;
}
a.z-menu-item .z-menu-btn .z-btn-m em {
	background: none; padding-right: 0;
}
.z-menupopup {
	border:	1px solid #7F9DB9;	z-index: 88000; zoom: 1; padding: 2px;
	background: #F0F0F0 url(${c:encodeURL('~./zul/img/menu2/pp-bg.gif')}) repeat-y;
}
.z-menupopup ul, .z-menupopup li {
	list-style: none !important; margin: 0 !important;
	list-style-position: outside !important; list-style-type: none !important;
	list-style-image: none !important;
}
.z-menupopup ul {
	padding: 0;
}
.z-menupopup a {
	text-decoration: none!important;
}
.z-menu-list {
	background: transparent; border: 0 none;
}
.z-menupopup li {	
	line-height:100%;
}
.z-menupopup li.z-menu-sp {
	font-size: 1px; line-height: 1px;
}
.z-menu-item-arrow {
	background: transparent url(${c:encodeURL('~./zul/img/menu2/arrow.gif')}) no-repeat right;
}
span.z-menu-sp {
	display: block; font-size: 1px; line-height: 1px; margin: 2px 3px; background-color: #e0e0e0;
	border-bottom: 1px solid #fff; overflow: hidden; width: auto;
}
.z-menupopup a.z-menu-item {
	display: block; line-height: 16px; padding: 3px 21px 3px 3px; white-space: nowrap;
	text-decoration: none; color: #222; -moz-outline: 0 none; outline: 0 none; cursor: pointer;
}
li.z-menu-item-over {
	background: #ebf3fd url(${c:encodeURL('~./zul/img/menu2/item-over.gif')}) repeat-x left bottom;
	border: 1px solid #aaccf6; padding: 0;
}
.z-menu-item-over a.z-menu-item {
	color:#233d6d;
}
.z-menu-item-icon {
	border: 0 none; height: 16px; padding: 0; vertical-align: top; width: 16px;
	margin: 0 8px 0 0; background-position: center;
}
.z-menu-item-ck .z-menu-item-icon {
	background: transparent url(${c:encodeURL('~./zul/img/menu/checked.gif')}) no-repeat center;
}
.z-menu-item-unck .z-menu-item-icon {
	background: transparent url(${c:encodeURL('~./zul/img/menu/unchecked.gif')}) no-repeat center;
}
.z-paging .ypb-text{
	padding:2px;
}
.z-paging .ypb-sep{
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-split.gif')});
	background-position:center;	background-repeat:no-repeat;
	display:block;font-size:1px;height:16px;width:4px;overflow:hidden;
	cursor:default;margin:0 2px 0;border:0;
}
.z-paging .ypb-spacer{
	width:2px;
}
.z-pbar-page-number {
	width: 24px; height: 14px; border: 1px solid #7F9DB9;
}
.z-pbar-page-first{
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-first.gif')})!important;
}
.z-pbar-page-last{
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-last.gif')})!important;
}
.z-pbar-page-next{
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-next.gif')})!important;
}
.z-pbar-page-prev{
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-prev.gif')})!important;
}
.z-item-disd .z-pbar-page-first{
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-first-disd.gif')})!important;
}
.z-item-disd .z-pbar-page-last{
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-last-disd.gif')})!important;
}
.z-item-disd .z-pbar-page-next{
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-next-disd.gif')})!important;
}
.z-item-disd .z-pbar-page-prev{
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-prev-disd.gif')})!important;
}
.z-paging-info{
	position:absolute;top:5px;right:8px;color:#444;
}

<%-- Menu - v30 look-and-feel --%>
div.menubar, div.menupopup {
	cursor: pointer; background: #e9effa; padding: 1px;
}
div.menupopup {
	background: #f0f5ff;
}
div.menubar {
	border: 1px solid #919397;
}
div.menupopup {
	display: block; position: absolute; z-index: 88000;
	border: 1px outset;
}
div.menubar td, div.menupopup td {
	white-space: nowrap;
}
div.menubar td {
	border: 1px solid #eff0f3;
	<%-- IE7: unable to define it with menupopup td; IE6: no transparent color --%>
}
div.menubar a, div.menubar a:visited, div.menubar a:hover, div.menupopup a, div.menupopup a:visited, div.menupopup a:hover {
	color: black; text-decoration: none;
}
td.menusp {
	height: 7px;
	background-image: url(${c:encodeURL('~./zul/img/menu/sep.gif')});
	background-repeat: repeat-x;
}
td.menu1, td.menu1ck, td.menu1unck, td.menu3ar{<%-- menuitem normal --%>
	width: 11px; height: 13px;
	padding: 0px 12px 0px 14px;
}
td.menu1 a, td.menu1ck a, td.menu3ar a{
	white-space: nowrap;
}
td.menu1ck {<%-- menuitem checked --%>
	background-image: url(${c:encodeURL('~./zul/img/menu/checked.gif')});
	background-repeat: no-repeat;
	background-position: left center;
}
td.menu1unck {<%-- menuitem unchecked --%>
	background-image: url(${c:encodeURL('~./zul/img/menu/unchecked.gif')});
	background-repeat: no-repeat;
	background-position: left center;
}
td.menu3ar {<%-- menuitem arrow --%>
	background-image: url(${c:encodeURL('~./zul/img/menu/arrow.gif')});
	background-repeat: no-repeat;
	background-position: right center;
}

<%-- Combobox, Bandbox and Datebox --%>
span.combobox, span.datebox, span.bandbox {
	border: 0; padding: 0; margin: 0; white-space: nowrap;
}
<%-- v30 mold --%>
span.rbtnbk {<%-- button at the right edge --%>
	background-image: url(${c:encodeURL('~./zul/img/btnbk.gif')}); background-repeat: no-repeat;
	border: 1px solid #7f9db9; border-left: none;
}
<%-- Combobox trendy mold --%>
img.combobox {
	background: transparent url(${c:encodeURL('~./zul/img/button/combobtn.gif')}) no-repeat 0 0;
	vertical-align: top; cursor: pointer; width: 17px; height: 19px; border: 0; 
	border-bottom: 1px solid #B5B8C8;
}
<%-- Bandbox trendy mold --%>
img.bandbox {
	background: transparent url(${c:encodeURL('~./zul/img/button/bandbtn.gif')}) no-repeat 0 0;
	vertical-align: top; cursor: pointer; width: 17px; height: 19px; border: 0; 
	border-bottom: 1px solid #B5B8C8;
}
<%-- Datebox trendy mold --%>
img.datebox {
	background: transparent url(${c:encodeURL('~./zul/img/button/datebtn.gif')}) no-repeat 0 0;
	vertical-align: top; cursor: pointer; width: 17px; height: 19px; border: 0; 
	border-bottom: 1px solid #B5B8C8;
}
<%-- Timebox trendy mold --%>
img.timebox {
	background: transparent url(${c:encodeURL('~./zul/img/button/timebtn.gif')}) no-repeat 0 0;
	vertical-align: top; cursor: pointer; width: 17px; height: 19px; border: 0; 
	border-bottom: 1px solid #B5B8C8;
}
<%-- Spinner trendy mold --%>
img.spinner {
	background: transparent url(${c:encodeURL('~./zul/img/button/timebtn.gif')}) no-repeat 0 0;
	vertical-align: top; cursor: pointer; width: 17px; height: 19px; border: 0; 
	border-bottom: 1px solid #B5B8C8;
}
span.z-rbtn-over img.combobox, span.z-rbtn-over img.bandbox, span.z-rbtn-over img.datebox,
	span.z-rbtn-over img.timebox, span.z-rbtn-over img.spinner {
	background-position: -17px 0;
}
span.z-rbtn-click img.combobox, span.z-rbtn-click img.bandbox, span.z-rbtn-click img.datebox,
	span.z-rbtn-click img.timebox, span.z-rbtn-click img.spinner {
	background-position: -34px 0;
}
.z-form-focus img.combobox, .z-form-focus img.bandbox, .z-form-focus img.datebox,
	.z-form-focus img.timebox, .z-form-focus img.spinner {
	background-position: -51px 0;
}
.z-form-focus span.z-rbtn-over img.combobox, .z-form-focus span.z-rbtn-over img.bandbox,
	.z-form-focus span.z-rbtn-over img.datebox, .z-form-focus span.z-rbtn-over img.timebox,
	.z-form-focus span.z-rbtn-over img.spinner {
	background-position: -68px 0;
}
<%-- Popup of Combox, Bandbox--%>
div.comboboxpp, div.bandboxpp {<%--sclass + "pp"--%>
	display: block; position: absolute; z-index: 88000;
	background: white; border: 1px solid #7F9DB9; padding: 2px;
	font-size: x-small;
}
div.comboboxpp {
	overflow: auto; <%-- if bandboxpp overflow:auto, crop popup if any --%>
}
.comboboxpp td {<%--label--%>
	white-space: nowrap; font-size: x-small; cursor: pointer;
}
.comboboxpp td span {<%--description--%>
	color: #888; font-size: xx-small; padding-left: 6px;
}
<%-- ZK Popup --%>
.ctxpopup {
	position: absolute; top: 0; left: 0; visibility: hidden; z-index: 88000; border: 0 none;
}
.ctxpopup .z-pp-tm {
	background: transparent url(${c:encodeURL('~./zul/img/popup/pp-tb.gif')}) repeat-x 0 0;
	overflow: hidden; zoom: 1; font-size: 0; line-height: 0; height: 8px;
}
.ctxpopup .z-pp-tl {
	background: transparent url(${c:encodeURL('~./zul/img/popup/pp-corners.gif')}) no-repeat 0 0;
	padding-left: 8px; overflow: hidden; zoom: 1;
}
.ctxpopup .z-pp-tr {
	background:transparent url(${c:encodeURL('~./zul/img/popup/pp-corners.gif')}) no-repeat right -8px;
	overflow: hidden; zoom: 1; padding-right: 8px;
}
.ctxpopup .z-pp-cm {
	background: #EEEEEE url(${c:encodeURL('~./zul/img/popup/pp-tb.gif')}) repeat-x 0 -16px;
	padding:4px 10px; overflow: hidden; zoom: 1;
}
.ctxpopup .z-pp-cl {
	background: transparent url(${c:encodeURL('~./zul/img/popup/pp-l.gif')}) repeat-y 0;
	padding-left: 4px; overflow: hidden; zoom: 1;
}
.ctxpopup .z-pp-cr {
	background: transparent url(${c:encodeURL('~./zul/img/popup/pp-r.gif')}) repeat-y right;
	padding-right: 4px; overflow: hidden; zoom: 1;
}
.ctxpopup .z-pp-bm {
	background: transparent url(${c:encodeURL('~./zul/img/popup/pp-tb.gif')}) repeat-x 0 -8px;
	height: 8px; overflow: hidden; zoom: 1;
}
.ctxpopup .z-pp-bl {
	background: transparent url(${c:encodeURL('~./zul/img/popup/pp-corners.gif')}) no-repeat 0 -16px;
	zoom: 1; padding-left: 8px;
}
.ctxpopup .z-pp-br {
	background: transparent url(${c:encodeURL('~./zul/img/popup/pp-corners.gif')}) no-repeat right -24px;
	zoom:1; padding-right: 8px;
}
.ctxpopup .z-pp-body {
	margin: 0 !important; line-height: 14px; color: #444; padding: 0;
}
<%-- ZK error message box --%>
div.errbox {
	margin: 0; padding: 1px; border: 1px outset; cursor: pointer;
	background: #E8E0D8; position: absolute; z-index: 88000;
}

div.progressmeter {
	border-top: 1px solid #666; border-left: 1px solid #666;
	border-bottom: 1px solid #bbb; border-right: 1px solid #bbb;
	text-align: left;
}
span.progressmeter-img {
	display:-moz-inline-box; display:inline-block;
	background-image: url(${c:encodeURL('~./zk/img/prgmeter.gif')});
	height: 10px; font-size:0;
}
.messagebox {
	word-break: break-all; overflow:auto;
}
.messagebox-btn {
	min-width: 45pt; width: 100%;
}
<%-- ZK JavaScript debug box --%>
div.debugbox {
	border: 1px solid #77c;	position: absolute;
	width: 60%; z-index: 99000; background: white;
}
<%--ZK datebox and calendar--%>
div.dateboxpp {<%--hardcoded in DSP--%>
	display: block; position: absolute; z-index: 88000;
	background: white; border: 1px solid black; padding: 2px;
}

table.calendar {
	background: white; border: 1px solid #7F9DB9;
}
table.calyear {
	background: #eaf0f4; border: 1px solid;
	border-color: #f8fbff #aca899 #aca899 #f8fbff;
}
table.calday {
	border: 1px solid #ddd;
}
table.calyear td {
	font-size: small; font-weight: bold; text-align: center;
	white-space: nowrap;
}
table.calmon td, tr.calday td, tr.calday td a, tr.calday td a:visited {
	font-size: x-small; color: #35254F; text-align: center;
	cursor: pointer; text-decoration: none;
}
tr.calday td {
	padding: 1px 3px;
}
tr.calday td a:hover {
	text-decoration: underline;
}
table.calmon td.seld, tr.calday td.seld {
	background: #bbd1ed; border: 1px solid #aac6e9;
}
table.calmon td.dis, tr.calday td.dis {
	color: #888;
}
tr.caldow td {
	font-size: x-small; color: #333; font-weight: bold;
	padding: 1px 2px; background: #e8e8f0; text-align: center;
}

div.dateboxpp table.calyear {
	background: #d8e8f0;
}

<%-- additional tab --%>
td.tab-lite-first1 {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-first1.gif')});
	width: 1px; height: 3px;
}
td.tab-lite-first2 {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-first2.gif')});
	width: 3px; height: 3px;
}
td.tab-lite-last1 {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-last1.gif')});
	width: 3px; height: 3px;
}
td.tab-lite-last2 {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-last2.gif')});
	width: 1px; height: 3px;
}

td.tab-lite-tl-sel, td.groupbox-lite-tl {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-tl-sel.gif')});
	width: 5px; height: 6px;
}
td.tab-lite-tl-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-tl-uns.gif')});
	width: 5px; height: 6px;
}
td.tab-lite-tm-sel, td.groupbox-lite-tm {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-tm-sel.gif')});
	height: 6px;
}
td.tab-lite-tm-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-tm-uns.gif')});
	height: 6px;
}
td.tab-lite-tr-sel, td.groupbox-lite-tr {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-tr-sel.gif')});
	width: 5px; height: 6px;
}
td.tab-lite-tr-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-tr-uns.gif')});
	width: 5px; height: 6px;
}

td.tab-lite-ml-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-ml-sel.gif')});
	width: 5px;
}
td.tab-lite-ml-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-ml-uns.gif')});
	width: 5px;
}
td.groupbox-lite-ml {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-ml-grp.gif')});
	width: 5px;
}
td.tab-lite-mm-sel, td.groupbox-lite-mm, td.tab-lite-mm-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-white.gif')});
}
td.tab-lite-mr-sel, td.groupbox-lite-mr {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-mr-sel.gif')});
	width: 5px;
}
td.tab-lite-mr-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-mr-uns.gif')});
	width: 5px;
}

td.tab-lite-bl-sel, td.tab-lite-bm-sel, td.tab-lite-br-sel {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-white.gif')});
	width: 5px; height: 3px;
}
td.tab-lite-bl-uns, td.tab-lite-bm-uns, td.tab-lite-br-uns {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-last1.gif')});
	width: 5px; height: 3px;
}

td.groupbox-lite-b {
	background-image: url(${c:encodeURL('~./zul/img/tab/lite-b.gif')});
	height: 1px;
}
div.gc-lite {<%-- content of 3d groupbox --%>
	border: 1px solid #9095a1; padding: 5px;
}

td.groupbox-lite-shdl {
	background-image: url(${c:encodeURL('~./img/shdlf.gif')});
	width: 6px; height: 6px;
}
td.groupbox-lite-shdm {
	background-image: url(${c:encodeURL('~./img/shdmd.gif')});
}
td.groupbox-lite-shdr {
	background-image: url(${c:encodeURL('~./img/shdrg.gif')});
	width: 6px; height: 6px;
}
tr.tab-lite-m, tr.groupbox-lite-m {
	height: 21px;
}

<%-- Splitter component --%>
span.splitter-btn-l, span.splitter-btn-r, span.splitter-btn-t ,span.splitter-btn-b {
    font-size:0;
}
td.splitter-h {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-h-ns.png')}");
    background-repeat: repeat-y; max-width: 8px; width: 8px;
    background-position: top right;
}
tr.splitter-v td {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-v-ns.png')}");
    background-repeat: repeat-x; max-height: 8px; height: 8px;
    background-position: bottom left;
}
div.splitter-h {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-h.png')}");
    background-position: center left; font-size:0; max-width: 8px; width: 8px;
}
div.splitter-v {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-v.png')}");
    background-position: top center; font-size:0; max-height: 8px; height: 8px;
}
div.splitter-h-ns {
    font-size:0; max-width: 8px; width: 8px;
}
div.splitter-v-ns {
    font-size:0; max-height: 8px; height: 8px;
}
span.splitter-btn-l:hover, span.splitter-btn-r:hover, span.splitter-btn-t:hover ,span.splitter-btn-b:hover {
	opacity:1;
}

span.splitter-btn-l, span.splitter-btn-r, span.splitter-btn-t ,span.splitter-btn-b {
	filter:alpha(opacity=50);  <%-- IE --%>
	opacity:0.5;  <%-- Moz + FF --%>	
	background-repeat: no-repeat; vertical-align:top;
	display:-moz-inline-box; display:inline-block; font-size:0;
}

span.splitter-btn-visi {
	filter:alpha(opacity=100) !important;  <%-- IE --%>
}
span.splitter-btn-l {
	width: 6px; min-height: 50px; height: 50px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-l.png')});
}
span.splitter-btn-r {
	width: 6px; min-height: 50px; height: 50px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-r.png')});
}
span.splitter-btn-t {
	width: 50px; min-height: 6px; height: 6px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-t.png')});

}
span.splitter-btn-b {
	width: 50px; min-height: 6px; height: 6px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-b.png')});
}

<%-- Splitter - OS look-and-feel --%>
span.splitter-os-btn-l, span.splitter-os-btn-r, span.splitter-os-btn-t ,span.splitter-os-btn-b {
    font-size:0;
}
td.splitter-os-h {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-h.gif')}");
    background-repeat: repeat-y; max-width: 8px; width: 8px;
    background-position: top right;
}
tr.splitter-os-v td {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-v.gif')}");
    background-repeat: repeat-x; max-height: 8px; height: 8px;
    background-position: bottom left;
}
div.splitter-os-h, div.splitter-os-h-ns {
    font-size:0; max-width: 8px; width: 8px;
}
div.splitter-os-v, div.splitter-os-v-ns {
    font-size:0; max-height: 8px; height: 8px;
}
span.splitter-os-btn-l:hover, span.splitter-os-btn-r:hover, span.splitter-os-btn-t:hover ,span.splitter-os-btn-b:hover {
	opacity:1;
}

span.splitter-os-btn-l, span.splitter-os-btn-r, span.splitter-os-btn-t ,span.splitter-os-btn-b {
	filter:alpha(opacity=50);  <%-- IE --%>
	opacity:0.5;  <%-- Moz + FF --%>	
	background-repeat: no-repeat; vertical-align:top;
	display:-moz-inline-box; display:inline-block; font-size: 0;
}

span.splitter-os-btn-l {
	width: 8px; min-height: 50px; height: 50px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-l.gif')});
}
span.splitter-os-btn-r {
	width: 8px; min-height: 50px; height: 50px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-r.gif')});
}
span.splitter-os-btn-t {
	width: 50px; min-height: 8px; height: 8px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-t.gif')});

}
span.splitter-os-btn-b {
	width: 50px; min-height: 8px; height: 8px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-b.gif')});
}
<%-- ZK Drag-Drop --%>
span.drop-allow, span.drop-disallow {
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
	width: 16px; min-height: 16px; height: 16px;
}
span.drop-allow {
	background-image: url(${c:encodeURL('~./zul/img/grid/drop-yes.gif')});
}
span.drop-disallow {
	background-image: url(${c:encodeURL('~./zul/img/grid/drop-no.gif')});
}
div.drop-ghost {
	border:1px solid #6593CF;
}
div.drop-content {
	background-image: url(${c:encodeURL('~./zul/img/grid/drop-bg.gif')});	
	width:120px;height:18px;
	padding:2px;
	font-size:13px;
	font-weight: normal; font-family: Tahoma, Garamond, Century, Arial, serif;
}
<%-- // Header's width includs its style
tr.listbox-fake th, tr.tree-fake th, tr.grid-fake th {
	overflow: hidden; border: 1px solid;
	border-color: #DAE7F6 #9EB6CE #9EB6CE #DAE7F6;
	white-space: nowrap; padding: 2px;
	font-size: small; font-weight: normal;
}
tr.listbox-fake th.sort div, tr.tree-fake th.sort div, tr.grid-fake th.sort div{
	cursor: pointer; padding-right: 9px;
	background:transparent url(${c:encodeURL('~./zul/img/sort/v_hint.gif')});
	background-position: 99% center;
	background-repeat: no-repeat;
}
tr.listbox-fake th.sort-asc div, tr.tree-fake th.sort-asc div, tr.grid-fake th.sort-asc div {
	cursor: pointer; padding-right: 9px;
	background:transparent url(${c:encodeURL('~./zul/img/sort/v_asc.gif')});
	background-position: 99% center;
	background-repeat: no-repeat;
}
tr.listbox-fake th.sort-dsc div, tr.tree-fake th.sort-dsc div, tr.grid-fake th.sort-dsc div {
	cursor: pointer; padding-right: 9px;
	background:transparent url(${c:encodeURL('~./zul/img/sort/v_dsc.gif')});
	background-position: 99% center;
	background-repeat: no-repeat;
}--%>