/* Groupbox.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 12:39:24     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Groups a set of child elements to have a visual effect.
 * <p>Default {@link #getZclass}: "z-groupbox". If {@link #getMold()} is 3d,
 * "z-groupbox-3d" is assumed.
 *
 * <p>Events: onOpen.
 *
 */
zul.wgt.Groupbox = zk.$extends(zul.Widget, {
	_open: true,
	_closable: true,

	$define: { //zk.def
		/** Returns whether this groupbox is open.
		 * <p>Default: true.
		 * @return boolean
		 */
		/** Opens or closes this groupbox.
		 * @param boolean open
		 */
		open: function (open, fromServer) {
			var node = this.$n();
			if (node && this._closable) {
				if (open && this._isDefault())
						jq(node).removeClass(this.getZclass() + "-colpsd");
				zk(this.getCaveNode())[open?'slideDown':'slideUp'](this);
				if (!fromServer) this.fire('onOpen', {open:open});
			}
		},
		/** Returns whether user can open or close the group box.
		 * In other words, if false, users are no longer allowed to
		 * change the open status (by clicking on the title).
		 *
		 * <p>Default: true.
		 * @return boolean
		 */
		/** Sets whether user can open or close the group box.
		 * @param boolean closable
		 */
		closable: _zkf = function () {
			this._updDomOuter();
		},
		/** Returns the CSS style for the content block of the groupbox.
		 * Used only if {@link #getMold} is not default.
		 * @return String
		 */
		/** Sets the CSS style for the content block of the groupbox.
		 * Used only if {@link #getMold} is not default.
		 *
		 * <p>Default: null.
		 * @param String contentStyle
		 */
		contentStyle: _zkf,
		/** Returns the style class used for the content block of the groupbox.
		 * Used only if {@link #getMold} is not default.
		 * @return String
		 */
		/** Sets the style class used for the content block.
		 * @param String contentSclass
		 */
		contentSclass: _zkf,
		/** Returns the title of the groupbox.
		 * @return String
		 * @since 6.0
		 */
		/** Sets the title of the groupbox.
		 * @param String title
		 * @since 6.0
		 */
		title: _zkf
	},
	_isDefault: function () {
		return this._mold == 'default';
	},
	_updDomOuter: function () {
		this.rerender(zk.Skipper.nonCaptionSkipper);
	},
	_contentAttrs: function () {
		var html = ' class="', s = this._contentSclass,
			cap = this.caption,
			zcls = this.getZclass();
		if (s) html += s + ' ';
		html += zcls + '-cnt';
		if (!this.getTitle() && !cap) {
			html += ' '+ zcls + '-notitle';
		}
		html += '"';

		s = this._contentStyle;
		if (cap) s = 'border-top:0;' + (s||'');
		if (!this._open) s = 'display:none;' + (s||'');
		if (s) html += ' style="' + s + '"';
		return html;
	},
	_redrawCave: function (out, skipper) { //reserve for customizing
		out.push('<div id="', this.uuid, '-cave"', this._contentAttrs(), '>');
	
		if (!skipper)
			for (var w = this.firstChild, cap = this.caption; w; w = w.nextSibling)
				if (w != cap)
					w.redraw(out);

		out.push('</div>');
	},

	setHeight: function () {
		this.$supers('setHeight', arguments);
		if (this.desktop) this._fixHgh();
	},
	_fixHgh: function () {
		var hgh = this.$n().style.height;
		if (hgh && hgh != "auto") {
			var n;
			if (n = this.$n('cave')) {
				if (zk.ie6_) n.style.height = "";
				var wgt = this,
					$n = zk(n),
					fix = function() {
					// IE6 will count out the height of header in default mold while
					// calculate revisedHeight if vflex != min
					var fixh = 0;
					if (zk.ie6_ && wgt._isDefault()) {
						if (wgt.getVflex() != 'min')
							fixh = jq(wgt.$n()).find('.' + wgt.getZclass() + '-header').height();
						if (!wgt.caption)
							fixh += 1;
					}

					// B50-ZK-487: height isuue in the groupbox (with specified caption)
					n.style.height = $n.revisedHeight($n.vflexHeight(), true) + fixh + "px";
				};
				fix();
				if (zk.gecko) setTimeout(fix, 0);
					//Gecko bug: height is wrong if the browser visits the page first time
					//(reload won't reproduce the problem) test case: test/z5.zul
			}
		}
	},
	getParentSize_: zk.ie6_ ? function (p) {
		var zkp = zk(p),
			hgh,
			wdh,
			s = p.style;
		if (s.width.indexOf('px') >= 0) {
			wdh = zk.parseInt(s.width);
		} else {
			// Fixed for B50-3357573.zul
			var n = this.$n(),
				oldVal = n.style.display;
			n.style.display = 'none';
			wdh = zkp.revisedWidth(p.offsetWidth);
			n.style.display = oldVal;
		}
		if (s.height.indexOf('px') >= 0) {
			hgh = zk.parseInt(s.height);
		}
		return {height: hgh || zkp.revisedHeight(p.offsetHeight),
					width: wdh || zkp.revisedWidth(p.offsetWidth)};
	} : function(p) {
		return this.$supers('getParentSize_', arguments);
	},
	// B60-ZK-562: Groupbox vflex=min is wrong
	setFlexSizeH_: function(n, zkn, height, isFlexMin) {
		var h = 0, 
			margins = zkn.sumStyles("tb", jq.margins);
		if (isFlexMin && (this.caption || this._title)) {
			// B60-ZK-562
			var node = this.$n(),
				c;
			for (c = n.firstChild; c; c = c.nextSibling)
				h += jq(c).outerHeight();
		} else
			h = zkn.revisedHeight(height, true); // excluding margin for F50-3000873.zul and B50-3285635.zul
		n.style.height = jq.px0(h);
			
		// fixed for B50-3317729.zul on webkit
		if (zk.safari) {
			margins -= zkn.sumStyles("tb", jq.margins);
			if (margins) 
				n.style.height = jq.px0(h + margins);
		}
	},
	//watch//
	onSize: function () {
		this._fixHgh();
		// B50-ZK-487
		// classicblue is deprecated and 
		// shadow not used in breeze, sapphire and silvertail,
	},
	updateDomStyle_: function () {
		this.$supers('updateDomStyle_', arguments);
		if (this.desktop) this.onSize();
	},

	//super//
	focus_: function (timeout) {
		var cap = this.caption;
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w != cap && w.focus_(timeout))
				return true;
		return cap && cap.focus_(timeout);
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls ? zcls: this._isDefault() ? "z-groupbox": "z-groupbox-3d";
	},
	bind_: function () {
		this.$supers(zul.wgt.Groupbox, 'bind_', arguments);
		zWatch.listen({onSize: this});
		var tt;
		if (tt = this.$n('title'))
			this.domListen_(tt, 'onClick', '_doTitleClick');
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this});
		var tt;
		if (tt = this.$n('title'))
			this.domUnlisten_(tt, 'onClick', '_doTitleClick');
		this.$supers(zul.wgt.Groupbox, 'unbind_', arguments);
	},
	// will be called while click on title and title exists but no caption
	_doTitleClick: function () {
		this.setOpen(!this.isOpen());
		this.$supers('doClick_', arguments);
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.wgt.Caption)) {
			this.caption = child;
			this.rerender();
		}
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.caption) {
			this.caption = null;
			this.rerender();
		}
	},

	domClass_: function () {
		var html = this.$supers('domClass_', arguments);
		if (!this._open) {
			if (html) html += ' ';
			html += this.getZclass() + '-colpsd';
		}
		return html;
	},
	afterAnima_: function (visible) {
		if (!visible && this._isDefault())
			jq(this.$n()).addClass(this.getZclass() + "-colpsd");
		this.$supers('afterAnima_', arguments);
	}
});
