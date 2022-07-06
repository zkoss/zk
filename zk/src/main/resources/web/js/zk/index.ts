/* eslint @typescript-eslint/no-require-imports: 0,
			 no-undef: 0,
		   @typescript-eslint/no-var-requires: 0*/
/* index.ts

	Purpose:

	Description:

	History:
		1:17 PM 2022/1/18, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
import './crashmsg';
import './ext/jquery';
import {default as zk} from './zk';
import './js';
import {zjq} from './dom';
import {Event, zWatch} from './evt';
import './anima';
import {Draggable} from './drag';
import eff from './effect';
import * as math from './math';
import {zUtl} from './utl';
import {zKeys} from './keys';
import * as widget from './widget';
import * as zAu from './au';
import {default as zFlex} from './flex';
import {zkreg} from './widget';

declare global {
	interface Window {
		zk: ZKStatic;
		zFlex: typeof zFlex;
		$: typeof jq;
		jQuery: typeof jq;
	}
}
// export first for following js to use
window.zk = zk;

window.zjq = zjq;
window.$ = window.jQuery = jq;
if (zk.gecko) {
	require('./domgecko');
} else if (zk.safari) {
	require('./domsafari');
} else if (zk.opera) {
	require('./domopera');
}

window.$eval = function (s: string): unknown {
	//1. jq.globalEval() seems have memory leak problem, so use eval()
	//2. we cannot use eval() directly since compressor won't shorten names
	// eslint-disable-next-line no-eval
	return eval(s);
};

/*
TODO:
	<function class="org.zkoss.zk.ui.http.Wpds"
			  signature="java.lang.String outLibraryPropertyJavaScript()"/>
 */
zk.Event = Event;

window.zWatch = zWatch;

zk.Draggable = Draggable;
zk.eff = eff;
zk.BigDecimal = math.BigDecimal;
zk.Long = math.Long;

window.zUtl = zUtl;
window.zKeys = zKeys;

zk.DnD = widget.DnD;
zk.Widget = widget.Widget;
zk.$ = widget.$;
zk.RefWidget = widget.RefWidget;
zk.Desktop = widget.Desktop;
zk.Page = widget.Page;
zk.Body = widget.Body;
zk.Native = widget.Native;
zk.Macro = widget.Macro;
zk.Service = widget.Service;
zk.Skipper = widget.Skipper;
zk.NoDOM = widget.NoDOM;
zk.WrapClass = widget.WrapClass;


window.zkreg = widget.zkreg;
window.zkservice = widget.zkservice;
window.zkopt = widget.zkopt;

let pkg = require('./pkg').default;

zk.setLoaded = pkg.setLoaded;
zk.setScriptLoaded = pkg.setScriptLoaded;
zk.isLoaded = pkg.isLoaded;
zk.load = pkg.load;
zk._load = pkg._load;
zk.loadScript = pkg.loadScript;
zk.loadCSS = pkg.loadCSS;
zk.getVersion = pkg.getVersion;
zk.setVersion = pkg.setVersion;
zk.depends = pkg.depends;
zk.afterLoad = pkg.afterLoad;
zk.getHost = pkg.getHost;
zk.setHost = pkg.setHost;

require('./mount');

require('./bookmark');

require('./historystate');

if (!Promise) {
	require('./ext/promise-polyfill.js');
}

// workaround for FileUpload with fetch() API.
require('./ext/fetch.js');

window.zAu = zAu.default;
zk.afterAuResponse = zAu.afterAuResponse;
zk.doAfterAuResponse = zAu.doAfterAuResponse;
window.onIframeURLChange = zAu.onIframeURLChange;

window.zFlex = zFlex;

zk.touchEnabled = zk.touchEnabled !== false && (!!zk.mobile || navigator.maxTouchPoints > 0);
zk.tabletUIEnabled = zk.tabletUIEnabled !== false && !!zk.mobile;
if (zk.touchEnabled) {
	require('./ext/inputdevicecapabilities-polyfill');
	require('./zswipe');
	require('./domtouch');
}

if (zk.tabletUIEnabled) {
	document.addEventListener('DOMContentLoaded', function () {
		var jqTabletStylesheet = jq('link[href*="zkmax/css/tablet.css.dsp"]').eq(0);
		if (jqTabletStylesheet)
			jqTabletStylesheet.attr('disabled', false as unknown as string); // ZK-4451: disable tablet css
	});
}

require('./dateImpl');

require('./ext/jquery.json.js');
require('./ext/jquery.mousewheel.js');
require('./ext/jquery.transit.js');
require('./ext/focus-options-polyfill.js');

// workaround for webpack with ./ext/moment-timezone-with-data.js
zk.mm = require('./ext/moment.js');
require('./ext/moment-timezone-with-data.js');

// register Widgets
zkreg('zk.Page', true);

// export to window
export default {};

