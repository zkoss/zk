<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
.z-button,
.z-button tr td {
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	color: black;
	cursor: pointer;
	white-space: nowrap;
}
button.z-button {
	padding:0 !important;
	margin:0 !important;
	border:0 !important;
	background: transparent !important;
	font-size: 0 !important;
	line-height: 0 !important;
	width: 4px !important;
	height: ${zk.gecko > 0 ? 0 : 1}px !important;
}
.z-button .z-button-cr * {<%-- IE 6 --%>
	display: block;
	width: 4px !important;
	overflow: hidden;
	font-size: 0 !important;
	line-height: 0 !important;
}
span.z-button {
	display: -moz-inline-box;
	vertical-align: bottom;
	display: inline-block;
	margin: 1px 1px 0 0;
}
<c:if test="${zk.safari > 0}"><%-- remove browser's focus effect --%>
.z-button:focus {
	outline: none !important;
}
</c:if>

.z-button-disd {
	color: gray;
	opacity: .6;
	-moz-opacity: .6;
	filter: alpha(opacity=60);
}
.z-button-disd tr td {
	cursor: default;
}
<%-- os mold --%>
.z-button-os {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	font-weight: normal;
}

<%-- image --%>
.z-button .z-button-tl,
.z-button .z-button-tr,
.z-button .z-button-bl,
.z-button .z-button-br{
	background-image: url(${c:encodeThemeURL('~./${theme}/zul/img/button/btn-corner.gif', theme)});
}
.z-button .z-button-tm, .z-button .z-button-bm  {
	background-image: url(${c:encodeThemeURL('~./${theme}/zul/img/button/btn-x.gif', theme)});
}
.z-button .z-button-cl, .z-button .z-button-cr {
	background-image: url(${c:encodeThemeURL('~./${theme}/zul/img/button/btn-y.gif', theme)});
}
.z-button .z-button-cm {
	background-image: url(${c:encodeThemeURL('~./${theme}/zul/img/button/btn-ctr.gif', theme)});
}

<%-- tl, tr, cl, cr --%>
.z-button .z-button-tl,
.z-button .z-button-cl,
.z-button .z-button-tr,
.z-button .z-button-cr {
	background-repeat: no-repeat;
	background-position: 0 0;
	width: 4px;
	padding: 0;
	margin: 0;
}
.z-button .z-button-tr,
.z-button .z-button-cr {
	background-position: -4px 0;
}
.z-button .z-button-tl,
.z-button .z-button-tr {
	height: 4px;
}
.z-button-focus .z-button-tl,
.z-button-focus .z-button-cl {
	background-position: -16px 0;
}
.z-button-focus .z-button-tr,
.z-button-focus .z-button-cr {
	background-position: -20px 0;
}
.z-button-over .z-button-tl,
.z-button-over .z-button-cl {
	background-position: -8px 0;
}
.z-button-over .z-button-tr,
.z-button-over .z-button-cr {
	background-position: -12px 0;
}
.z-button-clk .z-button-tl,
.z-button-clk .z-button-cl {
	background-position: -24px 0;
}
.z-button-clk .z-button-tr,
.z-button-clk .z-button-cr {
	background-position: -28px 0;
}
.z-button .z-button-cl {
	text-align: right;
}

<%-- bl, br --%>
.z-button .z-button-bl,
.z-button .z-button-br {
	width: 4px;
	height: 4px;
	padding: 0;
	margin: 0;
	background-repeat: no-repeat;
	background-position: 0 -4px;
}
.z-button .z-button-br {
	background-position: -4px -4px;
}
.z-button-focus .z-button-bl {
	background-position: -16px -4px;
}
.z-button-focus .z-button-br {
	background-position: -20px -4px;
}
.z-button-over .z-button-bl {
	background-position: -8px -4px;
}
.z-button-over .z-button-br {
	background-position: -12px -4px;
}
.z-button-clk .z-button-bl {
	background-position: -24px -4px;
}
.z-button-clk .z-button-br {
	background-position: -28px -4px;
}

<%-- tm, bm --%>
.z-button .z-button-tm {
	background-repeat: repeat-x;
	background-position: 0 0;
}
.z-button .z-button-bm {
	height: 4px;
	background-repeat: repeat-x;
	background-position: 0 -4px;
}
.z-button-focus .z-button-tm {
	background-position: 0 -16px;
}
.z-button-focus .z-button-bm {
	background-position: 0 -20px;
}
.z-button-over .z-button-tm {
	background-position: 0 -8px;
}
.z-button-over .z-button-bm {
	background-position: 0 -12px;
}
.z-button-clk .z-button-tm{
	background-position: 0 -24px;
}
.z-button-clk .z-button-bm {
	background-position: 0 -28px;
}

<%-- cm --%>
.z-button .z-button-cm {
	margin: 0; overflow: hidden;
	vertical-align: middle;
	text-align: center;
	padding: 0 7px;
	background-repeat: repeat-x;
	background-position: 0 0;
	white-space: nowrap;
}
.z-button-focus .z-button-cm {
	background-position: 0 -1000px;
}
.z-button-over .z-button-cm {
	background-position: 0 -500px;
}
.z-button-clk .z-button-cm {
	background-position: 0 -1500px;
	padding: 0 6px 0 8px;
}

