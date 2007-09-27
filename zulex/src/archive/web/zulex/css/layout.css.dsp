<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
body{
	height:100%;
}
div.layout-container {
    width:100%;
    height:100%;
    overflow:hidden;
	background-color:#C4DCFB;
	border:0 none;
	position: relative;
}
div.layout-region {
    position:absolute;
    border:0 none; 
    overflow:hidden;
    background-color:white;
}
div.layout-region-normal {
	border:1px solid #9CBDFF; 
}
div.layout-nested {
	border:0 none;
}
div.layout-region-east, div.layout-region-west {
    z-index:10;
}
div.layout-region-north, div.layout-region-south {
    z-index:11;
}
div.layout-split-h {
    background-image:url("${c:encodeURL('~./zulex/img/layout/splt-h.png')}");
    background-position: left;
}
div.layout-split-v {
    background-image:url("${c:encodeURL('~./zulex/img/layout/splt-v.png')}");
    background-position: top;
}
div.layout-split-h-ns {
    background-image:url("${c:encodeURL('~./zulex/img/layout/splt-h-ns.png')}");
    background-position: left;
}
div.layout-split-v-ns {
    background-image:url("${c:encodeURL('~./zulex/img/layout/splt-v-ns.png')}");
    background-position: top;
}
div.layout-split{
    position:absolute;
    height:6px;
    width:6px;
    line-height:1px;
    font-size:1px;
    z-index:12;
    background-color: #C4DCFB;
}
.layout-split-button:hover {
	opacity:1;
}

.layout-split-button{
	filter:alpha(opacity=50);  /* IE */
	opacity:0.5;  /* Moz + FF */
}