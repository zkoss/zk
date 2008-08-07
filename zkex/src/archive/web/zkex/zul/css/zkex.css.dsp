<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%-- Borderlayout --%>
div.layout-container {
    width:100%; height:100%; overflow:hidden; background-color:#C4DCFB;	border:0 none;
	position: relative;	visibility: hidden;
}
div.layout-region {
    position:absolute; border:0 none; overflow:hidden; background-color:white;
}
div.layout-region-normal {
	border:1px solid #9CBDFF; 
}
div.layout-nested {
	border:0 none;
}
div.layout-region-west {
    z-index: 12;
}
div.layout-region-east {
    z-index: 10;
}
div.layout-region-north {
    z-index: 16;
}
div.layout-region-south {
	z-index: 14;
} 
div.layout-split-h {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-h.png')}");
    background-position: left; cursor: e-resize; cursor: col-resize;
}
div.layout-split-v {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-v.png')}");
    background-position: top; cursor: s-resize; cursor: row-resize;
}
div.layout-split-h-ns {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-h-ns.png')}");
    background-position: left; cursor: default;
}
div.layout-split-v-ns {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-v-ns.png')}");
    background-position: top; cursor: default;
}
div.layout-split {
    position: absolute; height: 6px; width: 6px; line-height: 1px; font-size: 1px;
    z-index: 12; background-color: #C4DCFB;
}
div.layout-split-west {
    z-index: 11;
}
div.layout-split-east {
    z-index: 9;
}
div.layout-split-north {
    z-index: 15;
}
div.layout-split-south {
	z-index: 13;
} 
span.layout-split-btn-l:hover, span.layout-split-btn-r:hover, span.layout-split-btn-t:hover ,span.layout-split-btn-b:hover {
	opacity:1;
}

span.layout-split-btn-l, span.layout-split-btn-r, span.layout-split-btn-t ,span.layout-split-btn-b {
	filter:alpha(opacity=50); <%-- IE --%> opacity:0.5; <%-- Moz + FF --%>
	background-repeat: no-repeat; display:-moz-inline-box; vertical-align:top;
	display:inline-block; line-height:1px; font-size:1px;
}
span.layout-split-btn-l {
	width: 6px; min-height: 50px; height: 50px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-l.png')});
}
span.layout-split-btn-r {
	width: 6px; min-height: 50px; height: 50px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-r.png')});
}
span.layout-split-btn-t {
	width: 50px; min-height: 6px; height: 6px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-t.png')});

}
span.layout-split-btn-b {
	width: 50px; min-height: 5px; height: 6px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-b.png')});
}
.layout-title {
	overflow: hidden; zoom: 1; color: #15428b; font: bold 11px tahoma, arial, verdana, sans-serif;
	padding: 5px 3px 4px 5px; border-bottom: 1px solid #99BBE8; line-height: 15px; 
	background:transparent url(${c:encodeURL('~./zul/img/panel/panel-tb.gif')}) repeat-x 0 -1px;
	white-space: nowrap;
}
.layout-tool {
	overflow: hidden; width: 15px; height: 15px; float: right; cursor: pointer;
	background: transparent url(${c:encodeURL('~./zul/img/panel/tool-btn.gif')}) no-repeat;
	margin-left: 2px;
}
.layout-collapse-south {
	background-position: 0 -195px;
}
.layout-collapse-south-over {
	background-position: -15px -195px;
}
.layout-collapse-north {
	background-position: 0 -210px;
}
.layout-collapse-north-over{
	background-position: -15px -210px;
}
.layout-collapse-west {
	background-position: 0 -180px;
}
.layout-collapse-west-over {
	background-position: -15px -180px;
}
.layout-collapse-east {
	background-position: 0 -165px;
}
.layout-collapse-east-over {
	background-position: -15px -165px;
}
.layout-expand-south {
	background-position: 0 -210px;
}
.layout-expand-south-over {
	background-position: -15px -210px;
}
.layout-expand-north {
	background-position: 0 -195px;
}
.layout-expand-north-over {
	background-position: -15px -195px;
}
.layout-expand-west {
	background-position: 0 -165px;
}
.layout-expand-west-over {
	background-position: -15px -165px;
}
.layout-expand-east {
	background-position: 0 -180px;
}
.layout-expand-east-over {
	background-position: -15px -180px;
}
.layout-expand-north, .layout-expand-south {
	float: right; margin: 3px;
}
.layout-expand-east, .layout-expand-west {
	float: none; margin: 3px auto;
}
.layout-collapsed {
	position: absolute; background-color: #D2E0F2; width: 20px; height: 20px;
	border: 1px solid #98C0F4; z-index:20; overflow: hidden;
}
.layout-collapsed-over {
	cursor: pointer; background-color: #D9E8FB;
}
.layout-collapsed-west {
    z-index: 12;
}
.layout-collapsed-east {
    z-index: 10;
}
.layout-collapsed-north {
    z-index: 16;
}
.layout-collapsed-south {
	z-index: 14;
} 
<%-- Column Layout--%>
.z-column-layout {
	visibility: hidden; overflow: hidden; zoom: 1;
}

.z-column-children {
    float: left; padding: 0; margin: 0; overflow: hidden; zoom: 1;
}

.z-column-children-body {
    overflow: hidden;
}

.z-column-children-bwrap {
    overflow: hidden; zoom: 1;
}
.z-column-layout-inner {
    overflow: hidden;
}
<%-- Fisheyebar--%>
.z-fisheye-label {
	font-family: Arial, Helvetica, sans-serif; background-color: #eee; border: 2px solid #666;
	padding: 2px; text-align: center; position: absolute; display: none;
}
.z-fisheye-image {
	border: 0px; position: absolute; width: 100%; height: 100%;
}
.z-fisheye {
	position: absolute; z-index: 2;
}
.z-fisheyebar {
	visibility: hidden;
}
.z-fisheyebar-inner {
	position: relative;
}
<%-- Detail --%>
.z-detail {
	height: 100%; padding: 0!important; overflow: hidden;
	-o-text-overflow: ellipsis; text-overflow: ellipsis; white-space:nowrap;
}
tr.grid td.z-detail-td {
	background: transparent url(${c:encodeURL('~./zul/img/grid/detail-bg.gif')}) repeat-y right;
	vertical-align: top; border-top: none; border-left: 1px solid white;
	border-right: 1px solid #CCC; border-bottom: 1px solid #DDD;
}
.z-detail-img {
	width: 100%; height: 18px; background-position: 4px 2px;
	background-repeat: no-repeat; background-color: transparent; 
}
.z-detail-expanded .z-detail-img {
	background-position: -21px 2px;
}