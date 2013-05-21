<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/theme" prefix="t" %>

<c:if test="${empty fontSizeM}">
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontSizeM')}"/>
<c:set var="fontSizeM" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="fontSizeM" value="12px" scope="request" if="${empty fontSizeM}"/>
</c:if>
<c:if test="${empty fontSizeMS}">
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontSizeMS')}"/>
<c:set var="fontSizeMS" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="fontSizeMS" value="11px" scope="request" if="${empty fontSizeMS}"/>
</c:if>
<c:if test="${empty fontSizeS}">
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontSizeS')}"/>
<c:set var="fontSizeS" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="fontSizeS" value="11px" scope="request" if="${empty fontSizeS}"/>
</c:if>
<c:if test="${empty fontSizeXS}">
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontSizeXS')}"/>
<c:set var="fontSizeXS" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="fontSizeXS" value="10px" scope="request" if="${empty fontSizeXS}"/>
</c:if>

<c:if test="${empty fontFamilyT}"><%-- title --%>
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontFamilyT')}"/>
<c:set var="fontFamilyT" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="fontFamilyT" value="arial, sans-serif"
	scope="request" if="${empty fontFamilyT}"/>
</c:if>
<c:if test="${empty fontFamilyC}"><%-- content --%>
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontFamilyC')}"/>
<c:set var="fontFamilyC" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="fontFamilyC" value="arial, sans-serif"
	scope="request" if="${empty fontFamilyC}"/>
</c:if>

html, body {
	height: 100%;
}

*, *:after, *:before {
	${t:applyCSS3('box-sizing', 'border-box')};
}

input[type=number]::-webkit-inner-spin-button,
input[type=number]::-webkit-outer-spin-button {
    -webkit-appearance: none;
    margin: 0;
}
input,
input:focus,
textarea,
textarea:focus {
	-webkit-appearance: none;
	-moz-appearance: none;
	 outline: none;
	 -webkit-user-select: text;
}

input[type="radio"] {
	-webkit-appearance: radio;
	-moz-appearance: radio;
}

input[type="checkbox"] {
	-webkit-appearance: checkbox;
	-moz-appearance: checkbox;
}

*:focus {
  outline: none;
}

<c:if test="${zk.gecko > 0}">
button::-moz-focus-inner {
	border: 0;
}
</c:if>
<c:if test="${empty c:property('org.zkoss.zul.theme.browserDefault')}">
body {
	margin: 0; padding: 0 5px;
}
</c:if>

<%-- paragraphs --%>
img {
	border: 0;
}

<%-- mobile --%>
.mobile * {
	-webkit-tap-highlight-color: rgba(0,0,0,0);
}
<%-- ZK JavaScript debug box --%>
div.z-error {
	display: none;
	z-index: 9999999;
	position: absolute;
	top: 0;
	left: 40%;
	height: 90px;
	width: 450px;
	padding: 3px 5px 3px 3px;
	border-width: 1px;
	border-style: solid;
	border-color: #990000;
	box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.35);
	-moz-box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.35);
	-webkit-box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.35);
	background-color: #FFEEEE;
}
div.z-error .msgcnt {
	padding: 0;
	border: 1px solid #990000;
	background-color: white;
	height: 60px;
}
div.z-error .msgs {
	padding: 2px 3px;
	height: 60px;
	width: 440px;
	word-wrap: break-word;
	overflow: auto;
}
div.z-error .msgs .msg {
	padding: 3px 0 2px;
	border-bottom: 1px solid #990000;
}
div.z-error .newmsg {
	background-color: #FFEEEE;
	display: none;
}

div.z-error .btn {
	cursor: pointer;	
	color: #363636; 
	width: 16px;
	height: 16px;
	display: inline-block;
	margin-left: 10px;
}
div.z-error #zk_err-p {
	cursor: move;
}
div.z-error .errnum {
	padding-left: 20px;
	color: #990000;
	font-weight: bold;	
}
<c:if test="${zk.ie != 6}">
<c:if test="${zk.ie == 7}">
div.z-error .btn {
	display: inline;
}
</c:if>
div.z-error .errnum {
	background: url(${c:encodeURL('~./zk/img/error.png')}) no-repeat scroll -31px 4px transparent;
}
div.z-error .redraw {
	background: url(${c:encodeURL('~./zk/img/error.png')}) no-repeat scroll 0 0 transparent;
}
div.z-error .close {
	background: url(${c:encodeURL('~./zk/img/error.png')}) no-repeat scroll -17px 0 transparent;
}
</c:if>
<%-- IE6 --%>
<c:if test="${zk.ie == 6}">
div.z-error .btn {
	display: inline;
	zoom: 1;
}
div.z-error .errnum {
	background: url(${c:encodeURL('~./zk/img/error.gif')}) no-repeat scroll -31px 4px transparent;
}
div.z-error .redraw {
	background: url(${c:encodeURL('~./zk/img/error.gif')}) no-repeat scroll 0 0 transparent;
}
div.z-error .close {
	background: url(${c:encodeURL('~./zk/img/error.gif')}) no-repeat scroll -17px 0 transparent;
}
</c:if>
div.z-log {
	text-align:right; width:50%; right:10px; bottom:5px;
	position:absolute; z-index: 99000;
}
div.z-log textarea {
	width: 100%;
}
div.z-log button {
	font-size: ${fontSizeXS};
}
<%-- General --%>
.noscript {<%-- the content of noscript --%>
	width: 100%;
	height: 100%;
	position: absolute;
	z-index: 32000;
	top: 0;
	left: 0;
	filter: alpha(opacity=60);
	opacity: .6;
	zoom: 1;
	text-align: center;
	background: #E0E1E3;
}
.noscript p {
	background: white;
	font-weight: bold;
	color: black;
	margin: 10% 15%;
	padding: 10px 0;
	border: 1px solid black;
	filter: alpha(opacity=100);
	opacity: 1;
}

.z-label, .z-radio-cnt, .z-checkbox-cnt, .z-loading {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; font-weight: normal;
}
.z-temp, .z-modal-mask {
	width: 100%;
	height: 100%;
	position: absolute;
	top: 0; left: 0;
	filter: alpha(opacity=60); <%-- IE --%>
	opacity: .6;
	zoom: 1;
	background: #E0E1E3;
}
.z-initing {
	width: 60px; height: 60px;
	position: absolute; right: 10px; bottom: 10px;
	z-index: 32000;
	background: transparent no-repeat center;
	background-image: url('http://www.zkoss.org/zk/img/${z:encodeWithZK("zkpowered.png")}');
}
.z-uptime {
	background-image: url('http://www.zkoss.org/zk/img/${z:encodeWithZK("zkuptime.png")}');
}
.z-loading, .z-apply-loading {
	position: absolute;
	cursor: wait;
	background-color: #E3E3E3;
	border: 1px solid #C5C5C5;
	white-space: nowrap;
	padding: 3px;
}
.z-loading {
	z-index: 31000;
	left: 0; top: 0;
}
.z-apply-loading {
	z-index: 89500;
	overflow: hidden;
}
.z-loading-indicator, .z-apply-loading-indicator {
	border: 1px solid #C5C5C5;
	background-color: #FFFFFF;
	color: #363636;
	white-space: nowrap;
}
.z-loading-indicator {
	padding: 6px;
}
.z-apply-loading-indicator {
	padding: 2px;
	font: normal ${fontSizeM} ${fontFamilyT};
}
.z-apply-loading-icon,
.z-loading-icon,
.z-renderdefer {
	height: 16px; width: 16px;
	background: transparent no-repeat center;
	background-image: url(${c:encodeThemeURL('~./zul/img/misc/progress.gif')});
}
.z-apply-loading-icon,
.z-loading-icon {
	display:-moz-inline-box; vertical-align:top; display:inline-block;
}
.z-apply-mask {
	width: 100%;
	height: 100%;
	background: #E0E1E3;
	position: absolute;
	z-index: 89000;
	top: 0;
	left: 0;
	filter: alpha(opacity=60);
	opacity: .6;
	zoom: 1;
}
.z-inline-block { <%-- used with label/checkbox and others to ensure the dimension --%>
	display:-moz-inline-box; vertical-align:top;<%-- vertical-align: make it looks same in diff browsers --%>
	display:inline-block;
}
.z-word-wrap {
	word-wrap: break-word;
}
.z-word-nowrap {
	white-space: nowrap;
}
<c:if test="${zk.ie < 8}">
.z-word-nowrap  .z-row-inner,
.z-word-nowrap  .z-cell,
.z-word-nowrap  .z-listcell {
	white-space: nowrap;
}
</c:if>
.z-overflow-hidden {
	overflow: hidden;
}
.z-dd-stackup {
	height: 100%;
	width: 100%;
	position: absolute;
	left: 0;
	top: 0;
	z-index: 16800;
	background-image: url(${c:encodeURL('~./img/spacer.gif')});
}

.z-temp * { <%-- temporary --%>
	color: white; background-color: white; font-size: 5px; text-decoration: none;
}
.z-temp .z-loading {
	background-color: #E3E3E3;
	top: 49%;
	left: 46%;
}
.z-temp .z-loading-indicator {
	font-size: ${fontSizeM};
	color: #363636;
}
<%-- Fix float issue for CSS --%>
.z-clear {
	clear: both;
	height: 0;
	width: 0;
	line-height: 0;
	font-size: 0;
	overflow: hidden;
}

<%-- Shadow --%>
.z-shadow {
	position: absolute;
	display: none;
	left: 0;
	top: 0;
	overflow: hidden;
}
.z-shadow-wrapper{
	padding-bottom:6px;
	height:100%;
}
.z-shadow .z-shadow-cl{
	background: transparent repeat-y 0 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/shadow-cl.png')});
	padding-left: 6px;
	overflow: hidden;
	height:100%;
	zoom: 1;
}
.z-shadow .z-shadow-cr{
	background: transparent repeat-y right;
	background-image: url(${c:encodeThemeURL('~./zul/img/shadow-cr.png')});
	padding-right: 6px;
	overflow: hidden;
	height:100%;
	zoom: 1;
}
.z-shadow .z-shadow-cm {
	background: transparent repeat 0 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/shadow-m.png')});
	overflow: hidden;
	height:100%;
	zoom: 1;
}

.z-shadow .z-shadow-tl,
.z-shadow .z-shadow-tr,
.z-shadow .z-shadow-bl,
.z-shadow .z-shadow-br {
	background:transparent no-repeat 0 top;
	font-size:0;
	height:6px;
	line-height:0;
	margin-right:6px;
	zoom:1;
}
.z-shadow .z-shadow-tr,
.z-shadow .z-shadow-br {
	background-position: right -6px;
	margin-right:-6px;
	position:relative;
}
.z-shadow .z-shadow-tl,
.z-shadow .z-shadow-tr{
	background-image: url(${c:encodeThemeURL('~./zul/img/shadow-tlr.png')});
}

.z-shadow .z-shadow-bl,
.z-shadow .z-shadow-br{
	background-image: url(${c:encodeThemeURL('~./zul/img/shadow-blr.png')});
}

<%-- Drag-Drop --%>
.z-dragged {
	background: none no-repeat scroll 0 0 #E0EAF0;
	color: #888888;
}
.z-drag-over {
	background: none no-repeat scroll 0 0 #ADD2FF;
}
span.z-drop-allow, span.z-drop-disallow {
	background: none no-repeat scroll 0 0 #FFFFFF;
	vertical-align: top;
	display: -moz-inline-box;
	display: inline-block;
	width: 16px;
	min-height: 16px;
	height: 16px;
}
div.z-drop-ghost {
	border: 1px solid #CCCCCC;
}
div.z-drop-cnt {
	background: none no-repeat scroll 0 0 #FFFFFF;
	width: 120px;
	height: 16px;
	padding: 3px;
	font-size: ${fontSizeM};
	font-weight: normal;
	font-family: ${fontFamilyC};
}
<%-- 5.0.4 --%>
div.z-drop-allow {
	border: 1px solid #99CC99;
}
div.z-drop-disallow {
	border: 1px solid #CC6666;
}
div.z-drop-allow div.z-drop-cnt,
span.z-drop-allow {
	background: none no-repeat scroll 0 0 #EEFFEE;
}
span.z-drop-allow {
	background-image: url(${c:encodeThemeURL('~./zul/img/misc/drag-allow.png')});
}
div.z-drop-disallow div.z-drop-cnt,
span.z-drop-disallow {
	background: none no-repeat scroll 0 0 #FFEEEE;
}
span.z-drop-disallow {
	background-image: url(${c:encodeThemeURL('~./zul/img/misc/drag-disallow.png')});
}

<%-- customized by users
.z-drag-ghost {
	border: 1px dotted #999;
}
--%>
.z-drag-ghost {
	list-style:none;
}

<%-- Focus Anchor --%>
.z-focus-a {
	position: absolute;
	left: 0; top: 0;
	padding: 0 !important;
	margin: 0 !important;
	border: 0 !important;
	background: transparent !important;
	font-size: 0 !important;
	line-height: 0 !important;
	width:1px !important;
	height:1px !important;
	-moz-outline:0 none; outline:0 none;
	-moz-user-select:text; -khtml-user-select:text;
	overflow:hidden;
}

<%-- upload button --%>
span.z-upload {
	position: relative;
	padding: 0; margin: 0;
	font-size: 0; width: 0; height: 0;
	display:-moz-inline-box; display:inline-block;
}
span.z-upload input {
	position: absolute;
	cursor: pointer;
	font-size: 45pt;
	z-index: 1; margin: 0; padding: 0;
	opacity: 0;
	filter: alpha(opacity=0); <%-- IE --%>
}
.z-upload-icon {
	overflow: hidden;
	background-image: url(${c:encodeThemeURL('~./zul/img/misc/prgmeter.png')});
}

<%-- fileupload dialog --%>
.z-fileupload-img {
	width: 16px;
	padding-top: 4px;
}
.z-fileupload-add {
	cursor: pointer;
	background: transparent no-repeat 0 -23px;
	background-image: url(${c:encodeThemeURL('~./zul/img/misc/fileupload.gif')});
	width: 16px;
	height: 17px;
}
.z-fileupload-rm {
	cursor: pointer;
	background: transparent no-repeat 0 0;
	background-image: url(${c:encodeThemeURL('~./zul/img/misc/fileupload.gif')});
	width: 16px;
	height: 17px;
}
.z-fileupload-progress {
	width: 300px;
}
.z-fileupload-manager {
	width: 350px;
}

<%-- IE --%>
<c:if test="${zk.ie > 0}">
img	{
	hspace: 0; vspace: 0;
}
option {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeXS}; font-weight: normal;
}

<%-- IE6 --%>
<c:if test="${zk.ie == 6}">
.z-shadow {
	background: #888; zoom: 1; display: none;
	filter: progid:DXImageTransform.Microsoft.Blur(PixelRadius=4, MakeShadow=true, ShadowOpacity=0.30);
}
span.z-drop-allow {
	background-image: url(${c:encodeThemeURL('~./zul/img/misc/drag-allow.gif')});
}
span.z-drop-disallow {
	background-image: url(${c:encodeThemeURL('~./zul/img/misc/drag-disallow.gif')});
}
</c:if><%-- IE6 --%>
</c:if><%--IE --%>

<%-- Opera --%>
<c:if test="${zk.opera > 0}">
option {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeXS}; font-weight: normal;
}
</c:if>
