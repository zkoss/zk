<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
.z-auxheader-cnt {
	border: 0;
	margin: 0;
	padding: 0;
	overflow: hidden;
	color: #636363;
	font-weight: bold;
	font-size: ${fontSizeM};
	font-family: ${fontFamilyC};
	<c:if test="${zk.ie > 0}">
		white-space: nowrap; <%-- Bug #1839960  --%>
		position: relative; <%-- Bug #1825896  --%>
	</c:if>
	<c:if test="${zk.ie >= 8}">
		text-align: left;
	</c:if>
}
.z-word-wrap .z-auxheader-cnt {
	word-wrap: break-word;
}
