<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:include page="~./zul/css/normsaf.css.dsp"/>

p, div, span, label, a, li, dt, dd, input, textarea, pre, body,
button, input.button, input.file {
	font-size: medium;
}

legend {
	font-size: medium;
}
.caption input, .caption td {
	font-size: small;
}
.caption td.caption {
	font-size: medium;
}
.caption button {
	font-size: x-small;
}
.caption a, .caption a:visited {
	font-size: small;
}
td.lwt-embedded, td.mwt-embedded, td.rwt-embedded,
td.lwt-popup, td.rwt-popup, td.mwt-popup,
td.lwt-modal, td.mwt-modal, td.rwt-modal,
td.lwt-highlighted, td.mwt-highlighted, td.rwt-highlighted,
td.lwt-overlapped, td.mwt-overlapped, td.rwt-overlapped,
td.lwt-wndcyan, td.mwt-wndcyan, td.rwt-wndcyan {
	font-size: medium;
}
.toolbar a, .toolbar a:visited, .toolbar a:hover {
	font-size: small;
}
div.tree-head th, div.listbox-head th, div.grid-head th, div.listbox-paging th, div.grid-paging th {
	font-size: medium;
}
div.head-cell-inner {
	font-size: medium;
}
div.tree-body td, div.listbox-body td, div.listbox-paging td {
	font-size: medium;
}
div.gc {
	font-size: medium;
}
tr.item, tr.item a, tr.item a:visited {
	font-size: medium;
}
.tab, .tab a, a.tab {
	font-size: small;
}
.tabsel, .tabsel a, a.tabsel {
	font-size: small;
}
.tabdis, .tabdis a, a.tabdis {
	font-size: small;
}
.tabdissel, .tabdissel a, a.tabdissel {
	font-size: small;
}
.comboboxpp td {
	font-size: small;
}
.comboboxpp td span {
	font-size: x-small;
}
div.paging, div.paging a {
	font-size: small;
}
div.paging span {
	font-size: small;
}
table.calyear td {
	font-size: medium;
}
table.calmon td, tr.calday td, tr.calday td a, tr.calday td a:visited {
	font-size: small;
}
tr.caldow td {
	font-size: small;
}
<%-- override normsaf font--%>
input.button{
	font-size: medium; 
}