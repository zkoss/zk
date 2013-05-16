<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
.z-hlayout, .z-vlayout {
	overflow: hidden;
}
.z-hlayout {
	white-space: nowrap;
}
.z-hlayout-inner {
	display:-moz-inline-box;
	display: inline-block;
	position: relative;
	vertical-align: top;
}
.z-valign-bottom > .z-hlayout-inner {
	vertical-align: bottom;
}
.z-valign-top > .z-hlayout-inner {
	vertical-align: top;
}
.z-valign-middle > .z-hlayout-inner {
	vertical-align: middle;
}
.z-vlayout-inner {
	position: relative;
}
