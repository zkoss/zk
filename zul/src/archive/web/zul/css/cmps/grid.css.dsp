<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<c:set var="fontSizeM" value="small" scope="request" if="${empty fontSizeM}"/>
<c:set var="fontSizeS" value="x-small" scope="request" if="${empty fontSizeS}"/>
<c:set var="fontSizeXS" value="xx-small" scope="request" if="${empty fontSizeXS}"/>

<%-- Grid --%>
div.z-grid {
	background: #DAE7F6; border: 1px solid #7F9DB9; overflow: hidden; zoom: 1;
}
div.z-grid-header, div.z-grid-header tr, div.z-grid-footer {
	border: 0; overflow: hidden; width: 100%;
}
div.z-grid-header tr {
	background-image: url(${c:encodeURL('~./zul/img/grid/s_hd.gif')});
}
div.z-grid-header th {
	overflow: hidden; border: 1px solid;
	border-color: #DAE7F6 #9EB6CE #9EB6CE #DAE7F6;
	white-space: nowrap; padding: 2px;
	font-size: ${fontSizeM}; font-weight: normal;
}
div.z-grid-header .z-column-sort div.z-column-content {
	cursor: pointer; padding-right: 9px;
	background:transparent url(${c:encodeURL('~./zul/img/sort/v_hint.gif')});
	background-position: 99% center;
	background-repeat: no-repeat;
}
div.z-grid-header .z-column-sort-asc div.z-column-content {
	cursor: pointer; padding-right: 9px;
	background:transparent url(${c:encodeURL('~./zul/img/sort/v_asc.gif')});
	background-position: 99% center;
	background-repeat: no-repeat;
}
div.z-grid-header .z-column-sort-asc, div.z-grid-header .z-column-sort-dsc {
	background: #DDEEFB url(${c:encodeURL('~./zul/img/grid/column-over.gif')}) repeat-x 0 0;
}
div.z-grid-header .z-column-sort-dsc div.z-column-content {
	cursor: pointer; padding-right: 9px;
	background:transparent url(${c:encodeURL('~./zul/img/sort/v_dsc.gif')});
	background-position: 99% center;
	background-repeat: no-repeat;
}
div.z-column-content {
	font-size: ${fontSizeM}; font-weight: normal; font-family: Tahoma, Garamond, Century, Arial, serif;
}
div.z-grid-body {
	background: white; border: 0; overflow: auto; width: 100%;
}
div.z-grid-pgi-b {
	border-top: 1px solid #AAB; overflow: hidden;
}
div.z-grid-pgi-t {
	border-bottom: 1px solid #AAB; overflow: hidden;
}
div.z-grid-footer {
	background: #DAE7F6; border-top: 1px solid #9EB6CE;
}
div.z-footer-content, div.z-row-content, div.z-group-content, div.z-group-foot-content, div.z-column-content {
	border: 0; margin: 0; padding: 0;
}
div.z-footer-content, div.z-column-content{
	overflow: hidden;
}
.z-word-wrap div.z-row-content,
.z-word-wrap div.z-group-content,
.z-word-wrap div.z-group-foot-content,
.z-word-wrap div.z-footer-content, .z-word-wrap div.z-column-content {
	word-wrap: break-word;
}
<%-- faker uses only --%>
tr.z-grid-faker, tr.z-grid-faker th, tr.z-grid-faker div {
	border-top: 0 !important; border-bottom: 0 !important; margin-top: 0 !important;
	margin-bottom: 0 !important; padding-top: 0 !important;	padding-bottom: 0 !important;
	height: 0px !important; <%-- these above css cannot be overrided--%>
	border-left: 0; border-right: 0; margin-left: 0; margin-right: 0; padding-left: 0;
	padding-right: 0;
}
td.z-row-inner, td.z-group-inner, td.z-group-foot-inner {
	padding: 2px; overflow: hidden; 
}
div.z-row-content, div.z-group-content, div.z-group-foot-content {
	font-size: ${fontSizeM}; font-weight: normal; color: black;
}
tr.z-row td.z-row-inner, tr.z-row td.z-group-inner, tr.z-row td.z-group-foot-inner {
	background: white; border-top: none; border-left: 1px solid white;
	border-right: 1px solid #CCC; border-bottom: 1px solid #DDD;
}
tr.z-grid-odd td.z-row-inner, tr.z-grid-odd {
	background: #EAF2F0;
}
<%-- Group --%>
td.z-group-inner {
	padding-top: 2px; border-bottom: 2px solid #84A6D4;
}
.z-group-inner .z-group-content span, .z-group-inner .z-group-content {
	color:#3764a0; font: bold 11px tahoma, arial, helvetica, sans-serif;
	padding: 4px 2px; width: auto;
}
.z-group-inner {
	color: #3764a0; font: bold 11px tahoma, arial, helvetica, sans-serif;
}
<%-- Groupfooter --%>
.z-group-foot {
	background: #EAEFF5 url(${c:encodeURL('~./zul/img/grid/groupfoot_bg.gif')}) repeat-x 0 0;
}
.z-group-foot-inner .z-group-foot-content span, .z-group-foot-inner .z-group-foot-content {
	color: #2C559C; font: bold 12px Tahoma, Arial, Helvetica, sans-serif;
}
<%-- ZK Column's menu --%>
.z-column .z-column-content {
	position: relative;
}
.z-column-btn {
	background: #DDEEFB url(${c:encodeURL('~./zul/img/grid/hd-btn.gif')}) no-repeat left center;
	display: none; position: absolute; width: 14px; right: 0; top: 0; z-index: 2;
	cursor: pointer;
}
.z-column-over .z-column-btn, .z-column-visi .z-column-btn {
	display: block;
}
a.z-column-btn:hover {
	background-position: -14px center;
}
.z-column-over {
	background: #DDEEFB url(${c:encodeURL('~./zul/img/grid/column-over.gif')}) repeat-x 0 0;
}