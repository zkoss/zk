<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-calendar {
	background: white; border: 1px solid #7F9DB9;
	font-family: ${fontFamilyC};font-size: ${fontSizeM}; font-weight: normal;
}

<%-- Calendar and Datebox --%>
.z-calendar-calyear, .z-datebox-calyear {
	background: #e9f1f3; border: 1px solid;
	border-color: #f8fbff #aca899 #aca899 #f8fbff;
}
.z-calendar-calday, .z-datebox-calday {
	border: 1px solid #ddd;
}
.z-calendar-calyear td, .z-datebox-calyear td {
	font-size: ${fontSizeM}; font-weight: bold; text-align: center;
	white-space: nowrap;
}
.z-calendar-calmon td, .z-calendar-calday td, .z-calendar-calday td a, .z-calendar-calday td a:visited,
.z-datebox-calmon td, .z-datebox-calday td, .z-datebox-calday td a, .z-datebox-calday td a:visited {
	font-size: ${fontSizeS}; color: #35254F; text-align: center;
	cursor: pointer; text-decoration: none;
}

.z-calendar-calday td, .z-datebox-calday td {
	padding: 1px 3px;
}
.z-calendar-calday td a:hover, .z-datebox-calday td a:hover {
	text-decoration: underline;
}
.z-calendar-calmon td.z-calendar-seld, .z-calendar-calday td.z-calendar-seld,
.z-datebox-calmon td.z-datebox-seld, .z-datebox-calday td.z-datebox-seld {
	background: #BCD2EF; border: 1px solid #aad6e6;
}
.z-datebox-calmon td.z-datebox-over-seld,.z-datebox-calday td.z-datebox-over-seld{
	background: #6eadff;
}
.z-calendar-caldow td, .z-datebox-caldow td {
	font-size: ${fontSizeS}; color: #333; font-weight: bold;
	padding: 1px 2px; background: #e8e8f0; text-align: center;
}