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
}
.z-textbox, .z-decimalbox, .z-intbox, .z-longbox, .z-doublebox {
	background: #FFFFFF repeat-x 0 0;
	border: 1px solid #E6E6E6;
	border-top-color: #B2B2B2;
	border-top-color: #B2B2B2;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	font-weight: normal;
	padding-top: 2px;
	padding-bottom: 2px;
	border-radius: 2px;
	-moz-border-radius: 2px;
	-webkit-border-radius: 2px;
	<c:if test="${zk.safari > 0}">
		margin: 0;
	</c:if>
}
.z-textbox-focus, .z-textbox-focus input,
.z-decimalbox-focus, .z-decimalbox-focus input,
.z-intbox-focus, .z-intbox-focus input,
.z-longbox-focus, .z-longbox-focus input,
.z-doublebox-focus, .z-doublebox-focus input {
	background: #FFFFFF repeat-x 0 0;
	border: 1px solid #D5EAFD;
	border-top: 1px solid #94B9DA;
	border-top: 1px solid #94B9DA;
}
.z-textbox-text-invalid,
.z-decimalbox-text-invalid,
.z-intbox-text-invalid,
.z-longbox-text-invalid,
.z-doublebox-text-invalid {
	background: #FFF repeat-x 0 0;
	border: 1px solid #F2AEB2;
	/* border-top: 1px solid #D59191; */
}
.z-textbox-readonly, .z-textbox-text-disd,
.z-intbox-readonly, .z-intbox-text-disd,
.z-longbox-readonly, .z-longbox-text-disd,
.z-doublebox-readonly, .z-doublebox-text-disd,
.z-decimalbox-readonly, .z-decimalbox-text-disd {
	background: #F0F0F0;
	border: 1px solid #E6E6E6;
	border-top: 1px solid #B2B2B2;
}
.z-textbox-readonly,
.z-intbox-readonly,
.z-longbox-readonly,
.z-doublebox-readonly,
.z-decimalbox-readonly {
	background: #FAFAFA;
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
<c:if test="${zk.opera > 0}">
.z-textbox-inplace,
.z-decimalbox-inplace,
.z-intbox-inplace,
.z-longbox-inplace,
.z-doublebox-inplace {
	padding: 3px 2px;
}
</c:if>
<c:if test="${zk.ie > 0}">
.z-textbox-inplace,
.z-decimalbox-inplace,
.z-intbox-inplace,
.z-longbox-inplace,
.z-doublebox-inplace {
	padding: 3px 2px;
}
</c:if>

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
	<c:if test="${zk.safari > 0}">
		margin: 0;
	</c:if>
	<c:if test="${zk.opera > 0}">
		font-style: normal;
	</c:if>
	height: 14px;
	border: 0;
	padding: 5px 4px;
	background-image: url(${c:encodeURL('~./zul/img/button/timebox-rounded.png')});
	background-position: 0 -24px;
}
.z-textbox-rounded-right-edge,
.z-decimalbox-rounded-right-edge,
.z-intbox-rounded-right-edge,
.z-longbox-rounded-right-edge,
.z-doublebox-rounded-right-edge {
	background: transparent no-repeat 0 0;
	background-image: url(${c:encodeURL('~./zul/img/button/timebox-rounded.png')});	
	background-position: -19px -120px;
	vertical-align: top; 
	overflow: hidden;
	display: -moz-inline-box; 
	display: inline-block;
	border: 0;
	height: 24px;
	width: 5px;
	cursor: default;
	<c:if test="${zk.ie < 8}">
		margin-top: 1px;
	</c:if>
}

.z-textbox-ml-rounded {
	border-radius: 7px;
	-moz-border-radius: 7px;
	-webkit-border-radius: 7px;
}
<%-- rounded: disabled --%>
.z-textbox-rounded-disd,
.z-decimalbox-rounded-disd,
.z-intbox-rounded-disd,
.z-longbox-rounded-disd,
.z-doublebox-rounded-disd {
	opacity: .6;
	-moz-opacity: .6;
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
<%-- rounded: focus, read only --%>
.z-textbox-rounded-focus input.z-textbox-rounded-inp,
.z-decimalbox-rounded-focus input.z-decimalbox-rounded-inp,
.z-intbox-rounded-focus input.z-intbox-rounded-inp,
.z-longbox-rounded-focus input.z-longbox-rounded-inp,
.z-doublebox-rounded-focus input.z-doublebox-rounded-inp {
	outline: none;
}
.z-textbox-rounded-focus input.z-textbox-rounded-inp,
.z-decimalbox-rounded-focus input.z-decimalbox-rounded-inp,
.z-intbox-rounded-focus input.z-intbox-rounded-inp,
.z-longbox-rounded-focus input.z-longbox-rounded-inp,
.z-doublebox-rounded-focus input.z-doublebox-rounded-inp,
.z-textbox-rounded-real-readonly .z-textbox-rounded-inp,
.z-decimalbox-rounded-real-readonly .z-decimalbox-rounded-inp,
.z-intbox-rounded-real-readonly .z-intbox-rounded-inp,
.z-longbox-rounded-real-readonly .z-longbox-rounded-inp,
.z-doublebox-rounded-real-readonly .z-doublebox-rounded-inp {
	background-image: url(${c:encodeURL('~./zul/img/button/timebox-rounded.png')});	
	background-position: 0 -72px;
}
.z-textbox-rounded-focus i.z-textbox-rounded-right-edge,
.z-decimalbox-rounded-focus i.z-decimalbox-rounded-right-edge,
.z-intbox-rounded-focus i.z-intbox-rounded-right-edge,
.z-longbox-rounded-focus i.z-longbox-rounded-right-edge,
.z-doublebox-rounded-focus i.z-doublebox-rounded-right-edge,
.z-textbox-rounded-real-readonly .z-textbox-rounded-right-edge,
.z-decimalbox-rounded-real-readonly .z-decimalbox-rounded-right-edge,
.z-intbox-rounded-real-readonly .z-intbox-rounded-right-edge,
.z-longbox-rounded-real-readonly .z-longbox-rounded-right-edge,
.z-doublebox-rounded-real-readonly .z-doublebox-rounded-right-edge {
	background-image: url(${c:encodeURL('~./zul/img/button/timebox-rounded.png')});
	background-position: -19px -192px;
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
<%-- rouneded: inplace --%>
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
	font-size: ${fontSizeM}; 
	font-weight: normal;
	-moz-box-shadow: 0 0 0 rgba(0, 0, 0, 0);
	overflow: visible !important;
}
.z-errbox .z-popup-cnt {
	font-family: ${fontFamilyC};
	font-size: 11;
	font-weight: bold;
	padding: 2px 2px;
}
.z-errbox-center {
	color: #940000;
	padding: 5px 3px;
}
.z-errbox-left {
	background-image: url(${c:encodeURL('~./zul/img/errbox/error-icon.png')});
	background-repeat: no-repeat;
	background-position: 5px 4px;
	cursor: pointer; 
	border: 0;
	padding-left: 22px;
}
.z-errbox-right {
	background-repeat: no-repeat;
	cursor: pointer; 
	border: 0;
	padding-right: 11px;
	background-position: right 0;
}
.z-pointer {
	background-repeat: no-repeat;
	position: absolute;
	width: 15px;
	height: 11px;
}
.z-pointer-d {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pointerD.png')});
	height: 6px;
}
.z-pointer-u {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pointerU.png')});
	height: 6px;
}
.z-pointer-l {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pointerL.png')});
	width: 6px;
}
.z-pointer-r {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pointerR.png')});
	width: 6px;
}
.z-pointer-ld {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pointerLD.png')});
}
.z-pointer-lu {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pointerLU.png')});
}
.z-pointer-rd {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pointerRD.png')});
}
.z-pointer-ru {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pointerRU.png')});
}

.z-errbox-close, .z-errbox-close-over {
	background-image: url(${c:encodeURL('~./zul/img/errbox/error-close.gif')});
	zoom: 1; <%--Bug 2916148 --%>
}
.z-errbox-close-over {
	background-image: url(${c:encodeURL('~./zul/img/errbox/error-close-over.gif')});
}
.z-errbox.z-popup .z-popup-cl {
	background: transparent repeat-x 0 0;
	background-color : #FFEDED;
	border: 1px solid #940000;
	-moz-border-radius: 1px 1px 1px 1px;
	-moz-box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.35);
}

<c:if test="${zk.ie > 0}">
.z-textbox-disd *, .z-decimalbox-disd *, .z-intbox-disd *, .z-longbox-disd *, .z-doublebox-disd * {
	filter: alpha(opacity=60);
}

<%-- IE 6 GIF  --%>
<c:if test="${zk.ie == 6}">
.z-pointer-d {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pointerD.gif')});
}
.z-pointer-l {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pointerL.gif')});
}
.z-pointer-ld {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pointerLD.gif')});
}
.z-pointer-lu {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pointerLU.gif')});
}
.z-pointer-rd {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pointerRD.gif')});
}
.z-pointer-ru {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pointerRU.gif')});
}
.z-pointer-r {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pointerR.gif')});
}
.z-pointer-u {
	background-image: url(${c:encodeURL('~./zul/img/errbox/pointerU.gif')});
}
.z-errbox-left {
	background-image: url(${c:encodeURL('~./zul/img/errbox/error-icon.gif')});
}
.z-popup .z-popup-cl {
	background: transparent repeat-x 0 0;
	background-image: url(${c:encodeURL('~./zul/img/popup2/popup-bg.png')});
	background-position: 0 -2px;
	border: 1px solid #CFCFCF;
	padding-left: 0;
}
.z-errbox .z-popup-cl {
	background: none !important;
	background-color : #FFEDED !important;
	border: 1px solid #940000 !important;
	-moz-border-radius: 1px 1px 1px 1px !important;
	-moz-box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.35) !important;
}
</c:if>
</c:if>
