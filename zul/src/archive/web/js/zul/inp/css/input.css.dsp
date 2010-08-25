<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-textbox-disd,
.z-decimalbox-disd,
.z-intbox-disd,
.z-longbox-disd,
.z-doublebox-disd {
	 opacity: .6;
	 -moz-opacity: .6;
	 filter: alpha(opacity=60);
}

.z-textbox-disd,   .z-decimalbox-disd,   .z-intbox-disd,   .z-longbox-disd,   .z-doublebox-disd,
.z-textbox-disd *, .z-decimalbox-disd *, .z-intbox-disd *, .z-longbox-disd *, .z-doublebox-disd * {
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
	-moz-border-radius: 2px;
	-webkit-border-radius: 2px;
}
.z-textbox-focus, .z-textbox-focus input,
.z-decimalbox-focus, .z-decimalbox-focus input,
.z-intbox-focus, .z-intbox-focus input,
.z-longbox-focus, .z-longbox-focus input,
.z-doublebox-focus, .z-doublebox-focus input {
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
</c:if>
<c:if test="${c:isExplorer()}">
.z-textbox-inplace,
.z-decimalbox-inplace,
.z-intbox-inplace,
.z-longbox-inplace,
.z-doublebox-inplace {
	padding: 3px 2px;
}
</c:if>
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

<c:if test="${c:isExplorer()}">
.z-textbox-disd *, .z-decimalbox-disd *, .z-intbox-disd *, .z-longbox-disd *, .z-doublebox-disd * {
	filter: alpha(opacity=60);
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
</c:if>
