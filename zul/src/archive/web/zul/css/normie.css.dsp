<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:include page="~./zul/css/norm.css.dsp"/>
<%-- Override or Append Exist--%>

p, div, span, label, a, li, dt, dd, input, textarea, pre, body {
	font-size: x-small;
}
button {
	font-size: xx-small;
}
legend {
	font-size: x-small;
}
img	{
	hspace: 0; vspace: 0
}
.caption input, .caption td {
	font-size: xx-small;
}
.caption button {
	font-size: 93%;
}
.caption a, .caption a:visited {
	font-size: xx-small; 
}
td.lwt-wndcyan, td.mwt-wndcyan, td.rwt-wndcyan {
	font-size: x-small;
}
.toolbar a, .toolbar a:visited, .toolbar a:hover {
	font-size: xx-small;
}
div.tree-head, div.listbox-head, div.grid-head {<%-- always used. --%>
	position:relative;
	<%-- Bug 1712708:  we have to specify position:relative --%>
}
div.tree-head th, div.listbox-head th, div.grid-head th, div.listbox-paging th, div.grid-paging th {
	text-overflow: ellipsis;
	font-size: x-small;
}
div.head-cell-inner {
	font-size: x-small;
}
div.tree-body, div.listbox-body, div.grid-body, div.listbox-paging, div.grid-paging {<%-- always used. --%>
	position: relative;
	<%-- Bug 1766244: we have to specify position:relative with overflow:auto --%>
}
div.tree-body td, div.listbox-body td, div.listbox-paging td {
	font-size: x-small;
}
div.gc {
	font-size: x-small;
}
tr.item, tr.item a, tr.item a:visited {
	font-size: x-small;
}

.tab, .tab a, a.tab {
	font-size: xx-small;
}
.tabsel, .tabsel a, a.tabsel {
	font-size: xx-small;
}
div.menubar a, div.menubar a:visited, div.menubar a:hover, div.menupopup a, div.menupopup a:visited, div.menupopup a:hover {
	font-size: xx-small;
}
div.comboboxpp, div.bandboxpp {
	font-size: xx-small;
}
.comboboxpp td {<%--label--%>
	font-size: xx-small;
}
.comboboxpp td span {<%--description--%>
	padding-left: 5px;
}
div.paging, div.paging a {
	font-size: xx-small;
}
div.paging span {
	font-size: xx-small;
}
table.calyear {
	background: #f0f2f4;
}
table.calyear td {
	font-size: x-small;
	color: black; <%-- 1735084 --%>
}
table.calmon td, tr.calday td, tr.calday td a, tr.calday td a:visited {
	font-size: xx-small;
}
tr.caldow td {
	font-size: xx-small;
}

div.modal_mask {<%-- don't change --%>
	opacity: .4; filter: alpha(opacity=40);
}

span.tree-root-open, span.tree-root-close, span.tree-tee-open, span.tree-tee-close, 
span.tree-last-open, span.tree-last-close, span.tree-tee, span.tree-vbar, span.tree-last, span.tree-spacer {
	height: 18px;
}

<%-- Append New --%>
option {
	font-family: Verdana, Tahoma, Arial, serif;
	font-size: xx-small; font-weight: normal;
}
