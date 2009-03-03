_z='zul.wgt';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

zul.wgt.Div = zk.$extends(zul.Widget, {
});

(_zkwg=_zkpk.Div).prototype.className='zul.wgt.Div';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<div', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Include = zk.$extends(zul.Widget, {
	/** Returns the content of this include.
	 */
	getContent: function () {
		var v = this._content;
		return v ? v: '';
	},
	/** Sets the content of this include.
	 */
	setContent: function(content) {
		if (content == null) content = '';
		if (this._content != content) {
			this._content = content;
			var n = this.getNode();
			if (n) n.innerHTML = content;
		}
	},

	//super//
	redraw: function (out) {
		out.push('<div id="', this.uuid, '"');
		if (this.style) out.push(' style="', this.style, '"');
		out.push('>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push(this._content, '</div>');
	}
});

(_zkwg=_zkpk.Include).prototype.className='zul.wgt.Include';
zul.wgt.Label = zk.$extends(zul.Widget, {
	_value: '',

	/** Returns the value of this label.
	 */
	getValue: function () {
		return this._value;
	},
	/** Sets the value of this label.
	 */
	setValue: function(value) {
		if (!value) value = '';
		if (this._value != value) {
			this._value = value;
			var n = this.getNode();
			if (n) n.innerHTML = this.getEncodedText();
		}
	},
	isMultiline: function () {
		return this._multiline;
	},
	setMultiline: function (multiline) {
		if (multiline != this._multiline) {
			this._multiline = multiline;
			var n = this.getNode();
			if (n) n.innerHTML = this.getEncodedText();
		}
	},
	isPre: function () {
		return this._pre;
	},
	setPre: function (pre) {
		if (pre != this._pre) {
			this._pre = pre;
			var n = this.getNode();
			if (n) n.innerHTML = this.getEncodedText();
		}
	},

	getEncodedText: function () {
		return zUtl.encodeXML(this._value, {multiline:this._multiline,pre:this._pre});
	},
	//super//
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-label";
	}
});

(_zkwg=_zkpk.Label).prototype.className='zul.wgt.Label';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<span', this.domAttrs_(), '>', this.getEncodedText(), '</span>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Button = zk.$extends(zul.LabelImageWidget, {
	_orient: "horizontal",
	_dir: "normal",
	_tabindex: -1,

	/** Returns the orient of this button.
	 */
	getOrient: function () {
		return this._orient;
	},
	/** Sets the orient of this button.
	 */
	setOrient: function(orient) {
		if (this._orient != orient) {
			this._orient = orient;
			this.updateDomContent_();
		}
	},
	/** Returns the dir of this button.
	 */
	getDir: function () {
		return this._dir;
	},
	/** Sets the dir of this button.
	 */
	setDir: function(dir) {
		if (this._dir != dir) {
			this._dir = dir;
			this.updateDomContent_();
		}
	},
	/** Returns whether this button is disabled
	 */
	isDisabled: function () {
		return this._disabled;
	},
	/** Sets whether this button is disabled
	 */
	setDisabled: function(disabled) {
		if (this._disabled != disabled) {
			this._disabled = disabled;
			if (this.desktop)
				if (this._mold == 'os') this.getNode().disabled = true;
				else this.rerender(); //bind and unbind required
		}
	},
	/** Returns the tab index
	 */
	getTabindex: function () {
		return this._tabindex;
	},
	/** Sets the tab index
	 */
	setTabindex: function(tabindex) {
		if (this._tabindex != tabindex) {
			this._tabindex = tabindex;
			var n = this.getNode();
			if (n) (this.getSubnode('btn') || n).tabIndex = tabindex;
		}
	},

	getHref: function () {
		return this._href;
	},
	setHref: function (href) {
		this._href = href;
	},
	getTarget: function () {
		return this._target;
	},
	setTarget: function (target) {
		this._target = target;
	},

	//super//
	focus: function (timeout) {
		if (this.isVisible() && this.canActivate({checkOnly:true})) {
			zDom.focus(this.getSubnode('btn') ? this.getSubnode('btn'): this.getNode(), timeout);
			return true;
		}
		return false;
	},

	/** Updates the label and image. */
	updateDomContent_: function () {
		if (this.desktop) {
			var n = this.getSubnode('box');
			if (n) n.tBodies[0].rows[1].cells[1].innerHTML = this.domContent_();
			else this.getNode().innertHTML = this.domContent_();
		}
	},
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this.getImage();
		if (!img) return label;

		img = '<img src="' + img + '" align="absmiddle" />';
		var space = "vertical" == this.getOrient() ? '<br/>': ' ';
		return this.getDir() == 'reverse' ?
			label + space + img: img + space + label;
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (this._disabled && (!no || !no.zclass)) {
			var s = this.getZclass();
			if (s) scls += (scls ? ' ': '') + s + '-disd';
		}
		return scls;
	},

	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: this._mold == 'os' ? "z-button-os": "z-button";
	},
	bind_: function () {
		this.$supers('bind_', arguments);

		var $Button = zul.wgt.Button, n;
		if (this._mold == 'os') {
			n = this.getNode();
		} else {
			if (this._disabled) return;

			zDom.disableSelection(this.getSubnode('box'));

			n = this.getSubnode('btn');
		}

		zEvt.listen(n, "focus", $Button._doFocus);
		zEvt.listen(n, "blur", $Button._doBlur);
	},
	unbind_: function () {
		var $Button = zul.wgt.Button,
			n = this._mold == 'os' ? this.getNode(): this.getSubnode('btn');
		if (n) {
			zEvt.unlisten(n, "focus", $Button._doFocus);
			zEvt.unlisten(n, "blur", $Button._doBlur);
		}

		this.$supers('unbind_', arguments);
	},
	doClick_: function (wevt) {
		if (!this._disabled) {
			this.fireX(wevt);

			if (!wevt.stopped) {
				var href = this._href;
				if (href)
					zUtl.go(href, false, this.getTarget(), "target");
			}
		}
		//Unlike DOM, we don't proprogate to parent (so no calling $supers)
	},
	doMouseOver_: function () {
		zDom.addClass(this.getSubnode('box'), this.getZclass() + "-over");
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function () {
		if (this != zul.wgt.Button._curdn)
			zDom.rmClass(this.getSubnode('box'), this.getZclass() + "-over");
		this.$supers('doMouseOut_', arguments);
	},
	doMouseDown_: function () {
		var box = this.getSubnode('box'),
			zcls = this.getZclass();
		zDom.addClass(box, zcls + "-clk");
		zDom.addClass(box, zcls + "-over");
		zDom.focus(this.getSubnode('btn'), 30);

		zk.mouseCapture = this; //capture mouse up
		this.$supers('doMouseDown_', arguments);
	},
	doMouseUp_: function () {
		var box = this.getSubnode('box'),
			zcls = this.getZclass();
		zDom.rmClass(box, zcls + "-clk");
		zDom.rmClass(box, zcls + "-over");
		this.$supers('doMouseUp_', arguments);
	}
},{
	_doFocus: function (evt) {
		var wgt = zk.Widget.$(evt);
		if (wgt && wgt.domFocus_() //FF2 will cause a focus error when resize browser.
		&& wgt._mold != 'os')
			zDom.addClass(wgt.getSubnode('box'), wgt.getZclass() + "-focus");
	},
	_doBlur: function (evt) {
		var wgt = zk.Widget.$(evt);
		if (wgt._mold != 'os')
			zDom.rmClass(wgt.getSubnode('box'), wgt.getZclass() + "-focus");
		wgt.domBlur_();
	}
});
(_zkwg=_zkpk.Button).prototype.className='zul.wgt.Button';_zkmd={};
_zkmd['os']=
function (out) {
	out.push('<button', this.domAttrs_());
	var tabi = this._tabindex;
	if (this._disabled) out.push(' disabled="disabled"');
	if (tabi >= 0) out.push(' tabindex="', tabi, '"');
	out.push('>', this.domContent_(), '</button>');
}
_zkmd['default']=
function (out) {
	var zcls = this.getZclass(),
		tabi = this._tabindex;
	tabi = tabi >= 0 ? ' tabindex="' + tabi + '"': '';

	out.push('<span', this.domAttrs_({style:1,domclass:1}), ' class="', zcls, '"');
	if (!this.isVisible()) out.push(' style="display:none"');
	out.push('><table id="', this.uuid, '$box"', zUtl.cellps0);
	if (tabi && !zk.gecko && !zk.safari) out.push(tabi);
	var s = this.domStyle_();
	if (s) out.push(' style="', s, '"');
	s = this.domClass_();
	if (s) out.push(' class="', s, '"');

	var btn = '<button id="' + this.uuid + '$btn" class="' + zcls + '"',
	s = this.isDisabled();
	if (s) btn += ' disabled="disabled"';
	if (tabi && (zk.gecko || zk.safari)) btn += tabi;
	btn += '></button>';

	out.push('><tr><td class="', zcls, '-tl">');
	if (!zk.ie) out.push(btn);
	out.push('</td><td class="', zcls, '-tm"></td>', '<td class="', zcls,
			'-tr"></td></tr>');

	out.push('<tr><td class="', zcls, '-cl">');
	if (zk.ie) out.push(btn);
	out.push('</td><td class="', zcls, '-cm">', this.domContent_(),
			'</td><td class="', zcls, '-cr"><div></div></td></tr>',
			'<tr><td class="', zcls, '-bl"></td>',
			'<td class="', zcls, '-bm"></td>',
			'<td class="', zcls, '-br"></td></tr></table></span>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Separator = zk.$extends(zul.Widget, {
	_orient: 'horizontal',

	isVertical: function () {
		return this._orient == 'vertical';
	},
	/** Returns the orient of this button.
	 */
	getOrient: function () {
		return this._orient;
	},
	/** Sets the orient of this button.
	 */
	setOrient: function(orient) {
		if (this._orient != orient) {
			this._orient = orient;
			this.updateDomClass_();
		}
	},

	/** Returns whether to display a visual bar as the separator.
	 * <p>Default: false
	 */
	isBar: function () {
		return this._bar;
	},
	/** Sets  whether to display a visual bar as the separator.
	 */
	setBar: function(bar) {
		if (this._bar != bar) {
			this._bar = bar;
			this.updateDomClass_();
		}
	},
	/** Returns the spacing.
	 * <p>Default: null (depending on CSS).
	 */
	getSpacing: function () {
		return this._spacing;
	},
	/** Sets the spacing.
	 * @param spacing the spacing (such as "0", "5px", "3pt" or "1em")
	 */
	setSpacing: function(spacing) {
		if (this._spacing != spacing) {
			this._spacing = spacing;
			this.updateDomStyle_();
		}
	},

	//super//
	getZclass: function () {
		var zcls = this.zclass,
			bar = this.isBar();
		return zcls ? zcls: "z-separator" +
			(this.isVertical() ? "-ver" + (bar ? "-bar" : "") :
				"-hor" + (bar ? "-bar" : ""))
	},
	domStyle_: function () {
		var s = this.$supers('domStyle_', arguments);
		if (!this._isPercentGecko()) return s;

		var v = zk.parseInt(_spacing.substring(0, _spacing.length() - 1).trim());
		if (v <= 0) return s;
		v = v >= 2 ? (v / 2) + "%": "1%";

		return 'margin:' + (this.isVertical() ? '0 ' + v: v + ' 0')
			+ ';' + s;
	},
	getWidth: function () {
		var wd = this.$supers('getWidth', arguments);
		return !this.isVertical() || (wd != null && wd.length() > 0)
			|| this._isPercentGecko() ? wd: this._spacing;
		
	},
	getHeight: function () {
		var hgh = this.$supers('getHeight', arguments);
		return this.isVertical() || (hgh != null && hgh.length() > 0)
			|| this._isPercentGecko() ? hgh: this._spacing;
	},
	_isPercentGecko: function () {
		return zk.gecko && this._spacing != null && this._spacing.endsWith("%");
	}
});

(_zkwg=_zkpk.Separator).prototype.className='zul.wgt.Separator';_zkmd={};
_zkmd['default']=
function (out) {
	var tag = this.isVertical() ? 'span': 'div';
	out.push('<', tag, this.domAttrs_(), '>&nbsp;</', tag, '>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Space = zk.$extends(zul.wgt.Separator, {
	_orient: 'vertical'
});
(_zkwg=_zkpk.Space).prototype.className='zul.wgt.Space';_zkmd={};
_zkmd['default']=
zul.wgt.Separator.molds['default']
zkmld(_zkwg,_zkmd);
zul.wgt.Caption = zk.$extends(zul.LabelImageWidget, {
	//super//
	domDependent_: true, //DOM content depends on parent

	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-caption";
	},
	domContent_: function () {
		var label = this.getLabel(),
			img = this.getImage(),
			title = this.parent ? this.parent.title: '';
		if (title) label = label ? title + ' - ' + label: title;
		label = zUtl.encodeXML(label);
		if (!img) return label;

		img = '<img src="' + img + '" align="absmiddle" />';
		return label ? img + ' ' + label: img;
	},
	unbind_: function () {
		var n = this.getNode(), parent = this.parent;
		if (n && parent.$instanceof(zul.wgt.Groupbox))
			zEvt.unlisten(n, "click", zul.wgt.Caption.ongbclk);

		this.$supers('unbind_', arguments);
	},
	doClick_: function () {
		if (this.parent.$instanceof(zul.wgt.Groupbox))
			this.parent.setOpen(!this.parent.isOpen());
		this.$supers('doClick_', arguments);
	},
	//private//
	/** Whether to generate a close button. */
	_isCloseVisible: function () {
		var parent = this.parent;
		return parent.isClosable && parent.isClosable();
	},
	/** Whether to generate a minimize button. */
	_isMinimizeVisible: function () {
		var parent = this.parent;
		return parent.isMinimizable && parent.isMinimizable();
	},
	/** Whether to generate a maximize button. */
	_isMaximizeVisible: function () {
		var parent = this.parent;
		return parent.isMaximizable && parent.isMaximizable();
	}
});
(_zkwg=_zkpk.Caption).prototype.className='zul.wgt.Caption';_zkmd={};
_zkmd['default']=
function (out) {
	var parent = this.parent;
	if (parent.isLegend && parent.isLegend()) {
		out.push('<legend', this.domAttrs_(), '>', this.domContent_());
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</legend>');
		return;
	}

	var zcls = this.getZclass(),
		cnt = this.domContent_(),
		puuid = parent.uuid,
		pzcls = parent.getZclass();
	out.push('<table', this.domAttrs_(), zUtl.cellps0,
			' width="100%"><tr valign="middle"><td align="left" class="',
			zcls, '-l">', (cnt?cnt:'&nbsp;'), //Bug 1688261: nbsp required
			'</td><td align="right" class="', zcls,
			'-r" id="', this.uuid, '$cave">');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);

	out.push('</td>');
	if (this._isMinimizeVisible())
		out.push('<td width="16"><div id="', puuid, '$min" class="',
				pzcls, '-tool ', pzcls, '-minimize"></div></td>');
	if (this._isMaximizeVisible()) {
		out.push('<td width="16"><div id="', puuid, '$max" class="',
				pzcls, '-tool ', pzcls, '-maximize');
		if (parent.isMaximized())
			out.push(' ', pzcls, '-maximized');
		out.push('"></div></td>');
	}
	if (this._isCloseVisible())
		out.push('<td width="16"><div id="', puuid, '$close" class="',
				pzcls, '-tool ', pzcls, '-close"></div></td>');

	out.push('</tr></table>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Checkbox = zk.$extends(zul.LabelImageWidget, {
	_tabindex: -1,
	
	isDisabled: function () {
		return this._disabled;
	},
	setDisabled: function (disabled) {
		if (this._disabled != disabled) {
			this._disabled = disabled;
			if (this.getSubnode('real'))
				this.getSubnode('real').disabled = disabled;
		}
	},
	isChecked: function () {
		return this._checked;
	},
	setChecked: function (checked) {
		if (this._checked != checked) {
			this._checked = checked;
			if (this.getSubnode('real')) this.getSubnode('real').checked = checked;
		}
	},
	getName: function () {
		return this._name;
	},
	setName: function (name) {
		if (!name) name = null;
		if (this._name != name) {
			this._name = name;
			if (this.getSubnode('real'))
				this.getSubnode('real').name = name;
		}
	},
	getTabindex: function () {
		return this._tabindex;
	},
	setTabindex: function (tabindex) {
		if (this._tabindex != tabindex) {
			this._tabindex = tabindex;
			if (this.getSubnode('real'))
				this.getSubnode('real').tabIndex = tabindex;
		}
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-checkbox";
	},
	contentAttrs_: function () {
		var html = '', v = this.getName(); // cannot use this._name for radio
		if (v)
			html += ' name="' + v + '"';
		if (this._disabled)
			html += ' disabled="disabled"';
		if (this._checked)
			html += ' checked="checked"';
		v = this._tabindex;
		if (v >= 0)
			html += ' tabindex="' + v + '"';
		return html;
	},
	labelAttrs_: function () {
		var style = zDom.getTextStyle(this.domStyle_());
		return style ? ' style="' + style + '"' : "";
	},
	bind_: function (desktop) {
		this.$supers('bind_', arguments);

		var $Checkbox = zul.wgt.Checkbox,
			n = this.getSubnode('real');

		if (zk.gecko2Only)
			zEvt.listen(n, "click", zul.wgt.Checkbox._doClick);
			// bug #2233787 : this is a bug of firefox 2, it need get currentTarget
		zEvt.listen(n, "focus", this.proxy(this.domFocus_, '_fxFocus'));
		zEvt.listen(n, "blur", this.proxy(this.domBlur_, '_fxBlur'));
	},
	unbind_: function () {
		var $Checkbox = zul.wgt.Checkbox,
			n = this.getSubnode('real');
		
		if (zk.gecko2Only)
			zEvt.unlisten(n, "click", zul.wgt.Checkbox._doClick);
		zEvt.unlisten(n, "focus", this._fxFocus);
		zEvt.unlisten(n, "blur", this._fxBlur);

		this.$supers('unbind_', arguments);
	},
	doClick_: function () {
		var real = this.getSubnode('real'),
			val = real.checked;
		if (val != real.defaultChecked) { //changed
			this.setChecked(val);
			real.defaultChecked = val;
			this.fire('onCheck', val);
		}
		return this.$supers('doClick_', arguments);
	},
	updateDomStyle_: function () {
		var node = this.getNode()
		zDom.setStyle(node, zDom.parseStyle(this.domStyle_()));
		var label = zDom.firstChild(node, "LABEL", true);
		if (label) zDom.setStyle(label, zDom.parseStyle(zDom.getTextStyle(this.domStyle_())));
	}
});
if (zk.gecko2Only)
	zul.wgt.Checkbox._doClick = function (evt) {
		evt.z_target = evt.currentTarget;
			//bug #2233787 : this is a bug of firefox 2, it need get currentTarget
	};

(_zkwg=_zkpk.Checkbox).prototype.className='zul.wgt.Checkbox';_zkmd={};
_zkmd['default']=
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass();
	out.push('<span', this.domAttrs_(), '>', '<input type="checkbox" id="', uuid,
			'$real"', this.contentAttrs_(), '/><label for="', uuid, '$real"',
			this.labelAttrs_(), ' class="', zcls, '-cnt">', this.domContent_(),
			'</label></span>');	
}
zkmld(_zkwg,_zkmd);
zul.wgt.Groupbox = zk.$extends(zul.Widget, {
	_open: true,
	_closable: true,

	isLegend: function () {
		return this._mold == 'default';
	},
	isOpen: function () {
		return this._open;
	},
	setOpen: function (open, fromServer) {
		if (this._open != open) {
			this._open = open;

			var node = this.getNode();
			if (node) {
				var panel = this.getSubnode('panel');
				if (panel) { //!legend
					if (open) zAnima.slideDown(this, panel, {afterAnima: this._afterSlideDown});
					else zAnima.slideUp(this, panel, {beforeAnima: this._beforeSlideUp});
				} else {
					zDom[open ? 'rmClass': 'addClass'](node, this.getZclass() + "-collapsed");
					zWatch.fireDown(open ? 'onVisible': 'onHide', {visible:true}, this);
				}
				if (!fromServer) this.fire('onOpen', open);
			}
		}
	},
	isClosable: function () {
		return this._closable;
	},
	setClosable: function (closable) {
		if (this._closable != closable) {
			this._closable = closable;
			this._updateDomOuter();
		}
	},
	getContentStyle: function () {
		return this._cntStyle;
	},
	setContentStyle: function (style) {
		if (this._cntStyle != style) {
			this._cntStyle = style;
			this._updateDomOuter();
		}
	},
	getContentSclass: function () {
		return this._cntSclass;
	},
	setContentSclass: function (sclass) {
		if (this._cntSclass != sclass) {
			this._cntSclass = sclass;
			this._updateDomOuter();
		}
	},

	_updateDomOuter: function () {
		this.rerender(zk.Skipper.nonCaptionSkipper);
	},
	_contentAttrs: function () {
		var html = ' class="', s = this._cntSclass;
		if (s) html += s + ' ';
		html += this.getZclass() + '-cnt"';

		s = this._cntStyle;
		if (!this.isLegend() && this.caption) s = 'border-top:0;' + (s ? s: '');
		if (s) html += ' style="' + s + '"';
		return html;
	},

	//watch//
	onSize: _zkf = function () {
		var hgh = this.getNode().style.height;
		if (hgh && hgh != "auto") {
			var n = this.getSubnode('cave');
			if (n) {
				if (zk.ie6Only) n.style.height = "";
				n.style.height =
					zDom.revisedHeight(n, zDom.vflexHeight(n.parentNode), true)
					+ "px";
					//we use n.parentNode(=this.getSubnode('panel')) to calc vflex,
					//so we have to subtract margin, too
			}
		}
		setTimeout(this.proxy(this._fixShadow), 500);
			//shadow raraly needs to fix so OK to delay for better performance
			//(getSubnode('sdw') a bit slow due to zDom.$)
	},
	onVisible: _zkf,
	_afterSlideDown: function (n) {
		zWatch.fireDown("onVisible", {visible:true}, this);
	},
	_beforeSlideUp: function (n) {
		zWatch.fireDown("onHide", {visible:true}, this);
	},
	_fixShadow: function () {
		var sdw = this.getSubnode('sdw');
		if (sdw)
			sdw.style.display =
				zk.parseInt(zDom.getStyle(this.getSubnode('cave'), "border-bottom-width")) ? "": "none";
				//if no border-bottom, hide the shadow
	},
	updateDomStyle_: function () {
		this.$supers('updateDomStyle_', arguments);
		if (this.desktop) this.onSize();
	},

	//super//
	focus: function (timeout) {
		if (this.desktop) {
			var cap = this.caption;
			for (var w = this.firstChild; w; w = w.nextSibling)
				if (w != cap && w.focus(timeout))
					return true;
			return cap && cap.focus(timeout);
		}
		return false;
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls ? zcls: this.isLegend() ? "z-fieldset": "z-groupbox";
	},
	bind_: function () {
		this.$supers('bind_', arguments);

		if (!this.isLegend()) {
			zWatch.listen("onSize", this);
			zWatch.listen("onVisible", this);
		}
	},
	unbind_: function () {
		if (!this.isLegend()) {
			zWatch.unlisten("onSize", this);
			zWatch.unlisten("onVisible", this);
		}
		this.$supers('unbind_', arguments);
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.wgt.Caption))
			this.caption = child;
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.caption)
			this.caption = null;
	},

	domClass_: function () {
		var html = this.$supers('domClass_', arguments);
		if (!this._open) {
			if (html) html += ' ';
			html += this.getZclass() + '-collapsed';
		}
		return html;
	}
});
(_zkwg=_zkpk.Groupbox).prototype.className='zul.wgt.Groupbox';_zkmd={};
_zkmd['3d']=
function (out, skipper) {	
	var	zcls = this.getZclass(),
		uuid = this.uuid,
		cap = this.caption;

	out.push('<div', this.domAttrs_(), '>');
	
	if (cap) {
		out.push('<div class=\"', zcls, '-tl"><div class="', zcls,
			'-tr"></div></div><div class="', zcls, '-hl"><div class="',
			zcls, '-hr"><div class="', zcls, '-hm"><div class="',
			zcls, '-header">');
		cap.redraw(out);
		out.push('</div></div></div></div>');
	}
	
	out.push('<div id="', uuid, '$panel" class="', zcls, '-body"');
	if (!this.isOpen()) out.push(' style="display:none"');
	out.push('><div id="', uuid, '$cave"', this._contentAttrs(), '>');

	if (!skipper)
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w != cap)
				w.redraw(out);
	out.push('</div></div>',
		//shadow
		'<div id="', uuid, '$sdw" class="', zcls, '-bl"><div class="', zcls,
			'-br"><div class="', zcls, '-bm"></div></div></div></div>');
}
_zkmd['default']=
function (out, skipper) {
	out.push('<fieldset', this.domAttrs_(), '>');
	
	var cap = this.caption;
	if (cap) cap.redraw(out);

	out.push('<div id="', this.uuid, '$cave"', this._contentAttrs(), '>');
	
	if (!skipper)
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w != cap)
				w.redraw(out);
	out.push('</div></fieldset>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Html = zk.$extends(zul.Widget, {
	_content: '',
	getContent: function () {
		return this._content;
	},
	setContent: function (content) {
		if (!content) content = '';
		if (this._content != content) {
			this._content = content;
			var n = this.getNode();
			if (n) n.innerHTML = content;
		}
	}
});

(_zkwg=_zkpk.Html).prototype.className='zul.wgt.Html';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<span', this.domAttrs_(), '>', this._content, '</span>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Popup = zk.$extends(zul.Widget, {
	_visible: false,
	isOpen: function () {
		return this.isVisible();
	},
	open: function (ref, offset, position, opts) {
		var posInfo = this._posInfo(ref, offset, position);

		var node = this.getNode();
		zDom.setStyle(node, {position: "absolute"});
		zDom.makeVParent(node);
		if (posInfo)
			zDom.position(node, posInfo.dim, posInfo.pos, opts);
		
		this.setVisible(true);
		this.setFloating_(true);
		this.setTopmost();
		
		if (this.isListen("onOpen")) {
			// use a progress bar to hide the popup
			this.mask = new zk.eff.Mask({
				id: this.uuid + "$mask",
				anchor: node
			});
			
			// register onResponse to remove the progress bar after receiving
			// the response from server.
			zWatch.listen('onResponse', this);		
		}
		if (zk.ie6Only) {
			if (!this._stackup)
				this._stackup = zDom.makeStackup(node);
			else {
				this._stackup.style.top = node.style.top;
				this._stackup.style.left = node.style.left;
				this._stackup.style.display = "block";
			}
		}
		ref = zk.Widget.$(ref); // just in case, if ref is not a kind of zul.Widget.
		if (opts && opts.sendOnOpen) this.fire('onOpen', ref ? [true, ref.uuid] : true);
		zDom.cleanVisibility(node);
	},
	position: function (ref, offset, position, opts) {
		var posInfo = this._posInfo(ref, offset, position);
		if (posInfo)
			zDom.position(this.getNode(), posInfo.dim, posInfo.pos, opts);
	},
	_posInfo: function (ref, offset, position, opts) {
		var pos, dim;
		
		if (ref && position) {
			if (typeof ref == 'string')
				ref = zk.Widget.$(ref);
				
			if (ref) {
				var refn = zul.Widget.isInstance(ref) ? ref.getNode() : ref,
					ofs = zDom.revisedOffset(refn);
				pos = position;
				dim = {
					top: ofs[0], left: ofs[1],
					width: zDom.offsetWidth(refn), height: zDom.offsetHeight(refn)  
				}
			}
		} else if (offset && offset.$array) {
			dim = {
				top: zk.parseInt(offset[0]),
				left:  zk.parseInt(offset[1])
			}
		}
		if (dim) return {pos: pos, dim: dim};
	},
	onResponse: function () {
		if (this.mask) this.mask.destroy();
		zWatch.unlisten('onResponse', this);
		this.mask = null;
	},
	close: function (opts) {
		if (this._stackup)
			this._stackup.style.display = "none";
		
		this.setVisible(false);
		zDom.undoVParent(this.getNode());
		this.setFloating_(false);
		if (opts && opts.sendOnOpen) this.fire('onOpen', false);
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-popup";
	},
	onFloatUp: function(wgt){
		if (!this.isVisible()) 
			return;
		for (var floatFound; wgt; wgt = wgt.parent) {
			if (wgt == this) {
				if (!floatFound) 
					this.setTopmost();
				return;
			}
			floatFound = floatFound || wgt.isFloating_();
		}
		this.close({sendOnOpen:true});
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen('onFloatUp', this);
		zWatch.listen('onVisible', this);
		this.setFloating_(true);
	},
	unbind_: function () {
		if (this._stackup) {
			zDom.remove(this._stackup);
			this._stackup = null;
		}
		
		zWatch.unlisten('onFloatUp', this);
		zWatch.unlisten('onVisible', this);
		this.setFloating_(false);
		this.$supers('unbind_', arguments);
	},
	onVisible: zk.ie7 ? function (wgt) {
		var node = wgt.getNode(),
			wdh = node.style.width,
			fir = zDom.firstChild(node, "DIV"),
			last = zDom.lastChild(zDom.lastChild(node, "DIV"), "DIV"),
			n = wgt.getSubnode('cave').parentNode;
		
		if (!wdh || wdh == "auto") {
			var diff = zDom.padBorderWidth(n.parentNode) + zDom.padBorderWidth(n.parentNode.parentNode);
			if (fir) {
				fir.firstChild.firstChild.style.width = Math.max(0, n.offsetWidth - (zDom.padBorderWidth(fir)
					+ zDom.padBorderWidth(fir.firstChild) - diff)) + "px";
			}
			if (last) {
				last.firstChild.firstChild.style.width = Math.max(0, n.offsetWidth - (zDom.padBorderWidth(last)
					+ zDom.padBorderWidth(last.firstChild) - diff)) + "px";
			}
		} else {
			if (fir) fir.firstChild.firstChild.style.width = "";
			if (last) last.firstChild.firstChild.style.width = "";
		}
	}: zk.$void,
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		zWatch.fireDown('onVisible', {visible:true}, this);
	},
	prologHTML_: function (out) {
	},
	epilogHTML_: function (out) {
	}
});

(_zkwg=_zkpk.Popup).prototype.className='zul.wgt.Popup';_zkmd={};
_zkmd['default']=
function (out) {
	var zcls = this.getZclass();
	out.push('<div', this.domAttrs_(), '><div class="', zcls, '-tl"><div class="',
			zcls, '-tr"><div class="', zcls, '-tm"></div></div></div>', '<div id="',
			this.uuid, '$bwrap" class="', zcls, '-body"><div class="', zcls,
			'-cl"><div class="', zcls, '-cr"><div class="', zcls, '-cm">',
			'<div id="', this.uuid, '$cave" class="', zcls, '-cnt">');
	this.prologHTML_(out);
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	this.epilogHTML_(out);
	out.push('</div></div></div></div><div class="', zcls, '-bl"><div class="',
			zcls, '-br"><div class="', zcls, '-bm"></div></div></div></div></div>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Radio = zk.$extends(zul.wgt.Checkbox, {
	_value: '',
	
	getRadiogroup: function (parent) {
		var wgt = parent || this.parent;
		for (; wgt; wgt = wgt.parent)
			if (wgt.$instanceof(zul.wgt.Radiogroup)) return wgt;
		return null;
	},
	isSelected: function () {
		return this.isChecked();
	},
	setSelected: function (selected, fromServer) {
		this.setChecked(selected, fromServer);
	},
	setChecked: function (checked, fromServer) {
		if (checked != this.isChecked()) {
			this.$supers('setChecked', arguments);
			if (this.getSubnode('real')) {
				var group = this.getRadiogroup();
				
				// bug #1893575 : we have to clean all of the radio at the same group.
				// in addition we can filter unnecessary onCheck with defaultChecked
				if (checked) {
					for (var items = group.getItems(), i = items.length; --i >= 0;) {
						if (items[i] != this) {
							items[i].getSubnode('real').defaultChecked = false;
							items[i]._checked = false;
						}
					}
				}
				if (group) 
					group._fixSelectedIndex();
			}
		}
	},
	getValue: function () {
		return this._value;
	},
	setValue: function (value) {
		if (value == null)
			value = "";
		if (this._value != value)
			this._value = value;
	},
	getName: function () {
		var group = this.getRadiogroup();
		return group != null ? group.getName(): this.uuid;
	},
	contentAttrs_: function () {
		var html = this.$supers('contentAttrs_', arguments);
		html += ' value="' + this.getValue() + '"';
		return html;
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-radio";
	},
	beforeParentChanged_: function (newParent) {
		var oldParent = this.getRadiogroup(),
			newParent = newParent ? this.getRadiogroup(newParent) : null;
		if (oldParent != newParent) {
			if (oldParent && oldParent.$instanceof(zul.wgt.Radiogroup))
				oldParent._fixOnRemove(this); 
			if (newParent && newParent.$instanceof(zul.wgt.Radiogroup))
				newParent._fixOnAdd(this); 
		}
	}
});

(_zkwg=_zkpk.Radio).prototype.className='zul.wgt.Radio';_zkmd={};
_zkmd['default']=
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		rg = this.getRadiogroup();
	out.push('<span', this.domAttrs_(), '>', '<input type="radio" id="', uuid,
				'$real"', this.contentAttrs_(), '/><label for="', uuid, '$real"',
				this.labelAttrs_(), ' class="', zcls, '-cnt">', this.domContent_(),
				'</label>', (rg && rg.getOrient() == 'vertical' ? '<br/></span>' : '</span>'));
}
zkmld(_zkwg,_zkmd);
zul.wgt.Radiogroup = zk.$extends(zul.Widget, {
	_orient: 'horizontal',
	_jsel: -1,
	
	getOrient: function () {
		return this._orient;
	},
	setOrient: function (orient) {
		if (this._orient != orient) {
			this._orient = orient;
			this.rerender();
		}
	},
	getItemAtIndex: function (index) {
		if (index < 0)
			return null;
		return this._getAt(this, {value: 0}, index);
	},
	getItemCount: function () {
		return this.getItems().length;
	},
	getItems: function () {
		return this._concatItem(this);
	},
	getSelectedIndex: function () {
		return this._jsel;
	},
	setSelectedIndex: function (jsel) {
		if (jsel < 0) jsel = -1;
		if (this._jsel != jsel) {
			if (jsel < 0) {
				getSelectedItem().setSelected(false);
			} else {
				getItemAtIndex(jsel).setSelected(true);
			}
		}
	},
	getSelectedItem: function () {
		return this._jsel >= 0 ? this.getItemAtIndex(this._jsel): null;
	},
	setSelectedItem: function (item) {
		if (item == null)
			this.setSelectedIndex(-1);
		else if (item.$instanceof(zul.wgt.Radio))
			item.setSelected(true);
	},
	appendItem: function (label, value) {
		var item = new zul.wgt.Radio();
		item.setLabel(label);
		item.setValue(value);
		this.appendChild(item);
		return item;
	},
	removeItemAt: function (index) {
		var item = this.getItemAtIndex(index);
		this.removeChild(item);
		return item;
	},
	getName: function () {
		return this._name;
	},
	setName: function (name) {
		if (!name) name = null;
		if (this._name != name) {
			this._name = name;
			for (var items = this.getItems(), i = items.length; --i >= 0;)
				items[i].setName(name);
		}
	},
	/** private method */
	_fixSelectedIndex: function () {
		this._jsel = this._fixSelIndex(this, {value: 0});
	},
	_concatItem: function (cmp) {
		var sum = [];
		for (var wgt = cmp.firstChild; wgt; wgt = wgt.nextSibling) {			
			if (wgt.$instanceof(zul.wgt.Radio)) 
				sum.push(wgt);
			else 
				if (!wgt.$instanceof(zul.wgt.Radiogroup)) { //skip nested radiogroup
					sum = sum.concat(this._concatItem(wgt));
				}
		}
		return sum;
	},
	_getAt: function (cmp, cur, index) {
		for (var cnt = 0, wgt = cmp.firstChild; wgt; wgt = wgt.nextSibling) {
			if (wgt.$instanceof(zul.wgt.Radio)) {
				if (cnt.value++ == index) return wgt;
			} else if (!wgt.$instanceof(zul.wgt.Radiogroup)) {
				var r = this._getAt(wgt, cur, index);
				if (r != null) return r;
			}				
		}
		return null;
	},
	_fixOnAdd: function (child) {
		if (this._jsel >= 0 && child.isSelected()) {
			child.setSelected(false); //it will call _fixSelectedIndex()
		} else {
			this._fixSelectedIndex();
		}
	},
	_fixOnRemove: function (child) {
		if (child.isSelected()) {
			this._jsel = -1;
		} else if (this._jsel > 0) { //excluding 0
			this._fixSelectedIndex();
		}
	},
	_fixSelIndex: function (cmp, cur) {
		for (var wgt = cmp.firstChild; wgt; wgt = wgt.nextSibling) {
			if (wgt.$instanceof(zul.wgt.Radio)) {
				if (wgt.isSelected())
					return cur.value;
				++cur.value;
			} else if (!wgt.$instanceof(zul.wgt.Radiogroup)) {
				var jsel = this._fixSelIndex(wgt, cur);
				if (jsel >= 0) return jsel;
			}
		}
		return -1;
	}
});

(_zkwg=_zkpk.Radiogroup).prototype.className='zul.wgt.Radiogroup';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<span', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</span>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Toolbar = zk.$extends(zul.Widget, {
	_orient: "horizontal",
	_align: "start",
	getAlign: function(){
		return this._align;
	},
	setAlign: function(align){
		if (!align) 
			align = "start";
		if (this._align != align) {
			this._align = align;
			this.rerender();
		}
	},
	getOrient: function(){
		return this._orient;
	},
	setOrient: function(orient){
		if (this._orient != orient) {
			this._orient = orient;
			this.rerender();
		}
	},
	// super
	getZclass: function(){
		var zcls = this._zclass;
		return zcls ? zcls : "z-toolbar" +
		(this.inPanelMold() ? "-panel" : "");
	},
	// protected 
    inPanelMold: function(){
        return this._mold == "panel";
    },
	onChildAdded_: function(){
		this.$supers('onChildAdded_', arguments);
		if (this.inPanelMold()) 
			this.rerender();
	},
	onChildRemoved_: function(){
		this.$supers('onChildRemoved_', arguments);
		if (this.inPanelMold()) 
			this.rerender();
	}
	
});

(_zkwg=_zkpk.Toolbar).prototype.className='zul.wgt.Toolbar';_zkmd={};
_zkmd['panel']=
function (out) {
	var zcls = this.getZclass();
	out.push('<div ', this.domAttrs_(), '>', '<div class="', zcls, '-body ',
				zcls, '-', this.getAlign(), '" >', '<table id="', this.uuid,
				'$cnt" class="', zcls, '-cnt"', zUtl.cellps0, '><tbody>');
	if ('vertical' != this.getOrient()) {
		out.push("<tr>");
		for (var w = this.firstChild; w; w = w.nextSibling) {
			out.push('<td class="', zcls, '-hor">');
			w.redraw(out);
			out.push("</td>");
		}
		out.push("</tr>");
	} else {
		for (var w = this.firstChild; w; w = w.nextSibling) {
			out.push('<tr><td class="', zcls, '-ver">');
			w.redraw(out);
			out.push('</td></tr>');
		}
	}
	out.push('</tbody></table><div class="z-clear"></div></div></div>');
}

_zkmd['default']=
function (out) {
	var zcls = this.getZclass(),
		space = 'vertical' != this.getOrient() ? '' : '<br/>';
		
	out.push('<div ', this.domAttrs_(), '>', '<div id="', this.uuid, '$cave"',
				' class="', zcls, "-body ", zcls, '-', this.getAlign(), '" >');
	
	for (var w = this.firstChild; w; w = w.nextSibling) {
		out.push(space);
		w.redraw(out);
	}
	out.push('</div><div class="z-clear"></div></div>');
}
zkmld(_zkwg,_zkmd);
zul.wgt.Toolbarbutton = zk.$extends(zul.LabelImageWidget, {
	_orient: "horizontal",
	_dir: "normal",
	_tabindex: -1,
	_disabled: false,

	isDisabled: function(){
		return this._disabled;
	},
	setDisabled: function(disabled){
		if (this._disabled != disabled) {
			this._disabled = disabled;
			this.updateDomClass_();//update class and attr
		}
	},
	
	getDir: function(){
		return this._dir;
	},
	setDir: function(dir){
		if (this._dir != dir) {
			this._dir = dir;
			var n = this.getNode();
			if (n) n.innerHTML = this.domContent_();
		}
	},
	
	getHref: function(){
		return this._href;
	},
	setHref: function(href){
		if (this._href != href) {
			this._href = href;
			var n = this.getNode();
			if (n) n.href = href;
		}
	},
	
	getOrient: function(){
		return this._orient;
	},
	setOrient: function(orient){
		if (this._orient != orient) {
			this._orient = orient;
			var n = this.getNode();
			if (n) n.innerHTML = this.domContent_();
		}
	},
	
	getTarget: function(){
		return this._target;
	},
	setTarget: function(target){
		if (this._target != target) {
			this._target = target;
			var n = this.getNode();
			if (n) n.target = target;
		}
	},
	
	getTabindex: function(){
		return this._tabindex == -1 ? "" : this._tabindex;
	},
	setTabindex: function(tabindex){
		if (this._tabindex != tabindex) {
			this._tabindex = tabindex;
			var n = this.getNode();
			if (n) n.tabIndex = tabindex < 0 ? null : tabindex;
		}
	},
	// super//
	getZclass: function(){
		var zcls = this._zclass;
		return zcls ? zcls : "z-toolbar-button";
	},

	bind_: function(){
		this.$supers('bind_', arguments);
		var n = this.getNode();
		if (!this._disabled) {
			zEvt.listen(n, "focus", this.proxy(this.domFocus_, '_fxFocus'));
			zEvt.listen(n, "blur", this.proxy(this.domBlur_, '_fxBlur'));
		}
	},
	unbind_: function(){
		var n = this.getNode();
		zEvt.unlisten(n, "focus", this._fxFocus);
		zEvt.unlisten(n, "blur", this._fxBlur);
		this.$supers('unbind_', arguments);
	},
	domContent_: function(){
		var label = zUtl.encodeXML(this.getLabel()), img = this.getImage();
		if (!img) 
			return label;
		
		img = '<img src="' + img + '" align="absmiddle" />';
		var space = "vertical" == this.getOrient() ? '<br/>' : '';
		return this.getDir() == 'reverse' ? label + space + img : img + space + label;
	},
	domClass_: function(no){
		var scls = this.$supers('domClass_', arguments);
		if (this._disabled && (!no || !no.zclass)) {
			var s = this.getZclass();
			if (s) 
				scls += (scls ? ' ' : '') + s + '-disd';
		}
		return scls;
	},
	domAttrs_: function(no){
		var attr = this.$supers('domAttrs_', arguments);
		if (this.getTarget()) 
			attr += ' target="' + this.getTarget() + '"';
		if (this.getTabindex()) 
			attr += ' tabIndex="' + this.getTabindex() + '"';
		if (this.getHref()) 
			attr += ' href="' + this.getHref() + '"';
		else 
			attr += ' href="javascript:;"';
		return attr;
	},
	doClick_: function(wevt, evt){
		if (this._disabled)
			zEvt.stop(evt); //prevent default behavior
		else {
			this.fireX(wevt);
			if (wevt.stopped)
				zEvt.stop(evt); //prevent default behavior
		}
		//Unlike DOM, we don't proprogate to parent (so no calling $supers)
	}
});

(_zkwg=_zkpk.Toolbarbutton).prototype.className='zul.wgt.Toolbarbutton';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<a ', this.domAttrs_(), '>', this.domContent_(), '</a>');
}

zkmld(_zkwg,_zkmd);
}finally{zPkg.end(_z);}}