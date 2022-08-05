/* historystate.ts

	Purpose:

	Description:

	History:
		Wed Jul 26 12:25:26 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
let popped = ('state' in window.history);
const initialURL = location.href;

export let historystate = {
	enabled: true,
	onPopState(event: PopStateEvent): void {
		const initialPop = !popped && location.href == initialURL;
		popped = true;
		if (initialPop) return;

		var data = {
			state: event.state as never,
			url: location.href
		};
		zAu.send(new zk.Event(zk.Desktop._dt!, 'onHistoryPopState', data, {implicit: true}), 1);
		if (zk.bmk.checkBookmark)
			zk.bmk.checkBookmark();
	},
	register(): void {
		if (zk.historystate.enabled)
			window.addEventListener('popstate', this.onPopState);
	}
};
zk.historystate = historystate;
zk._apac(function () {
	zk.historystate.register();
}); //see mount.js (after page AU cmds)