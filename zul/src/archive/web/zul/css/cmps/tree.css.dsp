<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- Tree --%>
div.z-tree {
	background: #DAE7F6; border: 1px solid #7F9DB9; overflow: hidden; zoom: 1;
}
div.z-tree-header, div.z-tree-header tr, div.z-tree-footer {
	border: 0; overflow: hidden; width: 100%;
}
div.z-tree-header tr.z-tree-cols, div.z-tree-header tr.z-auxhead {
	background-image: url(${c:encodeURL('~./zul/img/grid/s_hd.gif')});
}
div.z-tree-header th.z-tree-col, div.z-tree-header th.z-auxheader {
	overflow: hidden; border: 1px solid;
	border-color: #DAE7F6 #9EB6CE #9EB6CE #DAE7F6;
	white-space: nowrap; padding: 2px;
	font-size: ${fontSizeM}; font-weight: normal;
}
div.z-tree-body {
	background: white; border: 0; overflow: auto; width: 100%;
}
div.z-tree-pgi-b {
	border-top: 1px solid #AAB; overflow: hidden;
}
div.z-tree-pgi-t {
	border-bottom: 1px solid #AAB; overflow: hidden;
}
div.z-tree-body td.z-tree-cell, div.z-tree-footer td.z-tree-footer {
	cursor: pointer; padding: 0 2px;
	font-size: ${fontSizeM}; font-weight: normal; overflow: hidden; 
}
div.z-tree-footer {
	background: #DAE7F6; border-top: 1px solid #9EB6CE;
}

<%-- faker uses only for grid/listbox/tree --%>
tr.z-tree-faker, tr.z-tree-faker th, tr.z-tree-faker div {
	height: 0px !important; 	
	border-top: 0 !important; border-right : 0 !important;border-bottom: 0 !important;border-left: 0 !important;  
	padding-top: 0 !important;	padding-right: 0 !important; padding-bottom: 0 !important;padding-left: 0 !important;			
	margin-top: 0 !important; margin-right : 0 !important;margin-bottom: 0 !important;margin-left: 0 !important;  	
	<%-- these above css cannot be overrided--%>
}

<%-- tree icons --%>
span.z-tree-root-open, span.z-tree-tee-open, span.z-tree-last-open {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/open.png')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.z-tree-root-close, span.z-tree-tee-close, span.z-tree-last-close {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/close.png')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.z-tree-tee, span.z-tree-vbar, span.z-tree-last, span.z-tree-spacer, span.checkmark-spacer {
	width: 18px; min-height: 18px; height: 100%;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
<%-- Treecol, Treefooter, and Treecell--%>
div.z-tree-col-cnt {
	font-size: ${fontSizeM}; font-weight: normal;
	font-family: ${fontFamilyT};
}
div.z-tree-footer-cnt, div.z-tree-cell-cnt, div.z-tree-col-cnt {
	border: 0; margin: 0; padding: 0;
}
div.z-tree-footer-cnt, div.z-tree-col-cnt{
	overflow: hidden;
}
.z-word-wrap div.z-tree-cell-cnt, .z-word-wrap div.z-tree-footer-cnt,
	.z-word-wrap div.z-tree-col-cnt {
	word-wrap: break-word;
}
<%-- Treerow --%>
tr.z-tree-row, tr.z-tree-row a, tr.z-tree-row a:visited {
	font-size: ${fontSizeM}; font-weight: normal; color: black;
	text-decoration: none;
}
tr.z-tree-row a:hover {
	text-decoration: underline;
}
tr.z-tree-row-disd *, td.z-tree-cell-disd * {
	color: #C5CACB !important; cursor: default!important;
}
tr.z-tree-row-disd a:visited, tr.z-tree-row-disd a:hover,
td.z-tree-cell-disd a:visited, td.z-tree-cell-disd a:hover { 
	text-decoration: none !important;
	cursor: default !important;;
	border-color: #D0DEF0 !important;
}
tr.z-tree-row-seld {
	background: #b3c8e8; border: 1px solid #6f97d2;
}
tr.z-tree-row-over {
	background: #D3EFFA;
}
tr.z-tree-row-over-seld {
	background: #82D5F8;
}
tr.z-tree-row td.z-tree-row-focus {
	background-image: url(${c:encodeURL('~./zul/img/focusd.png')});
	background-repeat: no-repeat;
}

<%-- z-dottree --%>
div.z-dottree {
	background: #DAE7F6; border: 1px solid #7F9DB9; overflow: hidden; zoom: 1;
}
div.z-dottree-header, div.z-dottree-header tr, div.z-dottree-footer {
	border: 0; overflow: hidden; width: 100%;
}
div.z-dottree-header tr.z-tree-cols, div.z-tree-header tr.z-auxhead  {
	background-image: url(${c:encodeURL('~./zul/img/grid/s_hd.gif')});
}
div.z-dottree-header th {
	overflow: hidden; border: 1px solid;
	border-color: #DAE7F6 #9EB6CE #9EB6CE #DAE7F6;
	white-space: nowrap; padding: 2px;
	font-size: ${fontSizeM}; font-weight: normal;
}
div.z-dottree-body {
	background: white; border: 0; overflow: auto; width: 100%;
}
div.z-dottree-pgi-b {
	border-top: 1px solid #AAB; overflow: hidden;
}
div.z-dottree-pgi-t {
	border-bottom: 1px solid #AAB; overflow: hidden;
}
div.z-dottree-body td.z-tree-cell, div.z-dottree-footer td.z-tree-footer {
	cursor: pointer; padding: 0 2px;
	font-size: ${fontSizeM}; font-weight: normal; overflow: hidden; 
}
div.z-dottree-footer {
	background: #DAE7F6; border-top: 1px solid #9EB6CE;
}

<%-- faker uses only for grid/listbox/tree --%>
tr.z-dottree-faker, tr.z-dottree-faker th, tr.z-dottree-faker div {
	border-top: 0 !important; border-bottom: 0 !important; margin-top: 0 !important;
	margin-bottom: 0 !important; padding-top: 0 !important;	padding-bottom: 0 !important;
	height: 0px !important; <%-- these above css cannot be overrided--%>
	border-left: 0; border-right: 0; margin-left: 0; margin-right: 0; padding-left: 0;
	padding-right: 0;
}
span.z-dottree-root-open {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/root-open.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.z-dottree-root-close {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/root-close.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.z-dottree-tee-open {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/tee-open.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.z-dottree-tee-close {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/tee-close.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.z-dottree-last-open {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/last-open.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.z-dottree-last-close {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/last-close.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.z-dottree-tee {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/tee.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.z-dottree-vbar {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/vbar.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.z-dottree-last {
	width: 18px; min-height: 18px; height: 100%;
	background-image: url(${c:encodeURL('~./zul/img/tree/last.gif')});
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.z-dottree-spacer {
	width: 18px; min-height: 18px; height: 100%;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}