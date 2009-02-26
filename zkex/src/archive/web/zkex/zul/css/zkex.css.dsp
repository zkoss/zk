<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%-- Borderlayout --%>
.z-border-layout {
    width:100%;
    height:100%;
    overflow: hidden;
    background-color:#CDE6F5;
    border: 0;
	position: relative;
	visibility: hidden;
}
.z-border-layout-icon {
	overflow: hidden;
	width: 16px;
	height: 16px;
	float: right;
	background: transparent no-repeat 0 0;
	background-image : url(${c:encodeURL('~./zkex/zul/img/layout/borderlayout-btn.gif')});
	margin-left: 2px;
	cursor: pointer;
}
<%-- LayoutRegion --%>
.z-north,
.z-south,
.z-west,
.z-center,
.z-east {
	border: 1px solid #9ECAD8;
	position: absolute;
	overflow: hidden;
	background-color: white;
}
.z-west-noborder,
.z-center-noborder,
.z-east-noborder,
.z-north-noborder,
.z-south-noborder {
	border:0;
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
.z-east-splt,
.z-west-splt {
    position: absolute;
    height: 6px;
    width: 6px;
    z-index: 12;
    background: #C4DCFB left;
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-h.png')}");
    cursor: e-resize;
    cursor: col-resize;
    line-height: 1px;
    font-size: 1px;
}
.z-north-splt,
.z-south-splt {
    position: absolute;
    height: 6px;
    width: 6px;
    z-index: 12;
    background: #C4DCFB top;
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-v.png')}");
    cursor: s-resize;
    cursor: row-resize;
    
    line-height: 1px;
    font-size: 1px;
}
.z-west-splt {
    z-index: 11;
}
.z-east-splt {
    z-index: 9;
}
.z-north-splt {
    z-index: 15;
}
.z-south-splt {
	z-index: 13;
}
.z-west-header,
.z-center-header,
.z-east-header,
.z-north-header,
.z-south-header {
	color: #0F3B82;
	font: normal 11px tahoma, arial, verdana, sans-serif;
	font-weight:bold;
	padding: 5px 3px 4px 5px;
	border-bottom: 1px solid #a7dcf9;
	background:transparent repeat-x 0 0;
	background-image: url(${c:encodeURL('~./zkex/zul/img/layout/borderlayout-hm.png')});
	white-space: nowrap;
	overflow: hidden;
	line-height: 15px;
	zoom: 1;
}
.z-north-colps,
.z-south-exp {
	background-position: 0 0;
}
.z-north-colps-over,
.z-south-exp-over {
	background-position: -16px 0;
}
.z-east-colps,
.z-west-exp {
	background-position: 0 -16px;
}
.z-east-colps-over,
.z-west-exp-over {
	background-position: -16px -16px;
}
.z-south-colps,
.z-north-exp {
	background-position: 0 -32px;
}
.z-south-colps-over,
.z-north-exp-over {
	background-position: -16px -32px;
}
.z-west-colps,
.z-east-exp {
	background-position: 0 -48px;
}
.z-west-colps-over,
.z-east-exp-over {
	background-position: -16px -48px;
}
.z-north-exp, .z-south-exp {
	margin: 3px;
	float: right; 
}
.z-east-exp, .z-west-exp {
	margin: 3px auto;
	float: none;
}
.z-east-colpsd,
.z-west-colpsd,
.z-south-colpsd,
.z-north-colpsd {
	background-color: #E5FDFF;
	position: absolute;
	width: 20px;
	height: 20px;
	border: 1px solid #98C0F4;
	z-index:20;
	overflow: hidden;
}
.z-east-colpsd-over,
.z-west-colpsd-over,
.z-south-colpsd-over,
.z-north-colpsd-over {
	cursor: pointer;
	background-color: #EEFCFF;
}
.z-west-colpsd {
    z-index: 12;
}
.z-east-colpsd {
    z-index: 10;
}
.z-north-colpsd {
    z-index: 16;
}
.z-south-colpsd {
	z-index: 14;
} 
<%-- Column Layout--%>
.z-column-layout {
	visibility: hidden;
	overflow: hidden;
	zoom: 1;
}
.z-column-children {
    margin: 0;
    padding: 0;
    float: left;
    overflow: hidden;
    zoom: 1;
}
.z-column-children-cnt {
    overflow: hidden;
}
.z-column-children-body {
    overflow: hidden;
    zoom: 1;
}
.z-column-layout-inner {
    overflow: hidden;
}
<%-- Fisheyebar--%>
.z-fisheye-text {
	font-size: ${fontSizeM}; font-weight: normal;
	font-family: Arial, Helvetica, sans-serif;
	background-color: #eee;
	border: 2px solid #666;
	padding: 2px;
	text-align: center;
	position: absolute;
	display: none;
}
.z-fisheye-img {
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
	height: 100%;
	padding: 0!important;
	overflow: hidden;
	white-space: nowrap;
}
.z-detail .z-detail-img {
	cursor:pointer;
	width: 100%;
	height: 18px;
	background: transparent no-repeat 4px 3px;
	background-image: url(${c:encodeURL('~./zul/img/grid/row-expand.png')}); 
}
.z-detail.z-detail-expd .z-detail-img {
	background-position: -12px 3px;
}
tr.z-row .z-detail-outer {
	background: #C6E8FC repeat-y left;
	border-top: none;
	border-left: 1px solid white;
	border-right: 1px solid #C0C0C0;
	border-bottom: 1px solid #D0D0D0;
	vertical-align: top;
	background-image: url(${c:encodeURL('~./zul/img/grid/detail-bg.png')});
}
