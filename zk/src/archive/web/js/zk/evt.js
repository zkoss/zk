/* evt.js

	Purpose:
		ZK Event and ZK Watch
	Description:
		
	History:
		Thu Oct 23 10:53:17     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The class representing a widget event (aka., a ZK event).
 * A widget event is the widget-level event that a widget can fire and the client application can listen.
 *
 * <p>On the other hand, a DOM event ({@link jq.Event}) is the low-level event
 * related to DOM elements. It is usually listened by the implementation of a widget, rather than the client application.
 *
 * <p> In additions, {@link zWatch} is an utility to manage the system-level events, 
 * and both the client application and the widget implementation might listen.
 *
 * <p>To fire a widget event, use {@link zk.Widget#fire}.
 * To listen a widget event, use {@link zk.Widget#listen}. 
 *
 * <p>See Also
 * <ul>
 * <li><a href="http://docs.zkoss.org/wiki/AU_Request">AU Request</a> - a full list of built-in AU requests (aka., widget events)</li>
 * <li><a href="http://docs.zkoss.org/wiki/AU_Response">AU Response</a> - a full list of built-in AU responses.</li>
 * <li><a href="http://docs.zkoss.org/wiki/Client_Watches">Watches</a> - a full list of watches.</li>
 * <li><a href="http://docs.zkoss.org/wiki/Widget_and_DOM_Events#Listen_By_Overriding">Widget and DOM Events</a></li>
 * <li><a href="http://docs.zkoss.org/wiki/How_to_Process_Request_with_JSON">How to Process Request with JSON</a></li>
 * <li><a href="http://docs.zkoss.org/wiki/CDG5:_Event_Data">Event Data</a></li>
 * </ul>
 * <p>Common Key Codes:
 * <ul>
 * <li>BACKSSPACE: 8</li>
 * <li>TAB: 9</li>
 * <li>ENTER: 13</li>
 * <li>SHIFT: 16</li>
 * <li>CTRL: 17</li>
 * <li>ALT: 18</li>
 * <li>ESC: 27</li>
 * <li>PGUP: 33</li>
 * <li>PGDN: 34</li>
 * <li>END: 35</li>
 * <li>HOME: 36</li>
 * <li>LEFT: 37</li>
 * <li>UP: 38</li>
 * <li>RIGHT: 39</li>
 * <li>DOWN: 40</li>
 * <li>INS: 45</li>
 * <li>DEL: 46</li>
 * <li>F1: 112</li>
 * </ul>
*/
zk.Event = zk.$extends(zk.Object, {
	/** The target widget (readonly).
	 * @type zk.Widget 
	 * @see #currentTarget
	 */
	//target: null,
	/** Indicates the target which is handling this event.
	 * <p>By default, an event will be propagated to its parent, and this member tells which widget is handling it, while #target is the widget that the event is targeting.
 	 * @type zk.Widget
 	 * @see #target
 	 */
 	//currentTarget: null,
 	/** The event name, such as 'onChange'.
 	 * The data which depends on the event. Here is the list of Event Data.
	 * <p>However, if data is an instance of Map, its content is copied to the event instance. Thus, you can access them directly with the event instance as follows.
<pre><code>
onClick: function (evt) {
  if (evt.altKey) { //it is the same as evt.data.altKey
  }
}
</code></pre>
 	 * @type String
 	 */
 	//name: null,
 	/** The data which depends on the event. Here is the list of Event Data.
	 * <p>However, if data is an instance of Map, its content is copied to the event instance. Thus, you can access them directly with the event instance as follows.
<pre><code>
onClick: function (evt) {
  if (evt.altKey) { //it is the same as evt.data.altKey
  }
}
</code></pre>
	 * <p>Refer to How to <a href="http://docs.zkoss.org/wiki/How_to_Process_Request_with_JSON">How to Process Request with JSON</a>
	 * and <a href="http://docs.zkoss.org/wiki/CDG5:_Event_Data">Event Data</a> for more information.
	 * @type Object
 	 */
 	//data: null,
 	/** The options.
	 * <p>Allowed properties:
	 * <ul>
	 * <li>implicit: whether this event is an implicit event, i.e., whether it is implicit to users (so no progressing bar).</li>
	 * <li>ignorable: whether this event is ignorable, i.e., whether to ignore any error of sending this event back the server.
		<ul><li>An ignorable event is also an imiplicit event.</li></ul></li>
	 * <li>ctl: whether it is a control, such as onClick, rather than a notification for status change.</li>
	 * <li>toServer: whether to send this event to the server. If specified, it is always sent no matter the widget is created at the server or not.</li>
	 * <li>uri: the URI to send the Ajax request to. If not specified, zAu#comURI is used (i.e., the desktop's update URI is used). If specified, the URI specified in this option is used -- notice that there is no encoding at all, so make sure it is correct. </li>
	 * </ul>
     * @type Map
     */
 	//opts: null,
 	/** The DOM event that causes this widget event, or null if not available.
 	 * @type jq.Event
 	 */
 	//domEvent: null,
 	/** The DOM element that the event is targeting, or null if not available.
 	 * @type DOMElement
 	 */
 	//domTarget: null,
 	/** Indicates whether the event propagation is stopped. 
 	 * @type boolean
 	 * @see #stop
 	 * @see #auStopped
 	 * @see #domStopped
 	 */
 	//stopped: false,
 	/** Indicates whether to stop the sending of the AU request to the server.
 	 * @type boolean
 	 * @see #stop
 	 * @see #stopped
 	 * @see #domStopped
 	 */
 	//auStopped: false,
 	/** Indicates whether to stop the native DOM event.
 	 * @type boolean
 	 * @see #stop
 	 * @see #stopped
 	 * @see #auStopped
 	 */
 	//domStopped: false,

	/** Constructor.
	 * @param zk.Widget target the target widget.
	 * @param String name the event name, such as onClick
	 * @param Object data [optional] the data depending on the event.
	 * Here is a list of <a href="http://docs.zkoss.org/wiki/CDG5:_Event_Data">Event Data</a>
	 * @param Map opts [optional] the options. Refer to {@link #opts}
	 * @param jq.Event domEvent [optional] the DOM event that causes this widget event.
	 */
	$init: function (target, name, data, opts, domEvent) {
		this.currentTarget = this.target = target;
		this.name = name;
		this.data = data;
		if (data && typeof data == 'object' && !jq.isArray(data))
			zk.$default(this, data);

		this.opts = opts||{};
		if (this.domEvent = domEvent)
			this.domTarget = domEvent.target;
	},
	/** Adds the additions options to {@link #opts}.
	 * @param Map opts a map of options to append to #opts 
	 */
	addOptions: function (opts) {
		this.opts = zk.copy(this.opts, opts);
	},
	/** Stop the event propagation.
<pre><code>
evt.stop();
evt.stop({propagation:true}); //stop only the event propagation (see below)
evt.stop({au:true}); //stop only the sending of the AU request
</code></pre>
	 *<p>If you want to revoke the stop of the event propagation, you can specify {revoke:true} to the opts argument.
<pre><code>
evt.stop({progagation:true,revoke:true}); //revoke the event propagation
</code></pre>
    * <p>Notice that the event won't be sent to the server if stop() was called. 
	*
	* @param Map opts [optional] control what to stop.
	* If omitted, the event propagation ({@link #stopped}) and the native DOM event ({@link #domStopped}) are both stopped (but not {@link #serverStop}).
	* For fine control, you can use a combination of the following values:
	<ul>
	<li>revoke - revoke the stop, i.e., undo the last invocation of {@link #stop}<li>
	<li>propagation - stop (or revoke) the event propagation ({@link #stopped}).<li>
	<li>dom - stop (or revoke) the native DOM event ({@link #domStopped}).</li>
	<li>au - stop (or revoke) the sending of the AU request to the server ({@link #auStopped}).
	Notice that, unlike the propagation and dom options, the sending of AU requests won't be stopped if opts is omitted.
	In other words, to stop it, you have to specify the au option explicitly. </li>
	</ul>
	*/
	stop: function (opts) {
		var b = !opts || !opts.revoke;
		if (!opts || opts.propagation) this.stopped = b;
		if (!opts || opts.dom) this.domStopped = b;
		if (opts && opts.au) this.auStopped = b;
	}
});

zWatch = (function () {
	var _visiEvts = {onSize: true, onShow: true, onHide: true, beforeSize: true},
		_watches = {}, //Map(watch-name, [watch objects]
		_dirty,
		_Gun = zk.$extends(zk.Object, {
			$init: function (name, infs, args, org) {
				this.name = name;
				this.infs = infs;
				this.args = args;
				this.origin = org;
			},
			fire: function (ref) {
				var inf,
					name = this.name,
					infs = this.infs,
					args = this.args;
				if (ref) {
					for (var j = 0, l = infs.length; j < l; ++j)
						if (_target(inf = infs[j]) == ref) {
							infs.splice(j, 1);
							_invoke(name, inf, args);
						}
				} else
					while (inf = infs.shift())
						_invoke(name, inf, args);
			},
			fireDown: function (ref) {
				if (!ref || ref.bindLevel == null)
					this.fire(ref);

				(new _Gun(this.name, _visiChildSubset(this.name, this.infs, ref, true), this.args, this.origin))
				.fire();
			}
		});

	function _invoke(name, inf, args) {
		var o = _target(inf);
		_fn(inf, o, name).apply(o, args);
	}
	//Returns if c is visible
	function _visible(name, c) {
		var n;
		return c.$n && (n=c.$n()) && zk(n).isRealVisible(name!='onShow');
		//if onShow, we don't check visibility since window uses it for
		//non-embedded window that becomes invisible because of its parent
	}
	//Returns if c is a visible child of p
	function _visibleChild(name, p, c) {
		if (_visible(name, c))
			for (; c; c = c.parent)
				if (p == c) return true;
		return false;
	}
	//Returns subset of infs that are visible childrens of p
	function _visiChildSubset(name, infs, p, remove) {
		var found = [], bindLevel = p.bindLevel;
		for (var j = infs.length; j--;) { //child first
			var inf = infs[j],
				o = _target(inf),
				diff = bindLevel > o.bindLevel;
			if (diff) break;//nor ancestor, nor this (&sibling)
			if ((p == o && _visible(name, o)) || _visibleChild(name, p, o)) {
				if (remove) infs.splice(j, 1);
				found.unshift(inf); //parent first
			}
		}
		return found;
	}
	function _visiSubset(name, infs) {
		infs = infs.$clone(); //make a copy since unlisten might happen
		if (_visiEvts[name])
			for (var j = infs.length; j--;)
				if (!_visible(name, _target(infs[j])))
					infs.splice(j, 1);
		return infs;
	}
	function _target(inf) {
		return jq.isArray(inf) ? inf[0]: inf;
	}
	function _fn(inf, o, name) {
		var fn = jq.isArray(inf) ? inf[1]: o[name];
		if (!fn)
			throw name + ' not defined in '+(o.className || o);
		return fn;
	}
	function _sync() {
		if (!_dirty) return;

		_dirty = false;
		for (var nm in _watches) {
			var wts = _watches[nm];
			if (wts.length && _target(wts[0]).bindLevel != null)
				wts.sort(_cmpLevel);
		}
	}
	function _cmpLevel(a, b) {
		return _target(a).bindLevel - _target(b).bindLevel;
	}
	function _zsync(name) {
		if (name == 'onSize' || name == 'onShow' || name == 'onHide')
			jq.zsync();
	}
	function _fire(name, org, opts, vararg) {
		var wts = _watches[name];
		if (wts && wts.length) {
			var down = opts && opts.down && org.bindLevel != null;
			if (down) _sync();

			var args = [],
				gun = new _Gun(name,
					down ? _visiChildSubset(name, wts, org): _visiSubset(name, wts),
					args, org);
			args.push(gun);
			for (var j = 3, l = vararg.length; j < l;)
				args.push(vararg[j++]);

			if (opts && opts.timeout >= 0)
				setTimeout(function () {gun.fire();_zsync(name);}, opts.timeout);
			else {
				gun.fire();
				_zsync(name);
			}
		}
	}

  return {
	listen: function (infs) {
		for (var name in infs) {
			var wts = _watches[name],
				inf = infs[name];
			if (wts) {
				var bindLevel = _target(inf).bindLevel;
				if (bindLevel != null) {
					for (var j = wts.length;;) {
						if (--j < 0) {
							wts.unshift(inf);
							break;
						}
						if (bindLevel >= _target(wts[j]).bindLevel) { //parent first
							wts.splice(j + 1, 0, inf);
							break;
						}
					}
				} else
					wts.push(inf);
			} else
				wts = _watches[name] = [inf];
		}
		return this;
	},
	unlisten: function (infs) {
		for (var name in infs) {
			var wts = _watches[name];
			wts && wts.$remove(infs[name]);
		}
		return this;
	},
	unlistenAll: function (name) {
		delete _watches[name];
	},
	fire: function (name, org, opts) {
		_fire(name, org, opts, arguments);
	},
	fireDown: function (name, org, opts) {
		_fire(name, org, zk.copy(opts,{down:true}), arguments);
	},
	onBindLevelMove: function () {
		_dirty = true;
	}
  };
})();
zWatch.listen({onBindLevelMove: zWatch});
