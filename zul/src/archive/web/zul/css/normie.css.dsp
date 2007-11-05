<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:include page="~./zul/css/norm.css.dsp"/>

img	{
	hspace: 0; vspace: 0
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
.comboboxpp td span {<%--description--%>
	padding-left: 5px;
}
table.calyear td {
	color: black; <%-- 1735084 --%>
}
span.tree-root-open, span.tree-root-close, span.tree-tee-open, span.tree-tee-close, 
span.tree-last-open, span.tree-last-close, span.tree-tee, span.tree-vbar, span.tree-last, span.tree-spacer,
span.dottree-root-open, span.dottree-root-close, span.dottree-tee-open, span.dottree-tee-close, 
span.dottree-last-open, span.dottree-last-close, span.dottree-tee, span.dottree-vbar, span.dottree-last, span.dottree-spacer {
	height: 18px;
}
<%-- Append New --%>
option {
	font-family: Verdana, Tahoma, Arial, serif;
	font-size: xx-small; font-weight: normal;
}
