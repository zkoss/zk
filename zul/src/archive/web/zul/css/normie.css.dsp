<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

html {height:100%}

<%-- paragraphs --%>
p, div, span, label, a, li, dt, dd, input, textarea, pre, body {
	font-family: Verdana, Tahoma, Arial, serif;
	font-size: x-small; font-weight: normal;
}
body {
	height:100%; margin: 0px; padding: 0px 5px;
}
button {
	font-family: Verdana, Tahoma, Arial, serif;
	font-size: xx-small; font-weight: normal;
}
option {
	font-family: Verdana, Tahoma, Arial, serif;
	font-size: xx-small; font-weight: normal;
}
legend {
	font-family: Tahoma, Arial, serif;
	font-size: x-small; font-weight: normal;
}

th {
	font-family: Tahoma, Garamond, Century, Arial, serif;
	font-weight: bold; 
}

thead tr {
	font-family: Tahoma, Garamond, Century, Arial, serif;
	font-weight: bold;
}

img	{border: 0; hspace: 0; vspace: 0}

<%-- DSP --%>
a.gamma {color: #000000; text-decoration: none;}
a.gamma:hover {color: #000000; text-decoration: underline;}
tr.gamma {background: #F4F4F4;}
td.gamma {background: #F4F4F4;}

<%-- ZK --%>
<%-- groupbox caption --%>
.caption input, .caption td {
	font-size: xx-small;
}
.caption td.caption {
	font-size: x-small;
}
.caption button {
	font-size: xx-small; font-size: 93%; font-weight: normal;
	padding-top: 0; padding-bottom: 0; margin-top: 0; margin-bottom: 0;
}
.caption a, .caption a:visited {
	font-size: xx-small; font-weight: normal; color: black; background: none;
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
	margin: 0; padding: 3px; border: 1px solid #aab;
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
	font-size: x-small;
	padding: 4px 0 3px 0; margin: 0 0 2px 0;
	color: white;
}
td.lwt-embedded {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wte-l.gif')}); background-repeat: no-repeat;
	width: 7px;
}
td.mwt-embedded {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wte-m.gif')}); background-repeat: repeat-x;
}
td.rwt-embedded {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wte-r.gif')}); background-repeat: no-repeat;
	width: 7px;
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

div.modal_mask {<%-- don't change --%>
	position: absolute; z-index: 20000;
	top: 0; left: 0; width: 100%; height: 100%;
	opacity: .4; filter: alpha(opacity=40);
	background: transparent !important;
	background: white;
	background-image: url(${c:encodeURL('~./zk/img/xmask.png')}) !important; <%-- Moz... --%>
	background-image: none; background-repeat: repeat;
	display: none;
}

<%-- ZK separator --%>
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
<%-- ZK toolbar and toolbarbutton --%>
.toolbar {
	padding: 1px; background: threedface; border: 1px solid;
	border-color: threedhighlight threedshadow threedshadow threedhighlight;
}
.caption .toolbar, .caption .toolbarbutton {
	background: none; border: 0;
}

.toolbar a, .toolbar a:visited, .toolbar a:hover {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: xx-small; font-weight: normal; color: black;
	background: threedface; border: 1px solid threedface;
	text-decoration: none;
}
.toolbar a:hover {
	border-color: threedhighlight threedshadow threedshadow threedhighlight;
}

.caption .toolbar a, .caption .toolbar a:visited, .caption .toolbar a:hover {
	background: none; border: 0; color: black;
}
.caption .toolbar a:hover {
	text-decoration: underline;
}
.title .toolbar a, .title .toolbar a:visited, .title .toolbar a:hover {
	background: none; border: 0; color: white;
}
.title a:hover {
	text-decoration: underline;
}

<%-- ZK tree, listbox, grid --%>
div.listbox, div.tree, div.grid {<%-- depends sclass --%>
	background: threedface; border: 1px solid #7F9DB9;
}
div.tree-head, div.listbox-head, div.grid-head {<%-- always used. --%>
	background: threedface; border: 0; overflow: hidden; width: 100%; position:relative;
<%-- Bug 1712708:  we have to specify position:relative --%>
}
div.listbox-paging th, div.grid-paging th {
	background: threedface;
}
div.tree-head th, div.listbox-head th, div.grid-head th, div.listbox-paging th, div.grid-paging th {
	overflow: hidden; border: 1px solid;
	border-color: threedhighlight threedshadow threedshadow threedhighlight;
	text-overflow: ellipsis; white-space: nowrap; padding: 2px;
	font-size: x-small; font-weight: normal;
}
div.head-cell-inner {
	font-size: x-small; font-weight: normal; font-family: Tahoma, Garamond, Century, Arial, serif;
}
div.listbox-head th.sort, div.grid-head th.sort, div.listbox-paging th.sort, div.grid-paging th.sort {
	cursor: hand; cursor: pointer; padding-right: 9px;
	background-image: url(${c:encodeURL('~./zul/img/sort/hint.png')});
	background-position: right;
	background-repeat: no-repeat;
}
div.listbox-head th.sort-asc, div.grid-head th.sort-asc, div.listbox-paging th.sort-asc, div.grid-paging th.sort-asc {
	cursor: hand; cursor: pointer; padding-right: 9px;
	background-image: url(${c:encodeURL('~./zul/img/sort/asc.png')});
	background-position: right;
	background-repeat: no-repeat;
}
div.listbox-head th.sort-dsc, div.grid-head th.sort-dsc, div.listbox-paging th.sort-dsc, div.grid-paging th.sort-dsc {
	cursor: hand; cursor: pointer; padding-right: 9px;
	background-image: url(${c:encodeURL('~./zul/img/sort/dsc.png')});
	background-position: right;
	background-repeat: no-repeat;
}

div.tree-body, div.listbox-body, div.grid-body, div.listbox-paging, div.grid-paging {<%-- always used. --%>
	background: window; border: 0; overflow: auto; position: relative; width: 100%;
<%-- Bug 1766244: we have to specify position:relative with overflow:auto --%>
}
div.listbox-paging, div.grid-paging {
	height: 100%;
}
div.listbox-pgi, div.grid-pgi {
	border-top: 1px solid #AAB;
}
div.tree-body td, div.listbox-body td, div.listbox-paging td {
	cursor: hand; cursor: pointer; padding: 0 2px;
	font-size: x-small; font-weight: normal;
}

div.listbox-foot, tbody.listbox-foot, div.grid-foot, tbody.grid-foot, div.tree-foot, tbody.tree-foot {<%-- always used --%>
	background: threedface; border-top: 1px solid threedshadow;
}

div.foot-cell-inner, div.cell-inner, div.head-cell-inner, div.vbox, div.hbox {
	overflow:hidden; border: 0; margin: 0; padding: 0;
}

div.vbox {
	padding-bottom: 0.4em;
}
div.hbox {
	padding-right: 0.6em;
}
div.gc {
	padding: 2px; font-size: x-small; font-weight: normal; color: black;
}
tr.item, tr.item a, tr.item a:visited {
	font-size: x-small; font-weight: normal; color: black;
	text-decoration: none;
}
tr.item a:hover {
	text-decoration: underline;
}

tr.grid td.gc {
	background: white; border-bottom: none; border-left: 1px solid white;
	border-right: 1px solid #CCC; border-top: 1px solid #DDD;
}

tr.odd td.gc, tr.odd {
	background: #EAF2F0;<%--#E8EFEA--%>
}
tr.seld {<%-- item selected --%>
	background: #bbd1ed; border: 1px solid #aac6e9;
}
tr.overd {<%-- item onmouseover --%>
	background: #DAE8FF;
}
tr.overseld {<%-- item selected and onmouseover --%>
	background: #5588bb;
}

<%-- The style for treeitem's paging
span.treeitem-paging {
	background-image: url(${c:encodeURL('~./zul/img/bgbtnbk.gif')}); background-repeat: no-repeat;
	border: 1px solid #7f9db9;
}
--%>

<%-- tree icons --%>
span.tree-root-open {
	width: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/root-open.gif')});
	background-repeat: no-repeat;
	vertical-align:top; display:inline-block;
}
span.tree-root-close {
	width: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/root-close.gif')});
	background-repeat: no-repeat;
	vertical-align:top; display:inline-block;
}
span.tree-tee-open {
	width: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/tee-open.gif')});
	background-repeat: no-repeat;
	vertical-align:top; display:inline-block;
}
span.tree-tee-close {
	width: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/tee-close.gif')});
	background-repeat: no-repeat;
	vertical-align:top; display:inline-block;
}
span.tree-last-open {
	width: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/last-open.gif')});
	background-repeat: no-repeat;
	vertical-align:top; display:inline-block;
}
span.tree-last-close {
	width: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/last-close.gif')});
	background-repeat: no-repeat;
	vertical-align:top; display:inline-block;
}
span.tree-tee {
	width: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/tee.gif')});
	background-repeat: no-repeat;
	vertical-align:top; display:inline-block;
}
span.tree-vbar {
	width: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/vbar.gif')});
	background-repeat: no-repeat;
	vertical-align:top; display:inline-block;
}
span.tree-last {
	width: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/last.gif')});
	background-repeat: no-repeat;
	vertical-align:top; display:inline-block;
}
span.tree-spacer {
	width: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/spacer.gif')});
	background-repeat: no-repeat;
	vertical-align:top; display:inline-block;
}

<%-- ZK tab. --%>
.tab, .tab a, a.tab {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: xx-small; font-weight: normal; color: #300030;
}
.tab a, .tab a:visited, a.tab, a.tab:visited {
	text-decoration: none;
}
.tab a:hover, a.tab:hover {
	text-decoration: underline;
}
.tabsel, .tabsel a, a.tabsel {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: xx-small; font-weight: bold; color: #500060;
}
.tabsel a, .tabsel a:visited, a.tabsel, a.tabsel:visited {
	text-decoration: none;
}
.tabsel a:hover, a.tabsel:hover {
	text-decoration: underline;
}

div.gc-default {<%-- content of 3d groupbox --%>
	border: 1px solid #5C6C7C; padding: 5px;
}
tr.tabpanel td.tabpanel-hr, div.tabpanel-accordion {<%-- horz, accd --%>
	border-left: 1px solid #5C6C7C; border-right: 1px solid #5C6C7C; 
	border-bottom: 1px solid #5C6C7C; padding: 5px;
}
td.tabpanels {<%-- vert --%>
	border-top: 1px solid #5C6C7C; border-right: 1px solid #5C6C7C; 
	border-bottom: 1px solid #5C6C7C; padding: 5px;
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
td.slider-bk {
	background-image: url(${c:encodeURL('~./zul/img/slider/bk.gif')});
}
td.slider-bkl {
	background-image: url(${c:encodeURL('~./zul/img/slider/bkl.gif')});
	width: 4px;
}
td.slider-bkr {
	background-image: url(${c:encodeURL('~./zul/img/slider/bkr.gif')});
	width: 4px;
}

td.slidersph-bk {
	background-image: url(${c:encodeURL('~./zul/img/slider/bksph.gif')});
}
td.slidersph-bkl {
	background-image: url(${c:encodeURL('~./zul/img/slider/bklsph.gif')});
	width: 4px;
}
td.slidersph-bkr {
	background-image: url(${c:encodeURL('~./zul/img/slider/bkrsph.gif')});
	width: 4px;
}

<%-- ZK menu --%>
div.menubar, div.menupopup, div.ctxpopup {
	cursor: hand; cursor: pointer; background: #eff0f3; padding: 1px;
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
div.menubar a, div.menubar a:visited, div.menubar a:hover, div.menupopup a, div.menupopup a:visited, div.menupopup a:hover {
	font-size: xx-small; font-weight: normal; text-overflow: ellipsis;
	border: 1px solid #eff0f3; background: #eff0f3; color: black;
	text-decoration: none;
}
div.menubar a:hover, div.menupopup a:hover {
	background: #bbd1ed; border: 1px solid #aac6e9;
}
div.menupopup hr {
	border: 0; color: #c5c5c5; background: #c3c3c3; height: 1px;
}

<%-- combobox and datebox --%>
span.combobox, span.datebox, span.bandbox {
	border: 0; padding: 0; margin: 0; white-space: nowrap;
}
span.rbtnbk {<%-- button at the right edge --%>
	background-image: url(${c:encodeURL('~./zul/img/btnbk.gif')}); background-repeat: no-repeat;
	border: 1px solid #7f9db9; border-left: none;
}
div.comboboxpp, div.bandboxpp {<%--hardcoded in DSP--%>
	display: block; position: absolute; z-index: 88000;
	background: white; border: 1px solid black; padding: 2px;
	font-size: xx-small;
}
div.comboboxpp {
	overflow: auto; <%-- if bandboxpp overflow:auto, crop popup if any --%>
}
.comboboxpp td {<%--label--%>
	white-space: nowrap; font-size: xx-small; cursor: hand; cursor: pointer; 
}
.comboboxpp td span {<%--description--%>
	color: #888; font-size: xx-small; padding-left: 5px;
}

<%-- ZK error message box --%>
div.errbox {
	margin: 0; padding: 1px; border: 1px outset; cursor: hand; cursor: pointer;
	background: #E8E0D8; position: absolute; z-index: 88000;
}

div.progressmeter {
	border: 1px inset; text-align: left;
}

div.paging, div.paging a {
	font-size: xx-small; color: #a30; font-weight: bold; background: window;
}
div.paging span {
	font-size: xx-small; color: #555; font-weight: normal;
}
div.paging a, div.paging a:visited {
	color: #00a; font-weight: normal; text-decoration: underline;
}
div.paging a:hover {
	background: #DAE8FF;
}

<%--datebox and calendar--%>
div.dateboxpp {<%--hardcoded in DSP--%>
	display: block; position: absolute; z-index: 88000;
	background: white; border: 1px solid black; padding: 2px;
}

table.calendar {
	background: window; border: 1px solid #7F9DB9;
}
table.calyear {
	background: #f0f2f4; border: 1px solid;
	border-color: threedhighlight threedshadow threedshadow threedhighlight;
}
table.calday {
	border: 1px solid #ddd;
}
table.calyear td {
	font-size: x-small; font-weight: bold; text-align: center;
	white-space: nowrap; color: black; <%-- 1735084 --%>
}
table.calmon td, tr.calday td, tr.calday td a, tr.calday td a:visited {
	font-size: xx-small; color: #35254F; text-align: center;
	cursor: hand; cursor: pointer; text-decoration: none;
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
	font-size: xx-small; color: #333; font-weight: bold;
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
