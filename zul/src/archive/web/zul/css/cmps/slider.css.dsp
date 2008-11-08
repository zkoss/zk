<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- Horizontal style --%>
.z-slider-hor {
    padding-left: 7px; zoom:1;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-bg.png')});
	background-repeat : no-repeat;
	background-position : 0 -22px;
}
.z-slider-hor-center {
    position: relative; left: 0; top: 0; overflow: visible; zoom: 1;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-bg.png')});
	background-repeat :  repeat-x;
	background-position : 0 0;
    height: 22px;
}
.z-slider-hor-end {
    padding-right: 7px; zoom: 1;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-bg.png')});
	background-repeat : no-repeat;
	background-position : right -44px;;
}
.z-slider-hor-btn {
    width: 14px; height: 15px; position: absolute; left: 0; top: 3px;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-thumb.png')});
	background-repeat : no-repeat;
	background-position : 0 0;
}
.z-slider-hor-btn-over {
    background-position: -15px 0px;
}
.z-slider-hor-btn-drag {
    background-position: -30px 0px;
}
<%-- Vertical style --%>
.z-slider-ver {
    padding-top: 7px; zoom:1;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.png')});
	background-repeat : no-repeat;
	background-position : -44px 0;
    width: 22px;
}
.z-slider-ver-center {
    position: relative; left: 0; top: 0; overflow: visible; zoom: 1;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.png')});
	background-repeat : repeat-y;
	background-position : 0 0;
}
.z-slider-ver-end {
    padding-bottom: 7px; zoom: 1;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.png')});
	background-repeat : no-repeat;
	background-position : -22px bottom;
}
.z-slider-ver-btn {
    width: 15px; height: 14px; position: absolute; left: 3px; bottom: 0;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-v-thumb.png')});
	background-repeat : no-repeat;
	background-position : 0 0;
}
.z-slider-ver-btn-over {
    background-position: -15px 0px;
}
.z-slider-ver-btn-drag {
    background-position: -30px 0px;
}
<%-- Scale style --%>
.z-slider-scale-tick {
    background-image:url(${c: encodeURL('~./zul/img/slider2/ticks.gif')});
	padding-top:6px;
	width:214px;
}
.z-slider-scale {
    padding-left: 7px; zoom:1;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-bg.png')});
	background-repeat : no-repeat;
	background-position : 0 -22px;
}
.z-slider-scale-center {
    position: relative; left: 0; top: 0; overflow: visible; zoom: 1;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-bg.png')});
	background-repeat : repeat-x ;
	background-position : 0 0;
    height: 22px;
}
.z-slider-scale-end {
    padding-right: 7px; zoom: 1;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-bg.png')});
	background-repeat : no-repeat;
	background-position : right -44px;
}
.z-slider-scale-btn {
    width: 14px; height: 15px; position: absolute; left: 0; top: 3px;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-scale-thumb.gif')});
	background-repeat : no-repeat;
	background-position : 0 0;
}
.z-slider-scale-btn-over {
    background-position: -15px 0px;
}
.z-slider-scale-btn-drag {
    background-position: -30px 0px;
}
<%-- Sphere style --%>
.z-slider-sphere-hor {
    padding-left: 7px; zoom:1;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-bg.png')});
	background-repeat : no-repeat;
	background-position : 0 -22px;
}
.z-slider-sphere-hor-center {
    position: relative; left: 0; top: 0; overflow: visible; zoom: 1;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-bg.png')});
	background-repeat : repeat-x;
	background-position : 0 0;
    height: 22px;
}
.z-slider-sphere-hor-end {
    padding-right: 7px; zoom: 1;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-bg.png')});
	background-repeat : no-repeat;
	background-position : right -44px;
}
.z-slider-sphere-hor-btn {
    width: 14px; height: 15px; position: absolute; left: 0; top: 3px;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-thumb_circle.png')});
	background-repeat : no-repeat;
	background-position : 0 0;
}
.z-slider-sphere-hor-btn-over {
    background-position: -15px 0px;
}
.z-slider-sphere-hor-btn-drag {
    background-position: -30px 0px;
}
<%-- Sphere verticle style --%>
.z-slider-sphere-ver {
    padding-top: 7px; zoom:1;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.png')});
	background-repeat : no-repeat;
	background-position : -44px 0;
    width: 22px;
}
.z-slider-sphere-ver-center {
    position: relative; left: 0; top: 0; overflow: visible; zoom: 1;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.png')});
	background-repeat : repeat-y;
	background-position : 0 0;
}
.z-slider-sphere-ver-end {
    padding-bottom: 7px; zoom: 1;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.png')});
	background-repeat : no-repeat;
	background-position : -22px bottom;
}
.z-slider-sphere-ver-btn {
    width: 15px; height: 15px; position: absolute; left: 3px; bottom: 0;
    background-color : transparent;
	background-image : url(${c: encodeURL('~./zul/img/slider2/slider-v-thumb_circle.png')});
	background-repeat : no-repeat;
	background-position : 0px 0px;
}
.z-slider-sphere-ver-btn-over {
    background-position: -15px 0px;
}
.z-slider-sphere-ver-btn-drag {
    background-position: -30px 0px;
}

