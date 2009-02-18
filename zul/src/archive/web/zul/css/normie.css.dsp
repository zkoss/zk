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

<%-- tree.css.dsp, listbox.css.dsp, and grid.css.dsp --%>
<c:if test="${c:browser('ie6-')}">
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
<%-- Shadow --%>
.z-shadow-ie {
	left: 0; top: 0;
	position: absolute; overflow: hidden;
	background: #888; zoom: 1; display: none;
	filter: progid:DXImageTransform.Microsoft.Blur(PixelRadius=4, MakeShadow=true, ShadowOpacity=0.30)
}
<c:if test="${c:isExplorer7()}">
.z-panel-tm {
	overflow: visible;
}
</c:if>
.z-panel-bbar {
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
<%-- ZK Massagebox norm.css.dsp--%>
.z-msgbox-question {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/QuestionmarkButton-32x32.gif')});
}
.z-msgbox-exclamation {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/WarningTriangle-32x32.gif')});
}
.z-msgbox-imformation {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/InfoButton-32x32.gif')});
}
.z-msgbox-error {
	background-image: url(${c:encodeURL('~./zul/img/msgbox/StopSign-32x32.gif')});
}
.z-fileupload-add {
	background-image: url(${c:encodeURL('~./zul/img/fileupload/add.gif')});	
}
.z-fileupload-delete {
	background-image: url(${c:encodeURL('~./zul/img/fileupload/delete.gif')});
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
<%-- listbox.css.dsp --%>
tr.z-list-item td.z-list-item-focus {
	background-image: url(${c:encodeURL('~./zul/img/focusd.gif')});
}
<%-- menu.css.dsp --%>
.z-menu-cnt-img {
	background-image:url(${c:encodeURL('~./zul/img/menu2/arrow.gif')});
}
.z-menu-btn .z-menu-btn-m em, .z-menu-btn-text-img .z-menu-btn-m em {
	background-image: url(${c:encodeURL('~./zul/img/button/tb-btn-arrow.gif')});
}
.z-menubar-ver .z-menu-btn .z-menu-btn-m em, .z-menubar-ver .z-menu-btn-text-img .z-menu-btn-m em {
	background-image: url(${c:encodeURL('~./zul/img/button/tb-btn-arrow-ver.gif')});
}
.z-menu-item-cnt-unck .z-menu-item-img {
	background-image:  url(${c:encodeURL('~./zul/img/menu2/unchecked.gif')});
}
<%-- panel.css.dsp --%>
.z-panel-tl{
	background-image:url(${c:encodeURL('~./zul/img/panel/panel-corners.gif')});
}
.z-panel-tr {
	background-image:url(${c:encodeURL('~./zul/img/panel/panel-corners.gif')});
}
.z-panel-bl {
	background-image:url(${c:encodeURL('~./zul/img/panel/panel-corners.gif')});
}
.z-panel-br {
	background-image:url(${c:encodeURL('~./zul/img/panel/panel-corners.gif')});
}
<%-- popup.css.dsp --%>
.z-popup .z-popup-tl, .z-popup .z-popup-tr, .z-popup .z-popup-bl, .z-popup .z-popup-br {
	background-image: url(${c:encodeURL('~./zul/img/popup/pp-corners.gif')});
}
.z-popup .z-popup-tm, .z-popup .z-popup-cm, .z-popup .z-popup-bm {
	background-image: url(${c:encodeURL('~./zul/img/popup/pp-tb.gif')});
}
.z-popup .z-popup-cl {
	background-image: url(${c:encodeURL('~./zul/img/popup/pp-l.gif')});
}
.z-popup .z-popup-cr {
	background-image: url(${c:encodeURL('~./zul/img/popup/pp-r.gif')});
}
<%-- slider.css.dsp --%>
.z-slider-hor {    
    background-image: url(${c: encodeURL('~./zul/img/slider2/slider-bg.gif')});        
}
.z-slider-hor-center { 
	background-image: url(${c: encodeURL('~./zul/img/slider2/slider-bg.gif')});      
}
.z-slider-hor-end {
    background-image:url(${c: encodeURL('~./zul/img/slider2/slider-bg.gif')});    
}
.z-slider-hor-btn {
    background-image: url(${c: encodeURL('~./zul/img/slider2/slider-thumb.gif')});    
}
.z-slider-ver {
	background-image:url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.gif')});
}
.z-slider-ver-center {
	background-image:url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.gif')});
}
.z-slider-ver-end {
	background-image:url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.gif')});
}
.z-slider-ver-btn {
	background-image:url(${c: encodeURL('~./zul/img/slider2/slider-v-thumb.gif')});
}
.z-slider-scale {
    background-image:url(${c: encodeURL('~./zul/img/slider2/slider-bg.gif')});
}
.z-slider-scale-center {
    background-image:url(${c: encodeURL('~./zul/img/slider2/slider-bg.gif')});
}
.z-slider-scale-end {
    background-image:url(${c: encodeURL('~./zul/img/slider2/slider-bg.gif')});
}
.z-slider-scale-btn {
    background-image:url(${c: encodeURL('~./zul/img/slider2/slider-scale-thumb.gif')});
}
.z-slider-sphere-hor {
	background-image:url(${c: encodeURL('~./zul/img/slider2/slider-bg.gif')});
}
.z-slider-sphere-hor-center {
	background-image:url(${c: encodeURL('~./zul/img/slider2/slider-bg.gif')});
}
.z-slider-sphere-hor-end {
	background-image:url(${c: encodeURL('~./zul/img/slider2/slider-bg.gif')});
}
.z-slider-sphere-hor-btn {
	background-image:url(${c: encodeURL('~./zul/img/slider2/slider-thumb_circle.gif')});
}
.z-slider-sphere-ver {
    background-image:url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.gif')});
}
.z-slider-sphere-ver-center {
    background-image:url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.gif')});
}
.z-slider-sphere-ver-end {
    background-image:url(${c: encodeURL('~./zul/img/slider2/slider-v-bg.gif')});
}
.z-slider-sphere-ver-btn {
    background-image:url(${c: encodeURL('~./zul/img/slider2/slider-v-thumb_circle.gif')});
}
<%-- tabbox.css.dsp --%>
.z-tab-close, .z-tab-ver-close {
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-close.gif')});
}
.z-tab-accordion-close,
.z-tab-accordion-lite-close,
.z-tab-accordion-disd .z-tab-accordion-close-over,
.z-tab-accordion-disd-seld .z-tab-accordion-close-over,
.z-tab-accordion-lite-disd .z-tab-accordion-lite-close-over,
.z-tab-accordion-lite-disd-seld .z-tab-accordion-lite-close-over {
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-close-off.gif')});
}
.z-tab-accordion .z-tab-accordion-close-over {
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-close-on.gif')});
}
.z-tab-accordion-lite .z-tab-accordion-lite-close-over {
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-close-on-l.gif')});
}
.z-tab-accordion-tl,
.z-tab-accordion-tr {
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-accd-corner.gif')});
}
<%-- tree.css.dsp --%>
tr.z-tree-row td.z-tree-row-focus {
	background-image: url(${c:encodeURL('~./zul/img/focusd.gif')});
}
span.z-vfiletree-ico,span.z-vfiletree-firstspacer {
	background-image: url(${c:encodeURL('~./zul/img/tree/folder-toggle2.gif')});
}
span.z-vfiletree-tee, span.z-vfiletree-last {
	background-image: url(${c:encodeURL('~./zul/img/tree/entity.gif')});
}
<%-- window.css.dsp --%>
.z-window-embedded-tl{
	background-image: url(${c:encodeURL('~./zul/img/wnd2/wtp-l.gif')})
}
.z-window-embedded-tr{
	background-image: url(${c:encodeURL('~./zul/img/wnd2/wtp-r.gif')})
}
.z-window-modal-tl, .z-window-highlighted-tl, .z-window-overlapped-tl, .z-window-popup-tl {
	background-image: url(${c:encodeURL('~./zul/img/wnd2/wtp-l-ol.gif')})
}
.z-window-modal-tr, .z-window-highlighted-tr, .z-window-overlapped-tr, .z-window-popup-tr {
	background-image: url(${c:encodeURL('~./zul/img/wnd2/wtp-r-ol.gif')})
}
<%-- grid.css.dsp --%>
.z-columns-menu-grouping .z-menu-item-img {
	background-image:  url(${c:encodeURL('~./zul/img/grid/Group-16x16.gif')});
}
.z-columns-menu-asc .z-menu-item-img {
	background-image:  url(${c:encodeURL('~./zul/img/grid/AZArrowUp-16x16.gif')});
}
.z-columns-menu-desc .z-menu-item-img {
	background-image:  url(${c:encodeURL('~./zul/img/grid/AZArrowDown-16x16.gif')});
}
</c:if>
