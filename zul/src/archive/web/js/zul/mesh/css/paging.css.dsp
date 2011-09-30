<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- os mold --%>
.z-paging-os {
	background: #F9F9F9;
	padding: 6px 5px 6px 10px; zoom:1;
	position: relative;
}
.z-paging-os .z-paging-os-cnt {
	background: none;
	border: 0;
	font-size: ${fontSizeS};
	color: #0076a3; 
	font-weight: normal;
	text-decoration: none;
	font-family: arial;
	font-size: 11px;
	padding: 2px 2px 2px 4px;
	position: relative;
	top: -7px;
}
.z-paging-os .z-paging-os-cnt:hover {
	color: #0076A3;
	background-position: -24px 0;
	text-decoration: underline;
}
.z-paging-os .z-paging-os-seld {
	font-family:arial;
	font-size: 11px; 
	color: #363636; 
	font-weight: bold;
	background-image:none;
	background-position: right 0;
}
.z-paging-os .z-paging-os-seld:hover {
	color: #403E39;
}
.z-paging-os span {
	font-family: Arial;
	font-size: 11px; 
	color: #363636; 
	font-weight: normal;
	position:absolute;
	right: 8px;
	top: 10px;
}

.z-paging-os-btn,
.z-paging-os-btn-seld {
	height: 24px;
	width: 24px;
	display: inline;
}
.z-paging-os-btn-seld {
	background-position: right 0;
	color: #363636;
}
.z-paging-os .z-paging-os-cnt-label {
	background-image: none;
	color: #0076a3;
}
div.z-paging-os-cnt-l .z-paging-os-cnt,
div.z-paging-os-cnt-seld-l .z-paging-os-cnt {
	position: relative;
	top: 5px;
	left: -2px;
}
div.z-paging-os-cnt-l,
div.z-paging-os-cnt-r,
div.z-paging-os-cnt-m,
div.z-paging-os-cnt-seld-l,
div.z-paging-os-cnt-seld-r,
div.z-paging-os-cnt-seld-m,
div.z-paging-os-cnt-l-over {
	display: inline-block;
	*display: inline;
	background-image:url(${c:encodeURL('~./zul/img/paging/paging-btn.png')});
	background-position: 0 0;
	zoom: 1;
}
.z-paging-os div.z-paging-os-cnt-l .z-paging-os-cnt,
.z-paging-os div.z-paging-os-cnt-l .z-paging-os-cnt:hover,
.z-paging-os div.z-paging-os-cnt-seld-l .z-paging-os-cnt,
.z-paging-os div.z-paging-os-cnt-seld-l .z-paging-os-cnt:hover{ 
	font-weight: normal;
	text-decoration: none;
}
div.z-paging-os-cnt-l,
div.z-paging-os-cnt-seld-l {
	padding-left: 5px;
	background-repeat: no-repeat;
}
div.z-paging-os-cnt-r,
div.z-paging-os-cnt-seld-r {
	padding-right: 5px;
	background-position: right -24px;
	background-repeat: no-repeat;
}
div.z-paging-os-cnt-m {
	background-position: 0 -48px;
	height: 22px;
	padding: 1px 0 1px 2px;
	overflow:hidden;
}

div.z-paging-os-cnt-seld-l {
	padding-left: 5px;
	background-position: 0 -144px;
	background-repeat: no-repeat;
}
div.z-paging-os-cnt-seld-r {
	padding-right: 5px;
	background-position: right -168px;
	background-repeat: no-repeat;
}
div.z-paging-os-cnt-seld-m {
	background-position: 0 -192px;
	height: 22px;
	padding: 1px 0 1px 2px;
	overflow:hidden;
}

div.z-paging-os-cnt-l-over {
	padding-left: 5px;
	background-position: 0 -72px;
}
div.z-paging-os-cnt-l-over  div.z-paging-os-cnt-r {
	background-position: right -96px;
}
div.z-paging-os-cnt-l-over  div.z-paging-os-cnt-m {
	background-position: 0 -120px;
}
<%-- default mold --%>

<%-- font properties --%>
.z-paging td,
.z-paging span,
.z-paging input,
.z-paging div,
.z-paging button{
	font-weight:normal;
	white-space:nowrap;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeMS};
}

.z-paging {
	border-color:#CFCFCF;
	border-style:solid;
	border-width:0 0 1px;
	display:block;
	padding:6px 2px 6px 10px;
	position:relative;
	background:#F9F9F9;
	background-image: none;
}

.z-paging td {
	vertical-align:middle;
}

.z-paging-text {
	padding-left: 4px;
}

.z-paging .z-paging-btn {
	cursor:pointer;
	font-weight:normal;
	white-space:nowrap;
	width:auto;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeMS};
}

.z-paging .z-paging-btn {
	background:#F9F9F9 none repeat 0 0;
	cursor:pointer;
	text-align:center;
	vertical-align:middle;
	white-space:nowrap;
}

.z-paging-btn .z-paging-first,
.z-paging-btn .z-paging-last,
.z-paging-btn .z-paging-next,
.z-paging-btn .z-paging-prev {
	background-repeat:no-repeat;
	cursor:pointer;
	height:24px;
	width:24px;
	padding:0;
	white-space:nowrap;
}

<%--define icon of button--%>
.z-paging-btn button {
	background:transparent none repeat 0 0;
	border:0;
	padding:0;
	cursor:pointer;
	margin:0;
	min-height:13px;
	outline-color:-moz-use-text-color;
	outline-style:none;
	outline-width:0;
	overflow:visible;
	padding-left:0;
	padding-right:0;
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
	background-image:none;
}
.z-paging-inp,
.z-paging input.z-paging-inp {
	border:1px solid #CFCFCF;
	height: 20px;
	line-height: 20px;
	font-family: arial;
	font-size: 12px;
	color: #363636;
}
.z-paging-btn .z-paging-next {
	background-position:0 0;
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-btn2.png')});
}
.z-paging-btn .z-paging-prev {
	background-position:0 -24px;
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-btn2.png')});
}
.z-paging-btn .z-paging-last {
	background-position:0 -48px;
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-btn2.png')});
}
.z-paging-btn .z-paging-first {
	background-position:0 -72px;
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-btn2.png')});
}



<%--mouse over a button --%>
.z-paging-btn-over .z-paging-next {
	background-position:-24px 0;
}
.z-paging-btn-over .z-paging-prev {
	background-position:-24px -24px;
}
.z-paging-btn-over .z-paging-last {
	background-position:-24px -48px;
}
.z-paging-btn-over .z-paging-first {
	background-position:-24px -72px;
}
<%-- mouse click on a button --%>
.z-paging-btn-clk .z-paging-next {
	background-position:-48px 0;
}
.z-paging-btn-clk .z-paging-prev {
	background-position:-48px -24px;
}
.z-paging-btn-clk .z-paging-last {
	background-position:-48px -48px;
}
.z-paging-btn-clk .z-paging-first {
	background-position:-48px -72px;
}

<%-- disabled button --%>
.z-paging .z-paging-btn-disd {
	color:gray;
	cursor:default;
	opacity:0.4;
	-moz-opacity:0.4;
	filter: alpha(opacity=40);
}
.z-paging .z-paging-btn-disd *{
	color:gray !important;
	cursor:default !important;
}

<%--paging info--%>
.z-paging-info,
.z-paging div.z-paging-info {
	color:#363636;
	position:absolute;
	right:8px;
	top:11px;
	font-family:Arial;
	font-size: 11px;
}
<c:if test="${c:browser('ie7-') or c:browser('ie6-')}">
.z-paging-os .z-paging-os-cnt {
	top:-4px;
}
</c:if>
<%-- IE 6 GIF  --%>
<c:if test="${c:browser('ie6-')}">
.z-paging-btn .z-paging-next,
.z-paging-btn .z-paging-prev,
.z-paging-btn .z-paging-last,
.z-paging-btn .z-paging-first {	
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-btn2.gif')});
}
div.z-paging-os-cnt-l,
div.z-paging-os-cnt-r,
div.z-paging-os-cnt-m,
div.z-paging-os-cnt-seld-l,
div.z-paging-os-cnt-seld-r,
div.z-paging-os-cnt-seld-m,
div.z-paging-os-cnt-l-over {
	background-image:url(${c:encodeURL('~./zul/img/paging/paging-btn.gif')});
}
</c:if>
<c:if test="${c:isSafari()}">
.z-paging-os .z-paging-os-cnt {
	top: 4px;
}
</c:if>
<c:if test="${c:isGecko() or c:browser('ie8')}">
.z-paging-os {
	padding-bottom: 3px;
}
</c:if>