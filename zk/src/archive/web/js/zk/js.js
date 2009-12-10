/* js.js

	Purpose:
		Enhancement to JavaScript
	Description:
		
	History:
		Thu Dec 10 12:24:26 TST 2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
zk.copy(String.prototype, {
	startsWith: function (prefix) {
		return this.substring(0,prefix.length) == prefix;
	},
	endsWith: function (suffix) {
		return this.substring(this.length-suffix.length) == suffix;
	},
	trim: function () {
		var j = 0, tl = this.length, k = tl - 1;
		while (j < tl && this.charAt(j) <= ' ')
			++j;
		while (k >= j && this.charAt(k) <= ' ')
			--k;
		return j > k ? "": this.substring(j, k + 1);
	},
	$camel: function() {
		var parts = this.split('-'), len = parts.length;
		if (len == 1) return parts[0];

		var camelized = this.charAt(0) == '-' ?
			parts[0].charAt(0).toUpperCase() + parts[0].substring(1): parts[0];

		for (var i = 1; i < len; i++)
			camelized += parts[i].charAt(0).toUpperCase() + parts[i].substring(1);
		return camelized;
	},
	$inc: function (diff) {
		return String.fromCharCode(this.charCodeAt(0) + diff)
	},
	$sub: function (cc) {
		return this.charCodeAt(0) - cc.charCodeAt(0);
	}
});

zk.copy(Array.prototype, {
	$array: true, //indicate it is an array
	$contains: function (o) {
		for (var j = 0, tl = this.length; j < tl; ++j) {
			if (o == this[j])
				return true;
		}
		return false;
	},
	$equals: function (o) {
		if (o && o.$array && o.length == this.length) {
			for (var j = this.length; j--;) {
				var e = this[j];
				if (e != o[j] && (!e || !e.$array || !e.$equals(o[j])))
					return false;
			}
			return true;
		}
	},
	$remove: function (o) {
		for (var ary = o != null && o.$array, j = 0, tl = this.length; j < tl; ++j) {
			if (o == this[j] || (ary && o.$equals(this[j]))) {
				this.splice(j, 1);
				return true;
			}
		}
		return false;
	},
	$clone: function() {
		return [].concat(this);
	}
});
if (!Array.prototype.indexOf)
	Array.prototype.indexOf = function (o) {
		for (var i = 0, len = this.length; i < len; i++)
			if (this[i] == o) return i;
		return -1;
	};
