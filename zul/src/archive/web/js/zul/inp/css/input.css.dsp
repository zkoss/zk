<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-textbox-disd,
.z-decimalbox-disd,
.z-intbox-disd,
.z-longbox-disd,
.z-doublebox-disd {
	opacity: .6;
	filter: alpha(opacity=60);
}

.z-textbox-disd,
.z-textbox-disd *, 
.z-decimalbox-disd, 
.z-decimalbox-disd *, 
.z-intbox-disd, 
.z-intbox-disd *, 
.z-longbox-disd,
.z-longbox-disd *, 
.z-doublebox-disd, 
.z-doublebox-disd * {
	color: #AAA !important;
	cursor: default !important;
}
.z-textbox, .z-decimalbox, .z-intbox, .z-longbox, .z-doublebox {
	background: #FFF repeat-x 0 0;
	background-image: url(${c:encodeURL('~./zul/img/misc/text-bg.gif')});
	border: 1px solid #86A4BE;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	font-weight: normal;
	padding-top: 2px;
	padding-bottom: 2px;
	border-radius: 2px;
	-moz-border-radius: 2px;
	-webkit-border-radius: 2px;
	<c:if test="${c:isSafari()}">
		margin: 0;
	</c:if>
}
.z-textbox-focus,
.z-decimalbox-focus,
.z-intbox-focus,
.z-longbox-focus,
.z-doublebox-focus {
	border: 1px solid #90BCE6;
}
.z-textbox-text-invalid,
.z-decimalbox-text-invalid,
.z-intbox-text-invalid,
.z-longbox-text-invalid,
.z-doublebox-text-invalid {
	background: #FFF repeat-x 0 0;
	background-image: url(${c:encodeURL('~./zul/img/misc/text-bg-invalid.gif')});
	border: 1px solid #DD7870;
}
.z-textbox-readonly, .z-textbox-text-disd,
.z-intbox-readonly, .z-intbox-text-disd,
.z-longbox-readonly, .z-longbox-text-disd,
.z-doublebox-readonly, .z-doublebox-text-disd,
.z-decimalbox-readonly, .z-decimalbox-text-disd {
	background: #ECEAE4;
}

<%-- Inplace editing--%>
.z-textbox-inplace,
.z-decimalbox-inplace,
.z-intbox-inplace,
.z-longbox-inplace,
.z-doublebox-inplace {
	border: 0;
	padding: 3px 1px;
	background: none;
}
<c:if test="${c:isOpera()}">
.z-textbox-inplace,
.z-decimalbox-inplace,
.z-intbox-inplace,
.z-longbox-inplace,
.z-doublebox-inplace {
	padding: 3px 2px;
}
</c:if><%-- opera --%>
<c:if test="${c:isExplorer()}">
.z-textbox-inplace,
.z-decimalbox-inplace,
.z-intbox-inplace,
.z-longbox-inplace,
.z-doublebox-inplace {
	padding: 3px 2px;
}
.z-textbox-disd *, 
.z-decimalbox-disd *, 
.z-intbox-disd *, 
.z-longbox-disd *, 
.z-doublebox-disd * {
	opacity: .6;
	filter: alpha(opacity=60);
}
</c:if><%-- ie --%>

<%-- rounded --%>
.z-textbox-rounded,
.z-decimalbox-rounded,
.z-intbox-rounded,
.z-longbox-rounded,
.z-doublebox-rounded {
	display: -moz-inline-box;
	display: inline-block;
}
.z-textbox-rounded-inp,
.z-decimalbox-rounded-inp,
.z-intbox-rounded-inp,
.z-longbox-rounded-inp,
.z-doublebox-rounded-inp {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	font-weight: normal;
	background: transparent repeat-x 0 0;
	<c:if test="${c:isSafari()}">
		margin: 0;
	</c:if>
	<c:if test="${c:isOpera()}">
		font-style: normal;
	</c:if>
	height: 14px;
	border: 0;
	padding: 5px 4px;
	background-image: url(${c:encodeURL('~./zul/img/button/timebox-rounded.gif')});
}
.z-textbox-rounded-right-edge,
.z-decimalbox-rounded-right-edge,
.z-intbox-rounded-right-edge,
.z-longbox-rounded-right-edge,
.z-doublebox-rounded-right-edge {
	background: transparent no-repeat 0 0;
	background-image: url(${c:encodeURL('~./zul/img/button/timebox-rounded.gif')});
	background-position: -19px -120px;
	vertical-align: top; 
	overflow: hidden;
	display: -moz-inline-box; 
	display: inline-block;
	border: 0;
	height: 24px;
	width: 5px;
	cursor: default;
	<c:if test="${c:browser('ie7-') || c:browser('ie6-')}">
		margin-top: 1px;
	</c:if>
}
<%-- rounded: disabled --%>
.z-textbox-rounded-disd,
.z-decimalbox-rounded-disd,
.z-intbox-rounded-disd,
.z-longbox-rounded-disd,
.z-doublebox-rounded-disd {
	opacity: .6;
	filter: alpha(opacity=60);
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	font-weight: normal;
}
.z-textbox-rounded-disd, .z-textbox-rounded-disd *,
.z-decimalbox-rounded-disd, .z-decimalbox-rounded-disd *,
.z-intbox-rounded-disd, .z-intbox-rounded-disd *,
.z-longbox-rounded-disd, .z-longbox-rounded-disd *,
.z-doublebox-rounded-disd, .z-doublebox-rounded-disd * {
	cursor: default !important;
	color: #303030 !important;
}
<%-- rounded: invalid --%>
.z-textbox-rounded .z-textbox-rounded-text-invalid,
.z-decimalbox-rounded .z-decimalbox-rounded-text-invalid,
.z-intbox-rounded .z-intbox-rounded-text-invalid,
.z-longbox-rounded .z-longbox-rounded-text-invalid,
.z-doublebox-rounded .z-doublebox-rounded-text-invalid {
	background: transparent repeat-x 0 0;
	background-image: url(${c:encodeURL('~./zul/img/button/redcombo-rounded.gif')});
}
.z-textbox-rounded .z-textbox-rounded-text-invalid + .z-textbox-rounded-right-edge,
.z-decimalbox-rounded .z-decimalbox-rounded-text-invalid + .z-decimalbox-rounded-right-edge,
.z-intbox-rounded .z-intbox-rounded-text-invalid + .z-intbox-rounded-right-edge,
.z-longbox-rounded .z-longbox-rounded-text-invalid + .z-longbox-rounded-right-edge,
.z-doublebox-rounded .z-doublebox-rounded-text-invalid + .z-doublebox-rounded-right-edge {
	background-image: url(${c:encodeURL('~./zul/img/button/redcombo-rounded.gif')});
	background-position: 0 -24px;
}
i.z-textbox-rounded-right-edge-invalid,
i.z-decimalbox-rounded-right-edge-invalid,
i.z-intbox-rounded-right-edge-invalid,
i.z-longbox-rounded-right-edge-invalid,
i.z-doublebox-rounded-right-edge-invalid {
	<%-- extra class provided for IE 6 --%>
	background-image: url(${c:encodeURL('~./zul/img/button/redcombo-rounded.gif')});
	background-position: 0 -24px;
}
<%-- rounded: read only --%>
.z-textbox-rounded-real-readonly .z-textbox-rounded-inp,
.z-decimalbox-rounded-real-readonly .z-decimalbox-rounded-inp,
.z-intbox-rounded-real-readonly .z-intbox-rounded-inp,
.z-longbox-rounded-real-readonly .z-longbox-rounded-inp,
.z-doublebox-rounded-real-readonly .z-doublebox-rounded-inp {
	background-image: url(${c:encodeURL('~./zul/img/button/timebox-rounded.gif')});	
	background-position: 0 -72px;
}
.z-textbox-rounded-real-readonly .z-textbox-rounded-right-edge,
.z-decimalbox-rounded-real-readonly .z-decimalbox-rounded-right-edge,
.z-intbox-rounded-real-readonly .z-intbox-rounded-right-edge,
.z-longbox-rounded-real-readonly .z-longbox-rounded-right-edge,
.z-doublebox-rounded-real-readonly .z-doublebox-rounded-right-edge {
	background-position: -19px -192px;
}
<%-- rounded: focus + read only --%>
.z-textbox-rounded-focus .z-textbox-rounded-readonly,
.z-decimalbox-rounded-focus .z-decimalbox-rounded-readonly,
.z-intbox-rounded-focus .z-intbox-rounded-readonly,
.z-longbox-rounded-focus .z-longbox-rounded-readonly,
.z-doublebox-rounded-focus .z-doublebox-rounded-readonly {
	background-position: 0 -96px !important;
}
.z-textbox-rounded-focus .z-textbox-rounded-readonly + .z-textbox-rounded-right-edge,
.z-decimalbox-rounded-focus .z-decimalbox-rounded-readonly + .z-decimalbox-rounded-right-edge,
.z-intbox-rounded-focus .z-intbox-rounded-readonly + .z-intbox-rounded-right-edge,
.z-longbox-rounded-focus .z-longbox-rounded-readonly + .z-longbox-rounded-right-edge,
.z-doublebox-rounded-focus .z-doublebox-rounded-readonly + .z-doublebox-rounded-right-edge {
	background-position: -19px -216px !important;
}
.z-textbox-rounded-focus i.z-textbox-rounded-right-edge-readonly,
.z-decimalbox-rounded-focus i.z-decimalbox-rounded-right-edge-readonly,
.z-intbox-rounded-focus i.z-intbox-rounded-right-edge-readonly,
.z-longbox-rounded-focus i.z-longbox-rounded-right-edge-readonly,
.z-doublebox-rounded-focus i.z-doublebox-rounded-right-edge-readonly {
	<%-- extra class provided for IE 6 --%>
	background-position: -19px -216px !important;
}
<%-- rounded: focus --%>
.z-textbox-rounded-focus input.z-textbox-rounded-inp,
.z-decimalbox-rounded-focus input.z-decimalbox-rounded-inp,
.z-intbox-rounded-focus input.z-intbox-rounded-inp,
.z-longbox-rounded-focus input.z-longbox-rounded-inp,
.z-doublebox-rounded-focus input.z-doublebox-rounded-inp {
	outline: none;
	background-image: url(${c:encodeURL('~./zul/img/button/timebox-rounded.gif')});
	background-position: 0 0;
}
.z-textbox-rounded-focus i.z-textbox-rounded-right-edge,
.z-decimalbox-rounded-focus i.z-decimalbox-rounded-right-edge,
.z-intbox-rounded-focus i.z-intbox-rounded-right-edge,
.z-longbox-rounded-focus i.z-longbox-rounded-right-edge,
.z-doublebox-rounded-focus i.z-doublebox-rounded-right-edge {
	background-image: url(${c:encodeURL('~./zul/img/button/timebox-rounded.gif')});
	background-position: -19px -120px;
}
<%-- rounded: focus + invalid (overrides focus) --%>
.z-textbox-rounded-focus input.z-textbox-rounded-text-invalid,
.z-decimalbox-rounded-focus input.z-decimalbox-rounded-text-invalid,
.z-intbox-rounded-focus input.z-intbox-rounded-text-invalid,
.z-longbox-rounded-focus input.z-longbox-rounded-text-invalid,
.z-doublebox-rounded-focus input.z-doublebox-rounded-text-invalid {
	background: transparent repeat-x 0 0 !important;
	background-image: url(${c:encodeURL('~./zul/img/button/redcombo-rounded.gif')}) !important;
}
.z-textbox-rounded-focus input.z-textbox-rounded-text-invalid + i.z-textbox-rounded-right-edge,
.z-decimalbox-rounded-focus input.z-decimalbox-rounded-text-invalid + i.z-decimalbox-rounded-right-edge,
.z-intbox-rounded-focus input.z-intbox-rounded-text-invalid + i.z-intbox-rounded-right-edge,
.z-longbox-rounded-focus input.z-longbox-rounded-text-invalid + i.z-longbox-rounded-right-edge,
.z-doublebox-rounded-focus input.z-doublebox-rounded-text-invalid + i.z-doublebox-rounded-right-edge {
	background-image: url(${c:encodeURL('~./zul/img/button/redcombo-rounded.gif')}) !important;
	background-position: 0 -24px !important;
}
.z-textbox-rounded-focus .z-textbox-rounded-right-edge-invalid,
.z-decimalbox-rounded-focus .z-decimalbox-rounded-right-edge-invalid,
.z-intbox-rounded-focus .z-intbox-rounded-right-edge-invalid,
.z-longbox-rounded-focus .z-longbox-rounded-right-edge-invalid,
.z-doublebox-rounded-focus .z-doublebox-rounded-right-edge-invalid {
	<%-- extra class provided for IE 6 --%>
	background-image: url(${c:encodeURL('~./zul/img/button/redcombo-rounded.gif')}) !important;
	background-position: 0 -24px !important;
}
<%-- rounded: inplace --%>
.z-textbox-rounded-inplace .z-textbox-rounded-inp,
.z-decimalbox-rounded-inplace .z-decimalbox-rounded-inp,
.z-intbox-rounded-inplace .z-intbox-rounded-inp,
.z-longbox-rounded-inplace .z-longbox-rounded-inp,
.z-doublebox-rounded-inplace .z-doublebox-rounded-inp,
.z-textbox-rounded-inplace .z-textbox-rounded-right-edge,
.z-decimalbox-rounded-inplace .z-decimalbox-rounded-right-edge,
.z-intbox-rounded-inplace .z-intbox-rounded-right-edge,
.z-longbox-rounded-inplace .z-longbox-rounded-right-edge,
.z-doublebox-rounded-inplace .z-doublebox-rounded-right-edge {
	background-image: none;
}
<%-- rouneded: inplace + invalid (overrides invalid) --%>
.z-textbox-rounded-inplace input.z-textbox-rounded-text-invalid,
.z-decimalbox-rounded-inplace input.z-decimalbox-rounded-text-invalid,
.z-intbox-rounded-inplace input.z-intbox-rounded-text-invalid,
.z-longbox-rounded-inplace input.z-longbox-rounded-text-invalid,
.z-doublebox-rounded-inplace input.z-doublebox-rounded-text-invalid {
	background-image: none;
}
.z-textbox-rounded-inplace input.z-textbox-rounded-text-invalid + i.z-textbox-rounded-right-edge,
.z-decimalbox-rounded-inplace input.z-decimalbox-rounded-text-invalid + i.z-decimalbox-rounded-right-edge,
.z-intbox-rounded-inplace input.z-intbox-rounded-text-invalid + i.z-intbox-rounded-right-edge,
.z-longbox-rounded-inplace input.z-longbox-rounded-text-invalid + i.z-longbox-rounded-right-edge,
.z-doublebox-rounded-inplace input.z-doublebox-rounded-text-invalid + i.z-doublebox-rounded-right-edge {
	background-image: none;
}
.z-textbox-rounded-inplace i.z-textbox-rounded-right-edge-invalid,
.z-decimalbox-rounded-inplace i.z-decimalbox-rounded-right-edge-invalid,
.z-intbox-rounded-inplace i.z-intbox-rounded-right-edge-invalid,
.z-longbox-rounded-inplace i.z-longbox-rounded-right-edge-invalid,
.z-doublebox-rounded-inplace i.z-doublebox-rounded-right-edge-invalid {
	<%-- extra class provided for IE 6 --%>
	background-image: none;
}

<%-- error box --%>
.z-errbox {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; font-weight: normal;
}
.z-errbox-center {
	padding: 2px 3px;
}
.z-errbox-left {
	background-repeat: no-repeat;
	cursor: pointer; border: 0;
	padding-left: 17px;
}
.z-errbox-right {
	background-repeat: no-repeat;
	cursor: pointer; border: 0;
	padding-right: 17px;
	background-position: right 0;
}
.z-arrow-d {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowD.png')});
}
.z-arrow-l {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowL.png')});
}
.z-arrow-ld {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowLD.png')});
}
.z-arrow-lu {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowLU.png')});
}
.z-arrow-rd {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowRD.png')});
}
.z-arrow-ru {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowRU.png')});
}
.z-arrow-r {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowR.png')});
}
.z-arrow-u {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowU.png')});
}
.z-errbox-close {
	background-image: url(${c:encodeURL('~./zul/img/errbox/close.gif')});
	zoom: 1; <%--Bug 2916148 --%>
}
.z-errbox-close-over {
	background-image: url(${c:encodeURL('~./zul/img/errbox/close-over.gif')});
}
.z-errbox.z-popup .z-popup-tl,
.z-errbox.z-popup .z-popup-tr,
.z-errbox.z-popup .z-popup-bl,
.z-errbox.z-popup .z-popup-br {
	background-image:url(${c:encodeURL('~./zul/img/errbox/pp-corner.png')});
}
.z-errbox.z-popup .z-popup-cm {
	background-color : #FDF2E7;
	background-image: url(${c:encodeURL('~./zul/img/errbox/pp-cm.png')});
}
.z-errbox.z-popup .z-popup-cl,
.z-errbox.z-popup .z-popup-cr {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pp-clr.png')});
}

<%-- IE 6 GIF  --%>
<c:if test="${c:browser('ie6-')}">
.z-arrow-d {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowD.gif')});
}
.z-arrow-l {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowL.gif')});
}
.z-arrow-ld {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowLD.gif')});
}
.z-arrow-lu {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowLU.gif')});
}
.z-arrow-rd {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowRD.gif')});
}
.z-arrow-ru {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowRU.gif')});
}
.z-arrow-r {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowR.gif')});
}
.z-arrow-u {
	background-image: url(${c:encodeURL('~./zul/img/misc/arrowU.gif')});
}
div.z-errbox div.z-popup-tl,
div.z-errbox div.z-popup-tr,
div.z-errbox div.z-popup-bl,
div.z-errbox div.z-popup-br {
	background-image:url(${c:encodeURL('~./zul/img/errbox/pp-corner.gif')});
}
div.z-errbox div.z-popup-cm {
	background-color : #FDF2E7;
	background-image: url(${c:encodeURL('~./zul/img/errbox/pp-cm.gif')});
}
div.z-errbox div.z-popup-cl,
div.z-errbox div.z-popup-cr {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pp-clr.gif')});
}
</c:if>
