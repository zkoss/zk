<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- ZK Tabbox --%>
.z-tabbox {
	overflow: hidden;
	visibility: hidden;
}
.z-tabs-cnt .z-tab-hl,
.z-tabs-cnt .z-tab-hr,
.z-tabs-cnt .z-tab-hm,
.z-tabs-cnt .z-tab-text {
	display: block;
}
.z-tabs .z-tabs-space {
	height: 2px;
	border: 1px solid #8DB2E3;
	border-top: 0 none;
	font-size: 1px;
	line-height: 1px;
	background: #DEECFD none repeat scroll 0 0;
}
.z-tabs-scroll .z-tabs-space {
	background: none;
	border: 0;
	height: auto;
}
.z-tabs .z-tabs-cnt {
	background: transparent none repeat scroll 0 0;
	padding-left: 0px;
	list-style-image: none;
	list-style-position: outside;
	list-style-type: none;
	display: block;
	margin: 0;
	border-bottom: 1px solid #8DB2E3;
	width: 100%;
	zoom: 1;
}
.z-tabs {
	overflow: hidden;
	background: transparent none repeat scroll 0 0;
	border: 0;
	padding: 0;
	margin: 0;
	position: relative;
}
.z-tabs-scroll {
	border: 1px solid #8DB2E3;
	background: #DEECFD none repeat scroll 0 0;
	padding-bottom: 2px;
	zoom: 1;
}
.z-tabs-header {
	width: 100%;
	margin: 0px;
	position: relative;
	overflow: hidden;
	zoom: 1;
}
.z-tabs-scroll .z-tabs-cnt {
	background: #C7E3F3 repeat-x scroll center top;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-strip-bg.png')});
	padding-left: 0px;
	padding-top: 1px;
	list-style-image: none;
	list-style-position: outside;
	list-style-type: none;
	display: block;
	margin: 0;
	zoom: 1;
	border-bottom: 1px solid #8DB2E3;
	-moz-user-select: none;
}
.z-tabs-cnt li {
	position: relative;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	cursor: default;
	display: block;
	float: left;
	padding: 0px;
	margin: 0 0 0 2px;
	-moz-user-select: none;
}
.z-tab-close {
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-close.png')});
	background-repeat: no-repeat;
	cursor: pointer;
	display: block;
	height: 11px;
	position: absolute;
	right: 3px;
	top: 3px;
	width: 11px;
	z-index: 2;
	opacity: 0.6;
	filter: alpha(opacity=60);
	zoom: 1;
}
.z-tab-close: hover,
.z-tab-close-over {
	opacity: 1 !important;
	filter: alpha(opacity=100) !important;
}
.z-tab-hl {
	position: relative;
	padding-left: 10px;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tabs-sprite.png')});
	background-repeat: no-repeat;
	-moz-outline: none;
	outline: none;
	cursor: pointer;
}
.z-tab-hr {
	background: transparent no-repeat scroll right -351px;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tabs-sprite.png')});
	padding-right: 10px;
	display: block;
}
.z-tab-hm {
	padding-left: 2px;
	padding-right: 1px;
	overflow: hidden;
	cursor: pointer;
	background: transparent repeat-x scroll 0 -201px;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tabs-sprite.png')});
}
.z-tab-hm-close {
	padding-right: 10px;
}
.z-tabs-edge {
	float:left;
	line-height: 1px !important;
	width: 1px;
	margin: 0 !important;
	padding: 0 !important;
	border: 0 none !important;
	background: transparent !important;
	font-size: 1px !important;
	overflow: hidden;
	zoom: 1;
}
.z-tab .z-tab-hl {
	background-position: 0 -51px;
}
.z-tab .z-tab-hl: hover {
	background-position: 0 -101px;
}
.z-tab .z-tab-hl: hover .z-tab-hr {
	background-position: right -401px;
}
.z-tab .z-tab-hl: hover .z-tab-hm {
	background-position: right -251px;
}
.z-tab .z-tab-hl: hover .z-tab-text {
	color: #15428B;
}
.z-tab .z-tab-text {
	color: #416AA3;
	cursor: pointer;
	font-style: normal;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	font-size-adjust: none;
	padding:4px 0 4px;
	white-space: nowrap;
}
.z-tabs-header-scroll {
	margin-left: 18px;
	margin-right: 18px;
}
.z-tabs-right-scroll {
	right: 0;
	top: 0;
	width: 18px;
	height: 25px;
	background: transparent no-repeat scroll 0 0;
	background-image: url(${c:encodeURL('~./zul/img/tab2/scroll-right.png')});
	border-bottom: 1px solid #8DB2E3;
	cursor: pointer;
	position: absolute;
	z-index: 10;
}
.z-tabs-right-scroll: hover {
	background-position: -18px 0;
}
.z-tabs-left-scroll {
	left: 0;
	top: 0;
	width: 18px;
	height: 25px;
	background: transparent no-repeat scroll -18px 0;
	background-image: url(${c:encodeURL('~./zul/img/tab2/scroll-left.png')});
	border-bottom: 1px solid #8DB2E3;
	cursor: pointer;
	position: absolute;
	z-index: 10;
}
.z-tabs-left-scroll: hover {
	background-position: 0px 0;
}
<%-- Selected --%>
.z-tab-seld .z-tab-hl {
	cursor: default;
	background-position: 0 0px;
	margin: auto auto -1px;
}
.z-tab-seld .z-tab-hm {
	cursor: default;
	background-position: 0 -151px;
	hasLayout: -1;
	overflow: hidden;
	text-overflow: ellipsis;
}
.z-tab-seld .z-tab-close {
	opacity: 0.8;
	filter: alpha(opacity=80);
}
.z-tab-seld .z-tab-hr {
	background-position: right -301px;
}
.z-tab-seld .z-tab-text {
	color: #15428B;
	cursor: default;
	font-weight: bold;
	font-style: normal;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	white-space: nowrap;
	padding:4px 0 5px;
}
<c:if test="${c:isExplorer()}">
.z-tab-seld .z-tab-hr {
	position: relative;
}
</c:if>
<%-- Bug-2432784
<c:if test="${c:isExplorer7()}">
.z-tab-disd .z-tab-hl, .z-tab-disd-seld .z-tab-hl {	room: 1 }</c:if>
--%>
.z-tab-disd .z-tab-hl: hover,
.z-tab-disd-seld .z-tab-hl: hover {
	background-position: 0 -51px;
}
.z-tab-disd .z-tab-hl: hover .z-tab-hr,
.z-tab-disd-seld .z-tab-hl: hover .z-tab-hr {
	background-position: right -351px;
}
.z-tab-disd .z-tab-hl: hover .z-tab-hm,
.z-tab-disd-seld .z-tab-hl: hover .z-tab-hm {
	background-position: 0 -201px;
}
.z-tab-disd .z-tab-hl: hover .z-tab-text,
.z-tab-disd-seld .z-tab-hl: hover .z-tab-text {
	color: gray;
}
.z-tab-disd .z-tab-hl,
.z-tab-disd-seld .z-tab-hl {
	color: gray;
	cursor: default;
	opacity: 0.6;
	filter: alpha(opacity=60);
}
.z-tab-disd .z-tab-hl,
.z-tab-disd-seld .z-tab-hl {
	background-position: 0pt -51px;
}
.z-tab-disd .z-tab-hr,
.z-tab-disd-seld .z-tab-hr {
	background-position: right -351px;
}
.z-tab-disd .z-tab-close: hover ,
.z-tab-disd-seld .z-tab-close: hover {
	opacity: 0.6!important;
	filter: alpha(opacity=60) !important;
}
.z-tab-disd .z-tab-text,
.z-tab-disd-seld .z-tab-text {
	cursor: default;
	font-style: normal;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	white-space: nowrap;
	padding:4px 0 4px;
	color: gray;
}
.z-tab-disd .z-tab-hm ,
.z-tab-disd-seld .z-tab-hm {
	background-position: right -201px;
	cursor: default;
}
<%-- Tabbox vertical --%>
.z-tabbox-ver {
	overflow: hidden;
	visibility: hidden;
}
.z-tabs-ver {
	overflow: hidden;
	float:left;
	background: transparent none repeat scroll 0 0;
	border: 0; padding: 0; margin: 0; position: relative;
}
.z-tabs-ver-scroll {
	background: #DEECFD none repeat scroll 0 0;
	border-top: 1px solid #8DB2E3;
	border-bottom: 1px solid #8DB2E3;
	border-left: 1px solid #8DB2E3;
}
.z-tabs-ver .z-tabs-ver-space {
	background: #DEECFD none repeat scroll 0 0;
	border: 1px solid #8DB2E3;
	border-top: 0 none;
	font-size: 1px;
	width: 2px;
	line-height: 1px;
}
.z-tabs-ver-scroll .z-tabs-ver-space {
	background: none;
	border: 0;
	height: auto;
}
.z-tabs-ver .z-tabs-ver-header {
	background: none; zoom: 1; overflow: hidden; position: relative;
}
.z-tabs-ver-scroll .z-tabs-ver-header {
	background: #CEDFF5 repeat-y scroll center bottom;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-vstrip-bg.gif')});
	zoom: 1;
	overflow: hidden;
	position: relative;
}
.z-tabs-ver-edge {
	margin: 0 !important;
	padding: 0 !important;
	border: 0 none !important;
	font-size: 1px !important;
	line-height: 1px !important;
	overflow: hidden;
	zoom: 1;
	background: transparent !important;
	height: 1px;
}
.z-tabs-ver .z-tabs-ver-cnt {
	padding-top: 0px;
	padding-left: 1px;
	list-style-image: none;
	list-style-position:outside;
	list-style-type: none;
	display: block;
	margin: 0;
	zoom: 1;
	height: 3456px;
	border-right: 1px solid #8DB2E3;
	-moz-user-select: none;
}
.z-tabs-ver .z-tabs-ver-cnt li {
	position: relative;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	cursor: default;
	display: block;
	padding: 2px 0 0 0 ;
	margin: 0 0 0 0;
	-moz-user-select: none;
}
.z-tab-ver-close {
	right: 1px;
	top: 15px;
	height: 11px;
	width: 11px;
	position: absolute;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-close.png')});
	background-repeat: no-repeat;
	opacity: 0.6;
	filter: alpha(opacity=60);
	cursor: pointer;
	display: block;	
	z-index: 2;
	zoom: 1;
}
.z-tab-ver-close: hover,
.z-tab-ver-close-over {
	opacity: 1 !important;
	filter: alpha(opacity=100) !important;
}
.z-tab-ver-noclose {
	position: absolute;
	opacity: 0.6;
	filter: alpha(opacity=60);
	cursor: pointer;
	display: block;
	height: 11px;
	width: 11px;
	right: 1px;
	top: 15px;
	z-index: 2;
}
.z-tab-ver-hl,
.z-tab-ver-hr,
.z-tab-ver-hm,
.z-tab-ver-text {
	display: block;
}
.z-tab-ver-hl {
	position: relative;
	padding-top:8px;
	-moz-outline: none;
	outline: none;
	zoom: 1;
	background: transparent no-repeat scroll -1051px 0;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tabs-vsprite.png')});
	cursor: pointer;
}
.z-tab-ver .z-tab-ver-hl: hover {
	background-position: -1201px 0;
}
.z-tab-ver .z-tab-ver-hl: hover .z-tab-ver-hr {
	background-position: -301px bottom;
}
.z-tab-ver-hl .z-tab-ver-hr {
	padding-bottom: 10px;
	background: transparent no-repeat scroll -151px bottom;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tabs-vsprite.png')});
}
.z-tab-ver .z-tab-ver-hl: hover .z-tab-ver-hm {
	background-position: -751px 0;
}
.z-tab-ver .z-tab-ver-hm {
	overflow: hidden;
	cursor: pointer;
	padding: 2px 14px 0 4px;
	background: transparent repeat-y scroll -601px 0;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tabs-vsprite.png')});
}
.z-tab-ver .z-tab-ver-text {
	font-style: normal;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	text-align:center;
	font-style: normal;
	white-space: nowrap;
	color: #416AA3;
	cursor: pointer;
	font-size-adjust: none;
}
.z-tabs-ver-space {
	float:left;
	background: #DEECFD none repeat scroll 0 0;
	border: 1px solid #8DB2E3;
	border-left: 0 none;
	font-size: 1px;
	width: 2px;
	position: relative;
	<%-- DO NOT Delete this line--%>
}
.z-tabbox-ver .z-tabs-ver-header-scroll {
	margin-top: 18px;
	margin-bottom: 18px;
}
.z-tabs-ver-up-scroll {
	background: transparent no-repeat scroll 1px -18px;
	background-image: url(${c:encodeURL('~./zul/img/tab2/scroll-up.png')});
	border-right: 1px solid #8DB2E3;
	cursor: pointer;
	height: 18px;
	position: absolute;
	right: 0;
	top: 0;
	z-index: 10;
	width: 100%;
	display: block;
}
.z-tabs-ver-down-scroll {
	background: transparent no-repeat scroll 1px 0;
	background-image: url(${c:encodeURL('~./zul/img/tab2/scroll-down.png')});
	border-right: 1px solid #8DB2E3;
	cursor: pointer;
	height: 18px;
	position: absolute;
	right: 0;
	bottom: 0;
	z-index: 10;
	width: 100%;
	display: block;
}
.z-tabs-ver-up-scroll: hover {
	background-position: 1px 0px;
}
.z-tabs-ver-down-scroll: hover {
	background-position: 1px -18px;
}
<%-- Selected --%>
.z-tab-ver-seld .z-tab-ver-close {
	opacity: 0.8;
	filter: alpha(opacity=80);
}
.z-tab-ver-seld .z-tab-ver-hl {
	background-position: -901px 0;
	margin-right: -2px;
	cursor: default;
}
.z-tab-ver-seld .z-tab-ver-hl .z-tab-ver-hr {
	background-position: 0 bottom;
}
.z-tab-ver-seld .z-tab-ver-hm {
	background-position: -451px 0;
	cursor: default;
}
.z-tab-ver-seld .z-tab-ver-text {
	color: #15428B;
	cursor: default;
	font-weight: bold;
	white-space: nowrap;
}
<%-- Disabled --%>
.z-tab-ver-disd .z-tab-ver-hl,
.z-tab-ver-disd-seld .z-tab-ver-hl {
	color: gray;
	cursor: default;
	opacity: 0.6;
	filter: alpha(opacity=60);
}
.z-tab-ver-disd .z-tab-ver-close,
.z-tab-ver-disd-seld .z-tab-ver-close: hover {
	opacity: 0.6 !important;
	filter: alpha(opacity=60) !important;
}
.z-tab-ver-disd .z-tab-ver-hl, .z-tab-ver-disd-seld .z-tab-ver-hl,
.z-tab-ver-disd .z-tab-ver-hl: hover, .z-tab-ver-disd-seld .z-tab-ver-hl: hover {
	background-position: -1051px 0;
}
.z-tab-ver-disd .z-tab-ver-hl .z-tab-ver-hr,
.z-tab-ver-disd-seld .z-tab-ver-hl .z-tab-ver-hr,
.z-tab-ver-disd .z-tab-ver-hl: hover .z-tab-ver-hr,
.z-tab-ver-disd-seld .z-tab-ver-hl: hover .z-tab-ver-hr {
	background-position: -151px bottom;
}
.z-tab-ver-disd .z-tab-ver-hl .z-tab-ver-hm,
.z-tab-ver-disd-seld .z-tab-ver-hl .z-tab-ver-hm,
.z-tab-ver-disd .z-tab-ver-hl: hover .z-tab-ver-hm,
.z-tab-ver-disd-seld .z-tab-ver-hl: hover .z-tab-ver-hm {
	background-position: -601px 0;
	color: gray;
	cursor: default;
}
.z-tab-ver-disd .z-tab-ver-text,
.z-tab-ver-disd-seld .z-tab-ver-text {
	cursor: default;
	color: gray;
}
<%-- Tabpanels Tabpanel --%>
.z-tabpanel {<%-- horz, accd: tabpanel --%>
	border-left: 1px solid #8DB2E3;
	border-right: 1px solid #8DB2E3;
	border-bottom: 1px solid #8DB2E3; padding: 5px;
	zoom: 1;
}
.z-tabpanel-cnt { <%-- Bug 2104974 --%>
	height: 100%;
}
.z-tabbox-ver .z-tabpanels-ver {<%-- vert tabpanels --%>
	overflow: hidden;
	border-top: 1px solid #8DB2E3;
	border-right: 1px solid #8DB2E3;
	border-bottom: 1px solid #8DB2E3;
}
.z-tabbox-ver .z-tabpanel-ver {<%-- vert tabpanel --%>
	padding: 5px;
	zoom: 1;
}

<%-- Tabbox accordion --%>
.z-tabbox-accordion {
	overflow: hidden;
	visibility: hidden;
}
.z-tabpanel-accordion-outer {
	position: relative;
}
.z-tab-accordion-header {
	cursor: pointer;
	position: relative;
	zoom: 1;
}
.z-tab-accordion-text {
	cursor: pointer;
	color: #373737;
	font-family: ${fontFamilyT};
	font-style: normal;
	font-variant: normal;
	font-weight: bold;
	font-size: ${fontSizeM};
	line-height: normal;
	text-decoration: none;
	padding-right: 30px;
	padding-bottom: 2px;
}
.z-tabbox-accordion .z-tabpanel-accordion {
	border-bottom: 1px solid #ABD6EE;
	border-left: 1px solid #ABD6EE;
	border-right: 1px solid #ABD6EE;
	padding: 5px;
	zoom: 1;
}
.z-tab-accordion-tl {
	background: transparent no-repeat 0 top;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-accd-corner.png')});
	zoom: 1;
	height: 5px;
	margin-right: 5px;
	font-size: 0;
	line-height: 0;
}
.z-tab-accordion-tr {
	position: relative;
	background: transparent no-repeat right -5px;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-accd-corner.png')});
	height: 5px;
	margin-right: -5px;
	font-size: 0;
	line-height: 0;
	zoom: 1
}
.z-tab-accordion-hl {
	background: transparent no-repeat scroll 0 0;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-accd-hl.png')});
	padding-left: 6px;
	zoom: 1;
}
.z-tab-accordion-hr {
	background: transparent no-repeat scroll right 0;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-accd-hr.png')});
	padding-right: 6px;
	zoom: 1;
}
.z-tab-accordion-hm {
	background: transparent repeat-x scroll 0 0;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-accd-hm.png')});
	overflow: hidden;
	padding: 2px 0 5px;
	zoom: 1;
}
.z-tab-accordion-close {
	background-repeat: no-repeat;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-close-off.png')});
	cursor: pointer;
	width: 17px;
	height: 16px;
	position: absolute;
	right: 10px;
	top: 5px;
	z-index: 2;
}
.z-tab-accordion-close {
	opacity: 0.6;
	filter: alpha(opacity=60);
}
.z-tab-accordion .z-tab-accordion-close: hover {
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-close-on.png')});
	opacity: 1.0;
	filter: alpha(opacity=100);
}
<%-- Selected --%>
.z-tab-accordion-seld .z-tab-accordion-close {
	opacity: 0.8;
	filter: alpha(opacity=80);
}
<%-- Disabled --%>
.z-tab-accordion-disd a,
.z-tab-accordion-disd-seld a {
	color: gray;
	cursor: default;
	opacity: 0.6;
	filter: alpha(opacity=60);
}
.z-tab-accordion-disd .z-tab-accordion-close: hover,
.z-tab-accordion-disd-seld .z-tab-accordion-close: hover {
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-close-off.png')});
	opacity: 0.6; 
	filter: alpha(opacity=60);
}
.z-tab-accordion-disd .z-tab-accordion-text,
.z-tab-accordion-disd-seld .z-tab-accordion-text {
	cursor: default;
	color: gray;
}
<%-- ZK Tabbox accordion-lite --%>
.z-tabbox-accordion-lite {
	overflow: hidden;
	visibility: hidden;
}
.z-tabpanels-accordion-lite {
	border-top: 1px solid #99BBE8;
	border-right: 1px solid #99BBE8;
	border-left: 1px solid #99BBE8;
	position: relative;
}
.z-tabpanel-accordion-lite-outer {
	position: relative;
}
.z-tab-accordion-lite-header {
	overflow: hidden;
	zoom: 1;
	cursor: pointer;
	position: relative;
	border: 1px solid #99BBE8;
	border-top-width: 0;
	border-right-width: 0;
	border-left-width: 0;
}
.z-tab-accordion-lite-text {
	cursor: pointer;
	color: #373737;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	font-style: normal;
	font-variant: normal;
	line-height: 15px;
	text-decoration: none;
	padding-right: 30px;
}
.z-tabbox-accordion-lite .z-tabpanel-accordion-lite {
	border-bottom: 1px solid #99BBE8;
	padding: 5px;
	zoom: 1;
}
.z-tab-accordion-lite-tl {
	zoom: 1;
	padding-left: 6px;
	line-height: 0;
	display: block;
	text-decoration: none;
	text-decoration: none;
	background: transparent url(${c:encodeURL('~./zul/img/tab2/accdlite-all.png')}) repeat-x scroll 0 -9px;
}
.z-tab-accordion-lite-tr {
	padding-right: 6px;
	display: block;
	background: transparent url(${c:encodeURL('~./zul/img/tab2/accdlite-all.png')}) repeat-x scroll 0 -9px;
}
.z-tab-accordion-lite-tm {
	display: block;
	padding:4px 0 3px 0;
	overflow: hidden;
	background: transparent url(${c:encodeURL('~./zul/img/tab2/accdlite-all.png')}) repeat-x scroll 0 -9px;
}
.z-tab-accordion-lite-close {
	background-repeat: no-repeat;
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-close-off.png')});
	cursor: pointer;
	height: 16px;
	position: absolute;
	right: 10px;
	top: 3px;
	width: 17px;
	z-index: 5;
}
.z-tab-accordion-lite-close {
	opacity: 0.6;
	filter: alpha(opacity=60);
}
.z-tab-accordion-lite .z-tab-accordion-lite-close: hover {
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-close-on-l.gif')});
	opacity: 1; filter: alpha(opacity=100);
}
<%-- Selected --%>
.z-tab-accordion-lite-seld .z-tab-accordion-lite-close {
	opacity: 0.8;
	filter: alpha(opacity=80);
}
<%-- Disabled --%>
.z-tab-accordion-lite-disd .z-tab-accordion-lite-close: hover,
.z-tab-accordion-lite-disd-seld .z-tab-accordion-lite-close: hover {
	background-image: url(${c:encodeURL('~./zul/img/tab2/tab-close-off.png')});
	opacity: 0.6; filter: alpha(opacity=60);
}
.z-tab-accordion-lite-disd a,
.z-tab-accordion-lite-disd-seld a {
	color: gray;
	cursor: default;
	opacity: 0.6;
	filter: alpha(opacity=60);
}
.z-tab-accordion-lite-disd .z-tab-accordion-lite-text,
.z-tab-accordion-lite-disd-seld .z-tab-accordion-lite-text {
	cursor: default;
	color: gray;
}