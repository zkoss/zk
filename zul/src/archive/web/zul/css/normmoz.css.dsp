<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:include page="~./zul/css/norm.css.dsp"/>
<%-- Override or Append Exist--%>
span.tree-root-open, span.tree-root-close, span.tree-tee-open, span.tree-tee-close, 
span.tree-last-open, span.tree-last-close, span.tree-tee, span.tree-vbar, span.tree-last, span.tree-spacer {
	min-height: 18px;
	display:-moz-inline-box;
}

span.rbtnbk {<%-- button at the right edge --%>
	margin: 0; padding: 0;
}

<%-- Append New --%>