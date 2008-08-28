<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<c:set var="fontSizeM" value="small" scope="request" if="${empty fontSizeM}"/>
<c:set var="fontSizeS" value="x-small" scope="request" if="${empty fontSizeS}"/>
<c:set var="fontSizeXS" value="xx-small" scope="request" if="${empty fontSizeXS}"/>

.z-button {
	font: normal tahoma, verdana, helvetica; cursor: pointer;
	white-space: nowrap; font-size: ${fontSizeM};
}
span.z-button {
	display:-moz-inline-box; vertical-align:bottom; display:inline-block;
}
.z-button-cr .i {
	width: 3px;
}
.z-button-disd * {
	color: gray!important; cursor: default!important;
}
.z-button-disd {
	color: gray; cursor: default; opacity: .6; -moz-opacity: .6; filter: alpha(opacity=60);
}
<%-- default --%>
.z-button .z-button-tl {
	background: url(${c:encodeURL('~./zul/img/button/z-btn-trendy.gif')}) no-repeat 0 0;
	width: 3px; height: 3px; padding: 0; margin: 0;
}
.z-button .z-button-tm {
	background: url(${c:encodeURL('~./zul/img/button/z-btn-trendy.gif')}) repeat-x 0 -252px;		
}
.z-button .z-button-tr {
	background: url(${c:encodeURL('~./zul/img/button/z-btn-trendy.gif')}) no-repeat 0 -126px;
	width: 3px; height: 3px; padding: 0; margin: 0;
}
.z-button .z-button-cl {
	background: url(${c:encodeURL('~./zul/img/button/z-btn-trendy.gif')}) no-repeat 0 -3px;
	width: 3px; padding: 0; margin: 0; text-align: right;
}
.z-button .z-button-cm {
	font-family: Tahoma, Garamond, Century, Arial, serif;
	margin: 0; overflow: hidden;
	vertical-align: middle;
	text-align: center;
	padding: 0 5px;
	cursor: pointer; white-space: nowrap;
	background: url(${c:encodeURL('~./zul/img/button/z-btn-trendy.gif')}) repeat-x 0 -255px;  
}
.z-button .z-button-cr {
	background: url(${c:encodeURL('~./zul/img/button/z-btn-trendy.gif')}) no-repeat 0 -129px;
	width: 3px;  padding: 0; margin: 0;
}
.z-button .z-button-bl {
	background: url(${c:encodeURL('~./zul/img/button/z-btn-trendy.gif')}) no-repeat 0 -123px;
	width: 3px; height: 3px;  padding: 0; margin: 0;
}
.z-button .z-button-bm {
	background: url(${c:encodeURL('~./zul/img/button/z-btn-trendy.gif')}) repeat-x 0 -375px;
	height: 3px;
}
.z-button .z-button-br {
	background: url(${c:encodeURL('~./zul/img/button/z-btn-trendy.gif')}) no-repeat 0 -249px;
	width: 3px; height: 3px; padding: 0; margin: 0;
}
<%-- focus --%>
.z-button-focus .z-button-tl {
	background-position:0 -882px;
}
.z-button-focus .z-button-tm {
	background-position:0 -1134px;	
}
.z-button-focus .z-button-tr {
	background-position:0 -1008px;
}
.z-button-focus .z-button-cl {
	background-position:0 -885px;
}
.z-button-focus .z-button-cm {
  	background-position:0 -1137px;    
}
.z-button-focus .z-button-cr {
	background-position:0 -1011px;	
}
.z-button-focus .z-button-bl {
	background-position:0 -1005px;
}
.z-button-focus .z-button-bm {
	background-position:0 -1257px;	
}
.z-button-focus .z-button-br {
	background-position:0 -1131px;
}
<%-- hover --%>
.z-button-hover .z-button-tl {
	background-position:0 -378px;
}
.z-button-hover .z-button-tm {
	background-position:0 -629px;	
}
.z-button-hover .z-button-tr {
	background-position:0 -504px;
}
.z-button-hover .z-button-cl {
	background-position:0 -381px;
}
.z-button-hover .z-button-cm {
  background-position:0 -633px;    
}
.z-button-hover .z-button-cr {
	background-position:0 -507px;	
}
.z-button-hover .z-button-bl {
	background-position:0 -501px;
}
.z-button-hover .z-button-bm {
	background-position:0 -753px;	
}
.z-button-hover .z-button-br {
	background-position:0 -627px;
}
<%-- click --%>
.z-button-click .z-button-cm{
	background-position:0 -759px;
}
.z-button-click .z-button-bm {
	background-position:0 -879px;	
}