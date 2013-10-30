/* dom.js

	Purpose:
		Enhance jQuery
	Description:
		
	History:
		Fri Jun 12 10:44:53 2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zjq = function (jq) { //ZK extension
	this.jq = jq;
};
(function (document, window) {
	var _jq = {}, //original jQuery
		//refer to http://www.w3schools.com/css/css_text.asp
		_txtStyles = [
			'font-family', 'font-size', 'font-weight', 'font-style',
			'letter-spacing', 'line-height', 'text-align', 'text-decoration',
			'text-indent', 'text-shadow', 'text-transform', 'text-overflow',
			'direction', 'word-spacing', 'white-space'],
		_txtStylesCamel, _txtSizDiv, //inited in textSize
		_txtStyles2 = ["color", "background-color", "background"],
		_zsyncs = [],
		_pendzsync = 0,
		_vpId = 0, //id for virtual parent's reference node
		_sbwDiv; //scrollbarWidth

	function _elmOfWgt(id, ctx) {
		var w = ctx && ctx !== zk ? zk.Widget.$(ctx): null, w2;
		return (w2=w||zk.Desktop.sync()) && (w2=w2.$f(id, !w)) ? w2.$n(): null;
	}
	function _ofsParent(el) {
		if (el.offsetParent) return el.offsetParent;
		if (el == document.body) return el;

		while ((el = el.parentNode) && el != document.body)
			if (el.style && jq(el).css('position') != 'static') //in IE, style might not be available
				return el;

		return document.body;
	}
	function _zsync(org) {
		if (--_pendzsync <= 0)
			for (var j = _zsyncs.length; j--;)
				_zsyncs[j].zsync(org);
	}
	function _focus(n) {
		zk.afterAnimate(function () {
			try {
				n.focus();
				var w = zk.Widget.$(n);
				if (w) zk.currentFocus = w;

				zjq.fixInput(n);
			} catch (e) {
			}
		}, -1); //FF cannot change focus to a DOM element being animated
	}
	function _select(n) {
		try {
			n.select();
		} catch (e) {
		}
	}

	function _submit() {
		if (this.submit) {
			jq.Event.fire(this, 'submit');
			this.submit();
		}
	}

	function _dissel() {
		this.style.MozUserSelect = "none";
	}
	function _ensel() {
		this.style.MozUserSelect = "";
	}

	function _scrlIntoView(outer, inner, info) {
		if (outer && inner) {
			var ooft = zk(outer).revisedOffset(),
				ioft = info ? info.oft : zk(inner).revisedOffset(),		 
				top = ioft[1] - ooft[1] +
						(outer == (zk.safari ? document.body : document.body.parentNode)
								? 0 : outer.scrollTop),
				left = ioft[0] - ooft[0] +
						(outer == (zk.safari ? document.body : document.body.parentNode)
								? 0 : outer.scrollLeft),
				ih = info ? info.h : inner.offsetHeight,
				iw = info ? info.w : inner.offsetWidth,
				right = left + iw,
				bottom = top + ih,
				updated;
			//for fix the listbox(livedate) keydown select always at top
			if (/*outer.clientHeight < inner.offsetHeight || */ outer.scrollTop > top) {
				outer.scrollTop = top;
				updated = true;
			} else if (bottom > outer.clientHeight + outer.scrollTop) {
				outer.scrollTop = !info ? bottom : bottom - (outer.clientHeight + (inner.parentNode == outer ? 0 : outer.scrollTop));
				updated = true;
			}
			
			// ZK-1924:	scrollIntoView can also adjust horizontal scroll position
			if (outer.scrollLeft > left) {
				outer.scrollLeft = left;
				updated = true;
			} else if (right > outer.clientWidth + outer.scrollLeft) {
				outer.scrollLeft = !info ? right : right - (outer.clientWidth + (inner.parentNode == outer ? 0 : outer.scrollLeft));
				updated = true;
			}
			
			if (updated || !info) {
				if (!info)
					info = {
						oft: ioft,
						h: inner.offsetHeight,
						w: inner.offsetWidth,
						el: inner
					};
				else info.oft = zk(info.el).revisedOffset();
			}
			
			return info; 
		}
	}

	function _cmOffset(el) {
		var t = 0, l = 0, operaBug;
		//Fix gecko difference, the offset of gecko excludes its border-width when its CSS position is relative or absolute
		if (zk.gecko) {
			var p = el.parentNode;
			while (p && p != document.body && p.nodeType === 1) {
				var $p = jq(p),
					style = $p.css("position");
				if (style == "relative" || style == "absolute") {
					t += zk.parseInt($p.css("border-top-width"));
					l += zk.parseInt($p.css("border-left-width"));
				}
				p = p.offsetParent;
			}
		}

		do {
			//Bug 1577880: fix originated from http://dev.rubyonrails.org/ticket/4843
			var $el = jq(el);
			if ($el.css("position") == 'fixed') {
				t += jq.innerY() + el.offsetTop;
				l += jq.innerX() + el.offsetLeft;
				break;
			} else {
				//Fix opera bug. If the parent of "input" or "span" is "div"
				// and the scrollTop of "div" is more than 0, the offsetTop of "input" or "span" always is wrong.
				if (zk.opera) {
					if (operaBug && jq.nodeName(el, 'div') && el.scrollTop != 0)
						t += el.scrollTop || 0;
					operaBug = jq.nodeName(el, 'span', 'input');
				}
				t += el.offsetTop || 0;
				l += el.offsetLeft || 0;
				//Bug 1721158: In FF, el.offsetParent is null in this case
				el = zk.gecko && el != document.body ?
					_ofsParent(el): el.offsetParent;
			}
		} while (el);
		return [l, t];
	}
	function _posOffset(el) {
		if (zk.safari && jq.nodeName(el, "tr") && el.cells.length)
			el = el.cells[0];

		var t = 0, l = 0;
		do {
			t += el.offsetTop  || 0;
			l += el.offsetLeft || 0;
			//Bug 1721158: In FF, el.offsetParent is null in this case
			el = zk.gecko && el != document.body ?
				_ofsParent(el): el.offsetParent;
			if (el) {
				if(jq.nodeName(el, "body")) break;
				var p = jq(el).css('position');
				if (p == 'relative' || p == 'absolute') break;
			}
		} while (el);
		return [l, t];
	}
	function _addOfsToDim($this, dim, revised) {
		if (revised) {
			var ofs = $this.revisedOffset();
			dim.left = ofs[0];
			dim.top = ofs[1];
		} else {
			dim.left = $this.offsetLeft();
			dim.top = $this.offsetTop();
		}
		return dim;
	}

	//redoCSS
	var _rdcss = [];
	function _redoCSS0() {
		if (_rdcss.length) {
			for (var el; el = _rdcss.pop();)
				try {
					zjq._fixCSS(el);
				} catch (e) {
				}
		
			// just in case
			setTimeout(_redoCSS0);
		}
	}

zk.copy(zjq, {
	//Returns the minimal width to hold the given cell called by getChildMinSize_
	minWidth: function (el) {
		return zk(el).offsetWidth();
	},

	fixInput: zk.$void, //overriden in dom.js to fix the focus issue (losing caret...)
	fixOnResize: zk.$void, //overriden in domie.js to fix the window.onresize issue
	_fixCSS: function (el) { //overriden in domie.js , domsafari.js , domopera.js
		el.className += ' ';
		if (el.offsetHeight)
			;
		el.className.trim();
	},
	_cleanVisi: function (n) { //overriden in domopera.js
		n.style.visibility = "inherit";
	},
	_fixClick: zk.$void, //overriden in domie.js
	_fixedVParent: zk.$void,
	_fixIframe: zk.$void,
	_useQS: zk.$void, //overriden in domie.js (used in zAU)

	//The source URI used for iframe (to avoid HTTPS's displaying nonsecure issue)
	src0: "", //an empty src; overriden in domie.js
	eventTypes: {
		zmousedown: 'mousedown',
		zmouseup: 'mouseup',
		zmousemove: 'mousemove',
		zdblclick: 'dblclick',
		zcontextmenu: 'contextmenu'
	}
});
jq.fn.zbind = jq.fn.bind;
jq.fn.zunbind = jq.fn.unbind;
/** @class jq
 * @import jq.Event
 * @import zk.Widget
 * @import zk.Desktop
 * @import zk.Skipper
 * @import zk.eff.Shadow
 * Represents the object returned by the <code>jq</code> function.
 * For example, <code>jq('#id');</code>
 *
 * <p>ZK 5 Client Engine is based on <a href="http://jquery.com/" target="jq">jQuery</a>.
 * It inherits all functionality provided by jQuery. Refer to <a href="http://docs.jquery.com/Main_Page" target="jq">jQuery documentation</a>
 * for complete reference. However, we use the global function called <code>jq</code>
 * to represent jQuery. Furthermore, for documentation purpose,
 * we use @{link jq} to represent the object returned by the <code>jq</code> function.</p>

 * <p>Notice that there is no package called <code>_</code>.
	Rather, it represents the global namespace of JavaScript.
	In other words, it is the namespace of the <code>window</code> object
	in a browser.
 *
 * <h2>Diffirence and Enhancement to jQuery</h2>
 * <p><code>{@link jq} jq(Object selector, Object context);</code>
 *
 * <blockquote>
 * <h3>Refer jQuery as <code>jq</code></h3>
 * <p>First of all, the jQuery class is referenced as {@link #jq}, and it is suggested to use jq instead of $ or jQuery when developing a widget, since it might be renamed later by an application (say, overridden by other client framework). Here is an example uses jq:
 * <pre><code>jq(document.body).append("<div></div>");</code></pre>
 *
 * <h3>Dual Objects</h3>
 * <p>To extend jQuery's functionally,  each time <code>jq(...)</code>
 * or <code>zk(...)</code> is called, an instance of {@link jq}
 * and an instance of {@link jqzk} are created. The former one provides the
 * standard jQuery API plus some minimal enhancement as described below.
 * The later is ZK's addions APIs.
 *
 * <p>You can retrieve one of the other with
 * {@link jq#zk} and {@link jqzk#jq}.
 *
 * <pre><code>jq('#abc').zk; //the same as zk('#abc')
 *zk('#abc').jq; //the same as jq('#abc');</code></pre>
 *
 * <h3>Extra Selectors</h3>
 * <blockquote>
 *
 * <h4>@tagName</h4>
 * <p><code>jq</code> is extended to support the selection by use of ZK widget's
 * tagName. For example,
 * <pre><code>jq('@window');</code></pre>
 *
 *<p>Notice that it looks for the ZK widget tree to see if any widget whose className
 * ends with <code>window</code>.
 * <p>If you want to search the widget in the nested tag, you can specify
 * the selector after @. For example, the following searches the space owner named x,
 * then y, and finally z
 * <pre><code>jq('@x @y @z');</code></pre>
 * or search the element from the given attribute of the widget, you can specify
 * the selector as follows. For example,
 * <pre><code>jq('@window[border="normal"]')</code></pre>
 * 
 * <h4>$id</h4>
 * <p><code>jq</code> is extended to support the selection by use of widget's
 * ID ({@link Widget#id}), and then DOM element's ID. For example,
 * <pre><code>jq('$xx');</code></pre>
 *
 * <p>Notice that it looks for any bound widget whose ID is xx, and
 * select the associated DOM element if found.
 *
 * <p>If you want to search the widget in the inner ID space, you can specify
 * the selector after $. For example, the following searches the space owner
 * named x, then y, and finally z
 * <pre><code>jq('$x $y $z');</code></pre>
 * or advanced search combine with CSS3 and @, you can specify like this.
 * <pre><code>jq('@window[border="normal"] > $x + div$y > @button:first');</code></pre>
 *
 * <h4>A widget</h4>
 * <p><code>jq</code> is extended to support {@link Widget}.
 * If the selector is a widget, <code>jq</code> will select the associated DOM element
 * of the widget.
 *
 * <pre><code>jq(widget).after('<div></div>'); //assume widget is an instance of {@link Widget}</code></pre>
 *
 * <p>In other words, it is the same as
 *
 * <pre><code>jq(widget.$n()).after('<div></div>');</code></pre>
 * </blockquote>
 *
 * <h3>Extra Contexts</h3>
 * <blockquote>
 * <h4>The <code>zk</code> context</h4>
 * <pre><code>jq('foo', zk);</code></pre>
 * <p>With the zk context, the selector without any prefix is assumed to be
 * the identifier of ID. In other words, you don't need to prefix it with '#'.
 * Thus, the above example is the same as
 * <pre><code>jq('#foo')</code></pre>
 *
 * <p>Of course, if the selector is not a string or prefix with a non-alphnumeric letter, the zk context is ignored. 
 * </blockquote>
 *
 * <h3>Extra Global Functions</h3>
 * <blockquote>
 * <h4>The <code>zk</code> function</h4>
 * <pre><code>{@link jq} zk(Object selector);</code></pre>
 *
 * <p>It is the same as <code>jq(selector, zk).zk</code>. In other words,
 * it assumes the zk context and returns an instance of {@link jqzk}
 * rather than an instance of {@link jq}. 
 * </blockquote>
 *
 * <h3>Other Extension</h3>
 * <ul>
 * <li>{@link jq} - DOM utilities (such as, {@link jq#innerX}</li>
 * <li>{@link jqzk} - additional utilities to {@link jq}.</li>
 * <li>{@link Event} - the event object passed to the event listener</li>
 * </ul>
 * <h3>Not override previous copy if any</h3>
 * <p>Unlike the original jQuery behavior, ZK's jQuery doesn't override
 * the previous copy, if any, so ZK can be more compatible with other frameworks
 * that might use jQuery. For example, if you manually include a copy
 * of jQuery before loading ZK Client Engine, <code>jQuery</code>
 * will refer to the copy of jQuery you included explicitly. To refer
 * ZK's copy, always use <code>jq</code>.
 * </blockquote>
 *
 * @author tomyeh
 */
zk.override(jq.fn, _jq, /*prototype*/ {
	/** The associated instance of {@link jqzk} that
	 * provides additional utilities to <a href="http://docs.jquery.com/Main_Page" target="jq">jQuery</a>.
	 * @type jqzk
	 */
	//zk: null,

	init: function (sel, ctx) {
		if (ctx === zk) {
			if (typeof sel == 'string'
			&& zUtl.isChar(sel.charAt(0), {digit:1,upper:1,lower:1,'_':1})) {
				var el = document.getElementById(sel);
				if (!el || el.id == sel) {
					var ret = jq(el || []);
					ret.context = document;
					ret.selector = '#' + sel;
					ret.zk = new zjq(ret);
					return ret;
				}
				sel = '#' + sel;
			}
			ctx = null;
		}
		if (zk.Widget && zk.Widget.isInstance(sel))
			sel = sel.$n() || '#' + sel.uuid;
		var ret = _jq.init.apply(this, arguments);
		ret.zk = new zjq(ret);
		return ret;
	},
	/** Replaces the match elements with the specified HTML, DOM or {@link Widget}.
	 * We extends <a href="http://docs.jquery.com/Manipulation/replaceWith">jQuery's replaceWith</a>
	 * to allow replacing with an instance of {@link Widget}.
	 * @param Widget widget a widget
	 * @param Desktop desktop the desktop. It is optional.
	 * @param Skipper skipper the skipper. It is optional.
	 * @return jq the jq object matching the DOM element after replaced
	 */
	replaceWith: function (w, desktop, skipper) {
		if (!zk.Widget.isInstance(w))
			return _jq.replaceWith.apply(this, arguments);

		var n = this[0];
		if (n) w.replaceHTML(n, desktop, skipper);
		return this;
	},
	bind: function(type, data, fn) {
		type = zjq.eventTypes[type] || type;
		return this.zbind.apply(this, arguments);
	},
	unbind: function(type, fn){
		type = zjq.eventTypes[type] || type;
		return this.zunbind.apply(this, arguments);
	}
	/** Removes all matched elements from the DOM.
	 * <p>Unlike <a href="http://docs.jquery.com/Manipulation/remove">jQuery</a>,
	 * it does nothing if nothing is matched.
	 * @return jq this object
	 */
	//remove: function () {}
	/** Removes all children of the matched element from the DOM.
	 * <p>Unlike <a href="http://docs.jquery.com/Manipulation/empty">jQuery</a>,
	 * it does nothing if nothing is matched.
	 * @return jq this object
	 */
	//empty: function () {}
	/** Shows all matched elements from the DOM.
	 * <p>Unlike <a href="http://docs.jquery.com/show">jQuery</a>,
	 * it does nothing if nothing is matched.
	 * @return jq this object
	 */
	//show: function () {}
	/** Hides all matched elements from the DOM.
	 * <p>Unlike <a href="http://docs.jquery.com/hide">jQuery</a>,
	 * it does nothing if nothing is matched.
	 * @return jq this object
	 */
	//hide: function () {}

	/** Insert content before each of the matched elements.
	 * <p>Notice that this method is extended to handle {@link Widget}.
	 * <p>Refer to <a href="http://docs.jquery.com/Manipulation/before>jQuery documentation</a>
	 * for more information.
	 * @param Object content If it is a string, it is assumed to be
	 * a HTML fragment. If it is a widget, the widget will be insert before
	 * @param Desktop desktop [optional] the desktop. It is used only
	 * if content is a widget.
	 */
	//before: function () {}
	/** Insert content after each of the matched elements.
	 * <p>Notice that this method is extended to handle {@link Widget}.
	 * <p>Refer to <a href="http://docs.jquery.com/Manipulation/after>jQuery documentation</a>
	 * for more information.
	 * @param Object content If it is a string, it is assumed to be
	 * a HTML fragment. If it is a widget, the widget will be insert after
	 * @param Desktop desktop [optional] the desktop. It is used only
	 * if content is a widget.
	 */
	//after: function () {}
	/** Append content to inside of every matched element.
	 * <p>Notice that this method is extended to handle {@link Widget}.
	 * <p>Refer to <a href="http://docs.jquery.com/Manipulation/append>jQuery documentation</a>
	 * for more information.
	 * @param Object content If it is a string, it is assumed to be
	 * a HTML fragment. If it is a widget, the widget will be appended
	 * @param Desktop desktop [optional] the desktop. It is used only
	 * if content is a widget.
	 */
	//append: function () {}
	/** Prepend content to the inside of every matched element.
	 * <p>Notice that this method is extended to handle {@link Widget}.
	 * <p>Refer to <a href="http://docs.jquery.com/Manipulation/prepend>jQuery documentation</a>
	 * for more information.
	 * @param Object content If it is a string, it is assumed to be
	 * a HTML fragment. If it is a widget, the widget will be prepended
	 * @param Desktop desktop [optional] the desktop. It is used only
	 * if content is a widget.
	 */
	//prepend: function () {}
});
jq.fn.init.prototype = jq.fn;

jq.each(['remove', 'empty', 'show', 'hide'], function (i, nm) {
	_jq[nm] = jq.fn[nm];
	jq.fn[nm] = function () {
		return !this.selector && this[0] === document ? this: _jq[nm].apply(this, arguments);
	};
});
jq.each(['before','after','append','prepend'], function (i, nm) {
	_jq[nm] = jq.fn[nm];
	jq.fn[nm] = function (w, desktop) {
		if (!zk.Widget.isInstance(w))
			return _jq[nm].apply(this, arguments);

		if (!this.length) return this;
		if (!zk.Desktop._ndt) zk.stateless();

		var ret = _jq[nm].call(this, w.redrawHTML_());
		if (!w.z_rod) {
			w.bind(desktop);
			zUtl.fireSized(w);
		}
		return ret;
	};
});

/** @class jqzk
 * @import zk.Widget
 * Represents the object returned by the <code>zk</code> function, or by
 * {@link jq#zk}.
 * For example, <code>zk('#id');</code>
 *
 * <p>Refer to {@link jq} for more information.
 *
 * <h3>Other Extension</h3>
 * <ul>
 * <li>{@link jq} - the object returned by <code>jq(...)</code>. The original jQuery API.</li>
 * <li>{@link jq} - DOM utilities (such as, {@link jq#innerX}</li>
 * <li>{@link jq.Event} - the event object passed to the event listener</li>
 * </ul>
 */
zjq.prototype = {
	/** The associated instance of {@link jq}, the object returned by <code>jq(...)</code>.
	 * @type jq
	 */
	//jq: null, //assigned at run time

	/** Cleans, i.e., reset, the visibility (of the CSS style) for the matched elements.
	 * 	Depending on the browser, the reset visibility is either visible or inherit.
	 * @return jq 
	 */
	cleanVisibility: function () {
		return this.jq.each(function () {
			zjq._cleanVisi(this);
		});
	},
	/** Returns whether the first matched element is visible.
	 * Returns false if not exist. Returns true if no style attribute.
	 * @return boolean whether the first matched element is visible.
	 */
	/** Returns whether the first matched element is visible.
	 * Returns false if not exist. Returns true if no style attribute.
	 * @param boolean strict whether the visibility property must not be hidden, too. 
	 * If false, only the style.display property is tested.
	 * If true, both the style.display and style.visibility properties are tested.
	 * @return boolean whether the first matched element is visible.
	 */
	isVisible: function (strict) {
		var n = this.jq[0];
		return n && (!n.style || (n.style.display != "none" && (!strict || n.style.visibility != "hidden")));
	},
	/** Returns whether the first match element is really visible.
	 * By real visible we mean the element and all its ancestors are visible. 
	 * @param boolean strict whether the visibility property must not be hidden, too. 
	 * If false, only the style.display property is tested.
	 * If true, both the style.display and style.visibility properties are tested.
	 * @return boolean whether the first matched element is really visible.
	 */
	isRealVisible: function (strict) {
		var n = this.jq[0];
		return n && this.isVisible(strict) && (n.offsetWidth > 0 || n.offsetHeight > 0
		|| (!n.firstChild 
			&& (!(n=n.parentNode) || n==document.body || zk(n).isRealVisible(strict))));
			//Bug 3141549: look up parent if !firstChild (no check if firstChild for better performance)
	},

	/** Scrolls the browser window to make the first matched element visible.
	 * <p>Notice that it scrolls only the browser window. If the element is obscured by another element, it is still not visible. To really make it visible, use el.scrollIntoView() instead.
	 * @return jqzk this object
	 */
	scrollTo: function () {
		if (this.jq.length) {
			var pos = this.cmOffset();
			scrollTo(pos[0], pos[1]);
		}
		return this;
	},
	/** Causes the first matched element to scroll into view.
	 * @param DOMElement parent scrolls the first matched element into the parent's view,
	 * if any. Otherwise, document.body is assumed. 
	 * @return jqzk this object
	 */
	scrollIntoView: function (parent) {
		var n = this.jq[0];
		if (n) {
			parent = parent || document.body.parentNode;
			for (var p = n, c; (p = p.parentNode) && n != parent; n = p)
				c = _scrlIntoView(p, n, c);
		}
		return this;
	},
	/**
	 * Checks whether the element is shown in the current viewport.
	 * @return boolean if false, it means the element is not shown.
	 * @since 6.5.2
	 */
	isScrollIntoView: (function () {
		function _overflowElement(self) {
			var el = self.jq[0],
				te, le;
			do {
				if (!te) {
					if (el == document.body || el.style.overflow == 'auto' || el.style.overflowY == 'auto')
						te = el;
				}
				if (!le) {
					if (el == document.body || el.style.overflow == 'auto' || el.style.overflowX == 'auto')
						le = el;
				}
				if (te && le)
					break;
				el = el.parentNode;
			} while (el);
			return [le, te];
		}
		return function () {
			var vOffset = this.viewportOffset(),
				x = vOffset[0],
				y = vOffset[1],
				w = this.jq[0].offsetWidth,
				h = this.jq[0].offsetHeight,
				x1 = x + w,
				y1 = y + h;
			
			// browser's viewport
			if (x >= 0 && y >= 0 && x1 <= jq.innerWidth() && y1 <= jq.innerHeight()) {
				var oel = _overflowElement(this),
				lex = zk(oel[0].parentNode).viewportOffset()[0],
				tey = zk(oel[1].parentNode).viewportOffset()[1];
				
				// scrollbar's viewport
				return (x >= lex && x1 <= lex + oel[0].offsetWidth && y >= tey && y1 <= tey + oel[1].offsetHeight);
			}
			return false;		
		};
	})(),
	/** Tests if the first matched DOM element has the vertical scrollbar
	 * @return int the difference of offsetWidth and clientWidth if the element has the vertical scrollbar,
	 * or 0 if no scrollbar
	 * @since 5.0.8
	 */
	hasVScroll: function () {
		var n, v;
		return (n = this.jq[0]) && (v = n.clientWidth) && (v = n.offsetWidth - v) > 11 ? v: 0;
	},
	/** Tests if the first matched DOM element has the horizontal scrollbar
	 * @return int the difference of offsetHeight and clientHeight if the element has the horizontal scrollbar,
	 * or 0 if no scrollbar
	 * @since 5.0.8
	 */
	hasHScroll: function () {
		var n, v;
		return (n = this.jq[0]) && (v = n.clientHeight) && (v = n.offsetHeight - v) > 11 ? v: 0;
	},

	/** Tests if the first matched element is overlapped with the specified
	 * element.
	 * @param DOMElement el the element to check with
	 * @param int the tolerant value for the calculation
	 * @return boolean true if they are overlapped.
	 */
	isOverlapped: function (el, tolerant) {
		var n;
		if (n = this.jq[0])
			return jq.isOverlapped(
				this.cmOffset(), [n.offsetWidth, n.offsetHeight], zk(el).cmOffset(),
				    [el.offsetWidth, el.offsetHeight], tolerant);
	},

	/** Returns the summation of the specified styles.
	 * For example,
<pre><code>
jq(el).zk.sumStyles("lr", jq.paddings);
  //sums the style values of jq.paddings['l'] and jq.paddings['r'].
</code></pre>
	 *
	 * @param String areas the areas is abbreviation for left "l", right "r", top "t", and bottom "b". So you can specify to be "lr" or "tb" or more. 
	 * @param Array styles an array of styles, such as {@link jq#paddings}, {@link jq#margins} or {@link jq#borders}.
	 * @return int the summation
	 */
	sumStyles: function (areas, styles) {
		var val = 0;
		for (var i = 0, len = areas.length, $jq = this.jq; i < len; i++){
			 var w = Math.round(zk.parseFloat($jq.css(styles[areas.charAt(i)])));
			 if (!isNaN(w)) val += w;
		}
		return val;
	},

	/** Sets the offset height by specifying the inner height. 
	 * @param int hgh the height without margin and border 
	 * @return jqzk this object
	 */
	setOffsetHeight: function (hgh) {
		var $jq = this.jq;
		hgh -= this.padBorderHeight()
			+ zk.parseInt($jq.css("margin-top"))
			+ zk.parseInt($jq.css("margin-bottom"));
		$jq[0].style.height = jq.px0(hgh);
		return this;
	},

	/** Returns the revised (i.e., browser's coordinate) offset of the selected
	 * element.
	 * In other words, it is the offset of the left-top corner related to
	 * the browser window.
	 * @return Offset the revised offset 
	 * @see #cmOffset
	 */
	/** Converts the specified offset in the element's coordinate to
	 * to the browser window's coordinateReturns the revised (calibrated) offset, i.e., the offset of the element
	 * related to the screen.
	 * <p>It is calculated by subtracting the offset of the scrollbar
	 * ({@link #scrollOffset} and {@link jq#innerX}), for the first matched element.
	 * @param Offset ofs the offset to revise. If not specified, the first matched
	 * element's bounding rectange is assumed.
	 * @return Offset the revised offset 
	 */
	revisedOffset: function (ofs) {
		var el = this.jq[0];
		if(!ofs) {
			if (el.getBoundingClientRect){ // IE and FF3
				var elst, oldvisi;
				if (zk.ie && el.style.display == "none") {
				//When popup a window in an iframe, getBoundingClientRect not correct (test case: B36-2851102.zul within iframe)
					oldvisi = (elst = el.style).visibility;
					elst.visibility = "hidden";
					elst.display = "";
				}

				var b = el.getBoundingClientRect();
				b = [b.left + jq.innerX() - el.ownerDocument.documentElement.clientLeft,
					b.top + jq.innerY() - el.ownerDocument.documentElement.clientTop];

				if (elst) {
					elst.display = "none";
					elst.visibility = oldvisi;
				}
				return b;
				// IE adds the HTML element's border, by default it is medium which is 2px
				// IE 6 and 7 quirks mode the border width is overwritable by the following css html { border: 0; }
				// IE 7 standards mode, the border is always 2px
				// This border/offset is typically represented by the clientLeft and clientTop properties
				// However, in IE6 and 7 quirks mode the clientLeft and clientTop properties are not updated when overwriting it via CSS
				// Therefore this method will be off by 2px in IE while in quirksmode
			}
			ofs = this.cmOffset();
		}
		var scrolls = zk(el.parentNode).scrollOffset();
		scrolls[0] -= jq.innerX(); scrolls[1] -= jq.innerY();
		return [ofs[0] - scrolls[0], ofs[1] - scrolls[1]];
	},
	/** Returns the revised (calibrated) width, which subtracted the width of its CSS border or padding, for the first matched element.
	 * <p>It is usually used to assign the width to an element (since we have to subtract the padding).
<pre><code>el.style.width = jq(el).zk.revisedWidth(100);</code></pre>
	 *
	 * @param int size the width to be assigned to the specified element.
	 * @param boolean excludeMargin whether to subtract the margins, too.
	 * You rarely need this unless the width is specified in term of the parent's perspective. 
	 * @return int the revised width
	 */
	revisedWidth: function (size, excludeMargin) {
		if (this.jq.css('box-sizing') != 'border-box')
			size -= this.padBorderWidth();
		if (size > 0 && excludeMargin)
			size -= this.sumStyles("lr", jq.margins);
		return size < 0 ? 0: size;
	},
	/** Returns the revised (calibrated) height, which subtracted the height of its CSS border or padding, for the first matched element.
	 * <p>It is usually used to assign the height to an element (since we have to subtract the padding).
<pre><code>el.style.height = jq(el).zk.revisedHeight(100);</code></pre>
	 *
	 * @param int size #  the height to be assigned to the first matched element.
	 * @param boolean excludeMargin whether to subtract the margins, too.
	 * You rarely need this unless the height is specified in term of the parent's perspective. 
	 * @return int the revised height
	 */
	revisedHeight: function (size, excludeMargin) {
		if (this.jq.css('box-sizing') != 'border-box')
			size -= this.padBorderHeight();
		if (size > 0 && excludeMargin)
			size -= this.sumStyles("tb", jq.margins);
		return size < 0 ? 0: size;
	},
	/** Returns the summation of the border width of the first matched element.
	 * @return int summation
	 */
	borderWidth: function () {
		return this.sumStyles("lr", jq.borders);
	},
	/** Returns the summation of the border height of the first matched element.
	 * @return int summation
	 */
	borderHeight: function () {
		return this.sumStyles("tb", jq.borders);
	},
	/** Returns the summation of the padding width of the first matched element.
	 * @return int summation
	 */
	paddingWidth: function () {
		return this.sumStyles("lr", jq.paddings);
	},
	/** Returns the summation of the padding height of the first matched element.
	 * @return int summation
	 */
	paddingHeight: function () {
		return this.sumStyles("tb", jq.paddings);
	},
	/** Returns the summation of the padding height and the border width of the first matched element. 
	 * @return int the summation
	 */
	padBorderWidth: function () {
		return this.borderWidth() + this.paddingWidth();
	},
	/** Returns the summation of the padding width and the border height of the first matched element.
	 * @return int the summation
	 */
	padBorderHeight: function () {
		return this.borderHeight() + this.paddingHeight();
	},
	/** Returns the maximal allowed height of the first matched element.
	 * In other words, it is the client height of the parent minus all sibling's.
	 * @return int the maximal allowed height
	 */ 
	vflexHeight: function () {
		var el = this.jq[0],
			hgh = el.parentNode.clientHeight;
		for (var p = el; p = p.previousSibling;)
			if (p.offsetHeight && zk(p).isVisible())
				hgh -= p.offsetHeight; //may undefined
		for (var p = el; p = p.nextSibling;)
			if (p.offsetHeight && zk(p).isVisible())
				hgh -= p.offsetHeight; //may undefined
		return hgh;
	},
	/** Retrieves the index of the first selected (table) cell in the cells collection of a (table) row.
	 * <p>Note: The function fixed the problem of IE that cell.cellIndex returns a wrong index if there is a hidden cell in the table. 
 	 * @return int the index of the first selected cell
 	 * @see #ncols
 	 */
	cellIndex: function () {
		var cell = this.jq[0];
		return cell ? cell.cellIndex: 0;
	},
	/** Returns the number of columns of a row. Notice that it, unlike row.cells.length, calculate colSpan, too. In addition, it can filter out invisible cells.
	 * @param boolean visibleOnly whether not count invisible cells
	 * @return int the number of columns of the first selected row
	 * @see #cellIndex
	 */
	ncols: function (visibleOnly) {
		var row = this.jq[0],
			cnt = 0, cells;
		if (row && (cells = row.cells))
			for (var j = 0, cl = cells.length; j < cl; ++j) {
				var cell = cells[j];
				if (!visibleOnly || zk(cell).isVisible()) {
					var span = cell.colSpan;
					if (span >= 1) cnt += span;
					else ++cnt;
				}
			}
		return cnt;
	},
	/** Converts an offset (x,y) from absolute coordination to the element's style coordination, such that you can assign them to the style (el.style). 
	 * @param int x the X coordinate
	 * @param int y the Y coordinate
	 * @return Offset the offset
	 */
	toStyleOffset: function (x, y) {
		var el = this.jq[0],
			oldx = el.style.left, oldy = el.style.top,
			resetFirst = zk.opera || zk.air || zk.ie8;
		//Opera:
		//1)we have to reset left/top. Or, the second call position wrong
		//test case: Tooltips and Popups
		//2)we cannot assing "", either
		//test case: menu
		//IE/gecko fix: auto causes toStyleOffset incorrect
		if (resetFirst || el.style.left == "" || el.style.left == "auto")
			el.style.left = "0";
		if (resetFirst || el.style.top == "" || el.style.top == "auto")
			el.style.top = "0";

		var ofs1 = this.cmOffset(),
			x2 = zk.parseInt(el.style.left),
			y2 = zk.parseInt(el.style.top);
		ofs1 = [x - ofs1[0] + x2, y  - ofs1[1] + y2];

		el.style.left = oldx; el.style.top = oldy; //restore
		return ofs1;
	},
	/** Positions the first selected element at the particular location of the browser window.
<pre><code>
jq(el).zk.center('left top');
jq(el).zk.center('left center');
jq(el).zk.center('left'); //not to change the Y coordinate
jq(el).zk.center(); //same as 'center'
</code></pre>
	 *
     * @param Map flags A combination of center, left, right, top and bottom. If omitted, center is assumed.
     * @return jqzk this object
	 * @see #position
	 */
	center: function (flags) {
		var el = this.jq[0],
			wdgap = this.offsetWidth(),
			hghgap = this.offsetHeight();

		if ((!wdgap || !hghgap) && !this.isVisible()) {
			el.style.left = el.style.top = "-10000px"; //avoid annoying effect
			el.style.display = "block"; //we need to calculate the size
			wdgap = this.offsetWidth();
			hghgap = this.offsetHeight(),
			el.style.display = "none"; //avoid Firefox to display it too early
		}

		var left = jq.innerX(), top = jq.innerY();
		var x, y, skipx, skipy;

		wdgap = jq.innerWidth() - wdgap;
		if (!flags) x = left + wdgap / 2;
		else if (flags.indexOf("left") >= 0) x = left;
		else if (flags.indexOf("right") >= 0) x = left + wdgap - 1; //just in case
		else if (flags.indexOf("center") >= 0) x = left + wdgap / 2;
		else {
			x = 0; skipx = true;
		}

		hghgap = jq.innerHeight() - hghgap;
		if (!flags) y = top + hghgap / 2;
		else if (flags.indexOf("top") >= 0) y = top;
		else if (flags.indexOf("bottom") >= 0) y = top + hghgap - 1; //just in case
		else if (flags.indexOf("center") >= 0) y = top + hghgap / 2;
		else {
			y = 0; skipy = true;
		}

		if (x < left) x = left;
		if (y < top) y = top;

		var ofs = this.toStyleOffset(x, y);

		if (!skipx) el.style.left = jq.px(ofs[0]);
		if (!skipy) el.style.top =  jq.px(ofs[1]);
		return this;
	},
	/** Position the first matched element to the specified location.
	 *
	 * @param Dimension dim the dimension of the anchor location
	 * @param String where where to position. Default: <code>overlap</code><br/>
	 * Allowed values:</br>
	 * <ul>
	 * 	<li><b>before_start</b><br/> the element appears above the anchor, aligned to the left.</li>
	 * 	<li><b>before_center</b><br/> the element appears above the anchor, aligned to the center.</li>
	 *  <li><b>before_end</b><br/> the element appears above the anchor, aligned to the right.</li>
	 *  <li><b>after_start</b><br/> the element appears below the anchor, aligned to the left.</li>
	 *  <li><b>after_center</b><br/> the element appears below the anchor, aligned to the center.</li>
	 *  <li><b>after_end</b><br/> the element appears below the anchor, aligned to the right.</li>
	 *  <li><b>start_before</b><br/> the element appears to the left of the anchor, aligned to the top.</li>
	 *  <li><b>start_center</b><br/> the element appears to the left of the anchor, aligned to the middle.</li>
	 *  <li><b>start_after</b><br/> the element appears to the left of the anchor, aligned to the bottom.</li>
	 *  <li><b>end_before</b><br/> the element appears to the right of the anchor, aligned to the top.</li>
	 *  <li><b>end_center</b><br/> the element appears to the right of the anchor, aligned to the middle.</li>
	 *  <li><b>end_after</b><br/> the element appears to the right of the anchor, aligned to the bottom.</li>
	 *  <li><b>overlap/top_left</b><br/> the element overlaps the anchor, with anchor and element aligned at top-left.</li>
	 *  <li><b>top_center</b><br/> the element overlaps the anchor, with anchor and element aligned at top-center.</li>
	 *  <li><b>overlap_end/top_right</b><br/> the element overlaps the anchor, with anchor and element aligned at top-right.</li>
	 *  <li><b>middle_left</b><br/> the element overlaps the anchor, with anchor and element aligned at middle-left.</li>
	 *  <li><b>middle_center</b><br/> the element overlaps the anchor, with anchor and element aligned at middle-center.</li>
	 *  <li><b>middle_right</b><br/> the element overlaps the anchor, with anchor and element aligned at middle-right.</li>
	 *  <li><b>overlap_before/bottom_left</b><br/> the element overlaps the anchor, with anchor and element aligned at bottom-left.</li>
	 *  <li><b>bottom_center</b><br/> the element overlaps the anchor, with anchor and element aligned at bottom-center.</li>
	 *  <li><b>overlap_after/bottom_right</b><br/> the element overlaps the anchor, with anchor and element aligned at bottom-right.</li>
	 *  <li><b>at_pointer</b><br/> the element appears with the upper-left aligned with the mouse cursor.</li>
	 *  <li><b>after_pointer</b><br/> the element appears with the top aligned with
	 *  	the bottom of the mouse cursor, with the left side of the element at the horizontal position of the mouse cursor.</li>
	 * </ul>
	 * @param Map opts a map of addition options. Allowed values include
<ol><li> <b>overflow</b><br /> whether to allow the element being scrolled out of the visible area (Default: false, i.e., not allowed). If not specified (or false), the popup always remains visible even if the user scrolls the anchor widget out of the visible area</li></ol>
	 * @return jqzk this object
     * @see #center
     */
	/** Position the first matched element to the specified location.
	 *
	 * @param DOMElement dim the element used to get the dimension of the achor location.
	 * (by use of {@link #dimension})
	 * @param String where where to position. Default: <code>overlap</code><br/>
	 * Allowed values: refer to {@link #position(Dimension,String,Map)}.
	 * @param Map opts a map of addition options.<br/>
	 * Allowed values: refer to {@link #position(Dimension,String,Map)}.
	 * @return jqzk this object
     * @see #center
     */
	position: function (dim, where, opts) {
		where = where || "overlap";
		
		if (!dim) {
			var bd = jq('body')[0];
			dim = {
				left:0, top: 0,
				width: bd.offsetWidth, height: bd.offsetHeight
			};
		}
		
		if (dim.nodeType) //DOM element
			dim = zk(dim).dimension(true);
		var x = dim.left, y = dim.top,
			wd = this.dimension(), hgh = wd.height; //only width and height
		wd = wd.width;
		
		/*Fixed since ios safari 5.0.2(webkit 533.17.9)
		if (zk.ios) { // Bug 3042165(iphone/ipad)
			x -= jq.innerX();
			y -= jq.innerY();
		}
		*/
		switch(where) {
		case "before_start":
			y -= hgh;
			break;
		case "before_center":
			y -= hgh;
			x += (dim.width - wd) / 2 | 0;
			break;
		case "before_end":
			y -= hgh;
			x += dim.width - wd;
			break;
		case "after_start":
			y += dim.height;
			break;
		case "after_center":
			y += dim.height;
			x += (dim.width - wd) / 2 | 0;
			break;
		case "after_end":
			y += dim.height;
			x += dim.width - wd;
			break;
		case "start_before":
			x -= wd;
			break;
		case "start_center":
			x -= wd;
			y += (dim.height - hgh) / 2 | 0;
			break;
		case "start_after":
			x -= wd;
			y += dim.height - hgh;
			break;
		case "end_before":
			x += dim.width;
			break;
		case "end_center":
			x += dim.width;
			y += (dim.height - hgh) / 2 | 0;
			break;
		case "end_after":
			x += dim.width;
			y += dim.height - hgh;
			break;
		case "at_pointer":
			var offset = zk.currentPointer;
			x = offset[0];
			y = offset[1];
			break;
		case "after_pointer":
			var offset = zk.currentPointer;
			x = offset[0];
			y = offset[1] + 20;
			break;
		case "top_right":
		case "overlap_end":
			x += dim.width - wd;
			break;
		case "top_center":
			x += (dim.width - wd) / 2 | 0;
			break;
		case "middle_left":
			y += (dim.height - hgh) / 2 | 0;
			break;
		case "middle_center":
			x += (dim.width - wd) / 2 | 0;
			y += (dim.height - hgh) / 2 | 0;
			break;
		case "middle_right":
			x += dim.width - wd;
			y += (dim.height - hgh) / 2 | 0;
			break;
		case "bottom_left":
		case "overlap_before":
			y += dim.height - hgh;
			break;
		case "bottom_center":
			x += (dim.width - wd) / 2 | 0;
			y += dim.height - hgh;
			break;
		case "bottom_right":
		case "overlap_after":
			x += dim.width - wd;
			y += dim.height - hgh;
			break;
		default: // overlap/top_left is assumed
			// nothing to do.
		}
		
		if (!opts || !opts.overflow) {
			var scX = jq.innerX(),
				scY = jq.innerY(),
				scMaxX = scX + jq.innerWidth(),
				scMaxY = scY + jq.innerHeight();
			
			if (x + wd > scMaxX) x = scMaxX - wd;
			if (x < scX) x = scX;
			if (y + hgh > scMaxY) y = scMaxY - hgh;
			if (y < scY) y = scY;
		}
		
		// Bug 3251564
		// dodge reference element (i.e. not to cover the reference textbox, etc)
		if (opts && opts.dodgeRef) {
			var dl = dim.left, dt = dim.top,
				dr = dl + dim.width, db = dt + dim.height;
			// overlap test
			if (x + wd > dl && x < dr && y + hgh > dt && y < db) {
				if (opts.overflow) {
					// overflow allowed: try to dodge to the right
					x = dr;
				} else {
					var scX = jq.innerX(),
						scMaxX = scX + jq.innerWidth(),
						spr = scMaxX - dr,
						spl = dl - scX;
					// no overflow: dodge to the larger space
					// try right side first
					if (spr >= wd || spr >= spl)
						x = Math.min(dr, scMaxX - wd);
					else
						x = Math.max(dl - wd, scX);
				}
			}
		}

		var el = this.jq[0],
			ofs = this.toStyleOffset(x, y);
		el.style.left = jq.px(ofs[0]);
		el.style.top = jq.px(ofs[1]);
		return this;
	},

	/** Calculates the cumulative scroll offset of the first matched element in nested scrolling containers. It adds the cumulative scrollLeft and scrollTop of an element and all its parents.
	 * <p>It is used for calculating the scroll offset of an element that is in more than one scroll container (e.g., a draggable in a scrolling container which is itself part of a scrolling document).
	 * <p>Note that all values are returned as numbers only although they are expressed in pixels. 
	 * @return Offset the cumulative scroll offset.
	 * @see #cmOffset
	 * @see #viewportOffset
	 * @see #revisedOffset
	 */
	scrollOffset: function() {
		var el = this.jq[0],
			t = 0, l = 0;
		do {
			t += el.scrollTop  || 0;
			l += el.scrollLeft || 0;
			el = el.parentNode;
		} while (el);
		return [l, t];
	},
	/** Returns the cumulative offset of the first matched element from the top left corner of the document.
	 * <p>It actually adds the cumulative offsetLeft and offsetTop of an element and all its parents.
	 * <p>Note that it ignores the scroll offset. If you want the element's coordinate,
	 * use {@link #revisedOffset()} instead.
	 * <p>Note that all values are returned as numbers only although they are expressed in pixels. 
	 * @return Offset the cumulative offset
	 * @see #scrollOffset
	 * @see #viewportOffset
	 * @see #revisedOffset
	 */
	cmOffset: function () {
		//fix safari's bug: TR has no offsetXxx
		var el = this.jq[0];
		if (zk.safari && jq.nodeName(el, "tr") && el.cells.length)
			el = el.cells[0];

		//fix gecko and safari's bug: if not visible before, offset is wrong
		if (!(zk.gecko || zk.safari)
		|| this.isVisible() || this.offsetWidth())
			return _cmOffset(el);

		el.style.display = "";
		var ofs = _cmOffset(el);
		el.style.display = "none";
		return ofs;
	},
	/**
	 * A short cut for looking up ZK Widget from jQuery object.
	 * Now the code 
	 * <pre><code> zk.Widget.$(jq("@listbox"))</code></pre> could be replaced with <code><pre> zk("@listbox").$() </code></pre>
	 */
	$:function(){
		return zk.Widget.$(this.jq[0]);
	},
	/** Makes the position of the first selected element as absolute.
	 * In addition to changing the style's position to absolute, it
	 * will store the location such that it can be restored later when
	 * {@link #relativize} is called.
	 *<pre><code>zk('abc').absolutize();</code></pre>
	 * @return jqzk this object
	 * @see #relativize
	 */
	absolutize: function() {
		var el = this.jq[0];
		if (el.style.position == 'absolute') return this;

		var offsets = _posOffset(el),
			left = offsets[0], top = offsets[1],
			st = el.style;
		el._$orgLeft = left - parseFloat(st.left  || 0);
		el._$orgTop = top  - parseFloat(st.top || 0);
		st.position = 'absolute';
		st.top = jq.px(top);
		st.left = jq.px(left);
		return this;
	},
	/** Makes the position of the element as relative. In addition to changing the style's position to relative, it tries to restore the location before calling {@link #absolutize}. 
	 * @return jqzk this object
	 * @see #absolutize
	 */
	relativize: function() {
		var el = this.jq[0];
		if (el.style.position == 'relative') return this;

		var st = el.style;
		st.position = 'relative';
		var top  = parseFloat(st.top  || 0) - (el._$orgTop || 0),
			left = parseFloat(st.left || 0) - (el._$orgLeft || 0);

		st.top = jq.px(top);
		st.left = jq.px(left);
		return this;
	},

	/** Returns the offset width. It is similar to el.offsetWidth, except it solves some browser's bug or limitation. 
	 * @return int the offset width
	 */
	offsetWidth: function () {
		return this.jq[0].offsetWidth;
	},
	/** Returns the offset height. It is similar to el.offsetHeight, except it solves some browser's bug or limitation. 
	 * @return int the offset height
	 */
	offsetHeight: function () {
		return this.jq[0].offsetHeight;
	},
	/** Returns the offset top. It is similar to el.offsetTop, except it solves some browser's bug or limitation. 
	 * @return int the offset top
	 */
	offsetTop: function () {
		return this.jq[0].offsetTop;
	},
	/** Returns the offset left. It is similar to el.offsetLeft, except it solves some browser's bug or limitation.
	 * @return int the offset left
	 */
	offsetLeft: function () {
		return this.jq[0].offsetLeft;
	},

	/** Returns the X/Y coordinates of the first matched element relative to the viewport. 
	 * @return Offset the coordinates
	 * @see #cmOffset
	 * @see #scrollOffset
	 */
	viewportOffset: function() {
		var t = 0, l = 0, el = this.jq[0], p = el;
		do {
			t += p.offsetTop  || 0;
			l += p.offsetLeft || 0;

			// Safari fix
			if (p.offsetParent==document.body)
			if (jq(p).css('position')=='absolute') break;

		} while (p = p.offsetParent);

		do {
			// Opera 12.15 fix this
			// if (!zk.opera || jq.nodeName(el, 'body')) {
				t -= el.scrollTop  || 0;
				l -= el.scrollLeft || 0;
			//}
		} while (el = el.parentNode);
		return [l, t];
	},
	/** Returns the size of the text if it is placed inside the first matched element.
	 * @param String text the content text 
	 * @return Size the size of the text
	 */
	textSize: function (txt) {
		if (!_txtSizDiv) {
			_txtSizDiv = document.createElement("div");
			_txtSizDiv.style.cssText = "left:-1000px;top:-1000px;position:absolute;visibility:hidden;border:none";
			document.body.appendChild(_txtSizDiv);

			_txtStylesCamel = [];
			for (var ss = _txtStyles, j = ss.length; j--;)
				_txtStylesCamel[j] = ss[j].$camel();
		}
		_txtSizDiv.style.display = 'none';
		var jq = this.jq;
		for (var ss = _txtStylesCamel, j = ss.length; j--;) {
			var nm = ss[j];
			_txtSizDiv.style[nm] = jq.css(nm);
		}

		_txtSizDiv.innerHTML = txt || jq[0].innerHTML;
		_txtSizDiv.style.display = '';
		return [_txtSizDiv.offsetWidth, _txtSizDiv.offsetHeight];
	},

	/** Returns the dimension of the specified element.
	 * <p>If revised not specified (i.e., not to calibrate), the left and top are the offsetLeft and offsetTop of the element.
	 * @param boolean revised if revised is true, {@link #revisedOffset} will be
	 * 		used (i.e., the offset is calibrated). 
	 * @return Dimension the dimension
	 */
	dimension: function (revised) {
		var display = this.jq.css('display');
		if (display != 'none' && display != null) // Safari bug
			return _addOfsToDim(this,
				{width: this.offsetWidth(), height: this.offsetHeight()}, revised);

	// All *Width and *Height properties give 0 on elements with display none,
	// so enable the element temporarily
		var st = this.jq[0].style,
			backup = {};
		zk.copy(st, {
			visibility: 'hidden',
			position: 'absolute',
			display: 'block'
			}, backup);
		try {
			return _addOfsToDim(this,
				{width: this.offsetWidth(), height: this.offsetHeight()}, revised);
		} finally {
			zk.copy(st, backup);
		}
	},

	/** Forces the browser to redo (re-apply) CSS of all matched elements. 
	 * <p>Notice that calling this method might introduce some performance penality.
	 * @param int timeout number of milliseconds to wait before really re-applying CSS.
	 * 100 is assumed if not specified , -1 means re-applying css right now.
	 * @return jqzk this object
	 */
	redoCSS: function (timeout) {
		if (timeout == -1){ //timeout -1 means immediately
			for (var j = this.jq.length; j--;)
				zjq._fixCSS(this.jq[j]);	
		} else {
			for (var j = this.jq.length; j--;)
				_rdcss.push(this.jq[j]);
			setTimeout(_redoCSS0, timeout >= 0 ? timeout : 100);
		}
		return this;
	},
	/** Forces the browser to re-load the resource specified in the <code>src</code>
	 * attribute for all matched elements.
	 * @return jqzk this object
	 */
	redoSrc: function () {
		for (var j = this.jq.length; j--;) {
			var el = this.jq[j],
				src = el.src;
			el.src = zjq.src0;
			el.src = src;
		}
		return this;
	},

	/** Returns the virtual parent of the first matched element.
	 * <p>Refer to {@link #makeVParent} for more information.
	 * @param boolean real whether to return DOM element's parentNode if
	 * no virtual parent. In other words, <code>zk(n).vparentNode(true)</code>
	 * is the same as <code>zk(n).vparentNode()||n.parentNode</code>.
	 * @return DOMElement
	 */
	vparentNode: function (real) {
		var el = this.jq[0];
		if (el) {
			var v = el.z_vp; //might be empty
			if (v) return jq('#' + v)[0];
			v = el.z_vpagt;
			if (v && (v = jq('#' +v)[0]))
				return v.parentNode;
			if (real)
				return el.parentNode;
		}
	},
	/** Creates a virtual parent for the specified element. Creating a virtual parent makes the specified element able to appear above any other element (such as a menu popup). By default, the Z order of an element is decided by its parent and ancestors (if any of them has the relative or absolute position). If you want to resolve this limitation, you can create a virtual parent for it with this method.
	 * <p>To undo the creation of the virtual parent, use {@link #undoVParent}.
	 * <p>Notice that, due to browser's limitation, creating a virtual parent is still not enough to make an element appear on top of all others in all conditions. For example, it cannot in IE6 if there is a SELECT element underneath. To really solve this, you have to create a so-called stackup, which is actually an IFRAME element. By position the iframe right under the element, you can really want it appear on top.
	 * Refer to {@link jq#newStackup} for more information. 
	 * <h3>What Really Happens</h3>
	 * <p>This method actually moves the element to the topmost level in the DOM tree (i.e., as the child of document.body), and then the original parent (the parent before calling this method) becomes the virtual parent, which can be retrieved by {@link #vparentNode}.
	 * <h3>When to Use</h3>
	 * <p>When you implement a widget that appears above others, such as an overlapped window, a menu popup and a dropdown list, you can invoke this method when {@link Widget#bind_} is called. And then, restore it
	 * by calling {@link #undoVParent} when {@link Widget#unbind_} is called. 
	 * @return jqzk this object
	 */
	makeVParent: function () {
		var el = this.jq[0],
			p = el.parentNode;
		if (el.z_vp || el.z_vpagt || p == document.body)
			return this; //called twice or not necessary

		var sib = el.nextSibling,
			agt = document.createElement("span");
		agt.id = el.z_vpagt = '_z_vpagt' + _vpId ++;
		agt.style.display = "none";
		
		// Bug 3049181 and 3092040
		zjq._fixedVParent(el, true);
		
		if (sib) p.insertBefore(agt, sib);
		else p.appendChild(agt);

		el.z_vp = p.id; //might be empty
		var st = el.style;
		if (!st.top) st.top = "0";
			//B3178359: if no top and parent is relative+absolute, the following
			//line causes browser crazy
			//Strange: all browsers have the same behavior
		document.body.appendChild(el);
		return this;
	},
	/** Undoes the creation of a virtual parent of the first matched element.
	 * <p>Refer to {@link #makeVParent} for more information. 
	 * @return jqzk this object
	 */
	undoVParent: function () {
		var el = this.jq[0];
		if (el.z_vp || el.z_vpagt) {
			var p = el.z_vp,
				agt = el.z_vpagt,
				$agt = jq('#' + agt);
			el.z_vp = el.z_vpagt = null;
			agt = $agt[0];

			p = p ? jq('#' + p)[0]: agt ? agt.parentNode: null;
			if (p) {
				
				// Bug 3049181
				zjq._fixedVParent(el);
				
				if (agt) {
					p.insertBefore(el, agt);
					$agt.remove();
				} else
					p.appendChild(el);
				
				var cf, p, a;
				// ZK-851
				if ((zk.ff || zk.opera) && (cf = zk._prevFocus) && 
					(p = zk.Widget.$(el)) && zUtl.isAncestor(p, cf)) { 
					if (cf.getInputNode)
						jq(cf.getInputNode()).trigger('blur');
					else if ((a = cf.$n('a')) // ZK-1955
							&& jq.nodeName(a, 'button', 'input', 'textarea', 'a', 'select', 'iframe'))
						jq(a).trigger('blur');
					else if (cf.$instanceof(zul.wgt.Button)) // ZK-1324: Trendy button inside bandbox popup doesn't lose focus when popup is closed
						jq(cf.$n('btn') || cf.$n()).trigger('blur');
				}
			}
		}
		return this;
	},

	/** Fixes DOM elements when a widget's unbind_ is called
	 * and it will hide the DOM element (display="none" or visibility="hidden").
	 * <p>For firefox, it has to reset the src attribute of iframe (Bug 3076384)
	 */
	beforeHideOnUnbind: zk.$void,

	//focus/select//
	/** Sets the focus to the first matched element.
	 * It is the same as jq(n).focus() and n.focus, except
	 * <ul>
	 * <li>it doesn't throw any exception (rather, returns false).</li>
	 * <li>it can set the focus later (by use of timeout). </li>
	 * <li>it maintains {@link zk#currentFocus}.</li>
	 * </ul>
	 * <p>In general, it is suggested to use zk(n).focus(), unless
	 * n does not belong to any widget.
	 * @param int timeout the number of milliseconds to delay before setting the focus. If omitted or negative, the focus is set immediately. 
	 * @return boolean whether the focus is allowed to set to the element. Currently, only SELECT, BUTTON, INPUT and IFRAME is allowed. 
	 * @see #select
     */
	focus: function (timeout) {
		var n = this.jq[0];
		if (!n || !n.focus) return false;
			//ie: INPUT's focus not function

		if (!jq.nodeName(n, 'button', 'input', 'textarea', 'a', 'select', 'iframe'))
			return false;

		if (timeout >= 0) setTimeout(function() {_focus(n);}, timeout);
		else _focus(n);
		return true;
	},
	/** Selects the first matched element. It is the same as DOMElement.select, except it won't throws an exception (rather, returns false), and can delay the execution for the specified number of milliseconds.
	 * @param int timeout the number of milliseconds to delay before setting the focus. If omitted or negative, the focus is set immediately. 
	 * @return boolean whether the element supports the select method. In other words, it returns false if the object doesn't have the select method. 
	 * @see #getSelectionRange
	 * @see #setSelectionRange
	 * @see #focus
	 */
	select: function (timeout) {
		var n = this.jq[0];
		if (!n || typeof n.select != 'function') return false;

		if (timeout >= 0) setTimeout(function() {_select(n);}, timeout);
		else _select(n);
		return true;
	},

	/** Returns the selection range of the specified input-type element. The selection range is returned as a two-element array, where the first item is the starting index, and the second item is the ending index (excluding).
	 * <p>If an exception occurs, [0, 0] is returned. 
	 * @return Array a two-element array representing the selection range
	 * @see #setSelectionRange
	 * @see #select
	 */
	getSelectionRange: function() {
		var inp = this.jq[0];
		try {
			if (document.selection != null && inp.selectionStart == null) { //IE
				var range = document.selection.createRange();
				var rangetwo = inp.createTextRange();
				var stored_range = "";
				if(inp.type.toLowerCase() == "text"){
					stored_range = rangetwo.duplicate();
				}else{
					 stored_range = range.duplicate();
					 stored_range.moveToElementText(inp);
				}
				stored_range.setEndPoint('EndToEnd', range);
				var start = stored_range.text.length - range.text.length;
				return [start, start + range.text.length];
			} else { //Gecko
				return [inp.selectionStart, inp.selectionEnd];
			}
		} catch (e) {
			return [0, 0];
		}
	},
	/** Sets the selection range of the specified input-type element.
	 * @param int start the starting index of the selection range
	 * @param int end the ending index of the selection rane (excluding). In other words, the text between start and (end-1) is selected. 
	 * @return jqzk this object
	 */
	setSelectionRange: function (start, end) {
		var inp = this.jq[0],
			len = inp.value.length;
		if (start == null || start < 0) start = 0;
		if (start > len) start = len;
		if (end == null || end > len) end = len;
		if (end < 0) end = 0;

		if (inp.setSelectionRange) {
			inp.setSelectionRange(start, end);
		} else if (inp.createTextRange) {
			var range = inp.createTextRange();
			if(start != end){
				range.moveEnd('character', end - range.text.length);
				range.moveStart('character', start);
			}else{
				range.move('character', start);
			}
			range.select();
		}
		return this;
	},

	/** Submit the selected form.
	 * @return jqzk this object
	 * @since 5.0.4
	 */
	submit: function () {
		this.jq.each(_submit);
		return this;
	},

	//selection//
	/** Disallows the user to select a portion of its content. You usually invoke this method to disable the selection for button-like widgets. By default, all elements can be selected (unless disabled with CSS -- which not all browsers support).
	 * <p>If you disable the selection in {@link Widget#bind_}, you shall enable it back in {@link Widget#unbind_}
	 * since this method will register a DOM-level listener for certain browsers. 
	 * @return jqzk this object
	 */
	disableSelection: function () {
		this.jq.each(_dissel);
		return this;
	},
	/** Allows the user to select a portion of its content. You usually invoke this method to undo {@link #disableSelection}.
	 * By default, all elements can be selected (unless disabled with CSS -- which not all browsers support).
	 * @return jqzk this object
	 */
	enableSelection: function () {
		this.jq.each(_ensel);
		return this;
	},

	/** Sets the CSS style with a map of style names and values. For example,
<pre><code>
jq(el).css({width:'100px', paddingTop: "1px", "border-left": "2px"});
jq(el).css(jq.parseStyle(jq.filterTextStle('width:100px;font-size:10pt')));
</code></pre>
	 * <p>To parse a style (e.g., 'width:10px;padding:2px') to a map of style names and values, use {@link jq#parseStyle}.
	 * @return jqzk this object
	 * @deprecated As of release 5.0.2, use jq.css(map) instead
	 */
	setStyles: function (styles) {
		this.jq.css(styles);
		return this;
	},
	/** Clears the CSS styles (excluding the inherited styles).
	 * @since 5.0.2
	 * @return jqzk this object
	 */
	clearStyles: function () {
		var st = this.jq[0];
		if (st && (st=st.style))
			for (var nm in st)
				if ((!zk.ie || nm != "accelerator")
				&& st[nm] && typeof st[nm] == "string")
					try {
						st[nm] = "";
					} catch (e) { //ignore
					}
		return this;
	},
	/** Detaches all child elements and return them as an array.
	 * @since 5.0.10
	 * @return Array an array of {@link DOMElement} that are detached,
	 * or null if no element is selected.
	 */
	detachChildren: function () {
		var embed = this.jq[0];
		if (embed) {
			var val = [], n;
			while (n = embed.firstChild) {
				val.push(n);
				embed.removeChild(n);
			}
			return val;
		}
		return null;
	},

	/** Tests if all elements are input elements (including textarea).
	 * @return boolean
	 * @since 5.0.8
	 */
	isInput: function () {
		var $jq = this.jq,
			len = $jq.length,
			types = ['text', 'password', 'number', 'tel', 'url', 'email'];
		for (var j = len, tag, n; j--;)
			if ((tag = jq.nodeName(n = $jq[j])) != "textarea"
			&& (tag != "input" || (jq.inArray(n.type, types) == -1)))
				return false;
		return len > 0; //false if nothing selected
	}
};

/** @partial jq
 */
zk.copy(jq, {
	/** Returns the node name of the specified element in the lower case.
	 * @param DOMElement el the element to test.
	 * If el is null, an empty string is returned.
	 * @return String the node name.
	 * @since 5.0.1
	 */
	/** Returns if the node name of the specified element is the same
	 * as one of the specified name (case insensitive).
	 * @param DOMElement el the element to test
	 * @param String tag1 the name to test. You can have any number
	 * of names to test, such as <code>jq.nodeName(el, "tr", "td", "span")</code>
	 * @return boolean if the node name is the same as one of the specified names.
	 * @since 5.0.1
	 */
	nodeName: function (el) {
		var tag = el && el.nodeName ? el.nodeName.toLowerCase(): "",
			j = arguments.length;
		if (j <= 1)
			return tag;
		while (--j)
			if (tag == arguments[j].toLowerCase())
				return true;
		return false;// don't remove this line, texts are highlighted when SHIFT-click listitems (because of IE's onselect depends on it)
	},

	/** Converting an integer to a string ending with "px".
	 * <p>It is usually used for generating left or top.
	 * @param Integer v the number of pixels
	 * @return String the integer with string.
	 * @see #px0
	 */
	px: function (v) {
		return (v||0) + "px";
	},
	/** Converting an integer a string ending with "px".
	 * <p>Unlike {@link #px}, this method assumes 0 if v is negative.
	 * <p>It is usually used for generating width or height.
	 * @param Integer v the number of pixels. 0 is assumed if negative.
	 * @return String the integer with string.
	 * @see #px
	 */
	px0: function (v) {
		return Math.max(v||0, 0) + "px";
	},

	/** Returns an array of {@link DOMElement} that matches.
	 * It invokes <code>document.getElementsByName</code> to retrieve
	 * the DOM elements.
	 * @return Array an array of {@link DOMElement} that matches
	 * the specified condition
	 * @param String id the identifier
	 * @param String subId [Optional] the identifier of the sub-element.
	 * Example, <code>jq.$$('_u_12', 'cave');</code>.
	 */
	$$: function (id, subId) {
		return typeof id == 'string' ?
			id ? document.getElementsByName(id + (subId ? '-' + subId : '')): null: id;
	},

	/** Tests if one element (p) is an ancestor of another (c). 
	 * <p>Notice that, if you want to test widgets, please use
	 * {@link zUtl#isAncestor} instead.
	 *
	 * @param DOMElement p the parent element to test
	 * @param DOMElement c the child element to test
	 * @return boolean if p is an ancesotor of c.
	 * @see zUtl#isAncestor
	 */
	isAncestor: function (p, c) {
		if (!p) return true;
		for (; c; c = zk(c).vparentNode(true))
			if (p == c)
				return true;
		return false;
	},
	/** Returns the X coordination of the visible part of the browser window. 
	 * @return int
	 */
	innerX: function () {
		return window.pageXOffset
			|| document.documentElement.scrollLeft
			|| document.body.scrollLeft || 0;
	},
	/** Returns the Y coordination of the visible part of the browser window. 
	 * @return int
	 */
	innerY: function () {
		return window.pageYOffset
			|| document.documentElement.scrollTop
			|| document.body.scrollTop || 0;
	},
	/** Returns the height of the viewport (visible part) of the browser window. 
	 * It is the same as jq(window).width().
	 * @return int
	 */
	innerWidth: function () {
		return jq(window).width();
	},
	/** Returns the width of the viewport (visible part) of the browser window. 
	 * It is the same as jq(window).height().
	 * @return int
	 */
	innerHeight: function () {
		return jq(window).height();
	},

	/** A map of the margin style names: {l: 'margin-left', t: 'margin-top'...}. 
	 * It is usually used with {@link jqzk#sumStyles} to calculate the numbers specified
	 * in these styles. 
	 * @see #margins
	 * @see #paddings
	 * @return Map
	 */
	margins: {l: "margin-left", r: "margin-right", t: "margin-top", b: "margin-bottom"},
	/** A map of the border style names: {l: 'border-left', t: 'border-top'...}.
	 * It is usually used with {@link jqzk#sumStyles} to calculate the numbers specified
	 * in these styles. 
	 * @see #margins
	 * @see #paddings
	 * @return Map
	 */
	borders: {l: "border-left-width", r: "border-right-width", t: "border-top-width", b: "border-bottom-width"},
	/** A map of the padding style names: {l: 'padding-left', t: 'padding-top'...}. 
	 * It is usually used with {@link jqzk#sumStyles} to calculate the numbers specified
	 * in these styles. 
	 * @see #margins
	 * @see #borders
	 * @return Map
	 */
	paddings: {l: "padding-left", r: "padding-right", t: "padding-top", b: "padding-bottom"},

	/** Returns the width of the scrollbar
	 * @return int
	 */
	scrollbarWidth: function () {
		if (!_sbwDiv) {
			_sbwDiv = document.createElement("div");
			_sbwDiv.style.cssText = "top:-1000px;left:-1000px;position:absolute;visibility:hidden;border:none;width:50px;height:50px;overflow:scroll;";
			document.body.appendChild(_sbwDiv);
		}
		return _sbwDiv._value || (_sbwDiv._value = _sbwDiv.offsetWidth - _sbwDiv.clientWidth);
	},
    /** Returns if the specified rectangles are overlapped with each other.
     * @param Offset ofs1 the offset of the first rectangle
     * @param Offset dim1 the dimension (size) of the first rectangle
     * @param Offset ofs2 the offset of the second rectangle
     * @param Offset dim2 the dimension (size) of the second rectangle
     * @return boolean
     */
	/** Returns if the specified rectangles are overlapped with each other.
	 * @param Offset ofs1 the offset of the first rectangle
	 * @param Offset dim1 the dimension (size) of the first rectangle
	 * @param Offset ofs2 the offset of the second rectangle
	 * @param Offset dim2 the dimension (size) of the second rectangle
	 * @param int the tolerant value for the calculation
	 * @return boolean
	 */
	isOverlapped: function (ofs1, dim1, ofs2, dim2, tolerant) {
		var o1x1 = ofs1[0], o1x2 = dim1[0] + o1x1,
			o1y1 = ofs1[1], o1y2 = dim1[1] + o1y1;
		var o2x1 = ofs2[0], o2x2 = dim2[0] + o2x1,
			o2y1 = ofs2[1], o2y2 = dim2[1] + o2y1;
		if (tolerant) {
		  return o2x1 <= o1x2 && o2x2 >= o1x1 && o2y1 <= o1y2 && o2y2 >= o1y1
                 && o1x2 - o2x1 > tolerant && o2x2 - o1x1 > tolerant
                 && o1y2 - o2y1 > tolerant && o2y2 - o1y1 > tolerant;
		} else
		  return o2x1 <= o1x2 && o2x2 >= o1x1 && o2y1 <= o1y2 && o2y2 >= o1y1;
	},

	/** Clears the current selection in the browser window.
	 * <p>Notice: {@link jqzk#setSelectionRange} is used for the input-type
	 * elements, while this method is applied to the whole browser window. 
	 * @see jqzk#setSelectionRange
	 * @see jqzk#enableSelection
	 * @see jqzk#disableSelection
	 * @return boolean whether it is cleared successfully
	 */
	clearSelection: function () {
		try{
			if (window["getSelection"]) {
				if (zk.safari) window.getSelection().collapse();
				else window.getSelection().removeAllRanges();
			} else if (document.selection) {
				if (document.selection.empty) document.selection.empty();
				else if (document.selection.clear) document.selection.clear();
			}
			return true;
		} catch (e){
			return false;
		}
	},

	/** Returns the text-relevant style of the specified style
	 * (same as HTMLs.getTextRelevantStyle in Java).
	 * <pre>><code>
jq.filterTextStyle('width:100px;font-size:10pt;font-weight:bold');
  //return 'font-size:10pt;font-weight:bold'
	 *</code></pre>
	 *
	 * @param String style the style to filter
	 * @param Array plus an array of the names of the additional style to
	 * include, such as <code>['width', 'height']</code>. Ignored if not specified or null.
	 * @return String the text-related style
	 */
	/** Returns the text-relevant style of the specified styles
	 * (same as HTMLs.getTextRelevantStyle in Java).
	 * <pre>><code>
jq.filterTextStyle({width:"100px", fontSize: "10pt"});
  //return {font-size: "10pt"}
	 *</code></pre>
	 *
	 * @param Map styles the styles to filter
	 * @param Array plus an array of the names of the additional style to
	 * include, such as <code>['width', 'height']</code>. Ignored if not specified or null.
	 * @return Map the text-related styles
	 */
	filterTextStyle: function (style, plus) {
		if (typeof style == 'string') {
			var ts = "";
			if (style)
				for (var j = 0, k = 0; k >= 0; j = k + 1) {
					k = style.indexOf(';', j);
					var s = k >= 0 ? style.substring(j, k): style.substring(j),
						l = s.indexOf(':'),
						nm = l < 0 ? s.trim(): s.substring(0, l).trim();
					if (nm && (_txtStyles.$contains(nm)
					|| _txtStyles2.$contains(nm)
					|| (plus && plus.$contains(nm))))
						ts += s + ';';
				}
			return ts;
		}

		var ts = {};
		for (var nm in style)
			if (_txtStyles.$contains(nm) || _txtStyles2.$contains(nm)
			|| (plus && plus.$contains(nm)))
				ts[nm] = style[nm];
		return ts;
	},

	/** Parses a string-type CSS style into a map of names and values of styles.
	 * It is usually used with jq.css(map) to update the CSS style of an element. 
	 * @param String style the style to parse
	 * @return Map a map of styles (name, value)
	 */
	parseStyle: function (style) {
		var map = {};
		if (style) {
			var pairs = style.split(';');
			for (var j = 0, len = pairs.length; j < len;) {
				var v = pairs[j++].split(':'),
					nm = v.length > 0 ? v[0].trim(): '';
				if (nm)
					map[nm] = v.length > 1 ? v[1].trim(): '';
			}
		}
		return map;
	},

	/** Creates an IFRAME element with the specified ID, src and style. 
	 * @param String id ID (required)
	 * @param String src the source URL. If omitted, an one-pixel gif is assumed.
	 * @param String style the CSS style. Ingored if omitted. 
	 * @return DOMElement
	 */
	newFrame: function (id, src, style) {
		if (!src) src = zjq.src0;
			//IE: prevent secure/nonsecure warning with HTTPS

		var html = '<iframe id="'+id+'" name="'+id+'" src="'+src+'"';
		if (style == null) style = 'display:none';
		html += ' style="'+style+'"></iframe>';
		jq(document.body).append(html);
		return zk(id).jq[0];
	},
	/** Creates a 'stackup' (actually, an iframe) that makes an element
	 * (with position:absolute) shown above others.
	 * It is used to solve the layer issues of the browser.
	 * <ol>
	 * <li>IE6: SELECT's dropdown above any other DOM element</li>
	 * <li>All browser: PDF iframe above any other DOM element. However, this approach works only in FF and IE, and FF doesn't position IFRAME well if two or more of them are with the absolute position. </li>
	 * </ol>
	 *
	 * <p>Notice that you usually have to call {@link jqzk#makeVParent} before calling this, since DIV with relative or absolute position will crop the child element. In other words, you have to make the element as the top-level element before creating a stackup for it.
	 * <p>To remove the stackup, call {@link #remove}.
	 * <p>If you want to create a shadow, you don't need to access this method since {@link Shadow} has an option to create and maintain the stackup automatically. 
	 * @param DOMElement el the element to retrieve the dimensions. If omitted, the stackup is not appended to the DOM tree.
	 * @param String id ID of the stackup (iframe). If omitted and el is specified, it is el.id + '$ifrstk'. If both el and id are omitted, 'z_ifrstk' is assumed.
	 * @param DOMElement anchor where to insert the DOM element before
	 * (i.e., anchor will become the next sibling of the stackup, so anchor will be on top of the stackup if z-index is the same). If omitted, el is assumed. 
	 * @return DOMElement
	 */
	newStackup: function (el, id, anchor) {
		el = jq(el||[], zk)[0];
		var ifr = document.createElement("iframe");
		ifr.id = id || (el ? el.id + "-ifrstk": 'z_ifrstk');
		ifr.style.cssText = "position:absolute;overflow:hidden;opacity:0;filter:alpha(opacity=0)";
		ifr.frameBorder = "no";
		ifr.tabIndex = -1;
		ifr.src = zjq.src0;
		if (el) {
			ifr.style.width = el.offsetWidth + "px";
			ifr.style.height = el.offsetHeight + "px";
			ifr.style.top = el.style.top;
			ifr.style.left = el.style.left;
			ifr.style.zIndex = el.style.zIndex;
			el.parentNode.insertBefore(ifr, anchor || el);
		}
		return ifr;
	},
	/** Creates a HIDDEN element
	 * @param String name the name of the HIDDEN tag.
	 * @param String value the value of the HIDDEN tag.
	 * @param DOMElement parent the parent node. Ignored if not specified.
	 * @return DOMElement
	 */
	newHidden: function (nm, val, parent) {
		var inp = document.createElement("input");
		inp.type = "hidden";
		inp.name = nm;
		inp.value = val;
		if (parent) parent.appendChild(inp);
		return inp;
	},

	/** Returns the head element of this document.
	 * @return DOMElement the head element
	 * @since 5.0.1
	 */
	head: function () {
		return document.getElementsByTagName("head")[0] || document.documentElement;
	},

	//dialog//
	/** It is the same as <code>window.confirm</code?, except it will set
	 * {@link zk#alerting} so widgets know to ignore <code>onblur</code> (since the focus will be back later).
	 * <p>It is strongly suggested to use this method instead of <code>window.confirm</code>. 
	 * @return boolean whether the Yes button is pressed
	 */
	confirm: function (msg) {
		zk.alerting = true;
		try {
			return confirm(msg);
		} finally {
			try {zk.alerting = false;} catch (e) {} //doc might be unloaded
		}
	},
	/** Shows up a message.
	 * If opts.mode is os, this method is the same as window.alert, except it will set
	 * {@link zk#alerting}, so widgets (particularly input widgets) know to ignore onblur (since the focus will be back later).
	 * <p>It is strongly suggested to use this method instead of window.alert.
	 * <p>If opts is omitted or opts.mode is not os, it is similar to
	 * <code>org.zkoss.zul.Messagebox.show()</code> at the server.
<pre><code>
jq.alert('Hi');
jq.alert('This is a popup message box', {mode:"popup", icon: "ERROR"});
jq.alert('With listener', {
	button : {
  		YES: function () {jq.alert('Yes clicked')},
  		NO: function () {jq.alert('No clicked')}
	}
});
</code></pre>
	 * @param String msg the message to show
	 * @param Map opts the options.
<table border="1" cellspacing="0" width="100%">
<caption> Allowed Options
</caption>
<tr>
<th> Name
</th><th> Allowed Values
</th><th> Default Value
</th><th> Description
</th></tr>
<tr>
<td> icon
</td><td> 'QUESTION', 'EXCLAMATION', 'INFORMATION', 'ERROR', 'NONE'
</td><td> 'INFORMATION'
</td><td> Specifies the icon to display.
</td></tr>
<tr>
<td> mode
</td><td> 'overlapped', 'popup', 'modal'
</td><td> 'modal'
</td><td> Specifies which window mode to use.
</td></tr>
<tr>
<td> title
</td><td> any string
</td><td> 'ZK'
</td><td> Specifies the message box's title.
</td></tr>
<tr>
<td> desktop
</td><td> a desktop ({@link Desktop}) or null
</td><td> The current desktop
</td><td> Specifies which desktop this message box belongs to. You rarely need to specify it.
</td></tr>
<tr>
<td> button
</td><td> a map ({@link Map}) of buttons.
</td><td> If null or empty, OK is assumed
</td><td> Specifies what buttons to display. The key is the button name,
and the value is a function ({@link Function}) to execute when the button
is clicked.
The label is assumed to be <code>msgzul[name.toUpperCase()]||name</code>.
Localized labels include OK, Cancel, Yes, No, Retry, Abort, Ignore, Reload.
You can add your own labels by puttingit to <code>msgzul</code>.
</td></tr>
</table>
	 */
	alert: function (msg) {
		zk.alerting = true;
		try {
			alert(msg);
		} finally {
			try {zk.alerting = false;} catch (e) {} //doc might be unloaded
		}
	},
	/** To register one object for the <code>zsync</code> invocation.
	 * For example,
	 * <pre><code>jq.onzsync(obj1);</code></pre>
	 * @param Object obj the object to register
	 * @see #zsync
	 * @see #unzsync
	 * @since 5.0.1
	 */
	onzsync: function (obj) {
		_zsyncs.unshift(obj);
	},
	/** To unregister one object for the <code>zsync</code> invocation.
	 * For example,
	 * <pre><code>jq.unzsync(obj1);</code></pre>
	 * @param Object obj the object to register
	 * @see #zsync
	 * @see #onzsync
	 * @since 5.0.1
	 */
	unzsync: function (obj) {
		_zsyncs.$remove(obj);
	},
	/** To invoke the <code>zsync</code> method of the registered objects.
	 * <p><code>zsync</code> is called automatically when {@link zWatch}
	 * fires onSize, onShow or onHide.
	 * It is useful if you have a DOM element whose position is absolute.
	 * Then, if you register the widget, the widget's zsync method will be called when some widget becomes visible, is added and so on.
	 *
	 * <p>For example, {@link zul.wnd.Window} uses DIV to simulate the shadow in IE,
	 * then it can register itself in {@link Widget#bind_} and then
	 * synchronize the position and size of shadow (DIV) in zsync as follows.
	 * <pre><code>
bind_: function () {
  if (zk.ie) jq.onzsync(this); //register
...
},
unbind_: function () {
  if (zk.ie) jq.unzsync(this); //unregister
...
},
zsync: function () {
this._syncShadow(); //synchronize shadow
...
}</code></pre>
	 * <p>Notice that it is better not to use the absolute position for any child element, so the browser will maintain the position for you.
	 * After all, it runs faster and zsync won't be called if some 3rd-party library is used to create DOM element directly (without ZK).
	 */
	zsync: function (org) {
		++_pendzsync;
		setTimeout(function () {_zsync(org);}, 50);	
	},

	/** Move the focus out of any element.
	 * <p>Notice that you cannot simply use <code>jq(window).focus()</code>
	 * or <code>zk(window).focus()</code>,
	 * because it has no effect for browsers other than IE.
	 * @since 5.0.1
	 */
	focusOut: zk.ie ? function () {
		window.focus();
	}: function () {
		var a = jq('#z_focusOut')[0];
		if (!a) {
			// for Chrome and Safari, we can't set "display:none;"
			jq(document.body).append('<a href="javascript:;" style="position:absolute;'
					+ 'left:' + zk.clickPointer[0] + 'px;top:' + zk.clickPointer[1]
					+ 'px;" id="z_focusOut"/>');
			a = jq('#z_focusOut')[0];
		}
		a.focus();
		setTimeout(function () {jq(a).remove();}, 500);
	}
	/**
	 * An override function that provide a way to get the style value where is
	 * defined in the CSS file or the style object, rather than the computed value.
	 * <p> Note that the function is only applied to the width or height property,
	 *  and the third argument must be 'styleonly'.
	 * <p> For example,
<pre><code>
jq.css(elem, 'height', 'styleonly');
or
jq.css(elem, 'width', 'styleonly');
</code></pre>
	 * @since 5.0.6 
	 * @param DOMElement elem a Dom element
	 * @param String name the style name
	 * @param String extra an option in this case, it must be 'styleonly'
	 * @return String the style value.
	 */
	//css: function () {},
	/** Decodes a JSON string to a JavaScript object. 
	 * <p>It is similar to jq.parseJSON (jQuery's default function), except
	 * 1) it doesn't check if the string is a valid JSON
	 * 2) it uses eval to evaluate
	 * <p>Thus, it might not be safe to invoke this if the string's source
	 * is not trustable (and then it is better to use jq.parseJSON)
	 * @param String s the JSON string
	 * @return Object the converted object.
	 */
	//evalJSON: function () {},
	/** Encodes a JavaScript object to a JSON string. To decode, use jq.evalJSON(s), where s is a JSON string.
	 *
	 * <p>You can provide an optional replacer method. It will be passed the key and value of each member, with this bound to the containing object. The value that is returned from your method will be serialized. If your method returns undefined, then the member will be excluded from the serialization.
	 * Values that do not have JSON representations, such as undefined or functions, will not be serialized. Such values in objects will be dropped; in arrays they will be replaced with null. You can use a replacer function to replace those with JSON values. JSON.stringify(undefined) returns undefined.
	 * <p>The optional space parameter produces a stringification of the value that is filled with line breaks and indentation to make it easier to read.
	 * <p>If the space parameter is a non-empty string, then that string will be used for indentation. If the space parameter is a number, then the indentation will be that many spaces.
	 * <p>Example:
<pre><code>
text = jq.toJSON(['e', {pluribus: 'unum'}]);
// text is '["e",{"pluribus":"unum"}]'
 
text = jq.toJSON([new Date()], function (key, value) {
    return this[key] instanceof Date ?
        'Date(' + this[key] + ')' : value;
});
// text is '["Date(---current time---)"]'
</code></pre>
	 * @param Object obj any JavaScript object
	 * @param Object replace an optional parameter that determines how object values are stringified for objects. It can be a function. 
	 */
	//toJSON: function () {},
	/**
	 * Marshalls the Date object into a string such that it can be sent
	 * back to the server.
	 * <p>It works with org.zkoss.json.JSONs.d2j() to transfer data from client
	 * to server.
	 * @param Date d the date object to marshall. If null, null is returned
	 * @return String a string
	 * @since 5.0.5
	 */
	//d2j: function () {},
	/** Unmarshalls the string back to a Date object.
	 * <p>It works with org.zkoss.json.JSONs.j2d() to transfer data from server
	 * to client.
	 * @param String s the string that is marshalled at the server
	 * @return Date the date object after unmarshalled back
	 * @since 5.0.5
	 */
	//j2d: function () {}
});

/** @class jq.Event
 * A DOM event.
 */
zk.copy(jq.Event.prototype, {
	/** Stops the event propagation.
	 */
	stop: function () {
		this.preventDefault();
		this.stopPropagation();
	},
	/** Retrieve the mouse information of a DOM event. The properties of the returned object include pageX, pageY and the meta information 
	 * @return Map a map of data.
	 * @see zk.Event#data
	 */
	mouseData: function () {
		return zk.copy({
			pageX: this.pageX, pageY: this.pageY
		}, this.metaData());
	},
	/** Retrieve the mouse information of a DOM event. The properties of the returned object include pageX, pageY and the meta information ({@link #metaData}). 
	 * @return Map a map of data.
	 * @see zk.Event#data
	 */
	keyData: function () {
		return zk.copy({
			keyCode: this.keyCode,
			charCode: this.charCode
			}, this.metaData());
	},
	/** Retrieve the meta-information of a DOM event. The properties of the returned object include altKey, shiftKey, ctrlKey, leftClick, rightClick and which. 
	 * @return Map a map of data.
	 * @see zk.Event#data
	 */
	metaData: function () {
		var inf = {};
		if (this.altKey) inf.altKey = true;
		if (this.ctrlKey) inf.ctrlKey = true;
		if (this.shiftKey) inf.shiftKey = true;
		if (this.metaKey) inf.metaKey = true;
		inf.which = this.which || 0;
		return inf;
	}
});

/** @partial jq.Event
 */
zk.copy(jq.Event, {
	/** Fires a DOM element.
	 * @param DOMElement el the target element
	 * @param String evtnm the name of the event
	 */
	fire: document.createEvent ? function (el, evtnm) {
		var evt = document.createEvent('HTMLEvents');
		evt.initEvent(evtnm, false, false);
		el.dispatchEvent(evt);
	}: function (el, evtnm) {
		el.fireEvent('on' + evtnm);
	},
	/** Stops the event propagation of the specified event.
	 * It is usually used as the event listener, such as
	 * <pre><code>jq(el).mousemove(jq.Event.stop)</code></pre>
	 * @param jq.Event evt the event.
	 */
	stop: function (evt) {
		evt.stop();
	},
	/** Returns only the properties that are meta data, such as ctrlKey, altKey, ctrlKey and which.
	 * <p>For example, the following returns <code>{ctrlKey:true}</code>.
	* <pre><code>jq.event.filterMetaData({some:1,ctrlKey:true});</code></pre>
	* @param Map data a map of data. It is usually the value returned
	* by {@link #mouseData} or {@link #metaData}.
	* @return Map a map of data after filtered
	*/
	filterMetaData: function (data) {
		var inf = {}
		if (data.altKey) inf.altKey = true;
		if (data.ctrlKey) inf.ctrlKey = true;
		if (data.shiftKey) inf.shiftKey = true;
		if (data.metaKey) inf.metaKey = true;
		inf.which = data.which || 0;
		return inf;
	},
	/** Converts a DOM event ({@link jq.Event}) to a ZK event ({@link zk.Event}).
	 * @param jq.Event evt the DOM event
	 * @param zk.Widget wgt the target widget. It is used if the widget
	 * can be resolved from the event (<code>zk.Widget.$(evt)</code>)
	 * @return zk.Event the ZK event
	 */
	zk: function (evt, wgt) {
		var type = evt.type,
			target = zk.Widget.$(evt) || wgt,
			data;

		if (type.startsWith('mouse')) {
			if (type.length > 5)
				type = 'Mouse' + type.charAt(5).toUpperCase() + type.substring(6);
			data = evt.mouseData();
		} else if (type.startsWith('key')) {
			if (type.length > 3)
				type = 'Key' + type.charAt(3).toUpperCase() + type.substring(4);
			data = evt.keyData();
		} else if (type == 'dblclick') {
			data = evt.mouseData();
			type = 'DoubleClick';
		} else {
			if (type == 'click')
				data = evt.mouseData();
			type = type.charAt(0).toUpperCase() + type.substring(1);
		}
		return new zk.Event(target, 'on' + type, data, {}, evt);
	}
});
})(document, window);
