<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:include page="~./zul/css/norm.css.dsp"/>
<c:choose>
<c:when  test="${!empty c:property('org.zkoss.zul.theme.enableZKPrefix')}">
.zk img	{
	hspace: 0; vspace: 0
}
.zk option {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeXS}; font-weight: normal;
}
</c:when>
<c:otherwise>
img	{
	hspace: 0; vspace: 0
}
option {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeXS}; font-weight: normal;
}
</c:otherwise>
</c:choose>

.z-messagebox-btn {
	width: 47pt;
	text-overflow: ellipsis;
}
<%-- Widget.css.dsp --%>
.z-textbox-disd *, .z-decimalbox-disd *, .z-intbox-disd *, .z-longbox-disd *, .z-doublebox-disd * {
	filter: alpha(opacity=60);
}

<%-- shadow, tree.css.dsp, listbox.css.dsp, and grid.css.dsp --%>
<c:if test="${c:browser('ie6-')}">
.z-shadow {
	background: #888; zoom: 1; display: none;
	filter: progid:DXImageTransform.Microsoft.Blur(PixelRadius=4, MakeShadow=true, ShadowOpacity=0.30)
}

div.z-listbox, div.z-tree, div.z-dottree, div.z-filetree, div.z-vfiletree, div.z-grid {
	position:relative; <%-- Bug 1914215 and Bug 1914054 --%>
}
</c:if>
div.z-tree-header, div.z-dottree-header, div.z-filetree-header, div.z-vfiletree-header,
div.z-listbox-header, div.z-grid-header, div.z-tree-footer, div.z-listbox-footer,
	div.z-grid-footer {<%-- always used. --%>
	position:relative;
	<%-- Bug 1712708 and 1926094:  we have to specify position:relative --%>
}
div.z-tree-header th.z-tree-col, div.z-tree-header th.z-auxheader,
div.z-dottree-header th.z-tree-col, div.z-dottree-header th.z-auxheader,
div.z-filetree-header th.z-tree-col, div.z-filetree-header th.z-auxheader,
div.z-vfiletree-header th.z-tree-col, div.z-vfiletree-header th.z-auxheader,
div.z-listbox-header th.z-list-header, div.z-listbox-header th.z-auxheader,
div.z-grid-header th.z-column, div.z-grid-header th.z-auxheader {
	text-overflow: ellipsis;
}
div.z-tree-col-cnt, div.z-dottree-col-cnt, div.z-filetree-col-cnt, div.z-vfiletree-col-cnt,
div.z-list-header-cnt, div.z-column-cnt, .z-auxheader-cnt {
	white-space: nowrap;
	<%-- Bug #1839960  --%>
}
div.z-footer-cnt, div.z-row-cnt, div.z-group-cnt, div.z-group-foot-cnt, div.z-column-cnt,
div.z-tree-footer-cnt, div.z-tree-cell-cnt, div.z-tree-col-cnt,
div.z-dottree-footer-cnt, div.z-dottree-cell-cnt, div.z-dottree-col-cnt,
div.z-filetree-footer-cnt, div.z-filetree-cell-cnt, div.z-filetree-col-cnt,
div.z-vfiletree-footer-cnt, div.z-vfiletree-cell-cnt, div.z-vfiletree-col-cnt,
.z-auxheader-cnt, div.z-list-footer-cnt, div.z-list-cell-cnt, div.z-list-header-cnt {
	position: relative;
	<%-- Bug #1825896  --%>
}
div.z-row-cnt, div.z-group-cnt, div.z-group-foot-cnt,div.z-list-cell-cnt,
div.z-tree-cell-cnt, div.z-dottree-cell-cnt, div.z-filetree-cell-cnt, div.z-vfiletree-cell-cnt {
	width: 100%;
}
div.z-tree-body, div.z-dottree-body, div.z-filetree-body, div.z-vfiletree-body, div.z-listbox-body, div.z-grid-body {<%-- always used. --%>
	position: relative;
	<%-- Bug 1766244: we have to specify position:relative with overflow:auto --%>
}
tr.z-grid-faker, tr.z-listbox-faker, tr.z-tree-faker, tr.z-dottree-faker, tr.z-filetree-faker, tr.z-vfiletree-faker {
	position: absolute; top: -1000px; left: -1000px;<%-- fixed a white line for IE --%>
}
span.z-tree-root-open, span.z-tree-root-close, span.z-tree-tee-open, span.z-tree-tee-close,
span.z-tree-last-open, span.z-tree-last-close, span.z-tree-tee, span.z-tree-vbar, span.z-tree-last, span.z-tree-spacer,
span.z-dottree-root-open, span.z-dottree-root-close, span.z-dottree-tee-open, span.z-dottree-tee-close,
span.z-dottree-last-open, span.z-dottree-last-close, span.z-dottree-tee, span.z-dottree-vbar, span.z-dottree-last, span.z-dottree-spacer,
span.z-filetree-root-open, span.z-filetree-root-close, span.z-filetree-tee-open, span.z-filetree-tee-close,
span.z-filetree-last-open, span.z-filetree-last-close, span.z-filetree-tee, span.z-filetree-vbar, span.z-filetree-last, span.z-filetree-spacer,
span.z-vfiletree-root-open, span.z-vfiletree-root-close, span.z-vfiletree-tee-open, span.z-vfiletree-tee-close,
span.z-vfiletree-last-open, span.z-vfiletree-last-close, span.z-vfiletree-tee, span.z-vfiletree-vbar, span.z-vfiletree-last, span.z-vfiletree-spacer {
	height: 18px;
}

<%-- combo.css.dsp --%>
.z-combobox-pp .z-combo-item-inner {<%--description--%>
	padding-left: 5px;
}
.z-calendar-calyear td, .z-datebox-calyear td {
	color: black; <%-- 1735084 --%>
}

<%-- Append New --%>
<c:if test="${c:isExplorer7()}">
.z-panel-tm {
	overflow: visible;
}
</c:if>
.z-panel-header {
	zoom: 1;
}
.z-panel-btm {
	position: relative;
}
<%-- groupbox.css.dsp --%>
<c:if test="${c:isExplorer7()}">
.z-groupbox-body {
	zoom: 1;
}
</c:if>


<%-- IE 6 GIF  --%>
<c:if test="${c:browser('ie6-')}">
<%-- norm.css.dsp --%>
span.z-drop-allow,
span.z-drop-disallow {
	background-image: url(${c:encodeURL('~./zul/img/misc/drag.gif')});
}
.z-msgbox-question {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/question-btn.gif')});
}
.z-msgbox-exclamation {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/warning-btn.gif')});
}
.z-msgbox-information {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/info-btn.gif')});
}
.z-msgbox-error {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/stop-btn.gif')});
}
<%-- box.css.dsp --%>
.z-splitter-ver-btn-l, .z-splitter-hor-btn-l {
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-l.gif')});
}
.z-splitter-ver-btn-r, .z-splitter-hor-btn-r {
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-r.gif')});
}
.z-splitter-ver-btn-t, .z-splitter-hor-btn-t {
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-t.gif')});
}
.z-splitter-ver-btn-b, .z-splitter-hor-btn-b {
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-b.gif')});
}
<%-- grid.css.dsp --%>
.z-columns-menu-grouping .z-menu-item-img {
	background-image:  url(${c:encodeURL('~./zul/img/grid/menu-group.gif')});
}
.z-columns-menu-asc .z-menu-item-img {
	background-image:  url(${c:encodeURL('~./zul/img/grid/menu-arrowup.gif')});
}
.z-columns-menu-dsc .z-menu-item-img {
	background-image:  url(${c:encodeURL('~./zul/img/grid/menu-arrowdown.gif')});
}
<%-- listbox.css.dsp --%>
tr.z-list-item td.z-list-item-focus {
	background-image: url(${c:encodeURL('~./zul/img/common/focusd.gif')});
}
<%-- paging.css.dsp --%>
.z-paging-btn .z-paging-next,
.z-paging-btn .z-paging-prev,
.z-paging-btn .z-paging-last,
.z-paging-btn .z-paging-first {	
	background-image:url(${c:encodeURL('~./zul/img/paging/pg-btn.gif')});
}
<%-- panel.css.dsp --%>
.z-panel-tl,
.z-panel-tr,
.z-panel-bl,
.z-panel-br {
	background-image:url(${c:encodeURL('~./zul/img/wnd/panel-corner.gif')});
}
<%-- popup.css.dsp --%>
.z-popup .z-popup-tl,
.z-popup .z-popup-tr,
.z-popup .z-popup-bl,
.z-popup .z-popup-br {
	background-image:url(${c:encodeURL('~./zul/img/popup/pp-corner.gif')});
}
.z-popup .z-popup-cl,
.z-popup .z-popup-cr {
	background-image: url(${c:encodeURL('~./zul/img/popup/pp-clr.gif')});
}
.z-popup .z-popup-cm {
	background-image: url(${c:encodeURL('~./zul/img/popup/pp-cm.gif')});
}
<%-- slider.css.dsp --%>
.z-slider-sphere-hor,
.z-slider-scale,
.z-slider-hor,
.z-slider-sphere-hor-center,
.z-slider-scale-center,
.z-slider-hor-center {
	background-image:url(${c:encodeURL('~./zul/img/slider/slider-bg.gif')});
}
.z-slider-sphere-hor-btn,
.z-slider-scale-btn,
.z-slider-hor-btn {
    background-image : url(${c:encodeURL('~./zul/img/slider/slider-square.gif')});
}
.z-slider-scale-btn {
	background-image : url(${c:encodeURL('~./zul/img/slider/slider-scale.gif')});
}
.z-slider-sphere-ver,
.z-slider-ver,
.z-slider-sphere-ver-center,
.z-slider-ver-center {
	background-image:url(${c:encodeURL('~./zul/img/slider/slider-bg-ver.gif')});
}

.z-slider-sphere-ver-btn,
.z-slider-ver-btn {
	background-image : url(${c:encodeURL('~./zul/img/slider/slider-v-square.gif')});
}
.z-slider-sphere-hor-btn {
	background-image : url(${c:encodeURL('~./zul/img/slider/slider-circle.gif')});
}
.z-slider-sphere-ver-btn {
	background-image : url(${c:encodeURL('~./zul/img/slider/slider-v-circle.gif')});
}
<%-- tabbox.css.dsp --%>
.z-tab-hl,
.z-tab-hr {
	background-image: url(${c:encodeURL('~./zul/img/tab/tab-corner.gif')});
}
.z-tab-hm {
	background-image: url(${c:encodeURL('~./zul/img/tab/tab-hm.gif')});
}
.z-tab-ver-hl,
.z-tab-ver-hl .z-tab-ver-hr {
	background-image: url(${c:encodeURL('~./zul/img/tab/tab-v-corner.gif')});
}
.z-tab-ver .z-tab-ver-hm {
	background-image: url(${c:encodeURL('~./zul/img/tab/tab-v-hm.png')});
}
.z-tab-accordion-tl,
.z-tab-accordion-tr {
	background-image: url(${c:encodeURL('~./zul/img/tab/accd-corner.gif')});
}
<%-- tree.css.dsp --%>
tr.z-tree-row td.z-tree-row-focus {
	background-image: url(${c:encodeURL('~./zul/img/common/focusd.gif')});
}
span.z-vfiletree-ico,span.z-vfiletree-firstspacer {
	background-image: url(${c:encodeURL('~./zul/img/tree/vfolder-toggle.gif')});
}
span.z-vfiletree-tee, span.z-vfiletree-last {
	background-image: url(${c:encodeURL('~./zul/img/tree/ventity.gif')});
}
<%-- window.css.dsp --%>
.z-window-modal-tl,
.z-window-modal-tr,
.z-window-modal-bl,
.z-window-modal-br,
.z-window-overlapped-tl,
.z-window-overlapped-tr,
.z-window-overlapped-bl,
.z-window-overlapped-br,
.z-window-highlighted-tl,
.z-window-highlighted-tr,
.z-window-highlighted-bl,
.z-window-highlighted-br {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-corner.gif')});
}
.z-window-embedded-tl,
.z-window-embedded-tr {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-corner.gif')});
}
.z-window-popup-tl,
.z-window-popup-tr {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-pop-corner.gif')});
}
.z-window-modal-hr,
.z-window-highlighted-hr,
.z-window-overlapped-hr,
 .z-window-popup-hr {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-hr.gif')});
}
.z-window-embedded-hr,
.z-window-embedded-hr-noborder {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-hr.gif')});
}
.z-window-popup-hr {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-pop-hr.gif')});
}
.z-window-modal-hl,
.z-window-highlighted-hl,
.z-window-overlapped-hl,
.z-window-popup-hl {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-hl.gif')});
}
.z-window-embedded-hl,
.z-window-embedded-hl-noborder {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-hl.gif')});
}
.z-window-popup-hl {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-pop-hl.gif')});
}
.z-window-modal-hm,
.z-window-highlighted-hm,
.z-window-overlapped-hm,
.z-window-popup-hm {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-hm.gif')});
}
.z-window-embedded-hm,
.z-window-embedded-hm-noborder {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-hm.gif')});
}
.z-window-popup-hm {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-pop-hm.gif')});
}
.z-window-modal-cl,
.z-window-highlighted-cl,
.z-window-overlapped-cl,
.z-window-modal-cr,
.z-window-highlighted-cr,
.z-window-overlapped-cr {
	background-image: url(${c:encodeURL('~./zul/img/wnd/wnd-ol-clr.gif')});
}

.z-groupbox-tl {
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-corner.gif')});
}
.z-groupbox-tr{
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-corner.gif')});
}

.z-groupbox-hl {
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-hl.gif')});
}
.z-groupbox-hr {
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-hr.gif')});
}
.z-groupbox-hm {
	background-image:url(${c:encodeURL('~./zul/img/groupbox/groupbox-hm.gif')});
}
</c:if>
