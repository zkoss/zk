<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
.loading-indicator{
	font-size:12px;
	height:18px;
}

.loading-indicator {
    font-size:8pt;
    background-image:url(${c:encodeURL("~./js/ext/yuiext/resources/images/grid/loading.gif")});
    background-repeat: no-repeat;
    background-position:top left;
    padding-left:20px;
	height:18px;
	text-align:left;
}

#loading{
	position:absolute;
	left:45%;
	top:40%;
	border:1px solid #6593cf;
	padding:2px;
	background:#c3daf9;
	width:150px;
	text-align:center;
	z-index:20001;
}
#loading .loading-indicator{
	border:1px solid #a3bad9;
	background:white url(block-bg.gif) repeat-x;
	color:#003366;
	font:bold 13px tahoma,arial,helvetica;
	padding:10px;
	margin:0;
}
