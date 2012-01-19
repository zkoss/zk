<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- define common font property --%>
.z-menubar-hor .z-menu,.z-menubar-hor .z-menuitem,.z-menubar-hor .z-menu-btn,.z-menubar-hor .z-menuitem-btn,
.z-menubar-hor span,.z-menubar-hor a,.z-menubar-hor div,
.z-menubar-ver .z-menu,.z-menubar-ver .z-menuitem,.z-menubar-ver .z-menu-btn,.z-menubar-ver .z-menuitem-btn,
.z-menubar-ver span,.z-menubar-ver a,.z-menubar-ver div,
.z-menu-cnt, .z-menuitem-cnt {
	font-weight:normal;
	white-space: nowrap;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	color: #636363;
}
<%-- define common horizontal and vertical property --%>
.z-menubar-hor,.z-menubar-ver {
	position : relative;
	display: block;
	padding : 2px 0;
	border-top : 1px solid #D8D8D8;
	border-bottom : 1px solid #D8D8D8;
	background: #F5F5F5 repeat-x 0 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/menu/menu-bg.png')});
}
.z-menubar-ver {
	background-color: #f5f5f5;
	background-image: none;
}
.z-menubar-ver button.z-menu-btn {
	text-align:left;
}
.z-menubar-hor .z-menu, .z-menubar-hor .z-menuitem,
.z-menubar-ver .z-menu, .z-menubar-ver .z-menuitem {
	vertical-align:middle;
}
.z-menu-cnt, .z-menuitem-cnt {
	text-decoration: none;
}
.z-menubar-hor .z-menu-body, .z-menubar-hor .z-menuitem-body,
.z-menubar-ver .z-menu-body, .z-menubar-ver .z-menuitem-body {
	cursor: pointer;
}

.z-menubar-hor .z-menu-body .z-menu-inner-l,.z-menubar-hor .z-menu-body .z-menu-inner-r,
.z-menubar-hor .z-menuitem-body .z-menuitem-inner-l,.z-menubar-hor .z-menuitem-body .z-menuitem-inner-r,
.z-menubar-ver .z-menu-body .z-menu-inner-l,.z-menubar-ver .z-menu-body .z-menu-inner-r,
.z-menubar-ver .z-menuitem-body .z-menuitem-inner-l,.z-menubar-ver .z-menuitem-body .z-menuitem-inner-r{
	font-size: 0;
	height: 21px;
	line-height: 0;
	width: 4px;
}
.z-menu-inner-l .z-menu-space,.z-menu-inner-r .z-menu-space,
.z-menuitem-inner-l .z-menuitem-space,.z-menuitem-inner-r .z-menuitem-space{
	display: block;
	width: 4px;
}

.z-menubar-hor .z-menu-body .z-menu-inner-m, .z-menubar-hor .z-menuitem-body .z-menuitem-inner-m,
.z-menubar-ver .z-menu-body .z-menu-inner-m, .z-menubar-ver .z-menuitem-body .z-menuitem-inner-m{
	height: 21px;
	text-align: center;
	padding-right: 5px;
}
.z-menu-body .z-menu-inner-m div,
.z-menubar-hor .z-menu-body-clk .z-menu-inner-m  div {
	display: block;
	min-height: 17px;
	padding-right: 3px;
	padding-left: 0;
	background: transparent no-repeat right -14px;
	background-image: url(${c:encodeThemeURL('~./zul/img/menu/btn-arrow.gif')});
}
.z-menubar-hor .z-menu-body-clk .z-menu-inner-m  div,
.z-menubar-ver .z-menu-body-clk .z-menu-inner-m  div {
	padding-right: 6px;
}
.z-menubar-ver .z-menu-inner-m div {
	background-position: right 0;
}
.z-menu-body .z-menu-inner-m .z-menu-btn {
	padding-right: 6px;
}
.z-menuitem-body .z-menuitem-inner-m div {
	background-color: transparent;
	display: block;
	min-height: 17px;
	padding-right:0;
	padding-left:0;
}
.z-menu-inner-m .z-menu-btn,
.z-menuitem-inner-m .z-menuitem-btn{
	background:transparent none no-repeat 0 2px;
	border:0 none;
	cursor:pointer;
	margin:0;
	min-height:13px;
	outline-color:-moz-use-text-color;
	outline-style:none;
	outline-width:0;
	overflow:visible;
	width:auto;
	padding-left:2px; <%--button has default padding, reset it--%>
	padding-top:2px;
	padding-bottom:2px;
	padding-right:1px;
	text-decoration: none;
}
.z-menu-body-text-img .z-menu-inner-m .z-menu-btn,.z-menu-body-img .z-menu-inner-m .z-menu-btn,
.z-menuitem-body-text-img .z-menuitem-inner-m .z-menuitem-btn{
	padding-left:18px;
}
.z-menuitem-body-img .z-menuitem-inner-m .z-menuitem-btn,
.z-menuitem-body-text .z-menuitem-inner-m .z-menuitem-btn{
	padding-left:12px;
	padding-right:0;
}
<c:if test="${zk.gecko > 0}">
.z-menu-inner-m button.z-menu-btn::-moz-focus-inner,
.z-menuitem-inner-m button.z-menuitem-btn::-moz-focus-inner {
	border: 0;
}
</c:if>
<c:if test="${zk.ie > 0}">
.z-menu-inner-m .z-menu-btn{
	padding-right:4px;
}
.z-menuitem-body-img .z-menuitem-inner-m .z-menuitem-btn,
.z-menuitem-body-text .z-menuitem-inner-m .z-menuitem-btn{
	padding-right:0;
}
</c:if>

<%-- define menu/menuitem mouse over and seld effect --%>
<%-- over effect --%>
.z-menu-body-over .z-menu-inner-l,
.z-menuitem-body-over .z-menuitem-inner-l,
.z-menu-body-over .z-menu-inner-r,
.z-menuitem-body-over .z-menuitem-inner-r {
	background-repeat : no-repeat;
	background-position : 0 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/menu/menu-btn.png')});
}
.z-menu-body-over .z-menu-inner-r,
.z-menuitem-body-over .z-menuitem-inner-r {
	background-position : 0 -40px;
}
.z-menu-body-over .z-menu-inner-m,
.z-menuitem-body-over .z-menuitem-inner-m {
	background-repeat : repeat-x;
	background-position : 0 -80px;
	background-image: url(${c:encodeThemeURL('~./zul/img/menu/menu-btn.png')});
	overflow: hidden;
}
.z-menu-body-over .z-menu-inner-m .z-menu-btn,
.z-menuitem-body-over .z-menuitem-inner-m .z-menu-btn{
	color:#233D6D;
}
.z-menubar-hor .z-menu-body-over .z-menu-inner-m  div {
	background: transparent no-repeat right -14px;
	background-image: url(${c:encodeThemeURL('~./zul/img/menu/btn-arrow.gif')});
}
.z-menubar-ver .z-menu-body-over .z-menu-inner-m  div {
	background-image: url(${c:encodeThemeURL('~./zul/img/menu/btn-arrow.gif')});
}
.z-menubar-hor .z-menu-body-clk-over .z-menu-inner-m  div {
	background: transparent no-repeat right 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/menu/btn-menu-hor-over.gif')});
}
.z-menubar-ver .z-menu-body-clk-over .z-menu-inner-m  div {
	background: transparent no-repeat right 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/menu/btn-menu-ver-over.gif')});
}
<%-- seld effect --%>
.z-menu-body-seld .z-menu-inner-l,
.z-menu-body-seld .z-menu-inner-r {
	background:  url(${c:encodeThemeURL('~./zul/img/menu/menu-btn.png')}) no-repeat transparent 0 -120px;
}
.z-menu-body-seld .z-menu-inner-r {
	background-position : 0 -160px;
}
.z-menu-body-seld .z-menu-inner-m {
	background-position : 0 -200px;
	background-image: url(${c:encodeThemeURL('~./zul/img/menu/menu-btn.png')});
}
.z-menu-body-seld .z-menu-inner-m .z-menu-btn {
	color:#233D6D;
}
<%--define disabled menuitem effect--%>
.z-menubar-hor .z-menuitem-disd *, .z-menubar-ver .z-menuitem-disd *{
	color:gray !important;
	cursor:default !important;
}
.z-menubar-hor .z-menuitem-disd .z-menuitem-btn,
.z-menubar-ver .z-menuitem-disd .z-menuitem-btn,
.z-menupopup-cnt .z-menu-disd .z-menu-img,
.z-menupopup-cnt .z-menuitem-disd .z-menuitem-img{
	opacity: .5;
	-moz-opacity: .5;
	filter: alpha(opacity=50);
}
<%-- define menupopup effect --%>
.z-menupopup-shadow, .z-menu-palette-pp, .z-menu-picker-pp, .z-menu-cnt-pp {
	box-shadow: 1px 1px 2px rgba(0, 0, 0, 0.2);
	-moz-box-shadow: 1px 1px 2px rgba(0, 0, 0, 0.2);
	-webkit-box-shadow: 1px 1px 2px rgba(0, 0, 0, 0.2);
}
.z-menu-cnt-body {
	background-color:#FFFFFF;
}
<%-- define menupopup property--%>
.z-menupopup {
	background:none;
	background-color:#FAFAFA;
	border:1px solid #c2c2c2;
	padding:1px;
	z-index:88000;
	left: 0;
	top: 0;
	font-style:normal;
	font-variant:normal;
	font-weight:normal;
	text-decoration:none;
	white-space:nowrap;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeMS};
}
.z-menupopup a {
	text-decoration:none !important;
}
.z-menupopup .z-menupopup-cnt{
	background:transparent none repeat 0 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/menu/pp-bg.png')});
	border:0 none;
	padding:0;
	margin:0 !important;
	overflow:hidden;
}
<%-- define menu & menuitem in menupopup --%>
.z-menupopup-cnt .z-menu,
.z-menupopup-cnt .z-menuitem,
.z-menupopup-cnt .z-menuseparator {
	line-height:100%;
	list-style-image:none !important;
	list-style-position:outside !important;
	list-style-type:none !important;
	margin:0 !important;
	display:block;
	padding:1px;
	cursor:pointer;
}
.z-menupopup-cnt .z-menu a.z-menu-cnt,
.z-menupopup-cnt .z-menuitem a.z-menuitem-cnt {
	color:#222222;
	display:block;
	line-height:17px;
	<c:if test="${zk.ie == 7}">
		max-height:17px;
	</c:if>
	outline-color:-moz-use-text-color;
	outline-style:none;
	outline-width:0;
	padding:2px 21px 2px 3px;
	white-space:nowrap;
}
.z-menupopup-cnt .z-menu .z-menu-img,
.z-menupopup-cnt .z-menuitem .z-menuitem-img {
	background-position:center center;
	border:0;
	height: 100%;
	display:-moz-inline-box;
	vertical-align: top;
	display: inline-block;
	min-height: 16px;
	margin-right:9px;
	padding:0;
	vertical-align:top;
	width:16px;
}
.z-menupopup .z-menuitem-cnt .z-menuitem-img {
	margin-left: -6px;
	margin-right:14px;	
}
.z-menupopup-cnt .z-menu .z-menu-cnt-img {
	background:transparent no-repeat right center;
	background-image: url(${c:encodeThemeURL('~./zul/img/menu/arrow.gif')});
}
<%--define checked menuitem effect in menupopup --%>
.z-menupopup-cnt .z-menuitem-cnt-ck .z-menuitem-img {
	background:transparent no-repeat center center;
	background-image: url(${c:encodeThemeURL('~./zul/img/menu/checked.gif')});
}
.z-menupopup-cnt .z-menuitem-cnt-unck .z-menuitem-img {
	background:transparent no-repeat center center;
	background-image: url(${c:encodeThemeURL('~./zul/img/menu/unchecked.gif')});
}
<%--define disabled menuitem effect in menupopup--%>
.z-menupopup-cnt .z-menuitem-disd,
.z-menupopup-cnt .z-menuitem-disd *{
	color:gray !important;
	cursor:default !important;
}
<%--define menupopup--%>
.z-menupopup-cnt .z-menuitem .z-menuitem-cl,
.z-menupopup-cnt .z-menu .z-menu-cl {
	padding-left: 4px;
}
.z-menupopup-cnt .z-menuitem .z-menuitem-cr,
.z-menupopup-cnt .z-menu .z-menu-cr {
	padding-right: 4px;
}
<%--define mouse over effect in menupopup--%>
.z-menupopup-cnt .z-menu-over,
.z-menupopup-cnt .z-menuitem-over{
	border:0;
	padding:1px;
	background:none;
}
.z-menupopup-cnt .z-menu-over a.z-menu-cnt,
.z-menupopup-cnt .z-menuitem-over a.z-menuitem-cnt{
	color:#233D6D;
}
.z-menupopup-cnt .z-menuitem-over .z-menuitem-cl,
.z-menupopup-cnt .z-menuitem-over .z-menuitem-cr,
.z-menupopup-cnt .z-menuitem-over .z-menuitem-cm,
.z-menupopup-cnt .z-menu-over .z-menu-cl,
.z-menupopup-cnt .z-menu-over .z-menu-cr,
.z-menupopup-cnt .z-menu-over .z-menu-cm {
	background-repeat : no-repeat;
	background-position : 0 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/menu/menu-btn.png')});
}

.z-menupopup-cnt .z-menuitem-over .z-menuitem-cl {
	padding-left: 4px;
}

.z-menupopup-cnt .z-menuitem-over .z-menuitem-cr,
.z-menupopup-cnt .z-menu-over .z-menu-cr {
	background-position : right -40px;
	padding-right: 4px;
}

.z-menupopup-cnt .z-menuitem-over .z-menuitem-cm,
.z-menupopup-cnt .z-menu-over .z-menu-cm {
	background-position : 0 -80px;
	background-repeat : repeat-x;
}
.z-menupopup-cnt .z-menuitem-over .z-menuitem-cm div {
	height: 21px;
}
<%--define separator--%>
.z-menupopup-cnt .z-menuseparator {
	font-size:1px;
	line-height:1px;
}
.z-menubar-hor .z-menuseparator {
	background-image: url(${c:encodeURL('~./img/dot.gif')});
	background-position: top center; background-repeat: repeat-y;
}
.z-menubar-ver .z-menuseparator {
	background-image: url(${c:encodeURL('~./img/dot.gif')});
	background-position: center left; background-repeat: repeat-x;
}
.z-menupopup-cnt .z-menuseparator-inner {
	background-color:#E0E0E0;
	border-bottom:1px solid #FFFFFF;
	display:block;
	font-size:1px;
	line-height:1px;
	margin:2px 3px;
	width:auto;
	position:relative;
	left: 25px;
	margin-right: 23px;
	<c:if test="${zk.safari > 0}">
	height: 1px;
	</c:if>
}
<%--define menubar hor scroll--%>
.z-menubar-hor-scroll {
	overflow: hidden;
}
.z-menubar-hor-body {
	margin: 0;
	width: 100%;
}
.z-menubar-hor-body-scroll {
	position: relative;
	overflow: hidden;
	margin-left: 20px;
	margin-right : 20px;
}
.z-menubar-hor-cnt {
	width: 5000px;
}
.z-menubar-hor-left, .z-menubar-hor-right {
	width: 18px;
	position:absolute;
}
.z-menubar-hor-left-scroll, .z-menubar-hor-right-scroll{
	top: 0;
	width: 25px;
	height: 64px;
	cursor: pointer;
	position: absolute;
	z-index: 25;
}
.z-menubar-hor-left-scroll {
	left: 0;
	background: transparent no-repeat 0 -1px;
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/scroll-l.png')});
}
.z-menubar-hor-left-scroll-over {
	background-position: -25px -1px;
}
.z-menubar-hor-right-scroll {
	right: 0;
	background: transparent no-repeat -25px -1px;
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/scroll-r.png')});
}
.z-menubar-hor-right-scroll-over {
	background-position: 0 -1px;
}
<c:if test="${zk.ie < 8}">
a.z-menuitem-cnt:visited,
a.z-menuitem-cnt {
	color: black;
}
<%-- Fixed the text in menu doesn't align with IE6 and IE7 --%>
.z-menu-inner-m .z-menu-btn,
.z-menuitem-inner-m .z-menuitem-btn{
	padding-top:0px;
	padding-right:0px;
}
.z-menupopup-cnt li.z-menuitem {
	position: relative; <%-- B50-ZK-282 --%>
}
</c:if>
.z-menupopup-cnt li.z-menuitem {
	padding: 2px;
}