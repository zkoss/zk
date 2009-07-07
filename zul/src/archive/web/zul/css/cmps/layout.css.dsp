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
	background-image : url(${c:encodeURL('~./zul/img/layout/borderlayout-btn.gif')});
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
	font-size: ${fontSizeMS};
	font-family: ${fontFamilyT};
	font-weight: bold;
	padding: 5px 3px 4px 5px;
	border-bottom: 1px solid #9ecad8;
	background: transparent repeat-x 0 0;
	background-image: url(${c:encodeURL('~./zul/img/layout/borderlayout-hm.png')});
	white-space: nowrap;
	overflow: hidden;
	line-height: 15px;
	zoom: 1;
	cursor: default;
}
.z-north-exp,
.z-south-exp {
	margin: 3px;
	float: right; 
}
.z-east-exp,
.z-west-exp {
	margin-top: 3px;
	margin-bottom: 3px;
	margin-left: auto;
	margin-right: auto;
	float: none;
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
.z-east-colpsd,
.z-west-colpsd,
.z-south-colpsd,
.z-north-colpsd {
	background-color: #E1F0F2;
	width: 22px;
	height: 22px;
	z-index: 30;
	border: 1px solid #9ECAD8;
	overflow: hidden;
	position: absolute;
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
.z-east-colpsd-over,
.z-west-colpsd-over,
.z-south-colpsd-over,
.z-north-colpsd-over {
	cursor: pointer;
	background-color: #EEFCFF;
}