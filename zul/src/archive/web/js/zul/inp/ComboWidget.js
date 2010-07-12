/* ComboWidget.js

	Purpose:
		
	Description:
		
	History:
		Tue Mar 31 14:15:39     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A skeletal implementation for a combo widget.
 */
zul.inp.ComboWidget = zk.$extends(zul.inp.InputWidget, {
	_buttonVisible: true,

	$define: {
		/** Returns whether the button (on the right of the textbox) is visible.
		 * <p>Default: true.
		 * @return boolean
		 */
		/** Sets whether the button (on the right of the textbox) is visible.
		 * @param boolean visible
	 	*/
		buttonVisible: function (v) {
			var n = this.$n('btn'),
				zcls = this.getZclass();
			if (n) {
				if (!this.inRoundedMold()) {
					jq(n)[v ? 'show': 'hide']();
					jq(this.getInputNode())[v ? 'removeClass': 'addClass'](zcls + '-right-edge');
				} else {
					var fnm = v ? 'removeClass': 'addClass';
					jq(n)[fnm](zcls + '-btn-right-edge');
					
					if (zk.ie6_) {						
						jq(n)[fnm](zcls + 
							(this._readonly ? '-btn-right-edge-readonly':'-btn-right-edge'));
						
						if (jq(this.getInputNode()).hasClass(zcls + "-text-invalid"))
							jq(n)[fnm](zcls + "-btn-right-edge-invalid");
					}
				}
				this.onSize();
			}
		},
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
	setWidth: function () {
		this.$supers('setWidth', arguments);
		if (this.desktop) {
			this.onSize();
		}
	},
	onSize: _zkf = function () {
		var width = this.getWidth();
		if (!width || width.indexOf('%') != -1)
			this.getInputNode().style.width = '';
		this.syncWidth();
	},
	onShow: _zkf,
	
	onFloatUp: function (ctl) {
		if (jq(this.getPopupNode_()).is(':animated') || (!this._inplace && !this.isOpen()))
			return;
		var wgt = ctl.origin;
		if (!zUtl.isAncestor(this, wgt)) {
			if (this.isOpen())
				this.close({sendOnOpen: true});
			if (this._inplace) {
				var n = this.$n(),
					inplace = this.getInplaceCSS();
				
				if (jq(n).hasClass(inplace)) return;
				
				n.style.width = jq.px0(zk(n).revisedWidth(n.offsetWidth));
				jq(this.getInputNode()).addClass(inplace);
				jq(n).addClass(inplace);
				this.onSize();
				n.style.width = this.getWidth() || '';
			}
		}
	},
	onResponse: function (ctl, opts) {
		if (opts.rtags.onOpen && this.isOpen()) {
			if (zk.animating()) {
				var self = this;
				setTimeout(function() {self.onResponse(ctl, opts);}, 50);
				return;
			}
			var pp = this.getPopupNode_(),
				pz = this.getPopupSize_(pp);
			pp.style.height = pz[1];
			
			// Bug 2941343
			if (!zk.ie6_)
				pp.style.width = pz[0];
			this._fixsz(pz);
		}
	},
	/** Drops down or closes the list of combo items ({@link Comboitem}.
	 * @param boolean open
	 * @param Map opts the options.
	 * @see #open
	 * @see #close
	 */
	setOpen: function (open, opts) {
		if (open) this.open(opts);
		else this.close(opts);
	},
	/** Returns whether the list of combo items is open
	 * @return boolean
	 */
	isOpen: function () {
		return this._open;
	},
	/** Drops down the list of combo items ({@link Comboitem}.
	 * It is the same as setOpen(true).
	 * @param Map opts the options.
	 */
	open: function (opts) {
		if (this._open) return;
		this._open = true;
		if (opts && opts.focus)
			this.focus();

		var pp = this.getPopupNode_(),
			inp = this.getInputNode();
		if (!pp) return;

		zWatch.fire('onFloatUp', this); //notify all
		var topZIndex = this.setTopmost();

		var ppofs = this.getPopupSize_(pp);
		pp.style.width = ppofs[0];
		pp.style.height = "auto";
		pp.style.zIndex = topZIndex > 0 ? topZIndex : 1 ; //on-top of everything

		var pp2 = this.getPopupNode_(true);
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
				{left: -4, right: 4, top: -2, bottom: 3});

		if (opts && opts.sendOnOpen)
			this.fire('onOpen', {open:true, value: inp.value}, {rtags: {onOpen: 1}});
	},
	zsync: function () {
		this.$supers('zsync', arguments);
		if (!zk.css3 && this.isOpen() && this._shadow)
			this._shadow.sync();
	},
	_afterSlideDown: function (n) {
		if (this._shadow) this._shadow.sync();
	},
	/** Returns the DOM element of the popup.
	 * Default: <code>inner ? this.$n("cave"): this.$n("pp")</code>.
	 * Override it if it is not the case.
	 * @param boolean inner whether to return the inner popup.
	 * ComboWidget assumes there is at least one popup and returned by
	 * <code>getPopupNode_()</code>, and there might be an inner DOM element
	 * returned by <code>getPopupNode_(true)</code>.
	 * @return DOMElement
	 * @since 5.0.4
	 */
	getPopupNode_: function (inner) {
		return inner ? this.$n("cave"): this.$n("pp");
	},

	/** Closes the list of combo items ({@link Comboitem} if it was
	 * dropped down.
	 * It is the same as setOpen(false).
	 * @param Map opts the options.
	 */
	close: function (opts) {
		if (!this._open) return;
		if (zk.animating()) {
			var self = this;
			setTimeout(function() {self.close(opts);}, 50);
			return;
		}
		this._open = false;
		if (opts && opts.focus)
			this.focus();

		var pp = this.getPopupNode_();
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
			this.fire('onOpen', {open:false, value: this.getInputNode().value}, {rtags: {onOpen: 1}});

		zWatch.fireDown("onHide", this);
	},
	_fixsz: function (ppofs) {
		var pp = this.getPopupNode_();
		if (!pp) return;

		var pp2 = this.getPopupNode_(true);
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
	/** Called when the user presses enter when this widget has the focus ({@link #focus}).
	 * <p>call the close function
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #close
	 */
	enterPressed_: function (evt) {
		this.close({sendOnOpen:true});
		this.updateChange_();
		evt.stop();
	},
	/** Called when the user presses escape key when this widget has the focus ({@link #focus}).
	 * <p>call the close function
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #close
	 */
	escPressed_: function (evt) {
		this.close({sendOnOpen:true});
		evt.stop();
	},

	/** Returns [width, height] for the popup if specified by user.
	 * Default: ['auto', 'auto']
	 * @return Array
	 */
	getPopupSize_: function (pp) {
		return ['auto', 'auto'];
	},

	/** Utility to implement {@link #redraw}. 
	 *  @param Array out an array of HTML fragments.
	 */
	redraw_: function (out) {
		var uuid = this.uuid,
			zcls = this.getZclass();
		out.push('<i', this.domAttrs_({text:true}), '><input id="',
			uuid, '-real" class="', zcls, '-inp" autocomplete="off"',
			this.textAttrs_(), '/><i id="', uuid, '-btn" class="',
			zcls, '-btn');

		if (this.inRoundedMold()) {
			if (!this._buttonVisible)
				out.push(' ', zcls, '-btn-right-edge');
			if (this._readonly)
				out.push(' ', zcls, '-btn-readonly');	
			if (zk.ie6_ && !this._buttonVisible && this._readonly)
				out.push(' ', zcls, '-btn-right-edge-readonly');
		} else if (!this._buttonVisible)
			out.push('" style="display:none');

		out.push('"></i>');

		this.redrawpp_(out);

		out.push('</i>');
	},
	/** Called by {@link #redraw_} to redraw popup.
	 * <p>Default: does nothing
	 *  @param Array out an array of HTML fragments.
	 */
	redrawpp_: function (out) {
	},

	/** Synchronizes the input element's width of this component
	 */
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
					node.style.width = jq.px0(zk(node).revisedWidth(node.clientWidth) + zk(node).borderWidth());
				else
					node.style.width = jq.px0(zk(node).revisedWidth(node.offsetWidth));
				$n.addClass(inc);
			}
		}
		var inp = this.getInputNode();
		if (zk.ie6_)			
			inp.style.width = jq.px(0);
		var width = zk.opera ? zk(node).revisedWidth(node.clientWidth) + zk(node).borderWidth()
							 : zk(node).revisedWidth(node.offsetWidth),
			btn = this.$n('btn');
		inp.style.width = jq.px0(zk(inp).revisedWidth(width - (btn ? btn.offsetWidth : 0)));
	},
	doFocus_: function (evt) {
		var n = this.$n();
		if (this._inplace)
			n.style.width = jq.px0(zk(n).revisedWidth(n.offsetWidth));
			
		this.$supers('doFocus_', arguments);

		if (this._inplace) {
			if (jq(n).hasClass(this.getInplaceCSS())) {
				jq(n).removeClass(this.getInplaceCSS());
				this.onSize();
			}
		}
	},
	doBlur_: function (evt) {
		var n = this.$n();
		if (this._inplace && this._inplaceout) {
			n.style.width = jq.px0(zk(n).revisedWidth(n.offsetWidth));
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
		this.$supers(zul.inp.ComboWidget, 'bind_', arguments);

		var btn = this.$n('btn'),
			inp = this.getInputNode();
			
		if (this._inplace)
			jq(inp).addClass(this.getInplaceCSS());
			
		if (btn) {
			this._auxb = new zul.Auxbutton(this, btn, inp);
			this.domListen_(btn, 'onClick', '_doBtnClick');
		}
		if (this._readonly && !this.inRoundedMold())
			jq(inp).addClass(this.getZclass() + '-right-edge');
		
		zWatch.listen({onSize: this, onShow: this, onFloatUp: this, onResponse: this});
		if (!zk.css3) jq.onzsync(this);
		
		this.setFloating_(true,{node:this.getPopupNode_()});
	},
	unbind_: function () {
		this.close();

		var btn = this.$n('btn');
		if (btn) {
			this._auxb.cleanup();
			this._auxb = null;
			this.domUnlisten_(btn, 'onClick', '_doBtnClick');
		}

		zWatch.unlisten({onSize: this, onShow: this, onFloatUp: this, onResponse: this});
		if (!zk.css3) jq.unzsync(this);
		
		this.$supers(zul.inp.ComboWidget, 'unbind_', arguments);
	},
	_doBtnClick: function (evt) {
		if (this.inRoundedMold() && !this._buttonVisible) return;
		if (!this._disabled && !zk.animating()) {		
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
		if (!this._disabled) {
			if (evt.domTarget == this.getPopupNode_())
				this.close({
					focus: true,
					sendOnOpen: true
				});
			else if (this._readonly && !this.isOpen() && this._buttonVisible)
				this.open({
					focus: true,
					sendOnOpen: true
				});
			this.$supers('doClick_', arguments);
		}
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
