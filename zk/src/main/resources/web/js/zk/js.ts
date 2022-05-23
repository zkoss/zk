/* eslint-disable one-var */
/* js.ts

	Purpose:
		Enhancement to JavaScript
	Description:

	History:
		Thu Dec 10 12:24:26 TST 2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
Object.assign(String.prototype, {
	$camel(this: string): string {
		var parts = this.split('-'), len = parts.length;
		if (len == 1) return parts[0];

		var camelized = this.charAt(0) == '-' ?
			parts[0].charAt(0).toUpperCase() + parts[0].substring(1) : parts[0];

		for (var i = 1; i < len; i++)
			camelized += parts[i].charAt(0).toUpperCase() + parts[i].substring(1);
		return camelized;
	},
	$inc(this: string, diff: number): string {
		return String.fromCharCode(this.charCodeAt(0) + diff);
	},
	$sub(this: string, cc: string): number {
		return this.charCodeAt(0) - cc.charCodeAt(0);
	}
});

Object.assign(Array.prototype, {
	$indexOf(this: Array<unknown>, o): number {
		return this.indexOf(o);
	},
	$contains(this: Array<unknown>, o): boolean {
		return this.includes(o);
	},
	$equals(this: Array<unknown>, o): boolean {
		if (jq.isArray(o) && o.length == this.length) {
			for (var j = this.length; j--;) {
				var e = this[j];
				if (e != o[j] && (!jq.isArray(e) || !e.$equals(o[j])))
					return false;
			}
			return true;
		}
		return false;
	},
	$remove(this: Array<unknown>, o): boolean {
		for (var ary = jq.isArray(o), j = 0, tl = this.length; j < tl; ++j) {
			if (o == this[j] || (ary && o.$equals(this[j]))) {
				this.splice(j, 1);
				return true;
			}
		}
		return false;
	},
	$addAll(this: Array<unknown>, o): number {
		return this.push.apply(this, o);
	},
	$clone(this: Array<unknown>): Array<never> {
		return [].concat(this as []);
	}
});

//ZK-2825: Looping javascript array should not include zk defined function
Object.defineProperties(Array.prototype, {
	'$indexOf': {enumerable: false},
	'$contains': {enumerable: false},
	'$equals': {enumerable: false},
	'$remove': {enumerable: false},
	'$addAll': {enumerable: false},
	'$clone': {enumerable: false}
});