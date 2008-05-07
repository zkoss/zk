<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:include page="~./zul/css/norm.css.dsp"/>

span.rbtnbk {<%-- button at the right edge --%>
	margin: 0; padding: 0;
}
.word-wrap, .word-wrap div.cell-inner, .word-wrap div.foot-cell-inner, .word-wrap div.head-cell-inner {
	overflow: hidden;
	-moz-binding: url(${c:encodeURL('~./zk/wordwrap.xml#wordwrap')});
}
span.word-wrap {<%-- label use only --%>
	display: block;
}
<%-- Append New --%>
div.splitter-h, div.splitter-v, div.splitter-os-h, div.splitter-os-v {
	-moz-user-select: none;
}