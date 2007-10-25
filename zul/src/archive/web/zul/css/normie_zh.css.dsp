<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:include page="~./zul/css/norm.css.dsp"/>
<%-- Override or Append Exist--%>


img	{
	hspace: 0; vspace: 0
}
div.modal_mask {<%-- don't change --%>
	opacity: .4; filter: alpha(opacity=40);
}
div.tree-head, div.listbox-head, div.grid-head {<%-- always used. --%>
	position:relative;
	<%-- Bug 1712708:  we have to specify position:relative --%>
}

div.tree-head th, div.listbox-head th, div.grid-head th, div.listbox-paging th, div.grid-paging th {
	text-overflow: ellipsis;
}
div.tree-body, div.listbox-body, div.grid-body, div.listbox-paging, div.grid-paging {<%-- always used. --%>
	position: relative;
	<%-- Bug 1766244: we have to specify position:relative with overflow:auto --%>
}
div.menubar a, div.menubar a:visited, div.menubar a:hover, div.menupopup a, div.menupopup a:visited, div.menupopup a:hover {
	text-overflow: ellipsis;
}
table.calyear td {
	font-size: x-small;
	color: black; <%-- 1735084 --%>
}


<%-- Append New --%>
option {
	font-family: Verdana, Tahoma, Arial, serif;
	font-size: x-small; font-weight: normal;
}
