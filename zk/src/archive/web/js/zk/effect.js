/* effect.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov 10 14:45:53     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

script.aculo.us effects.js v1.7.0
Copyright (c) 2005, 2006 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function() {

	var _defSKUOpts, _useSKU;
	function _getSKUOpts() {
		return _defSKUOpts
			|| (_defSKUOpts = {stackup: zk.eff.shallStackup()});
	}

/** The effects, such as mask and shadow.
 */
//zk.$package('zk.eff');
zk.eff = {
	shallStackup: function () {
		return _useSKU;
	}
};

if (!zk.css3) {
	var _defShadowOpts = {left: 4, right: 4, top: 3, bottom: 3},
		_shadowEnding = zk.ie6_ ? '" class="z-shadow"></div>':
		'" class="z-shadow"><div class="z-shadow-tl"><div class="z-shadow-tr"></div></div>'
		+'<div class="z-shadow-cl"><div class="z-shadow-cr"><div class="z-shadow-cm">&#160;</div></div></div>'
		+'<div class="z-shadow-bl"><div class="z-shadow-br"></div></div></div>';

	/** The shadow effect.
	 */
	zk.eff.Shadow = zk.$extends(zk.Object, {
		/** Constructor of the Shadow object.
		 * <p>Notice that you have to invoke {@link #destroy},
		 * if the shadow is no longer used. 
		 * @param DOMElement element the element to associate the shadow
		 * @param Map opts [optional] the options. Alowed options:
		 * <ul>
		 * <li>left: The margin at left. Default: 4.</li>
		 * <li>right: The margin at right. Default: 4.</li>
		 * <li>top: the margin at top. Default: 3.</li>
		 * <li>bottom: the margin at bottom. Default: 3.</li>
		 * <li>stackup: whether to create a stackup (see jqzk#makeStackup)</li> 
		 * </ul>
		 */
		$init: function (element, opts) {
			opts = this.opts =
				zk.$default(zk.$default(opts, _defShadowOpts), _getSKUOpts());
			if (zk.ie6_) {
				opts.left -= 1;
				opts.right -= 8;
				opts.top -= 2;
				opts.bottom -= 6;
			}
	
			this.node = element;
			var sdwid = element.id + "-sdw";
			jq(element).before('<div id="' + sdwid + _shadowEnding);
			this.shadow = jq(sdwid, zk)[0];
		},
		/** Destroys the shadow. You cannot use this object any more. 
		 */
		destroy: function () {
			jq(this.shadow).remove();
			jq(this.stackup).remove();
			this.node = this.shadow = this.stackup = null;
		},
		/** Hides the shadow, no matter the associated element is visible or not.
		 * <p>Notice this method is rarely used. Rather, {@link #sync} is more convenient to use. 
		 */
		hide: function(){
			jq(this.shadow).hide();
			jq(this.stackup).hide();
		},
		/** Synchronizes the visual states of the element with shadow. The visual states include the visibility, location, dimensions and z-index. When the associated element is changed, you have to invoke this method to synchronize the visual states. 
		 */
		sync: function () {
			var node = this.node, $node = jq(node),
				shadow = this.shadow;
			if (!node || !$node.zk.isVisible(true)) {
				this.hide();
				return false;
			}
	
			for (var c = shadow;;) {
				if (!(c = c.nextSibling) || c.tagName) {
					if (c != node)
						node.parentNode.insertBefore(shadow, node);
					break;
				}
			}
	
			var opts = this.opts,
				l = node.offsetLeft, t = node.offsetTop,
				w = node.offsetWidth, h = node.offsetHeight,
				wd = Math.max(0, w - opts.left + opts.right),
				hgh = Math.max(0, h - opts.top + opts.bottom),
				st = shadow.style;
			st.left = jq.px(l + opts.left);
			st.top = jq.px(t + opts.top);
			st.width = jq.px0(wd);
			st.zIndex = zk.parseInt($node.css("zIndex"));
			st.display = "block";
			if (zk.ie6_) st.height = jq.px0(hgh);
			else {
				var cns = shadow.childNodes;
				cns[1].style.height = jq.px0(hgh - cns[0].offsetHeight - cns[2].offsetHeight);
			}
	
			var stackup = this.stackup;
			if(opts.stackup) {
				if(!stackup)
					stackup = this.stackup =
						jq.newStackup(node, node.id + '-sdwstk', shadow);
	
				st = stackup.style;
				st.left = jq.px(l);
				st.top = jq.px(t);
				st.width = jq.px0(w);
				st.height = jq.px0(h);
				st.zIndex = shadow.style.zIndex;
				st.display = "block";
			}
			return true;
		},
		/** Returns the lowest level of elements of this shadow. By lowest level, we mean the element is displayed at the botton in the z order.
		 * The bottom element is the stackup element if #$init is called with the stackup option. Otherwise, it is the shadow element. 
		 * @return DOMElement
		 */
		getBottomElement: function () {
			return this.stackup || this.shadow;
		}
	});
} else {
	zk.eff.Shadow = zk.$extends(zk.Object, {
		$init: function (element, opts) {
			this.wgt = zk.Widget.$(element.id);
			this.opts = zk.$default(opts, _getSKUOpts());
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
				st.zIndex = zk.parseInt($node.css("zIndex"));
				st.display = "block";
			}
			return true;
		},
		getBottomElement: function () {
			return this.stackup;
		}
	});
}

	//Position a mask to cover the whole browser window.
	//it must be called as _syncPos.call(this)
	function _syncPos() {
		var n = this.mask,
			ofs = zk(n).toStyleOffset(jq.innerX(), jq.innerY()),
			st = n.style;
		st.left = jq.px(ofs[0]);
		st.top = jq.px(ofs[1]);
		st.width = jq.px0(jq.innerWidth());
		st.height = jq.px0(jq.innerHeight());
		st.display = "block";

		n = this.stackup;
		if (n) {
			n = n.style;
			n.left = st.left;
			n.top = st.top;
			n.width = st.width;
			n.height = st.height;
		}
	}

/** A mask covers the browser window fully.
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
		opts = zk.$default(opts, _getSKUOpts());
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

		_syncPos.call(this);

		var f;
		jq(mask).mousemove(f = jq.Event.stop)
			.click(f);
		jq(window).resize(f = this.proxy(_syncPos))
			.scroll(f);
	},
	/** Removes the full mask. You can not access this object any more.
	 */
	destroy: function () {
		var mask = this.mask, f;
		jq(mask).unbind("mousemove", f = jq.Event.stop)
			.unbind("click", f)
			.remove()
		jq(window).unbind("resize", f = this.proxy(_syncPos))
			.unbind("scroll", f);
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
		if (this.stackup) {
			st = this.stackup.style;
			st.display = 'block';
			st.zIndex = el.style.zIndex;
		}
	}
});

/** Applies the mask over the specified element to indicate it is busy.
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
		
		var msg = opts.message || ((window.msgzk?msgzk.LOADING:"Loading")+'...'),
			n = document.createElement("DIV");
		
		document.body.appendChild(n);
		var xy = opts.offset || $anchor.revisedOffset(), 
			w = opts.width || $anchor.offsetWidth(),
			h = opts.height || $anchor.offsetHeight();
		jq(n).replaceWith(
		'<div id="'+maskId+'" style="visibility:hidden">' 
		+ '<div class="z-apply-mask" style="display:block;top:' + xy[1]
		+ 'px;left:' + xy[0] + 'px;width:' + w + 'px;height:' + h + 'px;"></div>'
		+ '<div id="'+maskId+'-z_loading" class="z-apply-loading"><div class="z-apply-loading-indicator">'
		+ '<span class="z-apply-loading-icon"></span> '
		+ msg+ '</div></div></div>');
		
		this.mask = jq(maskId, zk)[0];
		this.sync();
	},
	/** Hide the mask. Application developers rarely need to invoke this method.
	 * Rather, use {@link #sync} to synchronized the visual states.
	 */
	hide: function () {
		this.mask.style.display = 'none';
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
		
		var zi = $anchor.jq.offsetParent().css('z-index');
		// IE bug
		if (zk.ie && !zk.ie8)
			zi = zi == 0 ? 1 : zi;
			
		st.zIndex = zi;
		this.mask.lastChild.style.zIndex = zi;
		
		this.mask.style.display = 'block';
		
		var loading = jq(this.mask.id+"-z_loading", zk)[0];
		if (loading) {
			if (loading.offsetHeight > h) 
				loading.style.height = jq.px0(zk(loading).revisedHeight(h));
			if (loading.offsetWidth > w)
				loading.style.width = jq.px0(zk(loading).revisedWidth(w));
			loading.style.top = jq.px0(xy[1] + ((h - loading.offsetHeight) /2)); //non-negative only
			loading.style.left = jq.px0(xy[0] + ((w - loading.offsetWidth) /2));
		}
		
		this.mask.style.visibility = "";
	},
	/** Removes the mask.
	 */
	destroy: function () {
		jq(this.mask).remove();
		this.mask = null;
	}
});

jq(function() {
	//Handle zk.useStackup
	var _lastFloat, _autohideCnt = 0, _callback;

	function _onFloatUp(ctl) {
		var wgt = ctl.origin;
		++_autohideCnt;
		setTimeout(function () {
			if (!--_autohideCnt) {
				if (wgt) wgt = wgt.getTopWidget();
				if (wgt != _lastFloat) {
					_lastFloat = wgt
					zk.Widget._autohide();
				}
			}
		}, 120); //filter
	}
	function _autohide() {
		_lastFloat = false; //enforce to run if onFloatUp also fired
		++_autohideCnt;
		setTimeout(function () {
			if (!--_autohideCnt)
				zk.Widget._autohide();
		}, 100); //filter
	}

	_useSKU = zk.useStackup;
	if (_useSKU == "auto" || (_callback = _useSKU == "auto/gecko")) {
		if (zk.gecko && _callback)
			_useSKU = false;
		else {
			_callback = zk.safari || zk.opera;
			_useSKU = !_callback || zk.ie6_;
		}
	} else if (_useSKU == null)
		_useSKU = zk.ie6_;

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
		zWatch.listen({onFloatUp: {onFloatUp: _onFloatUp}});
	}
}); //jq

})();
