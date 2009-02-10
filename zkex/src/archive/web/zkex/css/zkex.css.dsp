<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%-- Column Layout--%>
.z-column-layout {
	visibility: hidden; overflow: hidden; zoom: 1;
}
.z-column-children {
    float: left; padding: 0; margin: 0; overflow: hidden; zoom: 1;
}
.z-column-children-cnt {
    overflow: hidden;
}
.z-column-children-body {
    overflow: hidden; zoom: 1;
}
.z-column-layout-inner {
    overflow: hidden;
}
<%-- Fisheyebar--%>
.z-fisheye-text {
	font-size: ${fontSizeM}; font-weight: normal;
	font-family: Arial, Helvetica, sans-serif; background-color: #eee; border: 2px solid #666;
	padding: 2px; text-align: center; position: absolute; display: none;
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
	height: 100%; padding: 0!important; overflow: hidden;
	-o-text-overflow: ellipsis; text-overflow: ellipsis; white-space:nowrap;
}
tr.z-row td.z-detail-outer {
	background: #bae2f0 url(${c:encodeURL('~./zul/img/grid/detail-bg.gif')}) repeat-y left;
	vertical-align: top; border-top: none; border-left: 1px solid white;
	border-right: 1px solid #CCC; border-bottom: 1px solid #DDD;
}
.z-detail-img {
	background-image: url(${c:encodeURL('~./zul/img/grid/row-expand.gif')});
	width: 100%; height: 18px; background-position: 4px 2px;
	background-repeat: no-repeat; background-color: transparent; 
}
.z-detail-expanded .z-detail-img {
	background-position: -21px 2px;
}
