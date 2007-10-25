<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:include page="~./zul/css/norm.css.dsp"/>

span.tree-root-open, span.tree-root-close, span.tree-tee-open, span.tree-tee-close, 
span.tree-last-open, span.tree-last-close, span.tree-tee, span.tree-vbar, span.tree-last, span.tree-spacer,
span.dottree-root-open, span.dottree-root-close, span.dottree-tee-open, span.dottree-tee-close, 
span.dottree-last-open, span.dottree-last-close, span.dottree-tee, span.dottree-vbar, span.dottree-last, span.dottree-spacer {
	display:-moz-inline-box;
}

span.rbtnbk {<%-- button at the right edge --%>
	margin: 0; padding: 0;
}

<%-- Append New --%>