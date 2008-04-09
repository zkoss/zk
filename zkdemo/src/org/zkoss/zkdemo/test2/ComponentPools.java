/* ComponentPools.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun 21 20:49:41     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2;

import java.util.*;

import org.zkoss.zk.ui.*;
import org.zkoss.zul.*;

/**
 * A pool of components being created when app starts.
 * It is used to test with desktop.zul.
 *
 * @author tomyeh
 */
public class ComponentPools {
	private static final List _comps = new LinkedList();
	private static int _cnt;

	/** Returns the next component in the pool, or null if no more.
	 */
	public static Component next() {
		synchronized (_comps) {
			return _comps.isEmpty() ? null: (Component)_comps.remove(0);
		}
	}
	/** Put a component to the pool.
	 * Note: the component must be detached first before calling this method.
	 */
	public static void add(int j, Component comp) {
		if (comp.getPage() != null)
			throw new IllegalArgumentException("detached first");
		synchronized (_comps) {
			_comps.add(j, comp);
		}
	}
	/** Generates a set of components and stores them in the pool.
	 */
	public static void generate(final int num) {
		final Thread td = new Thread() {
			public void run() {
				for (int j = 0; j < num; ++j) {
					final Label l = new Label("Pool " + _cnt++);
					_comps.add(l);
					if ((j & 1) == 1)
						l.setId("L" + _cnt);
				}
			}
		};
		td.start();
	}
}
