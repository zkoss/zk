<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<c:set var="fontSizeM" value="small" scope="request" if="${empty fontSizeM}"/>
<c:set var="fontSizeS" value="x-small" scope="request" if="${empty fontSizeS}"/>
<c:set var="fontSizeXS" value="xx-small" scope="request" if="${empty fontSizeXS}"/>

td.z-hbox-sep {
	width: 0.3em; padding: 0; margin: 0;
}
tr.z-vbox-sep, tr-z-box-sep {
	height: 0.3em; padding: 0; margin: 0;
}

<%-- Splitter --%>
.z-splitter-ver-btn-l, .z-splitter-ver-btn-r, .z-splitter-ver-btn-t ,.z-splitter-ver-btn-b,
.z-splitter-hor-btn-l, .z-splitter-hor-btn-r, .z-splitter-hor-btn-t ,.z-splitter-hor-btn-b {
    font-size:0;
}
.z-splitter-hor-outer {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-h-ns.png')}");
    background-repeat: repeat-y; max-width: 8px; width: 8px;
    background-position: top right;
}
.z-splitter-ver-outer td {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-v-ns.png')}");
    background-repeat: repeat-x; max-height: 8px; height: 8px;
    background-position: bottom left;
}
.z-splitter-hor {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-h.png')}");
    background-position: center left; font-size:0; max-width: 8px; width: 8px;
}
.z-splitter-ver {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-v.png')}");
    background-position: top center; font-size:0; max-height: 8px; height: 8px;
}
.z-splitter-hor-ns {
    font-size:0; max-width: 8px; width: 8px;
}
.z-splitter-ver-ns {
    font-size:0; max-height: 8px; height: 8px;
}
.z-splitter-ver-btn-l:hover, .z-splitter-ver-btn-r:hover, .z-splitter-ver-btn-t:hover, .z-splitter-ver-btn-b:hover,
.z-splitter-hor-btn-l:hover, .z-splitter-hor-btn-r:hover, .z-splitter-hor-btn-t:hover, .z-splitter-hor-btn-b:hover {
	opacity:1;
}

.z-splitter-ver-btn-l, .z-splitter-ver-btn-r, .z-splitter-ver-btn-t, .z-splitter-ver-btn-b,
.z-splitter-hor-btn-l, .z-splitter-hor-btn-r, .z-splitter-hor-btn-t, .z-splitter-hor-btn-b {
	filter:alpha(opacity=50);  <%-- IE --%>
	opacity:0.5;  <%-- Moz + FF --%>	
	background-repeat: no-repeat; vertical-align:top;
	display:-moz-inline-box; display:inline-block; font-size:0;
}

.z-splitter-ver-btn-visi, .z-splitter-hor-btn-visi {
	filter:alpha(opacity=100) !important;  <%-- IE --%>
}
.z-splitter-ver-btn-l, .z-splitter-hor-btn-l {
	width: 6px; min-height: 50px; height: 50px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-l.png')});
}
.z-splitter-ver-btn-r, .z-splitter-hor-btn-r {
	width: 6px; min-height: 50px; height: 50px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-r.png')});
}
.z-splitter-ver-btn-t, .z-splitter-hor-btn-t {
	width: 50px; min-height: 6px; height: 6px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-t.png')});

}
.z-splitter-ver-btn-b, .z-splitter-hor-btn-b {
	width: 50px; min-height: 6px; height: 6px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-b.png')});
}

<%-- OS look-and-feel --%>
.z-splitter-os-ver-btn-l, .z-splitter-os-ver-btn-r, .z-splitter-os-ver-btn-t ,.z-splitter-os-ver-btn-b,
.z-splitter-os-hor-btn-l, .z-splitter-os-hor-btn-r, .z-splitter-os-hor-btn-t ,.z-splitter-os-hor-btn-b {
    font-size:0;
}
.z-splitter-os-hor-outer {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-h.gif')}");
    background-repeat: repeat-y; max-width: 8px; width: 8px;
    background-position: top right;
}
.z-splitter-os-ver-outer td {
    background-image:url("${c:encodeURL('~./zul/img/splt/splt-v.gif')}");
    background-repeat: repeat-x; max-height: 8px; height: 8px;
    background-position: bottom left;
}
.z-splitter-os-hor, .z-splitter-os-hor-ns {
    font-size:0; max-width: 8px; width: 8px;
}
.z-splitter-os-ver, .z-splitter-os-ver-ns {
    font-size:0; max-height: 8px; height: 8px;
}
.z-splitter-os-ver-btn-l:hover, .z-splitter-os-ver-btn-r:hover, .z-splitter-os-ver-btn-t:hover ,.z-splitter-os-ver-btn-b:hover,
.z-splitter-os-hor-btn-l:hover, .z-splitter-os-hor-btn-r:hover, .z-splitter-os-hor-btn-t:hover ,.z-splitter-os-hor-btn-b:hover {
	opacity:1;
}
.z-splitter-os-ver-btn-l, .z-splitter-os-ver-btn-r, .z-splitter-os-ver-btn-t ,.z-splitter-os-ver-btn-b,
.z-splitter-os-hor-btn-l, .z-splitter-os-hor-btn-r, .z-splitter-os-hor-btn-t ,.z-splitter-os-hor-btn-b {
	filter:alpha(opacity=50);  <%-- IE --%>
	opacity:0.5;  <%-- Moz + FF --%>	
	background-repeat: no-repeat; vertical-align:top;
	display:-moz-inline-box; display:inline-block; font-size: 0;
}

.z-splitter-os-ver-btn-l, .z-splitter-os-hor-btn-l {
	width: 8px; min-height: 50px; height: 50px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-l.gif')});
}
.z-splitter-os-ver-btn-r, .z-splitter-os-hor-btn-r {
	width: 8px; min-height: 50px; height: 50px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-r.gif')});
}
.z-splitter-os-ver-btn-t, .z-splitter-os-hor-btn-t {
	width: 50px; min-height: 8px; height: 8px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-t.gif')});

}
.z-splitter-os-ver-btn-b, .z-splitter-os-hor-btn-b {
	width: 50px; min-height: 8px; height: 8px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-b.gif')});
}