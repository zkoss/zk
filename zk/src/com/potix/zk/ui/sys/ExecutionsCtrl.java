/* ExecutionsCtrl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun  6 12:20:51     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.sys;

import java.lang.reflect.Method;

import com.potix.lang.Classes;
import com.potix.lang.Objects;
import com.potix.util.Pair;
import com.potix.util.CacheMap;

import com.potix.zk.ui.Execution;
import com.potix.zk.ui.Executions;
import com.potix.zk.ui.event.Event;

/**
 * Additional utilities for {@link Execution}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.7 $ $Date: 2006/05/29 04:28:09 $
 */
public class ExecutionsCtrl extends Executions {
	protected ExecutionsCtrl() {} //prevent from instantiation

	/** Sets the execution for the current thread.
	 * Called only internally.
	 */
	public static final void setCurrent(Execution exec) {
		_exec.set(exec);
	}
	/** Returns the current {@link ExecutionCtrl}.
	 */
	public static final ExecutionCtrl getCurrentCtrl() {
		return (ExecutionCtrl)getCurrent();
	}

	/** A map of (Pair(Class,String evtnm), Method). */
	private static final CacheMap _evtmtds =
		new CacheMap().setMaxSize(1000).setLifetime(60*60000);
	/** Returns the method for handling the specified event, or null
	 * if not available.
	 */
	public static final Method getEventMethod(Class cls, String evtnm) {
		final Pair key = new Pair(cls, evtnm);
		final Object o;
		synchronized (_evtmtds) {
			o = _evtmtds.get(key);
		}
		if (o != null)
			return o == Objects.UNKNOWN ? null: (Method)o;

		Method mtd = null;
		try {
			mtd = Classes.getCloseMethodBySubclass(
					cls, evtnm, new Class[] {Event.class});
		} catch (NoSuchMethodException ex) {
			try {
				mtd = cls.getMethod(evtnm, null);
			} catch (NoSuchMethodException e2) {
			}
		}
		synchronized (_evtmtds) {
			_evtmtds.put(key, mtd != null ? mtd: Objects.UNKNOWN);
		}
		return mtd;
	}
}
