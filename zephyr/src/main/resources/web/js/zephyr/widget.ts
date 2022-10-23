/* widget.ts

	Purpose:
		
	Description:
		
	History:
		5:21 PM 2021/9/10, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
export default {};
const _xWidget: Partial<zk.Widget> = {},
	xWidget = zk.override(zk.Widget.prototype, _xWidget, {
    detach(): void {
		this._sendRMS();
		_xWidget.detach!.apply(this);
    },
	_sendRMS() {
		// ignore in cloudMode
		if (!zephyr.cloudMode) {
			let uuids: string[] = [];
			this._lookupAllWidgetUuids(this, uuids);
			this.fire('$rms$', uuids, {toServer: true});
		}
	},
    _lookupAllWidgetUuids(parent: zk.Widget, uuids: string[]): void {
        if (parent != null) {
            uuids.push(parent.uuid);
            for (let w = parent.firstChild; w; w = w.nextSibling) {
                this._lookupAllWidgetUuids(w, uuids);
            }
        }
    },
	// support multiple @action
	'set@action'(args) {
		let at = this['@action'] as [] | unknown | undefined;
		if (Array.isArray(at)) {
			at.push(args);
		} else if (at) {
			this['@action'] = [at, args];
		} else {
			this['@action'] = args as never;
		}
	}
});

// support closest()
let xWidget$ = zk.Widget.$;

	// Support two syntax:
	// 1. selector < singleSelector
	//     For example,
	//         .child < .parent
	// 2. selector < { multiple selectors }
	//     For example,
	//         .child < { .parent > div + .second } > .something
	function _parse(query: string): string[][] {
		let matches: string[] = [],
			parts: string[] = [], closest = false, sibling = false, start = 0, open;
		for (let i = 0, j = query.length; i < j; i++) {
			let c = query.charAt(i);
			switch (c) {
				case '<': {
					parts.push(query.substring(start, i - 1));
					closest = true;
					start = i;
					break;
				}
				case '.': {
					// avoid to mismatch
					let substr = query.substring(i + 1);
					if (substr.startsWith('nextSibling') ||
						substr.startsWith('previousSibling') ||
						substr.startsWith('firstChild') ||
						substr.startsWith('lastChild')) {
						parts.push(query.substring(start, i - 1));
						sibling = true;
						start = i;
					}
					break;
				}
				case '{': {
					start = i + 1;
					open = true;
					break;
				}
				case '}': {
					matches.push(query.substring(start, i - 1));
					start = i + 1;
					closest = false;
					open = false;
					break;
				}
				case ' ': {
					if (closest && !open || sibling && !open) {
						if (start + 1 != i) {
							matches.push(query.substring(start, i));
							closest = false;
						}
						start = i;
					}
					break;
				}
			}
		}

		// add last part into matches or parts
		if (closest || sibling) {
			if (start < query.length) {
				matches.push(query.substring(start));
			}
		} else if (start < query.length) {
			parts.push(query.substring(start));
		}
		return [matches, parts];
	}
zk.Widget.$ = <T extends zk.Widget> (n?: string | JQuery | JQuery.Event | zk.Event | Node | undefined | null | T, // eslint-disable-line zk/noNull
		opts?: Partial<{exact: boolean; strict: boolean; child: boolean}>): T | undefined => {
	if (typeof n == 'string') {
		let query = n.trim(),
			[matches, parts] = _parse(query);
		if (matches?.length) {
			// has closest expression
			let	elems: JQuery | Document = document;
			for (let i = 0, j = parts.length; i < j; i++) {
				if (parts[i].trim()) {
					elems = jq(parts[i], elems);
					if (elems.length) {
						for (let i = 0, j = matches.length; i < j; i++) {
							let match = matches[i];
							switch (match) {
								case '.nextSibling':
								case '.previousSibling':
								case '.firstChild':
								case '.lastChild': {
									elems = jq(elems.zk.$()[match.substring(1)]);
									break;
								}
								default: {
									elems = elems.closest(match.substring(1));
								}
							}
						}
					}
				}
			}
			return (elems as JQuery).zk.$() as T | undefined;
		}
	}
	return xWidget$.call(this, n, opts) as never;
};
const xCmd1: Partial<typeof zAu.cmd1> = {};
zk.override(zAu.cmd1, xCmd1, {
	outer(wgt: zk.Widget, code: string): void {
		// send RMS firs before the wgt detached.
		(wgt as typeof xWidget)._sendRMS();
		xCmd1.outer!.call(this, wgt, code);
	},
	rmChd(wgt: zk.Widget): void {
		if (wgt) {
			for (let w = wgt.firstChild; w;) {
				let next = w.nextSibling;
				w.detach();
				zAu._storeStub(w); //used by mount.js
				w = next;
			}
		}
	},
	addBfrChd(wgt: zk.Widget, nChild: number, ...args: string[]): void {
		if (wgt) {
			if (wgt.nChildren <= nChild) {
				zAu.cmd1.addChd(wgt, ...args);
			} else {
				let target = wgt.getChildAt(nChild);
				if (target) {
					zAu.cmd1.addBfr(target, ...args);
				}
			}
		}
	},
	replaceChd(wgt: zk.Widget, nChild: number, ...args: string[]): void {
		if (wgt) {
			let target = wgt.getChildAt(nChild);
			if (target) {
				if (target.nextSibling) {
					zAu.cmd1.addBfr(target.nextSibling, ...args);
				} else {
					zAu.cmd1.addChd(wgt, ...args);
				}
				target.detach();
				zAu._storeStub(target); //used by mount.js
			}
		}
	},
	insertAdjacent(wgt: zk.Widget, position: string, ...args: string[]): void {
		if (wgt) {
			switch (position.toLowerCase()) {
				case 'beforebegin':
					zAu.cmd1.addBfr(wgt, ...args);
					break;
				case 'afterbegin':
					zAu.cmd1.addBfr(wgt.firstChild!, ...args);
					break;
				case 'beforeend':
					zAu.cmd1.addChd(wgt, ...args);
					break;
				case 'afterend':
					zAu.cmd1.addAft(wgt, ...args);
					break;
				default:
					zk.error('Unknown position for insertAdjacent [' + position + ']');
			}
		}
	}
});