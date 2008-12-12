/* domevt.js

	Purpose:
		DOM Event, ZK Event and ZK Watch
	Description:
		
	History:
		Thu Oct 23 10:53:17     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
zEvt = {
	BS:		8,
	TAB:	9,
	ENTER:	13,
	SHIFT:	16,
	CTRL:	17,
	ALT:	18,
	ESC:	27,
	PGUP:	33,
	PGDN:	34,
	END:	35,
	HOME:	36,
	LFT:	37,
	UP:		38,
	RGH:	39,
	DN:		40,
	INS:	45,
	DEL:	46,
	F1:		112,

	target: function(evt) {
		if (!evt) evt = window.event;
		return evt.target || evt.srcElement;
	},
	stop: function(evt) {
		if (!evt) evt = window.event;
		if (evt.preventDefault) {
			evt.preventDefault();
			evt.stopPropagation();
		} else {
			evt.returnValue = false;
			evt.cancelBubble = true;
			if (!evt.shiftKey && !evt.ctrlKey)
				evt.keyCode = 0; //Bug 1834891
		}
	},

	leftClick: function(evt) {
		if (!evt) evt = window.event;
		return evt.which == 1 || evt.button == 0 || evt.button == 1;
	},
	rightClick: function (evt) {
		if (!evt) evt = window.event;
		return evt.which == 3 || evt.button == 2;
	},
	mouseData: function (evt, target) {
		if (!evt) evt = window.event;
		var ofs = zDom.cmOffset(target ? target: zEvt.target(evt));
		return {
			x: zEvt.x(evt) - ofs[0],
			y: zEvt.y(evt) - ofs[1],
			keys: zEvt.keyMetaData(evt),
			marshal: zEvt._mouseDataMarshal
		};
	},
	keyData: function (evt) {
		if (!evt) evt = window.event;
		return {
			keyCode: zEvt.keyCode(evt),
			charCode: zEvt.charCode(evt),
			keys: zEvt.keyMetaData(evt),
			marshal: zEvt._keyDataMarshal
		};
	},
	keyMetaData: function (evt) {
		if (!evt) evt = window.event;
		return {
			altKey: evt.altKey,
			ctrlKey: evt.ctrlKey,
			shiftKey: evt.shiftKey,
			leftClick: zEvt.leftClick(evt),
			rightClick: zEvt.rightClick(evt),
			marshal: zEvt._keyMetaDataMarshal
		};
	},
	_mouseDataMarshal: function () {
		return [this.x, this.y, this.keys.marshal()];
	},
	_keyDataMarshal: function () {
		return [this.keyCode, this.charCode, this.keys.marshal()];
	},
	_keyMetaDataMarshal: function () {
		var s = "";
		if (this.altKey) s += 'a';
		if (this.ctrlKey) s += 'c';
		if (this.shiftKey) s += 's';
		if (this.leftClick) s += 'l';
		if (this.rightClick) s += 'r';
		return s;
	},

	x: function (evt) {
		if (!evt) evt = window.event;
		return evt.pageX || (evt.clientX +
			(document.documentElement.scrollLeft || document.body.scrollLeft));
  	},
	y: function(evt) {
		if (!evt) evt = window.event;
		return evt.pageY || (evt.clientY +
			(document.documentElement.scrollTop || document.body.scrollTop));
	},

	charCode: function(evt) {
		if (!evt) evt = window.event;
		return evt.charCode || evt.keyCode;
	},
	keyCode: function(evt) {
		if (!evt) evt = window.event;
		var k = evt.keyCode || evt.charCode;
		return zk.safari ? (this.safariKeys[k] || k) : k;
	},

	listen: function (el, evtnm, fn) {
		if (el.addEventListener)
			el.addEventListener(evtnm, fn, false);
		else /*if (el.attachEvent)*/
			el.attachEvent('on' + evtnm, fn);
	},
	unlisten: function (el, evtnm, fn) {
		if (el.removeEventListener)
			el.removeEventListener(evtnm, fn, false);
		else if (el.detachEvent) {
			try {
				el.detachEvent('on' + evtnm, fn);
			} catch (e) {
			}
		}
	},

	enableESC: function () {
		if (zDom._noESC) {
			zEvt.unlisten(document, "keydown", zDom._noESC);
			delete zDom._noESC;
		}
		if (zDom._onErrChange) {
			window.onerror = zDom._oldOnErr;
			if (zDom._oldOnErr) delete zDom._oldOnErr;
			delete zDom._onErrChange;
		}
	},
	disableESC: function () {
		if (!zDom._noESC) {
			zDom._noESC = function (evt) {
				if (!evt) evt = window.event;
				if (evt.keyCode == 27) {
					zEvt.stop(evt);
					return false;//eat
				}
				return true;
			};
			zEvt.listen(document, "keydown", zDom._noESC);

			//FUTURE: onerror not working in Safari and Opera
			//if error occurs, loading will be never ended, so try to ignore
			//we cannot use zEvt.listen. reason: no way to get back msg...(FF)
			zDom._oldOnErr = window.onerror;
			zDom._onErrChange = true;
			window.onerror =
	function (msg, url, lineno) {
		//We display errors only for local class web resource
		//It is annoying to show error if google analytics's js not found
		var au = zAu.comURI();
		if (au && url.indexOf(location.host) >= 0) {
			var v = au.lastIndexOf(';');
			v = v >= 0 ? au.substring(0, v): au;
			if (url.indexOf(v + "/web/") >= 0) {
				msg = mesg.FAILED_TO_LOAD + url + "\n" + mesg.FAILED_TO_LOAD_DETAIL
					+ "\n" + mesg.CAUSE + msg+" (line "+lineno + ")";
				if (zk.error) zk.error(msg);
				else alert(msg);
				return true;
			}
		}
	};
		}
	}
};

/** A widget event, fired by {@link zk.Widget#fire}.
 * It is an application-level event that is used by application to
 * hook the listeners to.
 * On the other hand, a DOM event ({@link zEvt}) is the low-level event
 * listened by the implementation of a widget.
 */
zk.Event = zk.$extends(zk.Object, {
	/** The target widget. */
	//target: null,
	/** The event name. */
	//name: null,
	/** Options.
	 * <dl>
	 * <dt>implicit</dt>
	 * <dd>Whether this event is an implicit event, i.e., whether it is implicit
	 * to users (so no progressing bar).</dd>
	 * <dt>ignorable</dt>
	 * <dd>Whether this event is ignorable, i.e., whether to ignore any error
	 * of sending this event back the server.
	 * An ignorable event is also an imiplicit event.</dd>
	 * <dt>ctl</dt>
	 * <dd>Whether it is a control, such as onClick, rather than
	 * a notification for status change.</dd>
	 * <dt>$busyIgnore</dt>
	 * <dd>Whether it can be ignored by the server when the server
	 * is busy.</dd>
	 * <dt>$repeatIgnore</dt>
	 * <dd>Whether it can be ignored by the server when the server
	 * receives the same requests consecutively.</dd>
	 * <dt>$duplicateIgnore</dt>
	 * <dd>Whether it can be ignored by the server when the server
	 * receives the same requests that was not processed yet.</dd>
	 * </dl>
	 * <p>Note: in addition, to specify$busyIgnore, $repeatIgnore
	 * and $duplicateIgnore in opts, they are determinted by
	 * {@link zk.Event#serverOptions}. You can add your own events to
	 * {@link zk.Event#busyIngores}, {@link zk.Event#duplicateIgnores}
	 * or {@link zk.Event#repleatIgnores}. Or, You might replace it with yours.
	 */
	/** Whether to stop the event propogation.
	 * Note: it won't be sent to the server if stop is true.
	 */
	//stop: false,

	$init: function (target, name, data, opts) {
		this.target = target;
		this.name = name;
		this.data = data;
		this.opts = opts;
	}
},{
	duplicateIgnores: {
		onBookmarkChange: true, onURIChange: true, onClientInfo: true,
		onError: true, onBlur: true, onFocus: true, onSort: true,
		onTimer: true, onMove: true, onSize: true, onZIndex: true,
		onMaximize: true, onMinimize: true, onOpen: true,
		onRender: true, onSelect: true
	},
	/** A map of event names that can be ignored by the server when
	 *  the server is busy (i.e., $busyIgnore).
	 */
	busyIgnores: {
		dummy: true, getUploadInfo: true, onChanging: true, onScrolling: true
	},
	repeatIgnores: {
		onChange: true, onScroll: true, onCheck: true
	},
	/** Returns a string to represent the server options.
	 * <ul>
	 * <li>b: $busyIgnore</li>
	 * <li>r: $repeatIgnore</li>
	 * <li>d: $duplicateIgnore</li>
	 * </ul>
	 */
	serverOptions: function (evtnm, opts) {
		var $Event = zk.Event, so = '';
		if ((opts && opts.$duplicateIgnore) || $Event.duplicateIgnores[evtnm])
			so = 'd';
		if ((opts && opts.$repeatIgnore) || $Event.repeatIgnores[evtnm])
			so += 'r';
		if ((opts && opts.$busyIgnore) || $Event.busyIgnores[evtnm])
			so += 'b'
		return so;
	}
});

/** An utility to manage a collection of watches.
 * A watch is any JavaScript object used to 'watch' an action, such as onSize,
 * The watch must implement a method having
 * the same as the action name.
 * For example, zAu.watch("onSend", o) where
 * o must have a method called onSend. Then, when the onSend action occurs,
 * o.onSend() will be invoked.
 *
 * <p>Note: the watches are shared by the whole client engine, so be careful
 * to avoid the conflict of action names. Here is a list of all action
 * names.
 * <dl>
 * <dt>onSend(implicit)</dt>
 * <dd>It is called before sending the AU request to the server.
 * The implicit argument indicates whether all AU requests being
 * sent are implicit.</dd>
 * </dl>
 */
zWatch = {
	/** Adds a watch.
	 * Note: the order is parent-first, so the invocation ({@link #fire})
	 * is from the parent to the child.
	 * @param name the action name. Currently, it supports only onSend,
	 * which is called before sending the AU request(s).
	 * @return true if added successfully.
	 */
	watch: function (name, watch) {
		var wts = this._wts[name];
		if (!wts) wts = this._wts[name] = [];
		wts.$add(watch, zUtl.isAncestor); //parent first
	},
	/** Removes a watch.
	 * @return whether the watch has been removed successfully.
	 * It returns false if the watch was not added before.
	 */
	unwatch: function (name, watch) {
		var wts = this._wts[name];
		return wts && wts.$remove(watch);
	},
	/** Remove all watches of the specified name.
	 */
	unwatchAll: function (name) {
		delete this._wts[name];
	},
	/** Calls all watches of the specified name.
	 * @param timeout when to call the watch. If positive or zero,
	 * setTimeout is used. Otherwise, it is called
	 */
	fire: function (name, timeout, vararg) {
		var wts = this._wts[name],
			len = wts ? wts.length: 0;
		if (len) {
			var args = [], o;
			for (var j = 2, l = arguments.length; j < l;)
				args.push(arguments[j++]);

			wts = wts.$clone(); //make a copy since unwatch might be called
			if (timeout >= 0) {
				setTimeout(
				function () {
					while (o = wts.shift())
						o[name].apply(o, args);
				}, timeout);
				return;
			}

			while (o = wts.shift())
				o[name].apply(o, args);
		}
	},
	/** Calls all descendant watches of the specified name.
	 * By descendant we mean the watch is the same or an descendant of
	 * the specified origin.
	 * <p>Note: it assumes the watch's parent can be retrieved by either
	 * the method called <code>getParent</code>, or the
	 * property called <code>parent</code>.
	 * <p>In other words, if the specified origin is not the ancestor
	 * of a watch, the watch won't be called.
	 */
	fireDown: function (name, timeout, origin, vararg) {
		var wts = this._wts[name],
			len = wts ? wts.length: 0;
		if (len) {
			var args = [];
			for (var j = 3, l = arguments.length; j < l;)
				args.push(arguments[j++]);

			var found = [], o;
			for (var j = 0; j < len;) {
				o = wts[j++];
				if (zUtl.isAncestor(origin, o))
					found.push(o);
			}

			if (timeout >= 0) {
				setTimeout(
				function () {
					while (o = found.shift())
						o[name].apply(o, args);
				}, timeout);
				return;
			}

			while (o = found.shift())
				o[name].apply(o, args);
		}
	},

	_wts: {}
};
