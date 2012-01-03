<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<%-- Combobox --%>
.z-combobox-rounded,
.z-bandbox-rounded,
.z-datebox-rounded,
.z-combobox,
.z-bandbox,
.z-datebox {
	border: 0; padding: 0; margin: 0; white-space: nowrap;
	font-family: ${fontFamilyC};font-size: ${fontSizeM};
	display: -moz-inline-box;
	display: inline-block;
}
.z-combobox,
.z-bandbox,
.z-datebox,
.z-timebox,
.z-spinner,
.z-doublespinner {
	background-color: #FFFFFF;
}
.z-combobox-rounded-inp,
.z-bandbox-rounded-inp,
.z-datebox-rounded-inp,
.z-timebox-rounded-inp,
.z-spinner-rounded-inp,
.z-doublespinner-rounded-inp,
.z-combobox-inp,
.z-bandbox-inp,
.z-datebox-inp,
.z-timebox-inp,
.z-spinner-inp,
.z-doublespinner-inp {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	font-weight: normal;
	background: #FFFFFF;
	border: 1px solid #CCCCCC;
	border-right: 0;
	padding-top: 2px;
	padding-bottom: 2px;
	padding-left: 4px;
	border-radius: 2px 0 0 2px;
	-moz-border-radius: 2px 0 0 2px;
	-webkit-border-radius: 2px 0 0 2px;
	<c:if test="${zk.safari > 0}">
		margin:0;
	</c:if>
	<c:if test="${zk.opera > 0}">
		font-style: normal;
	</c:if>
	height: 15px;
	outline: none;
}
.z-combobox-rounded-inp,
.z-bandbox-rounded-inp,
.z-datebox-rounded-inp,
.z-timebox-rounded-inp,
.z-spinner-rounded-inp,
.z-doublespinner-rounded-inp {
	border:0;	
	padding: 5px 4px;
	height: 14px;
	<c:if test="${zk.ios > 0}">
		margin-right:-1px;
	</c:if>
}
.z-combobox-rounded-inp {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/combobox-rounded.png', theme)});
}
.z-bandbox-rounded-inp {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/bandbox-rounded.png', theme)});
}
.z-datebox-rounded-inp {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/datebox-rounded.png', theme)});
}
.z-timebox-rounded-inp,
.z-spinner-rounded-inp,
.z-doublespinner-rounded-inp {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/timebox-rounded.png', theme)});	
}
.z-combobox-right-edge,
.z-bandbox-right-edge,
.z-datebox-right-edge,
.z-timebox-right-edge,
.z-spinner-right-edge,
.z-doublespinner-right-edge {
	border-right: 1px solid #CCCCCC;
	-moz-border-radius: 2px 2px 2px 2px;
}
.z-combobox-focus .z-combobox-inp,
.z-bandbox-focus .z-bandbox-inp,
.z-datebox-focus .z-datebox-inp,
.z-timebox-focus .z-timebox-inp,
.z-spinner-focus .z-spinner-inp,
.z-doublespinner-focus .z-doublespinner-inp {
	border: 1px solid #90BCE6;
	border-right: 0;
}
.z-combobox-focus .z-combobox-right-edge,
.z-bandbox-focus .z-bandbox-right-edge,
.z-datebox-focus .z-datebox-right-edge,
.z-timebox-focus .z-timebox-right-edge,
.z-spinner-focus .z-spinner-right-edge,
.z-doublespinner-focus .z-doublespinner-right-edge {
	border-right: 1px solid #90BCE6;
	-moz-border-radius: 2px 2px 2px 2px;
}
.z-combobox .z-combobox-text-invalid,
.z-bandbox .z-bandbox-text-invalid,
.z-datebox .z-datebox-text-invalid,
.z-timebox .z-timebox-text-invalid,
.z-spinner .z-spinner-text-invalid,
.z-doublespinner .z-doublespinner-text-invalid {
	background: #FFFFFF;
	border: 1px solid #DD7870;
	border-right-width: 1px !important;
	margin-right: -1px;
}
.z-combobox input.z-combobox-right-edge,
.z-bandbox input.z-bandbox-right-edge,
.z-datebox input.z-datebox-right-edge,
.z-timebox input.z-timebox-right-edge,
.z-spinner input.z-spinner-right-edge,
.z-doublespinner input.z-doublespinner-right-edge {
	border-right-width: 1px !important;
}
.z-combobox-rounded input.z-combobox-rounded-text-invalid,
.z-bandbox-rounded input.z-bandbox-rounded-text-invalid,
.z-datebox-rounded input.z-datebox-rounded-text-invalid,
.z-timebox-rounded input.z-timebox-rounded-text-invalid,
.z-spinner-rounded input.z-spinner-rounded-text-invalid,
.z-doublespinner-rounded input.z-doublespinner-rounded-text-invalid {
	background: #FFF url(${c:encodeThemeURL('~./${theme}/img/button/redcombo-rounded.gif', theme)}) repeat-x 0 0;
}
.z-combobox-rounded .z-combobox-rounded-text-invalid + i.z-combobox-rounded-btn-right-edge,
.z-bandbox-rounded .z-bandbox-rounded-text-invalid + i.z-bandbox-rounded-btn-right-edge,
.z-datebox-rounded .z-datebox-rounded-text-invalid + i.z-datebox-rounded-btn-right-edge,
.z-timebox-rounded .z-timebox-rounded-text-invalid + i.z-timebox-rounded-btn-right-edge,
.z-spinner-rounded .z-spinner-rounded-text-invalid + i.z-spinner-rounded-btn-right-edge,
.z-doublespinner-rounded .z-doublespinner-rounded-text-invalid + i.z-doublespinner-rounded-btn-right-edge {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/redcombo-rounded.gif', theme)});
	background-position: 0 -24px;
}
i.z-combobox-rounded i.z-combobox-rounded-btn-right-edge-invalid,
i.z-bandbox-rounded i.z-bandbox-rounded-btn-right-edge-invalid,
i.z-datebox-rounded i.z-datebox-rounded-btn-right-edge-invalid,
i.z-timebox-rounded i.z-timebox-rounded-btn-right-edge-invalid,
i.z-spinner-rounded i.z-spinner-rounded-btn-right-edge-invalid,
i.z-doublespinner-rounded i.z-doublespinner-rounded-btn-right-edge-invalid {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/redcombo-rounded.gif', theme)});
	background-position: 0 -24px;
}

.z-combobox-rounded .z-combobox-rounded-btn,
.z-bandbox-rounded .z-bandbox-rounded-btn,
.z-datebox-rounded .z-datebox-rounded-btn,
.z-timebox-rounded .z-timebox-rounded-btn,
.z-spinner-rounded .z-spinner-rounded-btn,
.z-doublespinner-rounded .z-doublespinner-rounded-btn,
.z-combobox .z-combobox-btn,
.z-bandbox .z-bandbox-btn,
.z-datebox .z-datebox-btn,
.z-timebox .z-timebox-btn,
.z-spinner .z-spinner-btn,
.z-doublespinner .z-doublespinner-btn {
	background: transparent no-repeat 0 0;
	background-image: url(${c:encodeThemeURL('~./${theme}/img/input/combobtn.gif', theme)});
	vertical-align: top; 
	cursor: pointer; 
	width: 19px; 
	height: 20px; 
	border: 0;
	border-bottom: 1px solid #CCCCCC;
	overflow: hidden;
	display:-moz-inline-box; display:inline-block;
	border-radius: 0 2px 2px 0;
	-moz-border-radius: 0 2px 2px 0;
	-webkit-border-radius: 0 2px 2px 0;
	<c:if test="${zk.ie < 8}">
		margin-top: 1px;
	</c:if>
}
.z-combobox-rounded .z-combobox-rounded-btn,
.z-bandbox-rounded .z-bandbox-rounded-btn,
.z-datebox-rounded .z-datebox-rounded-btn,
.z-timebox-rounded .z-timebox-rounded-btn,
.z-spinner-rounded .z-spinner-rounded-btn,
.z-doublespinner-rounded .z-doublespinner-rounded-btn {
	border: 0;
	width: 24px; 
	height: 24px;
	background-position: 0 -120px;	
}
.z-combobox-rounded .z-combobox-rounded-btn{	
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/combobox-rounded.png', theme)});
}
.z-bandbox-rounded .z-bandbox-rounded-btn {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/bandbox-rounded.png', theme)});
}
.z-datebox-rounded .z-datebox-rounded-btn {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/datebox-rounded.png', theme)});
}
.z-timebox-rounded .z-timebox-rounded-btn,
.z-spinner-rounded .z-spinner-rounded-btn,
.z-doublespinner-rounded .z-doublespinner-rounded-btn {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/timebox-rounded.png', theme)});
}
.z-combobox-rounded .z-combobox-rounded-btn-right-edge,
.z-bandbox-rounded .z-bandbox-rounded-btn-right-edge,
.z-datebox-rounded .z-datebox-rounded-btn-right-edge,
.z-timebox-rounded .z-timebox-rounded-btn-right-edge,
.z-spinner-rounded .z-spinner-rounded-btn-right-edge,
.z-doublespinner-rounded .z-doublespinner-rounded-btn-right-edge {
	background-position: -19px -120px;
	width: 5px;
	cursor: default;
}
.z-combobox .z-combobox-btn-over,
.z-bandbox .z-bandbox-btn-over,
.z-datebox .z-datebox-btn-over {
	background-position: -19px 0;
	border-bottom: 1px solid #8FB9D0;
	border-left: 1px solid #8FB9D0;
	margin-left: -1px;
}
.z-combobox-rounded-inp-over,
.z-bandbox-rounded-inp-over,
.z-datebox-rounded-inp-over,
.z-timebox-rounded-inp-over,
.z-spinner-rounded-inp-over,
.z-doublespinner-rounded-inp-over {
	background-position: 0 -24px;
}
.z-combobox-rounded .z-combobox-rounded-btn-over,
.z-bandbox-rounded .z-bandbox-rounded-btn-over,
.z-datebox-rounded .z-datebox-rounded-btn-over,
.z-timebox-rounded .z-timebox-rounded-btn-over,
.z-spinner-rounded .z-spinner-rounded-btn-over,
.z-doublespinner-rounded .z-doublespinner-rounded-btn-over  {
	background-position: 0 -144px;
}
.z-combobox-focus .z-combobox-btn-clk, .z-combobox .z-combobox-btn-clk,
.z-bandbox-focus .z-bandbox-btn-clk, .z-bandbox .z-bandbox-btn-clk,
.z-datebox-focus .z-datebox-btn-clk, .z-datebox .z-datebox-btn-clk,
.z-timebox-focus .z-timebox-btn-clk, .z-timebox .z-timebox-btn-clk,
.z-spinner-focus .z-spinner-btn-clk, .z-spinner .z-spinner-btn-clk,
.z-doublespinner-focus .z-doublespinner-btn-clk, .z-doublespinner .z-doublespinner-btn-clk {
	background-position: -38px 0;
	border-bottom: 1px solid #8FB9D0;
}
.z-combobox-focus .z-combobox-btn,
.z-bandbox-focus .z-bandbox-btn,
.z-datebox-focus .z-datebox-btn,
.z-timebox-focus .z-timebox-btn,
.z-spinner-focus .z-spinner-btn,
.z-doublespinner-focus .z-doublespinner-btn {
	background-position: -57px 0;
	border-bottom: 1px solid #8FB9D0;
}
.z-combobox-rounded-focus .z-combobox-rounded-inp,
.z-bandbox-rounded-focus .z-bandbox-rounded-inp,
.z-datebox-rounded-focus .z-datebox-rounded-inp,
.z-timebox-rounded-focus .z-timebox-rounded-inp,
.z-spinner-rounded-focus .z-spinner-rounded-inp,
.z-doublespinner-rounded-focus .z-doublespinner-rounded-inp {
	background-position: 0 -72px;
}
.z-combobox-rounded-focus .z-combobox-rounded-btn,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn,
.z-datebox-rounded-focus .z-datebox-rounded-btn,
.z-timebox-rounded-focus .z-timebox-rounded-btn,
.z-spinner-rounded-focus .z-spinner-rounded-btn,
.z-doublespinner-rounded-focus .z-doublespinner-rounded-btn {
	background-position: 0 -192px;
}
.z-combobox-rounded-focus .z-combobox-rounded-btn-right-edge,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn-right-edge,
.z-datebox-rounded-focus .z-datebox-rounded-btn-right-edge,
.z-timebox-rounded-focus .z-timebox-rounded-btn-right-edge,
.z-spinner-rounded-focus .z-spinner-rounded-btn-right-edge,
.z-doublespinner-rounded-focus .z-doublespinner-rounded-btn-right-edge {
	background-position: -19px -192px;
}
.z-combobox-focus .z-combobox-btn-over,
.z-bandbox-focus .z-bandbox-btn-over,
.z-datebox-focus .z-datebox-btn-over,
.z-timebox-focus .z-timebox-btn-over,
.z-spinner-focus .z-spinner-btn-over,
.z-doublespinner-focus .z-doublespinner-btn-over {
	background-position: -19px 0;
	border-bottom: 1px solid #8FB9D0;
}
.z-combobox-rounded-focus .z-combobox-rounded-btn-over,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn-over,
.z-datebox-rounded-focus .z-datebox-rounded-btn-over,
.z-timebox-rounded-focus .z-timebox-rounded-btn-over,
.z-spinner-rounded-focus .z-spinner-rounded-btn-over,
.z-doublespinner-rounded-focus .z-doublespinner-rounded-btn-over {
	background-position: 0 -216px;
}
.z-combobox-rounded-focus .z-combobox-rounded-inp-clk, .z-combobox-rounded .z-combobox-inp-clk,
.z-bandbox-rounded-focus .z-bandbox-rounded-inp-clk, .z-bandbox-rounded .z-bandbox-inp-clk,
.z-datebox-rounded-focus .z-datebox-rounded-inp-clk, .z-datebox-rounded .z-datebox-inp-clk,
.z-timebox-rounded-focus .z-timebox-rounded-inp-clk, .z-timebox-rounded .z-timebox-inp-clk,
.z-spinner-rounded-focus .z-spinner-rounded-inp-clk, .z-spinner-rounded .z-spinner-inp-clk,
.z-doublespinner-rounded-focus .z-doublespinner-rounded-inp-clk, .z-doublespinner-rounded .z-doublespinner-inp-clk {
	background-position: 0 -48px;
}
.z-combobox-rounded-focus .z-combobox-rounded-btn-clk, .z-combobox-rounded .z-combobox-rounded-btn-clk,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn-clk, .z-bandbox-rounded .z-bandbox-rounded-btn-clk,
.z-datebox-rounded-focus .z-datebox-rounded-btn-clk, .z-datebox-rounded .z-datebox-rounded-btn-clk,
.z-timebox-rounded-focus .z-timebox-rounded-btn-clk, .z-timebox-rounded .z-timebox-rounded-btn-clk,
.z-spinner-rounded-focus .z-spinner-rounded-btn-clk, .z-spinner-rounded .z-spinner-rounded-btn-clk,
.z-doublespinner-rounded-focus .z-doublespinner-rounded-btn-clk, .z-doublespinner-rounded .z-doublespinner-rounded-btn-clk {
	background-position: 0 -168px !important;
}

<%-- Timebox and Spinner --%>
.z-timebox-rounded,
.z-spinner-rounded,
.z-doublespinner-rounded,
.z-timebox,
.z-spinner,
.z-doublespinner {
	display:-moz-inline-box;
	display:inline-block;
}
.z-timebox .z-timebox-btn,
.z-spinner .z-spinner-btn,
.z-doublespinner .z-doublespinner-btn {
	background: none;
	background-image: none;
	border: 1px solid #CCCCCC;
	border-left: 0;
	width: 18px;
	height: 19px;
	overflow: visible;
}
.z-timebox-focus .z-timebox-btn,
.z-spinner-focus .z-spinner-btn,
.z-doublespinner-focus .z-doublespinner-btn {
	border-color: #90BCE6;
}
.z-timebox .z-timebox-btn-upper,
.z-spinner .z-spinner-btn-upper,
.z-doublespinner .z-doublespinner-btn-upper,
.z-timebox .z-timebox-btn-lower,
.z-spinner .z-spinner-btn-lower,
.z-doublespinner .z-doublespinner-btn-lower {
	background: none repeat scroll 0 0 transparent;
	height: 9px;
	border: 0;
	overflow: hidden; /* IE 6 */
}

.z-timebox .z-timebox-btn-upper,
.z-spinner .z-spinner-btn-upper,
.z-doublespinner .z-doublespinner-btn-upper {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/input/timebtn-up.gif', theme)});
	border-bottom: 1px solid rgba(0,0,0,0);
	-moz-border-radius:0 2px 0 0;
}
.z-timebox .z-timebox-btn-lower,
.z-spinner .z-spinner-btn-lower,
.z-doublespinner .z-doublespinner-btn-lower {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/input/timebtn-down.gif', theme)});
	-moz-border-radius:0 0 2px 0;
}
.z-timebox .z-timebox-btn-over .z-timebox-btn-upper,
.z-spinner .z-spinner-btn-over .z-spinner-btn-upper,
.z-doublespinner .z-doublespinner-btn-over .z-doublespinner-btn-upper,
.z-timebox .z-timebox-btn-over .z-timebox-btn-lower,
.z-spinner .z-spinner-btn-over .z-spinner-btn-lower,
.z-doublespinner .z-doublespinner-btn-over .z-doublespinner-btn-lower {
	background-position: -18px 0;
}
.z-timebox .z-timebox-btn-over .z-timebox-btn-up-clk,
.z-spinner .z-spinner-btn-over .z-spinner-btn-up-clk,
.z-doublespinner .z-doublespinner-btn-over .z-doublespinner-btn-up-clk,
.z-timebox .z-timebox-btn-over .z-timebox-btn-down-clk,
.z-spinner .z-spinner-btn-over .z-spinner-btn-down-clk,
.z-doublespinner .z-doublespinner-btn-over .z-doublespinner-btn-down-clk {
	background-position: -36px 0;
}
.z-timebox .z-timebox-btn-over .z-timebox-btn-upper,
.z-spinner .z-spinner-btn-over .z-spinner-btn-upper,
.z-doublespinner .z-doublespinner-btn-over .z-doublespinner-btn-upper,
.z-timebox .z-timebox-btn-up-clk,
.z-spinner .z-spinner-btn-up-clk,
.z-doublespinner .z-doublespinner-btn-up-clk {
	border: 1px solid #90BCE6;
	border-bottom: 1px solid #AADEFB;
	margin: -1px;
	margin-bottom: 0;
}
.z-timebox .z-timebox-btn-over .z-timebox-btn-lower,
.z-spinner .z-spinner-btn-over .z-spinner-btn-lower,
.z-doublespinner .z-doublespinner-btn-over .z-doublespinner-btn-lower,
.z-timebox .z-timebox-btn-down-clk,
.z-spinner .z-spinner-btn-down-clk,
.z-doublespinner .z-doublespinner-btn-down-clk {
	border: 1px solid #90BCE6;
	border-top: 0;
	margin: -1px;
	margin-top: 0;
}

.z-combobox-rounded-pp,
.z-bandbox-rounded-pp,
.z-datebox-rounded-pp,
.z-combobox-pp,
.z-bandbox-pp,
.z-datebox-pp {
	display: block; position: absolute;
	background: white; border: 1px solid #86A4BE; padding: 2px;
	font-size: ${fontSizeS};
}
.z-combobox-rounded-pp,
.z-bandbox-rounded-pp,
.z-combobox-pp,
.z-bandbox-pp {
	font-family: ${fontFamilyC};
	overflow: auto;
}
<%-- Comboitem --%>
.z-combobox-rounded-pp .z-comboitem-text, .z-combobox-rounded-pp .z-comboitem-btn,
.z-combobox-pp .z-comboitem-text, .z-combobox-pp .z-comboitem-btn {
	white-space: nowrap; font-size: ${fontSizeS}; cursor: pointer;
}
.z-combobox-rounded-pp .z-comboitem-inner, .z-combobox-rounded-pp .z-comboitem-cnt,
.z-combobox-pp .z-comboitem-inner, .z-combobox-pp .z-comboitem-cnt {<%--description--%>
	color: #888; font-size: ${fontSizeXS}; padding-left: 6px;
}
.z-combobox-rounded-pp .z-comboitem, 
.z-combobox-rounded-pp .z-comboitem a, 
.z-combobox-rounded-pp .z-comboitem a:visited,
.z-combobox-pp .z-comboitem, 
.z-combobox-pp .z-comboitem a, 
.z-combobox-pp .z-comboitem a:visited {
	font-size: ${fontSizeM}; 
	font-weight: normal; 
	color: black;
	text-decoration: none;
}
.z-combobox-rounded-pp .z-comboitem a:hover,
.z-combobox-pp .z-comboitem a:hover {
	text-decoration: underline;
}
.z-combobox-rounded-pp .z-comboitem-seld,
.z-combobox-pp .z-comboitem-seld {
	background: #b3c8e8;
}
.z-combobox-rounded-pp .z-comboitem-over,
.z-combobox-pp .z-comboitem-over {
	background: #D3EFFA;
}
.z-combobox-rounded-pp .z-comboitem-over-seld,
.z-combobox-pp .z-comboitem-over-seld {
	background: #82D5F8;
}
.z-comboitem .z-comboitem-text {
	font-size: 12px;
}

<%-- Bandbox trendy mold --%>
.z-bandbox .z-bandbox-btn {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/input/bandbtn.gif', theme)});
}
<%-- Datebox --%>
.z-datebox-rounded-over,
.z-datebox-over{
	background: #dae7f6;
}
.z-datebox .z-datebox-btn {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/input/datebtn.gif', theme)});
}
.z-datebox-rounded-pp,
.z-datebox-pp {
	border: 1px solid #888888;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	font-weight: normal;
}
.z-datebox-rounded-pp .z-datebox-rounded-calyear,
.z-datebox-pp .z-datebox-calyear {
	background: #d8e8f0;
}
<%-- Shadow --%>
.z-combobox-rounded-shadow, .z-bandbox-rounded-shadow, .z-datebox-rounded-shadow,
.z-combobox-shadow, .z-bandbox-shadow, .z-datebox-shadow {
	border-radius: 3px;
	box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.3);
	-moz-border-radius: 3px;
	-moz-box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.3);
	-webkit-border-radius: 3px;
	-webkit-box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.3);
}
<%-- disable --%>
.z-spinner-rounded-disd,
.z-doublespinner-rounded-disd,
.z-timebox-rounded-disd,
.z-datebox-rounded-disd,
.z-bandbox-rounded-disd,
.z-combobox-rounded-disd,
.z-spinner-disd,
.z-doublespinner-disd,
.z-timebox-disd,
.z-datebox-disd,
.z-bandbox-disd,
.z-comboitem-disd,
.z-combobox-disd {
	opacity: .6;
	-moz-opacity: .6;
	filter: alpha(opacity=60);
}
.z-spinner-rounded-disd, .z-spinner-rounded-disd *,
.z-doublespinner-rounded-disd, .z-doublespinner-rounded-disd *,
.z-timebox-rounded-disd, .z-timebox-rounded-disd *,
.z-datebox-rounded-disd, .z-datebox-rounded-disd *,
.z-bandbox-rounded-disd, .z-bandbox-rounded-disd *,
.z-combobox-rounded-disd, .z-combobox-rounded-disd * {
	cursor: default !important;
	color: #303030 !important;
}
.z-spinner-disd, .z-spinner-disd *,
.z-doublespinner-disd, .z-doublespinner-disd *,
.z-timebox-disd, .z-timebox-disd *,
.z-datebox-disd, .z-datebox-disd *,
.z-bandbox-disd, .z-bandbox-disd *,
.z-comboitem-disd, .z-comboitem-disd *,
.z-combobox-disd, .z-combobox-disd * {
	cursor: default !important;
	color: #CCCCCC !important;
	background: #F0F0F0;
}
.z-timebox-rounded-disd,
.z-timebox-disd {
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM};
	font-weight: normal;
}
.z-comboitem-text-disd,
.z-spinner-text-disd,
.z-doublespinner-text-disd,
.z-timebox-text-disd,
.z-datebox-text-disd,
.z-bandbox-text-disd,
.z-combobox-text-disd {
	background: #F0F0F0;
}
.z-spinner-readonly,
.z-doublespinner-readonly,
.z-timebox-readonly,
.z-datebox-readonly,
.z-bandbox-readonly,
.z-combobox-readonly,
.z-spinner-focus .z-spinner-readonly,
.z-doublespinner-focus .z-doublespinner-readonly,
.z-timebox-focus .z-timebox-readonly,
.z-datebox-focus .z-datebox-readonly,
.z-bandbox-focus .z-bandbox-readonly,
.z-combobox-focus .z-combobox-readonly {
	background: transparent repeat-x 0 0;
	background-color: #FAFAFA;
	border-right-width: 0;
	padding-right: 1px;
	cursor: default;
}
.z-combobox-real-readonly .z-combobox-right-edge,
.z-bandbox-real-readonly .z-bandbox-right-edge,
.z-datebox-real-readonly .z-datebox-right-edge,
.z-timebox-real-readonly .z-timebox-right-edge,
.z-spinner-real-readonly .z-spinner-right-edge,
.z-doublespinner-real-readonly .z-doublespinner-right-edge {
	border-right-width: 1px !important;
	-moz-border-radius: 2px 2px 2px 2px;
}
.z-combobox-rounded-readonly,
.z-bandbox-rounded-readonly,
.z-datebox-rounded-readonly,
.z-timebox-rounded-readonly,
.z-spinner-rounded-readonly,
.z-doublespinner-rounded-readonly {
	background-position: 0 -72px;
}
.z-combobox-rounded-readonly {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/combobox-rounded.png', theme)});
}
.z-bandbox-rounded-readonly {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/bandbox-rounded.png', theme)});
}
.z-datebox-rounded-readonly {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/datebox-rounded.png', theme)});
}
.z-timebox-rounded-readonly,
.z-spinner-rounded-readonly,
.z-doublespinner-rounded-readonly {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/timebox-rounded.png', theme)});	
}
.z-combobox-rounded .z-combobox-rounded-btn-right-edge.z-combobox-rounded-btn-readonly,
.z-combobox-rounded i.z-combobox-rounded-btn-right-edge-readonly,
.z-bandbox-rounded .z-bandbox-rounded-btn-right-edge.z-bandbox-rounded-btn-readonly,
.z-bandbox-rounded i.z-bandbox-rounded-btn-right-edge-readonly,
.z-datebox-rounded .z-datebox-rounded-btn-right-edge.z-datebox-rounded-btn-readonly,
.z-datebox-rounded i.z-datebox-rounded-btn-right-edge-readonly,
.z-timebox-rounded .z-timebox-rounded-btn-right-edge.z-timebox-rounded-btn-readonly,
.z-timebox-rounded i.z-timebox-rounded-btn-right-edge-readonly,
.z-spinner-rounded .z-spinner-rounded-btn-right-edge.z-spinner-rounded-btn-readonly,
.z-doublespinner-rounded .z-doublespinner-rounded-btn-right-edge.z-doublespinner-rounded-btn-readonly,
.z-spinner-rounded i.z-spinner-rounded-btn-right-edge-readonly,
.z-doublespinner-rounded i.z-doublespinner-rounded-btn-right-edge-readonly {
	background-position: -19px -192px;
}
.z-combobox-rounded .z-combobox-rounded-btn-readonly,
.z-bandbox-rounded .z-bandbox-rounded-btn-readonly,
.z-datebox-rounded .z-datebox-rounded-btn-readonly,
.z-timebox-rounded .z-timebox-rounded-btn-readonly,
.z-spinner-rounded .z-spinner-rounded-btn-readonly,
.z-doublespinner-rounded .z-doublespinner-rounded-btn-readonly {
	background-position: 0 -192px;
}

.z-spinner-real-readonly,
.z-doublespinner-real-readonly,
.z-timebox-real-readonly,
.z-datebox-real-readonly,
.z-bandbox-real-readonly,
.z-combobox-real-readonly {
	background-color: #FAFAFA;
}
<%-- focus inp btn readonly --%>
.z-combobox-rounded-focus .z-combobox-rounded-readonly,
.z-bandbox-rounded-focus .z-bandbox-rounded-readonly,
.z-datebox-rounded-focus .z-datebox-rounded-readonly,
.z-timebox-rounded-focus .z-timebox-rounded-readonly,
.z-spinner-rounded-focus .z-spinner-rounded-readonly,
.z-doublespinner-rounded-focus .z-doublespinner-rounded-readonly {
	background-position: 0 -96px;
}
.z-combobox-rounded-focus .z-combobox-rounded-btn-right-edge.z-combobox-rounded-btn-readonly,
.z-combobox-rounded-focus i.z-combobox-rounded-btn-right-edge-readonly,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn-right-edge.z-bandbox-rounded-btn-readonly,
.z-bandbox-rounded-focus i.z-bandbox-rounded-btn-right-edge-readonly,
.z-datebox-rounded-focus .z-datebox-rounded-btn-right-edge.z-datebox-rounded-btn-readonly,
.z-datebox-rounded-focus i.z-datebox-rounded-btn-right-edge-readonly,
.z-timebox-rounded-focus .z-timebox-rounded-btn-right-edge.z-timebox-rounded-btn-readonly,
.z-timebox-rounded-focus i.z-timebox-rounded-btn-right-edge-readonly,
.z-spinner-rounded-focus .z-spinner-rounded-btn-right-edge.z-spinner-rounded-btn-readonly,
.z-doublespinner-rounded-focus .z-doublespinner-rounded-btn-right-edge.z-doublespinner-rounded-btn-readonly,
.z-spinner-rounded-focus i.z-spinner-rounded-btn-right-edge-readonly,
.z-doublespinner-rounded-focus i.z-doublespinner-rounded-btn-right-edge-readonly {
	background-position: -19px -192px;
}
.z-combobox-rounded-focus .z-combobox-rounded-btn-readonly,
.z-bandbox-rounded-focus .z-bandbox-rounded-btn-readonly,
.z-datebox-rounded-focus .z-datebox-rounded-btn-readonly,
.z-timebox-rounded-focus .z-timebox-rounded-btn-readonly,
.z-spinner-rounded-focus .z-spinner-rounded-btn-readonly,
.z-doublespinner-rounded-focus .z-doublespinner-rounded-btn-readonly {
	background-position: 0 -192px;
}
<%-- Inplace editing --%>
.z-combobox-inplace,
.z-bandbox-inplace,
.z-datebox-inplace,
.z-timebox-inplace,
.z-spinner-inplace,
.z-doublespinner-inplace {
	border: 0;
	background: none;
	padding-top: 1px;
	padding-bottom: 1px;
}
.z-combobox-rounded-inplace .z-combobox-rounded-inp,
.z-bandbox-rounded-inplace .z-bandbox-rounded-inp,
.z-datebox-rounded-inplace .z-datebox-rounded-inp,
.z-timebox-rounded-inplace .z-timebox-rounded-inp,
.z-spinner-rounded-inplace .z-spinner-rounded-inp,
.z-doublespinner-rounded-inplace .z-doublespinner-rounded-inp,
.z-combobox-inplace .z-combobox-inp,
.z-bandbox-inplace .z-bandbox-inp,
.z-datebox-inplace .z-datebox-inp,
.z-timebox-inplace .z-timebox-inp,
.z-spinner-inplace .z-spinner-inp,
.z-doublespinner-inplace .z-doublespinner-inp {
	padding: 2px 1px 2px 5px;
	border: 0;
	background: none;
	<c:if test="${zk.ie > 0 or zk.opera > 0}">
	padding: 2px;
		<c:if test="${zk.ie >= 8}">
			padding: 1px;
			padding-left: 2px;
			padding-right: 2px;
		</c:if>
	</c:if>
}
.z-combobox-rounded-inplace .z-combobox-rounded-inp,
.z-bandbox-rounded-inplace .z-bandbox-rounded-inp,
.z-datebox-rounded-inplace .z-datebox-rounded-inp,
.z-timebox-rounded-inplace .z-timebox-rounded-inp,
.z-spinner-rounded-inplace .z-spinner-rounded-inp,
.z-doublespinner-rounded-inplace .z-doublespinner-rounded-inp {
	padding-top: 5px;
	padding-bottom: 5px;
	background: none !important;
}
.z-combobox-inplace .z-combobox-inp,
.z-bandbox-inplace .z-bandbox-inp,
.z-datebox-inplace .z-datebox-inp,
.z-timebox-inplace .z-timebox-inp,
.z-spinner-inplace .z-spinner-inp,
.z-doublespinner-inplace .z-doublespinner-inp {
	border-right-width: 0 !important;
}
.z-combobox-inplace .z-combobox-btn,
.z-bandbox-inplace .z-bandbox-btn,
.z-datebox-inplace .z-datebox-btn,
.z-timebox-inplace .z-timebox-btn,
.z-spinner-inplace .z-spinner-btn,
.z-doublespinner-inplace .z-doublespinner-btn {
	display: none;
}
.z-combobox-rounded-inplace .z-combobox-rounded-btn,
.z-bandbox-rounded-inplace .z-bandbox-rounded-btn,
.z-datebox-rounded-inplace .z-datebox-rounded-btn,
.z-timebox-rounded-inplace .z-timebox-rounded-btn,
.z-spinner-rounded-inplace .z-spinner-rounded-btn,
.z-doublespinner-rounded-inplace .z-doublespinner-rounded-btn {
	visibility: hidden;
	background: none !important;
}

<%-- IE --%>
<c:if test="${zk.ie > 0}">
.z-combobox-rounded-pp .z-comboitem-inner,
.z-combobox-pp .z-comboitem-inner {<%--description--%>
	padding-left: 5px;
}
.z-timebox .z-timebox-btn-upper,
.z-spinner .z-spinner-btn-upper,
.z-doublespinner .z-doublespinner-btn-upper {
	border-bottom: 1px solid transparent;
}
</c:if>
<c:if test="${zk.ie < 8}">
.z-combobox-inplace input.z-combobox-right-edge,
.z-bandbox-inplace input.z-bandbox-right-edge,
.z-datebox-inplace input.z-datebox-right-edge,
.z-timebox-inplace input.z-timebox-right-edge,
.z-spinner-inplace input.z-spinner-right-edge,
.z-doublespinner-inplace input.z-doublespinner-right-edge {
	border: 0 !important;
}
</c:if>
<c:if test="${zk.ie == 6}">
.z-timebox .z-timebox-btn-upper,
.z-spinner .z-spinner-btn-upper,
.z-doublespinner .z-doublespinner-btn-upper {
	border-bottom: 1px solid #FFFFFF;
}
.z-timebox .z-timebox-btn-over,
.z-spinner .z-spinner-btn-over,
.z-doublespinner .z-doublespinner-btn-over {
	margin-left: -1px;
	border: 1px solid #8FB9D0;
}
.z-timebox-focus .z-timebox-btn-over,
.z-spinner-focus .z-spinner-btn-over,
.z-doublespinner-focus .z-doublespinner-btn-over {
	margin-left: -1px;
	border-left: 1px solid #8FB9D0;
}
.z-combobox-rounded-inp,
.z-combobox-rounded .z-combobox-rounded-btn,
.z-combobox-rounded-readonly {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/combobox-rounded.gif', theme)});
}
.z-bandbox-rounded-inp,
.z-bandbox-rounded .z-bandbox-rounded-btn,
.z-bandbox-rounded-readonly {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/bandbox-rounded.gif', theme)});
}
.z-datebox-rounded-inp,
.z-datebox-rounded .z-datebox-rounded-btn,
.z-datebox-rounded-readonly {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/datebox-rounded.gif', theme)});
}
.z-timebox-rounded-inp,
.z-spinner-rounded-inp,
.z-doublespinner-rounded-inp,
.z-timebox-rounded .z-timebox-rounded-btn,
.z-spinner-rounded .z-spinner-rounded-btn,
.z-doublespinner-rounded .z-doublespinner-rounded-btn,
.z-timebox-rounded-readonly,
.z-spinner-rounded-readonly,
.z-doublespinner-rounded-readonly {
	background-image: url(${c:encodeThemeURL('~./${theme}/img/button/timebox-rounded.gif', theme)});	
}
</c:if>
<%-- Gecko --%>
<c:if test="${zk.gecko > 0}">
i.z-combobox-rounded-btn, i.z-datebox-rounded-btn, i.z-bandbox-rounded-btn,
i.z-timebox-rounded-btn, i.z-spinner-rounded-btn, i.z-doublespinner-rounded-btn,
i.z-combobox-btn, i.z-datebox-btn, i.z-bandbox-btn, i.z-timebox-btn,
i.z-spinner-btn, i.z-doublespinner-btn {<%-- button at the right edge --%>
	margin: 0; padding: 0;
}
</c:if>
