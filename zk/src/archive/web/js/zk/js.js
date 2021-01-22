/* js.js

	Purpose:
		Enhancement to JavaScript
	Description:
		
	History:
		Thu Dec 10 12:24:26 TST 2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
zk.copy(String.prototype, {
	$camel: function () {
		var parts = this.split('-'), len = parts.length;
		if (len == 1) return parts[0];

		var camelized = this.charAt(0) == '-' ?
			parts[0].charAt(0).toUpperCase() + parts[0].substring(1) : parts[0];

		for (var i = 1; i < len; i++)
			camelized += parts[i].charAt(0).toUpperCase() + parts[i].substring(1);
		return camelized;
	},
	$inc: function (diff) {
		return String.fromCharCode(this.charCodeAt(0) + diff);
	},
	$sub: function (cc) {
		return this.charCodeAt(0) - cc.charCodeAt(0);
	}
});
// This polyfill is available under the MIT license.
/*! https://mths.be/startswith v0.2.0 by @mathias */
if (!String.prototype.startsWith) {
	(function () {
		'use strict'; // needed to support `apply`/`call` with `undefined`/`null`
		var defineProperty = (function () {
			// IE 8 only supports `Object.defineProperty` on DOM elements
			try {
				var object = {};
				var $defineProperty = Object.defineProperty;
				var result = $defineProperty(object, object, object) && $defineProperty;
			} catch (error) {}
			return result;
		}());
		var toString = {}.toString;
		var startsWith = function (search) {
			if (this == null) {
				throw TypeError();
			}
			var string = String(this);
			if (search && toString.call(search) == '[object RegExp]') {
				throw TypeError();
			}
			var stringLength = string.length;
			var searchString = String(search);
			var searchLength = searchString.length;
			var position = arguments.length > 1 ? arguments[1] : undefined;
			// `ToInteger`
			var pos = position ? Number(position) : 0;
			if (pos != pos) { // better `isNaN`
				pos = 0;
			}
			var start = Math.min(Math.max(pos, 0), stringLength);
			// Avoid the `indexOf` call if no match is possible
			if (searchLength + start > stringLength) {
				return false;
			}
			var index = -1;
			while (++index < searchLength) {
				if (string.charCodeAt(start + index) != searchString.charCodeAt(index)) {
					return false;
				}
			}
			return true;
		};
		if (defineProperty) {
			defineProperty(String.prototype, 'startsWith', {
				'value': startsWith,
				'configurable': true,
				'writable': true
			});
		} else {
			String.prototype.startsWith = startsWith;
		}
	}());
}
/*! https://mths.be/endswith v0.2.0 by @mathias */
if (!String.prototype.endsWith) {
	(function () {
		'use strict'; // needed to support `apply`/`call` with `undefined`/`null`
		var defineProperty = (function () {
			// IE 8 only supports `Object.defineProperty` on DOM elements
			try {
				var object = {};
				var $defineProperty = Object.defineProperty;
				var result = $defineProperty(object, object, object) && $defineProperty;
			} catch (error) {}
			return result;
		}());
		var toString = {}.toString;
		var endsWith = function (search) {
			if (this == null) {
				throw TypeError();
			}
			var string = String(this);
			if (search && toString.call(search) == '[object RegExp]') {
				throw TypeError();
			}
			var stringLength = string.length;
			var searchString = String(search);
			var searchLength = searchString.length;
			var pos = stringLength;
			if (arguments.length > 1) {
				var position = arguments[1];
				if (position !== undefined) {
					// `ToInteger`
					pos = position ? Number(position) : 0;
					if (pos != pos) { // better `isNaN`
						pos = 0;
					}
				}
			}
			var end = Math.min(Math.max(pos, 0), stringLength);
			var start = end - searchLength;
			if (start < 0) {
				return false;
			}
			var index = -1;
			while (++index < searchLength) {
				if (string.charCodeAt(start + index) != searchString.charCodeAt(index)) {
					return false;
				}
			}
			return true;
		};
		if (defineProperty) {
			defineProperty(String.prototype, 'endsWith', {
				'value': endsWith,
				'configurable': true,
				'writable': true
			});
		} else {
			String.prototype.endsWith = endsWith;
		}
	}());
}

zk.copy(Array.prototype, {
	$indexOf: function (o) {
		return jq.inArray(o, this);
	},
	$contains: function (o) {
		return this.$indexOf(o) >= 0;
	},
	$equals: function (o) {
		if (jq.isArray(o) && o.length == this.length) {
			for (var j = this.length; j--;) {
				var e = this[j];
				if (e != o[j] && (!jq.isArray(e) || !e.$equals(o[j])))
					return false;
			}
			return true;
		}
	},
	$remove: function (o) {
		for (var ary = jq.isArray(o), j = 0, tl = this.length; j < tl; ++j) {
			if (o == this[j] || (ary && o.$equals(this[j]))) {
				this.splice(j, 1);
				return true;
			}
		}
		return false;
	},
	$addAll: function (o) {
		return this.push.apply(this, o);
	},
	$clone: function () {
		return [].concat(this);
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

/*!
 * weakmap-polyfill v2.0.0 - ECMAScript6 WeakMap polyfill
 * https://github.com/polygonplanet/weakmap-polyfill
 * Copyright (c) 2015-2016 polygon planet <polygon.planet.aqua@gmail.com>
 * @license MIT
 */

(function (self) {
	'use strict';

	if (self.WeakMap) {
		return;
	}

	var hasOwnProperty = Object.prototype.hasOwnProperty;
	var defineProperty = function (object, name, value) {
		if (Object.defineProperty) {
			Object.defineProperty(object, name, {
				configurable: true,
				writable: true,
				value: value
			});
		} else {
			object[name] = value;
		}
	};

	self.WeakMap = (function () {

		// ECMA-262 23.3 WeakMap Objects
		function WeakMap() {
			if (this === void 0) {
				throw new TypeError("Constructor WeakMap requires 'new'");
			}

			defineProperty(this, '_id', genId('_WeakMap'));

			// ECMA-262 23.3.1.1 WeakMap([iterable])
			if (arguments.length > 0) {
				// Currently, WeakMap `iterable` argument is not supported
				throw new TypeError('WeakMap iterable is not supported');
			}
		}

		// ECMA-262 23.3.3.2 WeakMap.prototype.delete(key)
		defineProperty(WeakMap.prototype, 'delete', function (key) {
			checkInstance(this, 'delete');

			if (!isObject(key)) {
				return false;
			}

			var entry = key[this._id];
			if (entry && entry[0] === key) {
				delete key[this._id];
				return true;
			}

			return false;
		});

		// ECMA-262 23.3.3.3 WeakMap.prototype.get(key)
		defineProperty(WeakMap.prototype, 'get', function (key) {
			checkInstance(this, 'get');

			if (!isObject(key)) {
				return void 0;
			}

			var entry = key[this._id];
			if (entry && entry[0] === key) {
				return entry[1];
			}

			return void 0;
		});

		// ECMA-262 23.3.3.4 WeakMap.prototype.has(key)
		defineProperty(WeakMap.prototype, 'has', function (key) {
			checkInstance(this, 'has');

			if (!isObject(key)) {
				return false;
			}

			var entry = key[this._id];
			if (entry && entry[0] === key) {
				return true;
			}

			return false;
		});

		// ECMA-262 23.3.3.5 WeakMap.prototype.set(key, value)
		defineProperty(WeakMap.prototype, 'set', function (key, value) {
			checkInstance(this, 'set');

			if (!isObject(key)) {
				throw new TypeError('Invalid value used as weak map key');
			}

			var entry = key[this._id];
			if (entry && entry[0] === key) {
				entry[1] = value;
				return this;
			}

			defineProperty(key, this._id, [key, value]);
			return this;
		});


		function checkInstance(x, methodName) {
			if (!isObject(x) || !hasOwnProperty.call(x, '_id')) {
				throw new TypeError(
					methodName + ' method called on incompatible receiver ' +
					typeof x
				);
			}
		}

		function genId(prefix) {
			return prefix + '_' + rand() + '.' + rand();
		}

		function rand() {
			return Math.random().toString().substring(2);
		}


		defineProperty(WeakMap, '_polyfill', true);
		return WeakMap;
	})();


	function isObject(x) {
		return Object(x) === x;
	}

})(
	typeof self !== 'undefined' ? self :
	typeof window !== 'undefined' ? window :
	// eslint-disable-next-line no-undef
	typeof global !== 'undefined' ? global : this
);
