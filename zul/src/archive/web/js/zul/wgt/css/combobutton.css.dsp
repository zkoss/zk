<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
.z-combobutton,
.z-combobutton tr td,
.z-combobutton-toolbar,
.z-combobutton-toolbar tr td {
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM}; 
	color: black;
	cursor: pointer; 
	white-space: nowrap;
}
button.z-combobutton,
button.z-combobutton-toolbar {
	padding:0 !important; 
	margin:0 !important; 
	border:0 !important;
	background: transparent !important;
	font-size: 0 !important;
	line-height: 0 !important;
	width: 4px !important;
	height: ${zk.gecko > 0 ? 0 : 1}px !important;
}
.z-combobutton .z-combobutton-cr *,
.z-combobutton-toolbar .z-combobutton-toolbar-cr * {<%-- IE 6 --%>
	display: block;
	overflow: hidden;
	font-size: 0 !important;
	line-height: 0 !important;
}
span.z-combobutton,
span.z-combobutton-toolbar {
	display: -moz-inline-box; 
	vertical-align: bottom; 
	display: inline-block;
	margin: 1px 1px 0 0;
}
<c:if test="${zk.safari > 0}"><%-- remove browser's focus effect --%>
.z-combobutton:focus,
.z-combobutton-toolbar:focus {
	outline: none !important;
}
</c:if>

.z-combobutton-disd,
.z-combobutton-toolbar-disd {
	color: gray; 
	opacity: .6; 
	-moz-opacity: .6; 
	filter: alpha(opacity=60);
}
.z-combobutton-disd tr td,
.z-combobutton-toolbar-disd tr td {
	cursor: default;
}
<%-- image --%>
.z-combobutton .z-combobutton-tl, 
.z-combobutton .z-combobutton-bl {
	background-image: url(${c:encodeThemeURL('~./zul/img/button/btn-corner.gif')});
}
.z-combobutton .z-combobutton-tr, 
.z-combobutton .z-combobutton-br {
	background-image: url(${c:encodeThemeURL('~./zul/img/button/combobutton-corner-r.gif')});
}
.z-combobutton .z-combobutton-tm,
.z-combobutton .z-combobutton-bm {
	background-image: url(${c:encodeThemeURL('~./zul/img/button/btn-x.gif')});
}
.z-combobutton .z-combobutton-cl {
	background-image: url(${c:encodeThemeURL('~./zul/img/button/btn-y.gif')});
}
.z-combobutton .z-combobutton-cr,
.z-combobutton-toolbar .z-combobutton-toolbar-cr {
	background-image: url(${c:encodeThemeURL('~./zul/img/button/combobutton-y-r.gif')});
}
.z-combobutton .z-combobutton-cm,
.z-combobutton-toolbar .z-combobutton-toolbar-cm {
	background-image: url(${c:encodeThemeURL('~./zul/img/button/btn-ctr.gif')});
}

.z-combobutton .z-combobutton-tl,
.z-combobutton .z-combobutton-cl,
.z-combobutton .z-combobutton-bl {
	width: 4px;
	padding: 0;
	margin: 0;
	background-repeat: no-repeat;
	background-position: 0 0;
}
.z-combobutton .z-combobutton-tr,
.z-combobutton .z-combobutton-cr,
.z-combobutton .z-combobutton-br {
	width: 17px;
	padding: 0;
	margin: 0;
	background-repeat: no-repeat;
	background-position: -4px 0;
	border-left: 1px solid #CCCCCC;
}
.z-combobutton .z-combobutton-tl, 
.z-combobutton .z-combobutton-tm, 
.z-combobutton .z-combobutton-tr,
.z-combobutton .z-combobutton-bl,
.z-combobutton .z-combobutton-bm,
.z-combobutton .z-combobutton-br {
	height: 4px;
	padding: 0;
	margin: 0;
}
<%-- tl, tr, cl, cr --%>
.z-combobutton-focus .z-combobutton-tl,
.z-combobutton-focus .z-combobutton-cl {
	background-position: -16px 0;
}
.z-combobutton-focus .z-combobutton-tr {
	background-position: -4px -16px;
}
.z-combobutton-focus .z-combobutton-cr,
.z-combobutton-toolbar-focus .z-combobutton-toolbar-cr {
	background-position: -46px 0;
}
.z-combobutton-over .z-combobutton-tl,
.z-combobutton-over .z-combobutton-cl {
	background-position: -8px 0;
}
.z-combobutton-over .z-combobutton-cr,
.z-combobutton-over .z-combobutton-tr {
	border-left: 1px solid #8FB9D0;
}
.z-combobutton-over .z-combobutton-tr {
	background-position: -4px -8px;
}
.z-combobutton-over .z-combobutton-cr,
.z-combobutton-toolbar-over .z-combobutton-toolbar-cr {
	background-position: -25px 0;
}
.z-combobutton-clk .z-combobutton-tl,
.z-combobutton-clk .z-combobutton-cl {
	background-position: -24px 0;
}
.z-combobutton-clk .z-combobutton-tr,
.z-combobutton-clk .z-combobutton-cr {
	background-position: -4px -24px;
	border-color: #499EB3;
}
.z-combobutton-clk .z-combobutton-cr {
	background-position: -67px 0;
}
.z-combobutton .z-combobutton-cl {
	text-align: right;
}

<%-- bl, br --%>
.z-combobutton .z-combobutton-bl {
	background-position: 0 -4px;
}
.z-combobutton .z-combobutton-br {
	background-position: -4px -4px;
}
.z-combobutton-focus .z-combobutton-bl {
	background-position: -16px -4px;
}
.z-combobutton-focus .z-combobutton-br {
	background-position: -4px -20px;
}
.z-combobutton-over .z-combobutton-bl {
	background-position: -8px -4px;
}
.z-combobutton-over .z-combobutton-br {
	background-position: -4px -12px;
	border-left: 1px solid #8FB9D0;
}
.z-combobutton-clk .z-combobutton-bl {
	background-position: -24px -4px;
}
.z-combobutton-clk .z-combobutton-br {
	background-position: -4px -28px;
	border-color: #499EB3;
}

<%-- tm, bm --%>
.z-combobutton .z-combobutton-tm {
	background-repeat: repeat-x;
	background-position: 0 0;
}
.z-combobutton .z-combobutton-bm {
	height: 4px;
	background-repeat: repeat-x;
	background-position: 0 -4px;
}
.z-combobutton-focus .z-combobutton-tm {
	background-position: 0 -16px;
}
.z-combobutton-focus .z-combobutton-bm {
	background-position: 0 -20px;
}
.z-combobutton-over .z-combobutton-tm {
	background-position: 0 -8px;
}
.z-combobutton-over .z-combobutton-bm {
	background-position: 0 -12px;
}
.z-combobutton-clk .z-combobutton-tm {
	background-position: 0 -24px;
}
.z-combobutton-clk .z-combobutton-bm {
	background-position: 0 -28px;
}

<%-- cm --%>
.z-combobutton .z-combobutton-cm,
.z-combobutton-toolbar .z-combobutton-toolbar-cm {
	margin: 0; overflow: hidden;
	vertical-align: middle;
	text-align: center;
	padding: 0 7px;
	background-repeat: repeat-x;
	background-position: 0 0;
	white-space: nowrap;
}
.z-combobutton-focus .z-combobutton-cm,
.z-combobutton-toolbar-focus .z-combobutton-toolbar-cm {
	background-position: 0 -1000px;
}
.z-combobutton-over .z-combobutton-cm,
.z-combobutton-toolbar-over .z-combobutton-toolbar-cm {
	background-position: 0 -500px;
}
.z-combobutton-clk .z-combobutton-cm {
	background-position: 0 -1500px;
	padding: 0 6px 0 8px;
}
.z-combobutton .z-combobutton-cr .z-combobutton-btn-img {
	background-image: url(${c:encodeThemeURL('~./zul/img/button/combobutton-right-btn.gif')});
	background-position: 0 -4px;
	width: 14px !important;
	height: 15px;
	background-repeat: no-repeat;
}

<%-- toolbar mold --%>
.z-combobutton-toolbar .z-combobutton-toolbar-cm {
	font-size: ${fontSizeS};
	background-image: none;
}
.z-combobutton-toolbar-over  .z-combobutton-toolbar-cm {
	background-image: url(${c:encodeThemeURL('~./zul/img/button/btn-ctr.gif')});
}
.z-combobutton-toolbar .z-combobutton-toolbar-tl, 
.z-combobutton-toolbar .z-combobutton-toolbar-bl,
.z-combobutton-toolbar .z-combobutton-toolbar-tr, 
.z-combobutton-toolbar .z-combobutton-toolbar-br,
.z-combobutton-toolbar .z-combobutton-toolbar-tm,
.z-combobutton-toolbar .z-combobutton-toolbar-bm {
	display: none;
	zoom: 1;
}
.z-combobutton-toolbar .z-combobutton-toolbar-cr .z-combobutton-toolbar-btn-img {
	background-repeat: no-repeat;
	background-image: url(${c:encodeThemeURL('~./zul/img/button/combobutton-right-btn.gif')});
	width: 10px !important;
	height: 18px;
	background-position: -2px -3px;
}
.z-combobutton-toolbar .z-combobutton-toolbar-cr {
	background-image: url(${c:encodeThemeURL('~./zul/img/button/btn-ctr.gif')});
	background-repeat: repeat-x;
	background-position: 0 0;
	width: 12px;
	border: 0 none;
}
.z-combobutton-toolbar-over .z-combobutton-toolbar-cr {
	background-position: 0 -500px;
}
.z-combobutton-toolbar .z-combobutton-toolbar-cm,
.z-combobutton-toolbar .z-combobutton-toolbar-cr {
	padding: 2px 2px 2px 3px;
}
.z-combobutton-toolbar .z-combobutton-toolbar-cr {
	padding-right: 1px;
	background-image: none;
}
.z-combobutton-toolbar-over .z-combobutton-toolbar-cr {
	background-image: url(${c:encodeThemeURL('~./zul/img/button/btn-ctr.gif')});
}
.z-combobutton-toolbar-over .z-combobutton-toolbar-cm {
	padding: 1px 2px 1px 2px;
	border: 1px solid #7EAAC6;
	border-right: 0 none;
}
.z-combobutton-toolbar-over .z-combobutton-toolbar-cr {
	padding: 1px 1px 1px 2px;
	border: 1px solid #7EAAC6;
}