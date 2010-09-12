/* ArgumentInfo.java

	Purpose:
		
	Description:
		
	History:
		Tue May 12 10:18:48     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.metainfo;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Collections;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.Evaluator;

/**
 * Simplify the parsing of arguments.
 * @author tomyeh
 * @since 3.6.2
 */
/*package*/ class ArgumentInfo {
	/** The arguments (String name, ExValue value), null if no argument. */
	private final Map<String, ExValue> _args;

	/*package*/ ArgumentInfo(Map<String, String> args) {
		if (args != null && !args.isEmpty()) {
			_args = new LinkedHashMap<String, ExValue>();
			for (Map.Entry<String, String> me: args.entrySet())
				_args.put(me.getKey(), new ExValue(me.getValue(), Object.class));
		} else
			_args = null;
	}

	/*package*/ Object newInstance(Class<?> cls, Evaluator eval, Page page)
	throws Exception {
		if (_args != null) {
			Map<String, Object> args = resolveArguments(eval, page);
			try {
				return cls.getConstructor(new Class[] {Map.class})
					.newInstance(new Object[] {args});
			} catch (NoSuchMethodException ex) {
			}
			try {
				return cls.getConstructor(new Class[] {Object[].class})
					.newInstance(new Object[] {toArray(args)});
			} catch (NoSuchMethodException e2) {
			}
		}
		return cls.newInstance();
	}
	/*package*/ Map<String, Object> resolveArguments(Evaluator eval, Page page) {
		if (_args == null)
			return Collections.emptyMap();

		final Map<String, Object> args = new LinkedHashMap<String, Object>(); //eval order is important
		for (Map.Entry<String, ExValue> me: _args.entrySet())
			args.put(me.getKey(), me.getValue().getValue(eval, page));
		return args;
	}

	/** After called, args is destroyed so the caller cannot use it anymore.
	 */
	/*package*/ static Object[] toArray(Map<String, Object> args) {
		if (args.isEmpty())
			return new Object[0];

		final List<Object> lst = new LinkedList<Object>();
		for (int j = 0;;j++) {
			final String nm = "arg" + j;
			if (args.containsKey(nm))
				lst.add(args.remove(nm));
			else
				break;
		}
		lst.addAll(args.values());
		return lst.toArray(new Object[lst.size()]);
	}
}
