<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

/* define common font property*/
.z-menubar-hor .z-menu,.z-menubar-hor .z-menu-item,.z-menubar-hor .z-menu-btn,.z-menubar-hor .z-menu-item-btn,
.z-menubar-hor span,.z-menubar-hor a,.z-menubar-hor div,
.z-menubar-ver .z-menu,.z-menubar-ver .z-menu-item,.z-menubar-ver .z-menu-btn,.z-menubar-ver .z-menu-item-btn, 
.z-menubar-ver span,.z-menubar-ver a,.z-menubar-ver div {
	font-family:Verdana,Tahoma,Arial,Helvetica,sans-serif;
	font-size:11px;
	font-size-adjust:none;
	font-stretch:normal;
	font-style:normal;
	font-variant:normal;
	font-weight:normal;
	line-height:normal;
	white-space:nowrap;
}

/* define common horizontal and vertical property */ 
.z-menubar-hor,.z-menubar-ver {
	position : relative;
	display: block;	
	padding : 2px 0px;	
	border-bottom : 1px solid #A9BFD3;	
	background: #CEE7F5 repeat-x; 
	background-image: url(${c:encodeURL('~./zul/img/menu2/pp-bg.gif')}); 
}

.z-menubar-hor .z-menu, .z-menubar-hor .z-menu-item,
.z-menubar-ver .z-menu, .z-menubar-ver .z-menu-item {
	vertical-align:middle;
}

.z-menu-cnt, .z-menu-item-cnt {
	text-decoration: none; white-space: nowrap;  font-style: normal;
	font-family: ${fontFamilyT}; font-size: ${fontSizeMS};
}

.z-menubar-hor .z-menu-bdy, .z-menubar-hor .z-menu-item-bdy,
.z-menubar-ver .z-menu-bdy, .z-menubar-ver .z-menu-item-bdy {
	cursor: pointer;
}

.z-menubar-hor .z-menu-bdy .z-menu-inner-l,.z-menubar-hor .z-menu-bdy .z-menu-inner-r,
.z-menubar-hor .z-menu-item-bdy .z-menu-item-inner-l,.z-menubar-hor .z-menu-item-bdy .z-menu-item-inner-r,
.z-menubar-ver .z-menu-bdy .z-menu-inner-l,.z-menubar-ver .z-menu-bdy .z-menu-inner-r,
.z-menubar-ver .z-menu-item-bdy .z-menu-item-inner-l,.z-menubar-ver .z-menu-item-bdy .z-menu-item-inner-r{
	font-size:1px;
	height:21px;
	line-height:1px;
	width:3px;
}

.z-menubar-hor .z-menu-bdy .z-menu-inner, .z-menubar-hor .z-menu-item-bdy .z-menu-item-inner,
.z-menubar-ver .z-menu-bdy .z-menu-inner, .z-menubar-ver .z-menu-item-bdy .z-menu-item-inner{
	height:21px;
	text-align:center;
}

.z-menu-bdy .z-menu-inner div{
	background-color:transparent;
	background-position:right 3px;
	background-repeat:no-repeat;
	display:block;
	min-height:16px;
	padding-right:2px;
	padding-left:0px;
	background-image:url(${c:encodeURL('~./zul/img/button/tb-btn-arrow.png')});
}

.z-menu-item-bdy .z-menu-item-inner div{
	background-color:transparent;
	display:block;
	min-height:16px;
	padding-right:0px;
	padding-left:0px;
}

.z-menu-inner .z-menu-btn,
.z-menu-item-inner .z-menu-item-btn{
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

.z-menu-bdy-text-img .z-menu-inner .z-menu-btn,.z-menu-bdy-img .z-menu-inner .z-menu-btn,
.z-menu-item-bdy-text-img .z-menu-item-inner .z-menu-item-btn{
	padding-left:18px;
}

.z-menu-item-bdy-img .z-menu-item-inner .z-menu-item-btn,
.z-menu-item-bdy-text .z-menu-item-inner .z-menu-item-btn{
	padding-left:12px;
	padding-right:0px;
}


<c:if test="${c:isExplorer()}">
<%-- IE only --%>
.z-menu-inner .z-menu-btn{
	padding-right:4px;
}
.z-menu-item-bdy-img .z-menu-item-inner .z-menu-item-btn,
.z-menu-item-bdy-text .z-menu-item-inner .z-menu-item-btn{
	padding-right:0px;
}
</c:if>

/* define menu/menuitem mouse over and seld effect */
.z-menu-bdy-over .z-menu-inner-l,.z-menu-bdy-seld .z-menu-inner-l,.z-menu-item-bdy-over .z-menu-item-inner-l {
	background-repeat : no-repeat;
	background-position : 0 0;
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-side.png')});
}
.z-menu-bdy-over .z-menu-inner-r,.z-menu-bdy-seld .z-menu-inner-r,.z-menu-item-bdy-over .z-menu-item-inner-r {
	background-repeat : no-repeat; 
	background-position : 0 -21px;
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-side.png')});
}
.z-menu-bdy-over .z-menu-inner,.z-menu-bdy-seld .z-menu-inner,.z-menu-item-bdy-over .z-menu-item-inner {
	background-repeat : repeat-x; 
	background-position : 0 -42px;
	background-image : url(${c:encodeURL('~./zul/img/button/tb-btn-side.png')});
}
.z-menu-bdy-over .z-menu-inner .z-menu-btn,
.z-menu-bdy-seld .z-menu-inner .z-menu-btn,
.z-menu-item-bdy-over .z-menu-item-inner .z-menu-btn{
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
	background:#E7F3FA  repeat-y scroll 0 0;
	border:1px solid #7F9DB9;
	padding:2px;
	z-index:88000;
	background-image : url(${c:encodeURL('~./zul/img/menu2/pp-bg.gif')});
}
/* define menupopup property*/
.z-menu-popup {
	-x-system-font:none;
	font-family:Verdana,Tahoma,Arial,Helvetica,sans-serif;
	font-size:11px;
	font-size-adjust:none;
	font-stretch:normal;
	font-style:normal;
	font-variant:normal;
	font-weight:normal;
	line-height:normal;
	text-decoration:none;
	white-space:nowrap;
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
.z-menu-popup-cnt .z-menu-item {
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
	background-image:url(${c:encodeURL('~./zul/img/menu2/arrow.png')});
}

/*define checked menuitem effect in menupopup */
.z-menu-popup-cnt .z-menu-item-cnt-ck .z-menu-item-img {
	background:transparent no-repeat scroll center center;
	background-image:url(${c:encodeURL('~./zul/img/menu2/checked.gif')});
}

.z-menu-popup-cnt .z-menu-item-cnt-unck .z-menu-item-img {
	background:transparent no-repeat scroll center center;
	background-image:url(${c:encodeURL('~./zul/img/menu2/unchecked.gif')});
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
	background-image:url(${c:encodeURL('~./zul/img/menu2/item-over.gif')});
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