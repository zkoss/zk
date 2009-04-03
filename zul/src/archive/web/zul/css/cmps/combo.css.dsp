<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- Combobox --%>
.z-combobox {
	border: 0; padding: 0; margin: 0; white-space: nowrap;
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
}

.z-combobox-inp {
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #7F9DB9;
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
	border-bottom: 1px solid #7F9DB9;
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
	background: white; border: 1px solid #7F9DB9; padding: 2px;
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
}

.z-bandbox-inp {
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #7F9DB9;
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
	background: white; border: 1px solid #7F9DB9; padding: 2px;
	font-size: ${fontSizeS};
}

<%-- Datebox --%>
.z-datebox {
	border: 0; padding: 0; margin: 0; white-space: nowrap;
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
}
.z-datebox-over{
	background: #dae7f6;
}

.z-datebox-inp {
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #7F9DB9;
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

<%-- Timebox --%>
.z-timebox-inp {
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #7F9DB9;
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
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
.z-spinner-inp {
	background: #FFF url(${c:encodeURL('~./zul/img/misc/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #7F9DB9;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; font-weight: normal;
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
.z-comboitem-readonly, .z-comboitem-text-disd,
.z-spinner-readonly, .z-spinner-text-disd,
.z-timebox-readonly, .z-timebox-text-disd,
.z-datebox-readonly, .z-datebox-text-disd,
.z-bandbox-readonly, .z-bandbox-text-disd,
.z-combobox-readonly, .z-combobox-text-disd {
	background: #ECEAE4;
}
