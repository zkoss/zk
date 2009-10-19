<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- Combobox --%>
.z-combobox {
	border: 0; padding: 0; margin: 0; white-space: nowrap;
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
	display:-moz-inline-box;
	display:inline-block;
}

.z-combobox-inp {
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #86A4BE;
	padding-top: 2px;
	padding-bottom: 2px;
}
.z-combobox-focus .z-combobox-inp {
	border: 1px solid #90BCE6;
}
.z-combobox-text-invalid {
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg-invalid.gif')}) repeat-x 0 0;
	border: 1px solid #DD7870;
}

.z-combobox .z-combobox-img {
	background-color : transparent;
	background-image : url(${c:encodeURL('~./zul/img/button/combobtn.gif')});
	background-position : 0 0;
	background-repeat : no-repeat;
	vertical-align: top; cursor: pointer; width: 17px; height: 19px; border: 0;
	border-bottom: 1px solid #86A4BE;
	overflow: hidden;
	display:-moz-inline-box; display:inline-block;
}
.z-combobox-btn-over .z-combobox-img {
	background-position: -17px 0;
}
.z-combobox-focus .z-combobox-img {
	background-position: -51px 0;
	border-bottom: 1px solid #80B9E9;
}
.z-combobox-focus .z-combobox-btn-over .z-combobox-img {
	background-position: -68px 0;
}
.z-combobox-focus .z-combobox-btn-clk .z-combobox-img, .z-combobox-btn-clk .z-combobox-img {
	background-position: -34px 0;
}
.z-combobox-pp {
	display: block; position: absolute; z-index: 88000;
	background: white; border: 1px solid #86A4BE; padding: 2px;
	font-size: ${fontSizeS}; overflow: auto;
	font-family: ${fontFamilyC};
	font-weight: normal;
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
.z-bandbox {
	border: 0; padding: 0; margin: 0; white-space: nowrap;
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
	display:-moz-inline-box;
	display:inline-block;
}

.z-bandbox-inp {
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #86A4BE;
	padding-top: 2px;
	padding-bottom: 2px;
}
.z-bandbox-focus .z-bandbox-inp {
	border: 1px solid #90BCE6;
}
.z-bandbox-text-invalid {
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg-invalid.gif')}) repeat-x 0 0;
	border: 1px solid #DD7870;
}

.z-bandbox .z-bandbox-img {
	background-color : transparent;
	background-image : url(${c:encodeURL('~./zul/img/button/bandbtn.gif')});
	background-position : 0 0;
	background-repeat : no-repeat;
	vertical-align: top; cursor: pointer; width: 17px; height: 19px; border: 0;
	border-bottom: 1px solid #86A4BE;
	overflow: hidden;
	display:-moz-inline-box; display:inline-block;
}
.z-bandbox-btn-over .z-bandbox-img {
	background-position: -17px 0;
}
.z-bandbox-focus .z-bandbox-img {
	background-position: -51px 0;
	border-bottom: 1px solid #80B9E9;
}
.z-bandbox-focus .z-bandbox-btn-over .z-bandbox-img {
	background-position: -68px 0;
}
.z-bandbox-focus .z-bandbox-btn-clk .z-bandbox-img, .z-bandbox-btn-clk .z-bandbox-img {
	background-position: -34px 0;
}
.z-bandbox-pp {
	display: block; position: absolute; z-index: 88000;
	background: white; border: 1px solid #86A4BE; padding: 2px;
	font-size: ${fontSizeS};
}
<%-- Datebox --%>
.z-datebox {
	border: 0; padding: 0; margin: 0; white-space: nowrap;
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
	display:-moz-inline-box;
	display:inline-block;
}
.z-datebox-over{
	background: #dae7f6;
}

.z-datebox-inp {
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #86A4BE;
	padding-top: 2px;
	padding-bottom: 2px;
}
.z-datebox-focus .z-datebox-inp {
	border: 1px solid #90BCE6;
}
.z-datebox-text-invalid {
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg-invalid.gif')}) repeat-x 0 0;
	border: 1px solid #DD7870;
}

.z-datebox .z-datebox-img {
	background-color : transparent;
	background-image : url(${c:encodeURL('~./zul/img/button/datebtn.gif')});
	background-position : 0 0;
	background-repeat : no-repeat;
	vertical-align: top; cursor: pointer; width: 17px; height: 19px; border: 0;
	border-bottom: 1px solid #86A4BE;
	overflow: hidden;
	display:-moz-inline-box; display:inline-block;
}
.z-datebox-btn-over .z-datebox-img {
	background-position: -17px 0;
}
.z-datebox-focus .z-datebox-img {
	background-position: -51px 0;
	border-bottom: 1px solid #80B9E9;
}
.z-datebox-focus .z-datebox-btn-over .z-datebox-img {
	background-position: -68px 0;
}
.z-datebox-focus .z-datebox-btn-clk .z-datebox-img, .z-datebox-btn-clk .z-datebox-img {
	background-position: -34px 0;
}
.z-datebox-pp {
	display: block; position: absolute; z-index: 88000;
	background: white; border: 1px solid #888888; padding: 2px;
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
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
<%-- Timebox --%>
.z-timebox {
	display:-moz-inline-box;
	display:inline-block;
}
.z-timebox-inp {
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #86A4BE;
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
	padding-top: 2px;
	padding-bottom: 2px;
}
.z-timebox-focus .z-timebox-inp {
	border: 1px solid #90BCE6;
}
.z-timebox-text-invalid {
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg-invalid.gif')}) repeat-x 0 0;
	border: 1px solid #DD7870;
}

.z-timebox .z-timebox-img {
	background-color : transparent;
	background-image : url(${c:encodeURL('~./zul/img/button/timebtn.gif')});
	background-position : 0 0;
	background-repeat : no-repeat;
	vertical-align: top; cursor: pointer; width: 17px; height: 19px; border: 0;
	border-bottom: 1px solid #86A4BE;
	overflow: hidden;
	display:-moz-inline-box; display:inline-block;
}
.z-timebox-btn-over .z-timebox-img {
	background-position: -17px 0;
}
.z-timebox-focus .z-timebox-img {
	background-position: -51px 0;
	border-bottom: 1px solid #80B9E9;
}
.z-timebox-focus .z-timebox-btn-over .z-timebox-img {
	background-position: -68px 0;
}
.z-timebox-focus .z-timebox-btn-clk .z-timebox-img, .z-timebox-btn-clk .z-timebox-img {
	background-position: -34px 0;
}
<%-- Spinner --%>
.z-spinner {
	display:-moz-inline-box;
	display:inline-block;
}
.z-spinner-inp {
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #86A4BE;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; font-weight: normal;
	padding-top: 2px;
	padding-bottom: 2px;
}
.z-spinner-focus .z-spinner-inp {
	border: 1px solid #90BCE6;
}
.z-spinner-text-invalid {
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg-invalid.gif')}) repeat-x 0 0;
	border: 1px solid #DD7870;
}
.z-spinner .z-spinner-img {
	background-color : transparent;
	background-image : url(${c:encodeURL('~./zul/img/button/timebtn.gif')});
	background-position : 0 0;
	background-repeat : no-repeat;
	vertical-align: top; cursor: pointer; width: 17px; height: 19px; border: 0;
	border-bottom: 1px solid #86A4BE;
	display:-moz-inline-box; display:inline-block;
}
.z-spinner-btn-over .z-spinner-img {
	background-position: -17px 0;
}
.z-spinner-focus .z-spinner-img {
	background-position: -51px 0;
	border-bottom: 1px solid #80B9E9;
}
.z-spinner-focus .z-spinner-btn-over .z-spinner-img {
	background-position: -68px 0;
}
.z-spinner-focus .z-spinner-btn-clk .z-spinner-img , .z-spinner-btn-clk .z-spinner-img {
	background-position: -34px 0;
}
<%-- Shadow --%>
.z-combobox-shadow, .z-bandbox-shadow, .z-datebox-shadow {
	-moz-box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
	-webkit-box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
}
<%-- disable --%>
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

.z-spinner-disd, .z-spinner-disd *,
.z-timebox-disd, .z-timebox-disd *,
.z-datebox-disd, .z-datebox-disd *,
.z-bandbox-disd, .z-bandbox-disd *,
.z-comboitem-disd, .z-comboitem-disd *,
.z-combobox-disd, .z-combobox-disd * {
	cursor: default !important;
	color: #AAA !important;
}

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
.z-combobox-readonly {
	background-color: transparent;
	background-image: url(${c:encodeURL('~./zul/img/button/readonly-bg.gif')});
	background-position: 0 0;
	background-repeat: repeat-x;
	border-right-width: 0;
	padding-right: 1px;
}

.z-spinner-focus .z-spinner-readonly,
.z-timebox-focus .z-timebox-readonly,
.z-datebox-focus .z-datebox-readonly,
.z-bandbox-focus .z-bandbox-readonly,
.z-combobox-focus .z-combobox-readonly {
	background-color: transparent;
	background-image: url(${c:encodeURL('~./zul/img/button/readonly-focus-bg.gif')});
	background-position: 0 0;
	background-repeat: repeat-x;
	border-right-width: 0;
	padding-right: 1px;
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
.z-combobox-inplace .z-combobox-inp,
.z-bandbox-inplace .z-bandbox-inp,
.z-datebox-inplace .z-datebox-inp,
.z-timebox-inplace .z-timebox-inp,
.z-spinner-inplace .z-spinner-inp {
	padding-left: 1px;
	padding-right: 1px;
	padding-top: 2px;
	padding-bottom: 2px;
	border: 0;
	background: none;
}
<c:if test="${c:isExplorer()}">
.z-combobox-inplace .z-combobox-inp,
.z-bandbox-inplace .z-bandbox-inp,
.z-datebox-inplace .z-datebox-inp,
.z-timebox-inplace .z-timebox-inp,
.z-spinner-inplace .z-spinner-inp {
	padding: 2px;
}

<c:if test="${c:browser('ie8')}">
.z-combobox-inplace .z-combobox-inp,
.z-bandbox-inplace .z-bandbox-inp,
.z-datebox-inplace .z-datebox-inp,
.z-timebox-inplace .z-timebox-inp,
.z-spinner-inplace .z-spinner-inp {
	padding: 1px;
	padding-left: 2px;
	padding-right: 2px;
}
</c:if>

</c:if>
<c:if test="${c:isOpera()}">
.z-combobox-inplace .z-combobox-inp,
.z-bandbox-inplace .z-bandbox-inp,
.z-datebox-inplace .z-datebox-inp,
.z-timebox-inplace .z-timebox-inp,
.z-spinner-inplace .z-spinner-inp {
	padding: 2px;
}
</c:if>
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