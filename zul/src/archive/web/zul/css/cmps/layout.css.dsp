<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%-- Borderlayout --%>
.z-border-layout {
    width:100%; height:100%; overflow:hidden; background-color:#CDE6F5;	border:0 none;
	position: relative;	visibility: hidden;
}
.z-border-layout-tool {
	overflow: hidden; width: 15px; height: 15px; float: right; cursor: pointer;
	background-color : transparent;
	background-image : url(${c:encodeURL('~./zul/img/panel/tool-btn.gif')});
	background-position : 0 0;
	background-repeat : no-repeat;
	margin-left: 2px;
}
<%-- LayoutRegion --%>
.z-west-nested, .z-center-nested, .z-east-nested, .z-north-nested, .z-south-nested {
	border:0 none;
}
.z-west, .z-center, .z-east, .z-north, .z-south {
	border:1px solid #B1CBD5; position:absolute; overflow:hidden; background-color:white;
}
.z-west-noborder, .z-center-noborder, .z-east-noborder,
	.z-north-noborder, .z-south-noborder {
	border:0 none;
}
.z-west {
    z-index: 12;
}
.z-center {
	z-index: 8;
}
.z-east {
    z-index: 10;
}
.z-north {
    z-index: 16;
}
.z-south {
	z-index: 14;
} 
.z-east-split, .z-west-split {
    position: absolute; height: 6px; width: 6px; line-height: 1px; font-size: 1px;
    z-index: 12; background-color: #C4DCFB;
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-h.png')}");
    background-position: left; cursor: e-resize; cursor: col-resize;
}
.z-north-split, .z-south-split {
    position: absolute; height: 6px; width: 6px; line-height: 1px; font-size: 1px;
    z-index: 12; background-color: #C4DCFB;
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-v.png')}");
    background-position: top; cursor: s-resize; cursor: row-resize;
}
.z-west-split {
    z-index: 11;
}
.z-east-split {
    z-index: 9;
}
.z-north-split {
    z-index: 15;
}
.z-south-split {
	z-index: 13;
}
.z-west-header, .z-center-header, .z-east-header,
	.z-north-header, .z-south-header {
	overflow: hidden; zoom: 1; color: #15428b; font: normal 11px tahoma, arial, verdana, sans-serif;
	padding: 5px 3px 4px 5px; border-bottom: 1px solid #B1CBD5; line-height: 15px; 
	background:transparent url(${c:encodeURL('~./zul/img/panel/panel-tb.png')}) repeat-x 0 -1px;
	white-space: nowrap;font-weight:bold;
}
.z-south-collapse {
	background-position: 0 -195px;
}
.z-south-collapse-over {
	background-position: -15px -195px;
}
.z-north-collapse {
	background-position: 0 -210px;
}
.z-north-collapse-over{
	background-position: -15px -210px;
}
.z-west-collapse {
	background-position: 0 -180px;
}
.z-west-collapse-over {
	background-position: -15px -180px;
}
.z-east-collapse {
	background-position: 0 -165px;
}
.z-east-collapse-over {
	background-position: -15px -165px;
}
.z-south-expand {
	background-position: 0 -210px;
}
.z-south-expand-over {
	background-position: -15px -210px;
}
.z-north-expand {
	background-position: 0 -195px;
}
.z-north-expand-over {
	background-position: -15px -195px;
}
.z-west-expand {
	background-position: 0 -165px;
}
.z-west-expand-over {
	background-position: -15px -165px;
}
.z-east-expand {
	background-position: 0 -180px;
}
.z-east-expand-over {
	background-position: -15px -180px;
}
.z-north-expand, .z-south-expand {
	float: right; margin: 3px;
}
.z-east-expand, .z-west-expand {
	float: none; margin: 3px auto;
}
.z-east-collapsed, .z-west-collapsed, .z-south-collapsed, .z-north-collapsed {
	position: absolute; background-color: #E1F1FB; width: 20px; height: 20px;
	border: 1px solid #98C0F4; z-index:20; overflow: hidden;
}
.z-east-collapsed-over, .z-west-collapsed-over, .z-south-collapsed-over, .z-north-collapsed-over {
	cursor: pointer; background-color: #E8FFFC;
}
.z-west-collapsed {
    z-index: 12;
}
.z-east-collapsed {
    z-index: 10;
}
.z-north-collapsed {
    z-index: 16;
}
.z-south-collapsed {
	z-index: 14;
} 