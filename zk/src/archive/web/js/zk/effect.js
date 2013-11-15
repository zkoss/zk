/* effect.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov 10 14:45:53     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

script.aculo.us effects.js v1.7.0
Copyright (c) 2005, 2006 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function() {

	var _defSKUOpts, _useSKU;

/** The effects, such as mask and shadow.
 */
//zk.$package('zk.eff');
zk.eff = {
	shallStackup: function () {
		return _useSKU;
	},
	_skuOpts: function (opts) {
		return zk.$default(opts,
			_defSKUOpts || (_defSKUOpts = {stackup: zk.eff.shallStackup()}));
	}
};

/** The shadow effect.
 */
zk.eff.Shadow = zk.$extends(zk.Object, {
	$init: function (element, opts) {
		this.wgt = zk.Widget.$(element.id);
		this.opts = zk.eff._skuOpts(opts);
		this.node = element;
	},
	destroy: function () {
		jq(this.stackup).remove();
		jq(this.node).removeClass(this.wgt.getZclass() + '-shadow');
		this.wgt = this.node = this.stackup = null;
	},
	hide: function(){
		jq(this.stackup).hide();
		jq(this.node).removeClass(this.wgt.getZclass() + '-shadow');
	},
	sync: function () {
		var node = this.node, $node = jq(node);
		if (!node || !$node.zk.isVisible(true)) {
			if (this.opts.stackup && node) {
				if (!this.stackup) 
					this.stackup = jq.newStackup(node, node.id + '-sdwstk', node);
			}
			this.hide();
			return false;
		}
		
		$node.addClass(this.wgt.getZclass() + '-shadow');
		
		var opts = this.opts,
			l = node.offsetLeft, t = node.offsetTop,
			w = node.offsetWidth, h = node.offsetHeight,
			stackup = this.stackup;
			
		if(opts.stackup) {
			if(!stackup)
				stackup = this.stackup =
					jq.newStackup(node, node.id + '-sdwstk', node);

			var st = stackup.style;
			st.left = jq.px(l);
			st.top = jq.px(t);
			st.width = jq.px0(w);
			st.height = jq.px0(h);
			st.zIndex = zk.parseInt($node.css('zIndex'));
			st.display = 'block';
		}
		return true;
	},
	getBottomElement: function () {
		return this.stackup;
	}
});

	//Position a mask to cover the whole browser window.
	//it must be called as _syncMaskPos.call(this)
	function _syncMaskPos() {
		var n = this.mask,
			st = n.style;
		if (st.display != 'none') {
			var ofs = zk(n).toStyleOffset(jq.innerX(), jq.innerY());
			st.left = jq.px(ofs[0]);
			st.top = jq.px(ofs[1]);
			st.width = jq.px0(jq.innerWidth());
			st.height = jq.px0(jq.innerHeight());

			if (n = this.stackup)
				zk.set(n.style, st, ['left', 'top', 'width', 'height']);
		}
	}

/** A mask covers the browser window fully.
 * @disable(zkgwt)
 */
zk.eff.FullMask = zk.$extends(zk.Object, {
	/** The constructor of the full mask object.
	 * <p>To remove the full mask, invoke {@link #destroy}.
	 * @param Map opts [optional] the options. Allowed options:
	 * <ul>
	 * <li>{@link DOMElement} mask: the mask element if the mask was created somewhere else. Default: create a new one.</li>
	 * <li>{@link DOMElement} anchor: whether to insert the mask before.</li>
	 * <li>String id: the mask ID. Default: z_mask.</li>
	 * <li>int zIndex: z-index to assign. Default: defined in the CSS called z-modal-mask.</code>
	 * <li>boolean visible: whether it is visible</li> 
	 * </ul>
	 */
	$init: function (opts) {
		opts = zk.eff._skuOpts(opts);
		var mask = this.mask = jq(opts.mask||[], zk)[0];
		if (this.mask) {
			if (opts.anchor)
				opts.anchor.parentNode.insertBefore(mask, opts.anchor);
			if (opts.id) mask.id = opts.id;
			if (opts.zIndex != null) mask.style.zIndex = opts.zIndex;
			if (opts.visible == false) mask.style.display = 'none';
		} else {
			var maskId = opts.id || 'z_mask',
				html = '<div id="' + maskId + '" class="z-modal-mask"';//FF: don't add tabIndex
			if (opts.zIndex != null || opts.visible == false) {
				html += ' style="';
				if (opts.zIndex != null) html += 'z-index:' + opts.zIndex;
				if (opts.visible == false) html += ';display:none';
				html +='"';
			}

			html += '></div>'
			if (opts.anchor)
				jq(opts.anchor, zk).before(html);
			else
				jq(document.body).append(html);
			mask = this.mask = jq(maskId, zk)[0];
		}
		if (opts.stackup)
			this.stackup = jq.newStackup(mask, mask.id + '-mkstk');

		_syncMaskPos.call(this);

		var f;
		jq(mask).click(jq.Event.stop); //don't eat mousemove (drag depends on it)
		jq(window).resize(f = this.proxy(_syncMaskPos))
			.scroll(f);
	},
	/** Removes the full mask. You can not access this object any more.
	 */
	destroy: function () {
		var mask = this.mask, f;
		jq(mask).unbind('click', jq.Event.stop)
			.remove()
		jq(window).unbind('resize', f = this.proxy(_syncMaskPos))
			.unbind('scroll', f);
		jq(this.stackup).remove();
		this.mask = this.stackup = null;
	},
	/** Hide the full mask. Application developers rarely need to invoke this method.
	 * Rather, use {@link #sync} to synchronized the visual states.
	 */
	hide: function () {
		this.mask.style.display = 'none';
		if (this.stackup) this.stackup.style.display = 'none';
	},
	/** Synchronizes the visual states of the full mask with the specified element and the browser window.
	 * The visual states include the visibility and Z Index. 
	 */
	sync: function (el) {
		if (!zk(el).isVisible(true)) {
			this.hide();
			return;
		}

		if (this.mask.nextSibling != el) {
			var p = el.parentNode;
			p.insertBefore(this.mask, el);
			if (this.stackup)
				p.insertBefore(this.stackup, this.mask);
		}

		var st = this.mask.style;
		st.display = 'block';
		st.zIndex = el.style.zIndex;

		_syncMaskPos.call(this, true);

		if (this.stackup) {
			st = this.stackup.style;
			st.display = 'block';
			st.zIndex = el.style.zIndex;
		}
	}
});

/** Applies the mask over the specified element to indicate it is busy.
 * @disable(zkgwt)
 */ 
zk.eff.Mask = zk.$extends(zk.Object, {
	/** The constructor.
	 * <p>To remove the mask, invoke {@link #destroy}.
	 * @param Map opts [optional] the options:
	 * <ul>
	 * <li>String id - the id of the applied mask, if any.</li>
	 * <li>String/{@link DOMElement} anchor - the anchor of the applied mask, it can be an instance of {@link String} or {@link DOMElement}.</li>
	 * <li>String msg - the message of the indicator, if any. null, Loading... is assumed.</li>
	 * </ul>
	 */
	$init: function(opts) {
		opts = opts || {};
		var $anchor = zk(opts.anchor);
		
		if (!$anchor.jq.length || !$anchor.isRealVisible(true)) return; //nothing do to.
		
		this._opts = opts;
		
		var maskId = opts.id || 'z_applymask',
			progbox = jq(maskId, zk)[0];
		
		if (progbox) return this;
		
		var msg = opts.message || ((window.msgzk?msgzk.LOADING:'Loading')+'...'),
			n = document.createElement('div');
		
		document.body.appendChild(n);
		var xy = opts.offset || $anchor.revisedOffset(), 
			w = opts.width || $anchor.offsetWidth(),
			h = opts.height || $anchor.offsetHeight();
		jq(n).replaceWith(
		'<div id="'+maskId+'" style="display:none">' //$anchor size changed if using visibility: hidden
		+ '<div class="z-apply-mask" style="display:block;top:' + xy[1]
		+ 'px;left:' + xy[0] + 'px;width:' + w + 'px;height:' + h + 'px;"></div>'
		+ '<div id="'+maskId+'-z_loading" class="z-apply-loading"><div class="z-apply-loading-indicator">'
		+ '<span class="z-apply-loading-icon"></span> '
		+ msg+ '</div></div></div>');
		
		this.mask = jq(maskId, zk)[0];
		this.wgt = zk.Widget.$(opts.anchor);
		if (this.wgt) {
			zWatch.listen( {
				onHide: [
					this.wgt, this.onHide
				],
				onSize: [
					this.wgt, this.onSize
				]
			});
			this.wgt.__mask = this;
		}
		
		this.sync();
	},
	/** Hide the mask. Application developers rarely need to invoke this method.
	 * Rather, use {@link #sync} to synchronized the visual states.
	 */
	hide: function () {
		this.mask.style.display = 'none';
	},
	onHide: function () {
		this.__mask.hide();
	}, 
	/** Synchronizes the visual states of the mask with the specified element and the browser window.
	 * The visual states include the visibility and Z Index. 
	 */
	sync: function () {
		var $anchor = zk(this._opts.anchor);
		
		if (!$anchor.isVisible(true)) {
			this.hide();
			return;
		}
		
		var opts = this._opts,
			st = this.mask.firstChild.style,
			xy = opts.offset || $anchor.revisedOffset(), 
			w = opts.width || $anchor.offsetWidth(),
			h = opts.height || $anchor.offsetHeight();

		st.top = jq.px(xy[1]);
		st.left = jq.px(xy[0]);
		st.width = jq.px(w);
		st.height = jq.px(h);
		
		// ZK-726: The z-index required to cover anchor is the maximum z-index
		// along the anchor's ancestor chain, from document body to the highest
		// non-static node with non-auto z-index.
		var body = document.body,
			html = body.parentNode,
			rleaf = $anchor.jq,
			zi = 'auto', 
			zic, zicv;
		// find the highest non-static node with non-auto z-index
		for (var offp = rleaf.offsetParent(); offp[0] != body && offp[0] != html; offp = offp.offsetParent())
			if ((zic = offp.css('z-index')) && zic != 'auto') {
				zi = zk.parseInt(zic);
				rleaf = offp[0];
			}
		// grab the maximum along the chain of nodes
		for (var n = rleaf[0]; n && n.style; n = n.parentNode) {
			//Chrome and Safari only, HTML tag's zIndex value is empty
			if (n.tagName == 'HTML' && zk.webkit)
				n.style.zIndex = 'auto';
			var zic = n.style.zIndex || jq(n).css('z-index');
			if (zic && zic != 'auto') {
				zicv = zk.parseInt(zic);
				if (zi == 'auto' || zicv > zi)
					zi = zicv;
			}
		}
		
		if (zi != 'auto') { //Bug ZK-1381: only apply z-index when it is not auto
			st.zIndex = zi;
			this.mask.lastChild.style.zIndex = zi;
		}
		
		this.mask.style.display = 'block';
		
		var loading = jq(this.mask.id+'-z_loading', zk)[0];
		if (loading) {
			if (loading.offsetHeight > h) 
				loading.style.height = jq.px0(zk(loading).revisedHeight(h));
			if (loading.offsetWidth > w)
				loading.style.width = jq.px0(zk(loading).revisedWidth(w));
			loading.style.top = jq.px0(xy[1] + ((h - loading.offsetHeight) /2)); //non-negative only
			loading.style.left = jq.px0(xy[0] + ((w - loading.offsetWidth) /2));
		}
		
		this.mask.style.visibility = '';
	},
	onSize: function () {
		this.__mask.sync();
	},

	/** Removes the mask.
	 */
	destroy: function () {
		jq(this.mask).remove();
		if (this.wgt) {
			zWatch.unlisten({onHide: [this.wgt, this.onHide], onSize: [this.wgt, this.onSize]});
			delete this.wgt.__mask;
		}
		this.mask = this.wgt = null;
	}
});

/** @class zk.eff.Actions
 * A collection of actions that can be used with {@link zk.Widget#setAction}.
 * <p>The signature of an action must be as follows:<br>
 * <code>function ({@link DOMElement} n, {@link Map} opts) {}</code>
 * <p>Furthermore, the method will be called as a widget's method, i.e.,
 * <code>this</code> references to the widget.
 * @since 5.0.6
 */
zk.eff.Actions = {
	/** Slides down to display this widget.
	 * @param DOMElement n the node to display
	 * @param Map opts the options. Allowed options:
	 * <ul>
	 * <li><code>duration</code>: how many milliseconds to slide down</li>
	 * </ul>
	 */
	slideDown: function (n, opts) {
		zk(n).slideDown(this, opts);
	},
	/** Slides up to hide this widget.
	 * @param DOMElement n the node to hide
	 * @param Map opts the options. Allowed options:
	 * <ul>
	 * <li><code>duration</code>: how many milliseconds to slide up</li>
	 * </ul>
	 */
	slideUp: function (n, opts) {
		zk(n).slideUp(this, opts);
	},
	/** Slides in to display this widget.
	 * @param DOMElement n the node to display
	 * @param Map opts the options. Allowed options:
	 * <ul>
	 * <li><code>duration</code>: how many milliseconds to slide in</li>
	 * </ul>
	 */
	slideIn: function (n, opts) {
		zk(n).slideIn(this, opts);
	},
	/** Slides out to hide this widget.
	 * @param DOMElement n the node to hide
	 * @param Map opts the options. Allowed options:
	 * <ul>
	 * <li><code>duration</code>: how many milliseconds to slide out</li>
	 * </ul>
	 */
	slideOut: function (n, opts) {
		zk(n).slideOut(this, opts);
	}
};

jq(function() {
	//Handle zk.useStackup
	var _lastFloat, _autohideCnt = 0, _callback;

	function _onFloatUp(ctl) {
		var wgt = ctl.origin;
		++_autohideCnt;
		setTimeout(function () {
			if (!--_autohideCnt) {
				if (wgt)
					wgt = wgt.getTopWidget();
				if (wgt != _lastFloat) {
					_lastFloat = wgt
					zk._wgtutl.autohide(); //see widget.js
				}
			}
		}, 120); //filter
	}
	function _autohide() {
		_lastFloat = false; //enforce to run if onFloatUp also fired
		++_autohideCnt;
		setTimeout(function () {
			if (!--_autohideCnt)
				zk._wgtutl.autohide();
		}, 100); //filter
	}

	_useSKU = zk.useStackup;
	if (_useSKU == 'auto' || (_callback = _useSKU == 'auto/gecko')) {
		if (zk.gecko && _callback)
			_useSKU = false;
		else {
			_callback = zk.webkit || zk.opera;
			_useSKU = !_callback || zk.ie < 11; // ZK-1748 should include all ie
		}
	} else if (_useSKU == null)
		_useSKU = zk.ie < 11; // ZK-1748 should include all ie

	if (_callback) {
		var w2hide = function (name) {
			if (name == 'onSize' || name == 'onMove'
			|| name == 'onShow' || name == 'onHide'
			|| name == 'onResponse')
				_autohide();
		};
		zk.override(zWatch, _callback = {}, {
			fire: function (name) {
				_callback.fire.apply(this, arguments);
				w2hide(name);
			},
			fireDown: function (name) {
				_callback.fireDown.apply(this, arguments);
				w2hide(name);
			}
		});
		zWatch.listen({onFloatUp: ['', _onFloatUp]});
	}
}); //jq

})();
