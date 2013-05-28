/* widget.js

	Purpose:
		Widget - the UI object at the client
	Description:
		z_rod indicates a widget is in the status of ROD (i.e., no rendered due to ROD)

	History:
		Tue Sep 30 09:23:56     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function (undefined) {
	var _binds = {}, //{uuid, wgt}: bind but no node
		_globals = {}, //global ID space {id, [wgt...]}
		_floatings = [], //[{widget:w,node:n}]
		_nextUuid = 0,
		_domevtfnm = {}, //{evtnm, funnm}
		_domevtnm = {onDoubleClick: 'dblclick'}, //{zk-evt-nm, dom-evt-nm}
		_wgtcls = {}, //{clsnm, cls}
		_hidden = [], //_autohide
		_noChildCallback, _noParentCallback, //used by removeChild/appendChild/insertBefore
		_syncdt, //timer ID to sync destkops
		_rdque = [], _rdtid, //async rerender's queue and timeout ID
		_ignCanActivate, //whether canActivate always returns true
		REGEX_DQUOT = /\"/g; //jsdoc can't handle it correctly, so we have to put here

	//Check if el is a prolog
	function _isProlog(el) {
		var txt;
		return el && el.nodeType == 3 //textnode
			&& (txt=el.nodeValue) && !txt.trim().length;
	}

	//Event Handling//
	function _domEvtInf(wgt, evtnm, fn) { //proxy event listener
		if (typeof fn != "function") {
			if (!fn && !(fn = _domevtfnm[evtnm]))
				_domevtfnm[evtnm] = fn = '_do' + evtnm.substring(2);

			var f = wgt[fn];
			if (!f)
				throw 'Listener ' + fn + ' not found in ' + wgt.className;
			fn = f;
		}

		var domn = _domevtnm[evtnm];
		if (!domn)
			domn = _domevtnm[evtnm] = evtnm.substring(2).toLowerCase();
		return [domn, _domEvtProxy(wgt, fn)];
	}
	function _domEvtProxy(wgt, f) {
		var fps = wgt._$evproxs, fp;
		if (!fps) wgt._$evproxs = fps = {};
		else if (fp = fps[f]) return fp;
		return fps[f] = _domEvtProxy0(wgt, f);
	}
	function _domEvtProxy0(wgt, f) {
		return function (evt) {
			var devt = evt; //make a copy since we will change evt (and arguments) in the following line
			evt = jq.Event.zk(devt, wgt); //also change arguments[0]

			switch (devt.type){
			case 'focus':
				if (wgt.canActivate()) {
					zk.currentFocus = wgt;
					//add triggerByFocus option for notification
					zWatch.fire('onFloatUp', wgt, {triggerByFocus: true}); //notify all
					break;
				}
				return; //ignore it
			case 'blur':
				//due to mimicMouseDown_ called, zk.currentFocus already corrected,
				//so we clear it only if caused by other case
				if (!zk._cfByMD) zk.currentFocus = null;
				break;
			case 'click':
			case 'dblclick':
			case 'mouseup': //we cannot simulate mousedown:(
				if (zk.Draggable.ignoreClick())
					return;
			}

			var ret = f.apply(wgt, arguments);
			if (ret === undefined) ret = evt.returnValue;
			if (evt.domStopped) devt.stop();
			return devt.type == 'dblclick' && ret === undefined ? false: ret;
		};
	}

	function _unlink(wgt, child) {
		var p = child.previousSibling, n = child.nextSibling;
		if (p) p.nextSibling = n;
		else wgt.firstChild = n;
		if (n) n.previousSibling = p;
		else wgt.lastChild = p;
		child.nextSibling = child.previousSibling = child.parent = null;

		--wgt.nChildren;
	}
	//replace the link of from with the link of to (note: it assumes no child)
	function _replaceLink(from, to) {
		var p = to.parent = from.parent,
			q = to.previousSibling = from.previousSibling;
		if (q) q.nextSibling = to;
		else if (p) p.firstChild = to;

		q = to.nextSibling = from.nextSibling;
		if (q) q.previousSibling = to;
		else if (p) p.lastChild = to;
	}

	function _bind0(wgt) { //always called no matter ROD or not
		_binds[wgt.uuid] = wgt;
		if (wgt.id)
			_addGlobal(wgt);
	}
	function _unbind0(wgt) {
		if (wgt.id)
			_rmGlobal(wgt);
		delete _binds[wgt.uuid];
		wgt.desktop = null;
		wgt.clearCache();
	}
	function _bindrod(wgt) {
		_bind0(wgt);
		if (!wgt.z_rod)
			wgt.z_rod = 9; //Bug 2948829: don't use true which is used by real ROD, such as combo-rod.js

		for (var child = wgt.firstChild; child; child = child.nextSibling)
			_bindrod(child);
	}
	function _unbindrod(wgt, nest) {
		_unbind0(wgt);

		if (!nest || wgt.z_rod === 9) { //Bug 2948829: don't delete value set by real ROD
			delete wgt.z_rod;

			for (var child = wgt.firstChild; child; child = child.nextSibling)
				_unbindrod(child, true);
		}
	}

	function _fixBindLevel(wgt, v) {
		wgt.bindLevel = v++;
		for (wgt = wgt.firstChild; wgt; wgt = wgt.nextSibling)
			_fixBindLevel(wgt, v);
	}

	function _addIdSpace(wgt) {
		if (wgt._fellows) wgt._fellows[wgt.id] = wgt;
		var p = wgt.parent;
		if (p) {
			p = p.$o();
			if (p) p._fellows[wgt.id] = wgt;
		}
	}
	function _rmIdSpace(wgt) {
		if (wgt._fellows) delete wgt._fellows[wgt.id];
		var p = wgt.parent;
		if (p) {
			p = p.$o();
			if (p) delete p._fellows[wgt.id];
		}
	}
	function _addIdSpaceDown(wgt) {
		var ow = wgt.parent;
		ow = ow ? ow.$o(): null;
		if (ow)
			_addIdSpaceDown0(wgt, ow);
	}
	function _addIdSpaceDown0(wgt, owner) {
		if (wgt.id) owner._fellows[wgt.id] = wgt;
		if (!wgt._fellows)
			for (wgt = wgt.firstChild; wgt; wgt = wgt.nextSibling)
				_addIdSpaceDown0(wgt, owner);
	}
	function _rmIdSpaceDown(wgt) {
		var ow = wgt.parent;
		ow = ow ? ow.$o(): null;
		if (ow)
			_rmIdSpaceDown0(wgt, ow);
	}
	function _rmIdSpaceDown0(wgt, owner) {
		if (wgt.id)
			delete owner._fellows[wgt.id];
		if (!wgt._fellows)
			for (wgt = wgt.firstChild; wgt; wgt = wgt.nextSibling)
				_rmIdSpaceDown0(wgt, owner);
	}
	//note: wgt.id must be checked before calling this method
	function _addGlobal(wgt) {
		var gs = _globals[wgt.id];
		if (gs)
			gs.push(wgt);
		else
			_globals[wgt.id] = [wgt];
	}
	function _rmGlobal(wgt) {
		var gs = _globals[wgt.id];
		if (gs) {
			gs.$remove(wgt);
			if (!gs.length) delete _globals[wgt.id];
		}
	}

	//check if a desktop exists
	function _exists(wgt) {
		if (document.getElementById(wgt.uuid)) //don't use $n() since it caches
			return true;

		for (wgt = wgt.firstChild; wgt; wgt = wgt.nextSibling)
			if (_exists(wgt))
				return  true;
	}

	function _fireClick(wgt, evt) {
		if (!wgt.shallIgnoreClick_(evt) && 
			!wgt.fireX(evt).stopped && evt.shallStop) {
			evt.stop();
			return false;	
		}
		return !evt.stopped;
	}

	function _rmDom(wgt, n) {
		//TO IMPROVE: actions_ always called if removeChild is called, while
		//insertBefore/appendChild don't (it is called only if attached by au)
		//NOT CONSISTENT! Better to improve in the future
		var act;
		if (wgt._visible && (act = wgt.actions_["hide"])) {
			wgt._rmAftAnm = function () {
				jq(n).remove();
			};
			n.style.visibility = ""; //Window (and maybe other) might turn it off
			act[0].call(wgt, n, act[1]);
		} else
			jq(n).remove();
	}

	//whether it is controlled by another dragControl
	//@param invoke whether to invoke dragControl
	function _dragCtl(wgt, invoke) {
		var p;
		return wgt && (p = wgt.parent) && p.dragControl && (!invoke || p.dragControl(wgt));
	}

	//backup current focus
	function _bkFocus(wgt) {
		var cf = zk.currentFocus;
		if (cf && zUtl.isAncestor(wgt, cf)) {
			zk.currentFocus = null;
			return {focus: cf, range: _bkRange(cf)};
		}
	}
	function _bkRange(wgt) {
		if (zk.ie && zk.cfrg) { //Bug ZK-1377
			var cfrg = zk.cfrg;
			delete zk.cfrg;
			return cfrg;
		}
		return wgt.getInputNode && (wgt = wgt.getInputNode())
			&& zk(wgt).getSelectionRange();
	}
	//restore focus
	function _rsFocus(cfi) {
		var cf;
		if (cfi && (cf = cfi.focus) && cf.desktop && !zk.currentFocus) {
			_ignCanActivate = true;
				//s.t., Window's rerender could gain focus back and receive onblur correctly
			try {
				cf.focus();
				if (cfi.range && cf.getInputNode && (cf = cf.getInputNode()))
					zk(cf).setSelectionRange(cfi.range[0], cfi.range[1]);
			} finally {
				_ignCanActivate = false;
			}
		}
	}
	
	function _listenFlex(wgt) {
		if (!wgt._flexListened){
			zWatch.listen({onSize: [wgt, zFlex.onSize], beforeSize: [wgt, zFlex.beforeSize]});
			if (wgt._hflex == 'min' || wgt._vflex == 'min')
				wgt.listenOnFitSize_();
			else
				wgt.unlistenOnFitSize_();
			wgt._flexListened = true;
		}
	}
	function _unlistenFlex(wgt) {
		if (wgt._flexListened) {
			zWatch.unlisten({onSize: [wgt, zFlex.onSize], beforeSize: [wgt, zFlex.beforeSize]});
			wgt.unlistenOnFitSize_();
			delete wgt._flexListened;
		}
	}
	
	/** @class zk.DnD
	 * Drag-and-drop utility.
	 * It is the low-level utility reserved for overriding for advanced customization.
	 */
	zk.DnD = { //for easy overriding
		/** Returns the widget to drop to.
		 * @param zk.Draggable drag the draggable controller
		 * @param Offset pt the mouse pointer's position.
		 * @param jq.Event evt the DOM event
		 * @return zk.Widget
		 */
		getDrop: function (drag, pt, evt) {
			var wgt = evt.target;
			return wgt ? wgt.getDrop_(drag.control): null;
		},
		/** Ghost the DOM element being dragged.
		 * @param zk.Draggable drag the draggable controller
		 * @param Offset ofs the offset of the returned element (left/top)
		 * @param String msg the message to show inside the returned element
		 * @return DOMElement the element representing what is being dragged
		 */
		ghost: function (drag, ofs, msg) {
			if (msg != null)  {
				jq(document.body).append(
					'<div id="zk_ddghost" class="z-drop-ghost z-drop-disallow" style="position:absolute;top:'
					+ofs[1]+'px;left:'+ofs[0]+'px;"><div class="z-drop-cnt"><span id="zk_ddghost-img" class="z-drop-disallow"></span>&nbsp;'+msg+'</div></div>');
				drag._dragImg = jq("#zk_ddghost-img")[0];
				return jq("#zk_ddghost")[0];
			}

			var dgelm = jq(drag.node).clone()[0];
			dgelm.id = "zk_ddghost";
			zk.copy(dgelm.style, {
				position: "absolute", left: ofs[0] + "px", top: ofs[1] + "px"
			});
			jq(dgelm).addClass("z-drag-ghost");
			document.body.appendChild(dgelm);
			return dgelm;
		}
	};
	function DD_cleanLastDrop(drag) {
		if (drag) {
			var drop;
			if (drop = drag._lastDrop) {
				drag._lastDrop = null;
				drop.dropEffect_();
			}
			drag._lastDropTo = null;
		}
	}
	function DD_pointer(evt, height) {
		if (zk.ios)
			return [evt.pageX - 50, evt.pageY - height - 30];
		return [evt.pageX + 7, evt.pageY + 5];
	}
	function DD_enddrag(drag, evt) {
		DD_cleanLastDrop(drag);
		var pt = [evt.pageX, evt.pageY],
			wgt = zk.DnD.getDrop(drag, pt, evt);
		if (wgt) wgt.onDrop_(drag, evt);
	}
	function DD_dragging(drag, pt, evt) {
		var dropTo;
		if (!evt || (dropTo = evt.domTarget) == drag._lastDropTo)
			return;

		var dropw = zk.DnD.getDrop(drag, pt, evt),
			found = dropw && dropw == drag._lastDrop;
		if (!found) {
			DD_cleanLastDrop(drag); //clean _lastDrop
			if (dropw) {
				drag._lastDrop = dropw;
				dropw.dropEffect_(true);
				found = true;
			}
		}

		var dragImg = drag._dragImg;
		if (dragImg) {
			if (found)
				jq(drag.node).removeClass('z-drop-disallow').addClass('z-drop-allow');
			else
				jq(drag.node).removeClass('z-drop-allow').addClass('z-drop-disallow');
			
			dragImg.className = found ? 'z-drop-allow': 'z-drop-disallow';
		}

		drag._lastDropTo = dropTo; //do it after _cleanLastDrop
	}
	function DD_ghosting(drag, ofs, evt) {
		return drag.control.cloneDrag_(drag, DD_pointer(evt, jq(drag.node).height()));
	}
	function DD_endghosting(drag, origin) {
		drag.control.uncloneDrag_(drag);
		drag._dragImg = null;
	}
	function DD_constraint(drag, pt, evt) {
		return DD_pointer(evt, jq(drag.node).height());
	}
	function DD_ignoredrag(drag, pt, evt) {
		//ZK 824:Textbox dragging issue with Listitem
		//since 5.0.11,6.0.0 introduce evt,drag to the wgt.ignoreDrag_() to provide more information. 
		return drag.control.ignoreDrag_(pt,evt,drag); 
	}

	function _topnode(n) {
		for (var v, body = document.body; n && n != body; n = n.parentNode) //no need to check vparentNode
			if ((v=n.style) && ((v=v.position) == 'absolute' || v == 'relative'))
				return n;
	}
	function _zIndex(n) {
		return n ? zk.parseInt(n.style.zIndex): 0;
	}

	function _getFirstNodeDown(wgt) {
		var n = wgt.$n();
		if (n) return n;
		for (var w = wgt.firstChild; w; w = w.nextSibling) {
			n = w.getFirstNode_();
			if (n) return n;
		}
	}
	//Returns if the specified widget's visibility depends the self widget.
	function _floatVisibleDependent(self, wgt) {
		for (; wgt; wgt = wgt.parent)
			if (wgt == self) return true;
			else if (!wgt.isVisible()) break;
		return false;
	}
	
	function _fullScreenZIndex(zi) {
		var pseudoFullscreen = null;
		if (document.fullscreenElement) {
			pseudoFullscreen = ":fullscreen";
		} else if (document.mozFullScreen) {
			//pseudoFullscreen = ":-moz-full-screen";
			//Firefox return zindex by scientific notation "2.14748e+9"
			//use zk.parseFloat() will get 2147480000, so return magic number directly.
			return 2147483648;
		} else if (document.webkitIsFullScreen) {
			pseudoFullscreen = ":-webkit-full-screen";
		}
		if (pseudoFullscreen) {
			var fsZI = jq.css(jq(pseudoFullscreen)[0],"zIndex");
			return fsZI == "auto" ? 2147483648 : ++fsZI;
		}
		return zi;
	}

	//Returns the topmost z-index for this widget
	function _topZIndex(wgt) {
		var zi = 1800; // we have to start from 1800 depended on all the css files.
		
		//ZK-1226: Full Screen API will make element's ZIndex bigger than 1800
		//	so set a higher zindex if browser is in full screen mode.
		zi = _fullScreenZIndex(zi);
		//
		
		for (var j = _floatings.length; j--;) {
			var w = _floatings[j].widget,
				wzi = zk.parseInt(w.getFloatZIndex_(_floatings[j].node));
			if (wzi >= zi && !zUtl.isAncestor(wgt, w) && w.isVisible())
				zi = wzi + 1;
		}
		return zi;
	}

	function _prepareRemove(wgt, ary) {
		for (wgt = wgt.firstChild; wgt; wgt = wgt.nextSibling) {
			var n = wgt.$n();
			if (n) ary.push(n);
			else _prepareRemove(wgt, ary);
		}
	}

	//render the render defer (usually controlled by server)
	function _doDeferRender(wgt) {
		if (wgt._z$rd) { //might be redrawn by forcerender
			delete wgt._z$rd;
			wgt._norenderdefer = true;
			wgt.replaceHTML('#' + wgt.uuid, wgt.parent ? wgt.parent.desktop: null, null, true);
			if (wgt.parent)
				wgt.parent.onChildRenderDefer_(wgt);
		}
	}

	//invoke rerender later
	function _rerender(wgt, timeout) {
		if (_rdtid)
			clearTimeout(_rdtid);
		_rdque.push(wgt);
		_rdtid = setTimeout(_rerender0, timeout);
	}
	function _rerender0() {
		_rdtid = null;
		l_out:
		for (var wgt; wgt = _rdque.shift();) {
			if (!wgt.desktop)
				continue;

			for (var j = _rdque.length; j--;)
				if (zUtl.isAncestor(wgt, _rdque[j]))
					_rdque.splice(j, 1); //skip _rdque[j]
				else if (zUtl.isAncestor(_rdque[j], wgt))
					continue l_out; //skip wgt

			wgt.rerender(-1);
		}
	}
	function _rerenderDone(wgt, skipper /* Bug ZK-1463 */) {
		for (var j = _rdque.length; j--;)
			if (zUtl.isAncestor(wgt, _rdque[j])) {
				if (!skipper || !skipper.skipped(wgt, _rdque[j]))
					_rdque.splice(j, 1);
			}
	}

	function _markCache(cache, visited, visible) {
		if (cache)
			for (var p; p = visited.pop();)
				cache[p.uuid] = visible;
		return visible;
	}
	var _dragoptions = {
		starteffect: zk.$void, //see bug #1886342
		endeffect: DD_enddrag, change: DD_dragging,
		ghosting: DD_ghosting, endghosting: DD_endghosting,
		constraint: DD_constraint, //s.t. cursor won't be overlapped with ghosting
		ignoredrag: DD_ignoredrag,
		zIndex: 88800
	};

var Widget =
/** A widget, i.e., an UI object.
 * Each component running at the server is associated with a widget
 * running at the client.
 * <p>Refer to <a href="http://books.zkoss.org/wiki/ZK_Component_Development_Essentials">ZK Component Development Essentials</a>
 * and <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference">ZK Client-side Reference</a>
 * for more information.
 * <p>Notice that, unlike the component at the server, {@link zk.Desktop}
 * and {@link zk.Page} are derived from zk.Widget. It means desktops, pages and widgets are in a widget tree. 
 * @disable(zkgwt)
 */
zk.Widget = zk.$extends(zk.Object, {
	_visible: true,
	/** The number of children (readonly).
	 * @type int
	 */
	nChildren: 0,
	/** The bind level (readonly)
	 * The level in the widget tree after this widget is bound to a DOM tree ({@link #bind_}).
	 * For example, a widget's bind level is one plus the parent widget's
	 * <p>It starts at 0 if it is the root of the widget tree (a desktop, zk.Desktop), then 1 if a child of the root widget, and son on. Notice that it is -1 if not bound.
	 * <p>It is mainly useful if you want to maintain a list that parent widgets is in front of (or after) child widgets. 
	 * bind level.
	 * @type int
	 */
	bindLevel: -1,
	_mold: 'default',
	/** The class name of the widget.
	 * For example, zk.Widget's class name is "zk.Widget", while
	 * zul.wnd.Window's "zul.wnd.Window".
	 * <p>Notice that it is available if a widget class is loaded by WPD loader (i.e., specified in zk.wpd). If you create a widget class dynamically,
	 * you have to invoke {@link #register} to make this member available. 
	 * On the other hand, {@link zk.Object#$class} is available for all objects
	 * extending from {@link zk.Object}.
	 * 
	 * @see #widgetName
	 * @type String
	 */
	className: 'zk.Widget',
	/** The widget name of the widget.
	 * It is the same as <code>this.className.substring(this.className.lastIndexOf('.') + 1).toLowerCase()</code>.
	 * For example, if {@link #className} is zul.wnd.Window, then
	 * {@link #widgetName} is window.
	 * <p>Notice that {@link #className} is unique while {@link #widgetName}
	 * is not necessary unique.
	 * @see #className
	 * @type String
	 * @since 5.0.2
	 */
	widgetName: "widget",
	/** The AU tag of this widget.
	 * The AU tag tag is used to tag the AU requests sent by the peer widget.
	 * For instance, if the AU tag is <code>xxx,yyy</code> and the desktop's
	 * request path ({@link Desktop#requestPath}) is <code>/foo.zul</code>, then
	 * the URL of the AU request will contain <code>/_/foo.zul/xxx,yyy</code>,.
	 * <p>Default: null.
	 * @type String
	 * @since 6.0.0
	 */
	//autag: null,

	//a map of actions. Notice: it is initialized as a shared empty map
	//setAction shall replace it with another map
	actions_: {}, //yes, it is shared

	_floating: false,

	/** The first child, or null if no child at all (readonly).
	 * @see #getChildAt
	 * @type zk.Widget
	 */
	//firstChild: null,
	/** The last child, or null if no child at all (readonly).
	 * @see #getChildAt
	 * @type zk.Widget
	 */
	//lastChild: null,
	/** The parent, or null if this widget has no parent (readonly).
	 * @type zk.Widget
	 */
	//parent: null,
	/** The next sibling, or null if this widget is the last child (readonly).
	 * @type zk.Widget
	 */
	//nextSibling: null,
	/** The previous sibling, or null if this widget is the first child (readonly).
	 * @type zk.Widget
	 */
	//previousSibling: null,
	/** The desktop that this widget belongs to (readonly).
	 * It is set when it is bound to the DOM tree.
	 * <p>Notice it is always non-null if bound to the DOM tree, while
	 * {@link #$n()} is always non-null if bound. For example, {@link zul.utl.Timer}.
	 * <p>It is readonly, and set automcatically when {@link #bind_} is called. 
	 * @type zk.Desktop
	 */
	//desktop: null,
	/** The identifier of this widget, or null if not assigned (readonly).
	 * It is the same as {@link #getId}.
	 * <p>To change the value, use {@link #setId}.
	 * @type String the ID
	 */
	//id: null,
	/** Whether this widget has a peer component (readonly).
	 * It is set if a widget is created automatically to represent a component
	 ( at the server. On the other hand, it is false if a widget is created
	 * by the client application (by calling, say, <code>new zul.inp.Textox()</code>). 
	 * @type boolean
	 */
	//inServer: false,
	/** The UUID. Don't change it if it is bound to the DOM tree, or {@link #inServer} is true.
	 * Developers rarely need to modify it since it is generated automatically. 
	 * @type String
	 */
	//uuid: null,
	/** Indicates an invocation of {@link #appendChild} is made by
	 * {@link #insertBefore}.
	 * @type boolean
	 */
	//insertingBefore_: false,

	/** A map of objects that are associated with this widget, and
	 * they shall be removed when this widget is unbound ({@link #unbind}).
	 * <p>The key must be an unique name of the object, while the value
	 * must be an object that implement the destroy method.
	 * <p>When {@link #unbind_} is called, <code>destroy()</code> is
	 * called for each object stored in this map. Furthermore,
	 * if the visibility of this widget is changed, and the object implements
	 * the sync method, then <code>sync()</code> will be called.
	 * Notice that the sync method is optional. It is ignored if not implemented.
	 * <p>It is useful if you implement an effect, such as shadow, mask
	 * and error message, that is tightly associated with a widget.
	 * @type Map
	 */
	//effects_: null;

	/** The weave controller that is used by ZK Weaver.
	 * It is not null if it is created and controlled by ZK Weaver.
	 * In other words, it is called in the Design Mode if $weave is not null.
	 * @type Object
	 */
	//$weave: null,

	/** The constructor.
	 * For example,
<pre><code>
new zul.wnd.Window({
  border: 'normal',
  title: 'Hello World',
  closable: true
});
</code></pre>
	 * @param Map props the properties to be assigned to this widget.
	 */
	$init: function (props) {
		this._asaps = {}; //event listened at server
		this._lsns = {}; //listeners(evtnm,listener)
		this._bklsns = {}; //backup for listners by setListeners
		this._subnodes = {}; //store sub nodes for widget(domId, domNode)
		this.effects_ = {};

		//There are two ways to specify IdSpace at client
		//1) Override $init and assign _fellows (e.g., Macro/Include/Window)
		//2) Assign this.z$is to true (used by AbstractComponent.java)
		if (this.z$is)
			this._fellows = {};

		//zkac is a token used by create() in mount.js for optimizing performance
		if (props !== zkac)
			this.afterInit(function () {
				//if props.$oid, it must be an object other than {} so ignore
				if (props && typeof props == 'object' && !props.$oid)
					for (var nm in props)
						this.set(nm, props[nm]);

				if ((zk.spaceless || this.rawId) && this.id)
					this.uuid = this.id; //setId was called
				if (!this.uuid)
					this.uuid = Widget.nextUuid();
			});
	},

	$define: {
		/** Sets this widget's mold. A mold is a template to render a widget.
		 * In other words, a mold represents a visual presentation of a widget. Depending on implementation, a widget can have multiple molds.
		 * <p>Default: <code>default</code>
		 * @param String mold the mold
		 * @return zk.Widget this widget
		 */
		/** Returns this widget's mold. A mold is a template to render a widget.
		 * In other words, a mold represents a visual presentation of a widget. Depending on implementation, a widget can have multiple molds.
		 * @return String
		 */
		mold: function () {
			this.rerender();
		},
		/** Sets the CSS style of this widget.
		 * <p>Default: null
		 * @param String style the CSS style
		 * @return zk.Widget this widget
		 * @see #getStyle
		 * @see #setSclass
		 * @see #setZclass
		 */
		/** Returns the CSS style of this widget
		 * @return String
		 * @see #setStyle
		 * @see #getSclass
		 * @see #getZclass
		 */
		style: function () {
			this.updateDomStyle_();
		},
		/** Sets the CSS class of this widget.
		 *<p>Default: null. 
		 *<p>The default styles of ZK components doesn't depend on sclass at all. Rather, setSclass is provided to perform small adjustment, e.g., changing only the font size. In other words, the default style is still applied if you change sclass.
		 *<p>To replace the default style completely, use {@link #setZclass} instead.
		 *<p>The real CSS class is a concatenation of {@link #getZclass} and {@link #getSclass}.
		 * @param String sclass the style class
		 * @return zk.Widget this widget
		 * @see #getSclass
		 * @see #setZclass
		 * @see #setStyle
		 */
		/** Returns the CSS class of this widget.
		 * @return String
		 * @see #setSclass
		 * @see #getZclass
		 * @see #getStyle
		 */
		sclass: function () {
			this.updateDomClass_();
		},
		/** Sets the ZK Cascading Style class(es) for this widget. It is the CSS class used to implement a mold of this widget. n implementation It usually depends on the implementation of the mold ({@link #getMold}).
		 * <p>Default: null but an implementation usually provides a default class, such as z-button.
		 * <p>Calling setZclass with a different value will completely replace the default style of a widget.
		 * Once you change it, all default styles are gone.
		 * If you want to perform small adjustments, use {@link #setSclass} instead.
		 * <p>The real CSS class is a concatenation of {@link #getZclass} and
		 * {@link #getSclass}. 
		 * @param String zclass the style class used to apply the whote widget.
		 * @return zk.Widget this widget
		 * @see #getZclass
		 * @see #setSclass
		 * @see #setStyle
		 */
		/** Returns the ZK Cascading Style class(es) for this widget.
		 * @return String
		 * @see #setZclass
		 * @see #getSclass
		 * @see #getStyle
		 */
		zclass: function (){
			this.rerender();
		},
		/** Sets the width of this widget.
		 * @param String width the width. Remember to specify 'px', 'pt' or '%'. 
		 * An empty or null value means "auto"
		 * @return zk.Widget this widget
		 */
		/** Returns the width of this widget.
		 * @return String
		 * @see #getHeight
		 */
		width: function (v) {
			if (!this._nhflex) {
				var n = this.$n();
				if (n) n.style.width = v || '';
			}
		},
		/** Sets the height of this widget.
		 * @param String height the height. Remember to specify 'px', 'pt' or '%'. 
		 * An empty or null value means "auto"
		 * @return zk.Widget this widget
		 */
		/** Returns the height of this widget.
		 * @return String
		 * @see #getWidth
		 */
		height: function (v) {
			if (!this._nvflex) {
				var n = this.$n();
				if (n) n.style.height = v || '';
			}
		},
		/** Sets the left of this widget.
		 * @param String left the left. Remember to specify 'px', 'pt' or '%'. 
		 * An empty or null value means "auto"
		 * @return zk.Widget this widget
		 */
		/** Returns the left of this widget.
		 * @return String
		 * @see #getTop
		 */
		left: function (v) {
			var n = this.$n();
			if (n) n.style.left = v || '';
		},
		/** Sets the top of this widget.
		 * If you want to specify <code>bottom</code>, use {@link #setStyle} instead.
		 * For example, <code>setStyle("bottom: 0px");</code>
		 * @param String top the top. Remember to specify 'px', 'pt' or '%'. 
		 * An empty or null value means "auto"
		 * @return zk.Widget this widget
		 */
		/** Returns the top of this widget.
		 * @return String
		 * @see #getLeft
		 */
		top: function (v) {
			var n = this.$n();
			if (n) n.style.top = v || '';
		},
		/** Sets the tooltip text of this widget.
		 * <p>Default implementation of setTooltiptext: update the title attribute of {@link #$n}
		 * @param String title the tooltip text
		 * @return zk.Widget this widget
		 */
		/** Returns the tooltip text of this widget.
		 * @return String
		 */
		tooltiptext: function (v) {
			var n = this.$n();
			// ZK-676 , ZK-752
			if (n) n.title = v || '';
		},

		/** Sets the identifier, or a list of identifiers of a droppable type for this widget.
		 * <p>Default: null
		 * <p>The simplest way to make a component droppable is to set this attribute to "true". To disable it, set this to "false" (or null).
		 * <p>If there are several types of draggable objects and this widget accepts only some of them, you could assign a list of identifiers that this widget accepts, separated by comma.
		 * <p>For example, if this component accpets dg1 and dg2, then assign "dg1, dg2" to this attribute. 
		 * @param String droppable "false", null or "" to denote not-droppable; "true" for accepting any draggable types; a list of identifiers, separated by comma for identifiers of draggables this widget accept (to be dropped in).
		 * @return zk.Widget this widget
		 */
		/** Returns the identifier, or a list of identifiers of a droppable type for this widget, or null if not droppable.
		 * @return String
		 */
		droppable: [
			function (v) {
				return v && "false" != v ? v: null;
			},
			function (v) {
				var dropTypes;
				if (v && v != "true") {
					dropTypes = v.split(',');
					for (var j = dropTypes.length; j--;)
						if (!(dropTypes[j] = dropTypes[j].trim()))
							dropTypes.splice(j, 1);
				}
				this._dropTypes = dropTypes;
			}
		],
		/**
		 * Returns vertical flex hint of this widget.
		 * @see #setVflex 
		 * @return String vertical flex hint of this widget.
		 */
		/**
		 * Sets vertical flexibility hint of this widget. 
		 * <p>The parameter flex is a number in String type indicating how this 
		 * widget's parent container distributes remaining empty space among 
		 * its children widget vertically. Flexible 
		 * widget grow and shrink to fit their given space. Flexible widget with 
		 * larger flex values will be made larger than widget with lower flex 
		 * values, at the ratio determined by all flexible widgets. The actual 
		 * flex value is not relevant unless there are other flexible widget within 
		 * the same parent container. Once the default sizes of widget in a 
		 * parent container are calculated, the remaining space in the parent 
		 * container is divided among the flexible widgets, according to their 
		 * flex ratios.</p>
		 * <p>Specify a flex value of negative value, 0, or "false" has the 
		 * same effect as leaving the flex attribute out entirely. 
		 * Specify a flex value of "true" has the same effect as a flex value of 1.</p>
		 * <p>Special flex hint, <b>"min"</b>, indicates that the minimum space shall be
		 * given to this flexible widget to enclose all of its children widgets.
		 * That is, the flexible widget grow and shrink to fit its children widgets.</p> 
		 * 
		 * @see #setHflex
		 * @see #getVflex 
		 * @param String flex the vertical flex hint.
		 */
		vflex: function(v) {
			this._nvflex = (true === v || 'true' == v) ? 1 : v == 'min' ? -65500 : zk.parseInt(v);
			if (this._nvflex < 0 && v != 'min')
				this._nvflex = 0;
			if (this.desktop) { //if already bind
				if (!this._nvflex) {
					this.setFlexSize_({height: ''}); //clear the height
					delete this._vflexsz;
					if (!this._nhflex)
						_unlistenFlex(this);
				} else
					_listenFlex(this);

				var p;
				if ((p = this.parent) && !p.isBinding()) //ZK-307
					zUtl.fireSized(p, -1); //no beforeSize
			}
		},
		/**
		 * Sets horizontal flexibility hint of this widget. 
		 * <p>The parameter flex is a number in String type indicating how this 
		 * widget's parent container distributes remaining empty space among 
		 * its children widget horizontally. Flexible 
		 * widget grow and shrink to fit their given space. Flexible widget with 
		 * larger flex values will be made larger than widget with lower flex 
		 * values, at the ratio determined by all flexible widgets. The actual 
		 * flex value is not relevant unless there are other flexible widget 
		 * within the same parent container. Once the default sizes of widget 
		 * in a parent container are calculated, the remaining space in the parent 
		 * container is divided among the flexible widgets, according to their 
		 * flex ratios.</p>
		 * <p>Specify a flex value of negative value, 0, or "false" has the 
		 * same effect as leaving this flex attribute out entirely. 
		 * Specify a flex value of "true" has the same effect as a flex value of 1.</p>
		 * <p>Special flex hint, <b>"min"</b>, indicates that the minimum space shall be
		 * given to this flexible widget to enclose all of its children widgets.
		 * That is, the flexible widget grow and shrink to fit its children widgets.</p> 
		 * 
		 * @param String flex the horizontal flex hint.
		 * @see #setVflex
		 * @see #getHflex 
		 */
		/**
		 * Return horizontal flex hint of this widget.
		 * @return String horizontal flex hint of this widget.
		 * @see #setHflex 
		 */
		hflex: function(v) {
			this.setHflex_(v);

			var p;
			if (this.desktop/*if already bind*/
			&& (p = this.parent) && !p.isBinding()/*ZK-307*/)
				zUtl.fireSized(p, -1); //no beforeSize
		},
		/** Returns the number of milliseconds before rendering this component
		 * at the client.
		 * <p>Default: -1 (don't wait).
		 * @return int the number of milliseconds to wait
		 * @since 5.0.2
		 */
		/** Sets the number of milliseconds before rendering this component
		 * at the client.
		 * <p>Default: -1 (don't wait).
		 *
		 * <p>This method is useful if you have a sophiscated page that takes
		 * long to render at a slow client. You can specify a non-negative value
		 * as the render-defer delay such that the other part of the UI can appear
		 * earlier. The styling of the render-deferred widget is controlled by
		 * a CSS class called <code>z-render-defer</code>.
		 *
		 * <p>Notice that it has no effect if the component has been rendered
		 * at the client.
		 * @param int ms time to wait in milliseconds before rendering.
		 * Notice: 0 also implies deferring the rendering (just right after
		 * all others are renderred).
		 * @since 5.0.2
		 */
		 renderdefer: null,
		/** Returns the client-side action.
		 * @return String the client-side action
		 * @since 5.0.6
		 */
		/** Sets the client-side action.
		 * <p>Default: null (no CSA at all)
		 * <p>The format: <br>
		 * <code>action1: action-effect1; action2: action-effect2</code><br/>
		 *
		 * <p>Currently, only two actions are <code>show</code> and <code>hide</code>.
		 * They are called when the widget is becoming visible (show) and invisible (hide).
		 * <p>The action effect (<code>action-effect1</code>) is the name of a method
		 * defined in <a href="http://www.zkoss.org/javadoc/latest/jsdoc/zk/eff/Actions.html">zk.eff.Actions</a>,
		 * such as
		 * <code>show: slideDown; hide: slideUp</code>
		 * @param String action the cient-side action
		 * @since 5.0.6
		 */
		action: function (v) {
			this.actions_ = {}; //reset it since it might be the shared one
			if (v)
				for (var ps = v.split(';'), j = ps.length; j--;) {
					var p = ps[j], k = p.indexOf(':');
					if (k >= 0) {
						var nm = p.substring(0, k).trim(),
							val = p.substring(k + 1).trim(),
							opts, fn, l;
						if (nm && val) {
							k = val.indexOf('(');
							if (k >= 0) {
								if ((l = val.lastIndexOf(')')) > k)
									opts = jq.evalJSON(val.substring(k + 1, l));
								val = val.substring(0, k);
							}
							if (fn = zk.eff.Actions[val])
								this.actions_[nm] = [fn, opts];
							else
								zk.error("Unknown action: "+val);
							continue;
						}
					}
					zk.error("Illegal action: "+v+", "+this.className);
				}
		}
	},
	setHflex_: function (v) {
		this._nhflex = (true === v || 'true' == v) ? 1 : v == 'min' ? -65500 : zk.parseInt(v);
		if (this._nhflex < 0 && v != 'min')
			this._nhflex = 0;
		if (this.desktop) { //ZK-1784 only update the components style when it is attached to desktop
		                    //checking on (_binds[this.uuid] === this) as before does not work when 
		                    //nested inside native component. in this case the nested component
		                    //is bound earlier, when the native component is reused (mount.js create()) 
			if (!this._nhflex) {
				this.setFlexSize_({width: ''}); //clear the width
				delete this._hflexsz;
				if (!this._nvflex)
					_unlistenFlex(this);
			} else
				_listenFlex(this);
		}
	},
	/** Invoked after an animation (e.g., {@link jqzk#slideDown}) has finished.
	 * You could override to clean up anything related to animation.
	 * Notice that, if you override, you have to call back this method.
	 * @param boolean visible whether the result of the animation will make
	 * the DOM element visible
	 * @since 5.0.6
	 */
	afterAnima_: function (visible) {
		var fn;
		if (fn = this._rmAftAnm) {
			this._rmAftAnm = null;
			fn();
		}
	},

	/** Sets the identifier of a draggable type for this widget.
	 * <p>Default: null
	 * <p>The simplest way to make a widget draggable is to set this property to "true". To disable it, set this to "false" (or null).
	 * If there are several types of draggable objects, you could assign an identifier for each type of draggable object.
	 * The identifier could be anything but empty and "false". 
	 * @param String draggable "false", "" or null to denote non-draggable; "true" for draggable with anonymous identifier; others for an identifier of draggable. 
	 * @return zk.Widget this widget
	 */
	setDraggable: function (v) {
		if (!v && v != null) v = "false"; //null means default
		this._draggable = v;

		if (this.desktop && !_dragCtl(this, true))
			if (v && v != "false") this.initDrag_();
			else this.cleanDrag_();
	},
	/** Returns the identifier of a draggable type for this widget, or null if not draggable.
	 * @return String
	 */
	getDraggable: function () {
		var v = this._draggable;
		return v ? v: _dragCtl(this) ? "true": "false";
	},
	/** Returns the owner of the ID space that this widget belongs to,
	 * or null if it doesn't belong to any ID space.
	 * <p>Notice that, if this widget is an ID space owner, this method
	 * returns itself.
	 * @return zk.Widget
	 */
	$o: function () {
		for (var w = this; w; w = w.parent)
			if (w._fellows) return w;
	},
	/** Returns the map of all fellows of this widget.
	 * <pre><code>
wgt.$f().main.setTitle("foo");
</code></pre>
	 * @return Map the map of all fellows.
	 * @since 5.0.2
	 */
	/** Returns the fellow of the specified ID of the ID space that this widget belongs to. It returns null if not found. 
	 * @param String id the widget's ID ({@link #id})
	 * @return zk.Widget
	 */
	/** Returns the fellow of the specified ID of the ID space that this widget belongs to. It returns null if not found. 
	 * @param String id the widget's ID ({@link #id})
	 * @param boolean global whether to search all ID spaces of this desktop.
	 * If true, it first search its own ID space, and then the other Id spaces in this browser window (might have one or multiple desktops). 
	 * If omitted, it won't search all ID spaces.
	 * @return zk.Widget
	 */
	$f: function (id, global) {
		var f = this.$o();
		if (!arguments.length)
			return f ? f._fellows: {};
		for (var ids = id.split('/'), j = 0, len = ids.length; j < len; ++j) {
			id = ids[j];
			if (id) {
				if (f) f = f._fellows[id];
				if (!f && global && (f=_globals[id])) f = f[0];
				if (!f || zk.spaceless) break;
				global = false;
			}
		}
		return f;
	},
	/** Returns the identifier of this widget, or null if not assigned.
	 * It is the same as {@link #id}.
	 * @return String the ID
	 */
	getId: function () {
		return this.id;
	},
	/** Sets the identifier of this widget.
	 * @param String id the identifier to assigned to.
	 * @return zk.Widget this widget
	 */
	setId: function (id) {
		if (id != this.id) {
			if (this.id) {
				_rmIdSpace(this);
				_rmGlobal(this); //no need to check this.desktop
			}

			if (id && (zk.spaceless || this.rawId))
				zk._wgtutl.setUuid(this, id);
			this.id = id;

			if (id) {
				_addIdSpace(this);
				if (this.desktop || this.z_rod)
					_addGlobal(this);
			}
		}
		return this;
	},

	/** Sets a property.
	 * The property updates sent from the server, including
	 * renderProperties and smartUpdate, will invoke this method.
	 * <h2>Special Names</h2>
	 * <h3>onXxx</h3>
	 * <p>If the name starts with <code>on</code>, it is assumed to be
	 * an event listener and {@link #setListener} will be called.
	 *
	 * <h3>$onXxx</h3>
	 * <p>If the name starts with <code>$on</code>, the value is assumed to
	 * be a boolean indicating if the server registers a listener.
	 *
	 * <h3>$$onXxx</h3>
	 * <p>If the name starts with <code>$$on</code>, it indicates
	 * the event is an important event that the client must send it
	 * back to the server. In additions, the value is assumed to
	 * be a boolean indicating if the server registers a listener.
	 *
	 * <h2>Special Value</h2>
	 * <h3>{$u: uuid}</h3>
	 * <p>If the value is in this format, it indicates <code>$u</code>'s
	 * value is UUID of a widget, and it will be resolved to a widget
	 * before calling the real method.
	 * <p>However, since we cannot resolve a widget by its UUID until
	 * the widget is bound (to DOM). Thus, ZK sets property after mounted.
	 * For example, <code>wgt.set("radiogroup", {$u: uuid})</code> is equivalent
	 * to the following.
	 * <pre><code>zk.afterMount(function () {
	 wgt.set("radiogroup", zk.Widget.$(uuid))
	 *});</code></pre>
	 *
	 * @param String name the name of property.
	 * @param Object value the value
	 * @return zk.Widget this widget
	 */
	/** Sets a property.
	 * The property updates sent from the server, including
	 * renderProperties and smartUpdate, will invoke this method.
	 * @param String name the name of property.
	 * Refer to {@link #set(String, Object)} for special names.
	 * @param Object value the value
	 * @param Object extra the extra argument. It could be anything.
	 * @return zk.Widget this widget
	 */
	set: function (name, value, extra) {
		var cc;
		if ((cc = value && value.$u) //value.$u is UUID
		&& !(value = Widget.$(cc))) { //not created yet
			var self = this;
			zk.afterMount(function () {
				var v = Widget.$(cc);
				// ZK-1069: may not be ready even in afterMount
				if (v)
					zk._set(self, name, v, extra);
				else
					setTimeout(function () {
						zk._set(self, name, Widget.$(cc), extra);
					});
			}, -1);
			return this;
		}

		if (cc = this['set' + name.charAt(0).toUpperCase() + name.substring(1)]) {
		//to optimize the performance we check the method first (most common)
			zk._set2(this, cc, null, value, extra);
			return this;
		}

		if ((cc = name.charAt(0)) == '$') {
			if (name.startsWith('$$on')) {
				var cls = this.$class,
					ime = cls._importantEvts;
				(ime || (cls._importantEvts = {}))[name.substring(2)] = value;
				return this;
			} else if (name.startsWith('$on')) {
				this._asaps[name.substring(1)] = value;
				return this;
			}
		} else if (cc == 'o' && name.charAt(1) == 'n'
			&& ((cc = name.charAt(2)) <= 'Z' && cc >= 'A')) {
			this.setListener(name, value);
			return this;
		}

		zk._set2(this, null, name, value, extra);
		return this;
	},
	/** Retrieves a value from the specified property.
	 * @param String name the name of property.
	 * @return Object the value of the property
	 * @since 5.0.2
	 */
	get: function (name) {
		return zk.get(this, name);
	},
	/** Return the child widget at the specified index.
	 * <p>Notice this method is not good if there are a lot of children
	 * since it iterates all children one by one.
	 * @param int j the index of the child widget to return. 0 means the first
	 * child, 1 for the second and so on.
	 * @return zk.Widget the widget or null if no such index
	 * @see #getChildIndex
	 */
	getChildAt: function (j) {
		if (j >= 0 && j < this.nChildren)
			for (var w = this.firstChild; w; w = w.nextSibling)
				if (--j < 0)
					return w;
	},
	/** Returns the child index of this widget.
	 * By child index we mean the order of the child list of the parent. For example, if this widget is the parent's first child, then 0 is returned. 
	 * <p>Notice that {@link #getChildAt} is called against the parent, while
	 * this method called against the child. In other words,
	 * <code>w.parent.getChildAt(w.getChildIndex())</code> returns <code>w</code>.
	 * <p>Notice this method is not good if there are a lot of children
	 * since it iterates all children one by one.
	 * @return int the child index
	 */
	getChildIndex: function () {
		var w = this.parent, j = 0;
		if (w)
			for (w = w.firstChild; w; w = w.nextSibling, ++j)
				if (w == this)
					return j;
		return 0;
	},
	/** Appends an array of children.
	 * Notice this method does NOT remove any existent child widget.
	 * @param Array children an array of children ({@link zk.Widget}) to add
	 * @return zk.Widget this widget
	 */
	setChildren: function (children) {
		if (children)
			for (var j = 0, l = children.length; j < l;)
				this.appendChild(children[j++]);
		return this;
	},
	/** Append a child widget.
	 * The child widget will be attached to the DOM tree automatically,
	 * if this widget has been attached to the DOM tree,
	 * unless this widget is {@link zk.Desktop}.
	 * In other words, you have to attach child widgets of {@link zk.Desktop}
	 * manually (by use of, say, {@link #replaceHTML}).
	 *
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>If this widget is bound to the DOM tree, this method invoke {@link #insertChildHTML_}
	 * to insert the DOM content of the child to the DOM tree.
	 * Thus, override {@link #insertChildHTML_} if you want to insert more than
	 * the DOM content generated by {@link #redraw}.</li>
	 * <li>If a widget wants to do something when the parent is changed, overrides {@link #beforeParentChanged_} 
	 * (which is called by {@link #insertBefore}, {@link #removeChild} and {@link #appendChild}).</li>
	 * <li>{@link #insertBefore} might invoke this method (if the widget shall be the last child).
	 * To know if it is the case you can check {@link #insertingBefore_}.</li>
	 * </ul>
	 * @param zk.Widget child the child widget to add
	 * @return boolean whether the widget was added successfully. It returns false if the child is always the last child ({@link #lastChild}).
	 * @see #insertBefore
	 */
	/** Append a child widget with more control.
	 * It is similar to {@link #appendChild(zk.Widget)} except the caller
	 * could prevent it from generating DOM element.
	 * It is usually used with {@link #rerender}.
	 * @param zk.Widget child the child widget to add
	 * @param boolean ignoreDom whether not to generate DOM elements
	 * @return boolean whether the widget was added successfully. It returns false if the child is always the last child ({@link #lastChild}).
	 * @see #appendChild(zk.Widget)
	 * @see #insertBefore
	 */
	appendChild: function (child, ignoreDom) {
		if (child == this.lastChild)
			return false;

		var oldpt;
		if ((oldpt = child.parent) != this)
			child.beforeParentChanged_(this);

		if (oldpt) {
			_noParentCallback = true;
			try {
				oldpt.removeChild(child);
			} finally {
				_noParentCallback = false;
			}
		}

		child.parent = this;
		var ref = this.lastChild;
		if (ref) {
			ref.nextSibling = child;
			child.previousSibling = ref;
			this.lastChild = child;
		} else {
			this.firstChild = this.lastChild = child;
		}
		++this.nChildren;

		if (child.id || child.firstChild) //optimize for mount.js's create()
			_addIdSpaceDown(child);

		if (!ignoreDom)
			if (this.shallChildROD_(child))
				_bindrod(child);
			else {
				var dt = this.desktop;
				if (dt) this.insertChildHTML_(child, null, dt);
			}

		child.afterParentChanged_(oldpt);
		if (!_noChildCallback)
			this.onChildAdded_(child);
		return true;
	},
	/** Returns whether a new child shall be ROD.
	 * <p>Default: return true if child.z_rod or this.z_rod
	 * @return boolean whether a new child shall be ROD.
	 * @since 5.0.1
	 */
	shallChildROD_: function (child) {
		return child.z_rod || this.z_rod;
	},
	/** Inserts a child widget before the reference widget (the <code>sibling</code> argument).
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>If this widget is bound to the DOM tree, this method invoke {@link #insertChildHTML_}
	 * to insert the DOM content of the child to the DOM tree. Thus, override {@link #insertChildHTML_}
	 * if you want to insert more than the DOM content generated by {@link #redraw}.</li>
	 * <li>If a widget wants to do something when the parent is changed,
	 * overrides {@link #beforeParentChanged_} (which is called by
	 * {@link #insertBefore}, {@link #removeChild} and {@link #appendChild}). 
	 *
	 * @param zk.Widget child the child widget
	 * @param zk.Widget sibling the sibling widget (the 'insert' point where
	 * the new widget will be placed before). If null or omitted, it is
	 * the same as {@link #appendChild}
	 * @return boolean whether the widget was added successfully. It returns false if the child is always the last child ({@link #lastChild}). 
	 * @see #appendChild(zk.Widget)
	 */
	insertBefore: function (child, sibling, ignoreDom) {
		if (!sibling || sibling.parent != this) {
			this.insertingBefore_ = true;
			try {
				return this.appendChild(child, ignoreDom);
			} finally {
				this.insertingBefore_ = false;
			}
		}

		if (child == sibling || child.nextSibling == sibling)
			return false;

		var oldpt;
		if ((oldpt = child.parent) != this)
			child.beforeParentChanged_(this);

		if (oldpt) {
			_noParentCallback = true;
			try {
				oldpt.removeChild(child);
			} finally {
				_noParentCallback = false;
			}
		}

		child.parent = this;
		var ref = sibling.previousSibling;
		if (ref) {
			child.previousSibling = ref;
			ref.nextSibling = child;
		} else this.firstChild = child;

		sibling.previousSibling = child;
		child.nextSibling = sibling;

		++this.nChildren;

		_addIdSpaceDown(child);

		if (!ignoreDom)
			if (this.shallChildROD_(child))
				_bindrod(child);
			else {
				var dt = this.desktop;
				if (dt) this.insertChildHTML_(child, sibling, dt);
			}

		child.afterParentChanged_(oldpt);
		if (!_noChildCallback)
			this.onChildAdded_(child);
		return true;
	},
	/** Removes a child.
	 * @param zk.Widget child the child to remove.
	 * @return boolean whether it is removed successfully.
	 * @see #detach
	 * @see #clear
	 */
	/** Removes a child with more control.
	 * It is similar to {@link #removeChild(zk.Widget)} except the caller
	 * could prevent it from removing the DOM element.
	 *
	 * <p>Notice that the associated DOM elements and {@link #unbind_}
	 * is called first (i.e., called before {@link #beforeParentChanged_},
	 * modifying the widget tree, ID space, and {@link #onChildRemoved_}).
	 * @param zk.Widget child the child to remove.
	 * @param boolean ignoreDom whether to remove the DOM element
	 * @return boolean whether it is removed successfully.
	 * @see #detach
	 * @see #clear
	 */
	removeChild: function (child, ignoreDom) {
		var oldpt;
		if (!(oldpt = child.parent))
			return false;
		if (this != oldpt)
			return false;

		_rmIdSpaceDown(child);

		//Note: remove HTML and unbind first, so unbind_ will have all info
		if (child.z_rod) {
			_unbindrod(child);
			
			// Bug ZK-454
			jq(child.uuid, zk).remove();
		} else if (child.desktop) 
			this.removeChildHTML_(child, ignoreDom);

		if (!_noParentCallback)
			child.beforeParentChanged_(null);

		_unlink(this, child);


		if (!_noParentCallback)
			child.afterParentChanged_(oldpt);
		if (!_noChildCallback)
			this.onChildRemoved_(child);
		return true;
	},
	/** Removes this widget (from its parent).
	 * If it was attached to a DOM tree, the associated DOM elements will
	 * be removed, too.
	 * @see #removeChild
	 */
	detach: function () {
		if (this.parent) this.parent.removeChild(this);
		else {
			var cf = zk.currentFocus;
			if (cf && zUtl.isAncestor(this, cf))
				zk.currentFocus = null;
			var n = this.$n();
			if (n) {
				this.unbind();
				_rmDom(this, n);
			}
		}
	},
	/** Removes all children.
	 */
	clear: function () {
		while (this.lastChild)
			this.removeChild(this.lastChild);
	},
	/** Replaces this widget with the specified one.
	 * The parent and siblings of this widget will become the parent
	 * and siblings of the specified one.
	 * <p>Notice that {@link #replaceHTML} is used to replace a DOM element
	 * that usually doesn't not belong to any widget.
	 * And, {@link #replaceWidget} is used to replace the widget, and
	 * it maintains both the widget tree and the DOM tree.
	 * @param zk.Widget newwgt the new widget that will replace this widget.
	 * @see #replaceHTML
	 * @since 5.0.1
	 */
	replaceWidget: function (newwgt) {
		_replaceLink(this, newwgt);

		_rmIdSpaceDown(this);
		_addIdSpaceDown(newwgt);

		var cf = zk.currentFocus, cfid, cfrg;
		if (cf && zUtl.isAncestor(this, cf)) {
			cfid = cf.uuid;
			cfrg = _bkRange(cf);
			zk.currentFocus = null;
		}

		var node = this.$n(),
			p = this.parent, shallReplace,
			dt = newwgt.desktop || this.desktop;
		if (this.z_rod) {
			_unbindrod(this);
			if (!(shallReplace = (dt = dt || (p ? p.desktop: p))
			&& (node = jq('#' + this.uuid))))
				_bindrod(newwgt);
		} else
			shallReplace = dt;

		if (shallReplace) {
			if (node) newwgt.replaceHTML(node, dt, null, true);
			else {
				this.unbind();
				newwgt.bind(dt);
			}

			_fixBindLevel(newwgt, p ? p.bindLevel + 1: 0);
			zWatch.fire('onBindLevelMove', newwgt);
		}

		if (p)
			p.onChildReplaced_(this, newwgt);

		this.parent = this.nextSibling = this.previousSibling = null;
		
		if (cfid) {
			cf = zk.Widget.$(cfid);
			if (!cf)
				_rsFocus({focus: newwgt, range: cfrg}); // restore to outer root
			else if (zUtl.isAncestor(newwgt, cf))
				_rsFocus({focus: cf, range: cfrg});
		}
	},
	/** Replaced the child widgets with the specified widgets.
	 * It is usefull if you want to replace a part of children whose
	 * DOM element is a child element of <code>subId</code> (this.$n(subId)).
	 * <p>Note: it assumes this.$n(subId) exists.
	 * @param String subId the ID of the cave that contains the child widgets
	 * to replace with.
	 * @param Array wgts an arrray of widgets that will become children of this widget
	 * @param String tagBeg the beginning of HTML tag, such as &tl;tbody&gt;.
	 * Ignored if null.
	 * @param String tagEnd the ending of HTML tag, such as &lt;/tbody&gt;
	 * Ignored if null.
	 * @see zAu#createWidgets
	 */
	replaceCavedChildren_: function (subId, wgts, tagBeg, tagEnd) {
		_noChildCallback = true; //no callback
		try {
			//1. remove (but don't update DOM)
			var cave = this.$n(subId), fc, oldwgts = [];
			for (var w = this.firstChild; w;) {
				var sib = w.nextSibling;
				if (jq.isAncestor(cave, w.$n())) {
					if (!fc || fc == w) fc = sib;
					this.removeChild(w, true); //no dom
					oldwgts.push(w);
				}
				w = sib;
			}

			//2. insert (but don't update DOM)
			for (var j = 0, len = wgts.length; j < len; ++j)
				this.insertBefore(wgts[j], fc, true); //no dom
		} finally {
			_noChildCallback = false;
		}

		if (fc = this.desktop) {
			//3. generate HTML
			var out = [];
			if (tagBeg) out.push(tagBeg);
			for (var j = 0, len = wgts.length; j < len; ++j)
				wgts[j].redraw(out);
			if (tagEnd) out.push(tagEnd);

			//4. update DOM
			jq(cave).html(out.join(''));

			//5. bind
			for (var j = 0, len = wgts.length; j < len; ++j) {
				wgts[j].bind(fc);
				//Bug 3322909 Dirty fix for nrows counting wrong,
				//currently the nrows is for Listbox.
				var n = this._nrows;  
				this.onChildReplaced_(oldwgts[j], wgts[j]);
				this._nrows = n;
			}
		}
	},

	/** A callback called before the parent is changed.
	 * @param zk.Widget newparent the new parent (null if it is removed)
	 * The previous parent can be found by {@link #parent}.
	 * @see #onChildAdded_
	 * @see #onChildRemoved_
	 * @see #afterParentChanged_
	 */
	beforeParentChanged_: function (/*newparent*/) {
	},
	/** A callback called after the parent has been changed.
	 * @param zk.Widget oldparent the previous parent (null if it was not attached)
	 * The current parent can be found by {@link #parent}.
	 * @since 5.0.4
	 * @see #beforeParentChanged_
	 */
	afterParentChanged_: function (/*oldparent*/) {
	},

	/** Returns if this widget is really visible, i.e., all ancestor widget and itself are visible. 
	 * @return boolean
	 * @see #isVisible
	 */
	/** Returns if this widget is really visible, i.e., all ancestor widget and itself are visible. 
	 * @param Map opts [optional] the options. Allowed values:
	 * <ul>
	 * <li>dom - whether to check DOM element instead of {@link #isVisible}</li>
	 * <li>until - specifies the ancestor to search up to (included).
	 * If not specified, this method searches all ancestors.
	 * If specified, this method searches only this widget and ancestors up
	 * to the specified one (included).</li>
	 * <li>strict - whether to check DOM element's style.visibility.
	 * It is used only if <code>dom</code> is also specified.</li>
	 * <li>cache - a map of cached result (since 5.0.8). Ignored if null.
	 * If specified, the result will be stored and used to speed up the processing.</li>
	 * </ul>
	 * @return boolean
	 * @see #isVisible
	 */
	isRealVisible: function (opts) {
		var dom = opts && opts.dom,
			cache = opts && opts.cache, visited = [], ck,
			wgt = this;
		while (wgt) {
			if (cache && (ck=wgt.uuid) && (ck=cache[ck]) !== undefined)
				return _markCache(cache, visited, ck);

			if (cache)
				visited.push(wgt);
	
			if (dom && !wgt.z_virnd) { //z_virnd implies zk.Native, zk.Page and zk.Desktop
			//Except native, we have to assume it is invsibile if $n() is null
			//Example, tabs in the accordion mold (case: zktest/test2 in IE)
			//Alertinative is to introduce another isVisibleXxx but not worth
				if (!zk(wgt.$n()).isVisible(opts.strict))
					return _markCache(cache, visited, false);
			} else if (!wgt._visible) // TODO: wgt._visible is not accurate, if tabpanel is not selected, we should fix in ZK 7.(B65-ZK-1076)
				return _markCache(cache, visited, false);

			//check if it is hidden by parent, such as child of hbox/vbox or border-layout
			var wp = wgt.parent, p, n;
			if (wp && wp._visible && (p=wp.$n()) && (n=wgt.$n()))
				while ((n=zk(n).vparentNode(true)) && p != n)
					if ((n.style||{}).display == 'none') //hidden by parent
						return _markCache(cache, visited, false);

			if (opts && opts.until == wgt)
				break;

			wgt = wp;
		}
		return _markCache(cache, visited, true);
	},
	/** Returns if this widget is visible
	 * @return boolean
	 * @see #isRealVisible
	 * @see jqzk#isVisible
	 */
	/** Returns if this widget is visible
	 * @param boolean strict whether to check the visibility of the associated
	 * DOM element. If true, this widget and the associated DOM element
	 * must be both visible.
	 * @return boolean
	 * @see #isRealVisible
	 * @see jqzk#isVisible
	 * @see #setVisible
	 */
	isVisible: function (strict) {
		var visible = this._visible;
		if (!strict || !visible)
			return visible;
		var n = this.$n();
		return n && zk(n).isVisible(); //ZK-1692: widget may not bind or render yet
	},
	/** Sets whether this widget is visible.
	 * <h3>Subclass Notes</h3>
	 * <ul>
	 * <li>setVisible invokes the parent's {@link #onChildVisible_}, so you
	 * can override {@link #onChildVisible_} to change the related DOM element.
	 * For example, updating the additional enclosing tags (such as zul.box.Box). </li>
	 * <li>setVisible invokes {@link #setDomVisible_} to change the visibility of a child DOM element, so override it if necessary.</li>
	 * </ul>
	 * @param boolean visible whether to be visible
	 * @return zk.Widget this widget
	 */
	setVisible: function (visible) {
		if (this._visible != visible) {
			this._visible = visible;

			var p = this.parent, ocvCalled;
			if (this.desktop) {
				var parentVisible = !p || p.isRealVisible(),
					node = this.$n(),
					floating = this._floating;

				if (!parentVisible) {
					if (!floating) this.setDomVisible_(node, visible);
				} else if (visible) {
					var zi;
					if (floating)
						this.setZIndex(zi = _topZIndex(this), {fire:true});

					this.setDomVisible_(node, true);

					//from parent to child
					for (var j = 0, fl = _floatings.length; j < fl; ++j) {
						var w = _floatings[j].widget,
							n = _floatings[j].node;
						if (this == w)
							w.setDomVisible_(n, true, {visibility:1});
						else if (_floatVisibleDependent(this, w)) {
							zi = zi >= 0 ? ++zi: _topZIndex(w);
							w.setFloatZIndex_(n, zi);
							w.setDomVisible_(n, true, {visibility:1});
						}
					}

					if (ocvCalled = p) p.onChildVisible_(this);
						//after setDomVisible_ and before onShow (Box depends on it)
					
					this.fire('onShow');
					if (!zk.animating())
						zUtl.fireShown(this);
				} else {
					this.fire('onHide');
					if (!zk.animating())
						zWatch.fireDown('onHide', this);

					for (var j = _floatings.length, bindLevel = this.bindLevel; j--;) {
						var w = _floatings[j].widget;
						if (bindLevel >= w.bindLevel)
							break; //skip non-descendant (and this)
						if (_floatVisibleDependent(this, w))
							w.setDomVisible_(_floatings[j].node, false, {visibility:1});
					}

					this.setDomVisible_(node, false);
				}
			}
			if (p && !ocvCalled) p.onChildVisible_(this);
				//after setDomVisible_ and after onHide
		}
		return this;
	},
	/** Synchronizes a map of objects that are associated with this widget, and
	 * they shall be resized when the size of this widget is changed.
	 * <p>It is useful to sync the layout, such as shadow, mask
	 * and error message, that is tightly associated with a widget.
	 * @param Map opts the options, or undefined if none of them specified.
	 * Allowed values:<br/>
	 */
	zsync: function () {
		for (var nm in this.effects_) {
			var ef = this.effects_[nm];
			if (ef && ef.sync) ef.sync();
		}
	},
	/** Makes this widget visible.
	 * It is a shortcut of <code>setVisible(true)</code>
	 * @return zk.Widget this widget
	 */
	show: function () {return this.setVisible(true);},
	/** Makes this widget invisible.
	 * It is a shortcut of <code>setVisible(false)</code>
	 * @return zk.Widget this widget
	 */
	hide: function () {return this.setVisible(false);},
	/** Changes the visibility of a child DOM content of this widget.
	 * It is called by {@link #setVisible} to really change the visibility
	 * of the associated DOM elements.
	 * <p>Default: change n.style.display directly. 
	 * @param DOMElement n the element (never null)
	 * @param boolean visible whether to make it visible
	 * @param Map opts [optional] the options.
	 * If omitted, <code>{display:true}</code> is assumed. Allowed value:
	 * <ul>
	 * <li>display - Modify n.style.display</li>
	 * <li>visibility - Modify n.style.visibility</li>
	 * </ul>
	 */
	setDomVisible_: function (n, visible, opts) {
		if (!opts || opts.display) {
			var act;
			if (act = this.actions_[visible ? "show": "hide"])
				act[0].call(this, n, act[1]);
			else
				n.style.display = visible ? '': 'none';
		}
		if (opts && opts.visibility)
			n.style.visibility = visible ? 'visible': 'hidden';
	},
	/** A callback called after a child has been added to this widget.
	 * <p>Notice: when overriding this method, {@link #onChildReplaced_}
	 * is usually required to override, too.
	 * @param zk.Widget child the child being added
	 * @see #beforeParentChanged_
	 * @see #onChildRemoved_
	 */
	onChildAdded_: function (/*child*/) {
	},
	/** A callback called after a child has been removed to this widget.
	 * <p>Notice: when overriding this method, {@link #onChildReplaced_}
	 * @param zk.Widget child the child being removed
	 * @see #beforeParentChanged_
	 * @see #onChildAdded_
	 */
	onChildRemoved_: function (/*child*/) {
	},
	/** A callback called after a child has been replaced.
	 * Unlike {@link #onChildAdded_} and {@link #onChildRemoved_}, this
	 * method is called only if {@link zk.AuCmd1#outer}.
	 * And if this method is called, neither {@link #onChildAdded_} nor {@link #onChildRemoved_}
	 * will be called.
	 * <p>Default: invoke {@link #onChildRemoved_} and then
	 * {@link #onChildAdded_}.
	 * Furthermore, it sets this.childReplacing_ to true before invoking
	 * {@link #onChildRemoved_} and {@link #onChildAdded_}, so we can optimize
	 * the code (such as rerender only once) by checking its value.
	 * @param zk.Widget oldc the old child (being removed). Note: it might be null.
	 * @param zk.Widget newc the new child (being added). Note: it might be null.
	 */
	onChildReplaced_: function (oldc, newc) {
		this.childReplacing_ = true;
		try {
			if (oldc) this.onChildRemoved_(oldc);
			if (newc) this.onChildAdded_(newc);
		} finally {
			this.childReplacing_ = false;
		}
	},
	/** A callback called after a child's visibility is changed
	 * (i.e., {@link #setVisible} was called).
	 * <p>Notice that this method is called after the _visible property
	 * and the associated DOM element(s) have been changed.
	 * <p>To know if it is becoming visible, you can check {@link #isVisible}
	 * (such as this._visible).
	 * @param zk.Widget child the child whose visiblity is changed
	 */
	onChildVisible_: function () {
	},
	/** A callback called after a child has been delay rendered.
	 * @param zk.Widget child the child being rendered
	 * @see #deferRedraw_
	 * @since 6.5.1
	 */
	onChildRenderDefer_: function (/*child*/) {
	},
	/** Makes this widget as topmost.
	 * <p>If this widget is not floating, this method will look for its ancestors for the first ancestor who is floating. In other words, this method makes the floating containing this widget as topmost.
	 * To make a widget floating, use {@link #setFloating_}.
	 * <p>This method has no effect if it is not bound to the DOM tree, or none of the widget and its ancestors is floating. 
	 * <p>Notice that it does not fire onFloatUp so it is caller's job if it is necessary
	 * to close other popups.
	 * @return int the new value of z-index of the topmost floating window, -1 if this widget and none of its ancestors is floating or not bound to the DOM tree. 
	 * @see #setFloating_
	 */
	setTopmost: function () {
		if (!this.desktop) return -1;

		for (var wgt = this; wgt; wgt = wgt.parent)
			if (wgt._floating) {
				var zi = _topZIndex(wgt);
				for (var j = 0, fl = _floatings.length; j < fl; ++j) { //from child to parent
					var w = _floatings[j].widget,
						n = _floatings[j].node;
					if (wgt == w)
						w.setFloatZIndex_(n, zi); //must be hit before any parent
					else if (zUtl.isAncestor(wgt, w) && w.isVisible())
						w.setFloatZIndex_(n, ++zi);
				}
				return zi;
			}
		return -1;
	},
	/** Sets the z-index for a floating widget.
	 * It is called by {@link #setTopmost} to set the z-index,
	 * and called only if {@link #setFloating_} is ever called.
	 * @param DOMElement node the element whose z-index needs to be set.
	 * It is the value specified in <code>opts.node</code> when {@link #setFloating_}
	 * is called. If not specified, it is the same as {@link #$n}.
	 * @param int zi the z-index to set
	 * @see #setFloating_
	 * @since 5.0.3
	 */
	setFloatZIndex_: function (node, zi) {
		if (node != this.$n()) node.style.zIndex = zi; //only a portion
		else this.setZIndex(zi, {fire:true});
	},
	/** Returns the z-index of a floating widget.
	 * It is called by {@link #setTopmost} to decide the topmost z-index,
	 * and called only if {@link #setFloating_} is ever called.
	 * @param DOMElement node the element whose z-index needs to be set.
	 * It is the value specified in <code>opts.node</code> when {@link #setFloating_}
	 * is called. If not specified, it is the same as {@link #$n}.
	 * @since 5.0.3
	 * @see #setFloating_
	 */
	getFloatZIndex_: function (node) {
		return node != this.$n() ? node.style.zIndex: this._zIndex;
	},
	/** Returns the top widget, which is the first floating ancestor,
	 * or null if no floating ancestor.
	 * @return zk.Widget
	 * @see #isFloating_
	 */
	getTopWidget: function () {
		for (var wgt = this; wgt; wgt = wgt.parent)
			if (wgt._floating)
				return wgt;
	},
	/** Returns if this widget is floating. 
	 * <p>We say a widget is floating if the widget floats on top of others, rather than embed inside the parent. For example, an overlapped window is floating, while an embedded window is not.
	 * @return boolean
	 * @see #setFloating_
	 */
	isFloating_: function () {
		return this._floating;
	},
	/** Sets a status to indicate if this widget is floating.
	 * <p>Notice that it doesn't change the DOM tree. It is caller's job. 
	 * In the other words, the caller have to adjust the style by assiging
	 * <code>position</code> with <code>absolute</code> or <code>relative</code>.
	 * @param boolean floating whther to make it floating
	 * @param Map opts [optional] The options. Allowed options:
	 * <ul>
	 * <li>node: the DOM element. If omitted, {@link #$n} is assumed.</li>
	 * </ul>
	 * @return zk.Widget this widget
	 * @see #isFloating_
	 */
	setFloating_: function (floating, opts) {
		if (this._floating != floating) {
			if (floating) {
				//parent first
				var inf = {widget: this, node: opts && opts.node? opts.node: this.$n()},
					bindLevel = this.bindLevel;
				for (var j = _floatings.length;;) {
					if (--j < 0) {
						_floatings.unshift(inf);
						break;
					}
					if (bindLevel >= _floatings[j].widget.bindLevel) { //parent first
						_floatings.splice(j + 1, 0, inf);
						break;
					}
				}
				this._floating = true;
			} else {
				for (var j = _floatings.length; j--;)
					if (_floatings[j].widget == this)
						_floatings.splice(j, 1);
				this._floating = false;
			}
		}
		return this;
	},

	/** Returns the Z index.
	 * @return int
	 */
	getZIndex: _zkf = function () {
		return this._zIndex;
	},
	getZindex: _zkf,
	/** Sets the Z index.
	 * @param int zIndex the Z index to assign to
	 * @param Map opts if opts.fire is specified, the onZIndex event will be triggered.
	 * @return zk.Widget this widget.
	 */
	setZIndex: _zkf = function (zIndex, opts) {
		if (this._zIndex != zIndex) {
			this._zIndex = zIndex;
			var n = this.$n();
			if (n) {
				n.style.zIndex = zIndex >= 0 ? zIndex: '';
				if (opts && opts.fire) this.fire('onZIndex', (zIndex > 0 || zIndex === 0) ? zIndex: -1, {ignorable: true});
			}
		}
		return this;
	},
	setZindex: _zkf,

	/** Returns the scoll top of the associated DOM element of this widget.
	 * <p>0 is always returned if this widget is not bound to a DOM element yet.
	 * @return int
	 */
	getScrollTop: function () {
		var n = this.$n();
		return n ? n.scrollTop: 0;
	},
	/** Returns the scoll left of the associated DOM element of this widget.
	 * <p>0 is always returned if this widget is not bound to a DOM element yet.
	 * @return int
	 */
	getScrollLeft: function () {
		var n = this.$n();
		return n ? n.scrollLeft: 0;
	},
	/** Sets the scoll top of the associated DOM element of this widget.
	 * <p>This method does nothing if this widget is not bound to a DOM element yet.
	 * @param int the scroll top.
	 * @return zk.Widget this widget.
	 */
	setScrollTop: function (val) {
		var n = this.$n();
		if (n) n.scrollTop = val;
		return this;
	},
	/** Sets the scoll left of the associated DOM element of this widget.
	 * <p>This method does nothing if this widget is not bound to a DOM element yet.
	 * @param int the scroll top.
	 * @return zk.Widget this widget.
	 */
	setScrollLeft: function (val) {
		var n = this.$n();
		if (n) n.scrollLeft = val;
		return this;
	},
	/** Makes this widget visible in the browser window by scrolling ancestors up or down, if necessary.
	 * <p>Default: invoke zk(this).scrollIntoView();
	 * @see jqzk#scrollIntoView
	 * @return zk.Widget this widget
	 */
	scrollIntoView: function () {
		zk(this.$n()).scrollIntoView();
		return this;
	},

	/** Generates the HTML fragment for this widget.
	 * The HTML fragment shall be pushed to out. For example,
<pre><code>
out.push('<div', this.domAttrs_(), '>');
for (var w = this.firstChild; w; w = w.nextSibling)
	w.redraw(out);
out.push('</div>');
</code></pre>
	 * <p>Default: it retrieves the redraw function associated with
	 * the mold ({@link #getMold}) and then invoke it.
	 * The redraw function must have the same signature as this method.
	 * @param Array out an array to output HTML fragments.
	 * Technically it can be anything that has the method called <code>push</code>
	 */
	redraw: function (out) {
		if (!this.deferRedraw_(out)) {
			var f;
			if (f = this.prolog)
				out.push(f);

			if ((f = this.$class.molds) && (f = f[this._mold]))
				return f.apply(this, arguments);

			zk.error("Mold "+this._mold+" not found in "+this.className);
		}
	},
	/* Utilities for handling the so-called render defer ({@link #setRenderdefer}).
	 * This method is called automatically by {@link #redraw},
	 * so you only need to use it if you override {@link #redraw}.
	 * <p>A typical usage is as follows.
	 * <pre><code>
redraw: function (out) {
  if (!this.deferRedraw_(out)) {
  	out.push(...); //redraw
  }
}
	 * </code></pre>
	 * @param Array out an array to output the HTML fragments.
	 * @since 5.0.2
	 */
	deferRedraw_: function (out) {
		var delay;
		if ((delay = this._renderdefer) >= 0) {
			if (!this._norenderdefer) {
				this.z_rod = this._z$rd = true;
				this.deferRedrawHTML_(out);
				out = null; //to free memory

				var wgt = this;
				setTimeout(function () {_doDeferRender(wgt);}, delay);
				return true;
			}
			delete this._norenderdefer;
			delete this.z_rod;
		}
		return false;
	},
	/**
	 * Renders a fake DOM element that will replace with the correct element after
	 * the deferring time is up. The method is designed for some widgets to override,
	 * such as Treeitem, Listitem, and Row, whose HTML tag is created inside a table.
	 * <p>By default, the Div tag is assumed.
	 * @param Array out an array to output the HTML fragments.
	 * @since 5.0.6
	 */
	deferRedrawHTML_: function (out) {
		out.push('<div', this.domAttrs_({domClass:1}), ' class="z-renderdefer"></div>');
	},
	/** Forces the rendering if it is deferred.
	 * A typical way to defer the render is to specify {@link #setRenderdefer}
	 * with a non-negative value. The other example is some widget might be
	 * optimized for the performance by not rendering some or the whole part
	 * of the widget. If the rendering is deferred, the corresponding DOM elements
	 * ({@link #$n}) are not available. If it is important to you, you can
	 * force it to be rendered.
	 * <p>Notice that this method only forces this widget to render. It doesn't
	 * force any of its children. If you want, you have invoke {@link #forcerender}
	 * one-by-one
	 * <p>The derived class shall override this method, if it implements
	 * the render deferring (other than {@link #setRenderdefer}).
	 * @since 5.0.2
	 */
	forcerender: function () {
		_doDeferRender(this);
	},
	/** Updates the DOM element's CSS class. It is called when the CSS class is changed (e.g., setZclass is called).
	 * <p>Default: it changes the class of {@link #$n}. 
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>Override it if the class has to be copied to DOM elements other than {@link #$n}.</li>
	 * </ul>
	 * @see #updateDomStyle_
	 */
	updateDomClass_: function () {
		if (this.desktop) {
			var n = this.$n();
			if (n) n.className = this.domClass_();
			this.zsync();
		}
	},
	/** Updates the DOM element's style. It is called when the CSS style is changed (e.g., setStyle is called).
	 * <p>Default: it changes the CSS style of {@link #$n}. 
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>Override it if the CSS style has to be copied to DOM elements other than {@link #$n}.</li>
	 * </ul>
	 */
	updateDomStyle_: function () {
		if (this.desktop) {
			var s = jq.parseStyle(this.domStyle_()),
				n = this.$n();
			// B50-3355680: size is potentially affected when setStyle
			if (!s.width && this._hflex)
				s.width = n.style.width;
			if (!s.height && this._vflex)
				s.height = n.style.height;
			zk(n).clearStyles().jq.css(s);

			var t = this.getTextNode();
			if (t && t != n) {
				s = this._domTextStyle(t, s);
				zk(t).clearStyles().jq.css(s);
			}
			this.zsync();
		}
	},
	_domTextStyle: function (t, s) {
		// B50-3355680
		s = jq.filterTextStyle(s);
		if (!s.width && this._hflex)
			s.width = t.style.width;
		if (!s.height && this._vflex)
			s.height = t.style.height;
		return s;
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls : 'z-' + this.widgetName;
	},
	/** Returns the DOM element that is used to hold the text, or null
	 * if this widget doesn't show any text.
	 * <p>Default: return null (no text node).
	 * <p>For example, {@link #updateDomStyle_} will change the style
	 * of the text node, if any, to make sure the text is displayed correctly.
	 * @return DOMElement the DOM element.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Component_Development/Client-side/Text_Styles_and_Inner_Tags">ZK Client-side Reference: Text Styles and Inner Tags</a>.
	 * @see #domTextStyleAttr_
	 * @see #updateDomStyle_
	 */
	getTextNode: function () {
	},

	/** Returns the style used for the DOM element of this widget.
	 * <p>Default: a concatenation of style, width, visible and so on. 
	 * @param Map no [options] the style to exclude (i.e., to turn off).
	 * If omitted, it means none (i.e., all included). For example, you don't
	 * want width to generate, call <code>domStyle_({width:1})</code>.
	 * Notice, though a bit counter-intuition, specify 1 (or true) to denote exclusion.
	 * Allowed value (subclass might support more options):<br/>
	 * <ul>
	 * <li>style - exclude {@link #getStyle}</li>
	 * <li>width - exclude {@link #getWidth}</li>
	 * <li>height - exclude {@link #getHeight}</li>
	 * <li>left - exclude {@link #getLeft}</li>
	 * <li>top - exclude {@link #getTop}</li>
	 * <li>zIndex - exclude {@link #getZIndex}</li>
	 * </ul>
	 * @return String the content of the style, such as width:100px;z-index:1; 
	 * @see #domClass_
	 * @see #domAttrs_
	 */
	domStyle_: function (no) {
		var out = [], s;
		if (s = this.z$display) //see au.js
			out.push("display:", s, ';');
		else if (!this.isVisible() && (!no || !no.visible))
			out.push("display:none;");

		if ((!no || !no.style) && (s = this.getStyle())) {
			s = s.replace(REGEX_DQUOT,'\'');  // B50-ZK-647
			out.push(s);
			if (s.charAt(s.length - 1) != ';')
				out.push(';');
		}
		if ((!no || !no.width) && (s = this.getWidth()))
			out.push('width:', s, ';');
		if ((!no || !no.height) && (s = this.getHeight())) 
			out.push('height:', s, ';');
		if ((!no || !no.left) && (s = this.getLeft()))
			out.push('left:', s, ';');
		if ((!no || !no.top) && (s = this.getTop()))
			out.push('top:', s, ';');
		if ((!no || !no.zIndex) && (s = this.getZIndex()) >= 0)
			out.push('z-index:', s, ';');
		return out.join('');
	},
	/** Returns the class name(s) used for the DOM element of this widget.
	 * <p>Default: a concatenation of {@link #getZclass} and {@link #getSclass}. 
	 *
	 * @param Map no [options] the style class to exclude (i.e., to turn off).
	 * If omitted, it means none (i.e., all included). For example, you don't
	 * want sclass to generate, call <code>domClass_({sclass:1})</code>.
	 * Notice, though a bit counter-intuition, specify 1 (or true) to denote exclusion.
	 * Allowed value (subclass might support more options):<br/>
	 * <ul>
	 * <li>sclass - exclude {@link #getSclass}</li>
	 * <li>zclass - exclude {@link #getZclass}</li>
	 * </ul>
	 * @return String the CSS class names, such as <code>z-button foo</code>
	 * @see #domStyle_
	 * @see #domAttrs_
	 */
	domClass_: function (no) {
		var s, z;
		if (!no || !no.sclass)
			s = this.getSclass();
		if (!no || !no.zclass)
			z = this.getZclass();
		return s ? z ? s + ' ' + z: s: z||'';
	},
	/** Returns the HTML attributes that is used to generate DOM element of this widget.
	 * It is usually used to implement a mold ({@link #redraw}):
</pre><code>
function () {
 return '<div' + this.domAttrs_() + '></div>';
}</code></pre>
	 * <p>Default: it generates id, style, class, and tooltiptext.
	 * Notice that it invokes {@link #domClass_} and {@link #domStyle_},
	 * unless they are disabled by the <code>no<code> argument. 
	 *
	 * @param Map no [options] the attributes to exclude (i.e., to turn off).
	 * If omitted, it means none (i.e., all included). For example, you don't
	 * want the style class to generate, call <code>domAttrs_({domClass:1})</code>.
	 * Notice, though a bit counter-intuition, specify 1 (or true) to denote exclusion.
	 * Allowed value (subclass might support more options):<br/>
	 * <ul>
	 * <li>domClass - exclude {@link #domClass_}</li>
	 * <li>domStyle - exclude {@link #domStyle_}</li>
	 * <li>tooltiptext - exclude {@link #getTooltiptext}</li>
	 * </ul>
	 * <p>return the HTML attributes, such as id="z_u7_3" class="z-button"
	 * @return String 
	 */
	domAttrs_: function (no) {
		var out = [], attrs, s;
		if ((!no || !no.id) && (s = this.uuid))
			out.push(' id="', s, '"')
		if ((!no || !no.domStyle) && (s = this.domStyle_(no)))
			out.push(' style="', s, '"');
		if ((!no || !no.domClass) && (s = this.domClass_(no)))
			out.push(' class="', s, '"');
		if ((!no || !no.tooltiptext) && (s = this.domTooltiptext_()))
			out.push(' title="', zUtl.encodeXML(s), '"'); // ZK-676
		for (var nm in (attrs = this.domExtraAttrs))
			out.push(' ', nm, '="', attrs[nm]||'', '"'); //generate even if val is empty
		return out.join('');
	},
	/** Returns the tooltiptext for generating the title attribute of the DOM element.
	 * <p>Default: return {@link #getTooltiptext}.
	 * <p>Deriving class might override this method if the parent widget
	 * is not associated with any DOM element, such as treerow's parent: treeitem.
	 * @return String the tooltiptext
	 * @since 5.0.2
	 */
	domTooltiptext_ : function () {
		return this.getTooltiptext();
	},
	/** Returns the style attribute that contains only the text related CSS styles. For example, it returns style="font-size:12pt;font-weight:bold" if #getStyle is border:none;font-size:12pt;font-weight:bold.
	 * <p>It is usually used with {@link #getTextNode} to
	 * <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Component_Development/Client-side/Text_Styles_and_Inner_Tags">ZK Client-side Reference: Text Styles and Inner Tags</a>. 
	 * @see #getTextNode
	 * @return String the CSS style that are related to text (string).
	 */
	domTextStyleAttr_: function () {
		var s = this.getStyle();
		return s ? zUtl.appendAttr("style", jq.filterTextStyle(s)): s;
	},

	/** Replaces the specified DOM element with the HTML content generated this widget.
	 * It is the same as <code>jq(n).replaceWith(wgt, desktop, skipper)</code>.
	 * <p>The DOM element to be replaced can be {@link #$n} or any independent DOM element. For example, you can replace a DIV element (and all its descendants) with this widget (and its descendants).
	 * <p>This method is usually used to replace a DOM element with a root widget (though, with care, it is OK for non-root widgets). Non-root widgets usually use {@link #appendChild}
	 *  and {@link #insertBefore} to attach to the DOM tree[1]
	 * <p>If the DOM element doesn't exist, you can use {@link _global_.jq#before} or {@link _global_.jq#after} instead.
	 * <p>Notice that, both {@link #replaceHTML} fires the beforeSize and onSize watch events
	 * (refer to {@link zWatch}).
	 * <p>If skipper is null. It implies the caller has to fire these two events if it specifies a skipper
	 * (that is how {@link #rerender} is implemented).
	 * <h3>Subclass Note</h3>
	 * This method actually forwards the invocation to its parent by invoking
	 * parent's {@link #replaceChildHTML_} to really replace the DOM element.
	 * Thus, override {@link #replaceChildHTML_} if you want to do something special for particular child widgets.
	 *
	 * @param Object n the DOM element ({@link DOMElement}) or anything
	 * {@link #$} allowed.
	 * @param zk.Desktop desktop [optional] the desktop that this widget shall belong to.
	 * If omitted, it is retrieve from the current desktop.
	 * If null, it is decided automatically ( such as the current value of {@link #desktop} or the first desktop)
	 * @param zk.Skipper skipper [optional] it is used only if it is called by {@link #rerender}
	 * @see #replaceWidget
	 * @see _global_.jq#replaceWith
	 * @return zk.Widget
	 */
	replaceHTML: function (n, desktop, skipper, _trim_) {
		if (!desktop) {
			desktop = this.desktop;
			if (!zk.Desktop._ndt) zk.stateless();
		}

		var cfi = skipper ? null: _bkFocus(this);

		var p = this.parent;
		if (p) p.replaceChildHTML_(this, n, desktop, skipper, _trim_);
		else {
			var oldwgt = this.getOldWidget_(n);
			if (oldwgt) oldwgt.unbind(skipper); //unbind first (w/o removal)
			else if (this.z_rod) _unbindrod(this); //possible (if replace directly)
			jq(n).replaceWith(this.redrawHTML_(skipper, _trim_));
			this.bind(desktop, skipper);
		}

		if (!skipper) {
			window._onsizet = jq.now();
			zUtl.fireSized(this);
		}

		_rsFocus(cfi);
		return this;
	},
	/**
	 * Returns the widget associated with the given node element.
	 * It is used by {@link #replaceHTML} and {@link #replaceChildHTML_} to retrieve
	 * the widget associated with the note.
	 * <p>It is similar to {@link #$} but it gives the widget a chance to
	 * handle extreme cases. For example, Treeitem doesn't associate a DOM element
	 * (or you can say Treeitem and Treerow shares the same DOM element), so
	 * <code>zk.Widget.$(n)</code> will return Treerow, not Treeitem.
	 * If it is the case, you can override it to make {@link #replaceHTML}
	 * works correctly.
	 * @param DOMElement n the DOM element to match the widget.
	 * @since 5.0.3
	 */
	getOldWidget_: function (n) {
		return Widget.$(n, {strict:true});
	},
	/** Returns the HTML fragment of this widget.
	 * @param zk.Skipper skipper the skipper. Ignored if null
	 * @param boolean trim whether to trim the HTML content before replacing
	 * @return String the HTML fragment
	 */
	redrawHTML_: function (skipper, trim) {
		var out = []; // Due to the side-effect of B65-ZK-1628, we remove the optimization of the array's join() for chrome.
		this.redraw(out, skipper);
		out = out.join('');
		return trim ? out.trim(): out;
			//To avoid the prolog being added repeatedly if keep invalidated:
			//<div><textbox/> <button label="Click!" onClick="self.invalidate()"/></div>
	},
	/** Re-renders the DOM element(s) of this widget.
	 * By re-rendering we mean to generate HTML again ({@link #redraw})
	 * and then replace the DOM elements with the new generated HTML code snippet.
	 * <p>It is equivalent to replaceHTML(this.node, null, skipper).
	 * <p>It is usually used to implement a setter of this widget.
	 * For example, if a setter (such as <code>setBorder</code>) has to
	 * modify the visual appearance, it can update the DOM tree directly,
	 * or it can call this method to re-render all DOM elements associated
	 * with is widget and its desendants.
	 * <p>It is coonvenient to synchronize the widget's state with
	 * the DOM tree with this method. However, it shall be avoided
	 * if the HTML code snippet is complex (otherwise, the performance won't be good).
	 * <p>If re-rendering is required, you can improve the performance
	 * by passing an instance of {@link zk.Skipper} that is used to
	 * re-render some or all descendant widgets of this widget.
	 * @param zk.Skipper skipper [optional] skip some portion of this widget
	 * to speed up the re-rendering.
	 * If not specified, rerender(0) is assumed (since ZK 6).
	 * @return zk.Widget this widget.
	 */
	/** Re-renders after the specified time (milliseconds).
	 * <p>Notice that, to have the best performance, we use the single timer
	 * to handle all pending rerenders for all widgets.
	 * In other words, if the previous timer is not expired (and called),
	 * the second call will reset the expiration time to the value given
	 * in the second call.
	 * @param int timeout the number milliseconds (non-negative) to wait
	 * before rerender. If negative, it means rerender shall take place
	 * immediately. If not specified, 0 is assumed (since ZK 6).
	 * @return zk.Widget this widget.
	 * @since 5.0.4
	 */
	rerender: function (skipper) {
		if (this.desktop) {
			if (!skipper || skipper > 0) { //default: 0
				_rerender(this, skipper||0);
				return this;
			}
			if (skipper < 0)
				skipper = null; //negative -> immediately

			var n = this.$n();
			if (n) {
				var oldrod = this.z$rod;
				this.z$rod = false;
					//to avoid side effect since the caller might look for $n(xx)

				var skipInfo;
				if (skipper) {
					skipInfo = skipper.skip(this);
					if (skipInfo) {
						var cfi = _bkFocus(this);

						this.replaceHTML(n, null, skipper, true);

						skipper.restore(this, skipInfo);

						zWatch.fireDown('onRestore', this);
							//to notify it is restored from rerender with skipper
						zUtl.fireSized(this);

						_rsFocus(cfi);
					}
				}

				if (!skipInfo)
					this.replaceHTML(n, null, null, true);

				this.z$rod = oldrod;
			}
		}
		return this;
	},

	/** Replaces the DOM element(s) of the specified child widget.
	 * It is called by {@link #replaceHTML} to give the parent a chance to
	 * do something special for particular child widgets.
	 * @param zk.Widget child the child widget whose DOM content is used to replace the DOM tree
	 * @param DOMElement n the DOM element to be replaced
	 * @param zk.Desktop dt [optional the desktop that this widget shall belong to.
	 * If null, it is decided automatically ( such as the current value of {@link #desktop} or the first desktop)
	 * @param zk.Skipper skipper it is used only if it is called by {@link #rerender}
	 */
	replaceChildHTML_: function (child, n, desktop, skipper, _trim_) {
		var oldwgt = child.getOldWidget_(n);
		if (oldwgt) oldwgt.unbind(skipper); //unbind first (w/o removal)
		else if (this.shallChildROD_(child))
			_unbindrod(child); //possible (e.g., Errorbox: jq().replaceWith)
		jq(n).replaceWith(child.redrawHTML_(skipper, _trim_));
		child.bind(desktop, skipper);
	},
	/** Inserts the HTML content generated by the specified child widget before the reference widget (the before argument).
	 * It is called by {@link #insertBefore} and {@link #appendChild} to handle the DOM tree.
	 * <p>Deriving classes might override this method to modify the HTML content, such as enclosing with TD.
	 * <p>Notice that when inserting the child (without the before argument), this method will call {@link #getCaveNode} to find the location to place the DOM element of the child. More precisely, the node returned by {@link #getCaveNode} is the parent DOM element of the child. The default implementation of {@link #getCaveNode} is to look for a sub-node named uuid$cave. In other words, it tried to place the child inside the so-called cave sub-node, if any.
	 * Otherwise, {@link #$n} is assumed.
	 * @param zk.Widget child the child widget to insert
	 * @param zk.Widget before the child widget as the reference to insert the new child before. If null, the HTML content will be appended as the last child. 
	 * The implementation can use before.getFirstNode_() ({@link #getFirstNode_}) to retrieve the DOM element
	 * @param zk.Desktop desktop
	 * @see #getCaveNode 
	 */
	insertChildHTML_: function (child, before, desktop) {
		var ben, html = child.redrawHTML_();
		if (before) {
			if (before.$instanceof(zk.Native)) { //native.$n() is usually null
				ben = before.previousSibling;
				if (ben) {
					if (ben == child) //always true (since link ready), but to be safe
						ben = ben.previousSibling;
					if (ben && (ben = ben.$n())) {
						jq(ben).after(html);
						child.bind(desktop);
						return;
					}
				}
				//FUTURE: it is not correct to go here, but no better choice yet
			}
			before = before.getFirstNode_();
		}
		if (!before)
			for (var w = this;;) {
				ben = w.getCaveNode();
				if (ben) break;

				var w2 = w.nextSibling;
				if (w2 && (before = w2.getFirstNode_()))
					break;

				if (!(w = w.parent)) {
					ben = document.body;
					break;
				}
			}

		if (before) {
			var sib = before.previousSibling;
			if (_isProlog(sib)) before = sib;
			jq(before).before(html);
		} else
			jq(ben).append(html);
		child.bind(desktop);
	},
	/** Called by {@link #insertChildHTML_} to to find the location to place the DOM element of the child.
	 * More precisely, the node returned by {@link #getCaveNode} is the parent DOM element of the child's DOM element.
	 * <p>Default: <code>this.$n('cave') || this.$n()</code>
	 * You can override it to return whatever DOM element you want. 
	 * @see #insertChildHTML_
	 * @return DOMElement
	 */
	getCaveNode: function () {
		return this.$n('cave') || this.$n();
	},
	/** Returns the first DOM element of this widget.
	 * If this widget has no corresponding DOM element, this method will look
	 * for its siblings.
	 * <p>This method is designed to be used with {@link #insertChildHTML_}
	 * for retrieving the DOM element of the <code>before</code> widget.
	 * @return DOMElement
	 */
	getFirstNode_: function () {
		for (var w = this; w; w = w.nextSibling) {
			var n = _getFirstNodeDown(w);
			if (n) return n;
		}
	},
	/** Removes the corresponding DOM content of the specified child.
	 * It is called by {@link #removeChild} to remove the DOM content.
	 * <p>The default implementation of this method will invoke {@link #removeHTML_}
	 * if the ignoreDom argument is false or not specified.
	 * <p>Overrides this method or {@link #removeHTML_} if you have to
	 * remove DOM elements other than child's node (and the descendants).
	 * @param zk.Widget child the child widget to remove
	 * @param boolean ignoreDom whether to remove the DOM element
	 */
	removeChildHTML_: function (child, ignoreDom) {
		var cf = zk.currentFocus;
		if (cf && zUtl.isAncestor(child, cf))
			zk.currentFocus = null;

		var n = child.$n();
		if (n) {
			var sib = n.previousSibling;
			if (child.prolog && _isProlog(sib))
				jq(sib).remove();
		} else
			_prepareRemove(child, n = []);

		child.unbind();

		if (!ignoreDom)
			child.removeHTML_(n);
	},
	/**
	 * Removes the HTML DOM content.
	 * <p>The default implementation simply removes the DOM element passed in.
	 * <p>Overrides this method if you have to remove the related DOM elements.
	 * @since 5.0.1
	 * @param Array n an array of {@link DOMElement} to remove.
	 * If this widget is associated with a DOM element ({@link #$n} returns non-null),
	 * n is a single element array.
	 * If this widget is not assoicated with any DOM element, an array of
	 * child widget's DOM elements are returned.
	 */
	removeHTML_: function (n) {
		_rmDom(this, n);
		this.clearCache();
	},
	/**
	 * Returns the DOM element that this widget is bound to.
	 * It is null if it is not bound to the DOM tree, or it doesn't have the associated DOM node (for example, {@link zul.utl.Timer}).
	 * <p>Notice that {@link #desktop} is always non-null if it is bound to the DOM tree.
	 * In additions, this method is much faster than invoking jq() (see {@link _global_.jq},
	 * since it caches the result (and clean up at the {@link #unbind_}).
	 * <pre><code>var n = wgt.$n();</code></pre>
	 * @return DOMElement
	 * @see #$n(String)
	 */
	/** Returns the child element of the DOM element(s) that this widget is bound to.
	 * This method assumes the ID of the child element the concatenation of
	 * {@link #uuid}, -, and subId. For example,
<pre><code>var cave = wgt.$n('cave'); //the same as jq('#' + wgt.uuid + '-' + 'cave')[0]</code></pre>
	 * Like {@link #$n()}, this method caches the result so the performance is much better
	 * than invoking jq() directly.
	 * @param String subId the sub ID of the child element
	 * @return DOMElement
	 * @see #$n()
	 */
	$n: function (subId) {
		if (subId) {
			var n = this._subnodes[subId];
			if (!n && this.desktop) {
				n = jq(this.uuid + '-' + subId, zk)[0];
				this._subnodes[subId] = n ? n : 'n/a';
			}
			return n == 'n/a' ? null : n;
		}
		var n = this._node;
		if (!n && this.desktop && !this._nodeSolved) {
			this._node = n = jq(this.uuid, zk)[0];
			this._nodeSolved = true;
		}
		return n;
	},
	/** Clears the cached nodes (by {@link #$n}). */
	clearCache: function () {
		this._node = null;
		this._subnodes = {};
		this._nodeSolved = false;
	},
	/** Returns the page that this widget belongs to, or null if there is
	 * no page available.
	 * @return zk.Page
	 */
	getPage: function () {
		var page, dt;
		for (page = this.parent; page; page = page.parent)
			if (page.$instanceof(zk.Page))
				return page;

		return (page = (dt = this.desktop)._bpg) ?
			page: (dt._bpg = new zk.Body(dt));
	},

	/** Returns whether this widget is being bound to DOM.
	 * In other words, it returns true if {@link #bind} is called
	 * against this widget or any of its ancestors.
	 * @return boolean
	 * @since 5.0.8
	 */
	isBinding: function () {
		if (this.desktop)
			for (var w = this; w; w = w.parent)
				if (w._binding)
					return true;
	},

	/** Binds this widget.
	 * It is called to assoicate (aka., attach) the widget with
	 * the DOM tree.
	 * <p>Notice that you rarely need to invoke this method, since
	 * it is called automatically (such as {@link #replaceHTML}
	 * and {@link #appendChild}).
	 * <p>Notice that you rarely need to override this method, either.
	 * Rather, override {@link #bind_} instead.
	 *
	 * @see #bind_
	 * @see #unbind
	 * @param zk.Desktop dt [optional] the desktop the DOM element belongs to.
	 * If not specified, ZK will decide it automatically.
	 * @param zk.Skipper skipper [optional] used if {@link #rerender} is called with a non-null skipper.
	 * @return zk.Widget this widget
	 */
	bind: function (desktop, skipper) {
		this._binding = true;

		_rerenderDone(this, skipper); //cancel pending async rerender
		if (this.z_rod) 
			_bindrod(this);
		else {
			var after = [], fn;
			this.bind_(desktop, skipper, after);
			while (fn = after.shift())
				fn();
		}

		delete this._binding;
		return this;
	},
	/** Unbinds this widget.
	 * It is called to remove the assoication (aka., detach) the widget from
	 * the DOM tree.
	 * <p>Notice that you rarely need to invoke this method, since
	 * it is called automatically (such as {@link #replaceHTML}).
	 * <p>Notice that you rarely need to override this method, either.
	 * Rather, override {@link #unbind_} instead.
	 *
	 * @see #unbind_
	 * @see #bind
	 * @param zk.Desktop dt [optional] the desktop the DOM element belongs to.
	 * If not specified, ZK will decide it automatically.
	 * @param zk.Skipper skipper [optional] used if {@link #rerender} is called with a non-null skipper.
	 * @return zk.Widget this widget
	 */
	unbind: function (skipper) {
		_rerenderDone(this, skipper); //cancel pending async rerender
		if (this.z_rod)
			_unbindrod(this);
		else {
			var after = [];
			this.unbind_(skipper, after);
			for (var j = 0, len = after.length; j < len;)
				after[j++]();
		}
		return this;
	},

	/** Callback when this widget is bound (aka., attached) to the DOM tree.
	 * It is called after the DOM tree has been modified (with the DOM content of this widget, i.e., {@link #redraw})
	 * (for example, by {@link #replaceHTML}).
	 * <p>Note: don't invoke this method directly. Rather, invoke {@link #bind} instead.
<pre><code>
wgt.bind();
</code></pre>
	 * <h3>Subclass Note</h3>
	 * <p>Subclass overrides this method to initialize the DOM element(s), such as adding a DOM listener. Refer to Widget and DOM Events and {@link #domListen_} for more information. 
	 *
	 * @see #bind
	 * @see #unbind_
	 * @param zk.Desktop dt [optional] the desktop the DOM element belongs to.
	 * If not specified, ZK will decide it automatically.
	 * @param zk.Skipper skipper [optional] used if {@link #rerender} is called with a non-null skipper.
	 * @param Array after an array of function ({@link Function}) that will be invoked after {@link #bind_} has been called. For example, 
<pre><code>
bind_: function (desktop, skipper, after) {
  this.$supers('bind_', arguments);
  var self = this;
  after.push(function () {
    self._doAfterBind(something);
    ...
  });
}
</code></pre>
	 */
	bind_: function (desktop, skipper, after) {
		_bind0(this);

		this.desktop = desktop || (desktop = zk.Desktop.$(this.parent));

		var p = this.parent, v;
		this.bindLevel = p ? p.bindLevel + 1: 0;

		if ((v = this._draggable) && v != "false" && !_dragCtl(this))
			this.initDrag_();
		
		if (this._nvflex || this._nhflex)
			_listenFlex(this);

		this.bindChildren_(desktop, skipper, after);
		var self = this;
		if (this.isListen('onBind')) {
			zk.afterMount(function () {
				if (self.desktop) //might be unbound
					self.fire('onBind');
			});
		}
		
		if (this.isListen('onAfterSize')) //Feature ZK-1672
			zWatch.listen({onSize: this});
		
		if (zk.mobile) {
			after.push(function (){
				setTimeout(function () {// lazy init
					self.bindSwipe_();
					self.bindDoubleTap_();
					self.bindTapHold_();
				}, 300);
			});
		}
	},
	/** Binds the children of this widget.
	 * It is called by {@link #bind_} to invoke child's {@link #bind_} one-by-one.
	 * @param zk.Desktop dt [optional] the desktop the DOM element belongs to.
	 * If not specified, ZK will decide it automatically.
	 * @param zk.Skipper skipper [optional] used if {@link #rerender} is called with a non-null skipper.
	 * @param Array after an array of function ({@link Function}) that will be invoked after {@link #bind_} has been called. For example, 
	 * @since 5.0.5
	 */
	bindChildren_: function (desktop, skipper, after) {
		for (var child = this.firstChild, nxt; child; child = nxt) {
			nxt = child.nextSibling;
				//we have to store first since RefWidget will replace widget

			if (!skipper || !skipper.skipped(this, child))
				if (child.z_rod) _bindrod(child);
				else child.bind_(desktop, null, after); //don't pass skipper
		}
	},

	/** Callback when a widget is unbound (aka., detached) from the DOM tree.
	 * It is called before the DOM element(s) of this widget is going to be removed from the DOM tree (such as {@link #removeChild}.
	 * <p>Note: don't invoke this method directly. Rather, invoke {@link #unbind} instead. 
	 * <p>Note: after invoking <code>this.$supers('unbind_', arguments)</code>,
	 * the association with DOM elements are lost. Thus it is better to invoke
	 * it as the last statement.
	 * <p>Notice that {@link #removeChild} removes DOM elements first, so
	 * {@link #unbind_} is called before {@link #beforeParentChanged_} and
	 * the modification of the widget tree. It means it is safe to access
	 * {@link #parent} and other information here
	 * @see #bind_
	 * @see #unbind
	 * @param zk.Skipper skipper [optional] used if {@link #rerender} is called with a non-null skipper 
	 * @param Array after an array of function ({@link Function})that will be invoked after {@link #unbind_} has been called. For example, 
<pre><code>
unbind_: function (skipper, after) {
  var self = this;
  after.push(function () {
    self._doAfterUnbind(something);
    ...
  }
  this.$supers('unbind_', arguments);
}
</code></pre>
	 */
	unbind_: function (skipper, after) {
		_unbind0(this);
		_unlistenFlex(this);

		this.unbindChildren_(skipper, after);
		this.cleanDrag_(); //ok to invoke even if not init
		this.unbindSwipe_();
		this.unbindDoubleTap_();
		this.unbindTapHold_();
		
		if (this.isListen('onAfterSize')) //Feature ZK-1672
			zWatch.unlisten({onSize: this});
		
		if (this.isListen('onUnbind')) {
			var self = this;
			zk.afterMount(function () {
				if (!self.desktop) //might be bound
					self.fire('onUnbind');
			});
		}

		for (var nm in this.effects_) {
			var ef = this.effects_[nm];
			if (ef) ef.destroy();
		}
		this.effects_ = {};
	},
	/** Unbinds the children of this widget.
	 * It is called by {@link #unbind_} to invoke child's {@link #unbind_} one-by-one.
	 * @param zk.Skipper skipper [optional] used if {@link #rerender} is called with a non-null skipper 
	 * @param Array after an array of function ({@link Function})that will be invoked after {@link #unbind_} has been called. For example, 
	 * @since 5.0.5
	 */
	unbindChildren_: function (skipper, after) {
		for (var child = this.firstChild, nxt; child; child = nxt) {
			nxt = child.nextSibling; //just in case

			// check child's desktop for bug 3035079: Dom elem isn't exist when parent do appendChild and rerender
			if (!skipper || !skipper.skipped(this, child))
				if (child.z_rod) _unbindrod(child);
				else if (child.desktop) {
					child.unbind_(null, after); //don't pass skipper
					if (zk.feature.ee && child.$instanceof(zk.Native))
						zAu._storeStub(child); //Bug ZK-1596: native will be transfer to stub in EE, store the widget for used in mount.js
				}
		}
	},

	/** Associates UUID with this widget.
	 * <p>Notice that {@link #uuid} is automically associated (aka., bound) to this widget.
	 * Thus, you rarely need to invoke this method unless you want to associate with other identifiers.
	 * <p>For example, ZK Google Maps uses this method since it has to
	 * bind the anchors manually.
	 *
	 * @param String uuid the UUID to assign to the widgtet
	 * @param boolean add whether to bind. Specify true if you want to bind;
	 * false if you want to unbind.
	 */
	extraBind_: function (uuid, add) {
		if (add == false) delete _binds[uuid];
		else _binds[uuid] = this;
	},
	setFlexSize_: function(sz, isFlexMin) {
		var n = this.$n(),
			zkn = zk(n);
		if (sz.height !== undefined) {
			if (sz.height == 'auto')
				n.style.height = '';
			else if (sz.height != '' || (sz.height === 0 && !this.isFloating_())) //bug #2943174, #2979776, ZK-1159, ZK-1358
				this.setFlexSizeH_(n, zkn, sz.height, isFlexMin);
			else
				n.style.height = this._height || '';
		}
		if (sz.width !== undefined) {
			if (sz.width == 'auto')
				n.style.width = '';
			else if (sz.width != '' || (sz.width === 0 && !this.isFloating_())) //bug #2943174, #2979776, ZK-1159, ZK-1358
				this.setFlexSizeW_(n, zkn, sz.width, isFlexMin);
			else
				n.style.width = this._width || '';
		}
		return {height: n.offsetHeight, width: n.offsetWidth};
	},
	setFlexSizeH_: function(n, zkn, height, isFlexMin) {
		var h = zkn.revisedHeight(height, true), // excluding margin for F50-3000873.zul and B50-3285635.zul 
			newh = h,
			margins = zkn.sumStyles("tb", jq.margins);
		n.style.height = jq.px0(h);
			
		// fixed for B50-3317729.zul on webkit
		if (zk.safari) {
			margins -= zkn.sumStyles("tb", jq.margins);
			if (margins) 
				n.style.height = jq.px0(h + margins);
		}
	},
	
	setFlexSizeW_: function(n, zkn, width, isFlexMin) {
		var w = zkn.revisedWidth(width, true), // excluding margin for F50-3000873.zul and B50-3285635.zul
			neww = w,
			margins = zkn.sumStyles("lr", jq.margins),
			pb = zkn.padBorderWidth(); 
		
		n.style.width = jq.px0(w);
		
		// Bug ZK-521
		if ((zk.linux || zk.mac) && zk.ff && jq.nodeName(n, "select")) {
			var offset = width - margins,
				diff = offset - n.offsetWidth;
			if (diff > 0)
				n.style.width = jq.px0(w + diff);
		}
		// fixed for B50-3317729.zul on webkit
		if (zk.safari) {
			margins -= zkn.sumStyles("lr", jq.margins);
			if (margins) 
				n.style.width = jq.px0(w + margins);
		}
	},
	beforeChildrenFlex_: function(kid) {
		//to be overridden
		return true; //return true to continue children flex fixing
	},
	afterChildrenFlex_: function(kid) {
		//to be overridden
	},
	ignoreFlexSize_: function(attr) { //'w' for width or 'h' for height calculation
		//to be overridden, whether ignore widget dimension in vflex/hflex calculation 
		return false;
	},
	ignoreChildNodeOffset_: function(attr) { //'w' for width or 'h' for height calculation
		//to be overridden, whether ignore child node offset in vflex/hflex calculation
		return false;
	},
	beforeMinFlex_: function (attr) { //'w' for width or 'h' for height
		//to be overridden, before calculate my minimum flex
		return null;
	},
	beforeParentMinFlex_: function (attr) { //'w' for width or 'h' for height
		//to be overridden, before my minimum flex parent ask my natural(not minimized) width/height
	},
	afterChildrenMinFlex_: function() {
		//to be overridden, after my children fix the minimum flex (both width and height)
	},
	afterResetChildSize_: function() {
		//to be overridden, after my children reset the size of (both width and height)
	},
	isExcludedHflex_: function () {
		return jq(this.$n()).css('position') == 'absolute'; // B60-ZK-917
		//to be overridden, if the widget is excluded for hflex calculation.
	},
	isExcludedVflex_: function () {
		return jq(this.$n()).css('position') == 'absolute'; // B60-ZK-917
		//to be overridden, if the widget is excluded for vflex calculation.
	},
	// to overridden this method have to fix the IE9 issue (ZK-483)
	// you can just add 1 px more for the offsetWidth
	getChildMinSize_: function (attr, wgt) { //'w' for width or 'h' for height
		// feature #ZK-314: zjq.minWidth function return extra 1px in IE9/10
		var wd = zjq.minWidth(wgt);
		if(zk.ie > 8 && zk.isLoaded('zul.wgt') && wgt.$instanceof(zul.wgt.Image)) {
			wd = zk(wgt).offsetWidth();
		}
		return attr == 'h' ? zk(wgt).offsetHeight() : wd; //See also bug ZK-483
	},
	getParentSize_: zk.ie6_ ? function (p) {
		var zkp = zk(p),
			hgh,
			wdh,
			s = p.style;
		if (s.width.indexOf('px') >= 0) {
			wdh = zk.parseInt(s.width);
		}
		if (s.height.indexOf('px') >= 0) {
			hgh = zk.parseInt(s.height);
		}
		return {height: hgh || zkp.revisedHeight(p.offsetHeight),
					width: wdh || zkp.revisedWidth(p.offsetWidth)};
	} : function(p) {
		//to be overridden
		var zkp = zk(p);
		return {height: zkp.revisedHeight(p.offsetHeight), width: zkp.revisedWidth(p.offsetWidth)};
	},
	getMarginSize_: function (attr) { //'w' for width or 'h' for height
		return zk(this).sumStyles(attr == 'h' ? 'tb' : 'lr', jq.margins);
	},
	getContentEdgeHeight_: function () {
		var p = this.$n(),
			fc = this.firstChild,
			fc = fc && zk.isLoaded('zul.wgt') && fc.$instanceof(zul.wgt.Caption) ? fc.nextSibling : fc, //Bug ZK-1524: Caption should ignored
			c = fc ? fc.$n() : p.firstChild,
			zkp = zk(p),
			h = zkp.padBorderHeight();
		
		if (c) {
			c = c.parentNode;
			while (c && c.nodeType == 1 && p != c) {
				var zkc = zk(c);
				h += zkc.padBorderHeight() + zkc.sumStyles("tb", jq.margins);
				c = c.parentNode;
			}
			return h;
		}
		return 0;
	},
	getContentEdgeWidth_: function () {
		var p = this.$n(),
			fc = this.firstChild,
			fc = fc && zk.isLoaded('zul.wgt') && fc.$instanceof(zul.wgt.Caption) ? fc.nextSibling : fc, //Bug ZK-1524: Caption should ignored
			c = fc ? fc.$n() : p.firstChild,
			zkp = zk(p),
			w = zkp.padBorderWidth();
		
		if (c) {
			c = c.parentNode;
			while (c && c.nodeType == 1 && p != c) {
				var zkc = zk(c);
				w += zkc.padBorderWidth() + zkc.sumStyles("lr", jq.margins);
				c = c.parentNode;
			}
			return w;
		}
		return 0;
	},
	fixFlex_: function() {
		zFlex.fixFlex(this);
	},
	fixMinFlex_: function(n, orient) { //internal use
		return zFlex.fixMinFlex(this, n, orient);
	},
	clearCachedSize_: function() {
		delete this._hflexsz;
		delete this._vflexsz;
	},
	resetSize_: function(orient) {
		(this.$n()).style[orient == 'w' ? 'width': 'height'] = '';
	},
	/** Initializes the widget to make it draggable.
	 * It is called if {@link #getDraggable} is set (and bound).
	 * <p>You rarely need to override this method, unless you want to handle drag-and-drop differently.
	 * <p>Default: use {@link zk.Draggable} to implement drag-and-drop,
	 * and the handle to drag is the element returned by {@link #getDragNode}
	 * @see #cleanDrag_
	 */
	initDrag_: function () {
		var n = this.getDragNode();
		if (n) { //ZK-1686: should check if DragNode exist
			this._drag = new zk.Draggable(this, n, this.getDragOptions_(_dragoptions));
			// B50-3306835.zul
			if (zk.ie9 && jq.nodeName(n, "img"))
				jq(n).bind('mousedown', zk.$void);
		}
	},
	/** Cleans up the widget to make it un-draggable. It is called if {@link #getDraggable}
	 * is cleaned (or unbound).
	 * <p>You rarely need to override this method, unless you want to handle drag-and-drop differently. 
	 * @see #cleanDrag_
	 */
	cleanDrag_: function () {
		var drag = this._drag;
		if (drag) {
			var n;
			if (zk.ie9 && (n = this.getDragNode()) && jq.nodeName(n, "img"))
				jq(n).unbind('mousedown', zk.$void);

			this._drag = null;
			drag.destroy();
		}
	},
	/** Returns the DOM element of this widget that can be dragged.
	 * <p>Default, it returns {@link #$n}, i.e., the user can drag the widget anywhere.
	 * @return DOMElement
	 * @see #ignoreDrag_
	 */
	getDragNode: function () {
		return this.$n();
	},
	/** Returns the options used to instantiate {@link zk.Draggable}.
	 * <p>Default, it does nothing but returns the <code>map</code> parameter,
	 * i.e., the default options.
	 * <p>Though rarely used, you can override any option passed to
	 * {@link zk.Draggable}, such as the start effect, ghosting and so on.
	 * @param Map map the default implementation 
	 * @return Map
	 */
	getDragOptions_: function (map) {
		return map;
	},
	/** Returns if the location that an user is trying to drag is allowed.
	 * <p>Default: it always returns false.
	 * If the location that an user can drag is static, override {@link #getDragNode},
	 * which is easier to implement.
	 * @param zk.Draggable pt
	 * @return boolean whether to ignore
	 */
	ignoreDrag_: function (pt) {
		return false;
	},
	/** Returns the widget if it allows to drop the specified widget (being dragged), or null if not allowed. It is called when the user is dragging a widget on top a widget.
	 * <p>Default: it check if the values of droppable and draggable match. It will check the parent ({@link #parent}), parent's parent, and so on until matched, or none of them are matched.
	 * <p>Notice that the widget to test if droppable might be the same as the widget being dragged (i.e., this == dragged). By default, we consider them as non-matched.
	 * @param zk.Widget dragged - the widget being dragged (never null). 
	 * @return zk.Widget the widget to drop to.
	 */
	getDrop_: function (dragged) {
		if (this != dragged) {
			var dropType = this._droppable,
				dragType = dragged._draggable;
			if (dropType == 'true') return this;
			if (dropType && dragType != "true")
				for (var dropTypes = this._dropTypes, j = dropTypes.length; j--;)
					if (dragType == dropTypes[j])
						return this;
		}
		return this.parent ? this.parent.getDrop_(dragged): null;
	},
	/** Called to have some visual effect when the user is dragging a widget over this widget and this widget is droppable.
	 * Notice it is the effect to indicate a widget is droppable.
	 * <p>Default, it adds the CSS class named 'z-drag-over' if over is true, and remove it if over is false.
	 * @param boolean over whether the user is dragging over (or out, if false) 
	 */
	dropEffect_: function (over) {
		jq(this.$n()||[])[over ? "addClass" : "removeClass"]("z-drag-over");
	},
	/** Returns the message to show when an user is dragging this widget, or null if it prefers to clone the widget with {@link #cloneDrag_}.
	 * <p>Default, it return the inner text if if {@link #$n} returns a TR, TD, or TH element. Otherwise, it returns null and {@link #cloneDrag_} will be called to create a DOM element to indicate dragging. 
	 * @return String the message to indicate the dragging, or null if clone is required
	 */
	getDragMessage_: function () {
		if (jq.nodeName(this.getDragNode(), "tr", "td", "th")) {
			var n = this.$n('real') || this.getCaveNode();
			return n ? n.textContent || n.innerText || '': '';
		}
	},
	/** Called to fire the onDrop event.
	 * You could override it to implement some effects to indicate dropping.
	 * <p>Default, it fires the onDrop event (with {@link #fire}).
	 * The subclass can override this method to pass more options such as the coordination where a widget is dropped. 
	 * @param zk.Draggable drag the draggable controller
	 * @param zk.Event evt the event causes the drop
	 */
	onDrop_: function (drag, evt) {
		var data = zk.copy({dragged: drag.control}, evt.data);
		this.fire('onDrop', data, null, Widget.auDelay);
	},
	/** Called to create the visual effect representing what is being dragged.
	 * In other words, it creates the DOM element that will be moved with the mouse pointer when the user is dragging.
	 * <p>This method is called if {@link #getDragMessage_} returns null.
	 * If {@link #getDragMessage_} returns a string (empty or not),
	 * a small popup containing the message is created to represent the widget being dragged.
	 * <p>You rarely need to override this method, unless you want a different visual effect. 
	 * @see #uncloneDrag_
	 * @param zk.Draggable drag the draggable controller
	 * @param Offset ofs the offset of the returned element (left/top)
	 * @return DOMElement the clone
	 */
	cloneDrag_: function (drag, ofs) {
		//See also bug 1783363 and 1766244

		var msg = this.getDragMessage_();
		if (typeof msg == "string" && msg.length > 9)
			msg = msg.substring(0, 9) + "...";

		var dgelm = zk.DnD.ghost(drag, ofs, msg);

		drag._orgcursor = document.body.style.cursor;
		document.body.style.cursor = "pointer";
		jq(this.getDragNode()).addClass('z-dragged'); //after clone
		return dgelm;
	},
	/** Undo the visual effect created by {@link #cloneDrag_}.
	 * @param zk.Draggable drag the draggable controller
	 */
	uncloneDrag_: function (drag) {
		document.body.style.cursor = drag._orgcursor || '';

		jq(this.getDragNode()).removeClass('z-dragged');
	},
	
	//Feature ZK-1672: provide empty onSize function if the widget is listened to onAfterSize 
	//	but the widget is never listened to onSize event
	onSize: function() {},
	/**
	 * Called to fire the onAfterSize event.
	 * @since 6.5.2
	 */
	onAfterSize: function () {
		if (this.desktop && this.isListen('onAfterSize')) {
			var n = this.getCaveNode(),
				width = n.offsetWidth,
				height = n.offsetHeight;
			if (this._preWidth != width || this._preHeight != height) {
				this._preWidth = width;
				this._preHeight = height;
				this.fire('onAfterSize', {width: width, height: height});
			}
		}
	},
	
	/** Bind swipe event to the widget on tablet device.
	 * It is called if HTML 5 data attribute (data-swipeable) is set to true.
	 * <p>You rarely need to override this method, unless you want to bind swipe behavior differently.
	 * <p>Default: use {@link zk.Swipe} to implement swipe event.
	 * @see #doSwipe_
	 * @since 6.5.0
	 */
	bindSwipe_: zk.$void,
	/** Unbind swipe event to the widget on tablet device.
	 * It is called if swipe event is unbound.
	 * <p>You rarely need to override this method, unless you want to unbind swipe event differently.
	 * @see #doSwipe_
	 * @since 6.5.0
	 */
	unbindSwipe_: zk.$void,
	/** Bind double click event to the widget on tablet device.
	 * It is called if the widget is listen to onDoubleClick event.
	 * <p>You rarely need to override this method, unless you want to implement double click behavior differently.
	 * @see #doDoubleClick_
	 * @since 6.5.0
	 */
	bindDoubleTap_: zk.$void,
	/** Unbind double click event to the widget on tablet device.
	 * It is called if the widget is listen to onDoubleClick event.
	 * <p>You rarely need to override this method, unless you want to implement double click behavior differently.
	 * @see #doDoubleClick_
	 * @since 6.5.0
	 */
	unbindDoubleTap_: zk.$void,
	/** Bind right click event to the widget on tablet device.
	 * It is called if the widget is listen to onRightClick event.
	 * <p>You rarely need to override this method, unless you want to implement right click behavior differently.
	 * @see #doRightClick_
	 * @since 6.5.1
	 */
	bindTapHold_: zk.$void,
	/** Unbind right click event to the widget on tablet device.
	 * It is called if the widget is listen to onRightClick event.
	 * <p>You rarely need to override this method, unless you want to implement right click behavior differently.
	 * @see #doRightClick_
	 * @since 6.5.1
	 */
	unbindTapHold_: zk.$void,
	/** Sets the focus to this widget.
	 * This method will check if this widget can be activated by invoking {@link #canActivate} first.
	 * <p>Notice: don't override this method. Rather, override {@link #focus_},
	 * which this method depends on.
     * @param int timeout how many milliseconds before changing the focus. If not specified or negative, the focus is changed immediately, 
	 * @return boolean whether the focus is gained to this widget. 
	 */
	focus: function (timeout) {
		return this.canActivate({checkOnly:true})
			&& zk(this.$n()).isRealVisible()
			&& this.focus_(timeout);
	},
	/** Called by {@link #focus} to set the focus.
	 * <p>Default: call child widget's focus until it returns true, or no child at all. 
	 * <h3>Subclass Note</h3>
	 * <ul>
	 * <li>If a widget is able to gain focus, it shall override this method to invoke {@link _global_.jqzk#focus}.</li>
	 * <li>It is called only if the DOM element is real visible (so you don't need to check again)</li>
	 * </ul>
<pre><code>
focus_: function (timeout) {
  zk(this.$n('foo').focus(timeout);
  return true;
}
</pre></code>
     * @param int timeout how many milliseconds before changing the focus. If not specified or negative, the focus is changed immediately, 
	 * @return boolean whether the focus is gained to this widget. 
	 * @since 5.0.5
	 */
	focus_: function (timeout) {
		if (zk(this.$n()).focus(timeout)) {
			this.setTopmost();
			return true;
		}
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w.isVisible() && w.focus_(timeout))
				return true;
		return false;
	},
	/** Checks if this widget can be activated (gaining focus and so on).
	 * <p>Default: return false if it is not a descendant of 
	 * {@link _global_.zk#currentModal}. 
	 * @param Map opts [optional] the options. Allowed values:
	 * <ul>
	 * <li>checkOnly: not to change focus back to modal dialog if unable to
	 * activate. If not specified, the focus will be changed back to
	 * {@link _global_.zk#currentModal}.
	 * In additions, if specified, it will ignore {@link zk#busy}, which is set
	 * if {@link zk.AuCmd0#showBusy} is called.
	 * This flag is usually set by {@link #focus}, and not set
	 * if it is caused by user's activity, such as clicking.</li>
	 * </ul>
	 * The reason to ignore busy is that we allow application to change focus
	 * even if busy, while the user cannot.
	 * @return boolean
	 */
	canActivate: function (opts) {
		if (_ignCanActivate)
			return true;
		if (zk.busy && (!opts || !opts.checkOnly)) { //Bug 2912533: none of widget can be activated if busy
			jq.focusOut(); // Bug 2968706
			return false;
		}

		var modal = zk.currentModal;
		if (modal && !zUtl.isAncestor(modal, this)
		&& !jq.isAncestor(modal.$n(), this.$n())) { //ZK-393: this might be included
			var wgt = this.getTopWidget();
			
			// Bug #3201879
			if (wgt && wgt != modal && wgt.getZIndex() > modal.getZIndex())
				return true;
			
			if (!opts || !opts.checkOnly) {
				var cf = zk.currentFocus;
				//Note: browser might change focus later, so delay a bit
				if (cf && zUtl.isAncestor(modal, cf)) cf.focus(0);
				else modal.focus(0);
			}
			return false;
		}
		return true;
	},

	//server comm//
	/** Smart-updates a property of the peer component associated with this widget, running at the server, with the specified value.
	 * <p>It is actually fired an AU requst named <code>setAttr</code>, and
	 * it is handled by the <code>updateByClient</code> method in <code>org.zkoss.zk.ui.AbstractComponent</code> (at the server).
	 * <p>By default, it is controlled by a component attribute called <code>org.zkoss.zk.ui.updateByClient</code>.
	 * And, it is default to false.
	 * Thus, the component developer has to override <code>updateByClient</code> at
	 * the server (in Java) and then update it rather than calling back superclass.
	 * For example,
	 * <pre><code>protected void updateByClient(String name, Object value) {
	if ("disabled".equals(name))
		setDisabled(value instanceof Boolean && ((Boolean)value).booleanValue());
	else
		super.updateByClient(name, value);
}</code></pre>
	 *
	 * @param String name the property name
     * @param Object value the property value
     * @param int timeout the delay before sending out the AU request. It is optional. If omitted, -1 is assumed (i.e., it will be sent with next non-deferrable request). 
	 * @see zAu#send
	 * @return zk.Widget
	 */
	smartUpdate: function (nm, val, timeout) {
		zAu.send(new zk.Event(this, 'setAttr', [nm, val]),
			timeout >= 0 ? timeout: -1);
		return this;
	},

	//widget event//
	/** Fire a widget event.
	 * @param zk.Event evt the event to fire
	 * @param int timeout the delay before sending the non-deferrable AU request (if necessary).
	 * If not specified or negative, it is decided automatically.
	 * It is ignored if no non-deferrable listener is registered at the server. 
	 * @return zk.Event the event being fired, i.e., evt. 
	 * @see #fire
	 * @see #listen
	 */
	fireX: function (evt, timeout) {
		var oldtg = evt.currentTarget;
		evt.currentTarget = this;
		try {
			var evtnm = evt.name,
				lsns = this._lsns[evtnm],
				len = lsns ? lsns.length: 0;
			if (len) {
				for (var j = 0; j < len;) {
					var inf = lsns[j++], o = inf[0];
					(inf[1] || o[evtnm]).call(o, evt);
					if (evt.stopped) return evt; //no more processing
				}
			}

			if (!evt.auStopped) {
				var toServer = evt.opts && evt.opts.toServer;
				if (toServer || (this.inServer && this.desktop)) {
					var asap = toServer || this._asaps[evtnm];
					if (asap == null) {
						var ime = this.$class._importantEvts;
						if (ime) {
							var ime = ime[evtnm];
							if (ime != null) 
								asap = ime;
						}
					}
					if (asap != null //true or false
					|| evt.opts.sendAhead)
						this.sendAU_(evt,
							asap ? timeout >= 0 ? timeout : Widget.auDelay : -1);
				}
			}
			return evt;
		} finally {
			evt.currentTarget = oldtg;
		}
	},
	/** Callback before sending an AU request.
	 * It is called by {@link #sendAU_}.
	 * <p>Default: this method will stop the event propagation
	 * and prevent the browser's default handling
	 * (by calling {@link zk.Event#stop}), 
	 * if the event is onClick, onRightClick or onDoubleClick.
	 * <p>Notice that {@link #sendAU_} is called against the widget sending the AU request
	 * to the server, while {@link #beforeSendAU_} is called against the event's
	 * target (evt.target).
	 *
	 * <p>Notice that since this method will stop the event propagation for onClick,
	 * onRightClick and onDoubleClick, it means the event propagation is stopped
	 * if the server registers a listener. However, it doesn't stop if
	 * only a client listener is registered (and, in this case, {@link zk.Event#stop}
	 * must be called explicitly if you want to stop).
	 *
	 * @param zk.Widget wgt the widget that causes the AU request to be sent.
	 * It will be the target widget when the server receives the event.
	 * @param zk.Event evt the event to be sent back to the server.
	 * Its content will be cloned to the AU request.
	 * @see #sendAU_
	 * @since 5.0.2
	 */
	beforeSendAU_: function (wgt, evt) {
		var en = evt.name;
		if (en == 'onClick' || en == 'onRightClick' || en == 'onDoubleClick')
			evt.shallStop = true;//Bug: 2975748: popup won't work when component with onClick handler
	},
	/** Sends an AU request to the server.
	 * It is invoked when {@link #fire} will send an AU request to the server.
	 *
	 * <p>Override Notice: {@link #sendAU_} will call evt.target's
	 * {@link #beforeSendAU_} to give the original target a chance to
	 * process it.
	 *
	 * @param zk.Event evt the event that will be sent to the server.
	 * @param int timeout the delay before really sending out the AU request
	 * @see #fire
	 * @see #beforeSendAU_
	 * @see zAu#sendAhead
	 * @since 5.0.1
	 */
	sendAU_: function (evt, timeout, opts) {
		(evt.target||this).beforeSendAU_(this, evt);
		evt = new zk.Event(this, evt.name, evt.data, evt.opts, evt.domEvent);
			//since evt will be used later, we have to make a copy and use this as target
		if (evt.opts.sendAhead) zAu.sendAhead(evt, timeout);
		else zAu.send(evt, timeout);
	},
	/** Check whether to ignore the click which might be caused by
	 * {@link #doClick_}
	 * {@link #doRightClick_}, or {@link #doDoubleClick_}.
	 * <p>Default: return false.
	 * <p>Deriving class might override this method to return true if
	 * it wants to ignore the click on certain DOM elements, such as
	 * the open icon of a treerow.
	 * <p>Notice: if true is returned, {@link #doClick_}
	 * {@link #doRightClick_}, and {@link #doDoubleClick_} won't be called.
	 * In additions, the popup and context of {@link zul.Widget} won't be
	 * handled, either.
	 * @param zk.Event the event that causes the click ({@link #doClick_}
	 * {@link #doRightClick_}, or {@link #doDoubleClick_}).
	 * @return boolean whether to ignore it
	 * @since 5.0.1
	 */
	shallIgnoreClick_: function (evt) {
	},

	/** Fire a widget event. An instance of {@link zk.Event} is created to represent the event.
	 *
	 * <p>The event listeners for this event will be called one-by-one unless {@link zk.Event#stop} is called.
	 *
	 * <p>If the event propagation is not stopped (i.e., {@link zk.Event#stop} not called)
	 * and {@link #inServer} is true, the event will be converted to an AU request and sent to the server.
	 Refer to <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Communication/AU_Requests/Client-side_Firing">ZK Client-side Reference: AU Requests: Client-side Firing</a> for more information.
	 * @param String evtnm the event name, such as onClick
	 * @param Object data [optional] the data depending on the event ({@link zk.Event}).
	 * @param Map opts [optional] the options. Refer to {@link zk.Event#opts}
	 * @param int timeout the delay before sending the non-deferrable AU request (if necessary).
	 * If not specified or negative, it is decided automatically.
	 * It is ignored if no non-deferrable listener is registered at the server. 
	 * @return zk.Event the event being fired. 
	 * @see #fire
	 * @see #listen
	 */
	fire: function (evtnm, data, opts, timeout) {
		return this.fireX(new zk.Event(this, evtnm, data, opts), timeout);
	},
	/** Registers listener(s) to the specified event. For example,
<pre><code>
wgt.listen({
  onClick: wgt,
  onOpen: wgt._onOpen,
  onMove: [o, o._onMove]
});
</code></pre>
	 * <p>As shown above, you can register multiple listeners at the same time, and echo value in infos can be a target, a function, or a two-element array, where the first element is a target and the second the function.
	 * A target can be any object that this will reference to when the event listener is called.
	 * Notice it is not {@link zk.Event#target}. Rather, it is <code>this</code> when the listener is called.
	 * <p>If the function is not specified, the target must have a method having the same name as the event. For example, if wgt.listen({onChange: target}) was called, then target.onChange(evt) will be called when onChange event is fired (by {@link #fire}). On the other hand, if the target is not specified, the widget is assumed to be the target.
	 * @param Map infos a map of event listeners.
	 * Each key is the event name, and each value can be the target, the listener function, or a two-element array, where the first element is the target and the second the listener function.
	 * Notice that the target is not {@link zk.Event#target}. Rather, it is <code>this</code> when the listener is called.
	 * @param int priority the higher the number, the earlier it is called. If omitted, 0 is assumed.
	 * If a widget needs to register a listener as the default behavior (such as zul.wnd.Window's onClose), -1000 is suggested 
	 * @return zk.Widget this widget
	 * @see #unlisten
	 * @see #fire
	 * @see #fireX
	 * @see #setListeners
	 * @see #setListener
	 */
	listen: function (infs, priority) {
		priority = priority ? priority: 0;
		for (var evt in infs) {
			var inf = infs[evt];
			if (jq.isArray(inf)) inf = [inf[0]||this, inf[1]];
			else if (typeof inf == 'function') inf = [this, inf];
			else inf = [inf||this, null];
			inf.priority = priority;

			var lsns = this._lsns[evt];
			if (!lsns) this._lsns[evt] = [inf];
			else
				for (var j = lsns.length;;)
					if (--j < 0 || lsns[j].priority >= priority) {
						lsns.splice(j + 1, 0, inf);
						break;
					}
		}
		return this;
	},
	/** Removes a listener from the sepcified event.
<pre><code>
wgt.unlisten({
  onClick: wgt,
  onOpen: wgt._onOpen,
  onMove: [o, o._onMove]
});
</code></pre>
	 * @param Map infos a map of event listeners.
	 * Each key is the event name, and each value can be the target, the listener function, or a two-element array, where the first element is the target and the second the listener function.
	 * @return zk.Widget this widget
	 * @see #listen
	 * @see #isListen
	 * @see #fire
	 * @see #fireX
	 */
	unlisten: function (infs) {
		l_out:
		for (var evt in infs) {
			var inf = infs[evt],
				lsns = this._lsns[evt], lsn;
			for (var j = lsns ? lsns.length: 0; j--;) {
				lsn = lsns[j];
				if (jq.isArray(inf)) inf = [inf[0]||this, inf[1]];
				else if (typeof inf == 'function') inf = [this, inf];
				else inf = [inf||this, null];
				if (lsn[0] == inf[0] && lsn[1] == inf[1]) {
					lsns.splice(j, 1);
					continue l_out;
				}
			}
		}
		return this;
	},
	/** Returns if a listener is registered for the specified event.
	 * @param String evtnm the event name, such as onClick.
	 * @param Map opts [optional] the options. If omitted, it checks only if the server registers any non-deferrable listener, and if the client register any listener. Allowed values:
	 * <ul>
	 * <li>any - in addition to the server's non-deferrable listener and client's listener, it also checks deferrable listener, and the so-called important events</li>
	 * <li>asapOnly - it checks only if the server registers a non-deferrable listener, and if any non-deferrable important event. Use this option, if you want to know whether an AU request will be sent.</li>
	 * </ul>
	 * @return boolean
	 */
	isListen: function (evt, opts) {
		var v = this._asaps[evt];
		if (v) return true;
		if (opts) {
			if (opts.asapOnly) {
				v = this.$class._importantEvts;
				return v && v[evt];
			}
			if (opts.any) {
				if (v != null) return true;
				v = this.$class._importantEvts;
				if (v && v[evt] != null) return true;
			}
		}

		var lsns = this._lsns[evt];
		return lsns && lsns.length;
	},
	/** Sets the listener a map of listeners.
	 * It is similar to {@link #listen}, except
	 * <ul>
	 * <li>It will 'remember' what the listeners are, such that it can unlisten
	 * by specifying null as the value of the <code>infs</code> argument</li>
	 * <li>The function can be a string and it will be converted to {@link Function}
	 * automatically.</li>
	 * </ul>
	 * <p>This method is mainly designed to be called by the application running
	 * at the server.
	 * 
	 * <p>Example:
<pre><code>
wgt.setListeners({
 onChange: function (event) {this.doSomething();},
 onFocus: 'this.doMore();',
 onBlur: null //unlisten
});
<code></pre>
	 * @param Map infos a map of event listeners.
	 * Each key is the event name, and each value is a string, a function or null.
	 * If the value is null, it means unlisten.
	 * If the value is a string, it will be converted to a {@link Function}.
	 * Notice that the target is not {@link zk.Event#target}. Rather, it is <code>this</code> when the listener is called.
	 */
	setListeners: function (infs) {
		for (var evt in infs)
			this.setListener(evt, infs[evt]);
	},
	/** Sets a listener that can be unlistened easily.
	 * It is designed to be called from server.
	 * For client-side programming, it is suggested to use {@link #listen}.
	 * <p>It is based {@link #listen}, but, unlike {@link #listen}, the second
	 * invocation for the same event will unlisten the previous one automatically.
	 * <p>In additions, if the function (specified in the second element of inf)
	 * is null, it unlistens the previous invocation.
	 * @param Array inf a two-element array. The first element is the event name,
	 * while the second is the listener function
	 * @see #setListeners
	 */
	/** Sets a listener
	 * It is designed to be called from server.
	 * For client-side programming, it is suggested to use {@link #listen}.
	 * Use it only if you want to unlisten the listener registered at the
	 * server (by use of the client namespace).
	 * <p>It is based {@link #listen}, but, unlike {@link #listen}, the second
	 * invocation for the same event will unlisten the previous one automatically.
	 * <p>In additions, if fn is null, it unlistens the previous invocation.
	 * @param String evt the event name
	 * @param Function fn the listener function.
	 * If null, it means unlisten.
	 * @see #setListeners
	 * @see #listen
	 */
	setListener: function (evt, fn) { //used by server
		if (jq.isArray(evt)) {
			fn = evt[1];
			evt = evt[0]
		}

		var bklsns = this._bklsns,
			oldfn = bklsns[evt],
			inf = {};
		if (oldfn) { //unlisten first
			delete bklsns[evt];
			inf[evt] = oldfn
			this.unlisten(inf);
		}
		if (fn) {
			inf[evt] = bklsns[evt]
				= typeof fn != 'function' ? new Function("var event=arguments[0];"+fn): fn;
			this.listen(inf);
		}
	},
	setOverride: function (nm, val) { //used by server (5.0.2)
		if (jq.isArray(nm)) {
			val = nm[1];
			nm = nm[0];
		}
		if (val) {
			var oldnm = '$' + nm;
			if (this[oldnm] == null && this[nm]) //only once
				this[oldnm] = this[nm];
			this[nm] = val;
				//use eval, since complete func decl
		} else {
			var oldnm = '$' + nm;
			this[nm] = this[oldnm]; //restore
			delete this[oldnm];
		}
	},
	setOverrides: function (infs) { //used by server
		for (var nm in infs)
			this.setOverride(nm, infs[nm]);
	},

	//ZK event handling//
	/** Called when the user clicks or right-clicks on widget or a child widget.
	 * It is called before {@link #doClick_} and {@link #doRightClick_}.
	 * <p>Default: does nothing but invokes the parent's {@link #doSelect_}.
	 * Notice that it does not fire any event.
	 * <p>Deriving class that supports selection (such as {@link zul.sel.ItemWidget})
	 * shall override this to handle the selection.
	 * <p>Technically, the selection can be handled in {@link #doClick_}.
	 * However, it is better to handle here since this method is invoked first
	 * such that the widget will be selected before one of its descendant widget
	 * handles {@link #doClick_}.
	 * <p>Notice that calling {@link zk.Event#stop} will stop the invocation of
	 * parent's {@link #doSelect_} and {@link #doClick_}/{@link #doRightClick_}.
	 * If you just don't want to call parent's {@link #doSelect_}, simply
	 * not to invoke super's doSelect_.
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doClick_
	 * @see #doRightClick_
	 * @since 5.0.1
	 */
	doSelect_: function(evt) {
		if (!evt.stopped) {
			var p = this.parent;
			if (p) p.doSelect_(evt);
		}
	},
	/** Called when the mouse is moved over this widget.
	 * It is called before {@link #doMouseOver_}.
	 * <p>Default: does nothing but invokes the parent's {@link #doTooltipOver_}.
	 * Notice that it does not fire any event.
	 * <p>Notice that calling {@link zk.Event#stop} will stop the invocation of
	 * parent's {@link #doTooltipOver_} and {@link #doMouseOver_}.
	 * If you just don't want to call parent's {@link #doMouseOver_}, simply
	 * not to invoke super's doMouseOver_.
	 * @since 5.0.5
	 * @see #doTooltipOut_
	 */
	doTooltipOver_: function (evt) {
		if (!evt.stopped) {
			var p = this.parent;
			if (p) p.doTooltipOver_(evt);
		}
	},
	/** Called when the mouse is moved out of this widget.
	 * It is called before {@link #doMouseOut_}.
	 * <p>Default: does nothing but invokes the parent's {@link #doTooltipOut_}.
	 * Notice that it does not fire any event.
	 * <p>Notice that calling {@link zk.Event#stop} will stop the invocation of
	 * parent's {@link #doTooltipOut_} and {@link #doMouseOut_}.
	 * If you just don't want to call parent's {@link #doMouseOut_}, simply
	 * not to invoke super's doMouseOut_.
	 * @since 5.0.5
	 * @see #doTooltipOver_
	 */
	doTooltipOut_: function (evt) {
		if (!evt.stopped) {
			var p = this.parent;
			if (p) p.doTooltipOut_(evt);
		}
	},
	/** Called when the user clicks on a widget or a child widget.
	 * A widget doesn't need to listen the click DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and call parent's doClick_
	 * if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>If a widget, such as zul.wgt.Button, handles onClick, it is better to override this method and <i>not</i> calling back the superclass.
	 * <p>Note: if {@link #shallIgnoreClick_} returns true, {@link #fireX} won't be
	 * called and this method invokes the parent's {@link #doClick_} instead
	 * (unless {@link zk.Event#stopped} is set).
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doDoubleClick_
	 * @see #doRightClick_
	 * @see #doSelect_
	 */
	doClick_: function (evt) {
		if (_fireClick(this, evt)) {
			var p = this.parent;
			if (p) p.doClick_(evt);
		}
	},
	/** Called when the user double-clicks on a widget or a child widget.
	 * A widget doesn't need to listen the dblclick DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and call parent's
	 * doDoubleClick_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>Note: if {@link #shallIgnoreClick_} returns true, {@link #fireX} won't be
	 * called and this method invokes the parent's {@link #doDoubleClick_} instead
	 * (unless {@link zk.Event#stopped} is set).
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doClick_
	 * @see #doRightClick_
	 */
	doDoubleClick_: function (evt) {
		if (_fireClick(this, evt)) {
			var p = this.parent;
			if (p) p.doDoubleClick_(evt);
		}
	},
	/** Called when the user right-clicks on a widget or a child widget.
	 * A widget doesn't need to listen the contextmenu DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and call parent's
	 * doRightClick_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>Note: if {@link #shallIgnoreClick_} returns true, {@link #fireX} won't be
	 * called and this method invokes the parent's {@link #doRightClick_} instead
	 * (unless {@link zk.Event#stopped} is set).
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doClick_
	 * @see #doDoubleClick_
	 */
	doRightClick_: function (evt) {
		if (_fireClick(this, evt)) {
			var p = this.parent;
			if (p) p.doRightClick_(evt);
		}
	},
	/** Called when the user moves the mouse pointer on top of a widget (or one of its child widget).
	 * A widget doesn't need to listen the mouseover DOM event.
	 * Rather, it shall override this method if necessary.
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doMouseOver_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doMouseMove_
	 * @see #doMouseOver_
	 * @see #doMouseOut_
	 * @see #doMouseDown_
	 * @see #doMouseUp_
	 * @see #doTooltipOver_
     */
	doMouseOver_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseOver_(evt);
		}
	},
	/** Called when the user moves the mouse pointer out of a widget (or one of its child widget).
	 * A widget doesn't need to listen the mouseout DOM event.
	 * Rather, it shall override this method if necessary. 
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doMouseOut_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @see #doMouseMove_
	 * @see #doMouseOver_
	 * @see #doMouseDown_
	 * @see #doMouseUp_
	 * @see #doTooltipOut_
	 */
	doMouseOut_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseOut_(evt);
		}
	},
	/** Called when the user presses down the mouse button on this widget (or one of its child widget).
	 * A widget doesn't need to listen the mousedown DOM event.
	 * Rather, it shall override this method if necessary. 
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doMouseDown_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doMouseMove_
	 * @see #doMouseOver_
	 * @see #doMouseOut_
	 * @see #doMouseUp_
	 * @see #doClick_
	 */
	doMouseDown_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseDown_(evt);
		}
	},
	/** Called when the user presses up the mouse button on this widget (or one of its child widget).
	 * A widget doesn't need to listen the mouseup DOM event.
	 * Rather, it shall override this method if necessary. 
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doMouseUp_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doMouseMove_
	 * @see #doMouseOver_
	 * @see #doMouseOut_
	 * @see #doMouseDown_
	 * @see #doClick_
	 */
	doMouseUp_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseUp_(evt);
		}
	},
	/** Called when the user moves the mouse pointer over this widget (or one of its child widget).
	 * A widget doesn't need to listen the mousemove DOM event.
	 * Rather, it shall override this method if necessary. 
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doMouseMove_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doMouseOver_
	 * @see #doMouseOut_
	 * @see #doMouseDown_
	 * @see #doMouseUp_
	 */
	doMouseMove_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doMouseMove_(evt);
		}
	},

	/** Called when the user presses down a key when this widget has the focus ({@link #focus}).
	 * <p>Notice that not every widget can have the focus.
	 * A widget doesn't need to listen the keydown DOM event.
	 * Rather, it shall override this method if necessary. 
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doKeyDown_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doKeyUp_
	 * @see #doKeyPress_
	 */
	doKeyDown_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyDown_(evt);
		}
	},
	/** Called when the user presses up a key when this widget has the focus ({@link #focus}).
	 * <p>Notice that not every widget can have the focus.
	 * A widget doesn't need to listen the keyup DOM event.
	 * Rather, it shall override this method if necessary. 
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doKeyUp_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doKeyDown_
	 * @see #doKeyPress_
	 */
	doKeyUp_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyUp_(evt);
		}
	},
	/** Called when the user presses a key when this widget has the focus ({@link #focus}).
	 * <p>Notice that not every widget can have the focus.
	 * A widget doesn't need to listen the keypress DOM event.
	 * Rather, it shall override this method if necessary. 
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doKeyPress_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doKeyDown_
	 * @see #doKeyUp_
	 */
	doKeyPress_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doKeyPress_(evt);
		}
	},
	/** Called when the user swipe left/right/up/down this widget.
	 * <p>For example,
<pre><code>
var opts = evt.opts, dir = opts.dir;
switch (dir) {
case 'left': doSwipeLeft(); break;
case 'right': doSwipeRight(); break;
case 'up': doSwipeUp(); break;
case 'down': doSwipeDown(); break;
}
</code></pre>
	 * To define swipe direction rather than default condition,
<pre><code>
var opts = evt.opts, start = opts.start, stop = opts.stop,
	dispT = stop.time - start.time,
	deltaX = start.coords[0] - stop.coords[0],
	deltaY = start.coords[1] - stop.coords[1],
	dispX = Math.abs(deltaX),
	dispY = Math.abs(deltaY);

//if swipe time is less than 500ms, it is considered as swipe event
if (dispT < 500) {
 	//if horizontal displacement is larger than 30px and vertical displacement is smaller than 75px, it is considered swipe left/right
	if (dispX > 30 && dispY < 75)
		//swipe left if deltaX > 0
	
	//if vertical displacement is large than 30px and horizontal displacement is smaller than 75px, it is considered swipe up/down
	else if (dispY > 30 && dispX < 75)
		//swipe up if deltaY > 0
}
</code></pre>
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doSwipe_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * @param zk.Event evt the widget event.
	 * @since 6.5.0
	 */
	doSwipe_: function(evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doSwipe_(evt);
		}
	},

	/** A utility to simplify the listening of <code>onFocus</code>.
	 * Unlike other doXxx_ (such as {@link #doClick_}), a widget needs to listen
	 * the onFocus event explicitly if it might gain and lose the focus.
	 * <p>For example,
<pre><code>
var fn = this.$n('focus');
this.domListen_(fn, 'onFocus', 'doFocus_');
this.domListen_(fn, 'onBlur', 'doBlur_');
</code></pre>
	 *<p>Of course, you can listen it with jQuery DOM-level utilities, if you pefer to handle it differently.
	 *
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doFocus_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doBlur_
	 */
	doFocus_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doFocus_(evt);
		}
	},
	/** A utility to simplify the listening of <code>onBlur</code>.
	 * Unlike other doXxx_ (such as {@link #doClick_}), a widget needs to listen
	 * the onBlur event explicitly if it might gain and lose the focus.
	 * <p>For example,
<pre><code>
var fn = this.$n('focus');
this.domListen_(fn, 'onFocus', 'doFocus_');
this.domListen_(fn, 'onBlur', 'doBlur_');
</code></pre>
	 *<p>Of course, you can listen it with jQuery DOM-level utilities, if you pefer to handle it differently.
	 *
	 * <p>Default: fire the widget event ({@link #fireX}), and
	 * call parent's doBlur_ if the event propagation is not stopped ({@link zk.Event#stopped}). 
	 * It is the so-called event propagation.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 * @param zk.Event evt the widget event.
	 * The original DOM event and target can be retrieved by {@link zk.Event#domEvent} and {@link zk.Event#domTarget} 
	 * @see #doFocus_
	 */
	doBlur_: function (evt) {
		if (!this.fireX(evt).stopped) {
			var p = this.parent;
			if (p) p.doBlur_(evt);
		}
	},

	//DOM event handling//
	/** Registers an DOM event listener for the specified DOM element (aka., node).
	 * You can use jQuery to listen the DOM event directly, or
	 * use this method instead.
<pre><code>
bind_: function () {
  this.$supers('bind_', arguments);
  this.domListen_(this.$n(), "onChange"); //fn is omitted, so _doChange is assumed
  this.domListen_(this.$n("foo"), "onSelect", "_doFooSelect"); //specify a particular listener
},
unbind_: function () {
  this.domUnlisten_(this.$n(), "onChange"); //unlisten
  this.domUnlisten_(this.$n("foo"), "onSelect", "_doFooSelect");
  this.$supers('unbind_', arguments);
},
_doChange_: function (evt) { //evt is an instance of zk.Event
  //event listener
},
_doFooSelect: function (evt) {
}
</code></pre>
	 * See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Notifications">ZK Client-side Reference: Notifications</a>
	 *
	 * <h3>Design Mode</h3>
	 * If a widget is created and controlled by ZK Weaver for visual design,
	 * we call the widget is in design mode ({@link #$weave}).
	 * Furthermore, this method does nothing if the widget is in the design mode.
	 * Thus, if you want to listen a DOM event ({@link jq.Event}), you have
	 * to use jQuery directly.
	 * @param DOMElement node a node of this widget.
	 * It is usually retrieved by {@link #$n}.
	 * @param String evtnm the event name to register, such as onClick.
	 * @param Object fn the name ({@link String}) of the member method to handle the event,
	 * or the function ({@link Function}).
	 * It is optional. If omitted, <i>_doEvtnm</i> is assumed, where <i>evtnm</i>
	 * is the value passed thru the <code>evtnm</code> argument.
	 * For example, if the event name is onFocus, then the method is assumed to be
	 * _doFocus.
	 * @return zk.Widget this widget
	 * @see #domUnlisten_
	 */
	domListen_: function (n, evtnm, fn) {
		if (!this.$weave) {
			var inf = _domEvtInf(this, evtnm, fn);
			jq(n, zk).bind(inf[0], inf[1]);
		}
		return this;
	},
	/** Un-registers an event listener for the specified DOM element (aka., node).
	 * <p>Refer to {@link #domListen_} for more information. 
	 * @param DOMElement node a node of this widget.
	 * It is usually retrieved by {@link #$n}.
	 * @param String evtnm the event name to register, such as onClick.
	 * @param Object fn the name ({@link String}) of the member method to handle the event,
	 * or the function ({@link Function}).
	 * It is optional. If omitted, <i>_doEvtnm</i> is assumed, where <i>evtnm</it>
	 * is the value passed thru the <code>evtnm</code> argument.
	 * For example, if the event name is onFocus, then the method is assumed to be
	 * _doFocus.
	 * @return zk.Widget this widget
	 * @see #domListen_
	 */
	domUnlisten_: function (n, evtnm, fn) {
		if (!this.$weave) {
			var inf = _domEvtInf(this, evtnm, fn);
			jq(n, zk).unbind(inf[0], inf[1]);
		}
		return this;
	},
	/**
	 * Listens to onFitSize event. Override if a subclass wants to skip listening
	 * or have extra processing. 
	 * @see #unlistenOnFitSize_
	 * @since 5.0.8
	 */
	listenOnFitSize_: function () {
		if (!this._fitSizeListened && (this._hflex == 'min' || this._vflex == 'min')){
			zWatch.listen({onFitSize: [this, zFlex.onFitSize]});
			this._fitSizeListened = true;
		}
	},
	/**
	 * Unlistens to onFitSize event. Override if a subclass wants to skip listening
	 * or have extra processing. 
	 * @see #listenOnFitSize_
	 * @since 5.0.8
	 */
	unlistenOnFitSize_: function () {
		if (this._fitSizeListened) {
			zWatch.unlisten({onFitSize: [this, zFlex.onFitSize]});
			delete this._fitSizeListened;
		}
	},
	/** Converts a coordinate related to the browser window into the coordinate
	 * related to this widget.
	 * @param int x the X coordinate related to the browser window
	 * @param int y the Y coordinate related to the browser window
	 * @return Offset the coordinate related to this widget (i.e., [0, 0] is
	 * the left-top corner of the widget).
	 * @since 5.0.2
	 */
	fromPageCoord: function (x, y) {
		var ofs = zk(this).revisedOffset();
		return [x - ofs[0], y - ofs[1]];
	},
	/** Returns if the given watch shall be fired for this widget.
	 * It is called by {@link zWatch} to check if the given watch shall be fired
	 * @param String name the name of the watch, such as onShow
	 * @param zk.Widget p the parent widget causing the watch event.
	 * It is null if it is not caused by {@link _global_.zWatch#fireDown}.
	 * @param Map cache a map of cached result (since 5.0.8). Ignored if null.
	 * If specified, the result will be stored and used to speed up the processing
	 * @return boolean
	 * @since 5.0.3
	 */
	isWatchable_: function (name, p, cache) {
		//if onShow, we don't check visibility since window uses it for
		//non-embedded window that becomes invisible because of its parent
		var strict = name != 'onShow', wgt;
		if (p)
			return this.isRealVisible({dom:true, strict:strict, until:p, cache: cache});

		for (wgt = this;;) {
			if (!wgt.$instanceof(zk.Native)) //if native, $n() might be null or wrong (if two with same ID)
				break;

			//Note: we check _visible only if native, since, when onHide is fired,
			//_visible is false but DOM element is visible (so it is watchable)
			if (!wgt._visible)
				return false;

			//it might be native or others, so we look up parent
			if (!(wgt = wgt.parent))
				return true; //consider as visible if it is root
		}

		return zk(wgt.$n()).isRealVisible(strict);
	},
	toJSON: function () { //used by JSON
		return this.uuid;
	},
	/** A widget call this function of its ancestor if it wants to know whether its ancestor prefer ignore float up event of it self.
	 * <p>Default: false.
	 * @return boolean
	 * @since 6.0.0
	 */
	ignoreDescendantFloatUp_: function (des) {
		return false;
	}

}, {
	/** Retrieves the widget.
	 * @param Object n the object to look for. If it is a string,
	 * it is assumed to be UUID, unless it starts with '$'.
	 * For example, <code>zk.Widget.$('uuid')<code> is the same as <code>zk.Widget.$('#uuid')<code>,
	 * and both look for a widget whose ID is 'uuid'. On the other hand,
	 * <code>zk.Widget.$('$id') looks for a widget whose ID is 'id'.<br/>
	 * If it is an DOM element ({@link DOMElement}), it will look up
	 * which widget it belongs to.<br/>
	 * If the object is not a DOM element and has a property called
	 * <code>target</code>, then <code>target</code> is assumed.
	 * Thus, you can pass an instance of {@link jq.Event} or {@link zk.Event},
	 * and the target widget will be returned.
	 * @param Map opts [optional] the options. Allowed values:
	 * <ul>
	 * <li>exact - id must exactly match uuid (i.e., uuid-xx ignored).
	 * It also implies strict (since 5.0.2)</li>
	 * <li>strict - whether not to look up the parent node.(since 5.0.2)
	 * If omitted, false is assumed (and it will look up parent).</li>
	 * <li>child - whether to ensure the given element is a child element
	 * of the widget's main element ({@link #$n}). In most cases, if ID
	 * of an element is xxx-yyy, the the element must be a child of
	 * the element whose ID is xxx. However, there is some exception
	 * such as the shadow of a window.</li>
	 * </ul>
	 * @return zk.Widget
	 */
	$: function (n, opts) {
		if (n && n.zk && n.zk.jq == n) //jq()
			n = n[0];

		if (!n || Widget.isInstance(n))
			return n;

		var wgt, id;
		if (typeof n == "string") {
		//Don't look for DOM (there might be some non-ZK node with same ID)
			if ((id = n.charAt(0)) == '#') n = n.substring(1);
			else if (id == '$') {
				id = _globals[n.substring(1)];
				return id ? id[0]: null;
			}
			wgt = _binds[n]; //try first (since ZHTML might use -)
			if (!wgt)
				wgt = (id = n.indexOf('-')) >= 0 ? _binds[n.substring(0, id)]: null;
			return wgt;
		}

		if (!n.nodeType) { //n could be an event (skip Element)
			var e1, e2;
			n = ((e1 = n.originalEvent) ? e1.z$target:null)
				|| ((e1 = n.target) && (e2 = e1.z$proxy) ? e2: e1) || n; //check DOM event first
		}

		opts = opts || {};
		if (opts.exact)
			return _binds[n.id];

		for (; n; n = zk(n).vparentNode(true)) {
			try {
				id = n.id || (n.getAttribute ? n.getAttribute("id") : '');
				if (id && typeof id == "string") {
					wgt = _binds[id]; //try first (since ZHTML might use -)
					if (wgt)
						return wgt;

					var j = id.indexOf('-');
					if (j >= 0) {
						wgt = _binds[id = id.substring(0, j)];
						if (wgt) {
							if (!opts.child)
								return wgt;

							var n2 = wgt.$n();
							if (n2 && jq.isAncestor(n2, n))
								return wgt;
						}
					}
				}
			} catch (e) { //ignore
			}
			if (opts.strict)
				break;
		}
		return null;
	},

	/** Called to mimic the mouse down event fired by the browser.
	 * It is used for implement a widget. In most cases, you don't need to
	 * invoke this method.
	 * <p>However, it is useful if the widget you are implemented will 'eat'
	 * the mouse-down event so ZK Client Engine won't be able to intercept it
	 * at the document level.
	 * @param zk.Widget wgt the widget that receives the mouse-down event
	 * @param boolean noFocusChange whether zk.currentFocus shall be changed to wgt. 
	 */
	mimicMouseDown_: function (wgt, noFocusChange) { //called by mount
		var modal = zk.currentModal;
		if (modal && !wgt) {
			var cf = zk.currentFocus;
			//Note: browser might change focus later, so delay a bit
			//(it doesn't work if we stop event instead of delay - IE)
			if (cf && zUtl.isAncestor(modal, cf)) cf.focus(0);
			else modal.focus(0);
		} else if (!wgt || wgt.canActivate()) {
			if (!noFocusChange) {
				zk._prevFocus = zk.currentFocus;
				zk.currentFocus = wgt;
				zk._cfByMD = true;
				setTimeout(function(){zk._cfByMD = false; zk._prevFocus = null;}, 0);
					//turn it off later since onBlur_ needs it
			}

			if (wgt)
				zWatch.fire('onFloatUp', wgt); //notify all
			else
				for (var dtid in zk.Desktop.all)
					zWatch.fire('onFloatUp', zk.Desktop.all[dtid]); //notify all
		}
	},
	/**
	 * Returns all elements with the given widget name.
	 * @param String name the widget name {@link #widgetName}.
	 * @return Array an array of {@link DOMElement}
	 * @since 5.0.2
	 */
	getElementsByName: function (name) {
		var els = [];
		for (var wid in _binds) {
			if (name == '*' || name == _binds[wid].widgetName) {
				var n = _binds[wid].$n(), w;
				//Bug B50-3310406 need to check if widget is removed or not.
				if (n && (w = Widget.$(_binds[wid]))) {
					els.push({
						n: n,
						w: w
					});
				}
			}
		}
		if (els.length) {
			// fixed the order of the component that have been changed dynamically.
			// (Bug in B30-1892446.ztl, B50-3095549.ztl, and B50-3131173.ztl)
			els.sort(function(a, b) {
				var w1 = a.w,
					w2 = b.w;
				// We have to compare each ancestor to make the result as CSS selector.
				// The performance is bad but it is only used for testing purpose.
				if (w1.bindLevel < w2.bindLevel) {
					do {
						w2 = w2.parent;
					} while (w1 && w1.bindLevel < w2.bindLevel);
				} else if (w1.bindLevel > w2.bindLevel) {
					do {
						w1 = w1.parent;
					} while (w2 && w1.bindLevel > w2.bindLevel);
				}
				var wp1 = w1.parent,
					wp2 = w2.parent;
				while (wp1 && wp2 && wp1 != wp2) {
					w1 = wp1;
					w2 = wp2;
					wp1 = wp1.parent;
					wp2 = wp2.parent;
				}
				if (w1 && w2) {
					return w1.getChildIndex() - w2.getChildIndex();
				}	
				return 0;
			});
			var tmp = [];
			for (var i = els.length; i--;)
				tmp.unshift(els[i].n);
			els = tmp;
		}
		return els;
	},
	/**
	 * Returns all elements with the given ID.
	 * @param String id the id of a widget, {@link #id}.
	 * @return Array an array of {@link DOMElement}
	 * @since 5.0.2
	 */
	getElementsById: function (id) {
		var els = [];
		for (var n, wgts = _globals[id], i = wgts?wgts.length:0; i--;) {
			n = wgts[i].$n();
			if (n) els.unshift(n);
		}
		return els;
	},

	//uuid//
	/** Converts an ID of a DOM element to UUID.
	 * It actually removes '-*'. For example, zk.Widget.uuid('z_aa-box') returns 'z_aa'. 
	 * @param String subId the ID of a DOM element
	 * @return String the uuid of the widget (notice that the widget might not exist)
	 */
	uuid: function (id) {
		var uuid = typeof id == 'object' ? id.id || '' : id,
			j = uuid.indexOf('-');
		return j >= 0 ? uuid.substring(0, j): id;
	},
	/** Returns the next unique UUID for a widget.
	 * The UUID is unique in the whole browser window and does not conflict with the peer component's UUID.
	 * <p>This method is called automatically if {@link #$init} is called without uuid.
	 * @return String the next unique UUID for a widget
	 */
	nextUuid: function () {
		return '_z_' + _nextUuid++;
	},

	/** @deprecated we cannot really detect at the client if UUID is generated automatically.
	 * @param String uuid the UUID to test
	 * @return boolean
	 */
	isAutoId: function (id) {
		return !id;
	},

	/** Registers a widget class.
	 * It is called automatically if the widget is loaded by WPD loader, so you rarely
	 * need to invoke this method.
	 * However, if you create a widget class at run time, you have to call this method explicitly.
	 * Otherwise, {@link #className}, {@link #getClass}, and {@link #newInstance}
	 * won't be applicable.
	 * <p>Notice that the class must be declared before calling this method.
	 * In other words, zk.$import(clsnm) must return the class of the specified class name.
<pre><code>
zk.Widget.register('foo.Cool'); //class name
zk.Widget.getClass('cool'); //widget name
</code></pre>
	 * @param String clsnm the class name, such as zul.wnd.Window
	 * @param boolean blankPreserved whether to preserve the whitespaces between child widgets when declared in iZUML. If true, a widget of clsnm will have a data member named blankPreserved (assigned with true). And, iZUML won't trim the whitespaces (aka., the blank text) between two adjacent child widgets. 
	 */
	register: function (clsnm, blankprev) {
		var cls = zk.$import(clsnm);
		cls.prototype.className = clsnm;
		var j = clsnm.lastIndexOf('.');
		if (j >= 0) clsnm = clsnm.substring(j + 1);
		_wgtcls[cls.prototype.widgetName = clsnm.toLowerCase()] = cls;
		if (blankprev) cls.prototype.blankPreserved = true;
	},
	/** Returns the class of the specified widget's name. For example,
<pre><code>
zk.Widget.getClass('combobox');
</code></pre>
	 *<p>Notice that null is returned if the widget is not loaded (or not exist) yet. 
	 * @param String wgtnm the widget name, such as textbox.
	 * @return zk.Class the class of the widget.
	 * @see #newInstance
	 * @see #register
	 */
	getClass: function (wgtnm) {
		return _wgtcls[wgtnm];
	},
	/** Creates a widget by specifying the widget name.
	 * The widget name is the last part of the class name of a widget (and converting the first letter to lower case).
	 * For example, if a widget's class name is zul.inp.Textbox, then the widget name is textbox.
	 * <p>This method is usually used by tools, such as zk.zuml.Parser, rather than developers, since developers can create the widget directly if he knows the class name. 
	 * @param String wgtnm the widget name, such as textbox.
	 * @param Map props [optional] the properties that will be passed to
	 * {@link #$init}.
	 * @see #getClass
	 * @see #register
	 * @return zk.Widget
	 */
	newInstance: function (wgtnm, props) {
		var cls = _wgtcls[wgtnm];
		if (!cls) {
			zk.error(cls = 'Unknown widget: '+wgtnm);
			throw cls;
		}
		return new cls(props);
	},
	/** The default delay before sending an AU request when {@link #fire}
	 * is called (and the server has an ARAP event listener registered).
	 * <p>Default: 38 (Unit: miliseconds).
	 * @since 5.0.8
	 * @type int
	 */
	auDelay: 38
});
zkreg = Widget.register; //a shortcut for WPD loader

/** A reference widget. It is used as a temporary widget that will be
 * replaced with a real widget when {@link #bind_} is called.
 * <p>Developers rarely need it.
 * Currently, it is used only for the server to generate the JavaScript codes
 * for mounting.
 * @disable(zkgwt)
 */
zk.RefWidget = zk.$extends(zk.Widget, {
	/** The class name (<code>zk.RefWidget</code>).
	 * @type String
	 * @since 5.0.3
	 */
	className: "zk.RefWidget",
	/** The widget name (<code>refWidget</code>).
	 * @type String
	 * @since 5.0.3
	 */
	widgetName: "refWidget",
	bind_: function () {
		var w = Widget.$(this.uuid);
		if (!w) {
			zk.error("RefWidget not found: " + this.uuid);
			return;
		}

		var p;
		if (p = w.parent) //shall be a desktop
			_unlink(p, w); //unlink only

		_replaceLink(this, w);
		this.parent = this.nextSibling = this.previousSibling = null;

		_addIdSpaceDown(w); //add again since parent is changed

		//no need to call super since it is bound
	}
});

//desktop//
/** A desktop.
 * Unlike the component at the server, a desktop is a widget.
 * <p>However, the desktop are different from normal widgets:
 * <ol>
 * <li>The desktop is a conceptual widget. It is never attached with the DOM tree. Its desktop field is always null. In addition, calling zk.Widget#appendChild won't cause the child to be attached to the DOM tree automatically.</li>
 * <li>The desktop's ID and UUID are the same. </li>
 * </ol>
 * @disable(zkgwt)
 */
zk.Desktop = zk.$extends(zk.Widget, {
	//a virtual node that might have no DOM node and must be handled specially
	z_virnd: true,

	bindLevel: 0,
	/** The class name (<code>zk.Desktop</code>).
	 * @type String
	 */
	className: "zk.Desktop",
	/** The widget name (<code>desktop</code>).
	 * @type String
	 * @since 5.0.2
	 */
	widgetName: "desktop",
	/** The request path.
	 * @type String
	 */
	//requestPath: null,
	
	/** Constructor
	 * @param String dtid the ID of the desktop
	 * @param String contextURI the context URI, such as <code>/zkdemo</code>
	 * @param String updateURI the URI of ZK Update Engine, such as <code>/zkdemo/zkau</code>
	 * @param String reqURI the URI of the request path.
	 * @param boolean stateless whether this desktop is used for a stateless page.
	 * Specify true if you want to use <a href="http://books.zkoss.org/wiki/Small_Talks/2009/July/ZK_5.0_and_Client-centric_Approach">the client-centric approach</a>.
	 */
	$init: function (dtid, contextURI, updateURI, reqURI, stateless) {
		this.$super('$init', {uuid: dtid}); //id also uuid

		var Desktop = zk.Desktop, dts = Desktop.all, dt;

		this._aureqs = [];
		//Sever side effect: this.desktop = this;

		if (dt = dts[dtid]) {
			if (updateURI != null) dt.updateURI = updateURI;
			if (contextURI != null) dt.contextURI = contextURI;
		} else {
			this.uuid = this.id = dtid;
			this.updateURI = updateURI != null ? updateURI: zk.updateURI;
			this.contextURI = contextURI != null ? contextURI: zk.contextURI;
			this.requestPath = reqURI || '';
			this.stateless = stateless;
			dts[dtid] = this;
			++Desktop._ndt;
		}

		Desktop._dt = dt||this; //default desktop
		Desktop.sync(60000); //wait since liferay on IE delays the creation
	},
	bind_: zk.$void,
	unbind_: zk.$void,
	/** This method is voided (does nothing) since the desktop's ID
	 * can be changed.
	 * @param String id the ID
	 * @return zk.Widget this widget
	 */
	setId: zk.$void
},{
	/** Returns the desktop of the specified desktop ID, widget, widget UUID, or DOM element.
	 * <p>Notice that the desktop's ID and UUID are the same.
	 * @param Object o a desktop's ID, a widget, a widget's UUID, or a DOM element.
	 * If not specified, the default desktop is assumed.
	 * @return zk.Desktop
	 */
	$: function (dtid) {
		var Desktop = zk.Desktop, w;
		if (dtid) {
			if (Desktop.isInstance(dtid))
				return dtid;

			w = Desktop.all[dtid];
			if (w)
				return w;

			w = Widget.$(dtid);
			for (; w; w = w.parent) {
				if (w.desktop)
					return w.desktop;
				if (w.$instanceof(Desktop))
					return w;
			}
			return null;
		}

		if (w = Desktop._dt)
			return w;
		for (dtid in Desktop.all)
			return Desktop.all[dtid];
	},
	/** A map of all desktops (readonly).
	 * The key is the desktop ID and the value is the desktop.
	 * @type Map
	 */
	all: {},
	_ndt: 0, //used in au.js/dom.js
	/** Checks if any desktop becomes invalid, and removes the invalid desktops.
	 * This method is called automatically when a new desktop is added. Application developers rarely need to access this method.
	 * @param int timeout how many miliseconds to wait before doing the synchronization
	 * @return zk.Desktop the first desktop, or null if no desktop at all. 
	 */
	sync: function (timeout) {
		var Desktop = zk.Desktop, dts = Desktop.all, dt;

		if (_syncdt) {
			clearTimeout(_syncdt);
			_syncdt = null;
		}

		if (timeout >= 0)
			_syncdt = setTimeout(function () {
				_syncdt = null;
				Desktop.sync();
			}, timeout); //Liferay on IE will create widgets later
		else {
			for (var dtid in dts)
				if (!_exists(dt = dts[dtid]) && dt.firstChild) { //to be safe, don't remove if no child)
					delete dts[dtid];
					--Desktop._ndt;
					if (Desktop._dt == dt)
						Desktop._dt = null;
					zAu._rmDesktop(dt);
				}

			if (!Desktop._dt)
				for (var dtid in dts) {
					Desktop._dt = dts[dtid];
					break;
				}
		}
		return Desktop._dt;
	}
});

zk._wgtutl = { //internal utilities
	setUuid: function (wgt, uuid) { //called by au.js
		if (!uuid)
			uuid = Widget.nextUuid();
		if (uuid != wgt.uuid) {
			var n = wgt.$n();
			if (n) {
				//Note: we assume RawId doesn't have sub-nodes
				if (!wgt.rawId)
					throw 'id immutable after bound'; //might have subnodes
				n.id = uuid;
				delete _binds[wgt.uuid];
				_binds[uuid] = wgt;
				wgt.clearCache();
			}
			wgt.uuid = uuid;
		}
	},
	//kids: whehter to move children of from to to
	replace: function (from, to, kids) { //called by mount.js
		_replaceLink(from, to);
		from.parent = from.nextSibling = from.previousSibling = null;

		if (kids) {
			to.lastChild = from.lastChild;
			for (var p = to.firstChild = from.firstChild; p; p = p.nextSibling)
				p.parent = to;
			to.nChildren = from.nChildren;
			from.firstChild = from.lastChild = null;
			from.nChildren = 0;
		}
		from.nChildren = 0;
	},

	autohide: function () { //called by effect.js
		if (!_floatings.length) {
			for (var n; n = _hidden.shift();)
				n.style.visibility = n.getAttribute('z_ahvis')||'';
			return;
		}

		for (var tns = ['IFRAME', 'APPLET'], i = 2; i--;)
			l_nxtel:
			for (var ns = document.getElementsByTagName(tns[i]), j = ns.length; j--;) {
				var n = ns[j], $n = zk(n), visi;
				if ((!(visi=$n.isVisible(true)) && !_hidden.$contains(n))
				|| (!i && !n.getAttribute("z_autohide") && !n.getAttribute("z.autohide"))) //check z_autohide (5.0) and z.autohide (3.6) if iframe
					continue; //ignore

				var tc = _topnode(n);
				function hide(f) {
					var tf = _topnode(f);
					if (tf == tc || _zIndex(tf) < _zIndex(tc) || !$n.isOverlapped(f))
						return;

					if (visi) {
						_hidden.push(n);
						try {
							n.setAttribute('z_ahvis', n.style.visibility);
						} catch (e) {
						}
						n.style.visibility = 'hidden';
					}
					return true; //processed
				}

				for (var k = _floatings.length; k--;)
					if (hide(_floatings[k].node))
						continue l_nxtel;

				if (_hidden.$remove(n))
					n.style.visibility = n.getAttribute('z_ahvis')||'';
			}
	}
};
})();

/** A page.
 * Unlike the component at the server, a page is a widget.
 * @disable(zkgwt)
*/
zk.Page = zk.$extends(zk.Widget, {
	//a virtual node that might have no DOM node and must be handled specially
	z_virnd: true,

	_style: "width:100%;height:100%",
	/** The class name (<code>zk.Page</code>).
	 * @type String
	 */
	className: "zk.Page",
	/** The widget name (<code>page</code>).
	 * @type String
	 * @since 5.0.2
	 */
	widgetName: "page",

	/** Constructor.
	 * @param Map props the properties to assign to this page
	 * @param boolean contained whether this page is contained.
	 * By contained we mean this page is a top page (i.e., not included
	 * by the include widget) but it is included by other technologies,
	 * such as JSP.
	 */
	$init: function (props, contained) {
		this._fellows = {};

		this.$super('$init', props);

		if (contained) zk.Page.contained.push(this);
	},
	/** Generates the HTML fragment for this macro component.
	 * <p>Default: it generate DIV to enclose the HTML fragment
	 * of all child widgets.
	 * @param Array out an array of HTML fragments.
	 */
	redraw: _zkf = function (out) {
		out.push('<div', this.domAttrs_(), '>');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</div>');
	}
},{
	$redraw: _zkf,
	/** An array of contained pages (i.e., a standalone ZK page but included by other technology).
	 * For example, a ZUL age that is included by a JSP page.
	 * A contained page usually covers a portion of the browser window. 
	 * @type Array an array of contained pages ({@link zk.Page})
	 */
	contained: []
});
zkreg('zk.Page', true);

//a fake page used in circumstance that a page is not available ({@link #getPage})
zk.Body = zk.$extends(zk.Page, {
	$init: function (dt) {
		this.$super('$init', {});
		this.desktop = dt;
	},
	$n: function (subId) {
		return subId ? null: document.body;
	},
	redraw: zk.$void
});
/** A native widget.
 * It is used mainly to represent the native componet created at the server.
 * @disable(zkgwt)
 */
zk.Native = zk.$extends(zk.Widget, {
	//a virtual node that might have no DOM node and must be handled specially
	z_virnd: true,

	/** The class name (<code>zk.Native</code>)
	 * @type String
	 */
	className: "zk.Native",
	/** The widget name (<code>native</code>).
	 * @type String
	 * @since 5.0.2
	 */
	widgetName: "native",
	//rawId: true, (Bug 3358505: it cannot be rawId)

	$n: function (subId) {
		return !subId && this.id ? jq('#' + this.id):
			this.$supers('$n', arguments); // Bug ZK-606/607
	},
	redraw: _zkf = function (out) {
		var s = this.prolog, p;
		if (s) {
			//Bug ZK-606/607: hflex/vflex and many components need to know
			//child.$n(), so we have to generate id if the parent is not native
			//(and no id is assigned) (otherwise, zk.Native.$n() failed)
			if (this.$instanceof(zk.Native) //ZK-745
			&& !this.id && (p=this.parent) && !p.z_virnd) { //z_virnd implies zk.Native, zk.Page and zk.Desktop
				var j = 0, len = s.length, cond, cc;
				for (cond = {whitespace:1}; j < len; ++j) {
					if ((cc = s.charAt(j)) == '<')
						break; //found
					if (!zUtl.isChar(cc, cond)) {
						j = len; //not recognized => don't handle
						break;
					}
				}
				if (j < len) {
					cond = {upper:1,lower:1,digit:1,'-':1};
					while (++j < len)
						if (!zUtl.isChar(s.charAt(j), cond))
							break;
					s = s.substring(0, j) + ' id="' + this.uuid + '"' + s.substring(j); 
				}
			}

			out.push(s);
			if (this.value && s.startsWith("<textarea"))
				out.push(this.value);
		}

		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);

		s = this.epilog;
		if (s) out.push(s);
	}
}, {
	$redraw: _zkf
});

/** A macro widget.
 * It is used mainly to represent the macro componet created at the server.
 */
zk.Macro = zk.$extends(zk.Widget, {
	/** The class name (<code>zk.Macro</code>).
	 * @type String
	 */
	className: "zk.Macro",
	/** The widget name (<code>macro</code>).
	 * @type String
	 * @since 5.0.2
	 */
	widgetName: "macro",
	_enclosingTag: "span",

	$init: function () {
		this._fellows = {};
		this.$supers('$init', arguments);
	},
	$define: {
		/** Returns the tag name for this macro widget.
		 * <p>Default: span
		 * @return String the tag name (such as div or span)
		 * @since 5.0.3
		 */
		/** Sets the tag name for this macro widget
		 * @param String tag the tag name, such as div
		 * @since 5.0.3
		 */
		enclosingTag: function () {
			this.rerender();
		}
	},

	/** Generates the HTML fragment for this macro component.
	 * <p>Default: it generate SPAN to enclose the HTML fragment
	 * of all child widgets.
	 * @param Array out an array of HTML fragments (String).
	 */
	redraw: function (out) {
		var style = ' style="display: inline-block; min-width: 1px;"';
		out.push('<', this._enclosingTag, this.domAttrs_(), style, '>'); //Bug ZK-1433: add style to pass isWatchable_
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</', this._enclosingTag, '>');
	}
});

/** A skipper is an object working with {@link zk.Widget#rerender}
 * to rerender portion(s) of a widget (rather than the whole widget).
 * It can improve the performance a lot if it can skip a lot of portions, such as a lot of child widgets. 
 * <p>The skipper decides what to skip (i.e., not to rerender), detach the skipped portion(s), and attach them back after rerendering. Thus, the skipped portion won't be rerendered, nor unbound/bound.
 * <p>The skipper has to implement three methods, {@link #skipped},
 * {@link #skip} and {@link #restore}. {@link #skipped} is used to test whether a child widget shall be skipped.
 * {@link #skip} and {@link #restore} works together to detach and attach the skipped portions from the DOM tree. Here is how
 * {@link zk.Widget#rerender} uses these two methods:
<pre><code>
rerender: function (skipper) {
  var skipInfo;
  if (skipper) skipInfo = skipper.skip(this);
 
  this.replaceHTML(this.node, null, skipper);
 
  if (skipInfo) skipper.restore(this, skipInfo);
}
</code></pre>
 * <p>Since {@link zk.Widget#rerender} will pass the returned value of {@link #skip} to {@link #restore}, the skipper doesn't need to store what are skipped. That means, it is possible to have one skipper to serve many widgets. {@link #nonCaptionSkipper} is a typical example.
 * <p>In additions to passing a skipper to {@link zk.Widget#rerender}, the widget has to implement the mold method to handle the skipper:
<pre><code>
function (skipper) {
 var html = '<fieldset' + this.domAttrs_() + '>',
 cap = this.caption;
 if (cap) html += cap.redraw();
 
 html += '<div id="' + this.uuid + '$cave"' + this._contentAttrs() + '>';
 if (!skipper)
  for (var w = this.firstChild; w; w = w.nextSibling)
   if (w != cap) html += w.redraw();
 return html + '</div></fieldset>';
}
</pre></code>
 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Component Development/Server-side/Property_Rendering">ZK Client-side Reference: Property Rendering</a>.
 * @disable(zkgwt)
 */
zk.Skipper = zk.$extends(zk.Object, {
	/** Returns whether the specified child widget will be skipped by {@link #skip}.
	 * <p>Default: returns if wgt.caption != child. In other words, it skip all children except the caption. 
	 * @param zk.Widget wgt the widget to re-render
	 * @param zk.Widget child a child (descendant) of this widget.
	 * @return boolean
	 */
	skipped: function (wgt, child) {
		return wgt.caption != child;
	},
	/** Skips all or subset of the descendant (child) widgets of the specified widget.
	 * <p>Notice that the <pre>skipId</pre> argument is not used by {@link zk.Widget#rerender}.
	 * Rather it is used to simplify the overriding of this method,
	 * such that the deriving class can call back this class and
	 * to pass a different ID to skip
	 *
	 * <p>If you don't want to pass a different ID (default: uuid + '-cave'),
	 * you can ignore <code>skipId</code>
<pre><code>
Object skip(zk.Widget wgt);
</code></pre>
	 * <p>Default: it detaches all DOM elements whose parent element is
	 * <code>jq(skipId || (wgt.uuid + '-cave'), zk)</code>. 
	
	 * @param zk.Widget wgt the widget being rerendered.
	 * @param String skipId [optional] the ID of the element where all its descendant
	 * elements shall be detached by this method, and restored later by {@link #restore}. 
	 * If not specified, <code>uuid + '-cave'</code> is assumed.
	 * @return DOMElement
	 */
	skip: function (wgt, skipId) {
		var skip = jq(skipId || wgt.getCaveNode(), zk)[0];
		if (skip && skip.firstChild) {
			var cf = zk.currentFocus,
				iscf = cf && cf.getInputNode;
			
			if (iscf && zk.ie) //Bug ZK-1377 IE will lost input selection range after remove node
				zk.cfrg = zk(cf.getInputNode()).getSelectionRange();
			
			skip.parentNode.removeChild(skip);
				//don't use jq to remove, since it unlisten events
			
			if (iscf && zk.chrome) //Bug ZK-1377 chrome will lost focus target after remove node
				zk.currentFocus = cf;
			
			return skip;
		}
	},
	/** Restores the DOM elements that are detached (i.e., skipped) by {@link #skip}. 
	 * @param zk.Widget wgt the widget being re-rendered
	 * @param Object inf the object being returned by {@link #skip}.
	 * It depends on how a skipper is implemented. It is usually to carry the information about what are skipped 
	 */
	restore: function (wgt, skip) {
		if (skip) {
			var loc = jq(skip.id, zk)[0];
			for (var el; el = skip.firstChild;) {
				skip.removeChild(el);
				loc.appendChild(el);

				zjq._fixIframe(el); //in domie.js, Bug 2900274
			}
		}
	}
});
/** @partial zk.Skipper
 */
//@{
	/** An instance of {@link zk.Skipper} that can be used to skip the rerendering of child widgets except the caption.
	 * <p>It assumes
	 * <ol>
	 * <li>The child widget not to skip can be found by the caption data member.</li>
	 * <li>The DOM elements to skip are child elements of the DOM element whose ID is widgetUUID$cave, where widgetUUID is the UUID of the widget being rerendered. </li>
	 * </ol>
	 * <p>In other words, it detaches (i.e., skipped) all DOM elements under widget.$n('cave').
<pre><code>
setClosable: function (closable) {
 if (this._closable != closable) {
  this._closable = closable;
  if (this.node) this.rerender(zk.Skipper.nonCaptionSkipper);
 }
}
</pre></code>
	 * @type zk.Skipper
	 */
	//nonCaptionSkipper: null
//@};
zk.Skipper.nonCaptionSkipper = new zk.Skipper();

//Extra//

function zkopt(opts) {
	for (var nm in opts) {
		var val = opts[nm];
		switch (nm) {
		case "pd": zk.procDelay = val; break;
		case "td": zk.tipDelay =  val; break;
		case "art": zk.resendTimeout = val; break;
		case "dj": zk.debugJS = val; break;
		case "kd": zk.keepDesktop = val; break;
		case "pf": zk.pfmeter = val; break;
		case "ta": zk.timerAlive = val; break;
		case "gd": zk.groupingDenied = val; break;
		case "to":
			zk.timeout = val;
			zAu._resetTimeout();
			break;
		case "ed":
			switch (val) {
			case 'e':
				zk.feature.ee = true;
			case 'p':
				zk.feature.pe = true;
			}
			break;
		case 'eu': zAu.setErrorURI(val); break;
		case 'ppos': zk.progPos = val; break;
		case 'eup': zAu.setPushErrorURI(val);
		}
	}
}
