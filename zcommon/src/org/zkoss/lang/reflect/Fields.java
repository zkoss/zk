/* Fields.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 28 14:40:50     2004, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.AccessibleObject;

import org.zkoss.mesg.MCommon;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.lang.SystemException;
import org.zkoss.util.ModificationException;

/**
 * Utilities to access fields.
 *
 * @author tomyeh
 */
public class Fields {
	/** Changes the accessibility without throwing any exception.
	 * @since 3.0.3
	 */
	public static final void setAccessible(AccessibleObject f, boolean accessible) {
		try {
			f.setAccessible(accessible);
		} catch (Throwable t) {
		}
	}
	/** Returnst the value of the specfied public field or public
	 * method of the object.
	 *
	 * <p>If getByCompound(obj, "a.b.c") is called and obj.getA() or
	 * obj.getA().getB() returns null, the result is null.
	 * However, NullPointerException is thrown if obj is null.
	 *
	 * @param name the field name. It can be in form of "a.b.c", but cannot
	 * be an expression.
	 * It must be a public field or public method (prefixed with set).
	 * @exception NoSuchMethodException if no corresponding field.
	 */
	public static final Object getByCompound(Object obj, String name)
	throws NoSuchMethodException {
		for (;;) {
			final int j = name.indexOf('.');
			if (j < 0)
				return get(obj, name);

			obj = get(obj, name.substring(0, j));
			if (obj == null)
				return obj;
			name = name.substring(j + 1);
		}
	}
	/** Sets the value of the specified public field or public
	 * method in the object.
	 *
	 * @param autoCoerce whether to automatically convert val to the proper
	 * class that matches the argument of method or field.
	 */
	public static final
	void setByCompound(Object obj, String name, Object val,
	boolean autoCoerce) throws NoSuchMethodException, ModificationException {
		for (;;) {
			final int j = name.indexOf('.');
			if (j < 0) {
				set(obj, name, val, autoCoerce);
				return;
			}

			obj = get(obj, name.substring(0, j));
			//Unlike getByCompound, obj==null is considered error here
			name = name.substring(j + 1);
		}
	}
	/** Sets the value of the specfied public field  or public method
	 * in the object, without converting the specified val.
	 *
	 * <p>It is a shortcut of setByCompound(obj, name, val, false).
	 *
	 * @param name the field name. It can be in form of "a.b.c", but cannot
	 * be an expression.
	 * It must be a public field or public method (prefixed with set).
	 */
	public static final
	void setByCompound(Object obj, String name, Object val)
	throws NoSuchMethodException, ModificationException {
		setByCompound(obj, name, val, false);
	}

	/** Returns the value of the specified public field or public method
	 * in the object.
	 * Unlike {@link #getByCompound}, name cannot contain '.'.
	 * In other words, it must be the name of a public field
	 * or method (prefixed with get).
	 */
	public static final Object get(Object obj, String name)
	throws NoSuchMethodException {
		try {
			final AccessibleObject acs = Classes.getAccessibleObject(
				obj.getClass(), name, null,
				Classes.B_GET|Classes.B_PUBLIC_ONLY);
			return 	acs instanceof Method ?
				((Method)acs).invoke(obj, null): ((Field)acs).get(obj);
		} catch (NoSuchMethodException ex) {
			throw ex;
		} catch (Exception ex) {
			throw SystemException.Aide.wrap(ex, MCommon.NOT_FOUND, name);
		}
	}
	/** Sets the value of the specified public filed or public method
	 * in the object.
	 * Unlike {@link #setByCompound}, name cannot contain '.'.
	 * In other words, it must be the name of a public field
	 * or method (prefixed with set).
	 */
	public static final void set(Object obj, String name, Object val,
	boolean autoCoerce) throws NoSuchMethodException, ModificationException {
		try {
			AccessibleObject acs;
			try {
				acs = Classes.getAccessibleObject(
					obj.getClass(),
					name, new Class[] {val != null ? val.getClass(): null},
					Classes.B_SET|Classes.B_PUBLIC_ONLY);
			} catch (NoSuchMethodException ex) {
				if (!autoCoerce || val == null)
					throw ex;

				//retry without specifying any argument type
				acs = Classes.getAccessibleObject(
					obj.getClass(), name, new Class[] {null},
					Classes.B_SET|Classes.B_PUBLIC_ONLY);
			}
			if (acs instanceof Method) {
				final Method mtd = (Method)acs;
				mtd.invoke(obj, new Object[] {
					autoCoerce ?
						Classes.coerce(mtd.getParameterTypes()[0], val): val});
			} else {
				final Field fld = (Field)acs;
				fld.set(obj,
					autoCoerce ? Classes.coerce(fld.getType(), val): val);
			}
		} catch (NoSuchMethodException ex) {
			throw ex;
		} catch (Exception ex) {
			throw ModificationException.Aide.wrap(ex, MCommon.NOT_FOUND, name);
		}
	}

	/** @deprecated As of release 3.0.6, replaced with {@link #getByCompound}.
	 */
	public static final Object getField(Object obj, String name)
	throws NoSuchMethodException {
		return getByCompound(obj, name);
	}
	/** @deprecated As of release 3.0.6, replaced with {@link #setByCompound(Object, String, Object, boolean)}.
	 */
	public static final void setField(Object obj, String name, Object val)
	throws NoSuchMethodException, ModificationException {
		setByCompound(obj, name, val);
	}
	/** @deprecated As of release 3.0.6, replaced with {@link #setByCompound(Object, String, Object, boolean)}.
	 */
	public static final void setField(Object obj, String name, Object val,
	boolean autoCoerce) throws NoSuchMethodException, ModificationException {
		setByCompound(obj, name, val, autoCoerce);
	}
}
