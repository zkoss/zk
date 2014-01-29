/* Groupbox.js

	Purpose:

	Description:

	History:
		Sun Nov 16 12:39:24     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
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
				if (open) {
					jq(node).removeClass(this.$s('collapsed'));
					zk(this).redoCSS(-1, {'fixFontIcon': true});
				}
				zk(this.getCaveNode())[open ? 'slideDown' : 'slideUp'](this);			
				
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
		var html = ' class="', s = this._contentSclass;
		if (s)
			html += s + ' ';
		html += this.$s('content') + '"';

		s = this._contentStyle;
		if (this.caption || this.getTitle()) // B60-ZK-987
			s = 'border-top:0;' + (s||'');
		if (!this._open)
			s = 'display:none;' + (s||'');
		if (s)
			html += ' style="' + s + '"';
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
		if (hgh && hgh != 'auto' && this.isOpen()) {
			var n;
			if (n = this.$n('cave')) {
				var $n = zk(n);
				// B50-ZK-487: height isuue in the groupbox (with specified caption)
				n.style.height = ($n.revisedHeight($n.vflexHeight(), true) - 
								 (this._isDefault() ? parseInt(jq(this).css('padding-top')) : 0)) + 'px';
					//if (zk.gecko) setTimeout(fix, 0);
					//Gecko bug: height is wrong if the browser visits the page first time
					//(reload won't reproduce the problem) test case: test/z5.zul
			}
		}
		if (this._isDefault()) {
			var title = this.$n('title'),
				cap = this.caption;
			if (cap)
				cap.$n().style.top = jq.px(zk(cap.$n('cave')).offsetHeight() / 2 * -1);
			if (title)
				title.style.top = jq.px(zk(this.$n('title-cnt')).offsetHeight() / 2 * -1);
		}
	},
	// B60-ZK-562: Groupbox vflex=min is wrong
	setFlexSizeH_: function(n, zkn, height, isFlexMin) {
		if (isFlexMin && (this.caption || this._title)) {
			// B60-ZK-562
			var node = this.$n(),
				c;
			height = this._isDefault() ? jq(this.$n('header')).outerHeight() : 0;
			for (c = n.firstChild; c; c = c.nextSibling)
				height += jq(c).outerHeight();
		}

		this.$supers('setFlexSizeH_', arguments);
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
	bind_: function () {
		this.$supers(zul.wgt.Groupbox, 'bind_', arguments);
		zWatch.listen({onSize: this});
		var tt;
		if (this.getTitle() && (tt = this.$n('title')))
			this.domListen_(tt, 'onClick', '_doTitleClick');
		if(zk.ie == 8) 
			zk(this).redoCSS();
		
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
	//@Override, Bug ZK-1524: caption children should not considered.
	getChildMinSize_: function (attr, wgt) {
		if (!wgt.$instanceof(zul.wgt.Caption))
			return this.$supers('getChildMinSize_', arguments);
	},

	domClass_: function () {
		var cls = this.$supers('domClass_', arguments);
		if (!this._isDefault()) {
			if (cls) cls += ' ';
			cls += this.$s('3d');
		}
		
		if (!this.caption && !this.getTitle()) {
			if (cls) cls += ' ';
			cls += ' '+ this.$s('notitle');
		}
			
		if (!this._open && this._isDefault()) {
			if (cls) cls += ' ';
			cls += this.$s('collapsed');
		}
		return cls;
	},
	afterAnima_: function (visible) {
		if (!visible && this._isDefault())
			jq(this.$n()).addClass(this.$s('collapsed'));		
				
		this.$supers('afterAnima_', arguments);
		
		// ZK-2138: parent should resize if parent has child with vflex
		var p = this.parent;
		for (var c = p.firstChild; c; c = c.nextSibling) {
			if (c == this)
				continue;
			var vflex = c.getVflex();
			if (vflex && vflex != 'min') {
				zUtl.fireSized(p);
				break;
			}
		}
	}
});
