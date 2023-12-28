/* rating.js

	Purpose:

	Description:

	History:
		Thu Jul 12 10:24:21 CST 2018, Created by wenninghsu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
function rating$mold$(out) {

	var sclass = this.getIconSclass(),
		isVert = 'vertical' == this._orient;

	out.push('<div', this.domAttrs_(), ' role="slider">');
	for (var i = 0; i < this._max; i++) {
		out.push('<i class="', this.$s('icon'), ' ', sclass ? zUtl.encodeXML(sclass) : 'z-icon-star');
		if (isVert) {
			if (this._max - 1 - i < this._rating)
				out.push(' ', this.$s('selected'));
		} else {
			if (i < this._rating)
				out.push(' ', this.$s('selected'));
		}
		if (this._disabled)
			out.push(' ', this.$s('disabled'));
		if (this._readonly)
			out.push(' ', this.$s('readonly'));
		out.push('" aria-hidden="true"></i>');
	}
	out.push('</div>');

}