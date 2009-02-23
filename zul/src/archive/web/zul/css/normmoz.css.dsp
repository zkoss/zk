<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:include page="~./zul/css/norm.css.dsp"/>
.z-messagebox-btn {
	width: 45pt;
}
<%-- combo.css.dsp --%>
span.z-combobox-btn, span.z-datebox-btn, span.z-bandbox-btn, span.z-timebox-btn,
	span.z-spinner-btn {<%-- button at the right edge --%>
	margin: 0; padding: 0;
}
<%-- tree.css.dsp, grid.css.dsp, and listbox.css.dsp --%>
.z-word-wrap div.z-tree-cell-cnt, .z-word-wrap div.z-tree-footer-cnt, <%-- Tree --%>
	.z-word-wrap div.z-tree-col-cnt,
.z-word-wrap div.z-row-cnt, <%-- Grid --%>
.z-word-wrap div.z-group-cnt,
.z-word-wrap div.z-group-foot-cnt,
.z-word-wrap div.z-footer-cnt, .z-word-wrap div.z-column-cnt,
.z-word-wrap div.z-list-cell-cnt, .z-word-wrap div.z-list-footer-cnt, <%-- Listbox --%>
	.z-word-wrap div.z-list-header-cnt {
	overflow: hidden;
	-moz-binding: url(${c:encodeURL('~./zk/wordwrap.xml#wordwrap')});
}
span.z-word-wrap {<%-- label use only --%>
	display: block;
}
<%-- box.css.dsp --%>
div.z-splitter-hor, div.z-splitter-ver, div.z-splitter-os-hor, div.z-splitter-os-ver {
	-moz-user-select: none;
}
