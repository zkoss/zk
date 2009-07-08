<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

.z-fileupload-img {
	width: 16px;
	padding-top: 4px;
}
.z-fileupload-add {
	cursor: pointer;
	background: transparent no-repeat 0 -23px;
	background-image: url(${c:encodeURL('~./zul/img/misc/fileupload.gif')});
	width: 16px;
	height: 17px;
}
.z-fileupload-delete {
	cursor: pointer;
	background: transparent no-repeat 0 0;
	background-image: url(${c:encodeURL('~./zul/img/misc/fileupload.gif')});
	width: 16px;
	height: 17px;
}

.z-progressContainer {
	margin: 5px;
	padding: 4px;
	border: solid 1px #E8E8E8;
}
.z-progressContainer td{
	padding:2px;
}
.z-progressName {
	font-size: 12px;
	font-weight: 700;
	color: #555;
	height: 14px;
	text-align: left;
	white-space: nowrap;
}

.z-progressBarInProgress,
.z-progressBarComplete,
.z-progressBarError {
	font-size: 0;
	width: 0%;
	height: 100%;
	background-color: #27F;
	display: inline-block;
}

.z-progressBarError {
	width: 100%;
	background-color: red;
	visibility: hidden;
}

.z-progressBarStatus {
	margin-top: 2px;
	font-size: 12px;
	font-family: Arial;
	text-align: left;
	white-space: nowrap;
	flow:right;
}

a.z-progressCancel {
	font-size: 0;
	display: block;
	height: 14px;
	width: 14px;
	background-image: url(${c:encodeURL('~./zul/img/fileupload/cancelbutton.gif')});
	background-repeat: no-repeat;
	background-position: -14px 0px;
	float: right;
}
.z-progressBarInProgressWrapper{
	width:100px;
	height:12px;
	background-color:#aaa;
	border:1px solid #ccc;
	display:inline-block;
}

a.z-progressCancel:hover {
	background-position: 0px 0px;
}

.z-jsupload-fileWrapper
{
    width: 61px;
    height: 22px;
    background: url(${c:encodeURL('~./zul/img/fileupload/swfuploadbtn.png')}) 0 0 no-repeat;
    display: block;
    overflow: hidden;
    cursor: hand;
}

.z-jsupload input
{
    position: relative;
    height: 100%;
    width: 100%;
    opacity: 0;
    -moz-opacity: 0;
    filter:progid:DXImageTransform.Microsoft.Alpha(opacity=0);
}