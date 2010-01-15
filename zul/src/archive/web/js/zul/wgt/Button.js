/* Button.js

	Purpose:
		
	Description:
		
	History:
		Sat Nov  8 22:58:16     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	//IE adds extra height to first and last row, so fix it
	var _fixhgh = zk.ie ? function (btn) {
		var n = btn.$n(),
			box = btn.$n('box');
		if (n.style.height && box.offsetHeight) {
			var cellHgh = zk.parseInt(jq(box.rows[0].cells[0]).css('height'));
			if (cellHgh != box.rows[0].cells[0].offsetHeight) {
				box.rows[1].style.height = jq.px0(box.offsetHeight -
					cellHgh - zk.parseInt(jq(box.rows[2].cells[0]).css('height')));
			}
		}
	}: zk.$void;

/**
 * A button.
 * <p>Default {@link #getZclass}: z-button.
 */
zul.wgt.Button = zk.$extends(zul.LabelImageWidget, {
	_orient: "horizontal",
	_dir: "normal",
	_tabindex: -1,

	$define: {
		/** Returns the href that the browser shall jump to, if an user clicks
		 * this button.
		 * <p>Default: null. If null, the button has no function unless you
		 * specify the onClick event listener.
		 * <p>If it is not null, the onClick event won't be sent.
		 * @return String
		 */
		/** Sets the href.
		 * @param String href
		 */
		href: null,
		/** Returns the target frame or window.
		 *
		 * <p>Note: it is useful only if href ({@link #setHref}) is specified
		 * (i.e., use the onClick listener).
		 *
		 * <p>Default: null.
		 * @return String
		 */
		/** Sets the target frame or window.
		 * @param String target the name of the frame or window to hyperlink.
		 */
		target: null,
		/** Returns the direction.
		 * <p>Default: "normal".
		 * @return String
		 */
		/** Sets the direction.
		 * @param String dir either "normal" or "reverse".
		 */
		dir: _zkf = function () {
			this.updateDomContent_();
		},
		/** Returns the orient.
		 * <p>Default: "horizontal".
		 * @return String
		 */
		/** Sets the orient.
		 * @param String orient either "horizontal" or "vertical".
		 */
		orient: _zkf,
		/** Returns whether it is disabled.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether it is disabled.
		 * @param boolean disabled
		 */
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
		/** Returns the tab order of this component.
		 * <p>Default: -1 (means the same as browser's default).
		 * @return int
		 */
		/** Sets the tab order of this component.
		 * @param int tabindex
		 */
		tabindex: function (v) {
			var n = this.$n();
			if (n) (this.$n('btn') || n).tabIndex = v >= 0 ? v: '';
		},
		/** Returns a list of component IDs that shall be disabled when the user
		 * clicks this button.
		 *
		 * <p>To represent the button itself, the developer can specify <code>self</code>.
		 * For example, 
		 * <pre><code>
		 * button.setId('ok');
		 * wgt.setAutodisable('self,cancel');
		 * </code></pre>
		 * is the same as
		 * <pre><code>
		 * button.setId('ok');
		 * wgt.setAutodisable('ok,cancel');
		 * </code></pre>
		 * that will disable
		 * both the ok and cancel buttons when an user clicks it.
		 *
		 * <p>The button being disabled will be enabled automatically
		 * once the client receives a response from the server.
		 * In other words, the server doesn't notice if a button is disabled
		 * with this method.
		 *
		 * <p>However, if you prefer to enable them later manually, you can
		 * prefix with '+'. For example,
		 * <pre><code>
		 * button.setId('ok');
		 * wgt.setAutodisable('+self,+cancel');
		 * </code></pre>
		 *
		 * <p>Then, you have to enable them manually such as
		 * <pre><code>if (something_happened){
		 *  ok.setDisabled(false);
		 *  cancel.setDisabled(false);
		 *</code></pre>
		 *
		 * <p>Default: null.
		 * @return String
		 */
		/** Sets whether to disable the button after the user clicks it.
		 * @param String autodisable
		 */
		autodisable: null,
		/** Returns non-null if this button is used for file upload, or null otherwise.
		 * Refer to {@link #setUpload} for more details.
		 * @return String
		 */
		/** Sets the JavaScript class at the client to handle the upload if this
		 * button is used for file upload.
		 * <p>Default: null.
		 *
		 * <p>For example, the following example declares a button for file upload:
		 * <pre><code>
		 * button.setLabel('Upload');
		 * button.setUpload('true');
		 * </code></pre>
		 *
		 * <p>If you want to customize the handling of the file upload at
		 * the client, you can specify a JavaScript class when calling
		 * this method:
		 * <code>button.setUpload('foo.Upload');</code>
		 *
		 * <p> Another options for the upload can be specified as follows:
		 *  <pre><code>button.setUpload('true,maxsize=-1,native');</code></pre>
		 *  <ul>
		 *  <li>maxsize: the maximal allowed upload size of the component, in kilobytes, or 
		 * a negative value if no limit.</li>
		 *  <li>native: treating the uploaded file(s) as binary, i.e., not to convert it to
		 * image, audio or text files.</li>
		 *  </ul>
		 *  
		 * @param String upload a JavaScript class to handle the file upload
		 * at the client, or "true" if the default class is used,
		 * or null or "false" to disable the file download (and then
		 * this button behaves like a normal button).
		 */
		upload: function (v) {
			var n = this.$n();
			if (n && !this._disabled) {
				this._cleanUpld();
				if (v && v != 'false') this._initUpld();
			}
		}
	},

	//super//
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

		img = '<img src="' + img + '" onmousedown="return false;" align="absmiddle" />';
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

	//@Override
	setWidth: zk.ie ? function (v) {
		this.$supers('setWidth', arguments);

		if (this.desktop && this._mold == 'trendy')
			this.$n('box').style.width = !v || v == "auto" ? "": "100%";
	}: function () {
		this.$supers('setWidth', arguments);
	},
	//@Override
	setHeight: zk.ie ? function (v) {
		var trendy;
		if (trendy = (this.desktop && this._mold == 'trendy'))
			this.$n('box').rows[1].style.height = "";

		this.$supers('setHeight', arguments);

		if (trendy)
			_fixhgh(this);
	}: function () {
		this.$supers('setHeight', arguments);
	},

	onSize: _zkf = zk.ie ? function () {
		_fixhgh(this);
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
					zUtl.go(href, {target: this._target || (evt.data.ctrlKey ? '_blank' : '')});
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
		var n = this.$n();
		if (sz.height !== undefined) {
			if (sz.height == 'auto')
				n.style.height = '';
			else if (sz.height != '')
				n.style.height = jq.px0(this._mold == 'trendy' ? zk(n).revisedHeight(sz.height, true) : sz.height);
			else
				n.style.height = this._height ? this._height : '';
			_fixhgh(this);
		}
		if (sz.width !== undefined) {
			if (sz.width == 'auto')
				n.style.width = '';
			else if (sz.width != '')
				n.style.width = jq.px0(this._mold == 'trendy' ? zk(n).revisedWidth(sz.width, true) : sz.width);
			else
				n.style.width = this._width ? this._width : '';
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

})();
