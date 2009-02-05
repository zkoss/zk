<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- Menu and Menuitem --%>
.z-menu-btn, .z-menu-item-btn {
	font: normal ${fontFamilyC};
	cursor: pointer; white-space: nowrap; font-size: ${fontSizeMS};
}
.z-menu-btn button, .z-menu-item-btn button {
	border: 0 none; background: transparent;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeMS};
	padding-left: 3px; padding-right: 3px; cursor: pointer; margin: 0; overflow: visible;
	width: auto; -moz-outline: 0 none; outline: 0 none; min-height: 13px;
}
<c:if test="${c:isExplorer()}">
.z-menu-btn button, .z-menu-item-btn button {
	padding-top: 2px;
}
</c:if>
<c:if test="${c:isGecko()}">
.z-menu-btn button, .z-menu-item-btn button {
	padding-left: 0; padding-right: 0;
}
</c:if>
.z-menu-btn-img .z-menu-btn-m .z-menu-btn-text,
.z-menu-item-btn-img .z-menu-item-btn-m .z-menu-item-btn-text {
	background-position: center; background-repeat: no-repeat; height: 16px; width: 16px;
	cursor: pointer; white-space: nowrap; padding: 0;
}
.z-menu-btn-img .z-menu-btn-m,
.z-menu-item-btn-img .z-menu-item-btn-m {
	padding: 1px;
}
.z-menu-btn em, .z-menu-item-btn em {
	font-style: normal; font-weight: normal;
}
.z-menu-btn-text-img .z-menu-btn-m .z-menu-btn-text,
.z-menu-item-btn-text-img .z-menu-item-btn-m .z-menu-item-btn-text {
	background-position: 0 2px; background-repeat: no-repeat; padding-left: 18px;
	padding-top: 3px; padding-bottom: 2px; padding-right:0;
}
.z-menu-btn-l, .z-menu-btn-r,
.z-menu-item-btn-l, .z-menu-item-btn-r {
	font-size: 1px; line-height: 1px;
	width: 3px; height: 21px;
}
.z-menu-btn-l i,.z-menu-btn-r i, .z-menu-btn-ml i, .z-menu-btn-mr i,
.z-menu-item-btn-l i,.z-menu-item-btn-r i, .z-menu-item-btn-ml i, .z-menu-item-btn-mr i {
	display: block; width: 3px; overflow: hidden; font-size: 1px; line-height: 1px;
}
.z-menu-btn-m, .z-menu-item-btn-m {
	text-align: center; cursor: pointer;
}
.z-menu-btn-over .z-menu-btn-l, .z-menu-item-btn-over .z-menu-item-btn-l {
	background-position: 0 -63px;
}
.z-menu-btn-over .z-menu-btn-r, .z-menu-item-btn-over .z-menu-item-btn-r {
	background-position: 0 -84px;
}
.z-menu-btn-over .z-menu-btn-m, .z-menu-item-btn-over .z-menu-item-btn-m {
	background-position: 0 -105px;
}
.z-menu-disd *, .z-menu-item-disd * {
	color: gray!important; cursor: default!important;
}
.z-menu-btn-seld .z-menu-btn-m,
.z-menu-item-btn-seld .z-menu-item-btn-m {
	background-position: 0 -126px;
}
.z-menu-btn .z-menu-btn-m,
.z-menu-item-btn .z-menu-item-btn-m {
	padding-right: 2px!important;
}
.z-menu-btn .z-menu-btn-m em {
	display: block; 
	background-color : transparent;
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-arrow.png')});
	background-repeat : no-repeat;
	background-position : right 0;
	padding-right: 10px; min-height: 16px;
}
.z-menu-btn-text-img .z-menu-btn-m em {
	display: block;
	background-color : transparent;
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-arrow.png')});
	background-repeat : no-repeat;
	background-position : right 3px;
	padding-right: 10px;
}
.z-menu-cnt-img {
	background: transparent url(${c:encodeURL('~./zul/img/menu2/arrow.png')}) no-repeat right;
}
.z-menu-cnt, .z-menu-item-cnt {
	text-decoration: none; white-space: nowrap;  font-style: normal;
	font-family: ${fontFamilyT}; font-size: ${fontSizeMS};
}
.z-menu-popup-cnt .z-menu-item-over, .z-menu-popup-cnt .z-menu-over {
	background: #DDEEFB url(${c:encodeURL('~./zul/img/menu2/item-over.gif')}) repeat-x 0 0;
	border: 1px solid #A8D8EB; padding: 0;
}
.z-menu-popup .z-menu-item-over .z-menu-item-cnt, .z-menu-popup .z-menu-over .z-menu-cnt {
	color:#233d6d;
}
.z-menu-item-img, .z-menu-img {
	border: 0 none; height: 16px; padding: 0; vertical-align: top; width: 16px;
	margin: 0 8px 0 0; background-position: center;
}
.z-menu-item-cnt-ck .z-menu-item-img {
	background: transparent url(${c:encodeURL('~./zul/img/menu2/checked.gif')}) no-repeat center;
}
.z-menu-item-cnt-unck .z-menu-item-img {
	background: transparent url(${c:encodeURL('~./zul/img/menu2/unchecked.png')}) no-repeat center;
}
<%-- Menubar --%>
.z-menubar-hor, .z-menubar-ver  {
	border-color: #a9bfd3; border-style: solid; border-width: 0 0 1px 0; display: block;
	padding: 2px; background: #CEE7F5 url(${c:encodeURL('~./zul/img/button/tb-bg.png')}) repeat-x top left;
	position: relative; zoom: 1;
}
.z-menubar-hor .z-menu-item-disd .z-menu-btn-img,
.z-menubar-ver .z-menu-item-disd .z-menu-btn-img,
.z-menu-popup .z-menu-item-disd .z-menu-btn-img {
	opacity: .35; -moz-opacity: .35; filter: alpha(opacity=35);
}
.z-menubar-hor .z-menu, .z-menubar-hor .z-menu-item, .z-menubar-ver .z-menu, .z-menubar-ver .z-menu-item{
	vertical-align: middle;
}
.z-menubar-hor .z-menu-btn td, .z-menubar-ver .z-menu-btn td,
.z-menubar-hor .z-menu-item-btn td, .z-menubar-ver .z-menu-item-btn td {
	border: 0 !important;
}
.z-menubar-hor .z-menu, .z-menubar-hor .z-menu-item, .z-menubar-hor span, .z-menubar-hor input, .z-menubar-hor div, .z-menubar-hor select,
	.z-menubar-hor label,
.z-menubar-ver .z-menu, .z-menubar-ver .z-menu-item, .z-menubar-ver span, .z-menubar-ver input, .z-menubar-ver div, .z-menubar-ver select,
	.z-menubar-ver label {
	white-space: nowrap; font: normal ${fontSizeMS} ${fontFamilyT};
}
.z-menubar-hor .z-menu-item-disd,
.z-menubar-ver .z-menu-item-disd,
.z-menu-popup .z-menu-item-disd {
	color: gray !important; cursor: default !important; opacity: .5; -moz-opacity: .5; filter: alpha(opacity=50);
}
.z-menubar-hor .z-menu-item-disd *,
.z-menubar-ver .z-menu-item-disd *,
.z-menu-popup .z-menu-item-disd * {
	color: gray !important; cursor: default !important;
}
.z-menubar-hor .z-menu-btn-l, .z-menubar-ver .z-menu-btn-l,
.z-menubar-hor .z-menu-item-btn-l, .z-menubar-ver .z-menu-item-btn-l {
	background: none;
}
.z-menubar-hor .z-menu-btn-r, .z-menubar-ver .z-menu-btn-r,
.z-menubar-hor .z-menu-item-btn-r, .z-menubar-ver .z-menu-item-btn-r {
	background: none;
}
.z-menubar-hor .z-menu-btn-m, .z-menubar-ver .z-menu-btn-m,
.z-menubar-hor .z-menu-item-btn-m, .z-menubar-ver .z-menu-item-btn-m {
	background: none; padding: 0;
}
.z-menubar-hor .z-menu-btn-over .z-menu-btn-l, .z-menubar-ver .z-menu-btn-over .z-menu-btn-l,
.z-menubar-hor .z-menu-item-btn-over .z-menu-item-btn-l, .z-menubar-ver .z-menu-item-btn-over .z-menu-item-btn-l {
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-side.png')});
	background-repeat : no-repeat;
	background-position : 0 0;
}
.z-menubar-hor .z-menu-btn-over .z-menu-btn-r, .z-menubar-ver .z-menu-btn-over .z-menu-btn-r,
.z-menubar-hor .z-menu-item-btn-over .z-menu-item-btn-r, .z-menubar-ver .z-menu-item-btn-over .z-menu-item-btn-r {
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-side.png')});
	background-repeat : no-repeat; 
	background-position : 0 -21px;
}
.z-menubar-hor .z-menu-btn-over .z-menu-btn-m, .z-menubar-ver .z-menu-btn-over .z-menu-btn-m,
.z-menubar-hor .z-menu-item-btn-over .z-menu-item-btn-m, .z-menubar-ver .z-menu-item-btn-over .z-menu-item-btn-m {
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-side.png')});
	background-repeat : repeat-x; 
	background-position : 0 -42px;
}
.z-menubar-hor .z-menu-btn-seld .z-menu-btn-l,
.z-menubar-ver .z-menu-btn-seld .z-menu-btn-l,
.z-menubar-hor .z-menu-item-btn-seld .z-menu-item-btn-l,
.z-menubar-ver .z-menu-item-btn-seld .z-menu-item-btn-l {
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-side.png')});
	background-repeat : no-repeat; 
	background-position :0 -63px;
}
.z-menubar-hor .z-menu-btn-seld .z-menu-btn-r,
.z-menubar-ver .z-menu-btn-seld .z-menu-btn-r,
.z-menubar-hor .z-menu-item-btn-seld .z-menu-item-btn-r,
.z-menubar-ver .z-menu-item-btn-seld .z-menu-item-btn-r {
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-side.png')});
	background-repeat : no-repeat; 
	background-position : 0 -84px;
}
.z-menubar-hor .z-menu-btn-seld .z-menu-btn-m,
.z-menubar-ver .z-menu-btn-seld .z-menu-btn-m,
.z-menubar-hor .z-menu-item-btn-seld .z-menu-item-btn-m,
.z-menubar-ver .z-menu-item-btn-seld .z-menu-item-btn-m {
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-side.png')});
	background-repeat : repeat-x; 
	background-position : 0 -105px;
}
.z-menubar-hor .z-menu-btn .z-menu-btn-m em, .z-menubar-ver .z-menu-btn .z-menu-btn-m em {
	padding-right: 8px;
}
.z-menubar-hor .z-menu-item-btn .z-menu-item-btn-m em, .z-menubar-ver .z-menu-item-btn .z-menu-item-btn-m em {
	padding-right: 0px;
} 
.z-menubar-ver .z-menu-btn .z-menu-btn-m em {
	display: block; 
	background-color : transparent;
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-arrow-ver.png')});
	background-repeat : no-repeat;
	background-position : right 0;
	padding-right: 10px; min-height: 16px;
}
.z-menubar-ver .z-menu-btn-text-img .z-menu-btn-m em {
	display: block;
	background-color : transparent;
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-arrow-ver.png')});
	background-repeat : no-repeat;
	background-position : right 3px;
	padding-right: 10px;
}

<%-- Menuseparator --%>
.z-menu-popup .z-menu-separator {
	font-size: 1px; line-height: 1px;
}
.z-menu-separator-inner {
	display: block;
	overflow: hidden;
	border-width:1px;
	border-style:none none solid;
	border-color:#eee;
	background-color: #e0e0e0;
	margin: 2px 3px;
	width: auto;
}
.z-menubar-hor .z-menu-separator-inner {
	font-size: ${fontSizeM}; margin: 2px 3px; width: 2px;
	border-style: none solid none none;
	background-color:#ddd;
}
.z-menubar-ver .z-menu-separator-inner {
	font-size: 1px; line-height: 1px;
	background-color:#ccc;
}
.z-menu-popup .z-menu-separator-inner {
	font-size: 1px; line-height: 1px;
	border-color:#fff;
}

<%-- Menupopup--%>
.z-menu-popup {
	position: absolute; top: 0; left: 0;
	border:	1px solid #7F9DB9; zoom: 1; padding: 2px;
	background: #E7F3FA url(${c:encodeURL('~./zul/img/menu2/pp-bg.gif')}) repeat-y;
}
.z-menu-popup-cnt li {
	text-decoration: none; font: normal ${fontSizeMS} ${fontFamilyT};
	white-space: nowrap;
	display: block; padding: 1px;
}
.z-menu-popup .z-menu-popup-cnt, .z-menu-popup li {
	list-style: none !important; margin: 0 !important;
	list-style-position: outside !important; list-style-type: none !important;
	list-style-image: none !important;
}
.z-menu-popup .z-menu-popup-cnt {
	padding: 0;
}
.z-menu-popup a {
	text-decoration: none!important;
}
.z-menu-popup-cnt {
	background: transparent; border: 0 none;
}
.z-menu-popup li {	
	line-height:100%;
}
.z-menu-popup a.z-menu-cnt, .z-menu-popup a.z-menu-item-cnt {
	display: block; line-height: 16px; padding: 3px 21px 3px 3px; white-space: nowrap;
	text-decoration: none; color: #222; -moz-outline: 0 none; outline: 0 none; cursor: pointer;
}
