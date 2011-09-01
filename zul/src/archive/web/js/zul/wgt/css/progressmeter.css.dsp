<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

div.z-progressmeter {
	background: #E0E8F3 repeat-x 0 0 ;
	background-image: url(${c:encodeURL('~./zul/img/misc/prgmeter_bg.png')});
	border:1px solid #6F9CDB;
	text-align: left;
	height: 17px;
	overflow: hidden;
}
span.z-progressmeter-img {
	display: -moz-inline-box;
	display: inline-block;
	background: #A4C6F2 repeat-x left center;
	background-image: url(${c:encodeURL('~./zul/img/misc/prgmeter.png')});
	height: 17px;
	line-height: 0;
	font-size: 0;
}
