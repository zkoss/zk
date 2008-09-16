<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
.z-button {
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM}; color: black;
	cursor: pointer; white-space: nowrap;
}
button.z-button {
	padding:0 !important; margin:0 !important; border:0 !important;
	background: transparent !important;
	font-size: 1px !important; width: 1px !important;
	height: ${c:isGecko() ? 0: 1}px !important;
}
i.z-button {
	display: block; width: 1px !important; overflow: hidden;
	font-size: 1px !important; line-height: 1px !important;
}
<c:if test="${c:isGecko() and !c:isGecko3()}">
table.z-button {
	table-layout:fixed;
}
button.z-button {<%-- remove browser's focus effect to scroll down, if any--%>
	position: relative; top: -5px;
}
</c:if>
span.z-button {
	display:-moz-inline-box; vertical-align:bottom; display:inline-block;
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
	margin: 1px 1px 0 0;
}
<c:if test="${c:isSafari()}"><%-- remove browser's focus effect --%>
.z-button :focus {
	outline: none!important;
}
</c:if>
button.z-button {
	width: 3px !important;
}
.z-button i.z-button {
	width: 3px !important;
}
.z-button-disd * {
	color: gray!important; cursor: default!important;
}
.z-button-disd {
	color: gray; cursor: default; opacity: .6; -moz-opacity: .6; filter: alpha(opacity=60);
}
<%-- default --%>
.z-button .z-button-tl, .z-button .z-button-tm, .z-button .z-button-tr, 
.z-button .z-button-cl, .z-button .z-button-cm, .z-button .z-button-cr, 
.z-button .z-button-bl, .z-button .z-button-bm, .z-button .z-button-br {
	background-image:url(${c:encodeURL('~./zul/img/button/z-btn-trendy.gif')});
}

.z-button .z-button-tl {
	background-repeat: no-repeat;
	background-position: 0 0;
	width: 3px; height: 3px; padding: 0; margin: 0;
}
.z-button .z-button-tm {
	background-repeat: repeat-x;
	background-position: 0 -1012px;
}
.z-button .z-button-tr {
	background-repeat: no-repeat;
	background-position: 0 -506px;
	width: 3px; height: 3px; padding: 0; margin: 0;
}
.z-button .z-button-cl {
	background-repeat: no-repeat;
	background-position: 0 -3px;
	width: 3px; padding: 0; margin: 0; text-align: right;
}
.z-button .z-button-cm {
	margin: 0; overflow: hidden;
	vertical-align: middle;
	text-align: center;
	padding: 0 5px;
	background-repeat: repeat-x;
	background-position: 0 -1015px;
	white-space: nowrap; 
}
.z-button .z-button-cr {
	background-repeat: no-repeat;
	background-position: 0 -509px;
	width: 3px;  padding: 0; margin: 0;
}
.z-button .z-button-bl {
	background-repeat: no-repeat;
	background-position: 0 -503px;
	width: 3px; height: 3px;  padding: 0; margin: 0;
}
.z-button .z-button-bm {
	background-repeat: repeat-x;
	background-position: 0 -1515px;
	height: 3px;
}
.z-button .z-button-br {
	background-repeat: no-repeat;
	background-position: 0 -1009px;
	width: 3px; height: 3px; padding: 0; margin: 0;
}

<%-- Mouseover --%>
.z-button-over .z-button-tl {
	background-position:0 -1518px;
}
.z-button-over .z-button-tm {
	background-position:0 -2530px;
}
.z-button-over .z-button-tr {
	background-position:0 -2024px;
}
.z-button-over .z-button-cl {
	background-position:0 -1521px;
}
.z-button-over .z-button-cm {
  background-position:0 -2533px;    
}
.z-button-over .z-button-cr {
	background-position:0 -2027px;
}
.z-button-over .z-button-bl {
	background-position:0 -2021px;
}
.z-button-over .z-button-bm {
	background-position:0 -3033px;
}
.z-button-over .z-button-br {
	background-position:0 -2527px;
}
<%-- focus --%>
.z-button-focus .z-button-tl {
	background-position:0 -3542px;
}
.z-button-focus .z-button-tm {
	background-position:0 -4554px;
}
.z-button-focus .z-button-tr {
	background-position:0 -4048px;
}
.z-button-focus .z-button-cl {
	background-position:0 -3545px;
}
.z-button-focus .z-button-cr {
	background-position:0 -4051px;
}
.z-button-focus .z-button-bl {
	background-position:0 -4045px;
}
.z-button-focus .z-button-bm {
	background-position:0 -5057px;
}
.z-button-focus .z-button-br {
	background-position:0 -4551px;
}
<%-- click --%>
.z-button-clk .z-button-tl {
	background-position:0 -1518px;
}
.z-button-clk .z-button-tm{
	background-position:0 -3036px;
}
.z-button-clk .z-button-tr {
	background-position:0 -2024px;
}
.z-button-clk .z-button-bl {
	background-position:0 -2021px;
}
.z-button-clk .z-button-bm {
	background-position:0 -3539px;
}
.z-button-clk .z-button-br {
	background-position:0 -2527px;
}
.z-button-clk .z-button-cl {
	background-position:0 -1521px;
}
.z-button-clk .z-button-cm {
    background-position:0 -3039px;
}
.z-button-clk .z-button-cr {
	background-position:0 -2027px;
}
