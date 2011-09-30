<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

div.z-progressmeter {
	background: #FFFFFF repeat-x 0 0 ;
	background-image: url(${c:encodeURL('~./zul/img/misc/prgmeter-anim.gif')});
	border:1px solid #CFCFCF;
	text-align: left;
	height: 15px;
	overflow: hidden;
}
span.z-progressmeter-img {
	display: -moz-inline-box;
	display: inline-block;
	background: #FFFFFF repeat-x 0 0;
	background-image: url(${c:encodeURL('~./zul/img/misc/prgmeter.png')});
	height: 15px;
	line-height: 0;
	font-size: 0;
}
