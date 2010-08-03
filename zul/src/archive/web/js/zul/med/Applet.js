/* Applet.js

	Purpose:
		
	Description:
		
	History:
		Wed Mar 25 17:11:55     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The multimedia widgets, such as applet and audio.
 */
//zk.$package('zul.med');

/**
 * A generic applet component.
 * 
 * <p>
 * Non XUL extension.
 * <p>Note: {@link #setVisible} with false cannot work in IE. (Browser's limitation) 
 */
zul.med.Applet = zk.$extends(zul.Widget, {
	$init: function() {
		this._params = {};
		this.$supers('$init', arguments);
	},

	$define: {
		/** Return the applet class to run.
		 * @return String
		 */
		/** Sets the applet class to run.
		 * @param String code
		 */
		code: _zkf = function () {
			this.rerender();
		},
		/** Return a relative base URL for applets specified in {@link #setCode} (URL).
		 * @return String
		 */
		/** Sets a relative base URL for applets specified in {@link #setCode} (URL).
		 * @param String codebase
		 */
		codebase: _zkf,
		/** Returns the location of an archive file (URL).
		 * @return String
		 * @since 5.0.3
		 */
		/** Sets the location of an archive file (URL).
		 * @param String archive
		 * @since 5.0.3
		 */
		archive: _zkf,
		/** Returns whether the applet is allowed to access the scripting object.
		 * @return boolean
		 * @since 5.0.3
		 */
		/** Sets whether the applet is allowed to access the scripting object.
		 * @param boolean myscript
		 * @since 5.0.3
		 */
		mayscript: function (v) {
			var n;
			if (n = this.$n())
				n.mayscript = v;
		},
		/** Returns the alignment of an applet according to surrounding elements.
		 * @return String
		 * @since 5.0.3
		 */
		/** Sets the alignment of an applet according to surrounding elements.
		 * @param String align
		 * @since 5.0.3
		 */
		align: function (v) {
			var n;
			if (n = this.$n())
				n.align = v;
		},
		/** Returns the horizontal spacing around an applet.
		 * @return String
		 * @since 5.0.3
		 */
		/** Sets the horizontal spacing around an applet.
		 * @param String hspace
		 * @since 5.0.3
		 */
		hspace: function (v) {
			var n;
			if (n = this.$n())
				n.hspace = v;
		},
		/** Returns the vertical spacing around an applet.
		 * @return String
		 * @since 5.0.3
		 */
		/** Sets the vertical spacing around an applet.
		 * @param String vspace
		 * @since 5.0.3
		 */
		vspace: function (v) {
			var n;
			if (n = this.$n())
				n.vspace = v;
		}
	},
	/** Invokes the function of the applet running at the client.
	 */
	invoke: zk.ie ? function() {
		var n = this.$n(),
			len = arguments.length;
		if (n && len >= 1) {
			var single = len < 3,
				begin = single ? '(' : '([',
				end = single ? ')' : '])',
				expr = "n." + arguments[0] + begin;
			for (var j = 1; j < len;) {
				if (j != 1) expr += ',';
				var s = arguments[j++];
				expr += '"' + (s ? s.replace('"', '\\"'): '') + '"';
			}
			try {
				eval(expr + end);
			} catch (e) {
				zk.error("Failed to invoke applet's method: "+expr+'\n'+e.message);
			}
		}
	}: function(){
		var n = this.$n();
		if (n && arguments.length >= 1) {
			var fn = arguments[0],
				func = n[fn];
			if (!func) {
				zk.error("Method not found: "+fn);
				return;
			}
			try {
				var args = [],
					arrayArg = [];
				if (arguments.length < 3) {
					if (arguments[1]) 
						args.push(arguments[1]);
				} else {
					for (var j = 1, len = arguments.length; j < len;) 
						arrayArg.push(arguments[j++]);
					args.push(arrayArg);
				}
				func.apply(n, args);
			} catch (e) {
				zk.error("Failed to invoke applet's method: "+fn+'\n'+e.message);
			}
		}
	},
	/** Returns the value of the specified filed.
	 * @param String name
	 * @return String
	 */
	getField: function (name) {
		var n = this.$n();
		return n ? n[name]: null;
	},
	/** Sets the value of the specified filed.
	 * @param String name
	 * @param String value
	 */
	setField: function (name, value) {
		var n = this.$n();
		if (n)
			try {
				n[name] = value;
			} catch(e) {
				zk.error("Failed to set applet's field: "+ name+'\n'+e.message);
			}
	},
	/** Sets the param. Notice that it is meaningful only if it is called
	 * before redraw. For example, <code>setParam('attr1', 'value1')</code> 
	 * gives a <code>param</code> tag under <code>applet</code> tag with name 
	 * <code>attr1</code>, value <code>value1</code>.
	 * There are two format:
	 * setParam(nm, val)
	 * and
	 * setParam([nm, val])
	 * @param String nm
	 * @param String val
	 */
	setParam: function (nm, val) {
		if (arguments.length == 1) {
			val = nm[1];
			nm = nm[0];
		}
		if (val != null) this._params[nm] = val;
		else delete this._params[nm];
	},
	/** Sets the params map. It should only be called before redraw.
	 * @param Map m A map of param pairs, as applet parameters. For example, 
	 * <code>{attr1:'value1', attr2:'value2'}</code> gives two <code>param</code> 
	 * tags under <code>applet</code> tag with names <code>attr1, attr2</code>, 
	 * values <code>value1, value2</code> respectively.
	 * <pre><code>
	 * <param 
	 * </code></pre>
	 * @since 5.0.4
	 */
	setParams: function (m) {
		this._params = m;
	},
	
	//super
	domAttrs_: function(no){
		return this.$supers('domAttrs_', arguments)
				+ ' code="' + (this._code || '') + '"'
				+ ' codebase="' + (this._codebase || '') + '"'
				+ zUtl.appendAttr("archive", this._archive)
				+ zUtl.appendAttr("align", this._align)
				+ zUtl.appendAttr("hspace", this._hspace)
				+ zUtl.appendAttr("vspace", this._vspace)
				+ zUtl.appendAttr("mayscript", this._mayscript);
	},
	domStyle_: function (no) {
		return this.$supers('domStyle_', arguments)
			+ "visibility:visible;"; //bug 2815049
	},

	_outParamHtml: function (out) {
		var params = this._params;
		for (var nm in params)
			out.push('<param name="', zUtl.encodeXML(nm), '" value="', zUtl.encodeXML(params[nm]), '"/>');
	}
});
