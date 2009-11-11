<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<c:choose>
	<c:when test="${empty c:property('org.zkoss.zul.Separator.spaceWithMargin')}">
	<%-- 3.0.4 and later --%>
.z-separator-hor, .z-separator-hor-bar {
	height: 7px; overflow: hidden; line-height: 0pt; font-size: 0pt;
}

.z-separator-ver, .z-separator-ver-bar {
	display:-moz-inline-box; display: inline-block;
	width: 10px; overflow: hidden;
}
.z-separator-hor-bar {
	background-image: url(${c:encodeURL('~./img/dot.gif')});
	background-position: center left; background-repeat: repeat-x;
}
.z-separator-ver-bar {
	background-image: url(${c:encodeURL('~./img/dot.gif')});
	background-position: top center; background-repeat: repeat-y;
}
	</c:when>
	<c:otherwise>
	<%-- backward compatible with 3.0.3 and earlier --%>
.z-separator-hor, .z-separator-hor-bar {
	display: block; width: 100%; padding: 0; margin: 2pt 0; font-size: 0;
}
.z-separator-ver, .z-separator-ver-bar {
	display: inline; margin: 0 1pt; padding: 0;
}
.z-separator-hor-bar {
	border-top: 1px solid #888;
}
.z-separator-ver-bar {
	border-left: 1px solid #666; margin-left: 2pt;
}
	</c:otherwise>
</c:choose>