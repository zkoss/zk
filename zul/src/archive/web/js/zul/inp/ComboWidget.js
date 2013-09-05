/* ComboWidget.js

	Purpose:
		
	Description:
		
	History:
		Tue Mar 31 14:15:39     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
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
			zul.inp.RoundUtl.buttonVisible(this, v);
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
	onSize: function () {
		zul.inp.RoundUtl.onSize(this);
	},
	
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
		if ((opts.rtags.onOpen || opts.rtags.onChanging) && this.isOpen()) {
			if (zk.animating()) {
				var self = this;
				setTimeout(function() {self.onResponse(ctl, opts);}, 50);
				return;
			}
			var pp = this.getPopupNode_(),
				pz = this.getPopupSize_(pp);
			pp.style.height = pz[1];
			
			// Bug 2941343, 2936095, and 3189142
			if (zk.ie8)
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
		if (this.isRealVisible()) {
			if (open) this.open(opts);
			else this.close(opts);
		}
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

		this.setFloating_(true, {node:pp});
		zWatch.fire('onFloatUp', this); //notify all
		var topZIndex = this.setTopmost();

		var ppofs = this.getPopupSize_(pp);
		pp.style.width = ppofs[0];
		pp.style.height = 'auto';
		pp.style.zIndex = topZIndex > 0 ? topZIndex : 1 ; //on-top of everything

		var pp2 = this.getPopupNode_(true);
		if (pp2) pp2.style.width = pp2.style.height = 'auto';

		pp.style.position = 'absolute'; //just in case
		pp.style.display = 'block';

		// throw out
		pp.style.visibility = 'hidden';
		pp.style.left = '-10000px';

		//FF: Bug 1486840
		//IE: Bug 1766244 (after specifying position:relative to grid/tree/listbox)
		//NOTE: since the parent/child relation is changed, new listitem
		//must be inserted into the popup (by use of uuid!child) rather
		//than invalidate!!
		var $pp = zk(pp);
		$pp.makeVParent();
		zWatch.fireDown('onVParent', this);
		
		// B50-ZK-859: need to carry out min size here
		if (this.presize_()) 
			ppofs = this.getPopupSize_(pp);
		
		// throw in
		pp.style.left = '';
		this._fixsz(ppofs);//fix size
		
		// given init position
		$pp.position(inp, 'after_start');	
		this._shallSyncPopupPosition = false;
		
		// B65-ZK-1588: bandbox popup should drop up 
		//   when the space between the bandbox and the bottom of browser is not enough  
		var top = jq(pp).position().top + (zk.chrome ? 1 : 0), // chrome alignement issue: -1px margin-top
			realtop = zk.ie > 9 ? Math.round(top) : top,
			after = jq(inp).position().top + zk(inp).offsetHeight(),
			realafter = zk.ie > 9 ? Math.round(after) : after;
		
		if (realtop < realafter)
			$pp.position(inp, 'before_start');	
			this._shallSyncPopupPosition = true;
		pp.style.display = 'none';
		pp.style.visibility = '';
		this.slideDown_(pp);

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
					pp.style.height = (hgh + 20) + 'px';
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
	/**
	 * Extra handling for min size of popup widget. Return true if size is affected.
	 */
	presize_: zk.$void,
	/** Slides down the drop-down list.
	 * <p>Default: <code>zk(pp).slideDown(this, {afterAnima: this._afterSlideDown});</code>
	 * @param DOMElement pp the DOM element of the drop-down list.
	 * @since 5.0.4
	 */
	slideDown_: function (pp) {
		zk(pp).slideDown(this, {afterAnima: this._afterSlideDown, duration: 100});
	},
	/** Slides up the drop-down list.
	 * <p>Default: <code>pp.style.display = "none";</code><br/>
	 * In other words, it just hides it without any animation effect.
	 * @param DOMElement pp the DOM element of the drop-down list.
	 * @since 5.0.4
	 */
	slideUp_: function (pp) {
		pp.style.display = 'none';
	},

	zsync: function () {
		this.$supers('zsync', arguments);
		if (!zk.css3 && this.isOpen() && this._shadow)
			this._shadow.sync();
	},
	_afterSlideDown: function (n) {
		if (!this.desktop) {
			//Bug 3035847: close (called by unbind) won't remove popup when animating
			zk(n).undoVParent(); //no need to fire onVParent since it will be removed
			jq(n).remove();
		}
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
		return inner ? this.$n('cave'): this.$n('pp');
	},

	/** Closes the list of combo items ({@link Comboitem} if it was
	 * dropped down.
	 * It is the same as setOpen(false).
	 * @param Map opts the options.
	 */
	close: function (opts) {
		if (!this._open) return;

		var self = this;
		if (zk.animating()) {
			setTimeout(function() {self.close(opts);}, 50);
			return;
		}
		this._open = false;
		if (opts && opts.focus)
			this.focus();
		
		var pp = this.getPopupNode_();
		if (!pp) return;

		this.setFloating_(false);
		zWatch.fireDown('onHide', this);
		this.slideUp_(pp);

		zk.afterAnimate(function() {
			zk(pp).undoVParent();
			zWatch.fireDown('onVParent', self);
		}, -1);
		
		if (this._shadow) {
			this._shadow.destroy();
			this._shadow = null;
		}

		if (opts && opts.sendOnOpen)
			this.fire('onOpen', {open:false, value: this.getInputNode().value}, {rtags: {onOpen: 1}});

	},
	_fixsz: function (ppofs) {
		var pp = this.getPopupNode_();
		if (!pp) return;

		var pp2 = this.getPopupNode_(true);
		if (ppofs[1] == 'auto' && pp.offsetHeight > 350) {
			pp.style.height = '350px';
		} else if (pp.offsetHeight < 10) {
			pp.style.height = '10px'; //minimal
		}

		if (ppofs[0] == 'auto') {
			var cb = this.$n();
			if (pp.offsetWidth < cb.offsetWidth) {
				pp.style.width = zk(pp).revisedWidth(cb.offsetWidth) + 'px';
				if (pp2) pp2.style.width = '100%';
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
	/** Called by {@link #redraw_} to redraw popup.
	 * <p>Default: does nothing
	 *  @param Array out an array of HTML fragments.
	 */
	redrawpp_: function (out) {
	},
	beforeParentMinFlex_: function (attr) { //'w' for width or 'h' for height
		if ('w' == attr)
			zul.inp.RoundUtl.syncWidth(this, this.$n('btn'));
	},
	doFocus_: function (evt) {
		this.$supers('doFocus_', arguments);

		zul.inp.RoundUtl.doFocus_(this);
	},
	doBlur_: function (evt) {
		this.$supers('doBlur_', arguments);
		zul.inp.RoundUtl.doBlur_(this);
	},
	afterKeyDown_: function (evt,simulated) {
		if (!simulated && this._inplace)
			jq(this.$n()).toggleClass(this.getInplaceCSS(),  evt.keyCode == 13 ? null : false);
			
		return this.$supers('afterKeyDown_', arguments);
	},
	bind_: function () {
		this.$supers(zul.inp.ComboWidget, 'bind_', arguments);
		var btn, inp = this.getInputNode();
			
		if (btn = this.$n('btn')) {
			this.domListen_(btn, zk.android ? 'onTouchstart' : 'onClick', '_doBtnClick');
		}
		
		zWatch.listen({onSize: this, onFloatUp: this, onResponse: this});
		if (!zk.css3) jq.onzsync(this);
	},
	unbind_: function () {
		this.close();

		var btn = this.$n('btn');
		if (btn) {
			this.domUnlisten_(btn, zk.android ? 'onTouchstart' : 'onClick', '_doBtnClick');
		}

		zWatch.unlisten({onSize: this, onFloatUp: this, onResponse: this});
		if (!zk.css3) jq.unzsync(this);
		
		this.$supers(zul.inp.ComboWidget, 'unbind_', arguments);
	},
	inRoundedMold: function () {
		return true;
	},
	_doBtnClick: function (evt) {
		if (!this._buttonVisible) return;
		if (!this._disabled && !zk.animating()) {		
			if (this._open) this.close({focus:zul.inp.InputCtrl.isPreservedFocus(this),sendOnOpen:true});
			else this.open({focus: zul.inp.InputCtrl.isPreservedFocus(this),sendOnOpen:true});	
		}
		if (zk.ios) { //Bug ZK-1313: keep window offset information before virtual keyboard opened on ipad
			this._windowX = window.pageXOffset;
			this._windowY = window.pageYOffset;
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
					focus: zul.inp.InputCtrl.isPreservedFocus(this),
					sendOnOpen: true
				});
			else if (this._readonly && !this.isOpen() && this._buttonVisible)
				this.open({
					focus: zul.inp.InputCtrl.isPreservedFocus(this),
					sendOnOpen: true
				});
			this.$supers('doClick_', arguments);
		}
	},
	_doKeyDown: function (evt) {
		var keyCode = evt.keyCode,
			bOpen = this._open;
		if ((evt.target == this || !(evt.target.$instanceof(zul.inp.InputWidget)))// Bug ZK-475
				&& (keyCode == 9 || (zk.webkit && keyCode == 0))) { //TAB or SHIFT-TAB (safari)
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
	onChildVisible_: _zkf,
	/**
	 * Returns the icon class for this combo widget. (override by subclass only)
	 */
	getIconClass_: zk.$void,
	/** Utility to implement {@link #redraw}. 
	 *  @param Array out an array of HTML fragments.
	 */
	redraw_: _zkf = function (out) {
		var uuid = this.uuid,
			isButtonVisible = this._buttonVisible;
			
		out.push('<span', this.domAttrs_({text:true}), '><input id="',
			uuid, '-real" class="', this.$s('input'));

		if (!isButtonVisible)
			out.push(' ', this.$s('rightedge'));
		
		out.push('" autocomplete="off"',
			this.textAttrs_(), '/><a id="', uuid, '-btn" class="',
			this.$s('button'));

		if (!isButtonVisible)
			out.push(' ', this.$s('disabled'));

		out.push('"><i class="', this.$s('icon'), ' ', this.getIconClass_(),'"></i></a>');

		this.redrawpp_(out);

		out.push('</span>');
	}
}, {
	$redraw: _zkf
});