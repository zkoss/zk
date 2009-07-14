<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
.z-button {
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM}; color: black;
	cursor: pointer; white-space: nowrap;
}
button.z-button {
	padding:0 !important; margin:0 !important; border:0 !important;
	background: transparent !important;
	font-size: 0 !important;
	line-height: 0 !important;
	width: 3px !important;
	height: ${c:isGecko() ? 0: 1}px !important;
}
.z-button .z-button-cr * {
	display: block; width: 1px !important; overflow: hidden;
	font-size: 0 !important;
	line-height: 0 !important;
}
<c:if test="${c:browser('gecko2-')}">
table.z-button {
	table-layout:fixed;
}
button.z-button {<%-- remove browser's focus effect to scroll down, if any--%>
	position: relative; top: -5px;
}
</c:if>
span.z-button {
	display:-moz-inline-box; vertical-align:bottom; display:inline-block;
	margin: 1px 1px 0 0;
}
<c:if test="${c:isSafari()}"><%-- remove browser's focus effect --%>
.z-button :focus {
	outline: none !important;
}
</c:if>

.z-button .z-button-cr * {
	width: 3px !important;
}
.z-button-disd {
	color: gray; cursor: default; opacity: .6; -moz-opacity: .6; filter: alpha(opacity=60);
}
<%-- default --%>
.z-button .z-button-tl, .z-button .z-button-tr, .z-button .z-button-bl, .z-button .z-button-br{
	background-image:url(${c:encodeURL('~./zul/img/button/btn-corner.gif')});
}
.z-button .z-button-tm, .z-button .z-button-bm  {
	background-image:url(${c:encodeURL('~./zul/img/button/btn-x.gif')});
}
.z-button .z-button-cl, .z-button .z-button-cr {
	background-image:url(${c:encodeURL('~./zul/img/button/btn-y.gif')});
}
.z-button .z-button-cm {
	background-image:url(${c:encodeURL('~./zul/img/button/btn-ctr.gif')});
}

.z-button .z-button-tl {
	background-repeat: no-repeat;
	background-position: 0 0;
	width: 3px; height: 3px; padding: 0; margin: 0;
}
.z-button .z-button-tm {
	background-repeat: repeat-x;
	background-position: 0 0;
}
.z-button .z-button-tr {
	background-repeat: no-repeat;
	background-position: -3px 0px;
	width: 3px; height: 3px; padding: 0; margin: 0;
}
.z-button .z-button-cl {
	background-repeat: no-repeat;
	background-position: 0 0;
	width: 3px; padding: 0; margin: 0; text-align: right;
}
.z-button .z-button-cm {
	margin: 0; overflow: hidden;
	vertical-align: middle;
	text-align: center;
	padding: 0 5px;
	background-repeat: repeat-x;
	background-position: 0 0;
	white-space: nowrap;
}
.z-button .z-button-cr {
	background-repeat: no-repeat;
	background-position: -3px 0px;
	width: 3px;  padding: 0; margin: 0;
}
.z-button .z-button-bl {
	background-repeat: no-repeat;
	background-position: 0px -3px;
	width: 3px; height: 3px;  padding: 0; margin: 0;
}
.z-button .z-button-bm {
	background-repeat: repeat-x;
	background-position: 0 -3px;
	height: 3px;
}
.z-button .z-button-br {
	background-repeat: no-repeat;
	background-position: -3px -3px;
	width: 3px; height: 3px; padding: 0; margin: 0;
}

<%-- Mouseover --%>
.z-button-over .z-button-tl {
	background-position:-6px 0;
}
.z-button-over .z-button-tm {
	background-position:0 -6px;
}
.z-button-over .z-button-tr {
	background-position:-9px 0;
}
.z-button-over .z-button-cl {
	background-position:-6px 0px;
}
.z-button-over .z-button-cm {
  background-position:0 -500px;
}
.z-button-over .z-button-cr {
	background-position:-9px 0px;
}
.z-button-over .z-button-bl {
	background-position:-6px -3px;
}
.z-button-over .z-button-bm {
	background-position:0 -9px;
}
.z-button-over .z-button-br {
	background-position:-9px -3px;
}
<%-- focus --%>
.z-button-focus .z-button-tl {
	background-position:-12px 0px;
}
.z-button-focus .z-button-tm {
	background-position:0 -12px;
}
.z-button-focus .z-button-tr {
	background-position:-15px 0px;
}
.z-button-focus .z-button-cl {
	background-position:-12px 0px;
}
.z-button-focus .z-button-cr {
	background-position:-15px 0px;
}
.z-button-focus .z-button-bl {
	background-position:-12px -3px;
}
.z-button-focus .z-button-bm {
	background-position:0 -15px;
}
.z-button-focus .z-button-br {
	background-position:-15px -3px;
}
<%-- click --%>
.z-button-clk .z-button-tl {
	background-position:-6px 0px;
}
.z-button-clk .z-button-tm{
	background-position:0 -18px;
}
.z-button-clk .z-button-tr {
	background-position:-9px 0px;
}
.z-button-clk .z-button-bl {
	background-position:-6px -3px;
}
.z-button-clk .z-button-bm {
	background-position:0 -21px;
}
.z-button-clk .z-button-br {
	background-position:-9px -3px;
}
.z-button-clk .z-button-cl {
	background-position:-6px 0px;
}
.z-button-clk .z-button-cm {
    background-position:0 -1000px;
}
.z-button-clk .z-button-cr {
	background-position:-9px 0px;
}
<%-- os mold --%>
.z-button-os {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; font-weight: normal;
}