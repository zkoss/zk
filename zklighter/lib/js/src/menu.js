_z='zul.menu';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

zPkg.load('zul.wgt');
zul.menu.Menubar = zk.$extends(zul.Widget, {
	_orient: "horizontal",
	
	getOrient: function () {
		return this._orient;
	},
	setOrient: function (orient) {
		if (this._orient != orient) {
			this._orient = orient;
			this.rerender();
		}
	},
	isAutodrop: function () {
		return this._autodrop;
	},
	setAutodrop: function (autodrop) {
		if (this._autodrop != autodrop)
			this._autodrop = autodrop;
	},
	getZclass: function () {
		return this._zclass == null ? "z-menubar" +
				("vertical" == this.getOrient() ? "-ver" : "-hor") : this._zclass;
	},
	unbind_: function () {
		this._lastTarget = null;
		this.$supers('unbind_', arguments);
	},
	insertChildHTML_: function (child, before, desktop) {
		if (before)
			zDom.insertHTMLBefore(before.getSubnode('chdextr'),
				this.encloseChildHTML_({child: child, vertical: 'vertical' == this.getOrient()}));
		else
			zDom.insertHTMLBeforeEnd(this.getNode(),
				this.encloseChildHTML_({child: child, vertical: 'vertical' == this.getOrient()}));
		
		child.bind_(desktop);
	},
	removeChildHTML_: function (child, prevsib) {
		this.$supers('removeChildHTML_', arguments);
		zDom.remove(child.uuid + '$chdextr');
	},
	encloseChildHTML_: function (opts) {
		var out = opts.out || [],
			child = opts.child,
			isVert = opts.vertical;
		if (isVert) {
			out.push('<td id="', child.uuid, '$chdextr"');
			if (child.getHeight())
				out.push(' height="', child.getHeight(), '"');
			out.push('>');
		}
		child.redraw(out);
		if (isVert)
			out.push('</tr>');
		if (!opts.out) return out.join('');
	}
});

(_zkwg=_zkpk.Menubar).prototype.className='zul.menu.Menubar';_zkmd={};
_zkmd['default']=
function (out) {
	var uuid = this.uuid;
	if ('vertical' == this.getOrient()) {
		out.push('<div', this.domAttrs_(), '><table id="', uuid, '$cave"',
				zUtl.cellps0, '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			this.encloseChildHTML_({out: out, child: w, vertical: true});
		out.push('</table></div>');
	} else {
		out.push('<div', this.domAttrs_(), '><table', zUtl.cellps0, '>',
				'<tr valign="bottom" id="', uuid, '$cave">')
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</tr></table></div>');
	}
}

zkmld(_zkwg,_zkmd);
zul.menu.Menu = zk.$extends(zul.LabelImageWidget, {
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this._image;
		if (!img) img = zAu.comURI('web/img/spacer.gif');

		img = '<img src="' + img + '" align="absmiddle" class="' + this.getZclass() + '-img"/>';
		return label ? img + ' ' + label: img;
	},
	isTopmost: function () {
		return this._topmost;
	},
	beforeParentChanged_: function (newParent) {
		this._topmost = newParent && !(newParent.$instanceof(zul.menu.Menupopup));		
	},
	getZclass: function () {
		return this._zclass == null ? "z-menu" : this._zclass;
	},
	domStyle_: function (no) {
		var style = this.$supers('domStyle_', arguments);
		return this.isTopmost() ?
			style + 'padding-left:4px;padding-right:4px;': style;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.menu.Menupopup)) 
			this.menupopup = child;
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.menupopup) 
			this.menupopup = null;
	},
	getMenubar: function () {
		for (var p = this.parent; p; p = p.parent)
			if (p.$instanceof(zul.menu.Menubar))
				return p;
		return null;
	},
	_fixBtn: function () {
		var btn = this.getSubnode('b');
		if (!btn || !btn.innerHTML.trim()) return;
		if (!this._textUtil) this._textUtil = zDom.getTextUtil();
		btn.style.width = this._textUtil.measure(btn).width + zDom.frameWidth(btn) + "px";
			// fix the gap between button and em tag for IE.
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		
		if (!this.isTopmost()) {
			var anc = this.getSubnode('a'), n = this.getNode();
			zEvt.listen(anc, "focus", this.proxy(this.domFocus_, '_fxFocus'));
			zEvt.listen(anc, "blur", this.proxy(this.domBlur_, '_fxBlur'));
			zEvt.listen(n, "mouseover", this.proxy(this._doMouseOver, '_fxMouseOver'));
			zEvt.listen(n, "mouseout", this.proxy(this._doMouseOut, '_fxMouseOut'));
		} else {
			if (zk.ie) this._fixBtn();
			
			var anc = this.getSubnode('a');
			zEvt.listen(anc, "mouseover", this.proxy(this._doMouseOver, '_fxMouseOver'));
			zEvt.listen(anc, "mouseout", this.proxy(this._doMouseOut, '_fxMouseOut'));
		}
	},
	unbind_: function () {		
		if (!this.isTopmost()) {
			var anc = this.getSubnode('a'),
				n = this.getNode();
			zEvt.unlisten(anc, "focus", this._fxFocus);
			zEvt.unlisten(anc, "blur", this._fxBlur);
			zEvt.unlisten(n, "mouseover", this._fxMouseOver);
			zEvt.unlisten(n, "mouseout", this._fxMouseOut);
		} else {
			if (this._textUtil) 
				this._textUtil.destroy();
		
			this._textUtil = null;
				
			var n = this.getNode();
			zEvt.unlisten(n, "mouseover", this._fxMouseOver);
			zEvt.unlisten(n, "mouseout", this._fxMouseOut);
		}
		
		this.$supers('unbind_', arguments);		
	},
	doClick_: function (evt) {
		if (this.isTopmost() && !zDom.isAncestor(this.getSubnode('a'), evt.nativeTarget)) return;		
		zDom.addClass(this.getSubnode('a'), this.getZclass() + '-btn-seld');
		if (this.menupopup) {
			this.menupopup._shallClose = false;
			if (this.isTopmost())
				this.getMenubar()._lastTarget = this;
			if (!this.menupopup.isOpen()) this.menupopup.open();
		}
		this.fireX(evt);
	},
	_doMouseOver: function (evt) {
		if (this.$class._isActive(this)) return;
		
		var	topmost = this.isTopmost();
		if (topmost && zk.ie && !zDom.isAncestor(this.getSubnode('a'), zEvt.target(evt)))
				return; // don't activate 
		
		this.$class._addActive(this);
		if (!topmost) {
			if (this.menupopup) this.menupopup._shallClose = false;
			zWatch.fire('onFloatUp', null, this); //notify all
			if (this.menupopup && !this.menupopup.isOpen()) this.menupopup.open();
		} else {
			var menubar = this.getMenubar();
			if (this.menupopup && menubar.isAutodrop()) {
				menubar._lastTarget = this;
				this.menupopup._shallClose = false;
				zWatch.fire('onFloatUp', null, this); //notify all
				if (!this.menupopup.isOpen()) this.menupopup.open();
			} else {
				var target = menubar._lastTarget;
				if (target && target != this && menubar._lastTarget.menupopup
						&& menubar._lastTarget.menupopup.isVisible()) {
					menubar._lastTarget.menupopup.close({sendOnOpen:true});
					this.$class._rmActive(menubar._lastTarget);
					menubar._lastTarget = this;
					if (this.menupopup) this.menupopup.open();
				}
			}
		}
	},
	_doMouseOut: function (evt) {
		if (zk.ie) {
			var n = this.getSubnode('a'),
				xy = zDom.revisedOffset(n),
				p = zEvt.pointer(evt),
				x = p[0],
				y = p[1],
				diff = this.isTopmost() ? 1 : 0,
				vdiff = this.isTopmost() && 'vertical' == this.parent.getOrient() ? 1 : 0;
			if (x - diff > xy[0] && x <= xy[0] + n.offsetWidth && y - diff > xy[1] &&
					y - vdiff <= xy[1] + n.offsetHeight)
				return; // don't deactivate;
		}
		var	topmost = this.isTopmost();
		if (topmost) {
			if (this.menupopup && this.getMenubar().isAutodrop()) {
				this.$class._rmActive(this);
				if (this.menupopup.isOpen()) this.menupopup._shallClose = true;
				zWatch.fire('onFloatUp', {
					timeout: 10
				}, this); //notify all
			}
		} else if (this.menupopup && !this.menupopup.isOpen())
			this.$class._rmActive(this);
	}
}, {
	_isActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getSubnode('a') : wgt.getNode(),
			cls = wgt.getZclass() + (top ? '-btn-over' : '-over');
		return zDom.hasClass(n, cls);
	},
	_addActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getSubnode('a') : wgt.getNode(),
			cls = wgt.getZclass() + (top ? '-btn-over' : '-over');
		zDom.addClass(n, cls);
		if (!top && wgt.parent.parent.$instanceof(zul.menu.Menu))
			this._addActive(wgt.parent.parent);
	},
	_rmActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getSubnode('a') : wgt.getNode(),
			cls = wgt.getZclass() + (top ? '-btn-over' : '-over');
		zDom.rmClass(n, cls);
	}
});

(_zkwg=_zkpk.Menu).prototype.className='zul.menu.Menu';_zkmd={};
_zkmd['default']=
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass();
		
	if (this.isTopmost()) {
		out.push('<td align="left"', this.domAttrs_(), '><table id="', uuid,
				'$a"', zUtl.cellps0, ' class="', zcls, '-btn');
		
		if (this.getImage()) {
			out.push(' ', zcls, '-btn');
			if (this.getLabel())
				out.push('-text');
			
			out.push('-img');			
		}
		
		out.push('" style="width: auto;"><tbody><tr><td class="', zcls,
				'-btn-l"><i>&nbsp;</i></td><td class="', zcls,
				'-btn-m"><em unselectable="on"><button id="', uuid,
				'$b" type="button" class="', zcls, '-btn-text"');
		if (this.getImage())
			out.push(' style="background-image:url(', this.getImage(), ')"');
			
		out.push('>', zUtl.encodeXML(this.getLabel()), '</button>');
		
		if (this.menupopup) this.menupopup.redraw(out);
		
		out.push('</em></td><td class="', zcls, '-btn-r"><i>&nbsp;</i></td></tr></tbody></table></td>');
		
	} else {
		out.push('<li', this.domAttrs_(), '><a href="javascript:;" id="', uuid,
				'$a" class="', zcls, '-cnt ', zcls, '-cnt-img">', this.domContent_(), '</a>');
		if (this.menupopup) this.menupopup.redraw(out);
		
		out.push('</li>');
	}
}

zkmld(_zkwg,_zkmd);
zul.menu.Menuitem = zk.$extends(zul.LabelImageWidget, {
	_value: "",
	
	isCheckmark: function () {
		return this._checkmark;
	},
	setCheckmark: function (checkmark) {
		if (this._checkmark != checkmark) {
			this._checkmark = checkmark;
			this.rerender();
		}
	},
	setDisabled: function (disabled) {
		if (this._disabled != disabled) {
			this._disabled = disabled;
			this.rerender();
		}
	},
	isDisabled: function () {
		return this._disabled;
	},
	getValue: function () {
		return this._value;
	},
	setValue: function (value) {
		if (!value)	value = "";
		this._value = value;
	},
	isChecked: function () {
		return this._checked;
	},
	setChecked: function (checked) {
		if (this._checked != checked) {
			this._checked = checked;
			if (this._checked)
				this._checkmark = this._checked;
			var n = this.getNode();
			if (n && !this.isTopmost() && !this.getImage()) {
				var zcls = this.getZclass();
				zDom.rmClass(n, zcls + '-cnt-ck');
				zDom.rmClass(n, zcls + '-cnt-unck');
				if (this._checkmark)
					zDom.addClass(n, zcls + (this._checked ? '-cnt-ck' : '-cnt-unck'));
			}
		}
	},
	isAutocheck: function () {
		return this._autocheck;
	},
	setAutocheck: function (autocheck) {
		if (this._autocheck != autocheck)
			this._autocheck = autocheck;
	},
	getHref: function () {
		return this._href;
	},
	setHref: function (href) {
		if (this._href != href) {
			this._href = href;
			this.rerender();
		}
	},
	getTarget: function () {
		return this._target;
	},
	setTarget: function (target) {
		if (this._target !=  target) {
			this._target = target;
			var anc = this.getSubnode('a');
			if (anc) {
				if (this.isTopmost())
					anc = anc.parentNode;
				anc.target = this._target;
			}
		}
	},
	isTopmost: function () {
		return this._topmost;
	},
	beforeParentChanged_: function (newParent) {
		this._topmost = newParent && !(newParent.$instanceof(zul.menu.Menupopup));		
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var added = this.isDisabled() ? this.getZclass() + '-disd' : '';
			if (added) scls += (scls ? ' ': '') + added;
		}
		return scls;
	},
	getZclass: function () {
		return this._zclass == null ? "z-menu-item" : this._zclass;
	},
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this._image;
		if (!img) img = zAu.comURI('web/img/spacer.gif');

		img = '<img src="' + img + '" align="absmiddle" class="' + this.getZclass() + '-img"/>';
		return label ? img + ' ' + label: img;
	},
	domStyle_: function (no) {
		var style = this.$supers('domStyle_', arguments);
		return this.isTopmost() ?
			style + 'padding-left:4px;padding-right:4px;': style;
	},
	getMenubar: function () {
		for (var p = this.parent; p; p = p.parent)
			if (p.$instanceof(zul.menu.Menubar))
				return p;
		return null;
	},
	_fixBtn: function () {
		var btn = this.getSubnode('b');
		if (!btn || !btn.innerHTML.trim()) return;
		if (!this._textUtil) this._textUtil = zDom.getTextUtil();
		btn.style.width = this._textUtil.measure(btn).width + zDom.frameWidth(btn) + "px";
			// fix the gap between button and em tag for IE.
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		
		if (!this.isDisabled()) {			
			var anc = this.getSubnode('a'),
				n = this.getNode();
			
			zEvt.listen(n, "mouseover", this.proxy(this._doMouseOver, '_fxMouseOver'));
			zEvt.listen(n, "mouseout", this.proxy(this._doMouseOut, '_fxMouseOut'));
			
			if (this.isTopmost()) {
				zEvt.listen(anc, "focus", this.proxy(this.domFocus_, '_fxFocus'));
				zEvt.listen(anc, "blur", this.proxy(this.domBlur_, '_fxBlur'));
			}
		}
		if (zk.ie && this.isTopmost()) this._fixBtn();
	},
	unbind_: function () {
		if (!this.isDisabled()) {
			var anc = this.getSubnode('a'),
				n = this.getNode();				
			zEvt.unlisten(n, "mouseover", this._fxMouseOver);
			zEvt.unlisten(n, "mouseout", this._fxMouseOut);
			
			if (this.isTopmost()) {
				zEvt.unlisten(anc, "focus", this._fxFocus);
				zEvt.unlisten(anc, "blur", this._fxBlur);
			}
		}
		
		if (this._textUtil)
			this._textUtil.destroy();
		this._textUtil = null;
			
		this.$supers('unbind_', arguments);		
	},
	doClick_: function (evt) {
		if (this._disabled)
			evt.stop();
		else {
			if (!this.$class._isActive(this)) return;
			
			var topmost = this.isTopmost(),
				anc = this.getSubnode('a');
			
			if (topmost) {
				zDom.rmClass(anc, this.getZclass() + '-btn-over');
				anc = anc.parentNode;
			}
			if ('javascript:;' == anc.href) {
				if (this.isAutocheck()) {
					this.setChecked(!this.isChecked());
					this.fire('onCheck', this.isChecked());
				}
				this.fireX(evt);
			} else {
				if (zk.ie && topmost && this.getNode().id != anc.id) 
					zUtl.go(anc.href, false, anc.target);
					// Bug #1886352 and #2154611 
					//Note: we cannot eat onclick. or, <a> won't work
			}
			zWatch.fire('onFloatUp', null, this); //notify all
		}
	},
	_doMouseOver: function (evt) {
		if (this.$class._isActive(this)) return;
		if (!this.isDisabled()) {
			if (this.isTopmost() && zk.ie && !zDom.isAncestor(this.getSubnode('a'), zEvt.target(evt)))
				return;
			
			this.$class._addActive(this);
			zWatch.fire('onFloatUp', null, this); //notify all
		}
	},
	_doMouseOut: function (evt) {
		if (!this.isDisabled()) {
			if (zk.ie) {
					var n = this.getSubnode('a'),
						xy = zDom.revisedOffset(n),
						p = zEvt.pointer(evt),
						x = p[0],
						y = p[1],
						diff = this.isTopmost() ? 1 : 0;
					if (x - diff > xy[0] && x <= xy[0] + n.offsetWidth && y - diff > xy[1] &&
						y <= xy[1] + n.offsetHeight)
						return; // don't deactivate;
				}
			this.$class._rmActive(this);
		}
	}
}, {
	_isActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getSubnode('a') : wgt.getNode(),
			cls = wgt.getZclass() + (top ? '-btn-over' : '-over');
		return zDom.hasClass(n, cls);
	},
	_addActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getSubnode('a') : wgt.getNode(),
			cls = wgt.getZclass() + (top ? '-btn-over' : '-over');
		zDom.addClass(n, cls);
		if (!top && wgt.parent.parent.$instanceof(zul.menu.Menu))
			this._addActive(wgt.parent.parent);
	},
	_rmActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.getSubnode('a') : wgt.getNode(),
			cls = wgt.getZclass() + (top ? '-btn-over' : '-over');
		zDom.rmClass(n, cls);
	}
});

(_zkwg=_zkpk.Menuitem).prototype.className='zul.menu.Menuitem';_zkmd={};
_zkmd['default']=
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass();
	
	if (this.isTopmost()) {
		out.push('<td align="left"', this.domAttrs_(), '><a href="',
				this.getHref() ? this.getHref() : 'javascript:;', '"');
		if (this.getTarget())
			out.push(' target="', this.getTarget(), '"');
		out.push(' class="', zcls, '-cnt"><table id="', uuid, '$a"', zUtl.cellps0,
				' class="', zcls, '-btn');
		if (this.getImage()) {
			out.push(' ', zcls, '-btn');
			if (this.getLabel())
				out.push('-text');
			
			out.push('-img');			
		}
		out.push('" style="width: auto;"><tbody><tr><td class="', zcls,
				'-btn-l"><i>&nbsp;</i></td><td class="', zcls,
				'-btn-m"><em unselectable="on"><button id="', uuid,
				'$b" type="button" class="', zcls, '-btn-text"');
		if (this.getImage())
			out.push(' style="background-image:url(', this.getImage(), ')"');
			
		out.push('>', zUtl.encodeXML(this.getLabel()), '</button></em></td><td class="',
					zcls, '-btn-r"><i>&nbsp;</i></td></tr></tbody></table></a></td>');
	} else {
		out.push('<li', this.domAttrs_(), '>');
		var cls = zcls + '-cnt' +
				(!this.getImage() && this.isCheckmark() ? 
						' ' + zcls + (this.isChecked() ? '-cnt-ck' : '-cnt-unck') : '');
		out.push('<a href="', this.getHref() ? this.getHref() : 'javascript:;', '"');
		if (this.getTarget())
			out.push(' target="', this.getTarget(), '"');
		out.push(' id="', uuid, '$a" class="', cls, '">', this.domContent_(), '</a></li>');
	}
}

zkmld(_zkwg,_zkmd);
zul.menu.Menuseparator = zk.$extends(zul.Widget, {
	isPopup: function () {
		return this.parent && this.parent.$instanceof(zul.menu.Menupopup);
	},
	getMenubar: function () {
		for (var p = this.parent; p; p = p.parent)
			if (p.$instanceof(zul.menu.Menubar))
				return p;
		return null;
	},
	getZclass: function () {
		return this._zclass == null ? "z-menu-separator" : this._zclass;
	},
	doMouseOver_: function () {
		zWatch.fire('onFloatUp', null, this); //notify all
		this.$supers('doMouseOver_', arguments);
	}
});

(_zkwg=_zkpk.Menuseparator).prototype.className='zul.menu.Menuseparator';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<li', this.domAttrs_(), '><span class="', this.getZclass(),
			'-inner">&nbsp;</span></li>');
}

zkmld(_zkwg,_zkmd);
zul.menu.Menupopup = zk.$extends('zul.wgt.Popup', {
	_curIndex: -1,
	
	_getCurrentIndex: function () {
		return this._curIndex;
	},
	getZclass: function () {
		return this._zclass == null ? "z-menu-popup" : this._zclass;
	},
	getSelectedItem: function () {
		return this.getSelectedItemAt(this.getSelectedIndex());
	},
	_isActiveItem: function (wgt) { 
		return wgt.isVisible() && (wgt.$instanceof(zul.menu.Menu) || (wgt.$instanceof(zul.menu.Menuitem) && !wgt.isDisabled()));
	},
	_currentChild: function (index) {
		var index = index != null ? index : this._curIndex;
		for (var w = this.firstChild, k = -1; w; w = w.nextSibling) 
			if (this._isActiveItem(w) && ++k === index)
				return w;
		return null;
	},
	_previousChild: function (wgt) {
		wgt = wgt ? wgt.previousSibling : this.lastChild;
		var lastChild = this.lastChild == wgt;
		for (; wgt; wgt = wgt.previousSibling) 
			if (this._isActiveItem(wgt)) {
				this._curIndex--;
				return wgt;
			}
		if (lastChild) return null; // avoid deadloop;
		this.curIndex = 0;
		for (wgt = this.firstChild; wgt; wgt = wgt.nextSibling)
			if (this._isActiveItem(wgt)) this._curIndex++;
		return this._previousChild();
	},
	_nextChild: function (wgt) {
		wgt = wgt ? wgt.nextSibling : this.firstChild;
		var firstChild = this.firstChild == wgt;
		for (; wgt; wgt = wgt.nextSibling) 
			if (this._isActiveItem(wgt)) {
				this._curIndex++;
				return wgt;
			}
		if (firstChild) return null; // avoid deadloop;
		this._curIndex = -1;	
		return this._nextChild();		
	},
	_syncShadow: function () {
		if (!this._shadow)
			this._shadow = new zk.eff.Shadow(this.getNode(), {stackup:zk.ie6Only});
		this._shadow.sync();
	},
	_hideShadow: function () {
		if (this._shadow) this._shadow.hide();
	},
	close: function () {
		this.$supers('close', arguments);
		this._hideShadow();
		var menu = this.parent;
		if (menu.$instanceof(zul.menu.Menu) && menu.isTopmost())
			zDom.rmClass(menu.getSubnode('a'), menu.getZclass() + "-btn-seld");
		
		var item = this._currentChild();
		if (item) item.$class._rmActive(item);
		this._curIndex = -1;
		this.$class._rmActive(this);
		// TODO for columns' menu zk.fire(pp, "close");
	},
	open: function (ref, offset, position, opts) {
		if (this.parent.$instanceof(zul.menu.Menu)) {
			if (!offset) {
				ref = this.parent.getSubnode('a');
				if (!position)
					if (this.parent.isTopmost())
						position = this.parent.parent.getOrient() == 'vertical'
							? 'end_before' : 'after_start';
					else position = 'end_before';
			}
		}
		this.$super('open', ref, offset, position, opts);
	},
	onFloatUp: function(wgt) {
		if (!this.isVisible()) 
			return;
		var org = wgt;
		if (this.parent.menupopup == this && !this.parent.isTopmost() && !this.parent.$class._isActive(this.parent)) {
			this.close({sendOnOpen:true});
			return;
		}
		
		// check if the wgt belongs to the popup
		for (var floatFound; wgt; wgt = wgt.parent) {
			if (wgt == this || (wgt.menupopup == this && !this._shallClose)) {
				if (!floatFound)
					this.setTopmost();
				return;
			}
			floatFound = floatFound || wgt.isFloating_();
		}
		
		// check if the popup is one of the wgt's children
		for (var floatFound, wgt = this.parent; wgt; wgt = wgt.parent) {
			if (wgt == org) {
				if (this._shallClose) break;
				if (!floatFound) 
					this.setTopmost();
				return;
			}
			floatFound = floatFound || wgt.isFloating_();
		}
		
		// check if the popup is an active menu
		if (!this._shallClose && this.parent.$instanceof(zul.menu.Menu)) {
			var menubar = this.parent.getMenubar();
			if (menubar && menubar._lastTarget == this.parent)
				return;
		}
		this.close({sendOnOpen:true});
	},
	onVisible: function (wgt) {
		if (zk.ie7) {
			var pp = this.getNode();
			if (!pp.style.width) {// Bug 2105158 and Bug 1911129
				var ul = this.getSubnode('cave');
				pp.style.width = ul.offsetWidth + zDom.frameWidth(pp) + "px";
			}
		}
		this._syncShadow();
		var anc = this.getSubnode('a');
		if (anc) {
			
			// just in case
			if (zk.ie)
				zDom.cleanVisibility(this.getNode());
				
			anc.focus();
		}
	},
	onHide: function (wgt) {
		this._hideShadow();
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen('onHide', this);
	},
	unbind_: function () {
		if (this._shadow)
			this._shadow.destroy();
		this._shadow = null;
		zWatch.unlisten('onHide', this);
		this.$supers('unbind_', arguments);
	},
	doKeyDown_: function (evt) {
		var w = this._currentChild(),
			keycode = evt.data.keyCode;
		switch (keycode) {
		case 38: //UP
		case 40: //DOWN
			if (w) w.$class._rmActive(w);
			w = keycode == 38 ? this._previousChild(w) : this._nextChild(w);
			if (w) w.$class._addActive(w);
			break;
		case 37: //LEFT
			this.close();
			
			if (this.parent.$instanceof(zul.menu.Menu) && !this.parent.isTopmost()) {
				var pp = this.parent.parent;
				if (pp) {
					var anc = pp.getSubnode('a');
					if (anc) anc.focus();
				}
			}
			break;
		case 39: //RIGHT
			if (w && w.$instanceof(zul.menu.Menu) && w.menupopup)
				w.menupopup.open();
			break;
		case 13: //ENTER
			if (w && w.$instanceof(zul.menu.Menuitem)) {
				w.doClick_(new zk.Event(w, 'onClick', null, {
					ctl: true
				}, evt.nativeEvent));
				zWatch.fire('onFloatUp', null, w); //notify all
				this.close({sendOnOpen:true});
			}
			break;
		}
		evt.stop();
		this.$supers('doKeyDown_', arguments);
	},
	doMouseOver_: function (evt) {
		this._shallClose = false;
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		this._shallClose = true;
		this.$supers('doMouseOut_', arguments);
	}	
}, {
	_rmActive: function (wgt) {
		if (wgt.parent.$instanceof(zul.menu.Menu)) {
			wgt.parent.$class._rmActive(wgt.parent);
			if (!wgt.parent.isTopmost())
				this._rmActive(wgt.parent.parent);
		}
	}
});

(_zkwg=_zkpk.Menupopup).prototype.className='zul.menu.Menupopup';_zkmd={};
_zkmd['default']=
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		tags = zk.ie || zk.gecko ? 'a' : 'button'; 
	out.push('<div', this.domAttrs_(), '><', tags, ' id="', uuid,
			'$a" tabindex="-1" onclick="return false;" href="javascript:;"',
			' style="padding:0 !important; margin:0 !important; border:0 !important;',
			' background: transparent !important; font-size: 1px !important;',
			' width: 1px !important; height: 1px !important;-moz-outline: 0 none;',
			' outline: 0 none;	-moz-user-select: text; -khtml-user-select: text;"></',
			tags, '><ul class="', zcls, '-cnt" id="', uuid, '$cave">');
			
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
		
	out.push('</ul></div>');
}

zkmld(_zkwg,_zkmd);
}finally{zPkg.end(_z);}}