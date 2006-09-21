/* ThreadLocals.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 21 10:07:46     2006, Created by henrichen@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.lang;

import java.lang.reflect.Field;

/**
 * ThreadLocal related utilties. This implementation get the static 
 * ThreadLocal via reflection.
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 */
public class ThreadLocals {
	/** Given class name and static ThreadLocal field name, return the associated ThreadLocal.
	 * @param clsname the class name
	 * @param fldname the ThreadLocal field name
	 */
	public static ThreadLocal getThreadLocal(String clsname, String fldname) {
		try {
			Class cls = Classes.forNameByThread(clsname);
			return getThreadLocal(cls, fldname);
		} catch (ClassNotFoundException ex) {
			throw SystemException.Aide.wrap(ex);
		}
	}

	/** Given class and static ThreadLocal field name, return the associated ThreadLocal.
	 * @param cls the class
	 * @param fldname the ThreadLocal field name.
	 */
	public static ThreadLocal getThreadLocal(Class cls, String fldname) {
		try {
			Field fld = cls.getDeclaredField(fldname);
			fld.setAccessible(true);
			return (ThreadLocal) fld.get(cls); //class static field, a ThreadLocal
		} catch (java.lang.NoSuchFieldException ex) {
			throw SystemException.Aide.wrap(ex);
		} catch (java.lang.IllegalAccessException ex) {
			throw SystemException.Aide.wrap(ex);
		}
	}
}
