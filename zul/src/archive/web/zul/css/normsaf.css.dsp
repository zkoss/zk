<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>

/* headers */
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
	font-family: Verdana, Tahoma, Arial, Helvetica, sans-serif;
	font-size: small; font-weight: bold; color: #107080;
}
h5 {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: small; font-weight: bold; color: #603080;
}
h6 {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: small; font-weight: normal; color: #404040;
}

/* paragraphs */
p, div, span, label, a, li, dt, dd {
	font-family: "Verdana", Tahoma, Arial, serif;
	font-size: small; font-weight: normal;
}
button {
	font-family: Tahoma, Arial, serif;
	font-size: x-small; font-weight: normal;
}
input, textarea {
	font-family: "Verdana", Tahoma, Arial, serif;
	font-size: small; font-weight: normal;
}
legend {
	font-family: Tahoma, Arial, serif;
	font-size: small; font-weight: normal;
}
hr {
	color: lightgray; background: lightgray;
	height: 1px;
}
pre {
	font-family: "Verdana", Tahoma, Arial, serif;
	font-size: small; font-weight: normal;
}
body {
	font-family: "Verdana", Tahoma, Arial, serif;
	font-size: small; font-weight: normal;
	margin: 2px 5px; padding: 0;
}
code {
	font-family: "Lucida Console", "Courier New", Courier, mono;
	font-size: x-small;  font-weight: normal;
}
dfn {
	font-family: "Lucida Console", "Courier New", Courier, mono;
	font-size: x-small; font-style: normal;
}

th {
	font-family: Tahoma, Garamond, Century, Arial, serif;
	font-weight: bold; 
}

thead tr {
	font-family: Tahoma, Garamond, Century, Arial, serif;
	font-weight: bold;
}

dt {
	margin: 0.5em 0 0.3em 0;
	font-family: Tahoma, Garamond, Century, Arial, serif;
	font-weight: bold;
}
dd {
	margin: 0 0 0 0.8em;
}

img	{border: 0;}

h1 em	{color: #dd0000}

li	{margin-top: 2pt}
ul li	{list-style: url(${c:encodeURL('~./img/bullet1.gif')}) disc}
ul ul li	{list-style: url(${c:encodeURL('~./img/bullet2.gif')}) circle}
ul ul ul li	{list-style: url(${c:encodeURL('~./img/bullet3.gif')}) square}

/* Alpha */
a.alpha { color: #000000; text-decoration: none; }
a.alpha:hover { color: #000000; text-decoration: underline; }
tr.alpha { background: #6699CC; }
td.alpha, td.alpha-gradient { background: #6699CC; }
font.alpha { color: #000000; font-family: Tahoma, Arial; font-weight:  bold; }
.alpha-neg-alert { color: #FF0000; }
.alpha-pos-alert { color: #007F00; }

/* Beta */
a.beta { color: #000000; text-decoration: none; }
a.beta:hover { color: #000000; text-decoration: underline; }
tr.beta { background: #B6CBEB; }
td.beta, td.beta-gradient { background: #B6CBEB; }
font.beta { color: #000000; font-family: "Verdana", Tahoma, Arial; font-weight: normal; }
.beta-neg-alert { color: #FF0000; }
.beta-pos-alert { color: #007F00; }

/* Gamma */
a.gamma { color: #000000; text-decoration: none; }
a.gamma:hover { color: #000000; text-decoration: underline; }
tr.gamma { background: #F4F4F4; }
td.gamma, td.gamma-gradient  { background: #F4F4F4; }
font.gamma { color: #000000; font-family: "Verdana", Tahoma, Arial; font-weight: normal; }
.gamma-neg-alert { color: #FF0000; }
.gamma-pos-alert { color: #007F00; }

/* The hyperlink's style class. */
.link {cursor: pointer;}

/* ZK */
div.embedded { /* must consistent with lang.xml */
	padding: 2px; border: 1px solid #aab;
}
div.modal, div.modal-none, div.overlapped, div.overlapped-none { /* must consistent with lang.xml */
	position: absolute;
	background: #E8E8D0;
	padding: 1px; margin: 1px;
}
div.modal, div.overlapped {
	border: 2px solid #77a; padding: 2px; margin: 0;
}
div.popup, div.popup-none { /* must consistent with lang.xml */
	position: absolute; background: white; padding: 1px; margin: 0;
}
div.popup {
	border: 1px solid black;
}

/* groupbox caption */
.caption input, .caption td {
	font-size: x-small;
}
.caption button {
	font-size: xx-small; font-weight: normal;
	padding-top: 0; padding-bottom: 0; margin-top: 0; margin-bottom: 0;
}
.caption a, .caption a:visited {
	font-size: x-small; font-weight: normal; color: black; background: none;
	text-decoration: none;
}

/* window title/caption */
.title td {
	font-size: x-small; font-weight: bold;
	padding-top: 2px; padding-bottom: 3px; margin-bottom: 2px;
	background: #669; color: white;
}
.title a, .title a:visited {
	color: white;
}
.caption a:hover, .title a:hover {
	text-decoration: underline;
}

div.modal_mask { /* don't change */
	position: absolute; z-index: 20000;
	opacity: .4;
	background:transparent !important;
	background: #181818;
	background-image: url(${c:encodeURL('~./zul/img/modal-mask.png')}) !important; /* Moz... */
	background-image: none; background-repeat: repeat;
	display: none;
}

/* ZK separator */
div.hsep, div.hsep-bar {
	display: block; width: 100%; padding: 0; margin: 2pt 0; font-size: 0;
}
div.vsep, div.vsep-bar {
	display: inline; margin: 0 1pt; padding: 0;
}
div.hsep-bar {
	border-top: 1px solid #888;
}
div.vsep-bar {
	border-left: 1px solid #666; margin-left: 2pt;
}

td.vbox {
	margin: 0; padding-bottom: 0.4em;
}
td.hbox {
	margin: 0; padding-right: 0.6em;
}

/* ZK toolbar and toolbarbutton */
.toolbar {
	padding: 1px; background: threedface; border: 1px solid threedface;
	border-color: threedhighlight threedshadow threedshadow threedhighlight;
}
.caption .toolbar, .caption .toolbarbutton {
	background: none; border: 0;
}

.toolbar a, .toolbar a:visited, .toolbar a:hover {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: x-small; font-weight: normal; color: black;
	background: threedface; border: 1px solid threedface;
	text-decoration: none;
}
.toolbar a:hover {
	border-color: threedhighlight threedshadow threedshadow threedhighlight;
}

.caption .toolbar a, .caption .toolbar a:visited, .caption .toolbar a:hover {
	background: none; border: 0; color: black;
}
.caption .toolbar a:hover {
	text-decoration: underline;
}
.title .toolbar a, .title .toolbar a:visited, .title .toolbar a:hover {
	background: none; border: 0; color: white;
}
.title a:hover {
	text-decoration: underline;
}

/* ZK tree, listbox, grid */
div.listbox, div.tree, div.grid, div.grid-no-striped { /* depends sclass. */
	background: threedface; border: 1px solid #7F9DB9;
}
div.tree-head, div.listbox-head, div.grid-head { /* always used. */
	background: threedface; border: 0; overflow: hidden; width: 100%;
}
div.tree-head td, div.listbox-head td, div.grid-head td {
	overflow: hidden; border: 1px solid;
	border-color: threedhighlight threedshadow threedshadow threedhighlight;
	white-space: nowrap; padding: 2px;
	font-size: small; font-weight: normal;
}

div.tree-body, div.listbox-body, div.grid-body { /* always used. */
	background: window; border: 0; overflow: auto; width: 100%;
}
div.tree-body td, div.listbox-body td {
	border: 0; cursor: pointer; padding: 0 2px;
	font-size: small; font-weight: normal;
}
div.listbox-head td.sort, div.grid-head td.sort {
	cursor: pointer;
	background-image: url(${c:encodeURL('~./zul/img/sort/hint.png')});
	background-position: right;
	background-repeat: no-repeat;
}
div.listbox-head td.sort-asc, div.grid-head td.sort-asc {
	cursor: pointer;
	background-image: url(${c:encodeURL('~./zul/img/sort/asc.png')});
	background-position: right;
	background-repeat: no-repeat;
}
div.listbox-head td.sort-dsc, div.grid-head td.sort-dsc {
	cursor: pointer;
	background-image: url(${c:encodeURL('~./zul/img/sort/dsc.png')});
	background-position: right;
	background-repeat: no-repeat;
}

div.listbox-foot { /* always used */
	background: #EEE; border-top: 1px solid #BBB;
}

tr.item, tr.item a, tr.item a:visited {
	font-size: small; font-weight: normal; color: black;
	text-decoration: none;
}
tr.item a:hover {
	text-decoration: underline;
}
tr.itemsel, tr.itemsel a, tr.itemsel a:visited {
	font-size: small; font-weight: normal;
	background: #B5D5FF; color: #000;
	text-decoration: none;
}
tr.itemsel a:hover {
	text-decoration: underline;
}

td.gridev, td.gridod { /* gridev and gridod always used. */
	background: #FFF; border-bottom: none; border-left: 1px solid #FFF;
	border-right: 1px solid #CCC; border-top: 1px solid #DDD; padding: 2px;
	font-size: small; font-weight: normal;
}
div.grid td.gridod { /* gridod always used for odd row. */
	background: #EEE;
}

/* ZK tab. */
.tab, .tab a, a.tab {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: x-small; font-weight: normal; color: #300030;
}
.tab a, .tab a:visited, a.tab, a.tab:visited {
	text-decoration: none;
}
.tab a:hover, a.tab:hover {
	text-decoration: underline;
}
.tabsel, .tabsel a, a.tabsel {
	font-family: Tahoma, Arial, Helvetica, sans-serif;
	font-size: x-small; font-weight: bold; color: #500060;
}
.tabsel a, .tabsel a:visited, a.tabsel, a.tabsel:visited {
	text-decoration: none;
}
.tabsel a:hover, a.tabsel:hover {
	text-decoration: underline;
}

td.tabpanel-hr, div.tabpanel-ac, div.groupbox-3d { /* horz, accd, gb-3d */
	border-left: 1px solid #5C6C7C; border-right: 1px solid #5C6C7C; 
	border-bottom: 1px solid #5C6C7C; padding: 5px;
}
td.tabpanels { /* vert */
	border-top: 1px solid #5C6C7C; border-right: 1px solid #5C6C7C; 
	border-bottom: 1px solid #5C6C7C; padding: 5px;
}

/* ZK menu. */
div.menubar, div.menupopup, div.ctxpopup {
	cursor: pointer; background: menu; padding: 1px;
}
div.menubar {
	border: 1px solid black;
}
div.menupopup, div.ctxpopup {
	display: block; position: absolute; z-index: 88000;
	border: 1px outset;
}
div.menubar td, div.menupopup td {
	white-space: nowrap;
}
div.menubar a, div.menubar a:visited, div.menubar a:hover, div.menupopup a, div.menupopup a:visited, div.menupopup a:hover {
	font-size: x-small; font-weight: normal;
	border: 1px solid menu; background: menu; color: black; /*menutext is white*/
	text-decoration: none;
}
div.menubar a:hover {
	background: threedface;
	border-color: threedhighlight threedshadow threedshadow threedhighlight;
}
div.menupopup a:hover {
	background: #B5D5FF; color: #000; /*Safari Bug 47298 */
}
div.menupopup hr {
	border: 0; color: darkgray; background: darkgray;
}

/** Combobox and Datebox */
span.combobox, span.datebox, span.bandbox {
	border: 0; padding: 0; margin: 0; white-space: nowrap;
}
div.comboboxpp, div.bandboxpp { /*hardcoded in DSP*/
	display: block; position: absolute; z-index: 80000; overflow: auto;
	background: white; border: 1px solid black; padding: 2px;
	font-size: x-small;
}
.comboboxpp td { /*label*/
	white-space: nowrap; font-size: x-small;
}
.comboboxpp td span { /*description*/
	color: #888; font-size: xx-small; padding-left: 6px;
}

/* ZK error message box */
div.errbox {
	margin: 0; padding: 1px; border: 1px outset; cursor: pointer;
	background: #E8E0D8; position: absolute; z-index: 70000;
}

/*ZK datebox and calendar*/
div.dateboxpp { /*hardcoded in DSP*/
	display: block; position: absolute; z-index: 80000;
	background: white; border: 1px solid black; padding: 2px;
}

table.calendar {
	background: window; border: 1px solid #7F9DB9;
}
table.calyear {
	background: #eaf0f4; border: 1px solid;
	border-color: threedhighlight threedshadow threedshadow threedhighlight;
}
table.calday {
	border: 1px solid #ddd;
}
.calyear td {
	font-size: small; font-weight: bold; text-align: center;
	white-space: nowrap;
}
.calmon td, tr.calday td, tr.calday td a, tr.calday td a:visited {
	font-size: x-small; color: #35254F; text-align: center;
	cursor: pointer; text-decoration: none;
}
.calday td {
	padding: 1px 3px;
}
.calday td a:hover {
	text-decoration: underline;
}
.calmon td.sel, tr.calday td.sel {
	background: #cddeee;
}
.caldmon td.dis, tr.calday td.dis {
	color: #888;
}
.caldow td {
	font-size: x-small; color: #333; font-weight: bold;
	padding: 1px 2px; background: #e8e8f0; text-align: center;
}

div.dateboxpp table.calyear {
	background: #d8e8f0;
}
