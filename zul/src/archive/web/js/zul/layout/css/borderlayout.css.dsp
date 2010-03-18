<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

@media print {
    .z-borderlayout {
		<c:if test="${c:browser('safari')}"><%-- Bug 2949287--%>
			position: static;
		</c:if>
	}
}

@media screen {
    .z-borderlayout { 
		position: relative; 
	}
}

.z-borderlayout {
	width:100%;
	height:100%;
	overflow: hidden;
	background-color:#CDE6F5;
	border: 0;	
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
}
.z-north,
.z-south,
.z-center {
	width: 100%;
	<%-- Fixed for B30-1902533.zul--%>
}
.z-west,
.z-east {
	height: 100%;
	<%-- Fixed for B30-1902533.zul--%>
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
	height: 8px;
	width: 8px;
	background: #C4DCFB left;
	background-image:url(${c:encodeURL('~./zul/img/splt/splt-h.png')});
	cursor: col-resize;
	cursor: e-resize;
	line-height: 0;
	font-size: 0;
}
.z-north-splt,
.z-south-splt {
	background: #C4DCFB top;
	background-image:url(${c:encodeURL('~./zul/img/splt/splt-v.png')});
	cursor: row-resize;
	cursor: s-resize;
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


.z-borderlayout-icon {
	overflow: hidden;
	width: 16px;
	height: 16px;
	float: right;
	background: transparent no-repeat 0 0;
	background-image : url(${c:encodeURL('~./zul/img/layout/borderlayout-btn.png')});
	margin-left: 2px;
	cursor: pointer;
}

.z-west-header,
.z-center-header,
.z-east-header,
.z-north-header,
.z-south-header {
	color: #0F3B82;
	font-size: ${fontSizeMS};
	font-family: ${fontFamilyT};
	font-weight: bold;
	padding: 5px 3px 4px 5px;
	border-bottom: 1px solid #9ecad8;
	background: transparent repeat-x 0 0;
	background-image: url(${c:encodeURL('~./zul/img/layout/borderlayout-hm.png')});
	white-space: nowrap;
	overflow: hidden;
	line-height: 15px;
	zoom: 1;
	cursor: default;
}

.z-north-exp,
.z-south-exp {
	margin: 3px;
	float: right; 
}
.z-east-exp,
.z-west-exp {
	margin: 3px auto;
	float: none;
}
.z-north-colps,
.z-south-exp {
	background-position: 0 0;
}
.z-north-colps-over,
.z-south-exp-over {
	background-position: -16px 0;
}
.z-east-colps,
.z-west-exp {
	background-position: 0 -16px;
}
.z-east-colps-over,
.z-west-exp-over {
	background-position: -16px -16px;
}
.z-south-colps,
.z-north-exp {
	background-position: 0 -32px;
}
.z-south-colps-over,
.z-north-exp-over {
	background-position: -16px -32px;
}
.z-west-colps,
.z-east-exp {
	background-position: 0 -48px;
}
.z-west-colps-over,
.z-east-exp-over {
	background-position: -16px -48px;
}
.z-east-colpsd,
.z-west-colpsd,
.z-south-colpsd,
.z-north-colpsd {
	background-color: #E1F0F2;
	width: 22px;
	height: 22px;
	border: 1px solid #9ECAD8;
	overflow: hidden;
	position: absolute;
}
.z-east-colpsd-over,
.z-west-colpsd-over,
.z-south-colpsd-over,
.z-north-colpsd-over {
	cursor: pointer;
	background-color: #EEFCFF;
}

<c:if test="${c:browser('ie6-')}">
.z-borderlayout-icon {
	background-image : url(${c:encodeURL('~./zul/img/layout/borderlayout-btn.gif')});
}
</c:if>

<%-- Splitter button --%>
.z-east-splt-btn,
.z-west-splt-btn,
.z-north-splt-btn,
.z-south-splt-btn {
	filter: alpha(opacity=50);  <%-- IE --%>
	opacity: 0.5;  <%-- Moz + FF --%>
	background-repeat: no-repeat;
	display: -moz-inline-box;
	vertical-align: top;
	display: inline-block;	
    line-height: 1px;
    font-size: 1px;
    cursor: pointer;
}
.z-east-splt-btn-over,
.z-west-splt-btn-over,
.z-north-splt-btn-over,
.z-south-splt-btn-over {
	opacity: 1;
	filter: alpha(opacity=100);
}
.z-west-splt-btn,
.z-east-splt-btn {
	width: 6px; min-height: 50px; height: 50px;
}
.z-west-splt-btn {
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-l.png')});
}
.z-east-splt-btn {
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-r.png')});
}
.z-north-splt-btn,
.z-south-splt-btn {
	width: 50px; min-height: 5px; height: 6px;
}
.z-north-splt-btn {
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-t.png')});
}
.z-south-splt-btn {
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-b.png')});
}