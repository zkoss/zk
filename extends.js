/* extends.js

	Purpose:

	Description:

	History:
		1:02 PM 2023/8/21, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
var extendStatics = function(d, b) {
	extendStatics = Object.setPrototypeOf ||
		({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
		function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
	return extendStatics(d, b);
};

// refer tslib#__extends
export default function (d, b) {
	const len = Object.keys(d.prototype).length; // Potix: Jumper Chen adds to support `$supers(foo.Bar, 'bind_')`
	extendStatics(d, b);
	function __() { this.constructor = d; }
	d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
	d.prototype._$super = len === 0 && b.prototype._$super ? b.prototype._$super : b.prototype; // Potix: Jumper Chen adds to support `$supers(foo.Bar, 'bind_')`
}