/* Button.js

	Purpose:
		
	Description:
		
	History:
		Sat Nov  8 22:58:16     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	//IE adds extra height to first and last row, so fix it
	var _fixhgh = zk.ie ? function (btn) {
		if (btn.desktop && btn._mold == 'trendy') {
			var n = btn.$n(),
				box = btn.$n('box');
			box.rows[1].style.height = "";
			box.style.height = !n.style.height || n.style.height == "auto" ? "": "100%";			
			if (n.style.height && box.offsetHeight) {
				var cellHgh = zk.parseInt(jq.css(box.rows[0].cells[0], 'height', 'styleonly'));
				if (cellHgh != box.rows[0].cells[0].offsetHeight) {
					box.rows[1].style.height = jq.px0(box.offsetHeight -
						cellHgh - zk.parseInt(jq.css(box.rows[2].cells[0], 'height', 'styleonly')));
				}
			}
		}
	}: zk.$void;
	var _fixwidth = zk.ie < 11 ? function (btn) {
		if (btn.desktop && btn._mold == 'trendy') {
			var width = btn.$n().style.width;
			btn.$n('box').style.width = !width || width == "auto" ? "": "100%";
		}
	}: zk.$void;

	function _initUpld(wgt) {
		if (!(zk.ie < 11) && wgt._mold == 'trendy')
			zWatch.listen({onSize: wgt});
		var v;
		if (v = wgt._upload)
			wgt._uplder = new zul.Upload(wgt, null, v);
	}
	
	function _cleanUpld(wgt) {
		var v;
		if (v = wgt._uplder) {
			if (!(zk.ie < 11) && wgt._mold == 'trendy')
				zWatch.unlisten({onSize: wgt});
			wgt._uplder = null;
			v.destroy();
		}
	}
	
	var _fixMouseupForClick = zk.safari || zk.gecko ? function (wgt, evt){
		//3276814:fix click then padding change issue for FF3 and Chrome/Safari
		/*
		 * Here we have these states :
		 * 1.down for mouse down in the widget  (down)
		 * 2.mouse up in the widget but click not fired (up in timeout)
		 * 3.mouse up in the widget and click event fired (null in timeout)
		 * 4.mouse up not in the widget (null)
		 */
		if ( wgt._fxcfg == 1 ) {
			var n = wgt.$n(); // the wgt may be detached while clicking quickly to invalidate itself.
			if (n && jq.contains(n, evt.domTarget)) {
				wgt._fxcfg = 2;
				if(wgt._fxctm) clearTimeout(wgt._fxctm);
				wgt._fxctm = setTimeout(function() {
					if (wgt._fxcfg == 2) {
						wgt.doClick_(new zk.Event(wgt, 'onClick', {}));
						wgt._fxctm = wgt._fxcfg = null;
					}
				}, 50);
			} else
				wgt._fxcfg = null;
		}
	}: zk.$void,

	_fixMousedownForClick = zk.safari || zk.gecko ?  function (wgt) {
		wgt._fxcfg = 1;
	}: zk.$void,

	_fixClick = zk.safari || zk.gecko  ? function (wgt) {
		if(wgt._fxctm) clearTimeout(wgt._fxctm);
		wgt._fxctm = wgt._fxcfg = null;
	}: zk.$void; 
	
var Button = 
/**
 * A button.
 * <p>Default {@link #getZclass}: z-button.
 */
zul.wgt.Button = zk.$extends(zul.LabelImageWidget, {
	_orient: "horizontal",
	_dir: "normal",
	_type: "button",
	//_tabindex: 0,

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
		/** Returns the button type.
		 * <p>Default: "button".
		 * @return String
		 */
		/** Sets the button type.
		 * @param String type either "button", "submit" or "reset".
		 */
		type: _zkf,
		/** Returns whether it is disabled.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether it is disabled.
		 * @param boolean disabled
		 */
		disabled: [
		    // B60-ZK-1176
		    // Autodisable should not re-enable when setDisabled(true) is called during onClick 
		    function (v, opts) {
		    	if (opts && opts.adbs)
		    		// called from zul.wgt.ADBS.autodisable
		    		this._adbs = true;	// Start autodisabling  
		    	else if (!opts || opts.adbs === undefined)
		    		// called somewhere else (including server-side)
		    		this._adbs = false;	// Stop autodisabling
		    	if (!v) {
		    		if (this._adbs) {
		    			// autodisable is still active, allow enabling
		    			this._adbs = false;
		    		} else if (opts && opts.adbs === false)
		    			// ignore re-enable by autodisable mechanism
		    			return this._disabled;
		    	}
		    	return v;
		    }, 
		    function (v, opts) {
		    	var mold = this._mold;
		    	if (mold == "os" || mold == "default") {
		    		var n = this.$n(),
						zclass = this.getZclass(),
						self = this,
						doDisable = function() {
		    				if (self.desktop) {
		    					var updateClass = (n.disabled = v) ? "addClass": "removeClass";
		    					jq(n)[updateClass](zclass + "-disd");
		    				}
		    			};
		    		// ZK-2042: delay the setting when the button's type is submit
		    		if (this._type == 'submit') 
		    			setTimeout(doDisable, 50);
		    		else
		    			doDisable();
		    	} else if (this.desktop)
	    			this.rerender(opts && opts.skip ? -1 : 0); //bind and unbind required (because of many CSS classes to update)
		    	
		    }
		],
		image: function (v) {
			if (v && this._preloadImage) zUtl.loadImage(v);
			if (this.isTableLayout_()) {
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
			if (n) (this.$n('btn') || n).tabIndex = v||'';
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
				_cleanUpld(this);
				if (v && v != 'false') _initUpld(this);
			}
		}
	},

	//super//
	setVisible: function (visible) {
		if (this._visible != visible) {
			this.$supers('setVisible', arguments);
			if (this._mold == 'trendy')
				this.onSize();
		}
		return this;
	},
	focus_: function (timeout) {
		// Bug ZK-1295: Disabled buttons cannot regain focus by re-enabling and then setting focus
		var wgt = this,
			btn = this.$n('btn') || this.$n();
		if (btn.disabled && !wgt._delayFocus) {
			wgt._delayFocus = true;
			setTimeout(function() {
				wgt.focus_(timeout);
				wgt._delayFocus = null;				
			}, 0);
		}		
		// Bug ZK-354: refer to _docMouseDown in mount.js for details
		if (!zk.focusBackFix || !this._upload)
			zk(btn).focus(timeout);
		return true;
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
		this.$supers(Button, 'bind_', arguments);

		var n;
		if (!this.isTableLayout_()) {
			n = this.$n();
		} else {
			if (this._disabled) return;

			zk(this.$n('box')).disableSelection();

			n = this.$n('btn');
			if (zk.ie < 11) zWatch.listen({onSize: this}); //always listen if zk.ie
		}

		this.domListen_(n, "onFocus", "doFocus_")
			.domListen_(n, "onBlur", "doBlur_");

		if (!this._disabled && this._upload) _initUpld(this);
	},
	unbind_: function () {
		_cleanUpld(this);

		var trendy = this._mold == 'trendy',
			n = !this.isTableLayout_() ? this.$n(): this.$n('btn');
		if (n) {
			this.domUnlisten_(n, "onFocus", "doFocus_")
				.domUnlisten_(n, "onBlur", "doBlur_");
		}
		if (zk.ie < 11 && trendy)
			zWatch.unlisten({onSize: this});

		this.$supers(Button, 'unbind_', arguments);
	},

	//@Override
	setWidth: zk.ie < 11 ? function (v) {
		this.$supers('setWidth', arguments);
		_fixwidth(this);
	}: function () {
		this.$supers('setWidth', arguments);
	},
	//@Override
	setHeight: zk.ie < 11 ? function (v) {
		this.$supers('setHeight', arguments);
		_fixhgh(this);
	}: function () {
		this.$supers('setHeight', arguments);
	},

	onSize: zk.ie < 11 ? function () {
		_fixhgh(this);
		_fixwidth(this);
		if (this._uplder)
			this._uplder.sync();
	} : function () {
		if (this._uplder)
			this._uplder.sync();
	},

	doFocus_: function (evt) {
		if (this.isTableLayout_())
			jq(this.$n('box')).addClass(this.getZclass() + "-focus");
		this.$supers('doFocus_', arguments);
	},
	doBlur_: function (evt) {
		if (this.isTableLayout_())
			jq(this.$n('box')).removeClass(this.getZclass() + "-focus");
		this.$supers('doBlur_', arguments);
	},
	doClick_: function (evt) {
		if (!evt.domEvent) // mobile will trigger doClick twice
			return;
		_fixClick(this);
		
		if (!this._disabled) {
			if (!this._upload)
				zul.wgt.ADBS.autodisable(this);
			else if (!zk.ie || zk.ie > 10)
				this._uplder.openFileDialog();
			var btn, fm;
			if (this._type != "button"
			&& (btn = this.$n('btn')) && (fm = btn.form)) {
				// ZK-664: Use a trendy button to submit a form will submit twice (IE only)
				// IE will submit directly without bubble up
				if (evt.domTarget != btn)
					if (this._type != "reset") zk(fm).submit();
					else fm.reset();
				return;
			}
			
			this.fireX(evt);

			if (!evt.stopped) {
				var href = this._href,
					isMailTo = href ? href.toLowerCase().startsWith('mailto:') : false;
				
				if (href) {
					// ZK-2506: use iframe to open a 'mailto' href
					if (isMailTo) {
						var ifrm = jq.newFrame('mailtoFrame', href, null);
						jq(ifrm).remove();
					} else {
						zUtl.go(href, {target: this._target || (evt.data.ctrlKey ? '_blank' : '')});
					}
				}
				
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
		if (!this._disabled && this != Button._curdn
		&& !((zk.ie < 11) && jq.isAncestor(this.$n('box'), evt.domEvent.relatedTarget || evt.domEvent.toElement)))
			jq(this.$n('box')).removeClass(this.getZclass() + "-over");
		this.$supers('doMouseOut_', arguments);
	},
	doMouseDown_: function () {
		//3276814:fix click then padding change issue for FF3 and Chrome/Safari
		//set it down to prevent the case for down in other place but up on this widget,
		//and down in this widget and up for other place
		
		_fixMousedownForClick(this);
		
		if (!this._disabled) {
			var zcls = this.getZclass();
			jq(this.$n('box')).addClass(zcls + "-clk")
				.addClass(zcls + "-over")
			if (!zk.ie || !this._uplder) zk(this.$n('btn')).focus(30);
				// ZK-2044: change focus will disable upload in IE
		}
		zk.mouseCapture = this; //capture mouse up
		this.$supers('doMouseDown_', arguments);
	},
	doMouseUp_: function (evt) {
		if (!this._disabled) {
			_fixMouseupForClick(this, evt);
			
			var zcls = this.getZclass();
			jq(this.$n('box')).removeClass(zcls + "-clk")
				.removeClass(zcls + "-over");
			if (zk.ie < 11 && this._uplder) zk(this.$n('btn')).focus(30);
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
			_fixwidth(this);
		}
		return {height: n.offsetHeight, width: n.offsetWidth};
	},
	/** Generates the HTML fragment at the right of the button layout.
	 * <p>Default: do nothing, override it as need.
	 * @param Array out an array of HTML fragments.
	 * @since 6.0.0
	 */
	renderIcon_: function (out) {
	},
	/** Generates the HTML fragment after the button layout table.
	 * <p>Default: do nothing, override it as need.
	 * @param Array out an array of HTML fragments.
	 * @since 6.0.0
	 */
	renderInner_: function (out) {
	},
	/** Returns whether have to listen to onfocus and onblur event on button element.
	 * @return boolean
	 * @since 6.0.0
	 */
	isTableLayout_: function () {
		return this._mold == 'trendy';
	}
});
//handle autodisabled buttons
zul.wgt.ADBS = zk.$extends(zk.Object, {
	$init: function (ads) {
		this._ads = ads;
	},
	onResponse: function () {
		for (var ads = this._ads, ad; ad = ads.shift();)
			// B60-ZK-1176: distinguish from other usages
			ad.setDisabled(false, {adbs: false, skip: true});
		zWatch.unlisten({onResponse: this});
	}
},{ //static
	/* Disable Targets and re-enable after response
	 * @param zk.Widget wgt
	 */
	autodisable: function(wgt) {
		var ads = wgt._autodisable, aded, uplder;
		if (ads) {
			ads = ads.split(',');
			for (var j = ads.length; j--;) {
				var ad = ads[j].trim();
				if (ad) {
					var perm;
					if (perm = ad.charAt(0) == '+')
						ad = ad.substring(1);
					ad = "self" == ad ? wgt: wgt.$f(ad);
					//B50-3304877: autodisable and Upload
					if (ad == wgt) { //backup uploader before disable
						uplder = wgt._uplder;
						wgt._uplder = null;
						wgt._autodisable_self = true;
					}
					if (ad && !ad._disabled) {
						// B60-ZK-1176: distinguish from other usages
						ad.setDisabled(true, {adbs: true, skip: true});
						if (wgt.inServer)
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
			if (uplder) {
				uplder._aded = aded;
				wgt._uplder = uplder;//zul.Upload.sendResult came on it.
			} else if (wgt.isListen('onClick', {asapOnly:true}))
				zWatch.listen({onResponse: aded});
			else
				setTimeout(function () {aded.onResponse();}, 800);
		}
	}
});

})();
