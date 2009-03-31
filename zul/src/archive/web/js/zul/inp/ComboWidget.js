/* ComboWidget.js

	Purpose:
		
	Description:
		
	History:
		Tue Mar 31 14:15:39     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.ComboWidget = zk.$extends(zul.inp.InputWidget, {
	_btnVisible: true,

	isButtonVisible: function () {
		return this._btnVisible;
	},
	setButtonVisible: function (btnVisible) {
		if (this._btnVisible != btnVisible) {
			this._btnVisible = btnVisible;
			var n = this.getSubnode('btn');
			if (n)
				btnVisible ? zDom.show(n): zDom.hide(n);
		}
	},
	isAutodrop: function () {
		return this._autodrop;
	},
	setAutodrop: function (autodrop) {
		this._autodrop = autodrop;
	},

	onSize: _zkf = function () {
		this._dropb.fixpos();
	},
	onVisible: _zkf,

	isOpen: function () {
		return this._open;
	},
	open: function () {
		var pp = this.getSubnode('pp');
		if (!pp) return;
	},
	close: function () {
	},

	downPressed_: zk.$void, //function (evt) {}
	upPressed_: zk.$void, //function (evt) {}

	//super
	bind_: function () {
		this.$supers('bind_', arguments);

		var btn = this.getSubnode('btn'),
			inp = this.getSubnode('real'),
			dropb = this._dropb = new zul.Dropbutton(this, btn, inp);

		zWatch.listen("onSize", this);
		zWatch.listen("onVisible", this);
	},
	unbind_: function () {
		zWatch.unlisten("onSize", this);
		zWatch.unlisten("onVisible", this);

		this._dropb.cleanup();
		delete this._dropb;

		this.$supers('unbind_', arguments);
	},
	doKeyDown_: function (evt) {
		if (evt.domTarget == this.getSubnode('real')) {
			this._doKeyDown(evt);
			if (evt.stopped) return;
		}
		this.$supers('doKeyDown_', arguments);
	},
	_doKeyDown: function (evt) {
		var ei = evt.data,
			keyCode = ei.keyCode,
			opened = this._opened;
		if (keyCode == 9 || (zk.safari && keyCode == 0)) { //TAB or SHIFT-TAB (safari)
			if (opened) this.close();
			return;
		}

		if (ei.altKey && (keyCode == 38 || keyCode == 40)) {//UP/DN
			if (keyCode == 38) { //UP
				if (opened) this.close();
			} else {
				if (!opened) this.open();
			}

			//FF: if we eat UP/DN, Alt+UP degenerate to Alt (select menubar)
			var opts = {propagation:true};
			if (zk.ie) opts.dom = true;
			evt.stop(opts);
			return;
		}

		//Request 1537962: better responsive
		if (opened && keyCode == 13) { //ENTER
			var item = this._autoselback(); //Better usability(Bug 1633335): auto selback
			if (item && this._selId != item.uuid) {
				this._selId = item.uuid;
				this.fire('onSelect', {items: [item], reference: item, keys: ei.keys});
			}
			this.updateChange_(); //fire onChange
			return;
		}

		if (keyCode == 18 || keyCode == 27 || keyCode == 13
		|| (keyCode >= 112 && keyCode <= 123)) //ALT, ESC, Enter, Fn
			return; //ignore it (doc will handle it)

		if (keyCode == 38) this.upPressed_();
		else if (keyCode == 40) this.downPressed_();
	}
});