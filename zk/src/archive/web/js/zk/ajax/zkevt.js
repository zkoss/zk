/* zkevt.js

	Purpose:
		ZK Event and Watch/Action
	Description:
		
	History:
		Wed Oct 29 11:55:58     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
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
	/** The extra data, which could be anything. */
	//data: null,
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
	 * </dl>
	 */
	/** Whether to stop the event propogation.
	 * Note: it won't be sent to the server if stop is true.
	 */
	//stop: false,

	$init: function (target, name, data, opts) {
		this.target = target;
		this.name = name;
		this.data = typeof data == 'string' ? [data]: data ? data: null;
		this.opts = opts;
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
	 * @param name the action name. Currently, it supports only onSend,
	 * which is called before sending the AU request(s).
	 * @param overwrite whether to overwrite if the watch was added.
	 * @return true if added successfully.
	 */
	watch: function (name, watch, overwrite) {
		var wts = this._wts[name];
		if (!wts) wts = this._wts[name] = [];
		wts.add(watch, overwrite);
	},
	/** Removes a watch.
	 * @return whether the watch has been removed successfully.
	 * It returns false if the watch was not added before.
	 */
	unwatch: function (name, watch) {
		var wts = this._wts[name];
		return wts && wts.remove(watch);
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
	call: function (name, timeout, vararg) {
		var wts = this._wts[name],
			len = wts ? wts.length: 0;
		if (len) {
			var args = [];
			for (var j = 1, l = arguments.length; j < l;)
				args.push(arguments[j]);

			if (timeout >= 0) {
				for (var j = 0; j < len;) {
					var o = wts[j++];
					setTimeout(function(){o[name].call(o, args)}, timeout);
				}
				return;
			}

			wts = wts.clone(); //make a copy since unwatch might be called
			for (var j = 0; j < len;) {
				var o = wts[j++];
				o[name].call(o, args);
			}
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
	callDown: function (name, timeout, origin, vararg) {
		var wts = this._wts[name],
			len = wts ? wts.length: 0;
		if (len) {
			var args = [];
			for (var j = 2, l = arguments.length; j < l;)
				args.push(arguments[j]);

			if (timeout >= 0) {
				for (var j = 0; j < len;) {
					var o = wts[j++];
					if (zUtl.isAncestor(origin, o))
						setTimeout(function(){o[name].call(o, args)}, timeout);
				}
				return;
			}

			wts = wts.clone(); //make a copy since unwatch might be called
			for (var j = 0; j < len;) {
				var o = wts[j++];
				if (zUtl.isAncestor(origin, o))
					o[name].call(o, args);
			}
		}
	},

	_wts: {}
};
