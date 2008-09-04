<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- Horizontal style --%>
.z-slider-hor {
    padding-left: 7px; zoom:1;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-bg.png')}) no-repeat 0 -22px;
}
.z-slider-hor-inner {
    position: relative; left: 0; top: 0; overflow: visible; zoom: 1;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-bg.png')}) repeat-x 0 0;
    height: 22px;
}
.z-slider-hor-end {
    padding-right: 7px; zoom: 1;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-bg.png')}) no-repeat right -44px;
}
.z-slider-hor-btn {
    width: 14px; height: 15px; position: absolute; left: 0; top: 3px;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-thumb.png')}) no-repeat 0 0;
}
.z-slider-hor-btn-over {
    background-position: -14px -15px;
}
.z-slider-hor-btn-drag {
    background-position: -28px -30px;
}
.z-slider-hor-focus {
	position: absolute;	left: 0; top: 0; width: 1px; height: 1px; line-height: 1px;
    font-size: 1px; -moz-outline: 0 none; outline: 0 none; -moz-user-select: normal;
    -khtml-user-select: normal;  
}
<%-- Vertical style --%>
.z-slider-ver {
    padding-top: 7px; zoom:1;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.png')}) no-repeat -44px 0;
    width: 22px;
}
.z-slider-ver-inner {
    position: relative; left: 0; top: 0; overflow: visible; zoom: 1;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.png')}) repeat-y 0 0;
}
.z-slider-ver-end {
    padding-bottom: 7px; zoom: 1;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.png')}) no-repeat -22px bottom;
}
.z-slider-ver-btn {
    width: 15px; height: 14px; position: absolute; left: 3px; bottom: 0;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-v-thumb.png')}) no-repeat 0 0;
}
.z-slider-ver-btn-over {
    background-position:  -15px -14px;
}
.z-slider-ver-btn-drag {
    background-position:  -30px -28px;
}
.z-slider-ver-focus {
	position: absolute;	left: 0; top: 0; width: 1px; height: 1px; line-height: 1px;
    font-size: 1px; -moz-outline: 0 none; outline: 0 none; -moz-user-select: normal;
    -khtml-user-select: normal;  
}
<%-- Scale style --%>
.z-slider-scale-tick {
    background-image:url(${c: encodeURL('~./zul/img/slider2/ticks.gif')});
	padding-top:6px;
	width:214px;
}
.z-slider-scale {
    padding-left: 7px; zoom:1;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-scale-bg.png')}) no-repeat 0 -22px;
}
.z-slider-scale-inner {
    position: relative; left: 0; top: 0; overflow: visible; zoom: 1;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-scale-bg.png')}) repeat-x 0 0;
    height: 22px;
}
.z-slider-scale-end {
    padding-right: 7px; zoom: 1;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-scale-bg.png')}) no-repeat right -44px;
}
.z-slider-scale-btn {
    width: 14px; height: 15px; position: absolute; left: 0; top: 3px;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-scale-thumb.gif')}) no-repeat 0 0;
}
.z-slider-scale-btn-over {
    background-position: -14px -15px;
}
.z-slider-scale-btn-drag {
    background-position: -28px -30px;
}
.z-slider-scale-focus {
	position: absolute;	left: 0; top: 0; width: 1px; height: 1px; line-height: 1px;
    font-size: 1px; -moz-outline: 0 none; outline: 0 none; -moz-user-select: normal;
    -khtml-user-select: normal;  
}
<%-- Sphere style --%>
.z-slider-sphere-hor {
    padding-left: 7px; zoom:1;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-bg.png')}) no-repeat 0 -22px;
}
.z-slider-sphere-hor-inner {
    position: relative; left: 0; top: 0; overflow: visible; zoom: 1;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-bg.png')}) repeat-x 0 0;
    height: 22px;
}
.z-slider-sphere-hor-end {
    padding-right: 7px; zoom: 1;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-bg.png')}) no-repeat right -44px;
}
.z-slider-sphere-hor-btn {
    width: 14px; height: 15px; position: absolute; left: 0; top: 3px;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-thumb_circle.png')}) no-repeat 0 0;
}
.z-slider-sphere-hor-btn-over {
    background-position: -14px -15px;
}
.z-slider-sphere-hor-btn-drag {
    background-position: -28px -30px;
}
.z-slider-sphere-hor-focus {
	position: absolute;	left: 0; top: 0; width: 1px; height: 1px; line-height: 1px;
    font-size: 1px; -moz-outline: 0 none; outline: 0 none; -moz-user-select: normal;
    -khtml-user-select: normal;  
}
<%-- Sphere verticle style --%>
.z-slider-sphere-ver {
    padding-top: 7px; zoom:1;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.png')}) no-repeat -44px 0;
    width: 22px;
}
.z-slider-sphere-ver-inner {
    position: relative; left: 0; top: 0; overflow: visible; zoom: 1;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.png')}) repeat-y 0 0;
}
.z-slider-sphere-ver-end {
    padding-bottom: 7px; zoom: 1;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.png')}) no-repeat -22px bottom;
}
.z-slider-sphere-ver-btn {
    width: 15px; height: 14px; position: absolute; left: 3px; bottom: 0;
    background: transparent url(${c: encodeURL('~./zul/img/slider2/slider-v-thumb_circle.png')}) no-repeat 1px -1px;
}
.z-slider-sphere-ver-btn-over {
    background-position:  -13px -16px;
}
.z-slider-sphere-ver-btn-drag {
    background-position:  -27px -31px;
}
.z-slider-sphere-ver-focus {
	position: absolute;	left: 0; top: 0; width: 1px; height: 1px; line-height: 1px;
    font-size: 1px; -moz-outline: 0 none; outline: 0 none; -moz-user-select: normal;
    -khtml-user-select: normal;  
}

