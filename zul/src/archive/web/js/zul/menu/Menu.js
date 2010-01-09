/* Menu.js

	Purpose:

	Description:

	History:
		Thu Jan 15 09:02:33     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * An element, much like a button, that is placed on a menu bar.
 * When the user clicks the menu element, the child {@link Menupopup}
 * of the menu will be displayed.
 * This element is also used to create submenus (of {@link Menupopup}.
 * 
 * <p>Default {@link #getZclass}: z-mean.
 */
zul.menu.Menu = zk.$extends(zul.LabelImageWidget, {
	$define: {
		/** Returns the embedded content (i.e., HTML tags) that is
		 * shown as part of the description.
		 * <p>It is useful to show the description in more versatile way.
		 * <p>Default: empty ("").
		 * @return String
		 */
		/** Sets the embedded content (i.e., HTML tags) that is
		 * shown as part of the description.
		 *
		 * <p>It is useful to show the description in more versatile way.
		 * 
		 * <p>There is a way to create Colorbox automatically by using
		 * #color=#RRGGBB, usage example <code>setContent("#color=FFFFFF")</code>
		 *
		 * @param String content
		 */
		content: function (content) {
			if (!content || content.length == 0) return;
			
			if (!this._contentHandler) {
				if (zk.feature.professional) {
					var self = this;
					zk.load('zkex.inp', null, function () { 
						self._contentHandler = new zkex.inp.ContentHandler(self, content);
					});
					return;
				}
				this._contentHandler = new zul.menu.ContentHandler(this, content);
			} else
				this._contentHandler.setContent(content);
		}
	},
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = ['<span id="', this.uuid, '-img" class="', this.getZclass(), '-img"'];
			
		img.push(this._image ? ' style="background-image:url(' + this._image + ')"' : '', '></span>', label ? ' ' + label : '');
		return img.join('');
	},
	/** Returns whether this is an top-level menu, i.e., not owning
	 * by another {@link Menupopup}.
	 * @return boolean
	 */
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
		if (child.$instanceof(zul.menu.Menupopup)) {
			this.menupopup = child;

			if (this._contentHandler)
				this._contentHandler.destroy();
		}
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.menupopup) {
			this.menupopup = null;

			if (this._contentHandler)
				this._contentHandler.setContent(this._content);
		}
	},
	/** Returns the {@link Menubar} it owns, or null if not available.
	 * @return zul.menu.Menubar
	 */
	getMenubar: function () {
		for (var p = this.parent; p; p = p.parent)
			if (p.$instanceof(zul.menu.Menubar))
				return p;
		return null;
	},
	onShow: function () {
		if (this._contentHandler)
			this._contentHandler.onShow();
	},
	onFloatUp: function (ctl) {
		if (this._contentHandler)
			this._contentHandler.onFloatUp(ctl);
	},
	onHide: function () {
		if (this._contentHandler)
			this._contentHandler.onHide();
	},
	bind_: function () {
		this.$supers('bind_', arguments);

		var anc = this.$n('a'),
			type = this._contentType;
		if (!this.isTopmost()) {
			var	n = this.$n();
			this.domListen_(anc, "onFocus", "doFocus_")
				.domListen_(anc, "onBlur", "doBlur_")
				.domListen_(n, "onMouseOver")
				.domListen_(n, "onMouseOut");
		} else {
			this.domListen_(anc, "onMouseOver")
				.domListen_(anc, "onMouseOut");
		}

		if (this._contentHandler)
			this._contentHandler.bind();
	},
	unbind_: function () {
		if (!this.isTopmost()) {
			var anc = this.$n('a'),
				n = this.$n();
			this.domUnlisten_(anc, "onFocus", "doFocus_")
				.domUnlisten_(anc, "onBlur", "doBlur_")
				.domUnlisten_(n, "onMouseOver")
				.domUnlisten_(n, "onMouseOut");
		} else {
			var anc = this.$n('a');
			this.domUnlisten_(anc, "onMouseOver")
				.domUnlisten_(anc, "onMouseOut");
		}

		if (this._contentHandler)
			this._contentHandler.unbind();

		this.$supers('unbind_', arguments);
	},
	doClick_: function (evt) {
		if (this.isTopmost() && !jq.isAncestor(this.$n('a'), evt.domTarget)) return;
		
		var node = this.$n();
		if (this.menupopup) {
			jq(this.$n('a')).addClass(this.getZclass() + '-body-seld');
			this.menupopup._shallClose = false;
			if (this.isTopmost())
				this.getMenubar()._lastTarget = this;
			if (this.isListen('onClick')) {
				var arrorWidth = 12, //note : /img/menu/btn-arrow.gif : width = 12
					clk = jq(node).find('TABLE'),
					offsetWidth = zk(clk).offsetWidth(),
					clickArea = offsetWidth - arrorWidth,
					ofs = zk(clk).revisedOffset(),
					clickOffsetX = evt.domEvent.clientX - ofs[0];

				if (clickOffsetX > clickArea) {
					this._openPopup();
					Event.stop(evt);
				} else {
					jq(this.$n('a')).removeClass(this.getZclass() + '-body-seld');
					this.fireX(evt);
				}		
			} else {
				this._openPopup();	
			}
		}
	},
	_openPopup: function () {
		if (!this.menupopup.isOpen())
			this.menupopup.open();	
		else {
			var anc = this.menupopup.$n('a');
			if (anc) anc.focus(); // force to get a focus 
		}			
	},
	_doMouseOver: function (evt) { //not zk.Widget.doMouseOver_
		if (this.$class._isActive(this)) return;

		var	topmost = this.isTopmost();
		if (topmost && zk.ie && !jq.isAncestor(this.$n('a'), evt.domTarget))
				return; // don't activate

		if (!topmost) {
			if (this.menupopup) this.menupopup._shallClose = false;
			zWatch.fire('onFloatUp', this); //notify all
			if (this.menupopup && !this.menupopup.isOpen()) this.menupopup.open();
		} else {
			var menubar = this.getMenubar();
			if (this.menupopup && menubar.isAutodrop()) {
				menubar._lastTarget = this;
				this.menupopup._shallClose = false;
				zWatch.fire('onFloatUp', this); //notify all
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
		this.$class._addActive(this);
	},
	_doMouseOut: function (evt) { //not zk.Widget.doMouseOut_
		if (zk.ie && jq.isAncestor(this.$n('a'), evt.domEvent.relatedTarget || evt.domEvent.toElement))
			return; // don't deactivate
	
		var	topmost = this.isTopmost();
		if (topmost) {
			this.$class._rmActive(this);
			if (this.menupopup && this.getMenubar().isAutodrop()) {
				if (this.menupopup.isOpen()) this.menupopup._shallClose = true;
				zWatch.fire('onFloatUp', this, {timeout: 10}); //notify all
			}
		} else if (this.menupopup && !this.menupopup.isOpen())
			this.$class._rmActive(this);
	}
}, {
	_isActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.$n('a') : wgt.$n(),
			cls = wgt.getZclass() + (top ? '-body-over' : '-over');
		return jq(n).hasClass(cls);
	},
	_addActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.$n('a') : wgt.$n(),
			cls = wgt.getZclass() + (top ? '-body-over' : '-over');
		jq(n).addClass(cls);
		if (!top && wgt.parent.parent.$instanceof(zul.menu.Menu))
			this._addActive(wgt.parent.parent);
	},
	_rmActive: function (wgt) {
		var top = wgt.isTopmost(),
			n = top ? wgt.$n('a') : wgt.$n(),
			cls = wgt.getZclass() + (top ? '-body-over' : '-over');
		jq(n).removeClass(cls);
	}
});

zul.menu.ContentHandler = zk.$extends(zk.Object, {
	 $init: function(wgt, content) {
	 	this.$supers('$init', arguments);
		this._wgt = wgt;
		this._content = content;
	 },
	 setContent: function (content) {
	 	if (this._content != content) {
			this._content = content;
			this._wgt.rerender();	
		}
	 },
	 redraw: function (out) {
	 	var wgt = this._wgt,
			zcls = wgt.getZclass();

	 	out.push('<div id="', wgt.uuid, '-cnt-pp" class="', zcls,
		'-cnt-pp" style="display:none"><div class="', zcls,'-cnt-body">', this._content, '</div></div>');
	 },
	 bind: function () {
				
	 	var wgt = this._wgt;
	 	if (!wgt.menupopup) {
			wgt.domListen_(wgt.$n(), 'onClick', 'onShow');
			zWatch.listen({onFloatUp: wgt, onHide: wgt});
		}
		
		this._pp = jq('#' + wgt.uuid + '-' + 'cnt-pp')[0];
	 },
	 unbind: function () {
	 	var wgt = this._wgt;
	 	if (!wgt.menupopup) {
			if (this._shadow) {
				this._shadow.destroy();
				this._shadow = null;
			}
			wgt.domUnlisten_(wgt.$n(), 'onClick', 'onShow');
			zWatch.unlisten({onFloatUp: wgt, onHide: wgt});
		}

		this._pp = null;
	 },
	 onShow: function () {
	 	var wgt = this._wgt,
			pp = this._pp;
		if (!pp) return;
			
		pp.style.width = pp.style.height = "auto";
		pp.style.position = "absolute";
		pp.style.overflow = "auto";
		pp.style.display = "block";
		pp.style.zIndex = "88000";
			
		jq(pp).zk.makeVParent();
		zk(pp).position(wgt.$n(), this.getPosition());
		this.syncShadow();
	 },
	 onHide: function () {
		var pp = this._pp;
		if (!pp || !zk(pp).isVisible()) return;

		pp.style.display = "none";
		jq(pp).zk.undoVParent();
		this.hideShadow();	
	 },
	 onFloatUp: function (ctl) {
		if (!zUtl.isAncestor(this._wgt, ctl.origin))
			this.onHide();
	 },
	 syncShadow: function () {
	 	if (!this._shadow)
			this._shadow = new zk.eff.Shadow(this._wgt.$n("cnt-pp"), {stackup:(zk.useStackup === undefined ? zk.ie6_: zk.useStackup)});
		this._shadow.sync();
	 },
	 hideShadow: function () {
	 	this._shadow.hide();
	 },
	 destroy: function () {
	 	this._wgt.rerender();
	 },
	 getPosition: function () {
	 	var wgt = this._wgt;
		if (wgt.isTopmost()) {
			var bar = wgt.getMenubar();
			if (bar)
				return 'vertical' == bar.getOrient() ? 'end_before' : 'after_start';
		}
		return 'end_before';
	}
});