<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontSizeM')}"/>
<c:set var="fontSizeM" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontSizeMS')}"/>
<c:set var="fontSizeMS" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontSizeS')}"/>
<c:set var="fontSizeS" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontSizeXS')}"/>
<c:set var="fontSizeXS" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontFamilyT')}"/>
<c:set var="fontFamilyT" value="${val}" scope="request" unless="${empty val}"/>
<c:set var="val" value="${c:property('org.zkoss.zul.theme.fontFamilyC')}"/>
<c:set var="fontFamilyC" value="${val}" scope="request" unless="${empty val}"/>

<c:set var="fontSizeM" value="12px" scope="request" if="${empty fontSizeM}"/>
<c:set var="fontSizeMS" value="11px" scope="request" if="${empty fontSizeMS}"/>
<c:set var="fontSizeS" value="11px" scope="request" if="${empty fontSizeS}"/>
<c:set var="fontSizeXS" value="10px" scope="request" if="${empty fontSizeXS}"/>

<c:set var="fontFamilyT" value="Verdana, Tahoma, Arial, Helvetica, sans-serif"
	scope="request" if="${empty fontFamilyT}"/><%-- title --%>
<c:set var="fontFamilyC" value="Verdana, Tahoma, Arial, serif"
	scope="request" if="${empty fontFamilyC}"/><%-- content --%>
p,span {
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
}
h1 {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: x-large; font-weight: bold; color: #250070;
	letter-spacing: -1px; margin-top: 3pt;
}
h2 {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: large; font-weight: bold; color: #200066;
}
h3 {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: medium; font-weight: bold; color: #100050;
}
h4 {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: small; font-weight: bold; color: #346B93;
}
h5 {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: small; font-weight: bold; color: #4BA7D2;
}
h6 {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: small; font-weight: normal; color: #404040;
}

h1 em {color: #dd0000}

dt {
	margin: 0.5em 0 0.3em 0;
	font-weight: bold;
}
dd {
	margin: 0 0 0 0.8em;
}

li, dt, dd, pre, body {
	font-family: Tahoma, Arial, serif;
	font-weight: normal;
	font-size: ${fontSizeM};
}

li	{margin-top: 2pt}
ul li	{list-style: url(${c:encodeURL('/img/z-bullet1.gif')}) disc}
ul ul li	{list-style: url(${c:encodeURL('/img/z-bullet2.gif')}) circle}
ul ul ul li	{list-style: url(${c:encodeURL('/img/z-bullet3.gif')}) square}

code {
	font-family: "Lucida Console", "Courier New", Courier, mono;
	font-weight: normal;
}
dfn {
	font-family: "Lucida Console", "Courier New", Courier, mono;
	font-style: normal;
}

<%-- The hyperlink's style class. --%>
.link {cursor: pointer;}

body {
	padding: 0 !important;
}
h4 {
	margin: 0;
	padding: 10px 0;
}
P {
	margin: 0;
	padding: 5px 0;
}
ul {
	margin-top: 5px;
	margin-bottom: 5px;
}
.demo-header .z-north-body {
	background:transparent url(${c:encodeURL('/img/category-bg.png')}) repeat-x scroll 0 0;
}
.demo-categorybar {
	position: relative;
	overflow: hidden;
}
.demo-categorybar-body {
	margin: 0px;
	width: 100%;
	overflow: hidden;
	zoom: 1;
}
.demo-categorybar-body-scroll {
	position: relative;
	margin-left: 20px;
	margin-right: 20px;
}
.demo-categorybar-right-scroll {
	background-color: transparent;
	background-image: url(${c:encodeURL('~./zul/img/tab2/scroll-right.png')});
	background-repeat: no-repeat;
	background-attachment: scroll;
	background-position: 0 0;
	border-bottom:1px solid #8DB2E3;
	cursor:pointer;
	position:absolute;
	right:0;
	top:37px;
	width:18px;
	z-index:10;
	height:25px;
}
.demo-categorybar-right-scroll:hover {
	background-position:-18px 0;
}
.demo-categorybar-left-scroll {
	background-color: transparent;
	background-image: url(${c:encodeURL('~./zul/img/tab2/scroll-left.png')});
	background-repeat: no-repeat;
	background-attachment: scroll;
	background-position: -18px 0;
	border-bottom:1px solid #8DB2E3;
	cursor:pointer;
	left:0;
	position:absolute;
	top:37px;
	width:18px;
	z-index:10;
	height:25px;
}
.demo-categorybar-left-scroll:hover {
	background-position:0px 0;
}
.demo-seld {
	background:transparent url(${c:encodeURL('/img/category-seld.png')}) no-repeat scroll 0 0;
}
.demo-over.demo-seld {
	background:transparent url(${c:encodeURL('/img/category-over-seld.png')}) no-repeat scroll 0 0;
}
.demo-over {
	background:transparent url(${c:encodeURL('/img/category-over.png')}) no-repeat scroll 0 0;
}
.demo-search-inp {
    padding: 2px 0 1px 18px;
	background: white url(${c:encodeURL('/img/search.png')}) no-repeat scroll 0 0;
}
<c:if test="${c:browser('ie6-')}">
.demo-search-inp {
    padding: 2px 0 1px 18px;
	background: white url(${c:encodeURL('/img/search.gif')}) no-repeat scroll 0 0;
}
</c:if>
.demo-category {
	margin-top: 10px; float:left; height: 80px; width: 90px;
}
.demo-category-img {
	padding: 0 7px;
	width: 76px;
	height: 80px;
}
.demo-logo {
	margin: 0;
	float:left;
}
.demo-logo img {
	padding: 15px;
}
.demo-items {
	border: none; background: white;
}
.demo-items .z-list-cell-cnt {
	padding-left: 5px;
}
.demo-main-cnt {
	padding-left: 5px;
}
.demo-main-desc {
	padding-bottom: 5px;
}
.pointer {
	cursor:pointer;
}