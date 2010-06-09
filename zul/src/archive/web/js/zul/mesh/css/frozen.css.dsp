<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
.z-frozen, .z-frozen-body, .z-frozen-inner {
	overflow: hidden;
}
.z-frozen {
	background-image: url(${c:encodeURL('~./zul/img/common/bar-bg.png')});
}
.z-frozen-body {
	float: left;
}
.z-frozen-inner {
	overflow-x: scroll;
	float: right;
}
.z-frozen-inner div {
	height: 100%;
}