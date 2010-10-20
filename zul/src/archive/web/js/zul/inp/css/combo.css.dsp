<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- Combobox --%>
.z-combobox-rounded,
.z-bandbox-rounded,
.z-datebox-rounded,
.z-combobox,
.z-bandbox,
.z-datebox {
	border: 0; padding: 0; margin: 0; white-space: nowrap;
	font-family: ${fontFamilyC};font-size: ${fontSizeM};
	display:-moz-inline-box;
	display:inline-block;
}
.z-combobox-rounded-inp,
.z-bandbox-rounded-inp,
.z-datebox-rounded-inp,
.z-timebox-rounded-inp,
.z-spinner-rounded-inp,
.z-combobox-inp,
.z-bandbox-inp,
.z-datebox-inp,
.z-timebox-inp,
.z-spinner-inp {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	font-weight: normal;
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #86A4BE;
	padding-top: 2px;
	padding-bottom: 2px;
	border-radius: 2px 0 0 2px;
	-moz-border-radius: 2px 0 0 2px;
	-webkit-border-radius: 2px 0 0 2px;
	<c:if test="${c:isSafari()}">
		margin:0;
	</c:if>
	<c:if test="${c:isOpera()}">
		font-style: normal;
	</c:if>
	height: 14px;
}
.z-combobox-rounded-inp,
.z-bandbox-rounded-inp,
.z-datebox-rounded-inp,
.z-timebox-rounded-inp,
.z-spinner-rounded-inp {
	border:0;	
	padding: 5px 4px;
}
.z-combobox-rounded-inp {
	background-image: url(${c:encodeURL('~./zul/img/button/combobox-rounded.gif')});
}
.z-bandbox-rounded-inp {
	background-image: url(${c:encodeURL('~./zul/img/button/bandbox-rounded.gif')});
}
.z-datebox-rounded-inp {
	background-image: url(${c:encodeURL('~./zul/img/button/datebox-rounded.gif')});
}
.z-timebox-rounded-inp,
.z-spinner-rounded-inp {
	background-image: url(${c:encodeURL('~./zul/img/button/timebox-rounded.gif')});	
}
.z-combobox-focus .z-combobox-inp,
.z-bandbox-focus .z-bandbox-inp,
.z-datebox-focus .z-datebox-inp,
.z-timebox-focus .z-timebox-inp,
.z-spinner-focus .z-spinner-inp {
	border: 1px solid #90BCE6;
}
.z-combobox .z-combobox-text-invalid,
.z-bandbox .z-bandbox-text-invalid,
.z-datebox .z-datebox-text-invalid,
.z-timebox .z-timebox-text-invalid,
.z-spinner .z-spinner-text-invalid {
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg-invalid.gif')}) repeat-x 0 0;
	border: 1px solid #DD7870;
	border-right-width: 1px !important;
}
.z-combobox input.z-combobox-right-edge,
.z-bandbox input.z-bandbox-right-edge,
.z-datebox input.z-datebox-right-edge,
.z-timebox input.z-timebox-right-edge,
.z-spinner input.z-spinner-right-edge {
	border-right-width: 1px !important;
}

.z-combobox-rounded input.z-combobox-rounded-text-invalid,
.z-bandbox-rounded input.z-bandbox-rounded-text-invalid,
.z-datebox-rounded input.z-datebox-rounded-text-invalid,
.z-timebox-rounded input.z-timebox-rounded-text-invalid,
.z-spinner-rounded input.z-spinner-rounded-text-invalid {
	background: #FFF url(${c:encodeURL('~./zul/img/button/redcombo-rounded.gif')}) repeat-x 0 0;
}
.z-combobox-rounded .z-combobox-rounded-text-invalid + i.z-combobox-rounded-btn-right-edge,
.z-bandbox-rounded .z-bandbox-rounded-text-invalid + i.z-bandbox-rounded-btn-right-edge,
.z-datebox-rounded .z-datebox-rounded-text-invalid + i.z-datebox-rounded-btn-right-edge,
.z-timebox-rounded .z-timebox-rounded-text-invalid + i.z-timebox-rounded-btn-right-edge,
.z-spinner-rounded .z-spinner-rounded-text-invalid + i.z-spinner-rounded-btn-right-edge {
	background-image: url(${c:encodeURL('~./zul/img/button/redcombo-rounded.gif')});
	background-position: 0 -24px;
}
i.z-combobox-rounded i.z-combobox-rounded-btn-right-edge-invalid,
i.z-bandbox-rounded i.z-bandbox-rounded-btn-right-edge-invalid,
i.z-datebox-rounded i.z-datebox-rounded-btn-right-edge-invalid,
i.z-timebox-rounded i.z-timebox-rounded-btn-right-edge-invalid,
i.z-spinner-rounded i.z-spinner-rounded-btn-right-edge-invalid {
	background-image: url(${c:encodeURL('~./zul/img/button/redcombo-rounded.gif')});
	background-position: 0 -24px;
}
.z-combobox-rounded .z-combobox-rounded-btn,
.z-bandbox-rounded .z-bandbox-rounded-btn,
.z-datebox-rounded .z-datebox-rounded-btn,
.z-timebox-rounded .z-timebox-rounded-btn,
.z-spinner-rounded .z-spinner-rounded-btn,
.z-combobox .z-combobox-btn,
.z-bandbox .z-bandbox-btn,
.z-datebox .z-datebox-btn,
.z-timebox .z-timebox-btn,
.z-spinner .z-spinner-btn {
	background: transparent no-repeat 0 0;
	background-image : url(${c:encodeURL('~./zul/img/button/combobtn.gif')});
	vertical-align: top; cursor: pointer; width: 17px; height: 19px; border: 0;
	border-bottom: 1px solid #86A4BE;
	overflow: hidden;
	display:-moz-inline-box; display:inline-block;
	border-radius: 0 2px 2px 0;
	-moz-border-radius: 0 2px 2px 0;
	-webkit-border-radius: 0 2px 2px 0;
	<c:if test="${c:browser('ie7-') || c:browser('ie6-')}">
		margin-top: 1px;
	</c:if>
}
.z-combobox-rounded .z-combobox-rounded-btn,
.z-bandbox-rounded .z-bandbox-rounded-btn,
.z-datebox-rounded .z-datebox-rounded-btn,
.z-timebox-rounded .z-timebox-rounded-btn,
.z-spinner-rounded .z-spinner-rounded-btn {
	border: 0;
	width: 24px; 
	height: 24px;
	background-position: 0 -120px;	
}
.z-combobox-rounded .z-combobox-rounded-btn{	
	background-image: url(${c:encodeURL('~./zul/img/button/combobox-rounded.gif')});
}
.z-bandbox-rounded .z-bandbox-rounded-btn {
	background-image: url(${c:encodeURL('~./zul/img/button/bandbox-rounded.gif')});
}
.z-datebox-rounded .z-datebox-rounded-btn {
	background-image: url(${c:encodeURL('~./zul/img/button/datebox-rounded.gif')});
}
.z-timebox-rounded .z-timebox-rounded-btn,
.z-spinner-rounded .z-spinner-rounded-btn {
	background-image: url(${c:encodeURL('~./zul/img/button/timebox-rounded.gif')});
}
.z-combobox-rounded .z-combobox-rounded-btn-right-edge,
.z-bandbox-rounded .z-bandbox-rounded-btn-right-edge,
.z-datebox-rounded .z-datebox-rounded-btn-right-edge,
.z-timebox-rounded .z-timebox-rounded-btn-right-edge,
.z-spinner-rounded .z-spinner-rounded-btn-right-edge {
	background-position: -19px -120px;
	width: 5px;
	cursor: default;
}
.z-combobox .z-combobox-btn-over,
.z-bandbox .z-bandbox-btn-over,
.z-datebox .z-datebox-btn-over,
.z-timebox .z-timebox-btn-over,
.z-spinner .z-spinner-btn-over {
	background-position: -17px 0;
}
.z-combobox-rounded-inp-over,
.z-bandbox-rounded-inp-over,
.z-datebox-rounded-inp-over,
.z-timebox-rounded-inp-over,
.z-spinner-rounded-inp-over {
	background-position: 0 -24px;
}
.z-combobox-rounded .z-combobox-rounded-btn-over,
.z-bandbox-rounded .z-bandbox-rounded-btn-over,
.z-datebox-rounded .z-datebox-rounded-btn-over,
.z-timebox-rounded .z-timebox-rounded-btn-over,
.z-spinner-rounded .z-spinner-rounded-btn-over  {
	background-position: 0 -144px;
}
.z-combobox-focus .z-combobox-btn,
.z-bandbox-focus .z-bandbox-btn,
.z-datebox-focus .z-datebox-btn,
.z-timebox-focus .z-timebox-btn,
.z-spinner-focus .z-spinner-btn {
	background-position: -51px 0;
	border-bottom: 1px solid #80B9E9;
}
.z-combobox-rounded-focus .z-combobox-rounded-btn,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn,
.z-datebox-rounded-focus .z-datebox-rounded-btn,
.z-timebox-rounded-focus .z-timebox-rounded-btn,
.z-spinner-rounded-focus .z-spinner-rounded-btn {
	background-position: 0 -192px;
}
.z-combobox-rounded-focus .z-combobox-rounded-btn-right-edge,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn-right-edge,
.z-datebox-rounded-focus .z-datebox-rounded-btn-right-edge,
.z-timebox-rounded-focus .z-timebox-rounded-btn-right-edge,
.z-spinner-rounded-focus .z-spinner-rounded-btn-right-edge {
	background-position: -19px -120px;
}
.z-combobox-focus .z-combobox-btn-over,
.z-bandbox-focus .z-bandbox-btn-over,
.z-datebox-focus .z-datebox-btn-over,
.z-timebox-focus .z-timebox-btn-over,
.z-spinner-focus .z-spinner-btn-over {
	background-position: -68px 0;
}
.z-combobox-rounded-focus .z-combobox-rounded-btn-over,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn-over,
.z-datebox-rounded-focus .z-datebox-rounded-btn-over,
.z-timebox-rounded-focus .z-timebox-rounded-btn-over,
.z-spinner-rounded-focus .z-spinner-rounded-btn-over {
	background-position: 0 -216px;
}
.z-combobox-focus .z-combobox-btn-clk, .z-combobox .z-combobox-btn-clk,
.z-bandbox-focus .z-bandbox-btn-clk, .z-bandbox .z-bandbox-btn-clk,
.z-datebox-focus .z-datebox-btn-clk, .z-datebox .z-datebox-btn-clk,
.z-timebox-focus .z-timebox-btn-clk, .z-timebox .z-timebox-btn-clk,
.z-spinner-focus .z-spinner-btn-clk, .z-spinner .z-spinner-btn-clk {
	background-position: -34px 0;
}
.z-combobox-rounded-focus .z-combobox-rounded-inp-clk, .z-combobox-rounded .z-combobox-inp-clk,
.z-bandbox-rounded-focus .z-bandbox-rounded-inp-clk, .z-bandbox-rounded .z-bandbox-inp-clk,
.z-datebox-rounded-focus .z-datebox-rounded-inp-clk, .z-datebox-rounded .z-datebox-inp-clk,
.z-timebox-rounded-focus .z-timebox-rounded-inp-clk, .z-timebox-rounded .z-timebox-inp-clk,
.z-spinner-rounded-focus .z-spinner-rounded-inp-clk, .z-spinner-rounded .z-spinner-inp-clk {
	background-position: 0 -48px;
}
.z-combobox-rounded-focus .z-combobox-rounded-btn-clk, .z-combobox-rounded .z-combobox-rounded-btn-clk,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn-clk, .z-bandbox-rounded .z-bandbox-rounded-btn-clk,
.z-datebox-rounded-focus .z-datebox-rounded-btn-clk, .z-datebox-rounded .z-datebox-rounded-btn-clk,
.z-timebox-rounded-focus .z-timebox-rounded-btn-clk, .z-timebox-rounded .z-timebox-rounded-btn-clk,
.z-spinner-rounded-focus .z-spinner-rounded-btn-clk, .z-spinner-rounded .z-spinner-rounded-btn-clk {
	background-position: 0 -168px !important;
}
.z-combobox-rounded-pp,
.z-bandbox-rounded-pp,
.z-datebox-rounded-pp,
.z-combobox-pp,
.z-bandbox-pp,
.z-datebox-pp {
	display: block; position: absolute;
	background: white; border: 1px solid #86A4BE; padding: 2px;
	font-size: ${fontSizeS};
}
.z-combobox-rounded-pp,
.z-combobox-pp,
.z-bandbox-rounded-pp,
.z-bandbox-pp {
	font-family: ${fontFamilyC};
	overflow: auto;
}
<%-- Comboitem --%>
.z-combobox-rounded-pp .z-comboitem-text, .z-combobox-rounded-pp .z-comboitem-btn,
.z-combobox-pp .z-comboitem-text, .z-combobox-pp .z-comboitem-btn {
	white-space: nowrap; font-size: ${fontSizeS}; cursor: pointer;
}
.z-combobox-rounded-pp .z-comboitem-inner, .z-combobox-rounded-pp .z-comboitem-cnt,
.z-combobox-pp .z-comboitem-inner, .z-combobox-pp .z-comboitem-cnt {<%--description--%>
	color: #888; font-size: ${fontSizeXS}; padding-left: 6px;
}
.z-combobox-rounded-pp .z-comboitem, .z-combobox-rounded-pp .z-comboitem a, .z-combobox-rounded-pp .z-comboitem a:visited,
.z-combobox-pp .z-comboitem, .z-combobox-pp .z-comboitem a, .z-combobox-pp .z-comboitem a:visited {
	font-size: ${fontSizeM}; font-weight: normal; color: black;
	text-decoration: none;
}
.z-combobox-rounded-pp .z-comboitem a:hover,
.z-combobox-pp .z-comboitem a:hover {
	text-decoration: underline;
}
.z-combobox-rounded-pp .z-comboitem-seld,
.z-combobox-pp .z-comboitem-seld {
	background: #b3c8e8; border: 1px solid #6f97d2;
}
.z-combobox-rounded-pp .z-comboitem-over,
.z-combobox-pp .z-comboitem-over {
	background: #D3EFFA;
}
.z-combobox-rounded-pp .z-comboitem-over-seld,
.z-combobox-pp .z-comboitem-over-seld {
	background: #82D5F8;
}

<%-- Bandbox trendy mold --%>
.z-bandbox .z-bandbox-btn {
	background-image : url(${c:encodeURL('~./zul/img/button/bandbtn.gif')});
}
<%-- Datebox --%>
.z-datebox-rounded-over,
.z-datebox-over{
	background: #dae7f6;
}
.z-datebox .z-datebox-btn {
	background-image : url(${c:encodeURL('~./zul/img/button/datebtn.gif')});
}
.z-datebox-rounded-pp,
.z-datebox-pp {
	border: 1px solid #888888;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	font-weight: normal;
}
.z-datebox-rounded-pp .z-datebox-rounded-calyear,
.z-datebox-pp .z-datebox-calyear {
	background: #d8e8f0;
}
.z-datebox-rounded-time,
.z-datebox-time {
	width: 20px;
	padding: 0;
	margin: 0;
}
.z-datebox-rounded-time,
.z-datebox-time {
	text-align: center;	
}
.z-datebox-rounded-time-up,
.z-datebox-rounded-time-down,
.z-datebox-time-up,
.z-datebox-time-down { 	
	width: 0; 
	height: 0;
	margin: 0;
	padding: 0;
	cursor: pointer;
}
.z-datebox-rounded-time-up,
.z-datebox-time-up {
	border-color: white white #004A7F;
	border-style: none solid solid solid ;
	border-width: 5px;
	<c:if test="${c:browser('ie') || c:isOpera() || c:browser('gecko2-')}">
		border-width: 6px;
	</c:if>
	overflow: hidden;
}
.z-datebox-rounded-time-down,
.z-datebox-time-down {
	border-color: #004A7F white white;
	border-style: solid solid none solid;
	border-width: 5px;
	margin-top: 3px;
	<c:if test="${c:isOpera() || c:browser('gecko2-')}">
		margin: 3px 0 0 1px;
	</c:if>
	overflow: hidden;
}
.z-datebox-rounded-time-up.z-datebox-rounded-time-over,
.z-datebox-time-up.z-datebox-time-over {
	border-color: white white #BFBFBF;
}
.z-datebox-rounded-time-down.z-datebox-rounded-time-over,
.z-datebox-time-down.z-datebox-time-over {
	border-color: #BFBFBF white white;
}
<%-- Timebox and Spinner --%>
.z-timebox-rounded,
.z-spinner-rounded,
.z-timebox,
.z-spinner {
	display:-moz-inline-box;
	display:inline-block;
}

.z-timebox .z-timebox-btn,
.z-spinner .z-spinner-btn {
	background-image : url(${c:encodeURL('~./zul/img/button/timebtn.gif')});
}
<%-- Shadow --%>
.z-combobox-rounded-shadow, .z-bandbox-rounded-shadow, .z-datebox-rounded-shadow,
.z-combobox-shadow, .z-bandbox-shadow, .z-datebox-shadow {
	border-radius: 3px;
	box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
	-moz-border-radius: 3px;
	-moz-box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
	-webkit-border-radius: 3px;
	-webkit-box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
}
<%-- disable --%>
.z-spinner-rounded-disd,
.z-timebox-rounded-disd,
.z-datebox-rounded-disd,
.z-bandbox-rounded-disd,
.z-combobox-rounded-disd,
.z-spinner-disd,
.z-timebox-disd,
.z-datebox-disd,
.z-bandbox-disd,
.z-comboitem-disd,
.z-combobox-disd {
	opacity: .6;
	-moz-opacity: .6;
	filter: alpha(opacity=60);
}
.z-spinner-rounded-disd, .z-spinner-rounded-disd *,
.z-timebox-rounded-disd, .z-timebox-rounded-disd *,
.z-datebox-rounded-disd, .z-datebox-rounded-disd *,
.z-bandbox-rounded-disd, .z-bandbox-rounded-disd *,
.z-combobox-rounded-disd, .z-combobox-rounded-disd *,
.z-spinner-disd, .z-spinner-disd *,
.z-timebox-disd, .z-timebox-disd *,
.z-datebox-disd, .z-datebox-disd *,
.z-bandbox-disd, .z-bandbox-disd *,
.z-comboitem-disd, .z-comboitem-disd *,
.z-combobox-disd, .z-combobox-disd * {
	cursor: default !important;
	color: #303030 !important;
}
.z-timebox-rounded-disd,
.z-timebox-disd {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	font-weight: normal;
}
.z-comboitem-text-disd,
.z-spinner-text-disd,
.z-timebox-text-disd,
.z-datebox-text-disd,
.z-bandbox-text-disd,
.z-combobox-text-disd {
	background: #ECEAE4;
}
.z-spinner-readonly,
.z-timebox-readonly,
.z-datebox-readonly,
.z-bandbox-readonly,
.z-combobox-readonly,
.z-spinner-focus .z-spinner-readonly,
.z-timebox-focus .z-timebox-readonly,
.z-datebox-focus .z-datebox-readonly,
.z-bandbox-focus .z-bandbox-readonly,
.z-combobox-focus .z-combobox-readonly {
	background: transparent repeat-x 0 0;
	background-image: url(${c:encodeURL('~./zul/img/button/readonly-bg.gif')});
	border-right-width: 0;
	padding-right: 1px;
}
.z-combobox-rounded-readonly,
.z-bandbox-rounded-readonly,
.z-datebox-rounded-readonly,
.z-timebox-rounded-readonly,
.z-spinner-rounded-readonly {
	background-position: 0 -72px;
}
.z-combobox-rounded-readonly {
	background-image: url(${c:encodeURL('~./zul/img/button/combobox-rounded.gif')});
}
.z-bandbox-rounded-readonly {
	background-image: url(${c:encodeURL('~./zul/img/button/bandbox-rounded.gif')});
}
.z-datebox-rounded-readonly {
	background-image: url(${c:encodeURL('~./zul/img/button/datebox-rounded.gif')});
}
.z-timebox-rounded-readonly,
.z-spinner-rounded-readonly {
	background-image: url(${c:encodeURL('~./zul/img/button/timebox-rounded.gif')});	
}
.z-combobox-rounded .z-combobox-rounded-btn-right-edge.z-combobox-rounded-btn-readonly,
.z-combobox-rounded i.z-combobox-rounded-btn-right-edge-readonly,
.z-bandbox-rounded .z-bandbox-rounded-btn-right-edge.z-bandbox-rounded-btn-readonly,
.z-bandbox-rounded i.z-bandbox-rounded-btn-right-edge-readonly,
.z-datebox-rounded .z-datebox-rounded-btn-right-edge.z-datebox-rounded-btn-readonly,
.z-datebox-rounded i.z-datebox-rounded-btn-right-edge-readonly,
.z-timebox-rounded .z-timebox-rounded-btn-right-edge.z-timebox-rounded-btn-readonly,
.z-timebox-rounded i.z-timebox-rounded-btn-right-edge-readonly,
.z-spinner-rounded .z-spinner-rounded-btn-right-edge.z-spinner-rounded-btn-readonly,
.z-spinner-rounded i.z-spinner-rounded-btn-right-edge-readonly {
	background-position: -19px -192px;
}
.z-combobox-rounded .z-combobox-rounded-btn-readonly,
.z-bandbox-rounded .z-bandbox-rounded-btn-readonly,
.z-datebox-rounded .z-datebox-rounded-btn-readonly,
.z-timebox-rounded .z-timebox-rounded-btn-readonly,
.z-spinner-rounded .z-spinner-rounded-btn-readonly {
	background-position: 0 -192px;
}

.z-spinner-focus .z-spinner-readonly,
.z-timebox-focus .z-timebox-readonly,
.z-datebox-focus .z-datebox-readonly,
.z-bandbox-focus .z-bandbox-readonly,
.z-combobox-focus .z-combobox-readonly {
	background-image: url(${c:encodeURL('~./zul/img/button/readonly-focus-bg.gif')});
}
<%-- focus inp btn readonly --%>
.z-combobox-rounded-focus .z-combobox-rounded-readonly,
.z-bandbox-rounded-focus .z-bandbox-rounded-readonly,
.z-datebox-rounded-focus .z-datebox-rounded-readonly,
.z-timebox-rounded-focus .z-timebox-rounded-readonly,
.z-spinner-rounded-focus .z-spinner-rounded-readonly {
	background-position: 0 -96px;
}
.z-combobox-rounded-focus .z-combobox-rounded-readonly {
	background-image: url(${c:encodeURL('~./zul/img/button/combobox-rounded.gif')});
}
.z-bandbox-rounded-focus .z-bandbox-rounded-readonly {
	background-image: url(${c:encodeURL('~./zul/img/button/bandbox-rounded.gif')});
}
.z-datebox-rounded-focus .z-datebox-rounded-readonly {
	background-image: url(${c:encodeURL('~./zul/img/button/datebox-rounded.gif')});
}
.z-timebox-rounded-focus .z-timebox-rounded-readonly,
.z-spinner-rounded-focus .z-spinner-rounded-readonly {
	background-image: url(${c:encodeURL('~./zul/img/button/timebox-rounded.gif')});	
}
.z-combobox-rounded-focus .z-combobox-rounded-btn-right-edge.z-combobox-rounded-btn-readonly,
.z-combobox-rounded-focus i.z-combobox-rounded-btn-right-edge-readonly,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn-right-edge.z-bandbox-rounded-btn-readonly,
.z-bandbox-rounded-focus i.z-bandbox-rounded-btn-right-edge-readonly,
.z-datebox-rounded-focus .z-datebox-rounded-btn-right-edge.z-datebox-rounded-btn-readonly,
.z-datebox-rounded-focus i.z-datebox-rounded-btn-right-edge-readonly,
.z-timebox-rounded-focus .z-timebox-rounded-btn-right-edge.z-timebox-rounded-btn-readonly,
.z-timebox-rounded-focus i.z-timebox-rounded-btn-right-edge-readonly,
.z-spinner-rounded-focus .z-spinner-rounded-btn-right-edge.z-spinner-rounded-btn-readonly,
.z-spinner-rounded-focus i.z-spinner-rounded-btn-right-edge-readonly {
	background-position: -19px -216px;
}
.z-combobox-rounded-focus .z-combobox-rounded-btn-readonly,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn-readonly,
.z-datebox-rounded-focus .z-datebox-rounded-btn-readonly,
.z-timebox-rounded-focus .z-timebox-rounded-btn-readonly,
.z-spinner-rounded-focus .z-spinner-rounded-btn-readonly {
	background-position: 0 -216px;
}
<%-- Inplace editing --%>
.z-combobox-inplace,
.z-bandbox-inplace,
.z-datebox-inplace,
.z-timebox-inplace,
.z-spinner-inplace {
	border: 0;
	background: none;
	padding-top: 1px;
	padding-bottom: 1px;
}
.z-combobox-rounded-inplace .z-combobox-rounded-inp,
.z-bandbox-rounded-inplace .z-bandbox-rounded-inp,
.z-datebox-rounded-inplace .z-datebox-rounded-inp,
.z-timebox-rounded-inplace .z-timebox-rounded-inp,
.z-spinner-rounded-inplace .z-spinner-rounded-inp,
.z-combobox-inplace .z-combobox-inp,
.z-bandbox-inplace .z-bandbox-inp,
.z-datebox-inplace .z-datebox-inp,
.z-timebox-inplace .z-timebox-inp,
.z-spinner-inplace .z-spinner-inp {
	padding: 2px 1px;
	border: 0;
	background: none;
	<c:if test="${c:isExplorer() || c:isOpera()}">
	padding: 2px;
		<c:if test="${c:browser('ie8')}">
			padding: 1px;
			padding-left: 2px;
			padding-right: 2px;
		</c:if>
	</c:if>
}
.z-combobox-rounded-inplace .z-combobox-rounded-inp,
.z-bandbox-rounded-inplace .z-bandbox-rounded-inp,
.z-datebox-rounded-inplace .z-datebox-rounded-inp,
.z-timebox-rounded-inplace .z-timebox-rounded-inp,
.z-spinner-rounded-inplace .z-spinner-rounded-inp {
	padding-top: 5px;
	padding-bottom: 5px;
	background: none !important;
}
.z-combobox-inplace .z-combobox-inp,
.z-bandbox-inplace .z-bandbox-inp,
.z-datebox-inplace .z-datebox-inp,
.z-timebox-inplace .z-timebox-inp,
.z-spinner-inplace .z-spinner-inp {
	border-right-width: 0 !important;
}

.z-combobox-inplace .z-combobox-btn,
.z-bandbox-inplace .z-bandbox-btn,
.z-datebox-inplace .z-datebox-btn,
.z-timebox-inplace .z-timebox-btn,
.z-spinner-inplace .z-spinner-btn {
	display: none;
}

.z-combobox-rounded-inplace .z-combobox-rounded-btn,
.z-bandbox-rounded-inplace .z-bandbox-rounded-btn,
.z-datebox-rounded-inplace .z-datebox-rounded-btn,
.z-timebox-rounded-inplace .z-timebox-rounded-btn,
.z-spinner-rounded-inplace .z-spinner-rounded-btn {
	visibility: hidden;
	background: none !important;
}
<%-- IE --%>
<c:if test="${c:isExplorer()}">
.z-combobox-rounded-pp .z-comboitem-inner,
.z-combobox-pp .z-comboitem-inner {<%--description--%>
	padding-left: 5px;
}
</c:if>

<%-- Gecko --%>
<c:if test="${c:isGecko()}">
i.z-combobox-rounded-btn, i.z-datebox-rounded-btn, i.z-bandbox-rounded-btn,
i.z-timebox-rounded-btn, i.z-spinner-rounded-btn,
i.z-combobox-btn, i.z-datebox-btn, i.z-bandbox-btn, i.z-timebox-btn,
i.z-spinner-btn {<%-- button at the right edge --%>
	margin: 0; padding: 0;
}
</c:if>
<c:if test="${c:browser('gecko2-')}">
.z-spinner-rounded,
.z-timebox-rounded,
.z-datebox-rounded,
.z-bandbox-rounded,
.z-combobox-rounded,
.z-spinner,
.z-timebox,
.z-datebox,
.z-bandbox,
.z-combobox {
	min-height: 18px;
}
</c:if>