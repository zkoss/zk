/* zkevt.js

{{IS_NOTE
	Purpose:
		Action and Watch
	Description:
		
	History:
		Wed Oct 29 11:55:58     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

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
	 */
	call: function (name, vararg) {
		var wts = this._wts[name],
			len = wts ? wts.length: 0;
		if (len) {
			var args = [];
			for (var j = 1, l = arguments.length; j < l;)
				args.push(arguments[j]);

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
	callDown: function (name, origin, vararg) {
		var wts = this._wts[name],
			len = wts ? wts.length: 0;
		if (len) {
			var args = [];
			for (var j = 2, l = arguments.length; j < l;)
				args.push(arguments[j]);

			for (var j = 0; j < len;) {
				var o = wts[j++];
				if (zUtl.isAncestor(origin, o))
					o[name].call(o, args);
			}
		}
	},

	_wts: {}
};
