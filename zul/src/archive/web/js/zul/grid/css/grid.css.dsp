<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- Grid --%>
div.z-grid {
	background: #DAE7F6;
	border: 1px solid #86A4BE;
	overflow: hidden;
	zoom: 1;
}
div.z-grid-header, div.z-grid-header tr, div.z-grid-footer {
	border: 0; overflow: hidden; width: 100%;
}
div.z-grid-header tr.z-columns, div.z-grid-header tr.z-auxhead {
	background-color: #C3E7FB;
	background-repeat: repeat-x;
	background-image: url(${c:encodeURL('~./zul/img/grid/column-bg.png')});
}
div.z-grid-header th.z-column, div.z-grid-header th.z-auxheader {
	overflow: hidden;
	border: 1px solid;
	border-color: #DAE7F6 #9EB6CE #9EB6CE #DAE7F6;
	white-space: nowrap;
	padding: 2px;
	font-size: ${fontSizeM}; font-weight: normal;
}
div.z-grid-header .z-column-sort div.z-column-cnt {
	cursor: pointer;
	padding-right: 9px;
	background: transparent no-repeat 99% center;
	background-image: url(${c:encodeURL('~./zul/img/sort/v_hint.gif')});
}
div.z-grid-header .z-column-sort-asc div.z-column-cnt {
	cursor: pointer;
	padding-right: 9px;
	background-color:transparent;
	background-position: 99% center;
	background-repeat: no-repeat;
	background-image:url(${c:encodeURL('~./zul/img/sort/v_asc.gif')});
}
div.z-grid-header .z-column-sort-asc, div.z-grid-header .z-column-sort-dsc {
	background: #DDEEFB repeat-x 0 0;
	background-image:url(${c:encodeURL('~./zul/img/grid/column-over.png')});
}
div.z-grid-header .z-column-sort-dsc div.z-column-cnt {
	cursor: pointer;
	padding-right: 9px;
	background:transparent ;
	background-position: 99% center;
	background-repeat: no-repeat;
	background-image:url(${c:encodeURL('~./zul/img/sort/v_dsc.gif')});
}
div.z-grid-body {
	background: white; border: 0; overflow: auto; width: 100%;
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
}
.z-word-wrap div.z-row-cnt,
.z-word-wrap div.z-group-cnt,
.z-word-wrap div.z-groupfoot-cnt,
.z-word-wrap div.z-footer-cnt, .z-word-wrap div.z-column-cnt {
	word-wrap: break-word;
}
<%-- faker uses only --%>
tr.z-grid-faker, tr.z-grid-faker th, tr.z-grid-faker div {
	height: 0px !important;
	border-top: 0 !important; border-right : 0 !important;border-bottom: 0 !important;border-left: 0 !important;
	padding-top: 0 !important;	padding-right: 0 !important; padding-bottom: 0 !important;padding-left: 0 !important;
	margin-top: 0 !important; margin-right : 0 !important;margin-bottom: 0 !important;margin-left: 0 !important;
} <%-- these above css cannot be overrided--%>
td.z-row-inner, td.z-groupfoot-inner {
	padding: 2px; overflow: hidden;
}
div.z-row-cnt {
	color: black;
}
tr.z-row td.z-row-inner {
	background: white; border-top: none; border-left: 1px solid white;
	border-right: 1px solid #CCC; border-bottom: 1px solid #DDD;
}
tr.z-grid-odd td.z-row-inner, tr.z-grid-odd {
	background: #F0FAFF;
}
<%-- Group --%>
tr.z-group {
	background: #E9F2FB repeat-x 0 0;
	background-image:url(${c:encodeURL('~./zul/img/grid/group_bg.gif')});
}
td.z-group-inner {
	padding: 2px; overflow: hidden;
	border-top: 2px solid #81BAF5;
	border-bottom: 1px solid #bcd2ef;
	color: #2C559C;
	font-weight: bold;
	font-size: ${fontSizeM};
	font-family: ${fontFamilyT};
}
.z-group-inner .z-group-cnt .z-label, .z-group-inner .z-group-cnt {
	color:#2C559C;
	padding: 4px 2px; width: auto;
	font-weight: bold;
	font-size: ${fontSizeM};
	font-family: ${fontFamilyT};
}
.z-group-img {
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
.z-group-img-open {
	background-position: 0px -18px;
}
.z-group-img-close {
	background-position: 0px 0px;
}
<%-- Group foot --%>
.z-groupfoot {
	background: #E9F2FB repeat-x 0 0;
	background-image:url(${c:encodeURL('~./zul/img/grid/groupfoot_bg.gif')});
}
.z-groupfoot-inner .z-groupfoot-cnt .z-label, .z-groupfoot-inner .z-groupfoot-cnt {
	color: #2C559C;
	font-weight: bold;
	font-size: ${fontSizeM};
	font-family: ${fontFamilyT};
}
<%-- ZK Column's menu --%>
.z-column .z-column-cnt {
	position: relative;
}
.z-column-btn {
	background: transparent no-repeat left center;
	display: none; position: absolute; width: 14px; right: 0; top: 0; z-index: 15;
	cursor: pointer;
	background-image: url(${c:encodeURL('~./zul/img/grid/hd-btn.png')});
}
.z-column-over .z-column-btn, .z-column-visi .z-column-btn {
	display: block;
}
a.z-column-btn:hover {
	background-position: -14px center;
}
.z-column-over {
	background: #ACDDF9 repeat-x 0 0;
	background-image: url(${c:encodeURL('~./zul/img/grid/column-over.png')});
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
<%-- ZK Column's sizing --%>
.z-grid-header .z-column.z-column-sizing, .z-grid-header .z-column.z-column-sizing .z-column-cnt {
	cursor: e-resize;
}

<%-- IE --%>
<c:if test="${c:isExplorer()}">
div.z-grid-header, div.z-grid-footer {
	position:relative; <%-- Bug 1712708 and 1926094 --%>
}
div.z-grid-header th.z-column, div.z-grid-header th.z-auxheader {
	text-overflow: ellipsis;
}
div.z-column-cnt, .z-auxheader-cnt {
	white-space: nowrap; <%-- Bug #1839960  --%>
}
div.z-footer-cnt, div.z-row-cnt, div.z-group-cnt,
div.z-groupfoot-cnt, div.z-column-cnt, .z-auxheader-cnt {
	position: relative; <%-- Bug #1825896  --%>
}
div.z-row-cnt, div.z-group-cnt, div.z-groupfoot-cnt {
	width: 100%;
}
div.z-grid-body {
	position: relative; <%-- Bug 1766244 --%>
}
tr.z-grid-faker {
	position: absolute; top: -1000px; left: -1000px;<%-- fixed a white line for IE --%>
}

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
</c:if>
</c:if>

<%-- Gecko --%>
<c:if test="${c:isGecko()}">
.z-word-wrap div.z-row-cnt, 
.z-word-wrap div.z-group-cnt,
.z-word-wrap div.z-groupfoot-cnt,
.z-word-wrap div.z-footer-cnt,
.z-word-wrap div.z-column-cnt {
	overflow: hidden;
	-moz-binding: url(${c:encodeURL('~./zk/wordwrap.xml#wordwrap')});
}
</c:if>