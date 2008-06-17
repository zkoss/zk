<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:include page="~./zul/css/normsaf.css.dsp"/>
<%-- Override norm font--%>
<c:choose>
<c:when  test="${empty c:getProperty('org.zkoss.zul.theme.disableZKPrefix')}">
.zk p, .zk div, .zk span, .zk label, .zk a, .zk input, .zk textarea,
.zk button, .zk legend, .zk input.button, .zk input.file {
	font-size: x-small;
}
<%-- override normsaf font--%>
.zk input.button{
	font-size: x-small; 
}
</c:when>
<c:otherwise>
p, div, span, label, a, input, textarea,
button, legend, input.button, input.file {
	font-size: x-small;
}
<%-- override normsaf font--%>
input.button{
	font-size: x-small; 
}
</c:otherwise>
</c:choose>

.caption input, .caption td {
	font-size: xx-small;
}
.caption td.caption {
	font-size: x-small;
}
.caption button {
	font-size: xx-small;
}
.caption a, .caption a:visited {
	font-size: xx-small;
}
td.lwt-embedded, td.mwt-embedded, td.rwt-embedded,
td.lwt-popup, td.rwt-popup, td.mwt-popup,
td.lwt-modal, td.mwt-modal, td.rwt-modal,
td.lwt-highlighted, td.mwt-highlighted, td.rwt-highlighted,
td.lwt-overlapped, td.mwt-overlapped, td.rwt-overlapped,
td.lwt-wndcyan, td.mwt-wndcyan, td.rwt-wndcyan {
	font-size: x-small;
}
.toolbar a, .toolbar a:visited, .toolbar a:hover {
	font-size: xx-small;
}
div.tree-head th, div.listbox-head th, div.grid-head th {
	font-size: x-small;
}
div.head-cell-inner {
	font-size: x-small;
}
div.tree-body td, div.listbox-body td {
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
.tabdis, .tabdis a, a.tabdis {
	font-size: xx-small;
}
.tabdissel, .tabdissel a, a.tabdissel {
	font-size: xx-small;
}
.comboboxpp td {
	font-size: xx-small;
}
.comboboxpp td span {
	font-size: xx-small;
}
div.paging, div.paging a {
	font-size: xx-small;
}
div.paging span {
	font-size: xx-small;
}
table.calyear td {
	font-size: x-small;
}
table.calmon td, tr.calday td, tr.calday td a, tr.calday td a:visited {
	font-size: xx-small;
}
tr.caldow td {
	font-size: xx-small;
}