<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:include page="~./zul/css/norm.css.dsp"/>
<c:choose>
<c:when  test="${!empty c:getProperty('org.zkoss.zul.theme.enableZKPrefix')}">
.zk img	{
	hspace: 0; vspace: 0
}
.zk option {
	font-family: Verdana, Tahoma, Arial, serif;
	font-size: ${fontSizeXS}; font-weight: normal;
}
</c:when>
<c:otherwise>
img	{
	hspace: 0; vspace: 0
}
option {
	font-family: Verdana, Tahoma, Arial, serif;
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

<%-- tree.css.dsp, listbox.css.dsp, and grid.css.dsp --%>
<c:if test="${c:isExplorer() && !c:isExplorer7()}">
div.z-listbox, div.z-tree, div.z-dottree, div.z-grid {
	position:relative; <%-- Bug 1914215 and Bug 1914054 --%>
}
</c:if>
div.z-tree-header, div.z-listbox-header, div.z-grid-header, div.z-tree-footer, div.z-listbox-footer,
	div.z-grid-footer {<%-- always used. --%>
	position:relative;
	<%-- Bug 1712708 and 1926094:  we have to specify position:relative --%>
}
div.z-tree-header th, div.z-listbox-header th, div.z-grid-header th {
	text-overflow: ellipsis;
}
div.z-tree-col-content, div.z-list-header-content, div.z-column-content, .z-auxheader-content {
	white-space: nowrap;
	<%-- Bug #1839960  --%>
}
div.z-footer-content, div.z-row-content, div.z-group-content, div.z-group-foot-content, div.z-column-content,
div.z-tree-footer-content, div.z-tree-cell-content, div.z-tree-col-content, .z-auxheader-content,
div.z-list-footer-content, div.z-list-cell-content, div.z-list-header-content {
	position: relative;
	<%-- Bug #1825896  --%>
}
div.z-row-content, div.z-group-content, div.z-group-foot-content, div.z-tree-cell-content, div.z-list-cell-content {
	width: 100%;
}
div.z-tree-body, div.z-dottree-body, div.z-listbox-body, div.z-grid-body {<%-- always used. --%>
	position: relative;
	<%-- Bug 1766244: we have to specify position:relative with overflow:auto --%>
}
tr.z-grid-faker, tr.z-listbox-faker, tr.z-tree-faker, tr.z-dottree-faker { 
	position: absolute; top: -1000px; left: -1000px;<%-- fixed a white line for IE --%> 
}
span.z-tree-root-open, span.z-tree-root-close, span.z-tree-tee-open, span.z-tree-tee-close, 
span.z-tree-last-open, span.z-tree-last-close, span.z-tree-tee, span.z-tree-vbar, span.z-tree-last, span.z-tree-spacer,
span.z-dottree-root-open, span.z-dottree-root-close, span.z-dottree-tee-open, span.z-dottree-tee-close, 
span.z-dottree-last-open, span.z-dottree-last-close, span.z-dottree-tee, span.z-dottree-vbar, span.z-dottree-last, span.z-dottree-spacer {
	height: 18px;
}

<%-- combo.css.dsp --%>
.z-combobox-pp td span {<%--description--%>
	padding-left: 5px;
}
.z-calendar-calyear td, .z-datebox-calyear td {
	color: black; <%-- 1735084 --%>
}

<%-- Append New --%>
<%-- Shadow --%>
.z-ie-shadow { <%-- IE can use the "progid:DXImageTransform.Microsoft.Blur" function --%>
	display: none; position: absolute; overflow: hidden; left: 0; top: 0; background: #777; zoom: 1;
}
<c:if test="${c:isExplorer7()}">
.z-panel-tm {
	overflow: visible;
}
</c:if>
.z-panel-bbar {
	position: relative;
}
<%-- tabbox.css.dsp --%>
div.tabpanel-accordion {
	zoom:1;
}