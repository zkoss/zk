<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- ZK Tabbox --%>
.z-tabbox{
	overflow:hidden;
	visibility: hidden;
}
.z-tabbox-ul a, .z-tabbox-ul em, .z-tabbox-ul span{
	display:block;
}

.z-tabbox-tabs .z-tabbox-space{
	background:#DEECFD none repeat scroll 0 0;
	border:1px solid #8DB2E3;
	border-top:0 none;
	font-size:1px;
	height:2px;
	line-height:1px;
}

.z-tabbox-tabs .z-tabbox-ul{
	background:transparent none repeat scroll 0 0 !important;
	padding-left:0px;
	list-style-image:none;
	list-style-position:outside;
	list-style-type:none;
	display:block;
	margin:0;
	border-bottom:1px solid #8DB2E3;
	width:100%;
	ZOOM: 1;
}

.z-tabbox-tabs{
	overflow:hidden;
	border:1px solid #8DB2E3;
	background:transparent none repeat scroll 0 0 !important;
	border-width:0 !important;
	padding:0 !important;
	margin:0 !important;
	position:relative;
}
.z-tabbox-header{
	margin:0px;
	width:100%;
    overflow:hidden;
    position:relative;
    zoom:1;
}

.z-tabbox-scrolltabs{
	zoom:1;
	margin:0;
	overflow:hidden;
	border:1px solid #8DB2E3;
	background:#DEECFD none repeat scroll 0 0;
	position:relative;
	padding-bottom:2px;
}

.z-tabbox-scrolltabs .z-tabbox-ul{
	background:#CEDFF5 url(${c:encodeURL('~./zul/img/tab2/tab-strip-bg.png')}) repeat-x scroll center bottom;
	padding-left:0px;
	padding-top:1px;
	list-style-image:none;
	list-style-position:outside;
	list-style-type:none;
	display:block;
	margin:0;

	ZOOM: 1;
	border-bottom:1px solid #8DB2E3;
	-moz-user-select:none;
	-khtml-user-select:none;
}

.z-tabbox-ul li{
	position:relative;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	cursor:default;
	display:block;
	float:left;
	padding: 0px;
	margin:0 0 0 2px;
	-moz-user-select:none;
	-khtml-user-select:none;
}
.z-tabbox-ul a{
	text-decoration:none;
}

.z-tabbox-close {
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-close.png')});
	background-repeat:no-repeat;
	cursor:pointer;
	display:block;
	height:11px;
	opacity:0.6;
	position:absolute;
	right:3px;
	top:3px;
	width:11px;
	z-index:2;
}
.z-tabbox-a{
	position:relative;
	padding-left:10px;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tabs-sprite.png')});
	background-repeat: no-repeat; 
	-moz-outline: none;
	outline: none;
	cursor:pointer;
}

.z-tabbox-em{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/tabs-sprite.png')}) no-repeat scroll right -351px;
	padding-right:10px;
	display:block;
}
.z-tabbox-inner{
	padding-left:2px;
	padding-right:1px;
	overflow:hidden;
	cursor:pointer;
	background:transparent url(${c:encodeURL('~./zul/img/tab2/tabs-sprite.png')}) repeat-x scroll 0 -201px;
}
.z-tabbox-innerclose {
	padding-right:10px;
}

.z-tabbox-edge{
	float:left;
    margin:0 !important;
    padding:0 !important;
    border:0 none !important;
    font-size:1px !important;
    line-height:1px !important;
    overflow:hidden;
    zoom:1;
    background:transparent !important;
    width:1px;
}

.z-tab .z-tabbox-a{
	background-position:0 -51px;
}
.z-tab .z-tabbox-a:hover{
	background-position:0 -101px;
}
.z-tab .z-tabbox-a:hover .z-tabbox-em{
	background-position:right -401px;
}
.z-tab .z-tabbox-a:hover .z-tabbox-inner{
	background-position:right -251px;
}
.z-tab .z-tabbox-a:hover .z-tabbox-text{
	color:#15428B;
}
.z-tab .z-tabbox-text{
	color:#416AA3;
	cursor:pointer;
	font-style:normal;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	font-size-adjust:none;
	padding:4px 0 4px;
	white-space:nowrap;
}
.z-tab-seld .z-tabbox-a{
	cursor:default;
	background-position:0 0px;
	margin:auto auto -1px;
}
.z-tab-seld .z-tabbox-inner{
	cursor:default;
	background-position:0 -151px;
	hasLayout:-1;
	overflow: hidden;
	text-overflow: ellipsis;
}
.z-tab-seld .z-tabbox-close{
	opacity:0.8;
}
.z-tab-seld .z-tabbox-em{
	background-position:right -301px;
}
.z-tab-seld .z-tabbox-text{
	color:#15428B;
	cursor:default;
	font-weight:bold;
	font-style:normal;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	white-space:nowrap;
	padding:4px 0 5px;
}
<c:if test="${c:isExplorer()}">
.z-tab-seld .z-tabbox-em{
	position:relative;
}
</c:if>
.z-tabbox-close:hover{
	opacity:1;
}
.z-tab-disd a,.z-tab-disd-seld a {
	color:gray;
	cursor:default;
	opacity:0.6;
}
.z-tab-disd .z-tabbox-a,.z-tab-disd-seld .z-tabbox-a {
	background-position:0pt -51px;
}
.z-tab-disd .z-tabbox-em,.z-tab-disd-seld .z-tabbox-em{
	background-position:right -351px;
}
.z-tab-disd .z-tabbox-close:hover , .z-tab-disd-seld .z-tabbox-close:hover{
	opacity:0.6;
} 
.z-tab-disd .z-tabbox-text,.z-tab-disd-seld .z-tabbox-text{
	cursor:default;
	font-style:normal;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	white-space:nowrap;
	padding:4px 0 4px;
}
.z-tab-disd .z-tabbox-inner , .z-tab-disd-seld .z-tabbox-inner{
	background-position:right -201px;
	cursor:default;
}
.z-tabbox-scrolling .z-tabbox-header{
	margin-left: 18px;
	margin-right: 18px;
}

.z-tabbox-scrolling .z-tabbox-scrollright{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/scroll-right.png')}) no-repeat scroll 0 0;
	border-bottom:1px solid #8DB2E3;
	cursor:pointer;
	position:absolute;
	right:0;
	top:0;
	width:18px;
	z-index:10;
	height:25px;
}

.z-tabbox-scrolling .z-tabbox-scrollright:hover{
	background-position:-18px 0;
}

.z-tabbox-scrolling .z-tabbox-scrollleft{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/scroll-left.png')}) no-repeat scroll -18px 0;
	border-bottom:1px solid #8DB2E3;
	cursor:pointer;
	left:0;
	position:absolute;
	top:0;
	width:18px;
	z-index:10;
	height:25px;
}

.z-tabbox-scrolling .z-tabbox-scrollleft:hover{
	background-position:0px 0;
}
<%-- Tabbox vertical --%>
.z-tabbox-v-tabs{
	overflow:hidden;
	position:relative;
	float:left;
	background:#DEECFD none repeat scroll 0 0;
	border-top:1px solid #8DB2E3;
	border-bottom:1px solid #8DB2E3;
	border-left:1px solid #8DB2E3;
}
.z-tabbox-v-header{
	background:#CEDFF5 url(${c:encodeURL('~./zul/img/tab2/tab-vstrip-bg.gif')}) repeat-y scroll center bottom;
	zoom:1;
	overflow:hidden;
	position:relative;
}
.z-tabbox-v-edge{
    margin:0 !important;
    padding:0 !important;
    border:0 none !important;
    font-size:1px !important;
    line-height:1px !important;
    overflow:hidden;
    zoom:1;
    background:transparent !important;
    height:1px;
}
.z-tabbox-v-si {
	padding-top:0px;
	padding-left:1px;
	list-style-image:none;
	list-style-position:outside;
	list-style-type:none;
	display:block;
	margin:0;
	ZOOM: 1;
	height:3456px;
	border-right:1px solid #8DB2E3;
	-moz-user-select:none;
	-khtml-user-select:none;
}
.z-tabbox-v-si li{
	position:relative;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	cursor:default;
	display:block;
	padding: 2px 0 0 0 ;
	margin:0 0 0 0;
	-moz-user-select:none;
	-khtml-user-select:none;
}
.z-tabbox-v-close{
	position:absolute;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-close.png')});
	background-repeat:no-repeat;
	opacity:0.6;
	cursor:pointer;
	display:block;
	height:11px;
	width:11px;
	right:1px;
	top:15px;
	z-index:2;
}
.z-tabbox-v-close:hover{
	opacity:1;
}
.z-tabbox-v-si .z-tab-seld .z-tabbox-v-close{
	opacity:0.8;
}
.z-tab-disd .z-tabbox-v-close:hover, .z-tab-disd-seld  .z-tabbox-v-close:hover{
	opacity:0.8;
}
.z-tabbox-v-noclose{
	position:absolute;
	opacity:0.6;
	cursor:pointer;
	display:block;
	height:11px;
	width:11px;
	right:1px;
	top:15px;
	z-index:2;
}
.z-tabbox-v-a, .z-tabbox-v-em,.z-tabbox-v-inner,.z-tabbox-v-text{
	display:block;
}
.z-tabbox-v-a{
	position:relative;
	padding-top:8px;
	-moz-outline: none;
	outline: none;
	zoom:1;
}
.z-tabbox-v-si .z-tab .z-tabbox-v-a:hover{
	background-position:-1201px 0;
}
.z-tabbox-v-si .z-tab .z-tabbox-v-a{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/tabs-vsprite.png')}) no-repeat scroll -1051px 0;
	cursor:pointer;
}
.z-tabbox-v-si .z-tab-seld .z-tabbox-v-a{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/tabs-vsprite.png')}) no-repeat scroll -901px 0;
	margin-right:-2px;
	cursor:default;
}
.z-tab-disd .z-tabbox-v-a, .z-tab-disd-seld  .z-tabbox-v-a{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/tabs-vsprite.png')}) no-repeat scroll -1051px 0;
}
.z-tabbox-v-em{
	padding-bottom:10px;
}
.z-tabbox-v-si .z-tab .z-tabbox-v-a:hover .z-tabbox-v-em{
	background-position:-301px bottom;
}
.z-tabbox-v-si .z-tab .z-tabbox-v-em{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/tabs-vsprite.png')}) no-repeat scroll -151px bottom;
}
.z-tabbox-v-si .z-tab-seld .z-tabbox-v-em{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/tabs-vsprite.png')}) no-repeat scroll 0 bottom;
}
.z-tab-disd .z-tabbox-v-em, .z-tab-disd-seld  .z-tabbox-v-em{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/tabs-vsprite.png')}) no-repeat scroll -151px bottom;
}
.z-tabbox-v-inner{
	overflow:hidden;
	cursor:pointer;
	padding: 2px 14px 0 4px;
}
.z-tabbox-v-innerclose{
}
.z-tabbox-v-si .z-tab .z-tabbox-v-a:hover .z-tabbox-v-inner{
	background-position:-751px 0;
}
.z-tabbox-v-si .z-tab .z-tabbox-v-inner{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/tabs-vsprite.png')}) repeat-y scroll -601px 0;
}
.z-tabbox-v-si .z-tab-seld .z-tabbox-v-inner{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/tabs-vsprite.png')}) repeat-y scroll -451px 0;
}
.z-tab-disd .z-tabbox-v-inner, .z-tab-disd-seld  .z-tabbox-v-inner{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/tabs-vsprite.png')}) repeat-y scroll -601px 0;
}
.z-tabbox-v-text{
	font-style:normal;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	text-align:center;
	font-style:normal;
	white-space:nowrap;
}
.z-tabbox-v-si .z-tab .z-tabbox-v-text{
	color:#416AA3;
	cursor:pointer;
	font-size-adjust:none;
}
.z-tabbox-v-si .z-tab-seld .z-tabbox-v-text{
	color:#15428B;
	cursor:default;
	font-weight:bold;
	white-space:nowrap;
}
.z-tabbox-v-space{
	float:left;
	background:#DEECFD none repeat scroll 0 0;
	border:1px solid #8DB2E3;
	border-left:0 none;
	font-size:1px;
	width:2px;
	position:relative;
	<%-- DO NOT Delete this line--%>
}
.z-tabbox-scrolling .z-tabbox-v-tabs{
}
.z-tabbox-scrolling .z-tabbox-v-header{
	margin-top:18px;
	margin-bottom:18px;
}
.z-tabbox-scrolling .z-tabbox-v-scrollup{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/scroll-up.png')}) no-repeat scroll 1px -18px;
	border-right:1px solid #8DB2E3;
	cursor:pointer;
	height:18px;
	position:absolute;
	right:0;
	top:0;
	z-index:10;
	width:100%;
	display:block;
}
.z-tabbox-scrolling .z-tabbox-v-scrolldown{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/scroll-down.png')}) no-repeat scroll 1px 0;
	border-right:1px solid #8DB2E3;
	cursor:pointer;
	height:18px;
	position:absolute;
	right:0;
	bottom:0;
	z-index:10;;
	width:100%;
	display:block;
}
.z-tabbox-scrolling .z-tabbox-v-scrollup:hover{
	background-position:1px 0px;
}
.z-tabbox-scrolling .z-tabbox-v-scrolldown:hover{
	background-position:1px -18px;
}
<%-- Tabpanels Tabpanel --%>
div.z-tabpanel {<%-- horz, accd: tabpanel --%>
	border-left: 1px solid #8DB2E3; 
	border-right: 1px solid #8DB2E3; 
	border-bottom: 1px solid #8DB2E3; padding: 5px;
	zoom: 1;
}
div.z-vtabpanels {<%-- vert tabpanels --%>
	overflow:hidden;
	border-top: 1px solid #8DB2E3; 
	border-right: 1px solid #8DB2E3; 
	border-bottom: 1px solid #8DB2E3;
}
div.z-vtabpanel {<%-- vert tabpanel --%>
	padding: 5px;
	zoom: 1;
}

<%-- Tabbox accordion --%>

.z-tabbox-accd{
	position:relative;
}
.z-tabbox-accd-header{
	cursor:pointer;
	position:relative;
	zoom:1;
}
.z-tabbox-accd-left-tr {
	text-decoration:none;
	padding-left:6px;
	line-height:0;
	display:block;
	zoom:1;
}
.z-tabbox-accd-right-tr {
	padding-right:6px;
	display:block;
}
.z-tabbox-accd-inner{
	display:block;
	padding:7px 0 6px 0;
	overflow:hidden;
}
.z-tabbox-accd-text{
	cursor:pointer;
	color:#373737;
	font-family: ${fontFamilyT};
	font-style:normal;
	font-variant:normal;
	font-weight:bold;
	font-size: ${fontSizeM};
	line-height:normal;
	text-decoration:none;
	padding-right:30px;
}

.z-tabbox-accd .z-tabpanel-accordion{
	border-bottom:1px solid #ABD6EE;
	border-left:1px solid #ABD6EE;
	border-right:1px solid #ABD6EE;
	padding:5px;
	zoom : 1;
}
.z-tabbox-accd .z-tab .z-tabbox-accd-left-tr{
	text-decoration:none;
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accd-border.png')}) no-repeat scroll 0 0;
}
.z-tabbox-accd .z-tab .z-tabbox-accd-right-tr {
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accd-border.png')}) no-repeat scroll right 0;
}
.z-tabbox-accd .z-tab .z-tabbox-accd-inner{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accd-inner.png')}) repeat-x scroll 0 0;
}

.z-tabbox-accd .z-tab-disd .z-tabbox-accd-left-tr  , .z-tabbox-accd  .z-tab-disd-seld  .z-tabbox-accd-left-tr{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accd-border.png')}) no-repeat scroll 0 0;
}
.z-tabbox-accd .z-tab-disd .z-tabbox-accd-right-tr  , .z-tabbox-accd  .z-tab-disd-seld  .z-tabbox-accd-right-tr{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accd-border.png')}) no-repeat scroll right 0;
}
.z-tabbox-accd .z-tab-disd .z-tabbox-accd-inner  , .z-tabbox-accd  .z-tab-disd-seld  .z-tabbox-accd-inner{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accd-inner.png')}) repeat-x scroll 0 0;
}
.z-tabbox-accd .z-tab-disd .z-tabbox-accd-closebtn , .z-tabbox-accd  .z-tab-disd-seld  .z-tabbox-accd-closebtn{
	opacity:0.6;
}

.z-tabbox-accd .z-tab-disd .z-tabbox-accd-closebtn:hover ,
 .z-tabbox-accd  .z-tab-disd-seld  .closebtn:hover{
	opacity:0.8;
}
.z-tabbox-accd .z-tabbox-accd-closebtn{
	z-index:5;
	background-image: url(${c:encodeURL('~./zul/img/tab2/close-off.png')});
	background-repeat:no-repeat;
	cursor:pointer;
	width:17px;
	height:16px;
	position:absolute;
	right:10px;
	top:5px;
	z-index:2;
}
.z-tabbox-accd .z-tab .z-tabbox-accd-closebtn{
	opacity:0.6;
}

.z-tabbox-accd .z-tab-seld .z-tabbox-accd-left-tr{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accd-border.png')}) no-repeat scroll 0 0;
}
.z-tabbox-accd .z-tab-seld .z-tabbox-accd-right-tr{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accd-border.png')}) no-repeat scroll right 0;
}
.z-tabbox-accd .z-tab-seld .z-tabbox-accd-inner{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accd-inner.png')}) repeat-x scroll 0 0;
}
.z-tabbox-accd .z-tab-seld .z-tabbox-accd-closebtn{
	opacity:0.8;
}
.z-tabbox-accd .z-tabbox-accd-closebtn:hover{
	background-image: url(${c:encodeURL('~./zul/img/tab2/close-on.png')});
	opacity:1;
}


<%-- ZK Tabbox accordion-lite --%>
.z-tabpanels-accordion-lite{
	border-top:1px solid #99BBE8;
	border-right:1px solid #99BBE8;
	border-left:1px solid #99BBE8;
}
.z-tabbox-accdlite{
	position:relative;
}
.z-tabbox-accdlite .z-tabbox-accdlite-header{
	overflow:hidden;
	zoom:1;
	cursor:pointer;
	position:relative;
	border: 1px solid #99BBE8;
	border-top-width:0;
	border-right-width:0;
	border-left-width:0;
}
.z-tabbox-accdlite .z-tabbox-accdlite-left-tr {
	zoom:1;
	padding-left:6px;
	line-height:0;
	display:block;
	text-decoration:none;
}
.z-tabbox-accdlite .z-tabbox-accdlite-right-tr {
	padding-right:6px;
	display:block;
}
.z-tabbox-accdlite .z-tabbox-accdlite-inner{
	display:block;
	padding:4px 0 3px 0;
	overflow:hidden;
}
.z-tabbox-accdlite .z-tabbox-accdlite-text{
	cursor:pointer;
	color:#373737;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	font-style:normal;
	font-variant:normal;
	line-height:15px;
	text-decoration:none;
	padding-right:30px;
}
.z-tabbox-accdlite .z-tabpanel-accordion-lite{
	border-bottom:1px solid #99BBE8;
	padding:5px;
	zoom : 1;
}

.z-tabbox-accdlite .z-tab .z-tabbox-accdlite-left-tr{
	text-decoration:none;
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accdlite-all.png')}) repeat-x scroll 0 -9px;
}
.z-tabbox-accdlite .z-tab .z-tabbox-accdlite-right-tr {
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accdlite-all.png')}) repeat-x scroll 0 -9px;
}
.z-tabbox-accdlite .z-tab .z-tabbox-accdlite-inner{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accdlite-all.png')}) repeat-x scroll 0 -9px;
}


.z-tabbox-accdlite .z-tab-disd .z-tabbox-accdlite-left-tr  , .z-tabbox-accdlite  .z-tab-disd-seld  .z-tabbox-accdlite-left-tr{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accdlite-all.png')}) repeat-x scroll 0 -9px;
}
.z-tabbox-accdlite .z-tab-disd .z-tabbox-accdlite-right-tr  , .z-tabbox-accdlite  .z-tab-disd-seld  .z-tabbox-accdlite-right-tr{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accdlite-all.png')}) repeat-x scroll 0 -9px;
}
.z-tabbox-accdlite .z-tab-disd .z-tabbox-accdlite-inner  , .z-tabbox-accdlite  .z-tab-disd-seld  .z-tabbox-accdlite-inner{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accdlite-all.png')}) repeat-x scroll 0 -9px;
}
.z-tabbox-accdlite .z-tab-disd .z-tabbox-accdlite-closebtn , .z-tabbox-accdlite  .z-tab-disd-seld  .z-tabbox-accdlite-closebtn{
	opacity:0.6;
}

.z-tabbox-accdlite .z-tab-disd .z-tabbox-accdlite-closebtn:hover ,
.z-tabbox-accdlite  .z-tab-disd-seld  .closebtn:hover{
	opacity:0.6;
}
.z-tabbox-accdlite .z-tabbox-accdlite-closebtn{
	background-image: url(${c:encodeURL('~./zul/img/tab2/close-off.png')});
	background-repeat:no-repeat;
	cursor:pointer;
	height:16px;
	position:absolute;
	right:10px;
	top:3px;
	width:17px;
	z-index:5;
}
.z-tabbox-accdlite .z-tab .z-tabbox-accdlite-closebtn{
	opacity:0.6;
}

.z-tabbox-accdlite .z-tab-seld .z-tabbox-accdlite-left-tr{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accdlite-all.png')}) repeat-x scroll 0 -9px;
}
.z-tabbox-accdlite .z-tab-seld .z-tabbox-accdlite-right-tr{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accdlite-all.png')}) repeat-x scroll 0 -9px;
}
.z-tabbox-accdlite .z-tab-seld .z-tabbox-accdlite-inner{
	background:transparent url(${c:encodeURL('~./zul/img/tab2/accdlite-all.png')}) repeat-x scroll 0 -9px;
}
.z-tabbox-accdlite .z-tab-seld .z-tabbox-accdlite-closebtn{
	opacity:0.8;
}
.z-tabbox-accdlite .z-tabbox-accdlite-closebtn:hover {
	background-image: url(${c:encodeURL('~./zul/img/tab2/close-on-l.gif')});
	opacity:1;
}
