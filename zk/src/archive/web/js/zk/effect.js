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

zk.eff = {
	shallStackup: function () {
		return _useSKU;
	}
};

if (zk.ie || zk.gecko2_ || zk.opera) {
	var _defShadowOpts = {left: 4, right: 4, top: 3, bottom: 3};

	zk.eff.Shadow = zk.$extends(zk.Object, {
		_HTML: zk.ie6_ ? '" class="z-shadow"></div>':
			'" class="z-shadow"><div class="z-shadow-tl"><div class="z-shadow-tr"></div></div>'
			+'<div class="z-shadow-cl"><div class="z-shadow-cr"><div class="z-shadow-cm">&#160;</div></div></div>'
			+'<div class="z-shadow-bl"><div class="z-shadow-br"></div></div></div>',
	
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
			jq(element).before('<div id="'+sdwid+this._HTML);
			this.shadow = jq(sdwid, zk)[0];
		},
		destroy: function () {
			jq(this.shadow).remove();
			jq(this.stackup).remove();
			this.node = this.shadow = this.stackup = null;
		},
		hide: function(){
			jq(this.shadow).hide();
			jq(this.stackup).hide();
		},
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
			st.left = jq.px(l + opts.left, true);
			st.top = jq.px(t + opts.top, true);
			st.width = jq.px(wd);
			st.zIndex = zk.parseInt($node.css("zIndex"));
			st.display = "block";
			if (zk.ie6_) st.height = jq.px(hgh);
			else {
				var cns = shadow.childNodes;
				cns[1].style.height = jq.px(hgh - cns[0].offsetHeight - cns[2].offsetHeight);
			}
	
			var stackup = this.stackup;
			if(opts.stackup) {
				if(!stackup)
					stackup = this.stackup =
						jq.newStackup(node, node.id + '-sdwstk', shadow);
	
				st = stackup.style;
				st.left = jq.px(l, true);
				st.top = jq.px(t, true);
				st.width = jq.px(w);
				st.height = jq.px(h);
				st.zIndex = shadow.style.zIndex;
				st.display = "block";
			}
			return true;
		},
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
				st.left = jq.px(l, true);
				st.top = jq.px(t, true);
				st.width = jq.px(w);
				st.height = jq.px(h);
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

zk.eff.FullMask = zk.$extends(zk.Object, {
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

		this._syncPos();

		var f;
		jq(mask).mousemove(f = jq.event.stop)
			.click(f);
		jq(window).resize(f = this.proxy(this._syncPos))
			.scroll(f);
	},
	destroy: function () {
		var mask = this.mask, f;
		jq(mask).unbind("mousemove", f = jq.event.stop)
			.unbind("click", f)
			.remove()
		jq(window).unbind("resize", f = this.proxy(this._syncPos))
			.unbind("scroll", f);
		jq(this.stackup).remove();
		this.mask = this.stackup = null;
	},
	hide: function () {
		this.mask.style.display = 'none';
		if (this.stackup) this.stackup.style.display = 'none';
	},
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
	},
	/** Position a mask to cover the whole browser window. */
	_syncPos: function () {
		var n = this.mask,
			ofs = zk(n).toStyleOffset(jq.innerX(), jq.innerY()),
			st = n.style;
		st.left = jq.px(ofs[0], true);
		st.top = jq.px(ofs[1], true);
		st.width = jq.px(jq.innerWidth());
		st.height = jq.px(jq.innerHeight());
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
});

zk.eff.Mask = zk.$extends(zk.Object, {
	$init: function(opts) {
		opts = opts || {};
		var $anchor = zk(opts.anchor);
		
		if (!$anchor.jq.length || !$anchor.isRealVisible(true)) return; //nothing do to.
		
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
		var loading = jq(maskId+"-z_loading", zk)[0],
			mask = this.mask = jq(maskId, zk)[0];
		
		if (loading) {
			if (loading.offsetHeight > h) 
				loading.style.height = jq.px(zk(loading).revisedHeight(h));
			if (loading.offsetWidth > w)
				loading.style.width = jq.px(zk(loading).revisedWidth(w));
			loading.style.top = jq.px(xy[1] + ((h - loading.offsetHeight) /2));
			loading.style.left = jq.px(xy[0] + ((w - loading.offsetWidth) /2));
		}
		
		mask.style.visibility = "";
	},
	destroy: function () {
		jq(this.mask).remove();
		this.mask = null;
	}
});

zk.eff.Tooltip = zk.$extends(zk.Object, {
	beforeBegin: function (ref) {
		if (this._tip && !this._tip.isOpen()) { //closed by other (such as clicking on menuitem)
			this._clearOpening();
			this._clearClosing();
			this._tip = this._ref = null;
		}

		var overTip = this._tip && zUtl.isAncestor(this._tip, ref);
		if (overTip) this._clearClosing(); //not close tip if over tip
		return !overTip;//disable tip in tip
	},
	begin: function (tip, ref, params) {
		this._params = params;
		if (this._tip != tip) {
			this.close_();

			this._inf = {
				tip: tip, ref: ref,
				timer: setTimeout(this.proxy(this.open_), params.delay !== undefined ? params.delay : zk.tipDelay)
			};
		} else if (this._ref == ref)
			this._clearClosing();
	},
	end: function (ref) {
		if (this._ref == ref || this._tip == ref)
			this._tmClosing =
				setTimeout(this.proxy(this.close_), 100);
			//don't cloes immediate since user might move from ref to toolip
		else
			this._clearOpening();
	},
	open_: function () {
		var inf = this._inf;
		if (inf) {
			var tip = this._tip = inf.tip,
				ref = this._ref = inf.ref;
			this._inf = null;
			var params = this._params,
				xy = params.x !== undefined ? [params.x, params.y]
							: zk.currentPointer;
			tip.open(ref, xy, params.position ? params.position : params.x === null ? "after_pointer" : null, {sendOnOpen:true});
		}
	},
	close_: function () {
		this._clearOpening();
		this._clearClosing();

		var tip = this._tip;
		if (tip) {
			this._tip = this._ref = null;
			tip.close({sendOnOpen:true});
		}
	},
	_clearOpening: function () {
		var inf = this._inf;
		if (inf) {
			this._inf = null;
			clearTimeout(inf.timer);
		}
	},
	_clearClosing: function () {
		var tmClosing = this._tmClosing;
		if (tmClosing) {
			this._tmClosing = null;
			clearTimeout(tmClosing);
		}
	}
});
zk.eff.tooltip = new zk.eff.Tooltip();

var _errs = [], _errcnt = 0;

zk.eff.Error = zk.$extends(zk.Object, {
	$init: function (msg) {
		var id = "zk_err" + _errcnt++,
			$id = '#' + id;
			x = (_errcnt * 5) % 50, y = (_errcnt * 5) % 50,
			html =
	'<div class="z-error" style="left:'+(jq.innerX()+x)+'px;top:'+(jq.innerY()+y)
	+'px;" id="'+id+'"><table cellpadding="2" cellspacing="2" width="100%"><tr>'
	+'<td align="right"><div id="'+id+'-p">';
	if (!zk.light)
		html += '<span class="btn" onclick="zk.eff.Error._redraw()">redraw</span>&nbsp;';
	html += '<span class="btn" onclick="zk.eff.Error._close(\''+id+'\')">close</span></div></td></tr>'
	+'<tr valign="top"><td class="z-error-msg">'+zUtl.encodeXML(msg, {multiline:true}) //Bug 1463668: security
	+'</td></tr></table></div>';
		jq(document.body).append(html);

		this.id = id;
		_errs.push(this);

		try {
			var n;
			this.dg = new zk.Draggable(null, n = jq($id)[0], {
				handle: jq($id + '-p')[0], zIndex: n.style.zIndex,
				starteffect: zk.$void, starteffect: zk.$void,
				endeffect: zk.$void});
		} catch (e) {
		}
	},
	destroy: function () {
		_errs.$remove(this);
		if (this.dg) this.dg.destroy();
		jq('#' + this.id).remove();
	}
},{
	_redraw: function () {
		zk.errorDismiss();
		zAu.send(new zk.Event(null, 'redraw'));
	},
	_close: function (id) {
		for (var j = _errs.length; j--;) {
			var e = _errs[j];
			if (e.id == id) {
				_errs.splice(j, 1);
				e.destroy();
				return;
			}
		}
	},
	closeAll: function () {
		for (var j = _errs.length; j--;)
			_errs[j].destroy();
		_errs = [];
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
	if (_useSKU == "auto" || (_callack = _useSKU == "auto/gecko")) {
		if (zk.gecko && _callack)
			_useSKU = false;
		else {
			_callack = zk.safari || zk.opera;
			_useSKU = !_callack || zk.ie6_;
		}
	} else if (_useSKU == null)
		_useSKU = zk.ie6_;

	if (_callack) {
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
