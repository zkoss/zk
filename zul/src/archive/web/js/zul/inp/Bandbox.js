/* Bandbox.js

	Purpose:
		
	Description:
		
	History:
		Tue Mar 31 14:17:28     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A band box. A bank box consists of an input box ({@link Textbox} and
 * a popup window {@link Bandpopup}.
 * It is similar to {@link Combobox} except the popup window could have
 * any kind of children. For example, you could place a textbox in
 * the popup to let user search particular items.
 *
 * <p>Default {@link #getZclass}: z-bandbox.
 */
zul.inp.Bandbox = zk.$extends(zul.inp.ComboWidget, {
	//super
	getPopupSize_: function (pp) {
		var bp = this.firstChild, //bandpopup
			w, h;
		if (bp) {
			w = bp._hflex == 'min' && bp._hflexsz ? jq.px0(bp._hflexsz) : bp.getWidth();
			h = bp._vflex == 'min' && bp._vflexsz ? jq.px0(bp._vflexsz) : bp.getHeight();
		}
		return [w||'auto', h||'auto'];
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-bandbox" + (this.inRoundedMold() ? "-rounded": "");
	},
	getCaveNode: function () {
		return this.$n('pp') || this.$n();
	},
	redrawpp_: function (out) {
		out.push('<div id="', this.uuid, '-pp" class="', this.getZclass(),
		'-pp" style="display:none" tabindex="-1">');

		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
	
		out.push('</div>');
	},
	//@Override
	open: function (opts) {
		if (!this.firstChild) { 
			// ignore when <bandpopup> is absent, but event is still fired
			if (opts && opts.sendOnOpen)
				this.fire('onOpen', {open:true, value: this.getInputNode().value}, {rtags: {onOpen: 1}});
			return;
		}
		this.$supers('open', arguments);
	},
	enterPressed_: function (evt) {
		//bug 3280506: do not close when children press enter.
		if(evt.domTarget == this.getInputNode())
			this.$supers('enterPressed_', arguments);
	},
	doKeyUp_: function(evt) {
		//bug 3287082: do not fire onChanging when children typing.
		if(evt.domTarget == this.getInputNode())
			this.$supers('doKeyUp_', arguments);
	}
});
