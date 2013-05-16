<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/theme" prefix="t" %>

.z-button {
	font-family: ${fontFamilyT};
	font-size: ${fontSizeM};
	font-weight: normal;
	padding: 3px 10px;
	margin: 1px 1px 0 0;
	line-height: 16px;
	border: 1px solid #A6A6A6;
	${t:borderRadius('4px')};
	${t:gradient('ver', '#FEFEFE 0%; #EEEEEE 100%')};
	text-shadow: 0 1px #FFFFFF;
	color: black;
}
.z-button:hover {
	border-color: #8FB9D0;
	${t:gradient('ver', '#E3F4FD 0%; #C7E9F9 100%')};
}
.z-button:focus {
	border-color: #00B9FF;
	<%--${Button_Focus_Background};--%>
	${t:boxShadow('inset 1px 1px 1px #0CBCFF, inset -1px -1px 1px #0CBCFF')};
}
.z-button:active {
	border-color: #499EB3;
	${t:gradient('ver', '#C3F5FE 0%; #86E2F9 100%')};
}
.z-button:disabled,
.z-button:disabled:hover,
.z-button:disabled:focus,
.z-button:disabled:active {
	color: gray;
	opacity: .6;
	border: 1px solid #A6A6A6;
	${t:gradient('ver', '#FEFEFE 0%; #EEEEEE 100%')};
	${t:boxShadow('none')};
}
