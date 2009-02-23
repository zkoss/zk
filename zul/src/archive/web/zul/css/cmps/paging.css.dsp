<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

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

<%-- default mold --%>

/* font properties */
.z-paging td, 
.z-paging span, 
.z-paging input, 
.z-paging div,
.z-paging button{
	-x-system-font:none;
	font-size-adjust:none;
	font-stretch:normal;
	font-style:normal;
	font-variant:normal;
	font-weight:normal;
	line-height:normal;
	white-space:nowrap;
	font-family: ${fontFamilyT}; 
	font-size: ${fontSizeMS};
}

.z-paging {
	border-color:#B1CBD5;
	border-style:solid;
	border-width:0 0 1px;
	display:block;
	padding:2px;
	position:relative;
	background:#D0DEF0 repeat-x scroll left top;
	background-image: url(${c:encodeURL('~./zul/img/button/tb-bg.png')});
}

.z-paging td {
	vertical-align:middle;
}

.z-paging .z-paging-btn {
	cursor:pointer;
	font-weight:normal;
	white-space:nowrap;
	width:auto;
	font-family: ${fontFamilyT}; 
	font-size: ${fontSizeMS};
}

.z-paging .z-paging-btn-l,
.z-paging .z-paging-btn-r{
	background:transparent none repeat scroll 0 0;
	font-size:1px;
	height:21px;
	line-height:1px;
	width:3px;
}

.zpaging .z-paging-btn-m {
	background:transparent none repeat scroll 0 0;
	cursor:pointer;
	padding:0;
	text-align:center;
	vertical-align:middle;
	white-space:nowrap;
}

.z-paging-btn-m .z-paging-first,
.z-paging-btn-m .z-paging-last,
.z-paging-btn-m .z-paging-next,
.z-paging-btn-m .z-paging-prev {
	background-position:center center;
	background-repeat:no-repeat;
	cursor:pointer;
	height:16px;
	padding:0;
	white-space:nowrap;
	width:16px;
}

/*define icon of button*/
.z-paging-btn button {
	background:transparent none repeat scroll 0 0;
	border:0 none;
	cursor:pointer;
	margin:0;
	min-height:13px;
	outline-color:-moz-use-text-color;
	outline-style:none;
	outline-width:0;
	overflow:visible;
	padding-left:0px;
	padding-right:0px;
	width:auto;
}

.z-paging .z-paging-sep {
	background:no-repeat center center;
	border:0 none;
	cursor:default;
	display:block;
	font-size:1px;
	height:16px;
	margin:0 2px;
	overflow:hidden;
	width:4px;
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-split.gif')});
}

.z-paging-btn-m .z-paging-first {
	background-position:0 0 !important;
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-first.gif')}) !important;
}
.z-paging-btn-m .z-paging-last {
	background-position:0 0 !important;
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-last.gif')}) !important;
}
.z-paging-btn-m .z-paging-next {
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-next.gif')}) !important;
	background-position:0 0 !important;
}
.z-paging-btn-m .z-paging-prev {
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-prev.gif')}) !important;
	background-position:0 0 !important;
}

/*mouse over a button */
.z-paging-btn-over .z-paging-btn-l,
.z-paging-btn-over .z-paging-btn-r,
.z-paging-btn-over .z-paging-btn-m,
.z-paging-btn-clk .z-paging-btn-l,
.z-paging-btn-clk .z-paging-btn-r,
.z-paging-btn-clk .z-paging-btn-m {
	background-repeat:no-repeat;
	background-image:url(${c:encodeURL('~./zul/img/button/tb-btn-side.png')});
}

.z-paging-btn-over .z-paging-btn-l {
	background-position:0 0;
}
.z-paging-btn-over .z-paging-btn-r {
	background-position:0 -41px;
}
.z-paging-btn-over .z-paging-btn-m {
	background-position:0 -82px;
	background-repeat:repeat-x;
}
/* mouse click on a button */
.z-paging-btn-clk .z-paging-btn-l {
	background-position:0 -123px;
}
.z-paging-btn-clk .z-paging-btn-r {
	background-position:0 -164px;
}
.z-paging-btn-clk .z-paging-btn-m {
	background-position:0 -205px;
	background-repeat:repeat-x;
}
/* disabled button */
.z-paging .z-paging-btn-disd{
	color:gray !important;
	cursor:default !important;
	opacity:0.5;
}
.z-paging .z-paging-btn-disd *{
	color:gray !important;
	cursor:default !important;
}

/*paging info*/
.z-paging-info {
	color:#444444;
	position:absolute;
	right:8px;
	top:5px;
}