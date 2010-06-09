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
	private final Map _args;

	/*package*/ ArgumentInfo(Map args) {
		if (args != null && !args.isEmpty()) {
			for (Iterator it = args.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				me.setValue(new ExValue((String)me.getValue(), Object.class));
			}
			_args = args;
		} else
			_args = null;
	}

	/*package*/ Object newInstance(Class cls, Evaluator eval, Page page)
	throws Exception {
		if (_args != null) {
			Map args = resolveArguments(eval, page);
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
	/*package*/ Map resolveArguments(Evaluator eval, Page page) {
		if (_args == null)
			return Collections.EMPTY_MAP;

		final Map args = new LinkedHashMap(); //eval order is important
		for (Iterator it = _args.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			args.put(me.getKey(),
				((ExValue)me.getValue()).getValue(eval, page));
		}
		return args;
	}

	/** After called, args is destroyed so the caller cannot use it anymore.
	 */
	/*package*/ static Object[] toArray(Map args) {
		if (args.isEmpty())
			return new Object[0];

		final List lst = new LinkedList();
		for (int j = 0;;j++) {
			final String nm = "arg" + j;
			if (args.containsKey(nm))
				lst.add(args.remove(nm));
			else
				break;
		}
		lst.addAll(args.values());
		return (Object[])lst.toArray(new Object[lst.size()]);
	}
}
