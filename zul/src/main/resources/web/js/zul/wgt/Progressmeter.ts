/* Progressmeter.ts

	Purpose:

	Description:

	History:
		Thu May 14 10:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A progress meter is a bar that indicates how much of a task has been completed.
 *
 * <p>Default {@link #getZclass}: z-progressmeter.
 */
zul.wgt.Progressmeter = zk.$extends(zul.Widget, {
	_value: 0,
	_indeterminate: false,
	_indeterminateAnimation: false,

	$define: {
		/** Returns the current value of the progress meter.
		 * @return int
		 */
		/** Sets the current value of the progress meter.
		 * <p>Range: 0~100.
		 * @param int value
		 */
		value: function () {
			if (this.$n())
				this._fixImgWidth();
		},
		/** Returns the indeterminate state of the progress meter.(default false)
		 * @return boolean
		 */
		/** Sets the indeterminate state of the progress meter.
		 * @param boolean indeterminate
		 */
		indeterminate: function (indeterminate) {
			if (this.$n()) {
				jq(this.$n()).toggleClass(this.$s('indeterminate'), indeterminate);
				this._handleIndeterminateAnimation();
			}
		}
	},

	//super//
	_fixImgWidth: _zkf = function () {
		var n = this.$n(),
			img = this.$n('img');
		if (img) {
			//B70-ZK-2453 remember to add brackets
			if (zk(n).isRealVisible() && !this._indeterminateAnimation) { //Bug 3134159
				var $img = jq(img);
				$img.animate({
					width: this._value + '%'
				}, { duration: $img.zk.getAnimationSpeed('slow'), queue: false, easing: 'linear' }); //ZK-4079: progressmeter animation not catching up with actual value
			}
		}
	},
	_handleIndeterminateAnimation: zk.ie9 ? function () { // ZK-3629: for ie9 indetermination animation
		var $img = jq(this.$n('img'));
		if (this._indeterminate) {
			$img.css({width: '50%'});
			this._startIndeterminateAnimation($img);
			this._indeterminateAnimation = true;
		} else if (this._indeterminateAnimation) {
			$img.stop(true);
			this._indeterminateAnimation = false;
			this._fixImgWidth();
		}
	} : zk.$void,
	_startIndeterminateAnimation: function (target) { // ZK-3629: for ie9 indetermination animation
		var self = this;
		target.css({left: '-100%'});
		target.animate({
			left: '100%',
		}, 1500, 'linear', function () {
			self._startIndeterminateAnimation(target);
		});
	},
	onSize: _zkf,
	bind_: function () {//after compose
		this.$supers(zul.wgt.Progressmeter, 'bind_', arguments);
		this._fixImgWidth(this._value);
		this._handleIndeterminateAnimation();
		zWatch.listen({onSize: this});
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this});
		this.$supers(zul.wgt.Progressmeter, 'unbind_', arguments);
	},
	setWidth: function (val) {
		this.$supers('setWidth', arguments);
		this._fixImgWidth();
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			if (this._indeterminate)
				scls += ' ' + this.$s('indeterminate');
		}
		return scls;
	}
});

