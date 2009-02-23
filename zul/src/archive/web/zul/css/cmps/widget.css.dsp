<%@ page contentType="text/css;charset=UTF-8" %>
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
	background-image: url(${c:encodeURL('~./zul/img/grid/text-bg.gif')});
	border: 1px solid #7F9DB9;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; 
	font-weight: normal;
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
	background-image: url(${c:encodeURL('~./zul/img/grid/text-bg-invalid.gif')});
	border: 1px solid #DD7870;
}
.z-textbox-readonly, .z-textbox-text-disd,
.z-intbox-readonly, .z-intbox-text-disd,
.z-longbox-readonly, .z-longbox-text-disd,
.z-doublebox-readonly, .z-doublebox-text-disd,
.z-decimalbox-readonly, .z-decimalbox-text-disd {
	background: #ECEAE4;
}