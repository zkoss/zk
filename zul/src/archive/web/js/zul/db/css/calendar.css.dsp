<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-calendar {
	background: white;
	border: 1px solid #C5C5C5;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	font-weight: normal;
	width: 215px;
	padding: 2px;
}
.z-calendar,
.z-calendar-title-over {
	border-radius: 3px;
	-moz-border-radius: 3px;
	-webkit-border-radius: 3px;
}
.z-datebox-rounded-pp .z-calendar,
.z-datebox-pp .z-calendar {
	border: 0;
}


<%-- Calendar and Datebox --%>
.z-calendar-tdl,
.z-calendar-tdr {
	position: relative;
	width : 10px;
	padding-bottom: 10px;
	width: 16px;
	cursor: pointer;
}
.z-calendar-left,
.z-calendar-right {
	position: relative;
}
.z-calendar-left-icon,
.z-calendar-right-icon {
	border-top: 6px solid white;
	border-bottom: 6px solid white;
	height: 0;
	width: 0;
	line-height: 0;
	position: absolute;
	cursor: pointer;
	font-size: 0;
}
.z-calendar-left-icon {
	border-right: 6px solid #cccccc;
	border-left: 0;
	right: 0;
}
.z-calendar-right-icon {
	border-left: 6px solid #cccccc;
	border-right: 0;
	left: 0;
}
.z-calendar-over .z-calendar-left-icon {
	border-right-color: #c3e5f4;
}
.z-calendar-over .z-calendar-right-icon {
	border-left-color: #c3e5f4;
}
.z-calendar-icon-disd .z-calendar-left-icon {
	border-right: 6px solid #E3E3E3;
	cursor: auto;
}
.z-calendar-icon-disd .z-calendar-right-icon {
	border-left: 6px solid #E3E3E3;
	cursor: auto;
}
.z-calendar-icon-disd {
	cursor: default;
}
.z-calendar-disd {
	opacity: .6;
	filter: alpha(opacity=60);
}

.z-calendar-disd,
.z-calendar-disd * {
	cursor: default !important;
	color: #AAAAAA !important;
	color: rgba(170,170,170,0.6) !important;
}

.z-datebox-timezone-body,
.z-datebox-timezone-item {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	font-weight: normal;
}
.z-calendar-calctrl td {
	font-size: ${fontSizeM}; 
	text-align: center;
	white-space: nowrap;
}
.z-calendar-title,
.z-calendar-calctrl .z-calendar-ctrler {
	cursor: pointer;
	font-weight: bold;
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	color: #636363;
}
.z-calendar-caldayrow .z-calendar-over,
.z-calendar-title-over .z-calendar-ctrler {
	color: #4D9CB5;
}

.z-calendar-calyear td,
.z-calendar-calmon td {
	padding: 12px 3px;
	font-size: ${fontSizeM};
}
.z-calendar-calday {
	table-layout: fixed;
}
.z-calendar-calyear td,
.z-calendar-calmon td,
.z-calendar-caldayrow td {
	font-size: ${fontSizeS}; 
	text-align: center;
	cursor: pointer; 
	text-decoration: none;
	-moz-user-select: none;
	color: #636363;	
}
.z-calendar-calday td {
	padding: 3px 0;
}
.z-calendar-calyear .z-calendar-over,
.z-calendar-calmon .z-calendar-over,
.z-calendar-caldayrow .z-calendar-over {
	background: #E3F2FF;
	border-radius: 4px;
	-moz-border-radius: 4px;
	-webkit-border-radius: 4px;
	text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.35);
}

.z-calendar-calyear td.z-calendar-seld,
.z-calendar-calmon td.z-calendar-seld,
.z-calendar-calday td.z-calendar-seld {
	background: #D9EFFA; 
	border-radius: 4px;
	-moz-border-radius: 4px;
	-webkit-border-radius: 4px;
	text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.35);
}
.z-calendar td.z-calendar-over-seld {
	background: #C3E5F4;
	border-radius: 4px;
	-moz-border-radius: 4px;
	-webkit-border-radius: 4px;
	text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.35);
	color: #008BB6;
}
.z-calendar-caldow td {
	text-align: center;
}
.z-calendar-caldow .z-calendar-wkend,
.z-calendar-caldow .z-calendar-wkday {
	font-family: ${fontFamilyT};
	font-weight: bold;
	font-size: ${fontSizeS};
}
.z-calendar-caldow .z-calendar-wkend {
	color: #f7941d;
}
.z-calendar-caldow .z-calendar-wkday {
	color: #636363;
}
.z-calendar .z-calendar-outside {
	color: #AAAAAA;
	text-shadow: none;
}

<%-- Calendar weekend --%>
td.z-calendar-wkend {
	background: #F3F3F3;
}

<%-- Issues --%>
<%-- 3159648 --%>
<c:if test="${zk.gecko > 0}">
.z-calendar-disd,
.z-calendar-disd * {
	opacity: 1;	
	color: rgba(170,170,170,0.6) !important;
}
</c:if>
<%-- 1735084 --%>
<c:if test="${zk.ie > 0}">
.z-calendar-calyear td {
	color: #363636;
}
</c:if>
