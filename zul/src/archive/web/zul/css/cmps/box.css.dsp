<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

td.z-hbox-sep {
	width: 0.3em; padding: 0; margin: 0;
}
tr.z-vbox-sep {
	height: 0.3em; padding: 0; margin: 0;
}

<%-- Splitter --%>
.z-splitter-ver-btn-t ,.z-splitter-ver-btn-b,
.z-splitter-hor-btn-l, .z-splitter-hor-btn-r {
    font-size:0;
}
.z-splitter-hor-outer {
    background: transparent repeat-y top right;
    background-image:url(${c:encodeURL('~./zul/img/splt/splt-h-ns.png')});
    max-width: 8px; width: 8px;
}
.z-splitter-ver-outer .z-splitter-ver-outer-td {
 	background: transparent repeat-x bottom left;
    background-image:url(${c:encodeURL('~./zul/img/splt/splt-v-ns.png')});
    max-height: 8px; height: 8px;
}
.z-splitter-hor {
	background: transparent no-repeat center left;
    background-image:url(${c:encodeURL('~./zul/img/splt/splt-h.png')});
    font-size:0; max-width: 8px; width: 8px;
}
.z-splitter-ver {
	background: transparent no-repeat top center;
    background-image:url(${c:encodeURL('~./zul/img/splt/splt-v.png')});
    font-size:0; max-height: 8px; height: 8px;
}
.z-splitter-hor-ns {
    background-image: none; background-position: none;
}
.z-splitter-ver-ns {
    background-image: none; background-position: none;
}
.z-splitter-ver-btn-t:hover, .z-splitter-ver-btn-b:hover,
.z-splitter-hor-btn-l:hover, .z-splitter-hor-btn-r:hover {
	opacity:1;
}

.z-splitter-ver-btn-t, .z-splitter-ver-btn-b,
.z-splitter-hor-btn-l, .z-splitter-hor-btn-r {
	filter:alpha(opacity=50);  <%-- IE --%>
	opacity:0.5;  <%-- Moz + FF --%>
	background-repeat: no-repeat; vertical-align:top;
	display:-moz-inline-box; display:inline-block; font-size:0;
}

.z-splitter-ver-btn-visi, .z-splitter-hor-btn-visi {
	filter:alpha(opacity=100) !important;  <%-- IE --%>
}
.z-splitter-hor-btn-l {
	width: 6px; min-height: 50px; height: 50px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-l.png')});
}
.z-splitter-hor-btn-r {
	width: 6px; min-height: 50px; height: 50px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-r.png')});
}
.z-splitter-ver-btn-t {
	width: 50px; min-height: 6px; height: 6px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-t.png')});
}
.z-splitter-ver-btn-b {
	width: 50px; min-height: 6px; height: 6px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-b.png')});
}

<%-- OS look-and-feel --%>
.z-splitter-os-ver-btn-t, .z-splitter-os-ver-btn-b,
.z-splitter-os-hor-btn-l, .z-splitter-os-hor-btn-r {
    font-size:0;
}
.z-splitter-os-hor-outer {
	background: transparent repeat-y top right;
    background-image:url(${c:encodeURL('~./zul/img/splt/splt-h.gif')});
    max-width: 8px; width: 8px;
}
.z-splitter-os-ver-outer .z-splitter-os-ver-outer-td {
	background: transparent repeat-x bottom left;
    background-image:url(${c:encodeURL('~./zul/img/splt/splt-v.gif')});
   	max-height: 8px; height: 8px;
}
.z-splitter-os-hor, .z-splitter-os-hor-ns {
    font-size:0; max-width: 8px; width: 8px;
}
.z-splitter-os-ver, .z-splitter-os-ver-ns {
    font-size:0; max-height: 8px; height: 8px;
}
.z-splitter-os-ver-btn-t:hover, .z-splitter-os-ver-btn-b:hover,
.z-splitter-os-hor-btn-l:hover, .z-splitter-os-hor-btn-r:hover {
	opacity:1;
}
.z-splitter-os-ver-btn-t, .z-splitter-os-ver-btn-b,
.z-splitter-os-hor-btn-l, .z-splitter-os-hor-btn-r {
	filter:alpha(opacity=50);  <%-- IE --%>
	opacity:0.5;  <%-- Moz + FF --%>
	background-repeat: no-repeat; vertical-align:top;
	display:-moz-inline-box; display:inline-block; font-size: 0;
}

.z-splitter-os-hor-btn-l {
	width: 8px; min-height: 50px; height: 50px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-l-os.gif')});
}
.z-splitter-os-hor-btn-r {
	width: 8px; min-height: 50px; height: 50px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-r-os.gif')});
}
.z-splitter-os-ver-btn-t {
	width: 50px; min-height: 8px; height: 8px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-t-os.gif')});
}
.z-splitter-os-ver-btn-b {
	width: 50px; min-height: 8px; height: 8px;
	background-image: url(${c:encodeURL('~./zul/img/splt/colps-b-os.gif')});
}
