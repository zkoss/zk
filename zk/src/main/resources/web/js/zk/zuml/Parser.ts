/* Parser.ts

	Purpose:
		
	Description:
		
	History:
		Fri Apr 10 14:30:00     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
/** iZUML utilities.
 */
//zk.$package('zk.zuml');
function _innerText(node: HTMLElement): string | undefined {
	var txt = node.innerHTML,
		j = txt.indexOf('<!--');
	if (j >= 0)
		txt = txt.substring(j + 4, txt.lastIndexOf('-->'));
	txt = txt.trim();
	return txt ? '<div>' + txt.trim() + '</div>' : undefined;
}
function _aftCreate(wgt: zk.Widget | undefined, cwgts: zk.Widget[], node: HTMLElement, opts?: {replaceHTML?}): undefined | zk.Widget | zk.Widget[] {
	let c: zk.Widget | undefined;
	if (!wgt || !(c = wgt.firstChild))
		return undefined;

	do {
		let sib = c.nextSibling;
		wgt.removeChild(c);
		cwgts.push(c);
		c = sib as never;
	} while (c);

	var l = cwgts.length;
	if (!opts || opts.replaceHTML !== false) {
		var ns = [node];
		if (l > 1) {
			const p = node.parentNode!, sib = node.nextSibling;
			for (var j = l; --j > 0;) {
				var n = document.createElement('DIV');
				ns.push(n);
				p.insertBefore(n, sib);
			}
		}
		for (let j = 0; j < l; ++j)
			cwgts[j].replaceHTML(ns[j]);
	}

	return cwgts.length <= 1 ? cwgts[0] : cwgts;
}
function _getPkgs(e: HTMLElement): string {
	var pkgmap = {}, pkgs: string[] = []; //use {} to remove duplicate packages
	_getPkgs0(e, pkgmap);
	for (var p in pkgmap)
		pkgs.push(p);
	return pkgs.join(',');
}
function _getPkgs0(e: HTMLElement, pkgmap: Record<string, unknown>): void {
	var tn = e.tagName;
	if ('zk' != tn && 'attribute' != tn) {
		if (!zk.Widget.getClass(tn)) { //not register?
			var clsnm = zk.wgt.WidgetInfo.getClassName(tn);
			if (!clsnm)
				throw 'Unknown tag: ' + tn;

			var j = clsnm.lastIndexOf('.');
			if (j >= 0)
				pkgmap[clsnm.substring(0, j)] = true;
		}

		for (let child = e.firstChild; child; child = child.nextSibling) {
			var nt = child.nodeType;
			if (nt == 1) _getPkgs0(child as HTMLElement, pkgmap);
		}
	}
}
function _create(parent: zk.Widget, e: HTMLElement | undefined, args: unknown, cwgts: zk.Widget[]): void {
	if (!e) return;

	var forEach = _eval(parent, e.getAttribute('forEach'), args);
	if (forEach != null) {
		var oldEach = window['each'] as never;
		for (var l = forEach.length, j = 0; j < l; j++) {
			window['each'] = forEach[j];
			_create0(parent, e, args, cwgts);
		}
		window['each'] = oldEach;
	} else
		_create0(parent, e, args, cwgts);
}
function _create0(parent: zk.Widget, e: HTMLElement, args: unknown, cwgts: zk.Widget[]): void {
	var ifc = _eval(parent, e.getAttribute('if'), args),
		unless = _eval(parent, e.getAttribute('unless'), args);
	if ((ifc == null || ifc) && (unless == null || !unless)) {
		var tn = e.tagName, wgt: zk.Widget;
		if ('zk' == tn) {
			wgt = parent;
		} else if ('attribute' == tn) {
			var attnm = _eval(parent, e.getAttribute('name'), args);
			if (!attnm)
				throw 'The name attribute required, ' + e;
			parent.set(attnm, zk.xml.Utl.getElementValue(e));
			return;
		} else {
			var atts = e.attributes;

			wgt = zk.Widget.newInstance(tn);
			if (cwgts) cwgts.push(wgt);
			if (parent) parent.appendChild(wgt);

			for (var l = atts.length, j = 0; j < l; ++j) {
				var att = atts[j];
				wgt.set(att.name, _eval(wgt, att.value, args)!);
			}
		}

		var prolog: undefined | string;
		for (let child = e.firstChild; child; child = child.nextSibling) {
			var nt = child.nodeType;
			if (nt == 1) {
				let ws: zk.Widget[] = [], w: zk.Widget | undefined;
				_create(wgt, child as HTMLElement, args, ws);
				if (prolog && (w = ws[0])) {
					w.prolog = prolog;
					prolog = undefined;
				}
			} else if (nt == 3) {
				let txt = _eval(wgt, child.nodeValue, args)!;
				if (txt.trim().length) {
					const w = new zk.Native();
					w.prolog = txt;
					wgt.appendChild(w);
				} else if (wgt.blankPreserved)
					prolog = txt;
			}
		}
	}
}

// eslint-disable-next-line zk/noNull
function _eval(wgt: zk.Widget, s: string | null, args: unknown): string | null {
	if (s)
		for (var j = 0, k: number, l: number, t: string, last = s.length - 1, s2: string | undefined; ;) {
			k = s.indexOf('#{', j);
			if (k < 0) {
				k = s.indexOf('${', j); //backward compatible
				if (k < 0) {
					if (s2) s = s2 + s.substring(j);
					break;
				}
			}

			t = s.substring(j, k);
			l = s.indexOf('}', k + 2);
			if (l < 0) {
				s = s2 ? s2 + t : t; //ignore #{...
				break;
			}

			s2 = s2 ? s2 + t : t;
			t = s.substring(k + 2, l); //EL

			try {
				var fn = new Function('var _=arguments[0];return ' + t) as (args) => string;
				t = wgt ? fn.call(wgt, args) : fn(args);
			} catch (e) {
				throw 'Failed to evaluate ' + t;
			}

			if (!s2 && l == last) return t; //don't convert to string

			if (t) s2 += t;

			j = l + 1;
		}
	return s;
}
/** @class zk.zuml.Parser
 * The parser of using iZUML(Client-side ZUML).
 */
export let Parser = {
	/** Parse the iZUML into widgets
	 * @param zk.Widget parent the root component
	 * @param String doc the content text of the domElement
	 * @param Map args a map of arguments
	 * @param Function fn the function to register for execution later
	 * @return zk.Widget
	 */
	create(parent: zk.Widget | undefined, doc: string | Document, args: Record<string, unknown> | undefined, fn: CallableFunction): zk.Widget | zk.Widget[] {
		if (typeof args == 'function' && !fn) {
			fn = args;
			args = undefined;
		}

		const root = (typeof doc == 'string' ? zk.xml.Utl.parseXML(doc) : doc).documentElement;

		var cwgts = [];
		zk.load(_getPkgs(root), function () {
			_create(parent!, root, args, cwgts);
			if (fn) fn(cwgts.length <= 1 ? cwgts[0] : cwgts);
		});
		return cwgts.length <= 1 ? cwgts[0] : cwgts;
	},
	/** Parse the iZUML into widgets
	 * @param String nodeId the id of the root component
	 * @param Map opts a map of options
	 * @param Map args a map of arguments
	 * @param Function fn the function to register for execution later
	 * @return zk.Widget
	 */
	createAt(nodeId: string, opts: Record<string, unknown>, args: Record<string, unknown> | undefined, fn: CallableFunction): zk.Widget | zk.Widget[] | undefined {
		if (typeof args == 'function' && !fn) {
			fn = args;
			args = undefined;
		}

		let node = jq(nodeId)[0];
		var txt = _innerText(node);
		if (!txt) return;

		var cwgts = [],
			wgt = zk.zuml.Parser.create(undefined, txt, args,
				function (w: zk.Widget) {
					w = _aftCreate(w, cwgts, node, opts) as zk.Widget;
					if (fn) fn(w);
				});
		return _aftCreate(wgt as zk.Widget, cwgts, node, opts);
	}
};
zk.zuml.Parser = Parser;
