<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<c:set var="fontSizeM" value="small" scope="request" if="${empty fontSizeM}"/>
<c:set var="fontSizeS" value="x-small" scope="request" if="${empty fontSizeS}"/>
<c:set var="fontSizeXS" value="xx-small" scope="request" if="${empty fontSizeXS}"/>

<%-- Combobox --%>
.z-combobox {
	border: 0; padding: 0; margin: 0; white-space: nowrap;
}
.z-combobox-disd {
	color: gray !important; cursor: default !important; opacity: .6; -moz-opacity: .6; filter: alpha(opacity=60);
}
.z-combobox-disd * {
	color: gray !important; cursor: default !important;
}
.z-combobox-inp {
	background: #FFF url(${c:encodeURL('~./zul/img/grid/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #7F9DB9;
}
.z-combobox-focus .z-combobox-inp {
	border: 1px solid #90BCE6;
}
.z-combobox-text-invalid {
	background: #FFF url(${c:encodeURL('~./zul/img/grid/text-bg-invalid.gif')}) repeat-x 0 0;
	border: 1px solid #DD7870;
}
.z-combobox-readonly, .z-combobox-text-disd {
	background: #ECEAE4;
}
.z-combobox .z-combobox-img {
	background: transparent url(${c:encodeURL('~./zul/img/button/combobtn.gif')}) no-repeat 0 0;
	vertical-align: top; cursor: pointer; width: 17px; height: 19px; border: 0; 
	border-bottom: 1px solid #B5B8C8;
}
.z-combobox-btn-over .z-combobox-img {
	background-position: -17px 0;
}
.z-combobox-btn-clk .z-combobox-img {
	background-position: -34px 0;
}
.z-combobox-focus .z-combobox-img {
	background-position: -51px 0;
}
.z-combobox-focus .z-combobox-btn-over .z-combobox-img {
	background-position: -68px 0;
}
.z-combobox-pp {
	display: block; position: absolute; z-index: 88000;
	background: white; border: 1px solid #7F9DB9; padding: 2px;
	font-size: ${fontSizeS}; overflow: auto;
}
<%-- Comboitem --%>
.z-combobox-pp .z-combo-item-text, .z-combobox-pp .z-combo-item-img {
	white-space: nowrap; font-size: ${fontSizeS}; cursor: pointer;
}
.z-combobox-pp .z-combo-item-inner, .z-combobox-pp .z-combo-item-cnt {<%--description--%>
	color: #888; font-size: ${fontSizeXS}; padding-left: 6px;
}
.z-combobox-pp .z-combo-item, .z-combobox-pp .z-combo-item a, .z-combobox-pp .z-combo-item a:visited {
	font-size: ${fontSizeM}; font-weight: normal; color: black;
	text-decoration: none;
}
.z-combobox-pp .z-combo-item a:hover {
	text-decoration: underline;
}
.z-combobox-pp .z-combo-item-seld {
	background: #b3c8e8; border: 1px solid #6f97d2;
}
.z-combobox-pp .z-combo-item-over {
	background: #D3EFFA;
}
.z-combobox-pp .z-combo-item-over-seld {
	background: #82D5F8;
}
.z-combo-item-disd {
	color: gray !important; cursor: default !important; opacity: .6; -moz-opacity: .6; filter: alpha(opacity=60);
}
.z-combo-item-disd * {
	color: gray !important; cursor: default !important;
}
<%-- Bandbox trendy mold --%>
.z-bandbox {
	border: 0; padding: 0; margin: 0; white-space: nowrap;
}
.z-bandbox-disd {
	color: gray !important; cursor: default !important; opacity: .6; -moz-opacity: .6; filter: alpha(opacity=60);
}
.z-bandbox-disd * {
	color: gray !important; cursor: default !important;
}
.z-bandbox-inp {
	background: #FFF url(${c:encodeURL('~./zul/img/grid/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #7F9DB9;
}
.z-bandbox-focus .z-bandbox-inp {
	border: 1px solid #90BCE6;
}
.z-bandbox-text-invalid {
	background: #FFF url(${c:encodeURL('~./zul/img/grid/text-bg-invalid.gif')}) repeat-x 0 0;
	border: 1px solid #DD7870;
}
.z-bandbox-readonly, .z-bandbox-text-disd {
	background: #ECEAE4;
}
.z-bandbox .z-bandbox-img {
	background: transparent url(${c:encodeURL('~./zul/img/button/bandbtn.gif')}) no-repeat 0 0;
	vertical-align: top; cursor: pointer; width: 17px; height: 19px; border: 0; 
	border-bottom: 1px solid #B5B8C8;
}
.z-bandbox-btn-over .z-bandbox-img {
	background-position: -17px 0;
}
.z-bandbox-btn-clk .z-bandbox-img {
	background-position: -34px 0;
}
.z-bandbox-focus .z-bandbox-img {
	background-position: -51px 0;
}
.z-bandbox-focus .z-bandbox-btn-over .z-bandbox-img {
	background-position: -68px 0;
}
.z-bandbox-pp {
	display: block; position: absolute; z-index: 88000;
	background: white; border: 1px solid #7F9DB9; padding: 2px;
	font-size: ${fontSizeS};
}

<%-- Datebox --%>
.z-datebox {
	border: 0; padding: 0; margin: 0; white-space: nowrap;
}
.z-datebox-disd {
	color: gray !important; cursor: default !important; opacity: .6; -moz-opacity: .6; filter: alpha(opacity=60);
}
.z-datebox-disd * {
	color: gray !important; cursor: default !important;
}
.z-datebox-inp {
	background: #FFF url(${c:encodeURL('~./zul/img/grid/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #7F9DB9;
}
.z-datebox-focus .z-datebox-inp {
	border: 1px solid #90BCE6;
}
.z-datebox-text-invalid {
	background: #FFF url(${c:encodeURL('~./zul/img/grid/text-bg-invalid.gif')}) repeat-x 0 0;
	border: 1px solid #DD7870;
}
.z-datebox-readonly, .z-datebox-text-disd {
	background: #ECEAE4;
}
.z-datebox .z-datebox-img {
	background: transparent url(${c:encodeURL('~./zul/img/button/datebtn.gif')}) no-repeat 0 0;
	vertical-align: top; cursor: pointer; width: 17px; height: 19px; border: 0; 
	border-bottom: 1px solid #B5B8C8;
}
.z-datebox-btn-over .z-datebox-img {
	background-position: -17px 0;
}
.z-datebox-btn-clk .z-datebox-img {
	background-position: -34px 0;
}
.z-datebox-focus .z-datebox-img {
	background-position: -51px 0;
}
.z-datebox-focus .z-datebox-btn-over .z-datebox-img {
	background-position: -68px 0;
}
.z-datebox-pp {
	display: block; position: absolute; z-index: 88000;
	background: white; border: 1px solid black; padding: 2px;
}
.z-datebox-pp table.calyear {
	background: #d8e8f0;
}

<%-- Timebox --%>
.z-timebox-disd {
	color: gray !important; cursor: default !important; opacity: .6; -moz-opacity: .6; filter: alpha(opacity=60);
}
.z-timebox-disd * {
	color: gray !important; cursor: default !important;
}
.z-timebox-inp {
	background: #FFF url(${c:encodeURL('~./zul/img/grid/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #7F9DB9;
}
.z-timebox-focus .z-timebox-inp {
	border: 1px solid #90BCE6;
}
.z-timebox-text-invalid {
	background: #FFF url(${c:encodeURL('~./zul/img/grid/text-bg-invalid.gif')}) repeat-x 0 0;
	border: 1px solid #DD7870;
}
.z-timebox-readonly, .z-timebox-text-disd {
	background: #ECEAE4;
}
.z-timebox .z-timebox-img {
	background: transparent url(${c:encodeURL('~./zul/img/button/timebtn.gif')}) no-repeat 0 0;
	vertical-align: top; cursor: pointer; width: 17px; height: 19px; border: 0; 
	border-bottom: 1px solid #B5B8C8;
}
.z-timebox-btn-over .z-timebox-img {
	background-position: -17px 0;
}
.z-timebox-btn-clk .z-timebox-img {
	background-position: -34px 0;
}
.z-timebox-focus .z-timebox-img {
	background-position: -51px 0;
}
.z-timebox-focus .z-timebox-btn-over .z-timebox-img {
	background-position: -68px 0;
}
<%-- Spinner --%>
.z-spinner-disd {
	color: gray !important; cursor: default !important; opacity: .6; -moz-opacity: .6; filter: alpha(opacity=60);
}
.z-spinner-disd * {
	color: gray !important; cursor: default !important;
}
.z-spinner-inp {
	background: #FFF url(${c:encodeURL('~./zul/img/grid/text-bg.gif')}) repeat-x 0 0;
	border: 1px solid #7F9DB9;
}
.z-spinner-focus .z-spinner-inp {
	border: 1px solid #90BCE6;
}
.z-spinner-text-invalid {
	background: #FFF url(${c:encodeURL('~./zul/img/grid/text-bg-invalid.gif')}) repeat-x 0 0;
	border: 1px solid #DD7870;
}
.z-spinner-readonly, .z-spinner-text-disd {
	background: #ECEAE4;
}
.z-spinner .z-spinner-img {
	background: transparent url(${c:encodeURL('~./zul/img/button/timebtn.gif')}) no-repeat 0 0;
	vertical-align: top; cursor: pointer; width: 17px; height: 19px; border: 0; 
	border-bottom: 1px solid #B5B8C8;
}
.z-spinner-btn-over .z-spinner-img {
	background-position: -17px 0;
}
.z-spinner-btn-clk .z-spinner-img {
	background-position: -34px 0;
}
.z-spinner-focus .z-spinner-img {
	background-position: -51px 0;
}
.z-spinner-focus .z-spinner-btn-over .z-spinner-img {
	background-position: -68px 0;
}