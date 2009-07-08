<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- Horizontal style --%>
.z-slider-sphere-hor,
.z-slider-scale,
.z-slider-hor {
	background:transparent no-repeat scroll 0 top;
	background-image:url(${c:encodeURL('~./zul/img/slider/slider-bg.png')});
	font-size:0;
	height:22px;
	line-height:0;
	margin-right:7px;
	zoom:1;
}
.z-slider-sphere-hor-center,
.z-slider-scale-center,
.z-slider-hor-center {
    background:transparent no-repeat scroll right -22px;
	background-image:url(${c:encodeURL('~./zul/img/slider/slider-bg.png')});
	font-size:0;
	height:22px;
	line-height:0;
	margin-right:-7px;
	position:relative;
	zoom:1;
}
.z-slider-sphere-hor-btn,
.z-slider-scale-btn,
.z-slider-hor-btn {
    width: 14px; height: 15px; position: absolute; left: 0; top: 3px;
    background-color : transparent;
	background-image : url(${c:encodeURL('~./zul/img/slider/slider-square.png')});
	background-repeat : no-repeat;
	background-position : 0 0;
}
.z-slider-scale-btn {
	background-image : url(${c:encodeURL('~./zul/img/slider/slider-scale.gif')});
}

<%-- Scale style --%>
.z-slider-scale-tick {
    background-image:url(${c:encodeURL('~./zul/img/slider/ticks.gif')});
	padding-top:6px;
	width:214px;
}

<%-- Vertical style --%>
.z-slider-sphere-ver,
.z-slider-ver {
	background:transparent no-repeat scroll left 0;
	background-image:url(${c:encodeURL('~./zul/img/slider/slider-bg-ver.png')});
	font-size:0;
	width:22px;
	line-height:0;
	margin-bottom:7px;
	zoom:1;
}

.z-slider-sphere-ver-center,
.z-slider-ver-center {
    background:transparent no-repeat scroll -22px bottom;
	background-image:url(${c:encodeURL('~./zul/img/slider/slider-bg-ver.png')});
	font-size:0;
	width:22px;
	line-height:0;
	margin-bottom:-7px;
	position:relative;
	zoom:1;
}

.z-slider-sphere-ver-btn,
.z-slider-ver-btn {
    width: 15px; height: 15px; position: absolute; left: 4px; bottom: 0;
    background-color : transparent;
	background-image : url(${c:encodeURL('~./zul/img/slider/slider-v-square.png')});
	background-repeat : no-repeat;
	background-position : 0 0;
}
.z-slider-sphere-ver-btn
 {
 	height: 14px;
 	left: 3px;
}
.z-slider-sphere-hor-btn {
	background-image : url(${c:encodeURL('~./zul/img/slider/slider-circle.png')});
}

.z-slider-sphere-ver-btn {
	background-image : url(${c:encodeURL('~./zul/img/slider/slider-v-circle.png')});
}
<%-- all button style --%>
.z-slider-hor-btn-over,
.z-slider-sphere-hor-btn-over,
.z-slider-sphere-ver-btn-over,
.z-slider-ver-btn-over,
.z-slider-scale-btn-over {
    background-position: -15px 0px;
}
.z-slider-hor-btn-drag,
.z-slider-sphere-hor-btn-drag,
.z-slider-sphere-ver-btn-drag,
.z-slider-ver-btn-drag,
.z-slider-scale-btn-drag {
    background-position: -30px 0px;
}

.z-slider-pp {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; font-weight: normal;
}

<%-- IE 6 GIF  --%>
<c:if test="${c:browser('ie6-')}">
.z-slider-sphere-hor,
.z-slider-scale,
.z-slider-hor,
.z-slider-sphere-hor-center,
.z-slider-scale-center,
.z-slider-hor-center {
	background-image:url(${c:encodeURL('~./zul/img/slider/slider-bg.gif')});
}
.z-slider-sphere-hor-btn,
.z-slider-scale-btn,
.z-slider-hor-btn {
    background-image : url(${c:encodeURL('~./zul/img/slider/slider-square.gif')});
}
.z-slider-scale-btn {
	background-image : url(${c:encodeURL('~./zul/img/slider/slider-scale.gif')});
}
.z-slider-sphere-ver,
.z-slider-ver,
.z-slider-sphere-ver-center,
.z-slider-ver-center {
	background-image:url(${c:encodeURL('~./zul/img/slider/slider-bg-ver.gif')});
}

.z-slider-sphere-ver-btn,
.z-slider-ver-btn {
	background-image : url(${c:encodeURL('~./zul/img/slider/slider-v-square.gif')});
}
.z-slider-sphere-hor-btn {
	background-image : url(${c:encodeURL('~./zul/img/slider/slider-circle.gif')});
}
.z-slider-sphere-ver-btn {
	background-image : url(${c:encodeURL('~./zul/img/slider/slider-v-circle.gif')});
}
</c:if>
