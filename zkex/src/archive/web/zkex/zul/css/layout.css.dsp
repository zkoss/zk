<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
div.layout-container {
    width: 100%; height: 100%; background-color:#C4DCFB;
	border: 0; position: relative; overflow: hidden; visibility: hidden;
}
div.layout-region {
    position: absolute; background-color: white; border: 0; overflow: hidden;
}
div.layout-region-normal {
	border: 1px solid #9CBDFF; 
}
div.layout-nested {
	border: 0;
}
div.layout-region-east, div.layout-region-west {
    z-index:10;
}
div.layout-region-north, div.layout-region-south {
    z-index:11;
}
div.layout-split-h {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-h.png')}");
    background-position: left;
}
div.layout-split-v {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-v.png')}");
    background-position: top;
}
div.layout-split-h-ns {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-h-ns.png')}");
    background-position: left;
}
div.layout-split-v-ns {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-v-ns.png')}");
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
span.layout-split-btn-l:hover, span.layout-split-btn-r:hover, span.layout-split-btn-t:hover ,span.layout-split-btn-b:hover {
	opacity:1;
}

span.layout-split-btn-l, span.layout-split-btn-r, span.layout-split-btn-t ,span.layout-split-btn-b {
	filter:alpha(opacity=50);  <%-- IE --%>
	opacity:0.5;  <%-- Moz + FF --%>
	background-repeat: no-repeat;
	display:-moz-inline-box; vertical-align:top;
	display:inline-block;	
    line-height:1px;
    font-size:1px;
}
span.layout-split-btn-l {
	width: 6px; min-height: 50px; height: 50px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-l.png')});
}
span.layout-split-btn-r {
	width: 6px; min-height: 50px; height: 50px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-r.png')});
}
span.layout-split-btn-t {
	width: 50px; min-height: 6px; height: 6px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-t.png')});

}
span.layout-split-btn-b {
	width: 50px; min-height: 5px; height: 6px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-b.png')});
}