<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
.z-tabbox,
.z-tabbox-ver,
.z-tabbox-accordion,
.z-tabbox-accordion-lite {
	overflow: hidden;
}
.z-tabs-cnt .z-tab-hl,
.z-tabs-cnt .z-tab-hr,
.z-tabs-cnt .z-tab-hm,
.z-tabs-cnt .z-tab-text {
	display: block;
}
.z-tab-text,
.z-tab-ver-text,
.z-tab-accordion-text,
.z-tab-accordion-lite-text {
	overflow: hidden;
	text-overflow: ellipsis;
}
.z-tabs .z-tabs-space {
	font-size: 0;
	line-height: 0;
	background: none;
	border: 0;
	height: auto;
}
.z-tabs, 
.z-tabs-ver {
	overflow: hidden;
	background: transparent none repeat 0 0;
	border: 0;
	padding: 0;
	margin: 0;
	position: relative;
}
.z-toolbar-tabs {
	overflow: hidden;
	position: absolute;
	background: none repeat scroll 0 0 transparent;
	right: 0;
	top: 0;
	z-index: 1;
	padding: 0 4px;
	margin-top: -1px;
	border-bottom: 1px solid #E1E1E1;
	height: 100%;
	<c:if test="${zk.ie == 6}">
		padding-bottom: 5px;
	</c:if>
}
.z-toolbar-tabs-outer {
	background: none repeat scroll 0 0 transparent;
	border-bottom: 0;
	overflow: auto;
}
.z-toolbar-tabs-outer .z-tabs-header {
	padding-bottom: 0;
}
.z-toolbar-tabs-body {
	padding-top: 2px;
}
.z-tabs-header {
	width: 100%;
	margin: 0;
	position: relative;
	overflow: hidden;
	zoom: 1;
}
.z-tabs-header .z-clear {
	height: 0;
}
.z-tabs .z-tabs-cnt {
	background: none repeat scroll 0 0 transparent;
	padding-left: 0;
	padding-top: 0;
	border-bottom: 1px solid #E1E1E1;
	list-style-image: none;
	list-style-position: outside;
	list-style-type: none;
	display: block;
	margin: 0;
	width: 100%;
	zoom: 1;
	-moz-user-select: none;
}
.z-tabs-cnt li {
	position: relative;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	cursor: default;
	display: block;
	float: left;
	padding: 0;
	margin: 0;
	-moz-user-select: none;
}
.z-tab-close,
.z-tab-ver-close {
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/tab-close.gif')});
	background-repeat: no-repeat;
	cursor: pointer;
	display: block;
	width: 15px;
	height: 15px;
	position: absolute;
	right: 3px;
	top: 5px;
	z-index: 15;
	opacity: .8;
	filter: alpha(opacity=80);
	zoom: 1;
}
.z-tab-ver-close {
	top: 9px;
	left: 3px;
}
.z-tab-close:hover,
.z-tab-close-over,
.z-tab-ver-close:hover,
.z-tab-ver-close-over {
	background-position: right 0;
}
.z-tab-hl {
	position: relative;
	padding-left: 10px;
	background: transparent no-repeat 0 -128px;
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/tab-corner.png')});
	-moz-outline: none;
	outline: none;
	cursor: pointer;
}
.z-tab-hr {
	background: transparent no-repeat right -128px;
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/tab-corner.png')});
	padding-right: 10px;
	display: block;
}
.z-tab-hm {
	padding-left: 2px;
	padding-right: 1px;
	overflow: hidden;
	cursor: pointer;
	background: transparent repeat-x 0 -128px;
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/tab-hm.png')});
}
.z-tab-hm-close {
	padding-right: 10px;
}
.z-tabs-edge {
	float: left;
	width: 1px;
	margin: 0 !important;
	padding: 0 !important;
	border: 0 none !important;
	background: transparent !important;
	font-size: 0 !important;
	line-height: 0 !important;
	overflow: hidden;
	zoom: 1;
}
.z-tab .z-tab-hl:hover {
	background-position: 0 -64px;
}
.z-tab .z-tab-hl:hover .z-tab-hr {
	background-position: right -64px;
}
.z-tab .z-tab-hl:hover .z-tab-hm {
	background-position: right -64px;
}
.z-tab .z-tab-hl:hover .z-tab-text {
	color: #555555;
}
.z-tab .z-tab-text {
	color: #555555;
	cursor: pointer;
	font-style: normal;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	font-size-adjust: none;
	padding:4px 0 4px;
	white-space: nowrap;
}
.z-tabs-header-scroll {
	margin-left: 25px;
	margin-right: 25px;
}
.z-tabs-scroll {
	background:none repeat scroll 0 0 transparent;
	border: 0;
	padding-bottom: 0;
	zoom: 1;
}
.z-tabs-scroll .z-tabs-right-scroll,
.z-tabs-scroll .z-tabs-left-scroll {
	right: 0;
	top: 0;
	width: 25px;
	height: 100%;
	background: transparent no-repeat -25px 2px;
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/scroll-r.png')});
	border-bottom: 1px solid #E1E1E1;
	cursor: pointer;
	position: absolute;
	z-index: 25;
	margin-top: -1px;
}

.z-tabs-scroll .z-tabs-left-scroll {
	left: 0;
	background: transparent no-repeat 0 2px;
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/scroll-l.png')});
}
.z-tabs-scroll .z-tabs-left-scroll:hover {
	background-position: -25px 2px;
}
.z-tabs-scroll .z-tabs-right-scroll:hover {
	background-position: 0 2px;
}
.z-tabs-scroll .z-toolbar-tabs-outer .z-tabs-cnt, 
.z-tabs .z-toolbar-tabs-outer .z-tabs-cnt {
	border-bottom: 1px solid #E1E1E1;
}
<%-- Selected --%>
.z-tab-seld .z-tab-hl {
	cursor: default;
	background-position: 0 0;
	margin: auto auto -1px;
}
.z-tab-seld .z-tab-hm {
	cursor: default;
	background-position: 0 0;
	overflow: hidden;
	text-overflow: ellipsis;
}
.z-tab-seld .z-tab-close {
	opacity: 1;
	filter: alpha(opacity=100);
}
.z-tab-seld .z-tab-hr {
	background-position: right 0;
	<c:if test="${zk.ie == 6}">
	position: relative;
	</c:if>
}
.z-tab-seld .z-tab-text {
	color: #555555;
	cursor: default;
	font-weight: bold;
	font-style: normal;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	white-space: nowrap;
	padding:4px 0 5px;
}
.z-tab-seld .z-tab-hl:hover,
.z-tab-seld .z-tab-hl:hover .z-tab-hm {
	background-position: 0 0;
}
.z-tab-seld .z-tab-hl:hover .z-tab-hr {
	background-position: right 0;
}
.z-tab-disd .z-tab-hl:hover,
.z-tab-disd-seld .z-tab-hl:hover,
.z-tab-disd .z-tab-hl:hover .z-tab-hm,
.z-tab-disd-seld .z-tab-hl:hover .z-tab-hm,
.z-tab-disd .z-tab-hl,
.z-tab-disd-seld .z-tab-hl {
	background-position: 0 -128px;
}
.z-tab-disd .z-tab-hl:hover .z-tab-hr,
.z-tab-disd-seld .z-tab-hl:hover .z-tab-hr,
.z-tab-disd .z-tab-hr,
.z-tab-disd-seld .z-tab-hr,
.z-tab-disd .z-tab-hm ,
.z-tab-disd-seld .z-tab-hm {
	background-position: right -128px;
	cursor: default;
}
.z-tab-disd .z-tab-hl:hover .z-tab-text,
.z-tab-disd-seld .z-tab-hl:hover .z-tab-text {
	color: gray;
}
.z-tab-disd .z-tab-close:hover ,
.z-tab-disd-seld .z-tab-close:hover {
	background-position: 0 0;
}
.z-tab-disd .z-tab-hl,
.z-tab-disd-seld .z-tab-hl {
	color: gray;
	cursor: default;
	opacity: .4;
	filter: alpha(opacity=40);
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

<%-- Tabbox vertical --%>
.z-tabs-ver {
	float:left;
}
.z-tabs-ver-scroll {
	background: none repeat scroll 0 0 transparent;
	border: 0;
}
.z-tabs-ver .z-tabs-ver-space,
.z-tabs-ver-space {
	background: none repeat scroll 0 0 transparent;
	border: 0;
	font-size: 0;
	line-height: 0;
	width: 0;
}
.z-tabs-ver .z-tabs-ver-space {
	border-top: 0 none;
}
.z-tabs-ver-scroll .z-tabs-ver-space {
	background: none;
	border: 0;
	height: auto;
}
.z-tabs-ver-scroll .z-tabs-ver-header {
	background: none repeat scroll 0 0 transparent;
	zoom: 1;
	overflow: hidden;
	position: relative;
}
.z-tabs-ver-edge {
	margin: 0 !important;
	padding: 0 !important;
	border: 0 none !important;
	font-size: 0 !important;
	line-height: 0 !important;
	overflow: hidden;
	zoom: 1;
	background: transparent !important;
	height: 1px;
}
.z-tabs-ver .z-tabs-ver-cnt {
	padding-left: 0;
	padding-top: 0;
	list-style-image: none;
	list-style-position:outside;
	list-style-type: none;
	display: block;
	margin: 0;
	zoom: 1;
	height: 4096px;
	border-right: 1px solid #E1E1E1;
	-moz-user-select: none;
}
.z-tabs-ver .z-tabs-ver-cnt li {
	position: relative;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	cursor: default;
	display: block;
	margin: 0;
	-moz-user-select: none;
}
.z-tab-ver-noclose {
	display:none;
}
.z-tab-ver-hl,
.z-tab-ver-hr,
.z-tab-ver-hm,
.z-tab-ver-text {
	display: block;
}
.z-tab-ver-hl {
	position: relative;
	padding-top: 9px;
	-moz-outline: none;
	outline: none;
	zoom: 1;
	background: transparent no-repeat -512px 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/tab-v-corner.png')});
	cursor: pointer;
}
.z-tab-ver-hl .z-tab-ver-hr {
	padding-bottom: 11px;
	background: transparent no-repeat -512px bottom;
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/tab-v-corner.png')});
}
.z-tab-ver .z-tab-ver-hm {
	overflow: hidden;
	cursor: pointer;
	padding-left: 5px;
	padding-right: 5px;
	background: transparent repeat-y -512px 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/tab-v-hm.png')});
	zoom: 1;
}
.z-tab-ver .z-tab-ver-hm-close{
	padding-left: 15px;
}
.z-tab-ver .z-tab-ver-hl:hover {
	background-position: -256px 0;
}
.z-tab-ver .z-tab-ver-hl:hover .z-tab-ver-hr {
	background-position: -256px bottom;
}
.z-tab-ver .z-tab-ver-hl:hover .z-tab-ver-hm {
	background-position: -256px 0;
}
.z-tab-ver .z-tab-ver-text {
	font-style: normal;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	text-align:center;
	font-style: normal;
	white-space: nowrap;
	color: #555555;
	cursor: pointer;
	font-size-adjust: none;
}
.z-tabs-ver-space {
	float: left;
	border-left: 0 none;
	position: relative;
}
<c:if test="${zk.ie == 6}">
.z-tabs-ver-space {
	margin-right: -4px;
}
</c:if>
.z-tabbox-ver .z-tabs-ver-header-scroll {
	margin-top: 20px;
	margin-bottom: 20px;
}
.z-tabs-ver-up-scroll,
.z-tabs-ver-down-scroll {
	cursor: pointer;
	height: 20px;
	position: absolute;
	left: 4px;
	z-index: 25;
	width: 100%;
	display: block;
}
.z-tabs-ver-up-scroll,
.z-tabs-ver-up-scroll-hl {
	background: transparent no-repeat center bottom;
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/scroll-u.png')});
	border-right: 1px solid #E1E1E1;
}
.z-tabs-ver-up-scroll {
	top: 0;
}
.z-tabs-ver-down-scroll,
.z-tabs-ver-down-scroll-hl {
	background: transparent no-repeat center bottom;
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/scroll-d.png')});
	border-right: 1px solid #E1E1E1;
}
.z-tabs-ver-down-scroll {
	bottom: 0;
}
.z-tabs-ver-up-scroll-hr,
.z-tabs-ver-down-scroll-hr {
	border-right: 1px solid #E1E1E1;
	width: 1px;
	height: 20px;
	position: absolute;
	right: 4px;
}
.z-tabs-ver-up-scroll:hover {
	background-position: center 0;
}
.z-tabs-ver-down-scroll:hover {
	background-position: center 0;
}
.z-tabs-ver-up-scroll-hl,
.z-tabs-ver-down-scroll-hl {
	background-position: 0 0;
	border: 0;
	height: 20px;
	width: 3px;
	position: absolute;
	margin-left: -3px;
}
.z-tabs-ver-up-scroll-hl{
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/scroll-u-hl.png')});
}
.z-tabs-ver-down-scroll-hl {
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/scroll-d-hl.png')});
}

<%-- Selected --%>
.z-tab-ver-seld .z-tab-ver-close {
	opacity: 1;
	filter: alpha(opacity=100);
}
.z-tab-ver-seld .z-tab-ver-hl {
	background-position: 0 0;
	margin-right: -2px;
	cursor: default;
}
.z-tab-ver-seld .z-tab-ver-hl .z-tab-ver-hr,
.z-tab-ver-seld .z-tab-ver-hl:hover .z-tab-ver-hr {
	background-position: 0 bottom;
}
.z-tab-ver-seld .z-tab-ver-hm {
	background-position: 0 0;
	cursor: default;
}
.z-tab-ver-seld .z-tab-ver-text {
	color: #555555;
	cursor: default;
	font-weight: bold;
	white-space: nowrap;
}
.z-tab-ver-seld .z-tab-ver-hl:hover,
.z-tab-ver-seld .z-tab-ver-hl:hover .z-tab-ver-hm,
.z-tab-ver-disd .z-tab-ver-close,
.z-tab-ver-disd-seld .z-tab-ver-close:hover {
	background-position: 0 0;
}
<%-- Disabled --%>
.z-tab-ver-disd .z-tab-ver-hl,
.z-tab-ver-disd-seld .z-tab-ver-hl {
	color: gray;
	cursor: default;
	opacity: .6;
	filter: alpha(opacity=60);
}
.z-tab-ver-disd .z-tab-ver-hl, .z-tab-ver-disd-seld .z-tab-ver-hl,
.z-tab-ver-disd .z-tab-ver-hl:hover, .z-tab-ver-disd-seld .z-tab-ver-hl:hover {
	background-position: -512px 0;
}
.z-tab-ver-disd .z-tab-ver-hl .z-tab-ver-hr,
.z-tab-ver-disd-seld .z-tab-ver-hl .z-tab-ver-hr,
.z-tab-ver-disd .z-tab-ver-hl:hover .z-tab-ver-hr,
.z-tab-ver-disd-seld .z-tab-ver-hl:hover .z-tab-ver-hr {
	background-position: -512px bottom;
}
.z-tab-ver-disd .z-tab-ver-hl .z-tab-ver-hm,
.z-tab-ver-disd-seld .z-tab-ver-hl .z-tab-ver-hm,
.z-tab-ver-disd .z-tab-ver-hl:hover .z-tab-ver-hm,
.z-tab-ver-disd-seld .z-tab-ver-hl:hover .z-tab-ver-hm {
	background-position: -512px 0;
	color: gray;
	cursor: default;
}
.z-tab-ver-disd .z-tab-ver-text,
.z-tab-ver-disd-seld .z-tab-ver-text {
	cursor: default;
	color: gray;
}
<%-- Tabpanels Tabpanel --%>
.z-tabpanel,
.z-tabbox-ver .z-tabpanels-ver {
	border-color: #E1E1E1;
	border-width: 1px;
	border-style: solid;
}
.z-tabbox-ver .z-tabpanels-ver .z-tabpanels-ver-inner {
	border-color: #E1E1E1;
}
.z-tabbox-ver .z-tabpanels-ver {
	border: 1px solid #E1E1E1;
	border-width: 1px 1px 1px 0;
}
.z-tabpanel,
.z-tabbox-ver .z-tabpanel-ver {
	padding: 5px;
	zoom: 1;
}
.z-tabpanels-ver {
	float: left;
	zoom: 1; 
	overflow: hidden; 
	position: relative;
}

.z-tabpanel {
	border-top: none;
}
.z-tabpanel-cnt { <%-- Bug 2104974 --%>
	height: 100%;
}
<%-- Tabbox accordion --%>
.z-tabpanels-accordion{
	border-top: 1px solid #CFCFCF;
}
.z-tab-accordion-header {
	cursor: pointer;
	position: relative;
	zoom: 1;
	border: 1px solid #CFCFCF;
	border-top: 0;
}
.z-tab-accordion-text {
	cursor: pointer;
	color: #555555;
	font-family: ${fontFamilyT};
	font-weight: bold;
	font-size: ${fontSizeM};
	text-decoration: none;
	padding-left: 5px;
	padding-right: 30px;
	padding-bottom: 2px;
}
.z-tabbox-accordion .z-tabpanel-accordion {
	border: 1px solid #CFCFCF;
	border-top: 0;
	padding: 5px;
	zoom: 1;
}
.z-tab-accordion-tl,
.z-tab-accordion-tr {
	background: transparent no-repeat 0 0;
	zoom: 1;
	height: 0;
	margin-right: 5px;
	font-size: 0;
	line-height: 0;
}
.z-tab-accordion-tr {
	position: relative;
	margin-right: -5px;
}
.z-tab-accordion-hl {
	background: transparent no-repeat 0 0;
	padding-left: 0;
	zoom: 1;
}
.z-tab-accordion-hr {
	background: transparent no-repeat 0 0;
	padding-right: 0;
	zoom: 1;
}
.z-tab-accordion-hm {
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/accd-hm.png')});
	overflow: hidden;
	padding: 5px 0 3px;
	zoom: 1;
}
.z-tab-accordion-hm:hover {
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/accd-hm-mouseover.png')});
}
.z-tab-accordion-close,
.z-tab-accordion-lite-close {
	background-color: transparent;
	background-repeat: no-repeat;
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/tab-close.gif')});
	cursor: pointer;
	width: 15px;
	height: 15px;
	position: absolute;
	right: 5px;
	top: 5px;
	z-index: 15;
	opacity: .6;
	filter: alpha(opacity=60);
}
.z-tab-accordion-lite-close {
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/tab-close.gif')});
	right: 4px;
	top: 4px;
}
.z-tab-accordion-close-over,
.z-tab-accordion .z-tab-accordion-close:hover {
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/tab-close.gif')});
	background-position: right 0;
	opacity: 1.0;
	filter: alpha(opacity=100);
}
<%-- Selected --%>
.z-tab-accordion-seld .z-tab-accordion-close,
.z-tab-accordion-lite-seld .z-tab-accordion-lite-close {
	opacity: .8;
	filter: alpha(opacity=80);
}
<%-- Disabled --%>
.z-tab-accordion-disd .z-tab-accordion-header,
.z-tab-accordion-disd-seld .z-tab-accordion-header {
	color: gray;
	cursor: default;
	opacity: .6;
	filter: alpha(opacity=60);
}
.z-tab-accordion-disd .z-tab-accordion-close:hover,
.z-tab-accordion-disd-seld .z-tab-accordion-close:hover,
.z-tab-accordion-lite-disd .z-tab-accordion-lite-close:hover,
.z-tab-accordion-lite-disd-seld .z-tab-accordion-lite-close:hover {
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/tab-close.gif')});
	background-position: right 0;
	opacity: .6;
	filter: alpha(opacity=60);
}
.z-tab-accordion-disd .z-tab-accordion-text,
.z-tab-accordion-disd-seld .z-tab-accordion-text {
	cursor: default;
	color: gray;
}
<%-- ZK Tabbox accordion-lite --%>
.z-tabpanels-accordion-lite {
	border: 0;
	border-top: 1px solid #CFCFCF;
}
.z-tab-accordion-lite-header {
	overflow: hidden;
	zoom: 1;
	cursor: pointer;
	position: relative;
	border: 1px solid #CFCFCF;
	border-top: 0;
}
.z-tab-accordion-lite-text {
	cursor: pointer;
	color: #555555;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	line-height: 15px;
	text-decoration: none;
	padding-right: 30px;
	padding-left: 5px;
}
.z-tabbox-accordion-lite .z-tabpanel-accordion-lite {
	border: 1px solid #CFCFCF;
	border-top: 0;
	padding: 5px;
	zoom: 1;
}
.z-tab-accordion-lite-tl,
.z-tab-accordion-lite-tr,
.z-tab-accordion-lite-tm {
	background: transparent no-repeat 0 0;
	background-color: #F1F1F1;
	display: block;
}
.z-tab-accordion-lite-tm:hover {
	background-color: #E6F5FC;
}
.z-tab-accordion-lite-tl {
	zoom: 1;
	padding-left: 0;
	line-height: 0;
	text-decoration: none;
}
.z-tab-accordion-lite-tr {
	padding-right: 0;
}
.z-tab-accordion-lite-tm {
	padding:4px 0 3px 0;
	overflow: hidden;
}
.z-tab-accordion-lite-close-over,
.z-tab-accordion-lite .z-tab-accordion-lite-close:hover {
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/tab-close.gif')});
	background-position: right 0;
	opacity: 1;
	filter: alpha(opacity=100);
}

<%-- Disabled --%>
.z-tab-accordion-lite-disd a,
.z-tab-accordion-lite-disd-seld a {
	color: gray;
	cursor: default;
	opacity: .6;
	filter: alpha(opacity=60);
}
.z-tab-accordion-lite-disd .z-tab-accordion-lite-text,
.z-tab-accordion-lite-disd-seld .z-tab-accordion-lite-text {
	cursor: default;
	color: gray;
}
.z-tab-accordion-spacing {
	border-bottom: 1px solid #CFCFCF;
}
<%-- IE 6 Image and Fix--%>
<c:if test="${zk.ie == 6}">
.z-tab-hl, .z-tab-hr {
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/tab-corner.gif')});
}
.z-tab-hm {
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/tab-hm.gif')});
}
.z-tab-ver-hl, .z-tab-ver-hl .z-tab-ver-hr {
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/tab-v-corner.gif')});
}
.z-tab-ver .z-tab-ver-hm {
	background-image: url(${c:encodeThemeURL('~./zul/img/tab/tab-v-hm.gif')});
}
.z-toolbar-tabs {
	height: 20px;
}
.z-tabs-scroll {
	height: 25px;
}
.z-tabs-left-scroll,
.z-tabs-right-scroll {
	height: 100%;
}
.z-tabs-header {
	height: 25px;
}
.z-tabbox-ver .z-tabpanels-ver {
	zoom: 1;
}
</c:if>

.z-tab-accordion-lite-tm .z-caption-l,
.z-tab-accordion-hm .z-caption-l,
.z-tab-ver-hm .z-caption-l {
	padding-left: 5px;
}