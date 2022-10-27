/* SimpleActionHandler.java

	Purpose:
		
	Description:
		
	History:
		3:29 PM 2022/1/4, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui.util;

import java.lang.reflect.Method;

import org.zkoss.stateless.util.ActionHandler;
import org.zkoss.stateless.util.ActionHandler0;
import org.zkoss.stateless.util.ActionHandler1;
import org.zkoss.stateless.util.ActionHandler2;
import org.zkoss.stateless.util.ActionHandler3;
import org.zkoss.stateless.util.ActionHandler4;
import org.zkoss.stateless.util.ActionHandler5;
import org.zkoss.stateless.util.ActionHandler6;
import org.zkoss.stateless.util.ActionHandler7;
import org.zkoss.stateless.util.ActionHandler8;
import org.zkoss.stateless.util.ActionHandler9;

/**
 * Internal use.
 * @author jumperchen
 * @hidden for Javadoc
 */
/*package*/ class SimpleActionHandler implements ActionHandler, ActionHandler0, ActionHandler1, ActionHandler2, ActionHandler3, ActionHandler4, ActionHandler5, ActionHandler6, ActionHandler7, ActionHandler8, ActionHandler9 {
	private final Object target;
	private final Method method;
	/*package*/ SimpleActionHandler(Object target, Method method) {
		this.target = target;
		this.method = method;
	}
	public int getParameterCount() {
		return method.getParameterCount();
	}

	public Method method() {
		return method;
	}

	public void run() throws Throwable {
		method.invoke(target, (Object[]) null);
	}

	public void accept(Object o) throws Throwable {
		method.invoke(target, o);
	}

	public void accept(Object o, Object o2) throws Throwable {
		method.invoke(target, o, o2);
	}

	public void accept(Object o, Object o2, Object o3) throws Throwable {
		method.invoke(target, o, o2, o3);
	}

	public void accept(Object o, Object o2, Object o3, Object o4)
			throws Throwable {
		method.invoke(target, o, o2, o3, o4);
	}

	public void accept(Object o, Object o2, Object o3, Object o4, Object o5)
			throws Throwable {
		method.invoke(target, o, o2, o3, o4, o5);
	}

	public void accept(Object o, Object o2, Object o3, Object o4, Object o5,
			Object o6) throws Throwable {
		method.invoke(target, o, o2, o3, o4, o5, o6);
	}

	public void accept(Object o, Object o2, Object o3, Object o4, Object o5,
			Object o6, Object o7) throws Throwable {
		method.invoke(target, o, o2, o3, o4, o5, o6, o7);
	}

	public void accept(Object o, Object o2, Object o3, Object o4, Object o5,
			Object o6, Object o7, Object o8) throws Throwable {
		method.invoke(target, o, o2, o3, o4, o5, o6, o7, o8);
	}

	public void accept(Object o, Object o2, Object o3, Object o4, Object o5,
			Object o6, Object o7, Object o8, Object o9) throws Throwable {
		method.invoke(target, o, o2, o3, o4, o5, o6, o7, o8, o9);
	}
}