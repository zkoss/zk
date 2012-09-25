/* BinderUtil.java

	Purpose:
		
	Description:
		
	History:
		2012/9/25 Created by dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import org.zkoss.bind.Binder;
import org.zkoss.zk.ui.Component;

/**
 * @author dennis
 *
 */
public class BinderUtil {
	public static void markHandling(Component comp,Binder binder){
		comp.setAttribute(BinderImpl.BINDER, binder);
	}
	
	public static void unmarkHandling(Component comp){
		comp.removeAttribute(BinderImpl.BINDER);
	}
	public static boolean isHandling(Component comp){
		return comp.hasAttribute(BinderImpl.BINDER);
	}
	public static Binder getBinder(Component comp){
		return getBinder(comp,false);
	}
	public static Binder getBinder(Component comp, boolean recurse){
		return (Binder)comp.getAttribute(BinderImpl.BINDER,recurse);
	}
}
