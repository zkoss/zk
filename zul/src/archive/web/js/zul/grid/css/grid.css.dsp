<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- Grid --%>
div.z-grid {
	border: 1px solid #CFCFCF;
	background: #FFF;
	background-image: url(${c:encodeURL('~./zul/img/grid/column-bg.png')});
	overflow: hidden;
	zoom: 1;
}
div.z-grid-header, div.z-grid-header tr, div.z-grid-footer {
	border: 0; width: 100%;
}

div.z-grid-header, div.z-grid-footer {
	overflow: hidden;
}
div.z-grid-header tr.z-columns, div.z-grid-header tr.z-auxhead {
	background-repeat: repeat-x;
	background-color: white;
	background-image: url(${c:encodeURL('~./zul/img/grid/column-bg.png')});
}
div.z-grid-header th.z-column, div.z-grid-header th.z-auxheader {
	position: relative;
	overflow: hidden;
	border: 1px solid;
	white-space: nowrap;
	padding: 0 0 0 2px;
	font-size: ${fontSizeM}; font-weight: normal;
	border-color: #CFCFCF #CFCFCF #CFCFCF white;
	border-top: none;
}
div.z-grid-header .z-column-sort div.z-column-cnt {
	cursor: pointer;
	padding-right: 9px;
	background: transparent no-repeat 99% center;
}
div.z-grid-header .z-column-sort-asc div.z-column-cnt {
	cursor: pointer;
	padding-right: 9px;
	background-color:transparent;
	background-position: 99% center;
	background-repeat: no-repeat;
}
div.z-grid-header .z-column-sort-asc, div.z-grid-header .z-column-sort-dsc {
	background: #DDEEFB repeat-x 0 0;
}
div.z-grid-header .z-column-sort-dsc div.z-column-cnt {
	cursor: pointer;
	padding-right: 9px;
	background:transparent ;
	background-position: 99% center;
	background-repeat: no-repeat;
}
div.z-grid-body {
	background: white; border: 0; overflow: auto; width: 100%;
}
tbody.z-grid-empty-body td{
   	font-family: ${fontFamilyC};
    font-size: ${fontSizeM};
    font-weight: normal;
    color:#7d7d7d;
    font-style:italic;
    text-align:center;
}
div.z-grid-pgi-b {
	border-top: 1px solid #AAB; overflow: hidden; width: 100%;
}
div.z-grid-pgi-t {
	border-bottom: 1px solid #AAB; overflow: hidden; width: 100%;
}
div.z-grid-footer {
	background: #DAE7F6; border-top: 1px solid #9EB6CE;
}
div.z-footer-cnt, div.z-row-cnt, div.z-group-cnt, div.z-groupfoot-cnt, div.z-column-cnt {
	border: 0; margin: 0; padding: 0;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; font-weight: normal;
}
div.z-row-cnt {
	padding: 1px 0 1px 0;
}
div.z-footer-cnt, div.z-column-cnt{
	overflow: hidden;
	cursor: default;
	color: #636363;
}
.z-word-wrap div.z-row-cnt,
.z-word-wrap div.z-group-cnt,
.z-word-wrap div.z-groupfoot-cnt,
.z-word-wrap div.z-footer-cnt, .z-word-wrap div.z-column-cnt {
	word-wrap: break-word;
}
.z-column, .z-column .z-column-cnt,
.z-row .z-row-cnt,
.z-row .z-cell,
.z-footer .z-footer-cnt,
.z-auxheader, .z-auxheader .z-auxheader-cnt {
	text-overflow: ellipsis;
}
<%-- faker uses only --%>
tr.z-grid-faker, tr.z-grid-faker th, tr.z-grid-faker div {
	height: 0 !important;
	border-top: 0 !important; border-right : 0 !important;border-bottom: 0 !important;border-left: 0 !important;
	padding-top: 0 !important;	padding-right: 0 !important; padding-bottom: 0 !important;padding-left: 0 !important;
	margin-top: 0 !important; margin-right : 0 !important;margin-bottom: 0 !important;margin-left: 0 !important;
	font-size: ${fontSizeM} !important;
} <%-- these above css cannot be overrided--%>
.z-cell, td.z-row-inner, td.z-groupfoot-inner {
	padding: 0; 
	overflow: hidden;
}
div.z-column-cnt, div.z-row-cnt {
	font-family: arial;
	font-size: 12px;
	font-weight:bold;
	color: #636363;
}
div.z-column-cnt, div.z-grid-header div.z-auxheader-cnt {
	padding: 8px 4px 7px;
}
div.z-row-cnt {
	color: #363636;
}
tr.z-grid-odd td.z-row-inner,
tr.z-grid-odd .z-cell,
tr.z-grid-odd {
	background: #F0FAFF;
}
<%-- Group --%>
tr.z-group {
	background: #E9F2FB repeat-x 0 0;
	background-image:url(${c:encodeURL('~./zul/img/grid/group_bg.gif')});
}
td.z-group-inner {
	padding: 2px; overflow: hidden;
	color: #2C559C;
	font-weight: bold;
	font-size: ${fontSizeM};
	font-family: ${fontFamilyT};
	border-bottom:1px solid #CFCFCF;
	border-top:1px solid #CFCFCF;
}
.z-group-inner .z-group-cnt .z-label, .z-group-inner .z-group-cnt {
	color:#636363;
	padding: 4px 2px;
	width: auto;
	font-weight: bold;
	font-size: ${fontSizeM};
	font-family: ${fontFamilyT};
}
.z-group-inner .z-group-cnt {
	padding: 3px 2px 0;
}
.z-group-img {
	width: 18px;
	min-height: 18px;
	height: 100%;
	display:-moz-inline-box;
	vertical-align: top;
	display: inline-block;
	background-image: url(${c:encodeURL('~./zul/img/tree/arrow-toggle.gif')});
	background-repeat: no-repeat;
	vertical-align: top; cursor: pointer; border: 0;
}
.z-group-img-open {
	background-position: 0 -18px;
}
.z-group-img-close {
	background-position: 0 0;
}
<%-- Group foot --%>
.z-groupfoot {
	height: 25px;
	background: #E9F2FB repeat-x 0 0;
	background-image:url(${c:encodeURL('~./zul/img/grid/groupfoot_bg.gif')});
}
.z-groupfoot-inner .z-groupfoot-cnt .z-label, .z-groupfoot-inner .z-groupfoot-cnt {
	color:#636363;
	font-weight: bold;
	font-size: ${fontSizeM};
	font-family: ${fontFamilyT};
}
td.z-groupfoot-inner {
	padding-left: 9px;
}
div.z-grid-footer {
	background: #F9F9F9;
	border-top: 1px solid #CFCFCF;
}
td.z-footer {
	padding: 5px 10px 5px 9px;
}
<%-- ZK Column's menu --%>
.z-column .z-column-cnt {
	position: relative;
	<c:if test="${c:browser('ie6-')}">
		 zoom: 1;
	</c:if>
}
.z-column-btn {
	display: none;
	position: absolute;
	z-index: 15;
	cursor: pointer;
	top: 0;
	right: 0;
	width: 23px;
	background: url(${c:encodeURL('~./zul/img/grid/hd-btn.png')}) no-repeat scroll left 0 transparent;
	background-position: 0 0;
}
.z-column-over .z-column-btn, .z-column-visi .z-column-btn {
	display: block;
}
a.z-column-btn:hover {
	display: inline;
	background-position: right 0;
}
.z-column-over  {
	background-image: none;
}
.z-columns-menu-grouping .z-menu-item-img {
	background-image: url(${c:encodeURL('~./zul/img/grid/menu-group.png')});
}
.z-columns-menu-asc .z-menu-item-img {
	background-image: url(${c:encodeURL('~./zul/img/grid/menu-arrowup.png')});
}
.z-columns-menu-dsc .z-menu-item-img {
	background-image: url(${c:encodeURL('~./zul/img/grid/menu-arrowdown.png')});
}
div.z-grid-header th.z-column-over,
div.z-grid-header th.z-auxheader-over {
	background-image: url(${c:encodeURL('~./zul/img/grid/header-over.png')});
}
.z-column-sort-img {
	position: absolute;
	float: left;
	left: 50%;
	font-size: 0;
	margin-left: auto;
    margin-right: auto;
}
.z-column-sort .z-column-sort-img {
	margin-top:-5px;
	width: 8px;
	height: 5px;
}
div.z-grid-header .z-column-sort-asc, div.z-grid-header .z-column-sort-dsc {
	background: none transparent;
}
.z-column-sort-asc .z-column-sort-img,
.z-column-sort-dsc .z-column-sort-img {
    background-position: 0 0;
    background-repeat: no-repeat;
	background-image: url(${c:encodeURL('~./zul/img/grid/arrows.png')});
}
.z-column-sort-asc .z-column-sort-img {
	background-position: 0 0;
}
.z-column-sort-dsc .z-column-sort-img {
	background-position: 0 -5px;
}

.z-grid-header-bg {
	position:relative;
	margin-right: -11px;
	top: 0;
	height: 1px;
	font-size: 0;
	background-image: url(${c:encodeURL('~./zul/img/grid/head-bg.png')});
	margin-top: -1px;
}
<%-- ZK Column's sizing --%>
div.z-grid-header .z-column.z-column-sizing, 
div.z-grid-header .z-column.z-column-sizing div.z-column-cnt,
div.z-grid-header .z-column.z-column-sizing div.z-column-cnt .z-column-btn {
	cursor: e-resize;
}
.z-auxheader-cnt {
	font-weight: bold;
	font-size: ${fontSizeM};
	font-family: ${fontFamilyC};
	color: #636363;
}
div.z-grid-header .z-column-over-sort-asc, div.z-grid-header .z-column-over-sort-dsc {
	background-image:url(${c:encodeURL('~./zul/img/grid/column-bg.png')});
}
div.z-grid-header tr.z-columns th:last-child,
div.z-grid-header tr.z-auxhead th:last-child {
	border-right: none;
}
tr.z-row td.z-row-inner,
tr.z-row .z-cell {
	background: white;
	border: 1px solid white;
	border-right: 1px solid transparent;
	<c:if test="${c:browser('ie6-')}">
	border-right: 1px solid white;
	</c:if>
	padding: 4px 4px 4px 6px;
}
tr.z-grid-odd td.z-row-inner,
tr.z-grid-odd .z-cell {
	border-left: 1px solid transparent;
	background-color: #F7F7F7;
	border-top: 1px solid #F7F7F7;
	border-bottom: 1px solid #F7F7F7;
	border-left: 1px solid #FFF;
}
tr.z-grid-odd td.z-row-inner,
tr.z-grid-odd .z-cell,
tr.z-grid-odd {
	background: #F7F7F7;
}

tr.z-row-over > td.z-row-inner {
	border-top: 1px solid #e3f2ff;
	border-bottom: 1px solid #e3f2ff;
}


tr.z-row-over > td.z-row-inner, tr.z-row-over > .z-cell {
	background-image:url(${c:encodeURL('~./zul/img/grid/column-over.png')});
}

<%-- Autopaging --%>
.z-grid-autopaging .z-row-cnt {
	height: 30px;
	overflow: hidden;
}
<%-- IE --%>
<c:if test="${c:isExplorer()}">
div.z-grid-header, div.z-grid-footer {
	position:relative; <%-- Bug 1712708 and 1926094 --%>
}
div.z-column-cnt, .z-auxheader-cnt {
	white-space: nowrap; <%-- Bug #1839960  --%>
}
div.z-footer-cnt, div.z-row-cnt, div.z-group-cnt,
div.z-groupfoot-cnt, div.z-column-cnt, .z-auxheader-cnt {
	position: relative; <%-- Bug #1825896  --%>
}
div.z-footer-cnt,
div.z-row-cnt,
div.z-group-cnt,
div.z-groupfoot-cnt {
	width: 100%;
}
div.z-grid-body {
	position: relative; <%-- Bug 1766244 --%>
}
<c:if test="${!c:browser('ie8')}">
tr.z-grid-faker {
	position: absolute; top: -1000px; left: -1000px;<%-- fixed a white line for IE --%>
}
</c:if>
<c:if test="${c:browser('ie8')}">
.z-column, .z-auxheader {
	text-align: left;
}
</c:if>

<c:if test="${c:browser('ie6-')}">
div.z-grid {
	position:relative; <%-- Bug 1914215 and Bug 1914054 --%>
}
.z-columns-menu-grouping .z-menu-item-img {
	background-image:  url(${c:encodeURL('~./zul/img/grid/menu-group.gif')});
}
.z-columns-menu-asc .z-menu-item-img {
	background-image:  url(${c:encodeURL('~./zul/img/grid/menu-arrowup.gif')});
}
.z-columns-menu-dsc .z-menu-item-img {
	background-image:  url(${c:encodeURL('~./zul/img/grid/menu-arrowdown.gif')});
}
.z-group-inner .z-group-cnt {
	width: 100%;
}
tr.z-row td.z-row-inner {
	border-right: 1px solid #FFF;
}
.z-column-sort-asc .z-column-sort-img,
.z-column-sort-dsc .z-column-sort-img {
	background-image: url(${c:encodeURL('~./zul/img/grid/arrows.gif')});
}
</c:if>
<c:if test="${c:browser('ie6-') or c:browser('ie7-')}">
div.z-grid-header-bg {
	display: none;
}
</c:if>
</c:if>

<%-- Gecko (3.5 supports word-break )--%>
<c:if test="${c:isGecko() and !c:browser('gecko3.5')}">
.z-word-wrap div.z-row-cnt, 
.z-word-wrap div.z-group-cnt,
.z-word-wrap div.z-groupfoot-cnt,
.z-word-wrap div.z-footer-cnt,
.z-word-wrap div.z-column-cnt {
	overflow: hidden;
	-moz-binding: url(${c:encodeURL('~./zk/wordwrap.xml#wordwrap')});
}
</c:if>