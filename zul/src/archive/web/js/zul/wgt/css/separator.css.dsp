<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-separator-hor, .z-separator-hor-bar {
	height: 7px; overflow: hidden; line-height: 0; font-size: 0;
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
