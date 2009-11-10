<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- Tree --%>
div.z-tree {
	background: #DAE7F6; border: 1px solid #86A4BE; overflow: hidden; zoom: 1;
}
div.z-tree-header, div.z-tree-header tr, div.z-tree-footer {
	border: 0; overflow: hidden; width: 100%;
}
div.z-tree-header tr.z-treecols, div.z-tree-header tr.z-auxhead {
	background-image: url(${c:encodeURL('~./zul/img/grid/column-bg.png')});
}
div.z-tree-header th.z-treecol, div.z-tree-header th.z-auxheader {
	overflow: hidden; border: 1px solid;
	border-color: #DAE7F6 #9EB6CE #9EB6CE #DAE7F6;
	white-space: nowrap; padding: 2px;
	font-size: ${fontSizeM}; font-weight: normal;
}
div.z-tree-body, div.z-dottree-body, div.z-filetree-body, div.z-vfiletree-body {
	background: white; border: 0; overflow: auto; width: 100%; position: relative;
}
div.z-tree-pgi-b {
	border-top: 1px solid #AAB; overflow: hidden;
}
div.z-tree-pgi-t {
	border-bottom: 1px solid #AAB; overflow: hidden;
}
div.z-tree-body td.z-treecell, div.z-tree-footer td.z-treefooter {
	cursor: pointer; padding: 0 2px;
	font-size: ${fontSizeM}; font-weight: normal; overflow: hidden;
}
div.z-tree-footer {
	background: #DAE7F6; border-top: 1px solid #9EB6CE;
}

tr.z-tree-faker, tr.z-tree-faker th, tr.z-tree-faker div,
tr.z-dottree-faker, tr.z-dottree-faker th, tr.z-dottree-faker div,
tr.z-filetree-faker, tr.z-filetree-faker th, tr.z-filetree-faker div,
tr.z-vfiletree-faker, tr.z-vfiletree-faker th, tr.z-vfiletree-faker div {
	border-top: 0 !important; border-right : 0 !important;border-bottom: 0 !important;border-left: 0 !important;
	padding-top: 0 !important;	padding-right: 0 !important; padding-bottom: 0 !important;padding-left: 0 !important;
	margin-top: 0 !important; margin-right : 0 !important;margin-bottom: 0 !important;margin-left: 0 !important;
	height: 0 !important;
	font-size: ${fontSizeM} !important;
} <%-- these above css cannot be overrided--%>
<%-- tree icons --%>
span.z-tree-ico, span.z-tree-line, span.checkmark-spacer {
	width: 18px; min-height: 18px; height: 100%;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.z-tree-ico {
	background-repeat: no-repeat;
}
span.z-tree-root-open, span.z-tree-tee-open, span.z-tree-last-open,
span.z-tree-root-close, span.z-tree-tee-close, span.z-tree-last-close {
	background-image: url(${c:encodeURL('~./zul/img/common/toggle.gif')});
}
span.z-tree-root-open, span.z-tree-tee-open, span.z-tree-last-open {
	background-position: 0 -18px;
}
span.z-tree-root-close, span.z-tree-tee-close, span.z-tree-last-close {
	background-position: 0 0;
}

<%-- Treecol, Treefooter, and Treecell--%>
div.z-treefooter-cnt, div.z-treecell-cnt, div.z-treecol-cnt {
	border: 0; margin: 0; padding: 0;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; font-weight: normal;
	white-space: nowrap;
}
div.z-treefooter-cnt, div.z-treecol-cnt{
	overflow: hidden;
	cursor: default;
}
.z-word-wrap div.z-treecell-cnt, .z-word-wrap div.z-treefooter-cnt,
	.z-word-wrap div.z-treecol-cnt {
	word-wrap: break-word;
}
<%-- ZK Treecol's sizing --%>
.z-tree-header .z-treecol.z-treecol-sizing, .z-tree-header .z-treecol.z-treecol-sizing .z-treecol-cnt {
	cursor: e-resize;
}
.z-dottree-header .z-treecol.z-treecol-sizing, .z-dottree-header .z-treecol.z-treecol-sizing .z-treecol-cnt {
	cursor: e-resize;
}
.z-filetree-header .z-treecol.z-treecol-sizing, .z-filetree-header .z-treecol.z-treecol-sizing .z-treecol-cnt {
	cursor: e-resize;
}
.z-vfiletree-header .z-treecol.z-treecol-sizing, .z-vfiletree-header .z-treecol.z-treecol-sizing .z-treecol-cnt {
	cursor: e-resize;
}
<%-- Treerow --%>
tr.z-treerow, tr.z-treerow a, tr.z-treerow a:visited {
	font-size: ${fontSizeM}; font-weight: normal; color: black;
	text-decoration: none;
}
tr.z-treerow a:hover {
	text-decoration: underline;
}
tr.z-treerow-disd *, td.z-treecell-disd * {
	color: #C5CACB !important; cursor: default!important;
}
tr.z-treerow-disd a:visited, tr.z-treerow-disd a:hover,
td.z-treecell-disd a:visited, td.z-treecell-disd a:hover {
	text-decoration: none !important;
	cursor: default !important;;
	border-color: #D0DEF0 !important;
}
tr.z-treerow-seld {
	background: #bcd2ef; border: 1px solid #6f97d2;
}
tr.z-treerow-over {
	background: #dae7f6;
}
tr.z-treerow-over-seld {
	background: #6eadff;
}
tr.z-treerow td.z-treerow-focus {
	background-image: url(${c:encodeURL('~./zul/img/common/focusd.png')});
	background-repeat: no-repeat;
}

<%-- z-dottree --%>
div.z-dottree {
	background: #DAE7F6; border: 1px solid #86A4BE; overflow: hidden; zoom: 1;
}
div.z-dottree-header, div.z-dottree-header tr, div.z-dottree-footer {
	border: 0; overflow: hidden; width: 100%;
}
div.z-dottree-header tr.z-treecols, div.z-tree-header tr.z-auxhead  {
	background-image: url(${c:encodeURL('~./zul/img/grid/column-bg.png')});
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
div.z-dottree-body td.z-treecell, div.z-dottree-footer td.z-treefooter {
	cursor: pointer; padding: 0 2px;
	font-size: ${fontSizeM}; font-weight: normal; overflow: hidden;
}
div.z-dottree-footer {
	background: #DAE7F6; border-top: 1px solid #9EB6CE;
}
span.z-dottree-ico, span.z-dottree-line {
	width: 18px; min-height: 18px; height: 100%;
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.z-dottree-root-open, span.z-dottree-root-close{
	background-image: url(${c:encodeURL('~./zul/img/tree/dot-toggle.gif')});
}
span.z-dottree-root-open {
	background-position: 0 0;
}
span.z-dottree-root-close {
	background-position: 0 -18px;
}
span.z-dottree-tee-open {
	background-image: url(${c:encodeURL('~./zul/img/tree/tee-open.gif')});
}
span.z-dottree-tee-close {
	background-image: url(${c:encodeURL('~./zul/img/tree/tee-close.gif')});
}
span.z-dottree-last-open {
	background-image: url(${c:encodeURL('~./zul/img/tree/tee-last-open.gif')});
}
span.z-dottree-last-close {
	background-image: url(${c:encodeURL('~./zul/img/tree/tee-last-close.gif')});
}
span.z-dottree-tee {
	background-image: url(${c:encodeURL('~./zul/img/tree/tee.gif')});
}
span.z-dottree-vbar {
	background-image: url(${c:encodeURL('~./zul/img/tree/tee-vbar.gif')});
}
span.z-dottree-last {
	background-image: url(${c:encodeURL('~./zul/img/tree/tee-last.gif')});
}
span.z-dottree-spacer, span.z-dottree-firstspacer {
	background:none;
}

<%-- z-filetree --%>
div.z-filetree {
	background: #DAE7F6; border: 1px solid #86A4BE; overflow: hidden; zoom: 1;
}
div.z-filetree-header, div.z-filetree-header tr, div.z-filetree-footer {
	border: 0; overflow: hidden; width: 100%;
}
div.z-filetree-header tr.z-treecols, div.z-tree-header tr.z-auxhead  {
	background-image: url(${c:encodeURL('~./zul/img/grid/column-bg.png')});
}
div.z-filetree-header th.z-treecol, div.z-filetree-header th.z-auxheader {
	overflow: hidden; border: 1px solid;
	border-color: #DAE7F6 #9EB6CE #9EB6CE #DAE7F6;
	white-space: nowrap; padding: 2px;
	font-size: ${fontSizeM}; font-weight: normal;
}
div.z-filetree-body {
	background: white; border: 0; overflow: auto; width: 100%;
}
div.z-filetree-pgi-b {
	border-top: 1px solid #AAB; overflow: hidden;
}
div.z-filetree-pgi-t {
	border-bottom: 1px solid #AAB; overflow: hidden;
}
div.z-filetree-body td.z-treecell, div.z-filetree-footer td.z-treefooter {
	cursor: pointer; padding: 0 2px;
	font-size: ${fontSizeM}; font-weight: normal; overflow: hidden;
}
div.z-filetree-footer {
	background: #DAE7F6; border-top: 1px solid #9EB6CE;
}
span.z-filetree-line, span.z-filetree-ico, span.z-filetree-firstspacer {
	width: 18px; min-height: 18px; height: 100%;
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.z-filetree-ico,span.z-filetree-firstspacer {
	background-image: url(${c:encodeURL('~./zul/img/tree/folder-toggle.gif')});
}
span.z-filetree-root-open, span.z-filetree-root-close,
span.z-filetree-root-open, span.z-filetree-tee-open, span.z-filetree-last-open {
	background-position: 0 0;
}
span.z-filetree-root-close, span.z-filetree-tee-close,
span.z-filetree-last-close, span.z-filetree-firstspacer {
	background-position: 0 -18px;
}
span.z-filetree-tee, span.z-filetree-last {
	background-image: url(${c:encodeURL('~./zul/img/tree/entity.gif')});
}
span.z-filetree-vbar, span.z-filetree-spacer {
	background:none;
}

<%-- z-vfiletree--%>
div.z-vfiletree {
	background: #DAE7F6; border: 1px solid #86A4BE; overflow: hidden; zoom: 1;
}
div.z-vfiletree-header, div.z-vfiletree-header tr, div.z-vfiletree-footer {
	border: 0; overflow: hidden; width: 100%;
}
div.z-vfiletree-header tr.z-treecols, div.z-tree-header tr.z-auxhead  {
	background-image: url(${c:encodeURL('~./zul/img/grid/column-bg.png')});
}
div.z-vfiletree-header th.z-treecol, div.z-vfiletree-header th.z-auxheader {
	overflow: hidden; border: 1px solid;
	border-color: #DAE7F6 #9EB6CE #9EB6CE #DAE7F6;
	white-space: nowrap; padding: 2px;
	font-size: ${fontSizeM}; font-weight: normal;
}
div.z-vfiletree-body {
	background: white; border: 0; overflow: auto; width: 100%;
}
div.z-vfiletree-pgi-b {
	border-top: 1px solid #AAB; overflow: hidden;
}
div.z-vfiletree-pgi-t {
	border-bottom: 1px solid #AAB; overflow: hidden;
}
div.z-vfiletree-body td.z-treecell, div.z-vfiletree-footer td.z-treefooter {
	cursor: pointer; padding: 0 2px;
	font-size: ${fontSizeM}; font-weight: normal; overflow: hidden;
}
div.z-vfiletree-footer {
	background: #DAE7F6; border-top: 1px solid #9EB6CE;
}
span.z-vfiletree-line, span.z-vfiletree-ico, span.z-vfiletree-firstspacer {
	width: 18px; min-height: 18px; height: 100%;
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;
}
span.z-vfiletree-ico,span.z-vfiletree-firstspacer {
	background-image: url(${c:encodeURL('~./zul/img/tree/vfolder-toggle.png')});
}
span.z-vfiletree-root-open, span.z-vfiletree-root-close,
span.z-vfiletree-root-open, span.z-vfiletree-tee-open, span.z-vfiletree-last-open {
	background-position: 0 0;
}
span.z-vfiletree-root-close, span.z-vfiletree-tee-close,
span.z-vfiletree-last-close, span.z-vfiletree-firstspacer {
	background-position: 0 -18px;
}
span.z-vfiletree-tee, span.z-vfiletree-last {
	background-image: url(${c:encodeURL('~./zul/img/tree/ventity.png')});
}
span.z-vfiletree-vbar, span.z-vfiletree-spacer {
	background:none;
}

<%-- IE --%>
<c:if test="${c:isExplorer()}">
div.z-tree-header, div.z-dottree-header, div.z-filetree-header, div.z-vfiletree-header,
div.z-tree-footer, div.z-dottree-footer, div.z-filetree-footer, div.z-vfiletree-footer {
	position:relative; <%-- Bug 1712708 and 1926094 --%>
}
div.z-tree-header th.z-treecol, div.z-tree-header th.z-auxheader,
div.z-dottree-header th.z-treecol, div.z-dottree-header th.z-auxheader,
div.z-filetree-header th.z-treecol, div.z-filetree-header th.z-auxheader,
div.z-vfiletree-header th.z-treecol, div.z-vfiletree-header th.z-auxheader {
	text-overflow: ellipsis;
}
div.z-treecol-cnt, div.z-dottreecol-cnt, div.z-filetreecol-cnt,
div.z-vfiletreecol-cnt, .z-auxheader-cnt {
	white-space: nowrap; <%-- Bug #1839960  --%>
}
div.z-treefooter-cnt, div.z-treecell-cnt, div.z-treecol-cnt,
div.z-dottreefooter-cnt, div.z-dottreecell-cnt, div.z-dottreecol-cnt,
div.z-filetreefooter-cnt, div.z-filetreecell-cnt, div.z-filetreecol-cnt,
div.z-vfiletreefooter-cnt, div.z-vfiletreecell-cnt, div.z-vfiletreecol-cnt,
.z-auxheader-cnt {
	position: relative; <%-- Bug #1825896  --%>
}
div.z-treecell-cnt, div.z-dottreecell-cnt,
div.z-filetreecell-cnt, div.z-vfiletreecell-cnt {
	width: 100%;
}
div.z-tree-body, div.z-dottree-body,
div.z-filetree-body, div.z-vfiletree-body {
	position: relative; <%-- Bug 1766244  --%>
}
<c:if test="${!c:browser('ie8')}">
tr.z-tree-faker, tr.z-dottree-faker,
tr.z-filetree-faker, tr.z-vfiletree-faker {
	position: absolute; top: -1000px; left: -1000px;<%-- fixed a white line for IE --%>
}
</c:if>
<c:if test="${c:browser('ie8')}">
.z-treecol, .z-auxheader {
	text-align: left;
}
</c:if>
span.z-tree-root-open, span.z-tree-root-close,
span.z-tree-tee-open, span.z-tree-tee-close,
span.z-tree-last-open, span.z-tree-last-close,
span.z-tree-tee, span.z-tree-vbar,
span.z-tree-last, span.z-tree-spacer,
span.z-dottree-root-open, span.z-dottree-root-close,
span.z-dottree-tee-open, span.z-dottree-tee-close,
span.z-dottree-last-open, span.z-dottree-last-close,
span.z-dottree-tee, span.z-dottree-vbar,
span.z-dottree-last, span.z-dottree-spacer,
span.z-filetree-root-open, span.z-filetree-root-close,
span.z-filetree-tee-open, span.z-filetree-tee-close,
span.z-filetree-last-open, span.z-filetree-last-close,
span.z-filetree-tee, span.z-filetree-vbar,
span.z-filetree-last, span.z-filetree-spacer,
span.z-vfiletree-root-open, span.z-vfiletree-root-close,
span.z-vfiletree-tee-open, span.z-vfiletree-tee-close,
span.z-vfiletree-last-open, span.z-vfiletree-last-close,
span.z-vfiletree-tee, span.z-vfiletree-vbar,
span.z-vfiletree-last, span.z-vfiletree-spacer {
	height: 18px;
}

<c:if test="${c:browser('ie6-')}">
div.z-tree, div.z-dottree, div.z-filetree, div.z-vfiletree {
	position:relative; <%-- Bug 1914215 and Bug 1914054 --%>
}
tr.z-treerow td.z-treerow-focus {
	background-image: url(${c:encodeURL('~./zul/img/common/focusd.gif')});
}
span.z-vfiletree-ico, span.z-vfiletree-firstspacer {
	background-image: url(${c:encodeURL('~./zul/img/tree/vfolder-toggle.gif')});
}
span.z-vfiletree-tee, span.z-vfiletree-last {
	background-image: url(${c:encodeURL('~./zul/img/tree/ventity.gif')});
}
</c:if>
</c:if>

<%-- Gecko (3.5 supports word-break )--%>
<c:if test="${c:isGecko() and !c:browser('gecko3.5')}">
.z-word-wrap div.z-treecell-cnt,
.z-word-wrap div.z-treefooter-cnt, 
.z-word-wrap div.z-treecol-cnt {
	overflow: hidden;
	-moz-binding: url(${c:encodeURL('~./zk/wordwrap.xml#wordwrap')});
}
</c:if>
