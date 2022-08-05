/* eslint-disable @typescript-eslint/no-require-imports */
/* index.ts

	Purpose:

	Description:

	History:
		1:17 PM 2022/1/18, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
export default {};
import './crashmsg';  // side-effect-only import
import './ext/jquery'; // side-effect-only import
export * from './zk';
import './js'; // side-effect-only import
export * from './dom';
export * from './evt';
export * from './anima';
export * from './drag';
export * from './effect';
export * from './math';
export * from './utl';
export * from './keys';
export * from './widget';
export * from './pkg';
export * from './mount';
export * from './bookmark';
export * from './historystate';
export * from './au';
export * from './flex';
export * from './dateImpl';

declare global {
	interface Window {
		$: typeof jq;
		jQuery: typeof jq;
	}
}
if (!window.jQuery) {
	window.$ = window.jQuery = jq;
}
if (zk.gecko) {
	require('./domgecko');
} else if (zk.safari) {
	require('./domsafari');
} else if (zk.opera) {
	require('./domopera');
}

window.$eval = function<T> (s: unknown): T | undefined {
	//1. jq.globalEval() seems have memory leak problem, so use eval()
	//2. we cannot use eval() directly since compressor won't shorten names
	// eslint-disable-next-line no-eval
	return eval(s as string) as T | undefined;
	// WARN: conisder https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/eval#never_use_eval!
	// Despite the official TS definition for `eval`, `eval` can actually accept anything.
};

if (!Promise) {
	require('./ext/promise-polyfill.js');
}

// workaround for FileUpload with fetch() API.
require('./ext/fetch.js');

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

require('./ext/jquery.json.js');
require('./ext/jquery.mousewheel.js');
require('./ext/jquery.transit.js');
require('./ext/focus-options-polyfill.js');

// workaround for webpack with ./ext/moment-timezone-with-data.js
// eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
zk.mm = require('./ext/moment.js');
require('./ext/moment-timezone-with-data.js');

// register Widgets
zkreg('zk.Page', true);
