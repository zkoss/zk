/* ComponentPools.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun 21 20:49:41     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zkdemo.test;

import java.util.*;

import com.potix.zk.ui.*;
import com.potix.zk.ui.util.WebAppInit;
import com.potix.zul.html.*;

/**
 * A pool of components being created when app starts.
 * It is used to test with desktop.zul.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class ComponentPools {
	private static final List _comps = new LinkedList();

	/** Returns the next component in the pool, or null if no more.
	 */
	public static Component next() {
		synchronized (_comps) {
			return _comps.isEmpty() ? null: (Component)_comps.remove(0);
		}
	}
	public static class Init implements WebAppInit {
		public void init(WebApp wapp) {
			for (int j = 0; j < 20; ++j)
				_comps.add(new Label("Pool "+j));
		}
	}
}
