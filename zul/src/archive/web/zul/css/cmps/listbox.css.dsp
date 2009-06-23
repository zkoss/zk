<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- Listbox --%>
div.z-listbox {
	background: #DAE7F6; border: 1px solid #86A4BE; overflow: hidden; zoom: 1;
}
div.z-listbox-header, div.z-listbox-header tr, div.z-listbox-footer {
	border: 0; overflow: hidden; width: 100%;
}

div.z-listbox-header tr.z-listhead, div.z-listbox-header tr.z-auxhead {
	background: #C7E5F1 repeat-x scroll 0 0;
	background-image: url(${c:encodeURL('~./zul/img/grid/column-bg.png')});
}
div.z-listbox-header th.z-listheader, div.z-listbox-header th.z-auxheader {
	overflow: hidden; border: 1px solid;
	border-color: #DAE7F6 #9EB6CE #9EB6CE #DAE7F6;
	white-space: nowrap; padding: 2px;
	font-size: ${fontSizeM}; font-weight: normal;
}
div.z-listbox-header th.z-listheader-sort div.z-listheader-cnt {
	cursor: pointer; padding-right: 9px;
	background: transparent no-repeat scroll 99% center;
	background-image: url(${c:encodeURL('~./zul/img/sort/v_hint.gif')});
}
div.z-listbox-header th.z-listheader-sort-asc div.z-listheader-cnt {
	cursor: pointer; padding-right: 9px;
	background: transparent no-repeat scroll 99% center;
	background-image: url(${c:encodeURL('~./zul/img/sort/v_asc.gif')});
}
div.z-listbox-header th.z-listheader-sort-dsc div.z-listheader-cnt {
	cursor: pointer; padding-right: 9px;
	background: transparent no-repeat scroll 99% center;
	background-image: url(${c:encodeURL('~./zul/img/sort/v_dsc.gif')});
}
div.z-listbox-body {
	background: white; border: 0; overflow: auto; width: 100%; position: relative;
}
div.z-listbox-pgi-b {
	border-top: 1px solid #AAB; overflow: hidden;
}
div.z-listbox-pgi-t {
	border-bottom: 1px solid #AAB; overflow: hidden;
}
div.z-listbox-body .z-listcell, div.z-listbox-footer .z-listfooter {
	cursor: pointer; padding: 0 2px;
	font-size: ${fontSizeM}; font-weight: normal; overflow: hidden;
}
div.z-listbox-footer {
	background: #DAE7F6; border-top: 1px solid #9EB6CE;
}
div.z-listfooter-cnt, div.z-listcell-cnt, div.z-listheader-cnt {
	border: 0; margin: 0; padding: 0;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; font-weight: normal;
}
div.z-listcell-cnt {
	padding: 1px 0 1px 0;
}
div.z-listfooter-cnt, div.z-listheader-cnt {
	overflow: hidden;
}
.z-word-wrap div.z-listcell-cnt, .z-word-wrap div.z-listfooter-cnt,
	.z-word-wrap div.z-listheader-cnt {
	word-wrap: break-word;
}
<%-- faker uses only --%>
tr.z-listbox-faker, tr.z-listbox-faker th, tr.z-listbox-faker div {
	height: 0px !important;
	border-top: 0 !important; border-right : 0 !important;border-bottom: 0 !important;border-left: 0 !important;
	padding-top: 0 !important;	padding-right: 0 !important; padding-bottom: 0 !important;padding-left: 0 !important;
	margin-top: 0 !important; margin-right : 0 !important;margin-bottom: 0 !important;margin-left: 0 !important;
	<%-- these above css cannot be overrided--%>
}
tr.z-listitem, tr.z-listitem a, tr.z-listitem a:visited {
	font-size: ${fontSizeM}; font-weight: normal; color: black;
	text-decoration: none;
}
tr.z-listitem a:hover {
	text-decoration: underline;
}
tr.z-listbox-odd {
	background: #E6F8FF;
}
tr.z-listitem-disd *, td.z-listcell-disd * {
	color: #C5CACB !important; cursor: default!important;
}
tr.z-listitem-disd a:visited, tr.z-listitem-disd a:hover,
td.z-listcell-disd a:visited, td.z-listcell-disd a:hover {
	text-decoration: none !important;
	cursor: default !important;;
	border-color: #D0DEF0 !important;
}
tr.z-listitem-seld {
	background: #b3c8e8; border: 1px solid #6f97d2;
}
tr.z-listitem-over {
	background: #dae7f6;
}
tr.z-listitem-over-seld {
	background: #6eadff;
}
tr.z-listitem td.z-listitem-focus {
	background-image: url(${c:encodeURL('~./zul/img/common/focusd.png')});
	background-repeat: no-repeat;
}
<%-- Listgroup --%>
tr.z-listgroup{
	background: #E9F2FB repeat-x 0 0;
	background-image: url(${c:encodeURL('~./zul/img/grid/group_bg.gif')});
}
td.z-listgroup-inner {
	padding-top: 2px;
	border-top: 2px solid #81BAF5;
	border-bottom: 1px solid #bcd2ef;
}
td.z-listgroup-inner div.z-listcell-cnt {
	color:#2C559C;
	padding: 4px 2px; width: auto;
	font-weight: bold;
	font-size: ${fontSizeM};
	font-family: ${fontFamilyT};
}
.z-listgroup-img {
	width: 18px;
	min-height: 18px;
	height: 100%;
	display:-moz-inline-box;
	vertical-align: top;
	display: inline-block;
	background-image: url(${c:encodeURL('~./zul/img/common/toggle.gif')});
	background-repeat: no-repeat;
	vertical-align: top; cursor: pointer; border: 0;
}
.z-listgroup-img-open {
	background-position: 0px -18px;
}
.z-listgroup-img-close {
	background-position: 0px 0px;
}
<%-- Listgroupfoot --%>
.z-listgroupfoot{
	background: #E9F2FB repeat-x 0 0;
	background-image: url(${c:encodeURL('~./zul/img/grid/groupfoot_bg.gif')});
}
td.z-listgroupfoot-inner div.z-listcell-cnt {
	color: #2C559C;
	font-weight: bold;
	font-size: ${fontSizeM};
	font-family: ${fontFamilyT};
}
<%-- ZK Listhead's sizing --%>
.z-listbox-header .z-listheader.z-listheader-sizing, .z-listbox-header .z-listheader.z-listheader-sizing .z-listheader-cnt {
	cursor: e-resize;
}
