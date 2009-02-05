<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-paging {
	border-color: #a9bfd3; border-style: solid; border-width: 0 0 1px 0; display: block;
	padding: 2px; background: #D0DEF0 url(${c:encodeURL('~./zul/img/button/tb-bg.png')}) repeat-x top left;
	position: relative; zoom: 1;
}
.z-paging .z-paging-text {
	padding:2px;
}
.z-paging .z-paging-sep {
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-split.gif')});
	background-position:center;	background-repeat:no-repeat;
	display:block;font-size:1px;height:16px;width:4px;overflow:hidden;
	cursor:default;margin:0 2px 0;border:0;
}
.z-paging-inp {
	width: 24px; height: 14px; border: 1px solid #7F9DB9;
}
.z-paging-btn-m .z-paging-first, .z-paging-btn-m .z-paging-last, .z-paging-btn-m .z-paging-next, .z-paging-btn-m .z-paging-prev {
	background-position: center; background-repeat: no-repeat; height: 16px; width: 16px;
	cursor: pointer; white-space: nowrap; padding: 0;
}
.z-paging td {
	vertical-align: middle;
}
.z-paging td, .z-paging span, .z-paging input,
	.z-paging div, .z-paging select, .z-paging label {
	white-space: nowrap; font: normal ${fontSizeM} ${fontFamilyT};
}
.z-paging-first {
	background-image: url(${c:encodeURL('~./zul/img/paging/pg-first.gif')})!important;
	background-position:0px 0px!important;
}
.z-paging-last {
	background-image: url(${c:encodeURL('~./zul/img/paging/pg-last.gif')})!important;
	background-position:0px 0px!important;
}
.z-paging-next {
	background-image: url(${c:encodeURL('~./zul/img/paging/pg-next.gif')})!important;
	background-position:0px 0px!important;
}
.z-paging-prev {
	background-image: url(${c:encodeURL('~./zul/img/paging/pg-prev.gif')})!important;
	background-position:0px 0px!important;
}
.z-paging-info{
	position:absolute;top:5px;right:8px;color:#444;
}
.z-paging-btn {
	font-weight: normal; font-family: ${fontFamilyT};
	cursor: pointer; white-space: nowrap; font-size: ${fontSizeM};
	width: auto;
}
.z-paging-btn button {
	border: 0 none; background: transparent;
	font-weight: normal; font-size: ${fontSizeM};
	font-family: ${fontFamilyT};
	padding-left: 3px; padding-right: 3px; cursor: pointer; margin: 0; overflow: visible;
	width: auto; -moz-outline: 0 none; outline: 0 none; min-height: 13px;
}
<c:if test="${c:isExplorer()}">
.z-paging-btn button {
	padding-top: 2px;
}
</c:if>
<c:if test="${c:isGecko()}">
.z-paging-btn button {
	padding-left: 0; padding-right: 0;
}
</c:if>
.z-paging-btn em {
	font-style: normal; font-weight: normal;
}
.z-paging-btn-l, .z-paging-btn-r {
	font-size: 1px; line-height: 1px;
	background: none; width: 3px; height: 21px;
}
.z-paging-btn-l i,.z-paging-btn-r i {
	display: block; width: 3px; overflow: hidden; font-size: 1px; line-height: 1px;
}
.z-paging-btn-m {
	vertical-align: middle; text-align: center; cursor: pointer;
	white-space: nowrap; background: none; padding: 0;
}
<%-- Disable --%>
.z-paging-disd .z-paging-first {
	background-image: url(${c:encodeURL('~./zul/img/paging/pg-first.gif')})!important;
	background-position:-80px 0px!important;
}
.z-paging-disd .z-paging-last {
	background-image: url(${c:encodeURL('~./zul/img/paging/pg-last.gif')})!important;
	background-position:-80px 0px!important;
}
.z-paging-disd .z-paging-next {
	background-image: url(${c:encodeURL('~./zul/img/paging/pg-next.gif')})!important;
	background-position:-80px 0px!important;
}
.z-paging-disd .z-paging-prev {
	background-image: url(${c:encodeURL('~./zul/img/paging/pg-prev.gif')})!important;
	background-position:-80px 0px!important;
}
.z-paging-btn-disd .z-paging-btn {
	opacity: .35; -moz-opacity: .35; filter: alpha(opacity=35);
}
.z-paging-btn-disd {
	color: gray !important; cursor: default !important; opacity: .5; -moz-opacity: .5; filter: alpha(opacity=50);
}
.z-paging-btn-disd * {
	color: gray !important; cursor: default !important;
}

<%-- Mouseover --%>
.z-paging-btn-over .z-paging-btn-l {
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-side.png')});
	background-repeat : no-repeat;
	background-position : 0 0;
}
.z-paging-btn-over .z-paging-btn-r {
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-side.png')});
	background-repeat : no-repeat;
	background-position : 0 -21px;
}
.z-paging-btn-over .z-paging-btn-m {
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-side.png')});
	background-repeat : repeat-x;
	background-position : 0 -42px;
}
<%-- Click --%>
.z-paging-btn-clk .z-paging-btn-l {
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-side.png')});
	background-repeat : no-repeat;
	background-position : 0 -63px;
}
.z-paging-btn-clk .z-paging-btn-r {
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-side.png')});
	background-repeat : no-repeat;
	background-position : 0 -84px;
}
.z-paging-btn-clk .z-paging-btn-m {
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-side.png')});
	background-repeat : repeat-x;
	background-position : 0 -105px;
}
<%-- os mold --%>
.z-paging-os {
	background: white; padding: 5px; zoom:1;
}
.z-paging-os .z-paging-os-cnt {
	padding: 2px 3px;
	background-color:#C7E5F1;
	background-image:url(${c:encodeURL('~./zul/img/grid/s_hd.gif')});
	background-repeat:repeat-x;
	border: 1px solid #DAE7F6;
	font-size: ${fontSizeS}; color: #1725A0; font-weight: normal;
	text-decoration: none;
}
.z-paging-os .z-paging-os-cnt:hover {
	color: red;
}
.z-paging-os .z-paging-os-seld {
	font-size: ${fontSizeS}; color: white; font-weight: bold;
	background-image:url(${c:encodeURL('~./zul/img/grid/paging-os-seld.gif')});
}
.z-paging-os .z-paging-os-seld:hover {
	color: #403E39;
}
.z-paging-os span {
	font-size: ${fontSizeS}; color: #555; font-weight: normal;
}