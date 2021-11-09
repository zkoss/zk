/* Bandpopup.js

	Purpose:

	Description:

	History:
		Fri Apr  3 15:24:37     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * The popup that belongs to a {@link Bandbox} instance.
 *
 * <p>Developer usually listen to the onOpen event that is sent to
 * {@link Bandbox} and then creates proper components as children
 * of this component.
 *
 * <p>z-class: z-bandpopup
 */
zul.inp.Bandpopup = zk.$extends(zul.Widget, {
	bind_: function () {
		this.$supers(zul.inp.Bandpopup, 'bind_', arguments);
		jq(this.$n()).on('focusin', this.proxy(this._focusin))
			.on('focusout', this.proxy(this._focusout));
	},
	unbind_: function () {
		jq(this.$n()).off('focusout', this.proxy(this._focusout))
			.off('focusin', this.proxy(this._focusin));
		this.$supers(zul.inp.Bandpopup, 'unbind_', arguments);
	},
	_focusin: function (e) {
		this._shallClosePopup = false;
	},
	_focusout: function (e) {
		var bandbox = this.parent,
			self = this;
		if (e.relatedTarget) {
			if (bandbox && bandbox.isOpen() && !jq.isAncestor(this.$n(), e.relatedTarget))
				bandbox.close();
		} else {
			// for solving B96-ZK-4748, treechildren will rerender itself when clicking
			// the open icon, and JQ will simulate a fake focusout event without any relatedTarget.

			self._shallClosePopup = true;
			setTimeout(function () {
				if (bandbox && bandbox.isOpen() && self._shallClosePopup) {
					bandbox.close();
					self._shallClosePopup = false;
				}
			});
		}
	},
	//super
	afterChildrenMinFlex_: function (orient) {
		if (orient == 'w') {
			var bandbox = this.parent,
				pp = bandbox && bandbox.$n('pp');
			if (pp) {
				// test case is B50-ZK-859.zul
				pp.style.width = jq.px0(this._hflexsz + zk(pp).padBorderWidth());
				zk(pp)._updateProp(['width']);
			}
		}
	},
	doClick_: function (evt) {
		if (evt.domTarget == this.$n())
			this.parent.focus();
		this.$supers('doClick_', arguments);
	}
});
