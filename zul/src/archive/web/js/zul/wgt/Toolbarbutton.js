/* Toolbarbutton.js

	Purpose:

	Description:

	History:
		Sat Dec 22 12:58:43	 2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A toolbar button.
 *
 * <p>Non-xul extension: Toolbarbutton supports {@link #getHref}. If {@link #getHref}
 * is not null, the onClick handler is ignored and this element is degenerated
 * to HTML's A tag.
 *
 * <p>Default {@link #getZclass}: z-toolbarbutton.
 */
zul.wgt.Toolbarbutton = zk.$extends(zul.LabelImageWidget, {
	_orient: "horizontal",
	_dir: "normal",
	_tabindex: -1,

	$define: {
		/** Returns whether it is disabled.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether it is disabled.
		 * @param boolean disabled
		 */
		disabled: function () {
			this.rerender(); //bind and unbind
		},
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
		/** Returns the tab order of this component.
		 * <p>Default: -1 (means the same as browser's default).
		 * @return int
		 */
		/** Sets the tab order of this component.
		 * @param int tabindex
		 */
		tabindex: function (v) {
			var n = this.$n();
			if (n) n.tabIndex = v < 0 ? '' : v;
		},
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

	// super//
	getZclass: function(){
		var zcls = this._zclass;
		return zcls ? zcls : "z-toolbarbutton";
	},
	getTextNode: function () {
		return this.$n().firstChild.firstChild;
	},
	bind_: function(){
		this.$supers(zul.wgt.Toolbarbutton, 'bind_', arguments);
		if (!this._disabled) {
			var n = this.$n();
			this.domListen_(n, "onFocus", "doFocus_")
				.domListen_(n, "onBlur", "doBlur_");
		}
		if (!this._disabled && this._upload) this._initUpld();
	},
	unbind_: function(){
		if (!this._disabled && this._upload) this._cleanUpld();
		var n = this.$n();
		this.domUnlisten_(n, "onFocus", "doFocus_")
			.domUnlisten_(n, "onBlur", "doBlur_");

		this.$supers(zul.wgt.Toolbarbutton, 'unbind_', arguments);
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
	domContent_: function(){
		var label = zUtl.encodeXML(this.getLabel()), img = this.getImage();
		if (!img)
			return label;

		img = '<img src="' + img + '" align="absmiddle" />';
		var space = "vertical" == this.getOrient() ? '<br/>' : '&nbsp;';
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
		var attr = this.$supers('domAttrs_', arguments),
			v = this.getTabindex();
		if (v)
			attr += ' tabIndex="' + v + '"';
		return attr;
	},	
	doClick_: function(evt){
		if (!this.isDisabled()) {
			this.fireX(evt);

			if (!evt.stopped) {
				var href = this._href;
				if (href)
					zUtl.go(href, {target: this._target || (evt.data.ctrlKey ? '_blank' : '')});
				this.$super('doClick_', evt, true);
			}
		}
	},
	doMouseOver_: function (evt) {
		if (!this.isDisabled()) {
			jq(this).addClass(this.getZclass() + '-over');
			this.$supers('doMouseOver_', arguments);
		}
	},
	doMouseOut_: function (evt) {
		if (!this.isDisabled()) {
			jq(this).removeClass(this.getZclass() + '-over');
			this.$supers('doMouseOut_', arguments);
		}
	}
});
