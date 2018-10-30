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
	_orient: 'horizontal',
	_rating: 0,
	_cancelable: true,
	_max: 5,
	_disabled: false,
	_iconSclass: 'z-icon-star',

	$define: {
		/**
		 * Sets the iconSclass.
		 * @param String sclass
		 */
		/**
		 * Returns the iconSclass.
		 * @return String
		 */
		iconSclass: function (sclass) {
			if (this.desktop) {
				this.rerender();
			}
		},
		/**
		 * Sets the rating.
		 * @param int rating
		 */
		/**
		 * Returns the rating.
		 * @return int
		 */
		rating: function (rating) {
			if (this.desktop) {
				this._toggleClass('selected', rating);
			}
		},
		/**
		 * Sets whether this widget is disabled
		 * @param boolean disabled
		 */
		/**
		 * Returns whether this widget is disabled
		 * @return boolean
		 */
		disabled: function (disabled) {
			if (this.desktop) {
				jq(this).children().toggleClass(this.$s('disabled'), disabled);
			}
		},
		/**
		 * Sets whether this widget is readonly
		 * @param boolean readonly
		 */
		/**
		 * Returns whether this widget is readonly
		 * @return boolean
		 */
		readonly: function (readonly) {
			if (this.desktop) {
				jq(this).children().toggleClass(this.$s('readonly'), readonly);
			}
		}
	},
	bind_: function () {
		this.$supers(zul.wgt.Rating, 'bind_', arguments);
		var wgt = this,
			isVertical = 'vertical' == wgt._orient;
		jq(wgt).children().each(function (i) {
			jq(this).data('rate', isVertical ? wgt._max - i : i + 1);
			wgt.domListen_(this, 'onMouseOver', '_doMouseOver').domListen_(this, 'onMouseOut', '_doMouseOut');
		});
	},
	unbind_: function () {
		var wgt = this;
		jq(wgt).children().each(function () {
			wgt.domUnlisten_(this, 'onMouseOver', '_doMouseOver').domUnlisten_(this, 'onMouseOut', '_doMouseOut');
		});
		this.$supers(zul.wgt.Rating, 'unbind_', arguments);
	},
	domClass_: function (no) {
		var sc = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			sc += ' ' + this.$s(this._orient);
		}
		return sc;
	},
	doSelect_: function (evt) {
		if (this._disabled || this._readonly)
			return;
		this._changeRating(evt);
	},
	_changeRating: function (evt) {
		var rating = jq(evt.domTarget).data('rate'),
			isCanceling = this._cancelable && this._rating == rating;
		if (isCanceling)
			jq(this).children().removeClass(this.$s('selected'));
		else
			this._toggleClass('selected', rating);
		jq(this).children().removeClass(this.$s('hover'));
		this._rating = isCanceling ? 0 : rating;
		this.fire('onChange', {rating: this._rating});
	},
	_doMouseOver: function (evt) {
		if (this._disabled || this._readonly)
			return;
		this._toggleClass('hover', jq(evt.domTarget).data('rate'));
	},
	_doMouseOut: function (evt) {
		if (this._disabled || this._readonly)
			return;
		jq(this).children().removeClass(this.$s('hover'));
	},
	_toggleClass: function (name, rate) {
		var wgt = this;
		jq(wgt).children().each(function () {
			var jqCh = jq(this);
			jqCh.toggleClass(wgt.$s(name), jqCh.data('rate') <= rate);
		});
	}
});
