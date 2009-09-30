/* Button.js

	Purpose:
		
	Description:
		
	History:
		Sat Nov  8 22:58:16     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Button = zk.$extends(zul.LabelImageWidget, {
	_orient: "horizontal",
	_dir: "normal",
	_tabindex: -1,

	$define: {
		href: null,
		target: null,
		dir: _zkf = function () {
			this.updateDomContent_();
		},
		orient: _zkf,
		disabled: function (v) {
			if (this.desktop)
				if (this._mold != 'trendy') this.$n().disabled = v;
				else this.rerender(); //bind and unbind required
		},
		image: function (v) {
			if (this._mold == 'trendy') {
				this.rerender();
			} else {				
				var n = this.getImageNode();
				if (n) 
					n.src = v || '';
			}
		},
		tabindex: function (v) {
			var n = this.$n();
			if (n) (this.$n('btn') || n).tabIndex = v >= 0 ? v: '';
		},
		autodisable: null,
		upload: function (v) {
			var n = this.$n();
			if (n && !this._disabled) {
				this._cleanUpld();
				if (v && v != 'false') this._initUpld();
			}
		}
	},

	//super//
	updateDomClass_: function () {
		if (this.desktop) {
			var n = this._mold == 'trendy' ? this.$n('box') : this.$n();
			if (n) n.className = this.domClass_();
		}
	},
	updateDomStyle_: function () {
		if (this.desktop) {
			var n = this._mold == 'trendy' ? this.$n('box') : this.$n();
			var s = jq.parseStyle(this.domStyle_());
			zk(n).setStyles(s);

			n = this.getTextNode();
			if (n) zk(n).css(jq.filterTextStyle(s));
		}
	},
	setVisible: function (visible) {
		if (this._visible != visible) {
			this.$supers('setVisible', arguments);
			if (this._mold == 'trendy')
				this.rerender();
		}
		return this;
	},
	focus: function (timeout) {
		if (this.desktop && this.isVisible() && this.canActivate({checkOnly:true})) {
			zk(this.$n('btn')||this.$n()).focus(timeout);
			return true;
		}
		return false;
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
		return zcls != null ? zcls: this._mold != 'trendy' ? "z-button-os": "z-button";
	},
	bind_: function () {
		this.$supers('bind_', arguments);

		var n;
		if (this._mold != 'trendy') {
			n = this.$n();
		} else {
			if (this._disabled) return;

			zk(this.$n('box')).disableSelection();

			n = this.$n('btn');
			if (this._upload || zk.ie) zWatch.listen({onSize: this, onShow: this});
		}

		this.domListen_(n, "onFocus", "doFocus_")
			.domListen_(n, "onBlur", "doBlur_");

		if (!this._disabled && this._upload) this._initUpld();
	},
	unbind_: function () {
		if (!this._disabled && this._upload) this._cleanUpld();

		var trendy = this._mold == 'trendy',
			n = !trendy ? this.$n(): this.$n('btn');
		if (n) {
			this.domUnlisten_(n, "onFocus", "doFocus_")
				.domUnlisten_(n, "onBlur", "doBlur_");
		}
		if (this._upload || (zk.ie && trendy))
			zWatch.unlisten({onSize: this, onShow: this});

		this.$supers('unbind_', arguments);
	},
	_initUpld: function () {
		var v;
		if (v = this._upload)
			this._uplder = new zul.Upload(this, null, v);
	},
	_cleanUpld: function () {
		var v;
		if (v = this._uplder) {
			this._uplder = null;
			v.destroy();
		}
	},

	onSize: _zkf = zk.ie ? function () {
		var box = this.$n('box');
		if (box.style.height && box.offsetHeight) {
			var cellHgh = zk.parseInt(jq(box.rows[0].cells[0]).css('height'));
			if (cellHgh != box.rows[0].cells[0].offsetHeight) {
				box.rows[1].style.height = jq.px(box.offsetHeight -
				cellHgh - zk.parseInt(jq(box.rows[2].cells[0]).css('height')));
			}
		}
		if (this._uplder)
			this._uplder.sync();
	} : function () {
		if (this._uplder)
			this._uplder.sync();
	},
	onShow: _zkf,
	doFocus_: function (evt) {
		if (this._mold == 'trendy')
			jq(this.$n('box')).addClass(this.getZclass() + "-focus");
		this.$supers('doFocus_', arguments);
	},
	doBlur_: function (evt) {
		if (this._mold == 'trendy')
			jq(this.$n('box')).removeClass(this.getZclass() + "-focus");
		this.$supers('doBlur_', arguments);
	},
	doClick_: function (evt) {
		if (!this._disabled) {
			var ads = this._autodisable, aded;
			if (ads) {
				ads = ads.split(',');
				for (var j = ads.length; j--;) {
					var ad = ads[j].trim();
					if (ad) {
						var perm;
						if (perm = ad.charAt(0) == '+')
							ad = ad.substring(1);
						ad = "self" == ad ? this: this.$f(ad);
						if (ad) {
							ad.setDisabled(true);
							if (this.inServer)
								if (perm)
									ad.smartUpdate('disabled', true);
								else if (!aded) aded = [ad];
								else aded.push(ad);
						}
					}
				}
			}
			if (aded) {
				aded = new zul.wgt.ADBS(aded);
				if (this.isListen('onClick', {asapOnly:true}))
					zWatch.listen({onResponse: aded});
				else
					setTimeout(function () {aded.onResponse();}, 800);
			}
			
			this.fireX(evt);

			if (!evt.stopped) {
				var href = this._href;
				if (href)
					zUtl.go(href, false, this._target || (evt.data.ctrlKey ? '_blank' : ''));
				this.$super('doClick_', evt, true);
			}
		}
		//Unlike DOM, we don't proprogate to parent (otherwise, onClick
		//will fired)
	},
	doMouseOver_: function () {
		if (!this._disabled)
			jq(this.$n('box')).addClass(this.getZclass() + "-over");
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function (evt) {
		if (!this._disabled && this != zul.wgt.Button._curdn
		&& !(zk.ie && jq.isAncestor(this.$n('box'), evt.domEvent.relatedTarget || evt.domEvent.toElement)))
			jq(this.$n('box')).removeClass(this.getZclass() + "-over");
		this.$supers('doMouseOut_', arguments);
	},
	doMouseDown_: function () {
		if (!this._disabled) {
			var zcls = this.getZclass();
			jq(this.$n('box')).addClass(zcls + "-clk")
				.addClass(zcls + "-over")
			if (!zk.ie || !this._uplder) zk(this.$n('btn')).focus(30);
				//change focus will disable upload in IE
		}

		zk.mouseCapture = this; //capture mouse up
		this.$supers('doMouseDown_', arguments);
	},
	doMouseUp_: function () {
		if (!this._disabled) {
			var zcls = this.getZclass();
			jq(this.$n('box')).removeClass(zcls + "-clk")
				.removeClass(zcls + "-over");
			if (zk.ie && this._uplder) zk(this.$n('btn')).focus(30);
		}
		this.$supers('doMouseUp_', arguments);
	},
	setFlexSize_: function(sz) { //Bug #2870652
		var n = this.$n(),
			box = this.$n('box') || n;
		if (sz.height !== undefined) {
			if (sz.height == 'auto')
				box.style.height = '';
			else if (sz.height != '')
				box.style.height = jq.px(zk(n).revisedHeight(sz.height, true));
			else
				box.style.height = this._height ? this._height : '';
		}
		if (sz.width !== undefined) {
			if (sz.width == 'auto')
				box.style.width = '';
			else if (sz.width != '')
				box.style.width = jq.px(zk(n).revisedWidth(sz.width, true));
			else
				box.style.width = this._width ? this._width : '';
		}
		return {height: n.offsetHeight, width: n.offsetWidth};
	}
});
//handle autodisabled buttons
zul.wgt.ADBS = zk.$extends(zk.Object, {
	$init: function (ads) {
		this._ads = ads;
	},
	onResponse: function () {
		for (var ads = this._ads, ad; ad = ads.shift();)
			ad.setDisabled(false);
		zWatch.unlisten({onResponse: this});
	}
});
