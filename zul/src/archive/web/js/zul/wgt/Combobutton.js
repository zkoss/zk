/* Combobutton.js

	Purpose:
		
	Description:
		
	History:
		Wed May 18 17:32:15     2011, Created by benbai

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function() {
	//called when user mouseout some element,
	//for prevent duplicate state change
	function _setCloseTimer (wgt) {
		if (!wgt._tidclose)
			wgt._tidclose = setTimeout(function() {
				if (!wgt._bover) {
					if (wgt._autodrop && wgt.isOpen())
						wgt.close({sendOnOpen: true});
				}
				wgt._tidclose = null;
			}, 200);
	}

	// detect whether the dom element is in the right side of combobutton 
	function _inRight (top, d) {
		for (var n = d; n && n != top; n = n.parentNode)
			if (jq.nodeName(n, 'td') && jq(n).index() == 2)
				return true;
		return false;
	}
	function _fireOnOpen (wgt, opts, o) {
		if (opts && opts.sendOnOpen)
			wgt.fire('onOpen', {open:o, value: wgt.getLabel()}, {rtags: {onOpen: 1}});
	}
	// called by open method 
	function _attachPopup(wgt, bListen) {
		// just attach if not attached
		if (!wgt._oldppclose) {
			var pp = wgt.firstChild;
			wgt._oldppclose = pp.close;
			// listen to onmouseover and onmouseout events of popup child
			if (bListen)
				wgt.domListen_(pp, "onMouseOver")
					.domListen_(pp, "onMouseOut");

			// override close function of popup widget for clear objects
			pp.close = function (opts) {
				wgt._oldppclose.apply(pp, arguments);
				_fireOnOpen(wgt, opts, false);

				if (bListen)
					wgt.domUnlisten_(pp, "onMouseOver")
						.domUnlisten_(pp, "onMouseOut");
				pp.close = wgt._oldppclose;
				delete wgt._oldppclose;
			}
		}
	}
/**
 * A combo button. A combo button consists of a button ({@link zul.wgt.Combobutton}) and
 * a popup window ({@link zul.wgt.Popup}).
 * It is similar to {@link zul.inp.Bandbox} except the input box is substituted by a button.
 * @since 6.0.0
 * <p>Default {@link #getZclass}: z-combobutton.
 */
zul.wgt.Combobutton = zk.$extends(zul.wgt.Button, {
	$define: {
		/** Returns whether to automatically drop the list if users is changing
		 * this text box.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether to automatically drop the list if users is changing
		 * this text box.
		 * @param boolean autodrop
		 */
		autodrop: null
	},
	getZclass: function () {
		var zcls = this._zclass;
		// F60-ZK-719
		return zcls ? zcls: this._isDefault() ? 'z-combobutton' : 'z-combobutton-tbbtn';
	},
	_isDefault: function () {
		return this._mold == 'default';
	},
	/** Returns whether the list of combo items is open
	 * @return boolean
	 */
	isOpen: function () {
		var pp = this.firstChild;
		return pp && pp.isOpen();
	},
	/** Drops down or closes the child popup ({@link zul.wgt.Popup})
	 * ({@link zul.menu.Menupopup}, and fire onOpen if it is called with an Event.
	 * @param boolean open
	 * @param Map opts 
	 * 	if opts.sendOnOpen exists, it will fire onOpen event.
	 * @see #open
	 * @see #close
	 */
	setOpen: function (b, opts) {
		if (!this._disabled && !zk.animating())
			// have to provide empty opts or menupopup will set sendOnOpen to true
			this[b ? 'open' : 'close'](opts || {});
	},
	renderIcon_: function (out) {
		out.push('<div class="', this.getZclass(), '-btn-img" />');
	},
	renderInner_: function (out) {
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
	},
	isTableLayout_: function () {
		return true;
	},
	unbind_: function () {
		var pp = this.firstChild.$n();
		if (pp)
			this.domUnlisten_(pp, "onMouseOver")
				.domUnlisten_(pp, "onMouseOut");
		this.$supers('unbind_', arguments);
	},
	doFocus_: function (evt) {
		if (this == evt.target)
			// not change style if mouse down in popup node
			this.$supers('doFocus_', arguments);
	},

	/** Open the dropdown widget of the Combobutton.
	 */
	open: function (opts) {
		var pp = this.firstChild;
		if (pp && !this.isOpen()) {
			if (pp.$instanceof(zul.wgt.Popup)) {
				pp.open(this.uuid, null, 'after_end', opts);
				_fireOnOpen(this, opts, true);
			}
			_attachPopup(this, !pp.$instanceof(zul.wgt.Menupopup));
		}
	},
	/** Close the dropdown widget of the Combobutton.
	 */
	close: function (opts) {
		if (this.isOpen())
			this.firstChild.close(opts);
	},

	doClick_: function (evt) {
		var d = evt.domTarget;
		// click will fired twice, one with dom target, another with undefined,
		// see _fixClick in Button.js
		if (d) {
			// open it if click on right side,
			// close it if click on both left and right side
			var open = !this.isOpen();
			if (this == evt.target)
				if (_inRight(this.$n(), d) || !open)
					this.setOpen(open, {sendOnOpen: true});
				else
					this.$supers('doClick_', arguments);
			if (this._mold == 'tbbtn')
				jq(this.$n('box')).addClass(this.getZclass() + "-over");
		}
	},
	doMouseDown_: function (evt) {
		if (this == evt.target)
			// not change style if mouse down in popup node
			this.$supers('doMouseDown_', arguments);
	},
	doMouseOver_: function (evt) {
		this._bover = true;
		if (this == evt.target) {
			// not change style and call open method if mouse over popup node
			if (this._autodrop && _inRight(this.$n(), evt.domTarget) && !this.isOpen())
				this.open({sendOnOpen: true});
			this.$supers('doMouseOver_', arguments);
		}
	},
	doMouseOut_: function (evt) {
		this._bover = false;
		_setCloseTimer(this);
		this.$supers('doMouseOut_', arguments);
	},
	_doMouseOver: function (evt) { //not zk.Widget.doMouseOver_
		// should not close popup if mouse out combobutton but over popup
		this._bover = true;
	},
	_doMouseOut: function (evt) { //not zk.Widget.doMouseOut_
		// should close it if mouse out popup
		this._bover = false;
		_setCloseTimer(this);
	},
	ignoreDescendantFloatUp_: function (des) {
		return des && des.$instanceof(zul.wgt.Popup);
	}
});
})();