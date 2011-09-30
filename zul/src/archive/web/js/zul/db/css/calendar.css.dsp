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
	color: #AAA !important;
	
	color: rgba(170,170,170,0.6) !important;
}

/*.z-calendar-calyear*/ 
.z-datebox-rounded-calyear,
.z-datebox-calyear {
	background: #e9f1f3;
	border: 1px solid;
	border-color: #f8fbff #aca899 #aca899 #f8fbff;
}
.z-datebox-rounded-calday,
.z-datebox-calday {
	border: 1px solid #ddd;
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
	font-weight:bold;
	font-family:arial;
	font-size: 12px;
	color: #595959;
}
.z-calendar-caldayrow .z-calendar-over,
.z-calendar-title-over .z-calendar-ctrler {
	color: #4D9CB5;
}

.z-calendar-calyear td,
.z-calendar-calmon td {
	padding: 12px 3px;
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
	color: #5C5C5C;	
}

.z-calendar-calyear td,
.z-calendar-calmon td {
	font-size: ${fontSizeM};
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
	text-shadow: 1px 1px 3px #008bb6;
}

.z-calendar-calyear td.z-calendar-seld,
.z-calendar-calmon td.z-calendar-seld,
.z-calendar-calday td.z-calendar-seld {
	background: #D9EFFA; 
	border-radius: 4px;
	-moz-border-radius: 4px;
	-webkit-border-radius: 4px;
	border-color: #fcd4a9;
	text-shadow: 1px 1px 3px #888;
}
.z-datebox-rounded-calmon td.z-datebox-rounded-over-seld,
.z-datebox-rounded-calday td.z-datebox-rounded-over-seld,
.z-calendar td.z-calendar-over-seld,
.z-datebox-calmon td.z-datebox-over-seld,
.z-datebox-calday td.z-datebox-over-seld {
	background: #C3E5F4;
	border-radius: 4px;
	-moz-border-radius: 4px;
	-webkit-border-radius: 4px;
	text-shadow: 1px 1px 3px #008bb6;
}
.z-calendar td.z-calendar-over-seld {
	color: #008BB6;
}
.z-calendar-caldow td {
	text-align: center;
}
.z-datebox-rounded-caldow td,
.z-datebox-caldow td {
	font-size: ${fontSizeS}; color: #333; font-weight: bold;
	padding: 1px 2px; background: #e8e8f0; text-align: center;
}
.z-calendar .z-weekend,
.z-calendar .z-weekday {
	font-family: arial;
	font-weight:bold;
	font-size:11px;
}
.z-calendar .z-weekend {
	color: #f7941d;
}
.z-calendar .z-weekday {
	color: #636363;
}
.z-calendar .z-outside {
	color: #AAA;
	text-shadow: none;
}

<%-- Calendar weekend --%>
td.z-calendar-wkend {
	background: #F3F3F3;
}

<%-- Issues --%>
<%-- 3159648 --%>
<c:if test="${c:isGecko3()}">
.z-calendar-disd,
.z-calendar-disd * {
	opacity: 1;	
	color: rgba(170,170,170,0.6) !important;
}
</c:if>
<%-- 1735084 --%>
<c:if test="${c:isExplorer()}">
.z-datebox-rounded-calyear td,
.z-calendar-calyear td, .z-datebox-calyear td {
	color: #404040; 
}
</c:if>
