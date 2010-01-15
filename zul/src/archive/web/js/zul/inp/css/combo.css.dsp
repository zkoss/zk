<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- Combobox --%>
.z-combobox,
.z-bandbox,
.z-datebox {
	border: 0; padding: 0; margin: 0; white-space: nowrap;
	font-family: ${fontFamilyC};font-size: ${fontSizeM};
	display:-moz-inline-box;
	display:inline-block;
}
.z-combobox-inp-simple,
.z-bandbox-inp-simple,
.z-datebox-inp-simple,
.z-timebox-inp-simple,
.z-spinner-inp-simple,
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
	-moz-border-radius: 2px 0 0 2px;
	-webkit-border-radius: 2px 0 0 2px;
}
/***************************************** inp default **************************************************************/
.z-combobox-inp-simple,
.z-bandbox-inp-simple,
.z-datebox-inp-simple,
.z-timebox-inp-simple,
.z-spinner-inp-simple {
	border:0;	
	padding: 5px 4px;
	<c:if test="${c:isSafari()}">
		margin:0;
	</c:if>
}
.z-combobox-inp-simple {
	background: #FFF url(${c:encodeURL('~./zul/img/button/combobox.gif')}) repeat-x 0 0;
}
.z-bandbox-inp-simple {
	background: #FFF url(${c:encodeURL('~./zul/img/button/bandbox.gif')}) repeat-x 0 0;
}
.z-datebox-inp-simple {
	background: #FFF url(${c:encodeURL('~./zul/img/button/datebox.gif')}) repeat-x 0 0;
}
.z-timebox-inp-simple,
.z-spinner-inp-simple {
	background: #FFF url(${c:encodeURL('~./zul/img/button/timebox.gif')}) repeat-x 0 0;	
}
/*******************************************************************************************************/
.z-combobox-focus .z-combobox-inp,
.z-bandbox-focus .z-bandbox-inp,
.z-datebox-focus .z-datebox-inp,
.z-timebox-focus .z-timebox-inp,
.z-spinner-focus .z-spinner-inp {
	border: 1px solid #90BCE6;
}
.z-combobox-text-invalid,
.z-bandbox-text-invalid,
.z-datebox-text-invalid,
.z-timebox-text-invalid,
.z-spinner-text-invalid {
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg-invalid.gif')}) repeat-x 0 0;
	border: 1px solid #DD7870;
	border-right-width: 1px !important;
}
.z-combobox input.z-combobox-simple-text-invalid,
.z-bandbox input.z-bandbox-simple-text-invalid,
.z-datebox input.z-datebox-simple-text-invalid,
.z-timebox input.z-timebox-simple-text-invalid,
.z-spinner input.z-spinner-simple-text-invalid {
	background: #FFF url(${c:encodeURL('~./zul/img/button/redcombo.gif')}) repeat-x 0 0;
}
.z-combobox .z-combobox-simple-text-invalid + i.z-combobox-btn-right-edge,
.z-bandbox .z-bandbox-simple-text-invalid + i.z-bandbox-btn-right-edge,
.z-datebox .z-datebox-simple-text-invalid + i.z-datebox-btn-right-edge,
.z-timebox .z-timebox-simple-text-invalid + i.z-timebox-btn-right-edge,
.z-spinner .z-spinner-simple-text-invalid + i.z-spinner-btn-right-edge {
	background: #FFF url(${c:encodeURL('~./zul/img/button/redcombo.gif')}) repeat-x 0 -24px;
}
i.z-combobox i.z-combobox-btn-right-edge-invalid,
i.z-bandbox i.z-bandbox-btn-right-edge-invalid,
i.z-datebox i.z-datebox-btn-right-edge-invalid,
i.z-timebox i.z-timebox-btn-right-edge-invalid,
i.z-spinner i.z-spinner-btn-right-edge-invalid {
	background: #FFF url(${c:encodeURL('~./zul/img/button/redcombo.gif')}) repeat-x 0 -24px;
}
.z-combobox .z-combobox-btn-simple,
.z-bandbox .z-bandbox-btn-simple,
.z-datebox .z-datebox-btn-simple,
.z-timebox .z-timebox-btn-simple,
.z-spinner .z-spinner-btn-simple,
.z-combobox .z-combobox-img,
.z-bandbox .z-bandbox-img,
.z-datebox .z-datebox-img,
.z-timebox .z-timebox-img,
.z-spinner .z-spinner-img {
	background: transparent no-repeat 0 0;
	background-image : url(${c:encodeURL('~./zul/img/button/combobtn.gif')});
	vertical-align: top; cursor: pointer; width: 17px; height: 19px; border: 0;
	border-bottom: 1px solid #86A4BE;
	overflow: hidden;
	display:-moz-inline-box; display:inline-block;
	-moz-border-radius: 0 2px 2px 0;
	-webkit-border-radius: 0 2px 2px 0;
}
/********************************************* btn default **********************************************************/

.z-combobox .z-combobox-btn-simple,
.z-bandbox .z-bandbox-btn-simple,
.z-datebox .z-datebox-btn-simple,
.z-timebox .z-timebox-btn-simple,
.z-spinner .z-spinner-btn-simple {
	border: 0;
	width: 24px; 
	height: 24px;
	<c:if test="${c:browser('ie7-') || c:browser('ie6-')}">
		margin-top: 1px;
	</c:if>
}
.z-combobox .z-combobox-btn-simple{	
	background: #FFF url(${c:encodeURL('~./zul/img/button/combobox.gif')}) repeat-x 0 -120px;
}
.z-bandbox .z-bandbox-btn-simple {
	background: #FFF url(${c:encodeURL('~./zul/img/button/bandbox.gif')}) repeat-x 0 -120px;
}
.z-datebox .z-datebox-btn-simple {
	background: #FFF url(${c:encodeURL('~./zul/img/button/datebox.gif')}) repeat-x 0 -120px;
}
.z-timebox .z-timebox-btn-simple,
.z-spinner .z-spinner-btn-simple {
	background: #FFF url(${c:encodeURL('~./zul/img/button/timebox.gif')}) repeat-x 0 -120px;
}
.z-combobox .z-combobox-btn-right-edge,
.z-bandbox .z-bandbox-btn-right-edge,
.z-datebox .z-datebox-btn-right-edge,
.z-timebox .z-timebox-btn-right-edge,
.z-spinner .z-spinner-btn-right-edge {
	background-position: -19px -120px;
	width: 5px;
	cursor: default;
}
/*******************************************************************************************************/
.z-combobox-btn-over .z-combobox-img,
.z-bandbox-btn-over .z-bandbox-img,
.z-datebox-btn-over .z-datebox-img,
.z-timebox-btn-over .z-timebox-img,
.z-spinner-btn-over .z-spinner-img {
	background-position: -17px 0;
}
/************************************************ inp btn over *******************************************************/
.z-combobox-inp-simple-over,
.z-bandbox-inp-simple-over,
.z-datebox-inp-simple-over,
.z-timebox-inp-simple-over,
.z-spinner-inp-simple-over {
	background-position: 0 -24px;
}
.z-combobox .z-combobox-btn-simple-over,
.z-bandbox .z-bandbox-btn-simple-over,
.z-datebox .z-datebox-btn-simple-over,
.z-timebox .z-timebox-btn-simple-over,
.z-spinner .z-spinner-btn-simple-over  {
	background-position: 0 -144px;
}
/*******************************************************************************************************/
.z-combobox-focus .z-combobox-img,
.z-bandbox-focus .z-bandbox-img,
.z-datebox-focus .z-datebox-img,
.z-timebox-focus .z-timebox-img,
.z-spinner-focus .z-spinner-img {
	background-position: -51px 0;
	border-bottom: 1px solid #80B9E9;
}
/*********************************************** btn focus ********************************************************/
.z-combobox-focus .z-combobox-btn-simple,
.z-bandbox-focus .z-bandbox-btn-simple,
.z-datebox-focus .z-datebox-btn-simple,
.z-timebox-focus .z-timebox-btn-simple,
.z-spinner-focus .z-spinner-btn-simple {
	background-position: 0 -192px;
}
.z-combobox-focus .z-combobox-btn-right-edge,
.z-bandbox-focus .z-bandbox-btn-right-edge,
.z-datebox-focus .z-datebox-btn-right-edge,
.z-timebox-focus .z-timebox-btn-right-edge,
.z-spinner-focus .z-spinner-btn-right-edge {
	background-position: -19px -120px;
}
/*******************************************************************************************************/
.z-combobox-focus .z-combobox-btn-over .z-combobox-img,
.z-bandbox-focus .z-bandbox-btn-over .z-bandbox-img,
.z-datebox-focus .z-datebox-btn-over .z-datebox-img,
.z-timebox-focus .z-timebox-btn-over .z-timebox-img,
.z-spinner-focus .z-spinner-btn-over .z-spinner-img {
	background-position: -68px 0;
}
/*************************************************** btn over focus ****************************************************/
.z-combobox-focus .z-combobox-btn-simple-over,
.z-bandbox-focus .z-bandbox-btn-simple-over,
.z-datebox-focus .z-datebox-btn-simple-over,
.z-timebox-focus .z-timebox-btn-simple-over,
.z-spinner-focus .z-spinner-btn-simple-over {
	background-position: 0 -216px;
}
/*******************************************************************************************************/
.z-combobox-focus .z-combobox-btn-clk .z-combobox-img, .z-combobox-btn-clk .z-combobox-img,
.z-bandbox-focus .z-bandbox-btn-clk .z-bandbox-img, .z-bandbox-btn-clk .z-bandbox-img,
.z-datebox-focus .z-datebox-btn-clk .z-datebox-img, .z-datebox-btn-clk .z-datebox-img,
.z-timebox-focus .z-timebox-btn-clk .z-timebox-img, .z-timebox-btn-clk .z-timebox-img,
.z-spinner-focus .z-spinner-btn-clk .z-spinner-img, .z-spinner-btn-clk .z-spinner-img {
	background-position: -34px 0;
}
/************************************************ inp btn click *******************************************************/
.z-combobox-focus .z-combobox-inp-simple-clk, .z-combobox .z-combobox-inp-simple-clk,
.z-bandbox-focus .z-bandbox-inp-simple-clk, .z-bandbox .z-bandbox-inp-simple-clk,
.z-datebox-focus .z-datebox-inp-simple-clk, .z-datebox .z-datebox-inp-simple-clk,
.z-timebox-focus .z-timebox-inp-simple-clk, .z-timebox .z-timebox-inp-simple-clk,
.z-spinner-focus .z-spinner-inp-simple-clk, .z-spinner .z-spinner-inp-simple-clk {
	background-position: 0 -48px;
}
.z-combobox-focus .z-combobox-btn-simple-clk, .z-combobox .z-combobox-btn-simple-clk,
.z-bandbox-focus .z-bandbox-btn-simple-clk, .z-bandbox .z-bandbox-btn-simple-clk,
.z-datebox-focus .z-datebox-btn-simple-clk, .z-datebox .z-datebox-btn-simple-clk,
.z-timebox-focus .z-timebox-btn-simple-clk, .z-timebox .z-timebox-btn-simple-clk,
.z-spinner-focus .z-spinner-btn-simple-clk, .z-spinner .z-spinner-btn-simple-clk {
	background-position: 0 -168px !important;
}
/*******************************************************************************************************/
.z-combobox-pp,
.z-bandbox-pp,
.z-datebox-pp {
	display: block; position: absolute; z-index: 88000;
	background: white; border: 1px solid #86A4BE; padding: 2px;
	font-size: ${fontSizeS};
}
.z-combobox-pp {
	font-family: ${fontFamilyC};
	overflow: auto;
}
<%-- Comboitem --%>
.z-combobox-pp .z-comboitem-text, .z-combobox-pp .z-comboitem-img {
	white-space: nowrap; font-size: ${fontSizeS}; cursor: pointer;
}
.z-combobox-pp .z-comboitem-inner, .z-combobox-pp .z-comboitem-cnt {<%--description--%>
	color: #888; font-size: ${fontSizeXS}; padding-left: 6px;
}
.z-combobox-pp .z-comboitem, .z-combobox-pp .z-comboitem a, .z-combobox-pp .z-comboitem a:visited {
	font-size: ${fontSizeM}; font-weight: normal; color: black;
	text-decoration: none;
}
.z-combobox-pp .z-comboitem a:hover {
	text-decoration: underline;
}
.z-combobox-pp .z-comboitem-seld {
	background: #b3c8e8; border: 1px solid #6f97d2;
}
.z-combobox-pp .z-comboitem-over {
	background: #D3EFFA;
}
.z-combobox-pp .z-comboitem-over-seld {
	background: #82D5F8;
}

<%-- Bandbox trendy mold --%>
.z-bandbox .z-bandbox-img {
	background-image : url(${c:encodeURL('~./zul/img/button/bandbtn.gif')});
}
<%-- Datebox --%>
.z-datebox-over{
	background: #dae7f6;
}
.z-datebox .z-datebox-img {
	background-image : url(${c:encodeURL('~./zul/img/button/datebtn.gif')});
}
.z-datebox-pp {
	border: 1px solid #888888;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	font-weight: normal;
}
.z-datebox-pp .z-datebox-calyear {
	background: #d8e8f0;
}
.z-datebox-time {
	width: 20px;
	padding: 0;
	margin: 0;
}
.z-datebox-time {
	text-align: center;	
}
.z-datebox-time-up,
.z-datebox-time-down { 	
	width: 0; 
	height: 0;
	margin: 0;
	padding: 0;
	cursor: pointer;
}
.z-datebox-time-up {
	border-color: white white #004A7F;
	border-style: none solid solid solid ;
	border-width: 5px;
	<c:if test="${c:browser('ie') || c:isOpera() || c:browser('gecko2-')}">
		border-width: 6px;
	</c:if>
	overflow: hidden;
}

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
.z-datebox-time-up.z-datebox-time-over {
	border-color: white white #BFBFBF;
}
.z-datebox-time-down.z-datebox-time-over {
	border-color: #BFBFBF white white;
}
<%-- Timebox and Spinner --%>
.z-timebox,
.z-spinner {
	display:-moz-inline-box;
	display:inline-block;
}

.z-timebox .z-timebox-img,
.z-spinner .z-spinner-img {
	background-image : url(${c:encodeURL('~./zul/img/button/timebtn.gif')});
}
<%-- Shadow --%>
.z-combobox-shadow, .z-bandbox-shadow, .z-datebox-shadow {
	-moz-border-radius: 3px;
	-moz-box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
	-webkit-border-radius: 3px;
	-webkit-box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
}
<%-- disable --%>
.z-spinner-simple-disd,
.z-timebox-simple-disd,
.z-datebox-simple-disd,
.z-bandbox-simple-disd,
.z-combobox-simple-disd,
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
.z-spinner-simple-disd, .z-spinner-simple-disd *,
.z-timebox-simple-disd, .z-timebox-simple-disd *,
.z-datebox-simple-disd, .z-datebox-simple-disd *,
.z-bandbox-simple-disd, .z-bandbox-simple-disd *,
.z-combobox-simple-disd, .z-combobox-simple-disd *,
.z-spinner-disd, .z-spinner-disd *,
.z-timebox-disd, .z-timebox-disd *,
.z-datebox-disd, .z-datebox-disd *,
.z-bandbox-disd, .z-bandbox-disd *,
.z-comboitem-disd, .z-comboitem-disd *,
.z-combobox-disd, .z-combobox-disd * {
	cursor: default !important;
	color: #AAA !important;
}
.z-timebox-simple-disd,
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
.z-spinner-inp-simple-readonly,
.z-timebox-inp-simple-readonly,
.z-datebox-inp-simple-readonly,
.z-bandbox-inp-simple-readonly,
.z-combobox-inp-simple-readonly,
.z-spinner-focus .z-spinner-inp-simple-readonly,
.z-timebox-focus .z-timebox-inp-simple-readonly,
.z-datebox-focus .z-datebox-inp-simple-readonly,
.z-bandbox-focus .z-bandbox-inp-simple-readonly,
.z-combobox-focus .z-combobox-inp-simple-readonly,
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
/********************************************* inp btn readonly **********************************************************/
.z-spinner-inp-simple-readonly,
.z-timebox-inp-simple-readonly,
.z-datebox-inp-simple-readonly,
.z-bandbox-inp-simple-readonly,
.z-combobox-inp-simple-readonly,
.z-spinner-focus .z-spinner-inp-simple-readonly,
.z-timebox-focus .z-timebox-inp-simple-readonly,
.z-datebox-focus .z-datebox-inp-simple-readonly,
.z-bandbox-focus .z-bandbox-inp-simple-readonly,
.z-combobox-focus .z-combobox-inp-simple-readonly {
	padding-right: 4px;
}
.z-combobox-inp-simple-readonly {
	background: #FFF url(${c:encodeURL('~./zul/img/button/combobox.gif')}) repeat-x 0 -72px;
}
.z-bandbox-inp-simple-readonly {
	background: #FFF url(${c:encodeURL('~./zul/img/button/bandbox.gif')}) repeat-x 0 -72px;
}
.z-datebox-inp-simple-readonly {
	background: #FFF url(${c:encodeURL('~./zul/img/button/datebox.gif')}) repeat-x 0 -72px;
}
.z-timebox-inp-simple-readonly,
.z-spinner-inp-simple-readonly {
	background: #FFF url(${c:encodeURL('~./zul/img/button/timebox.gif')}) repeat-x 0 -72px;	
}
.z-combobox .z-combobox-btn-right-edge.z-combobox-btn-simple-readonly,
.z-combobox i.z-combobox-btn-right-edge-readonly,
.z-bandbox .z-bandbox-btn-right-edge.z-bandbox-btn-simple-readonly,
.z-bandbox i.z-bandbox-btn-right-edge-readonly,
.z-datebox .z-datebox-btn-right-edge.z-datebox-btn-simple-readonly,
.z-datebox i.z-datebox-btn-right-edge-readonly,
.z-timebox .z-timebox-btn-right-edge.z-timebox-btn-simple-readonly,
.z-timebox i.z-timebox-btn-right-edge-readonly,
.z-spinner .z-spinner-btn-right-edge.z-spinner-btn-simple-readonly,
.z-spinner i.z-spinner-btn-right-edge-readonly {
	background-position: -19px -192px;
}
.z-combobox .z-combobox-btn-simple-readonly,
.z-bandbox .z-bandbox-btn-simple-readonly,
.z-datebox .z-datebox-btn-simple-readonly,
.z-timebox .z-timebox-btn-simple-readonly,
.z-spinner .z-spinner-btn-simple-readonly {
	background-position: 0 -192px;
}
/****************************************************************************************************************************/
.z-spinner-focus .z-spinner-readonly,
.z-timebox-focus .z-timebox-readonly,
.z-datebox-focus .z-datebox-readonly,
.z-bandbox-focus .z-bandbox-readonly,
.z-combobox-focus .z-combobox-readonly {
	background-image: url(${c:encodeURL('~./zul/img/button/readonly-focus-bg.gif')});
}
/************************************************* focus inp btn readonly *******************************************/
.z-combobox-focus .z-combobox-inp-simple-readonly {
	background: #FFF url(${c:encodeURL('~./zul/img/button/combobox.gif')}) repeat-x 0 -96px;
}
.z-bandbox-focus .z-bandbox-inp-simple-readonly {
	background: #FFF url(${c:encodeURL('~./zul/img/button/bandbox.gif')}) repeat-x 0 -96px;
}
.z-datebox-focus .z-datebox-inp-simple-readonly {
	background: #FFF url(${c:encodeURL('~./zul/img/button/datebox.gif')}) repeat-x 0 -96px;
}
.z-timebox-focus .z-timebox-inp-simple-readonly,
.z-spinner-focus .z-spinner-inp-simple-readonly {
	background: #FFF url(${c:encodeURL('~./zul/img/button/timebox.gif')}) repeat-x 0 -96px;	
}
.z-combobox-focus .z-combobox-btn-right-edge.z-combobox-btn-simple-readonly,
.z-combobox-focus i.z-combobox-btn-right-edge-readonly,
.z-bandbox-focus .z-bandbox-btn-right-edge.z-bandbox-btn-simple-readonly,
.z-bandbox-focus i.z-bandbox-btn-right-edge-readonly,
.z-datebox-focus .z-datebox-btn-right-edge.z-datebox-btn-simple-readonly,
.z-datebox-focus i.z-datebox-btn-right-edge-readonly,
.z-timebox-focus .z-timebox-btn-right-edge.z-timebox-btn-simple-readonly,
.z-timebox-focus i.z-timebox-btn-right-edge-readonly,
.z-spinner-focus .z-spinner-btn-right-edge.z-spinner-btn-simple-readonly,
.z-spinner-focus i.z-spinner-btn-right-edge-readonly {
	background-position: -19px -216px;
}
.z-combobox-focus .z-combobox-btn-simple-readonly,
.z-bandbox-focus .z-bandbox-btn-simple-readonly,
.z-datebox-focus .z-datebox-btn-simple-readonly,
.z-timebox-focus .z-timebox-btn-simple-readonly,
.z-spinner-focus .z-spinner-btn-simple-readonly {
	background-position: 0 -216px;
}
/*************************************************************************************************************/
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
.z-combobox-inplace .z-combobox-inp-simple,
.z-bandbox-inplace .z-bandbox-inp-simple,
.z-datebox-inplace .z-datebox-inp-simple,
.z-timebox-inplace .z-timebox-inp-simple,
.z-spinner-inplace .z-spinner-inp-simple {
	padding-top: 5px;
	padding-bottom: 5px;
	background: none !important;
}
.z-combobox-inplace .z-combobox-inp-simple,
.z-bandbox-inplace .z-bandbox-inp-simple,
.z-datebox-inplace .z-datebox-inp-simple,
.z-timebox-inplace .z-timebox-inp-simple,
.z-spinner-inplace .z-spinner-inp-simple,
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
.z-combobox-inplace .z-combobox-inp,
.z-bandbox-inplace .z-bandbox-inp,
.z-datebox-inplace .z-datebox-inp,
.z-timebox-inplace .z-timebox-inp,
.z-spinner-inplace .z-spinner-inp {
	border-right-width: 0 !important;
}

.z-combobox-inplace .z-combobox-btn-simple,
.z-bandbox-inplace .z-bandbox-btn-simple,
.z-datebox-inplace .z-datebox-btn-simple,
.z-timebox-inplace .z-timebox-btn-simple,
.z-spinner-inplace .z-spinner-btn-simple,
.z-combobox-inplace .z-combobox-btn,
.z-bandbox-inplace .z-bandbox-btn,
.z-datebox-inplace .z-datebox-btn,
.z-timebox-inplace .z-timebox-btn,
.z-spinner-inplace .z-spinner-btn {
	display: none;
}
<%-- IE --%>
<c:if test="${c:isExplorer()}">
.z-combobox-pp .z-comboitem-inner {<%--description--%>
	padding-left: 5px;
}
</c:if>

<%-- Gecko --%>
<c:if test="${c:isGecko()}">
span.z-combobox-btn, span.z-datebox-btn, span.z-bandbox-btn, span.z-timebox-btn,
span.z-spinner-btn {<%-- button at the right edge --%>
	margin: 0; padding: 0;
}
</c:if>
<c:if test="${c:browser('gecko2-')}">
.z-spinner,
.z-timebox,
.z-datebox,
.z-bandbox,
.z-combobox {
	min-height: 18px;
}
</c:if>