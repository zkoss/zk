/* Rating.js

	Purpose:

	Description:

	History:
		Thu Jul 12 10:24:21 CST 2018, Created by wenninghsu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/

/**
 * An icon based rating component.
 *
 * @author wenninghsu
 * @since 8.6.0
 */
zul.wgt.Rating = zk.$extends(zul.Widget, {
	_symbol: '\u2605',
	_orient: 'horizontal',
	_rating: 0,
	_cancelable: true,
	_max: 5,
	_disabled: false,

	$define: {
		/**
		 * Sets whether this widget is disabled
		 * @param boolean disabled
		 */
		/**
		 * Returns whether this widget is disabled
		 * @returns boolean
		 */
		disabled: function (disabled) {
			if (this.desktop) {
				this._toggleClass('disabled', disabled);
			}
		},
		/**
		 * Sets whether this widget is readonly
		 * @param boolean readonly
		 */
		/**
		 * Returns whether this widget is readonly
		 * @returns boolean
		 */
		readonly: function (readonly) {
			if (this.desktop) {
				this._toggleClass('readonly', readonly);
			}
		}
	},
	bind_: function () {
		this.$supers(zul.wgt.Rating, 'bind_', arguments);
		for (var i = 1; i <= this._max; i++) {
			this.domListen_(this.$n('a-' + i), 'onMouseOver', '_doMouseOver').domListen_(this.$n('a-' + i), 'onMouseOut', '_doMouseOut');
		}
	},
	unbind_: function () {
		for (var i = 1; i <= this._max; i++) {
			this.domUnlisten_(this.$n('a-' + i), 'onMouseOver', '_doMouseOver').domUnlisten_(this.$n('a-' + i), 'onMouseOut', '_doMouseOut');
		}
		this.$supers(zul.wgt.Rating, 'unbind_', arguments);
	},
	_toggleClass: function (name, toggle) {
		var rating = this._rating;
		for (var i = 1; i <= rating; i++) {
			jq(this.$n('a-' + i)).toggleClass(this.$s(name + '-selected'), toggle).toggleClass(this.$s('selected'), !toggle);
		}
		for (var i = rating + 1; i <= this._max; i++) {
			jq(this.$n('a-' + i)).toggleClass(this.$s(name), toggle);
		}
	},
	/**
	 * Sets the rating.
	 * @param int rating
	 */
	setRating: function (rating) {
		if (this.desktop) {
			for (var i = 1; i <= rating; i++) {
				jq(this.$n('a-' + i)).addClass(this.$s('selected'));
			}
			for (var i = rating + 1; i <= this._rating; i++) {
				jq(this.$n('a-' + i)).removeClass(this.$s('selected'));
			}
		}
		this._rating = rating;
	},
	/**
	 * Returns the rating.
	 * @returns int
	 */
	getRating: function () {
		return this._rating;
	},
	/**
	 * Sets the symbols.
	 * @param String symbol unicode or z-icon prefixed Font awesome icon string.
	 */
	setSymbol: function (symbol) {
		if (this.desktop) {
			var oldSymbol = this._symbol;
			for (var i = 1; i <= this._max; i++) {
				if (oldSymbol.startsWith('z-icon-')) {
					jq(this.$n('a-' + i)).removeClass(oldSymbol);
				} else {
					this.$n('a-' + i).innerHTML = '';
				}
				if (symbol.startsWith('z-icon-')) {
					jq(this.$n('a-' + i)).addClass(symbol);
				} else {
					this.$n('a-' + i).innerHTML = symbol;
				}
			}
		}
		this._symbol = symbol;
	},
	/**
	 * Returns the symbol.
	 * @returns String unicode or z-icon prefixed Font awesome icon string.
	 */
	getSymbol: function () {
		return this._symbol;
	},
	doSelect_: function (evt) {
		if (this._disabled || this._readonly)
			return;
		this._changeRating(evt);
	},
	_changeRating: function (evt) {
		var id = evt.domTarget.id,
			oldRating = this._rating,
			rating = parseInt(id.substring(id.lastIndexOf('-') + 1));
		if (this._cancelable && oldRating == rating) {
			for (var i = 1; i <= rating; i++) {
				jq(this.$n('a-' + i)).removeClass(this.$s('selected'));
				jq(this.$n('a-' + i)).removeClass(this.$s('hover'));
			}
			this._rating = 0;
		} else {
			for (var i = 1; i <= rating; i++) {
				jq(this.$n('a-' + i)).addClass(this.$s('selected'));
			}
			this._rating = rating;
		}
		this.fire('onChange', {rating: this._rating});
	},
	_doMouseOver: function (evt) {
		if (this._disabled || this._readonly)
			return;
		if (this._rating > 0) {
			for (var i = 1; i <= this._rating; i++) {
				jq(this.$n('a-' + i)).removeClass(this.$s('selected'));
			}
		}
		var n = evt.domTarget,
			id = n.id,
			hoveredIndex = parseInt(id.substring(id.lastIndexOf('-') + 1));
		for (var i = 1; i <= hoveredIndex; i++) {
			jq(this.$n('a-' + i)).addClass(this.$s('hover'));
		}
	},
	_doMouseOut: function (evt) {
		if (this._disabled || this._readonly)
			return;
		var n = evt.domTarget,
			id = n.id,
			hoveredIndex = parseInt(id.substring(id.lastIndexOf('-') + 1));
		for (var i = 1; i <= hoveredIndex; i++) {
			jq(this.$n('a-' + i)).removeClass(this.$s('hover'));
		}
		if (this._rating > 0) {
			for (var i = 1; i <= this._rating; i++) {
				jq(this.$n('a-' + i)).addClass(this.$s('selected'));
			}
		}
	},
	doKeyDown_: function (evt) {
		if (this._disabled || this._readonly)
			return;
		var keyCode = evt.keyCode,
			id = evt.domTarget.id,
			oldRating = this._rating,
			rating = parseInt(id.substring(id.lastIndexOf('-') + 1));
		if (keyCode == 32 || keyCode == 13) {
			for (var i = rating; i <= oldRating; i++) {
				jq(this.$n('a-' + i)).removeClass(this.$s('selected'));
			}
			this._changeRating(evt);
			evt.stop();
		}
	}
});