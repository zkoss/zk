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

<%-- General --%>
.text, .comboboxinp, .dateboxinp, .bandboxinp, .timeboxinp {<%--sclass + "inp"--%>
	background: #FFF url(${c:encodeURL('~./zul/img/grid/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #7F9DB9;
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
<%-- ZK --%>
<%-- groupbox caption --%>
.caption input, .caption td {
	font-size: ${fontSizeS};
}
.caption td.caption {
	font-size: ${fontSizeM};
}
.caption button {
	font-size: ${fontSizeXS}; font-weight: normal;
	padding-top: 0; padding-bottom: 0; margin-top: 0; margin-bottom: 0;
}
.caption a, .caption a:visited {
	font-size: ${fontSizeS}; font-weight: normal; color: black; background: none;
	text-decoration: none;
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
	border: 0;
}

td.lwt-embedded, td.mwt-embedded, td.rwt-embedded,
td.lwt-popup, td.rwt-popup, td.mwt-popup,
td.lwt-modal, td.mwt-modal, td.rwt-modal,
td.lwt-highlighted, td.mwt-highlighted, td.rwt-highlighted,
td.lwt-overlapped, td.mwt-overlapped, td.rwt-overlapped,
td.lwt-wndcyan, td.mwt-wndcyan, td.rwt-wndcyan {
	font-size: ${fontSizeM};
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
	background: #e6edf9; <%-- #dae4f5/#e1eaf7/e3ecf7 --%>
}
div.z-loading {
	position: absolute; z-index: 79000; background-color: #A8CAF8; 
	white-space: nowrap; border: 1px solid #83B5F7; padding:3px;
}
div.z-loading-indicator {
	color: #102B6D; border:1px solid #83B5F7; background-color: #FFF; 
	white-space: nowrap; padding:6px;
}
.z-loading-icon {
	background: transparent url(${c:encodeURL('~./zk/img/progress2.gif')}) no-repeat center;
	width: 16px; height: 16px;
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
<c:if test="${c:isExplorer()}">
span.vsep-bar {
	background-position: top left; <%-- Bug 2088712 --%>
}
</c:if>
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
	padding: 1px; background: #e0eaf7; border: 1px solid;
	border-color: #f8fbff #aca899 #aca899 #f8fbff;
}
.caption .toolbar, .caption .toolbarbutton {
	background: none; border: 0;
}

.toolbar a, .toolbar a:visited, .toolbar a:hover {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: ${fontSizeS}; font-weight: normal; color: black;
	background: #e0eaf7; border: 1px solid #e0eaf7;
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
	font-size: ${fontSizeM}; font-weight: normal;
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
	font-size: ${fontSizeM}; font-weight: normal; font-family: Tahoma, Garamond, Century, Arial, serif;
}
div.tree-body, div.listbox-body, div.grid-body {<%-- always used. --%>
	background: white; border: 0; overflow: auto; width: 100%;
}
div.listbox-pgi, div.grid-pgi, div.tree-pgi {
	border-top: 1px solid #AAB; overflow: hidden;
}
div.listbox-pgi-t, div.grid-pgi-t, div.tree-pgi-t {
	border-bottom: 1px solid #AAB; overflow: hidden;
}
div.tree-body td, div.listbox-body td, div.tree-foot td, div.listbox-foot td {
	cursor: pointer; padding: 0 2px;
	font-size: ${fontSizeM}; font-weight: normal; overflow: hidden; 
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
	font-size: ${fontSizeM}; font-weight: normal; color: black;
}
td.hbox-sp {
	width: 0.3em; padding: 0; margin: 0;
}
tr.vbox-sp {
	height: 0.3em; padding: 0; margin: 0;
}
tr.item, tr.item a, tr.item a:visited {
	font-size: ${fontSizeM}; font-weight: normal; color: black;
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
	color: #C5CACB !important;
}
a.disd:visited, a.disd:hover { 
	text-decoration: none !important;
	cursor: default !important;;
	border-color: #E0EAF7 !important;
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

<%-- ZK tab. --%>
.tab, .tab a, a.tab {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: ${fontSizeS}; font-weight: normal; color: #300030;
}
.tab a, .tab a:visited, a.tab, a.tab:visited {
	text-decoration: none;
}
.tab a:hover, a.tab:hover {
	text-decoration: underline;
}
.tabsel, .tabsel a, a.tabsel {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: ${fontSizeS}; font-weight: bold; color: #500060;
}
.tabsel a, .tabsel a:visited, a.tabsel, a.tabsel:visited {
	text-decoration: none;
}
.tabsel a:hover, a.tabsel:hover {
	text-decoration: underline;
}

.tabdis, .tabdis a, a.tabdis {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: ${fontSizeS}; font-weight: normal; color: #AAAAAA;
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
	font-size: ${fontSizeS}; font-weight: bold; color: #999999;
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
div.tabpanel-real{
	height:100%;
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
	overflow: visible !important;
	padding: 0 !important;
}
td.slider-bkl {
	background-image: url(${c:encodeURL('~./zul/img/slider/bkl.gif')});
	width: 4px; height: 17px;
	overflow: visible !important;
	padding: 0 !important;
}
td.slider-bkr {
	background-image: url(${c:encodeURL('~./zul/img/slider/bkr.gif')});
	width: 4px; height: 17px;
	overflow: visible !important;
	padding: 0 !important;
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
	overflow: visible !important;
	padding: 0 !important;
}
td.slidersph-bkl {
	background-image: url(${c:encodeURL('~./zul/img/slider/bklsph.gif')});
	width: 4px; height: 20px;
	overflow: visible !important;
	padding: 0 !important;
}
td.slidersph-bkr {
	background-image: url(${c:encodeURL('~./zul/img/slider/bkrsph.gif')});
	width: 4px; height: 20px;
	overflow: visible !important;
	padding: 0 !important;
}

<%-- ZK menu --%>
div.menubar, div.menupopup, div.ctxpopup {
	cursor: pointer; background: #e9effa; padding: 1px;
}
div.menupopup, div.ctxpopup {
	background: #f0f5ff;
}
div.menubar {
	border: 1px solid #919397;
}
div.menupopup, div.ctxpopup {
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
td.menu1 {<%-- menuitem normal (unchecked) --%>
	width: 11px;
}
td.menu1ck {<%-- menuitem checked --%>
	width: 11px; height: 13px;
	background-image: url(${c:encodeURL('~./zul/img/menu/checked.gif')});
	background-repeat: no-repeat;
}
td.menu3ar {<%-- menuitem arrow --%>
	width: 9px; height: 13px;
	background-image: url(${c:encodeURL('~./zul/img/menu/arrow.gif')});
	background-repeat: no-repeat;
}

<%-- Combobox and Datebox --%>
span.combobox, span.datebox, span.bandbox {
	border: 0; padding: 0; margin: 0; white-space: nowrap;
}
span.rbtnbk {<%-- button at the right edge --%>
	background-image: url(${c:encodeURL('~./zul/img/btnbk.gif')}); background-repeat: no-repeat;
	border: 1px solid #7f9db9; border-left: none;
}
div.comboboxpp, div.bandboxpp {<%--sclass + "pp"--%>
	display: block; position: absolute; z-index: 88000;
	background: white; border: 1px solid black; padding: 2px;
	font-size: ${fontSizeS};
}
div.comboboxpp {
	overflow: auto; <%-- if bandboxpp overflow:auto, crop popup if any --%>
}
.comboboxpp td {<%--label--%>
	white-space: nowrap; font-size: ${fontSizeS}; cursor: pointer;
}
.comboboxpp td span {<%--description--%>
	color: #888; font-size: ${fontSizeXS}; padding-left: 6px;
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
	height: 10px;
}
span.progressmeter-img {
	display:-moz-inline-box; display:inline-block;
	background-image: url(${c:encodeURL('~./zk/img/prgmeter.gif')});
	height: 10px; font-size:0;
}

div.paging, div.paging a {
	font-size: ${fontSizeS}; color: #a30; font-weight: bold;
}
div.paging {
	background: white;
}
div.paging span {
	font-size: ${fontSizeS}; color: #555; font-weight: normal;
}
div.paging a, div.paging a:visited {
	color: #00a; font-weight: normal; text-decoration: underline;
}
div.paging a:hover {
	background: #DAE8FF;
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
	font-size: ${fontSizeM}; font-weight: bold; text-align: center;
	white-space: nowrap;
}
table.calmon td, tr.calday td, tr.calday td a, tr.calday td a:visited {
	font-size: ${fontSizeS}; color: #35254F; text-align: center;
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
	font-size: ${fontSizeS}; color: #333; font-weight: bold;
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
	font-size: ${fontSizeM}; font-weight: normal;
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
