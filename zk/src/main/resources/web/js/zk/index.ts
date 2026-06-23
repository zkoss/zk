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
require('./ext/jquery'); // side-effect-only import
// workaround for FileUpload with fetch() API.
require('./ext/fetch.js'); // before ./au
require('./ext/purify.js');
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
	// Under strict CSP (no unsafe-eval), execute via a nonce-bearing <script> element.
	// Side effect: the body always runs in the global scope, so `var x = ...` lands on
	// window. Direct eval() in strict-mode modules already created its own scope, so
	// no caller relied on var leaking to the wrapper. Return value is NOT retrievable
	// from a <script> element — callers that need the return value must use JSON.parse,
	// jq.evalJSON, or zk.toFunction(body)() instead.
	var nonce = (typeof zk !== 'undefined') ? zk.cspNonce : undefined;
	if (nonce) {
		// Delegate to jQuery's nonce-aware executor instead of hand-rolling the
		// create/append/remove dance: jq.globalEval -> DOMEval already runs the
		// code in a nonce-bearing <script> in global scope, and zk.ts patches
		// jq.globalEval to forward zk.cspNonce when no options are passed.
		jq.globalEval(s as string);
		// One-shot dev-mode warning: callers that previously relied on $eval's return
		// value (typically pre-10.4 widgets) will silently observe undefined under CSP.
		// Surfacing this once helps locate non-migrated call sites without spamming.
		var w = window as unknown as Record<string, unknown>;
		// eslint-disable-next-line no-console
		if (!w._zkEvalCspWarned && typeof console !== 'undefined' && console.warn) {
			w._zkEvalCspWarned = true;
			// eslint-disable-next-line no-console
			console.warn('zk: $eval returns undefined under CSP. '
				+ 'Use JSON.parse / jq.evalJSON / zk.toFunction for return-value paths.');
		}
		return undefined;
	}
	// Non-CSP path: legacy eval. Requires script-src 'unsafe-eval' if enforced.
	// eslint-disable-next-line no-eval
	return eval(s as string) as T | undefined;
};

// eslint-disable-next-line @typescript-eslint/no-unnecessary-boolean-literal-compare
zk.touchEnabled = zk.touchEnabled !== false && (!!zk.mobile || navigator.maxTouchPoints > 0);
// eslint-disable-next-line @typescript-eslint/no-unnecessary-boolean-literal-compare
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
			jqTabletStylesheet.attr('disabled', false as never); // ZK-4451: disable tablet css
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
