<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
.z-borderlayout {
	width:100%;
	height:100%;
	overflow: hidden;
	background-color:#CDE6F5;
	border: 0;
	position: relative;
	visibility: hidden;
}

.z-north,
.z-south,
.z-west,
.z-center,
.z-east {
	border: 1px solid #9ECAD8;
	position: absolute;
	overflow: hidden;
	background-color: white;
	<%--width: 100%; display wrong in F35-2188951.zul --%>
}
.z-west-noborder,
.z-center-noborder,
.z-east-noborder,
.z-north-noborder,
.z-south-noborder {
	border:0;
}
.z-east-splt,
.z-west-splt,
.z-north-splt,
.z-south-splt {
	position: absolute;
	height: 6px;
	width: 6px;
	background: #C4DCFB left;
	background-image:url("${c:encodeURL('~./zul/img/splt/splt-h.png')}");
	cursor: e-resize;
	cursor: col-resize;
	line-height: 0;
	font-size: 0;
}
.z-north-splt,
.z-south-splt {
	background: #C4DCFB top;
	background-image:url("${c:encodeURL('~./zul/img/splt/splt-v.png')}");
	cursor: s-resize;
	cursor: row-resize;	
}

.z-west, .z-west-colpsd {
	z-index: 12;
}
.z-center {
	z-index: 8;
}
.z-east, .z-east-colpsd {
	z-index: 10;
}
.z-north, .z-north-colpsd {
	z-index: 16;
}
.z-south, .z-south-colpsd {
	z-index: 14;
} 
.z-west-splt {
	z-index: 11;
}
.z-east-splt {
	z-index: 9;
}
.z-north-splt {
	z-index: 15;
}
.z-south-splt {
	z-index: 13;
}
