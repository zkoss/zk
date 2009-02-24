<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

/* define common font property*/
.z-menubar-hor .z-menu,.z-menubar-hor .z-menu-item,.z-menubar-hor .z-menu-btn,.z-menubar-hor .z-menu-item-btn,
.z-menubar-hor span,.z-menubar-hor a,.z-menubar-hor div,
.z-menubar-ver .z-menu,.z-menubar-ver .z-menu-item,.z-menubar-ver .z-menu-btn,.z-menubar-ver .z-menu-item-btn,
.z-menubar-ver span,.z-menubar-ver a,.z-menubar-ver div {
	-adjust:none;
	font-stretch:normal;
	font-style:normal;
	font-variant:normal;
	font-weight:normal;
	line-height:normal;
	white-space:nowrap;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeMS};
}

/* define common horizontal and vertical property */
.z-menubar-hor,.z-menubar-ver {
	position : relative;
	display: block;
	padding : 2px 0px;
	border-bottom : 1px solid #B1CBD5;
	background: #CEE7F5 repeat-x;
	background-image: url(${c:encodeURL('~./zul/img/common/bar-bg.png')});
}

.z-menubar-hor .z-menu, .z-menubar-hor .z-menu-item,
.z-menubar-ver .z-menu, .z-menubar-ver .z-menu-item {
	vertical-align:middle;
}

.z-menu-cnt, .z-menu-item-cnt {
	text-decoration: none;
	white-space: nowrap;
	font-style: normal;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeMS};
}

.z-menubar-hor .z-menu-body, .z-menubar-hor .z-menu-item-body,
.z-menubar-ver .z-menu-body, .z-menubar-ver .z-menu-item-body {
	cursor: pointer;
}

.z-menubar-hor .z-menu-body .z-menu-inner-l,.z-menubar-hor .z-menu-body .z-menu-inner-r,
.z-menubar-hor .z-menu-item-body .z-menu-item-inner-l,.z-menubar-hor .z-menu-item-body .z-menu-item-inner-r,
.z-menubar-ver .z-menu-body .z-menu-inner-l,.z-menubar-ver .z-menu-body .z-menu-inner-r,
.z-menubar-ver .z-menu-item-body .z-menu-item-inner-l,.z-menubar-ver .z-menu-item-body .z-menu-item-inner-r{
	font-size:1px;
	height:21px;
	line-height:1px;
	width:3px;
}

.z-menu-inner-l .z-menu-space,.z-menu-inner-r .z-menu-space,
.z-menu-item-inner-l .z-menu-item-space,.z-menu-item-inner-r .z-menu-item-space{
	display:block;
	width:3px;
}

.z-menubar-hor .z-menu-body .z-menu-inner-m, .z-menubar-hor .z-menu-item-body .z-menu-item-inner-m,
.z-menubar-ver .z-menu-body .z-menu-inner-m, .z-menubar-ver .z-menu-item-body .z-menu-item-inner-m{
	height:21px;
	text-align:center;
}

.z-menu-body .z-menu-inner-m div {
	display:block;
	min-height:16px;
	padding-right:2px;
	padding-left:0px;
	background: transparent no-repeat right -14px;
	background-image:url(${c:encodeURL('~./zul/img/menu/btn-arrow.gif')});
}
.z-menubar-ver .z-menu-body .z-menu-inner-m div {
	background-position: right 0px;
}
.z-menu-item-body .z-menu-item-inner-m div {
	background-color:transparent;
	display:block;
	min-height:16px;
	padding-right:0px;
	padding-left:0px;
}

.z-menu-inner-m .z-menu-btn,
.z-menu-item-inner-m .z-menu-item-btn{
	background:transparent none no-repeat scroll 0 2px;
	border:0px none;
	cursor:pointer;
	margin:0px;
	min-height:13px;
	outline-color:-moz-use-text-color;
	outline-style:none;
	outline-width:0px;
	overflow:visible;
	width:auto;
	padding-left:2px; /*button has default padding, reset it*/
	padding-top:3px;
	padding-bottom:2px;
	padding-right:1px;
	text-decoration: none;
}

.z-menu-body-text-img .z-menu-inner-m .z-menu-btn,.z-menu-body-img .z-menu-inner-m .z-menu-btn,
.z-menu-item-body-text-img .z-menu-item-inner-m .z-menu-item-btn{
	padding-left:18px;
}

.z-menu-item-body-img .z-menu-item-inner-m .z-menu-item-btn,
.z-menu-item-body-text .z-menu-item-inner-m .z-menu-item-btn{
	padding-left:12px;
	padding-right:0px;
}


<c:if test="${c:isExplorer()}">
<%-- IE only --%>
.z-menu-inner-m .z-menu-btn{
	padding-right:4px;
}
.z-menu-item-body-img .z-menu-item-inner-m .z-menu-item-btn,
.z-menu-item-body-text .z-menu-item-inner-m .z-menu-item-btn{
	padding-right:0px;
}
</c:if>

/* define menu/menuitem mouse over and seld effect */
.z-menu-body-over .z-menu-inner-l,
.z-menu-body-seld .z-menu-inner-l,
.z-menu-item-body-over .z-menu-item-inner-l {
	background-repeat : no-repeat;
	background-position : 0 0;
	background-image : url(${c:encodeURL('~./zul/img/menu/menu-btn.png')});
}
.z-menu-body-over .z-menu-inner-r,
.z-menu-body-seld .z-menu-inner-r,
.z-menu-item-body-over .z-menu-item-inner-r {
	background-repeat : no-repeat;
	background-position : 0 -41px;
	background-image : url(${c:encodeURL('~./zul/img/menu/menu-btn.png')});
}
.z-menu-body-over .z-menu-inner-m,
.z-menu-body-seld .z-menu-inner-m,
.z-menu-item-body-over .z-menu-item-inner-m {
	background-repeat : repeat-x;
	background-position : 0 -82px;
	background-image : url(${c:encodeURL('~./zul/img/menu/menu-btn.png')});
}
.z-menu-body-over .z-menu-inner-m .z-menu-btn,
.z-menu-body-seld .z-menu-inner-m .z-menu-btn,
.z-menu-item-body-over .z-menu-item-inner-m .z-menu-btn{
	color:#233D6D;
}

/*define disabled menuitem effect*/
.z-menubar-hor .z-menu-item-disd *, .z-menubar-ver .z-menu-item-disd *{
	color:gray !important;
	cursor:default !important;
}
.z-menubar-hor .z-menu-item-disd .z-menu-item-btn, .z-menubar-ver .z-menu-item-disd .z-menu-item-btn{
	opacity: .5;
	-moz-opacity: .5;
	filter: alpha(opacity=50);
}

/* define menupopup effect */
.z-menu-popup {
	background:#85BAD5 repeat-y scroll 0 0;
	border:1px solid #7F9DB9;
	padding:2px;
	z-index:88000;
	background-image : url(${c:encodeURL('~./zul/img/menu/pp-bg.png')});
}
/* define menupopup property*/
.z-menu-popup {
	-x-system-font:none;
	font-size-adjust:none;
	font-stretch:normal;
	font-style:normal;
	font-variant:normal;
	font-weight:normal;
	line-height:normal;
	text-decoration:none;
	white-space:nowrap;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeMS};
}

.z-menu-popup a {
	text-decoration:none !important;
}

.z-menu-popup .z-menu-popup-cnt{
	background:transparent none repeat scroll 0 0;
	border:0 none;
	padding:0px;
	margin:0px !important;
}

/* define menu & menuitem in menupopup */
.z-menu-popup-cnt .z-menu,
.z-menu-popup-cnt .z-menu-item,
.z-menu-popup-cnt .z-menu-separator {
	line-height:100%;
	list-style-image:none !important;
	list-style-position:outside !important;
	list-style-type:none !important;
	margin:0 !important;
	display:block;
	padding:1px;
	cursor:pointer;
}

.z-menu-popup-cnt .z-menu a.z-menu-cnt,
.z-menu-popup-cnt .z-menu-item a.z-menu-item-cnt {
	color:#222222;
	display:block;
	line-height:16px;
	outline-color:-moz-use-text-color;
	outline-style:none;
	outline-width:0;
	padding:3px 21px 3px 3px;
	white-space:nowrap;
}

.z-menu-popup-cnt .z-menu .z-menu-img,
.z-menu-popup-cnt .z-menu-item .z-menu-item-img {
	background-position:center center;
	border:0px;
	height:16px;
	margin-right:9px;
	padding:0;
	vertical-align:top;
	width:16px;
}

.z-menu-popup-cnt .z-menu .z-menu-cnt-img {
	background:transparent no-repeat scroll right center;
	background-image:url(${c:encodeURL('~./zul/img/menu/arrow.gif')});
}

/*define checked menuitem effect in menupopup */
.z-menu-popup-cnt .z-menu-item-cnt-ck .z-menu-item-img {
	background:transparent no-repeat scroll center center;
	background-image:url(${c:encodeURL('~./zul/img/menu/checked.gif')});
}

.z-menu-popup-cnt .z-menu-item-cnt-unck .z-menu-item-img {
	background:transparent no-repeat scroll center center;
	background-image:url(${c:encodeURL('~./zul/img/menu/unchecked.gif')});
}

/*define disabled menuitem effect in menupopup*/
.z-menu-popup-cnt .z-menu-item-disd,
.z-menu-popup-cnt .z-menu-item-disd *{
	color:gray !important;
	cursor:default !important;
}
.z-menu-popup-cnt .z-menu-disd .z-menu-img,
.z-menu-popup-cnt .z-menu-item-disd .z-menu-item-img{
	opacity: .5;
	-moz-opacity: .5;
	filter: alpha(opacity=50);
}

/*define mouse over effect in menupopup*/
.z-menu-popup-cnt .z-menu-over,
.z-menu-popup-cnt .z-menu-item-over{
	border:1px solid #A8D8EB;
	padding:0;
	background:#DDEEFB repeat-x scroll 0 0;
	background-image:url(${c:encodeURL('~./zul/img/menu/item-over.png')});
}
.z-menu-popup-cnt .z-menu-over a.z-menu-cnt,
.z-menu-popup-cnt .z-menu-item-over a.z-menu-item-cnt{
	color:#233D6D;
}

/*define separator*/
.z-menu-popup-cnt .z-menu-separator {
	font-size:1px;
	line-height:1px;
}
.z-menu-popup-cnt .z-menu-separator-inner {
	background-color:#E0E0E0;
	border-bottom:1px solid #FFFFFF;
	display:block;
	font-size:1px;
	line-height:1px;
	margin:2px 3px;
	overflow:hidden;
	width:auto;
}