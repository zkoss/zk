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
.z-datebox-pp .z-calendar {
	border: 0 none;
}
.z-calendat-title-over {
	color: #8FADFF;
}

<%-- Calendar and Datebox --%>
.z-calendar-tdl,
.z-calendar-tdr {
	position: relative;
	width : 10px;
	padding-bottom:10px;
	width: 16px;
}
.z-calendar-left, .z-calendar-right {
	position:relative;
}
.z-calendar-left-icon {
	border-right: 6px solid #656565;
	border-top: 6px solid white;
	border-bottom: 6px solid white;
	border-left: 0;
	height: 0;
	width: 0;
	position: absolute;
	cursor: pointer;
	font-size: 0;
	line-height: 0;
	right: 0;
}
.z-calendar-right-icon {
	border-left: 6px solid #656565;
	border-top: 6px solid white;
	border-bottom: 6px solid white;
	border-right: 0;
	height: 0;
	width: 0;
	position: absolute;
	cursor: pointer;
	font-size: 0;
	left: 0;
	line-height: 0;
}
.z-calendar-right-icon-disd {
	border-left: 6px solid #D9DADA;
}
.z-calendar-left-icon-disd {
	border-right: 6px solid #D9DADA;
}

.z-calendar-disd {
	opacity: .6;
	-moz-opacity: .6;
	filter: alpha(opacity=60);
}
.z-calendar-disd, .z-calendar-disd * {
	cursor: default !important;
	color: #AAA !important;
}
/*.z-calendar-calyear*/ 
.z-datebox-calyear {
	background: #e9f1f3; border: 1px solid;
	border-color: #f8fbff #aca899 #aca899 #f8fbff;
}
.z-datebox-calday {
	border: 1px solid #ddd;
}
.z-calendar-calctrl td {
	font-size: ${fontSizeM}; 
	text-align: center;
	white-space: nowrap;
}
.z-calendar-calctrl .z-calendar-ctrler {
	cursor: pointer;
}
.z-calendar-calyear td,
.z-calendar-calmon td {
	padding: 12px 3px;
	text-align: center;
	cursor: pointer;
}
.z-calendar-calday {
	table-layout: fixed;
}
.z-calendar-caldayrow td,
.z-calendar td a,
.z-calendar td a:visited {
	font-size: ${fontSizeS}; 
	color: #35254F; 
	text-align: center;
	cursor: pointer; 
	text-decoration: none;
	-moz-user-select: none;
}

.z-calendar-calyear td a,
.z-calendar-calyear td a:visited,
.z-calendar-calmon td a,
.z-calendar-calmon td a:visited {
	font-size: ${fontSizeM};
}
.z-calendar-calday td {
	padding: 3px 0;
}
.z-calendar-calyear .z-calendar-over,
.z-calendar-calmon .z-calendar-over,
.z-calendar-caldayrow .z-calendar-over {
	background: #F1F9FC;
}

.z-calendar-calyear td.z-calendar-seld,
.z-calendar-calmon td.z-calendar-seld,
.z-calendar-calday td.z-calendar-seld {
	background: #CCE0FB; 
}
.z-calendar td.z-calendar-over-seld,
.z-datebox-calmon td.z-datebox-over-seld,
.z-datebox-calday td.z-datebox-over-seld {
	background: #5FA4FF;
}
.z-calendar td.z-calendar-over-seld a {
	color: white;
}
.z-calendar-caldow td {
	text-align: center;
}
.z-datebox-caldow td {
	font-size: ${fontSizeS}; color: #333; font-weight: bold;
	padding: 1px 2px; background: #e8e8f0; text-align: center;
}

<c:if test="${c:isExplorer()}">
.z-calendar-calyear td, .z-datebox-calyear td {
	color: black; <%-- 1735084 --%>
}
</c:if>
