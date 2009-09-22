/* ComboWidget.js

	Purpose:
		
	Description:
		
	History:
		Tue Mar 31 14:15:39     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.ComboWidget = zk.$extends(zul.inp.InputWidget, {
	_buttonVisible: true,

	$define: {
		buttonVisible: function (v) {
			var n = this.$n('btn');
			if (n) {
				v ? jq(n).show() : jq(n).hide();
				this.onSize();
			}
		},
		autodrop: null
	},

	onSize: _zkf = function () {
		var width = this.getWidth();
		if (!width || width.indexOf('%') != -1)
			this.getInputNode().style.width = '';
		this.syncWidth();
		this._auxb.fixpos();
	},
	onShow: _zkf,
	onFloatUp: function (ctl) {
		if (jq(this.$n('pp')).is(':animated') || (!this._inplace && !this.isOpen()))
			return;
		var wgt = ctl.origin;
		if (!zUtl.isAncestor(this, wgt)) {
			if (this.isOpen())
				this.close({sendOnOpen: true});
			if (this._inplace) {
				var n = this.$n(),
					inplace = this.getInplaceCSS();
				
				if (jq(n).hasClass(inplace)) return;
				
				n.style.width = jq.px(zk(n).revisedWidth(n.offsetWidth));
				jq(this.getInputNode()).addClass(inplace);
				jq(n).addClass(inplace);
				this.onSize();
				n.style.width = this.getWidth() || '';
			}
		}
	},

	setOpen: function (open, opts) {
		if (open) this.open(opts);
		else this.close(opts)
	},
	isOpen: function () {
		return this._open;
	},
	open: function (opts) {
		if (this._open) return;
		this._open = true;
		if (opts && opts.focus)
			this.focus();

		var pp = this.$n('pp'),
			inp = this.getInputNode();
		if (!pp) return;

		zWatch.fire('onFloatUp', this); //notify all
		this.setTopmost();

		var ppofs = this.getPopupSize_(pp);
		pp.style.width = ppofs[0];
		pp.style.height = "auto";
		pp.style.zIndex = 88000; //on-top of everything

		var pp2 = this.$n("cave");
		if (pp2) pp2.style.width = pp2.style.height = "auto";

		pp.style.position = "absolute"; //just in case
		pp.style.display = "block";

		// throw out
		pp.style.visibility = "hidden";
		pp.style.left = "-10000px";

		//FF: Bug 1486840
		//IE: Bug 1766244 (after specifying position:relative to grid/tree/listbox)
		//NOTE: since the parent/child relation is changed, new listitem
		//must be inserted into the popup (by use of uuid!child) rather
		//than invalidate!!
		var $pp = zk(pp);
		$pp.makeVParent();

		// throw in
		pp.style.left = "";
		this._fixsz(ppofs);//fix size
		$pp.position(inp, "after_start");
		pp.style.display = "none";
		pp.style.visibility = "";

		zk(pp).slideDown(this, {afterAnima: this._afterSlideDown});

		//FF issue:
		//If both horz and vert scrollbar are visible:
		//a row might be hidden by the horz bar.
		if (zk.gecko) {
			var rows = pp2 ? pp2.rows: null;
			if (rows) {
				var gap = pp.offsetHeight - pp.clientHeight;
				if (gap > 10 && pp.offsetHeight < 150) { //scrollbar
					var hgh = 0;
					for (var j = rows.length; j--;)
						hgh += rows[j].offsetHeight;
					pp.style.height = (hgh + 20) + "px";
						//add the height of scrollbar (18 is an experimental number)
				}
			}
		}

		if (!this._shadow)
			this._shadow = new zk.eff.Shadow(pp,
				{left: -4, right: 4, top: -2, bottom: 3, stackup: (zk.useStackup === undefined ? zk.ie6_: zk.useStackup)});

		if (opts && opts.sendOnOpen)
			this.fire('onOpen', {open:true, value: inp.value});
	},
	_afterSlideDown: function (n) {
		zWatch.fireDown("onShow", this);
		if (this._shadow) this._shadow.sync();
	},
	close: function (opts) {
		if (!this._open) return;
		this._open = false;
		if (opts && opts.focus)
			this.focus();

		var pp = this.$n('pp');
		if (!pp) return;

		pp.style.display = "none";
		zk(pp).undoVParent();
		if (this._shadow) {
			this._shadow.destroy();
			this._shadow = null;
		}
		var n = this.$n('btn');
		if (n) jq(n).removeClass(this.getZclass() + '-btn-over');

		if (opts && opts.sendOnOpen)
			this.fire('onOpen', {open:false, value: this.getInputNode().value});

		zWatch.fireDown("onHide", this);
	},
	_fixsz: function (ppofs) {
		var pp = this.$n('pp');
		if (!pp) return;

		var pp2 = this.$n('cave');
		if (ppofs[1] == "auto" && pp.offsetHeight > 250) {
			pp.style.height = "250px";
		} else if (pp.offsetHeight < 10) {
			pp.style.height = "10px"; //minimal
		}

		if (ppofs[0] == "auto") {
			var cb = this.$n();
			if (pp.offsetWidth < cb.offsetWidth) {
				pp.style.width = cb.offsetWidth + "px";
				if (pp2) pp2.style.width = "100%";
					//Note: we have to set width to auto and then 100%
					//Otherwise, the width is too wide in IE
			} else {
				var wd = jq.innerWidth() - 20;
				if (wd < cb.offsetWidth) wd = cb.offsetWidth;
				if (pp.offsetWidth > wd) pp.style.width = wd;
			}
		}
	},

	dnPressed_: zk.$void, //function (evt) {}
	upPressed_: zk.$void, //function (evt) {}
	otherPressed_: zk.$void, //function (evt) {}
	enterPressed_: function (evt) {
		this.close({sendOnOpen:true});
		this.updateChange_();
		evt.stop();
	},
	escPressed_: function (evt) {
		this.close({sendOnOpen:true});
		evt.stop();
	},

	/** Returns [width, height] for the popup if specified by user.
	 * Default: ['auto', 'auto']
	 */
	getPopupSize_: function (pp) {
		return ['auto', 'auto'];
	},

	//super
	getInputNode: function () {
		return this.$n('real');
	},
	syncWidth: function () {
		var node = this.$n();
		if (!zk(node).isRealVisible() || (!this._inplace && !node.style.width))
			return;
		
		if (this._buttonVisible && this._inplace) {
			if (!node.style.width) {
				var $n = jq(node),
					inc = this.getInplaceCSS();
				$n.removeClass(inc);
				if (zk.opera)
					node.style.width = jq.px(zk(node).revisedWidth(node.clientWidth) + zk(node).borderWidth());
				else
					node.style.width = jq.px(zk(node).revisedWidth(node.offsetWidth));
				$n.addClass(inc);
			}
		} 
		var width = zk.opera ? zk(node).revisedWidth(node.clientWidth) + zk(node).borderWidth()
							 : zk(node).revisedWidth(node.offsetWidth),
			btn = this.$n('btn'),
			inp = this.getInputNode();
		inp.style.width = jq.px(zk(inp).revisedWidth(width - (btn ? btn.offsetWidth : 0)));
	},
	doFocus_: function (evt) {
		var n = this.$n();
		if (this._inplace)
			n.style.width = jq.px(zk(n).revisedWidth(n.offsetWidth));
			
		this.$supers('doFocus_', arguments);

		if (this._inplace) {
			if (jq(n).hasClass(this.getInplaceCSS())) {
				jq(n).removeClass(this.getInplaceCSS());
				this.onSize();
			}
		}
		if (!this._shallClose && this._readonly && !this.isOpen())
			this.open({focus:true,sendOnOpen:true});
		this._shallClose = false;
	},
	doBlur_: function (evt) {
		var n = this.$n();
		if (this._inplace && this._inplaceout) {
			n.style.width = jq.px(zk(n).revisedWidth(n.offsetWidth));
		}
		this.$supers('doBlur_', arguments);
		if (this._inplace && this._inplaceout) {
			jq(n).addClass(this.getInplaceCSS());
			this.onSize();
			n.style.width = this.getWidth() || '';
		}
	},
	afterKeyDown_: function (evt) {
		if (this._inplace)
			jq(this.$n()).toggleClass(this.getInplaceCSS(),  evt.keyCode == 13 ? null : false);
			
		this.$supers('afterKeyDown_', arguments);
	},
	bind_: function () {
		this.$supers('bind_', arguments);

		var btn = this.$n('btn'),
			inp = this.getInputNode();
			
		if (this._inplace)
			jq(inp).addClass(this.getInplaceCSS());
			
		if (btn) {
			this._auxb = new zul.Auxbutton(this, btn, inp);
			this.domListen_(btn, 'onClick', '_doBtnClick');
		}
		//this.syncWidth();
		zWatch.listen({onSize: this, onShow: this, onFloatUp: this});
	},
	unbind_: function () {
		this.close();

		var btn = this.$n('btn');
		if (btn) {
			this._auxb.cleanup();
			this._auxb = null;
			this.domUnlisten_(btn, 'onClick', '_doBtnClick');
		}

		zWatch.unlisten({onSize: this, onShow: this, onFloatUp: this});


		this.$supers('unbind_', arguments);
	},
	_doBtnClick: function (evt) {
		if (!this._disabled) {		
			if (this._open) this.close({focus:true,sendOnOpen:true});
			else this.open({focus:true,sendOnOpen:true});	
		}
		evt.stop();
	},
	doKeyDown_: function (evt) {
		this._doKeyDown(evt);
		if (!evt.stopped)
			this.$supers('doKeyDown_', arguments);
	},
	doClick_: function (evt) {
		if (evt.domTarget == this.$n('pp'))
			this.close({focus:true, sendOnOpen:true});
		this.$supers('doClick_', arguments);
	},
	_doKeyDown: function (evt) {
		var keyCode = evt.keyCode,
			bOpen = this._open;
		if (keyCode == 9 || (zk.safari && keyCode == 0)) { //TAB or SHIFT-TAB (safari)
			if (bOpen) this.close({sendOnOpen:true});
			return;
		}

		if (evt.altKey && (keyCode == 38 || keyCode == 40)) {//UP/DN
			if (bOpen) this.close({sendOnOpen:true});
			else this.open({sendOnOpen:true});

			//FF: if we eat UP/DN, Alt+UP degenerate to Alt (select menubar)
			var opts = {propagation:true};
			if (zk.ie) opts.dom = true;
			evt.stop(opts);
			return;
		}

		//Request 1537962: better responsive
		if (bOpen && (keyCode == 13 || keyCode == 27)) { //ENTER or ESC
			if (keyCode == 13) this.enterPressed_(evt);
			else this.escPressed_(evt);
			return;
		}

		if (keyCode == 18 || keyCode == 27 || keyCode == 13
		|| (keyCode >= 112 && keyCode <= 123)) //ALT, ESC, Enter, Fn
			return; //ignore it (doc will handle it)

		if (this._autodrop && !bOpen)
			this.open({sendOnOpen:true});

		if (keyCode == 38) this.upPressed_(evt);
		else if (keyCode == 40) this.dnPressed_(evt);
		else this.otherPressed_(evt);
	},
	onChildAdded_: _zkf = function (child) {
		if (this._shadow) this._shadow.sync();
	},
	onChildRemoved_: _zkf,
	onChildVisible_: _zkf
});
