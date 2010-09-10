/* Library.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug  1 09:06:36     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

import java.util.Map;
import java.util.HashMap;

import org.zkoss.util.logging.Log;

/**
 * Represent the scope of ZK libraries.
 * Unlike {@link System}, the scope of {@link Library} depends how
 * ZK libraries are installed.
 * If they are installed in WEB-INF/lib of a ZK application,
 * the scope is the application. In other words, the library properties
 * ({@link #setProperty}) are shared by the application only.
 *
 * <p>On the other hand, if ZK libraries are installed in a folder
 * shared by all applications, the library properties are shared
 * by all applications.
 *
 * @author tomyeh
 * @since 3.0.7
 */
public class Library {
	private static final Log log = Log.lookup(Library.class);
	private static final Map<String, String> _props = new HashMap<String, String>();

	private Library() {}

	/** Returns the library property indicated by the specified key.
	 * If no such library property, this method will call
	 * {@link System#getProperty} to look for a system property.
	 *
	 * <p>The library property is shared by Java codes that access
	 * the same set of ZK libraries, since it is actually
	 * a static member of this class. Thus, if ZK libraries (including
	 * this class) are installed in WEB-INF/lib of an application,
	 * the library properties are accessible only in the application.
	 *
	 * <p>Note: unlike {@link System#getProperty}, this method won't
	 * throw SecurityException.
	 *
	 * @return the string value of the library property, or the system
	 * property, or null if no such property.
	 * @exception NullPointerException if key is null
	 * @exception IllegalArumentException if key is empty
	 * @see #setProperty
	 */
	public static String getProperty(String key) {
		final String v;
		synchronized (_props) {
			v = _props.get(key);
		}
		try {
			return v != null ? v: System.getProperty(key);
		} catch (SecurityException ex) {
			return null;
		}
	}
	/** Returns the library property indicated by the specified key.
	 * If no such library property, this method will call
	 * {@link System#getProperty} to look for a system property.
	 *
	 * <p>Note: unlike {@link System#getProperty}, this method won't
	 * throw SecurityException.
	 *
	 * @param key the name of the library property
	 * @param def a default value.
	 * @exception NullPointerException if key is null
	 * @exception IllegalArumentException if key is empty
	 */
	public static String getProperty(String key, String def) {
		final String v;
		synchronized (_props) {
			v = _props.get(key);
		}
		try {
			return v != null ? v: System.getProperty(key, def);
		} catch (SecurityException ex) {
			return def;
		}
	}
	/** Sets the libary property indicated by the specified key.
	 * @see #getProperty
	 * @exception NullPointerException if key is null
	 * @exception IllegalArumentException if key is empty
	 * @return the previous value of the library property, or null if
	 * it did not have one.
	 */
	public static String setProperty(String key, String value) {
		if (key == null) throw new NullPointerException();
		if (key.length() == 0) throw new IllegalArgumentException();

		synchronized (_props) {
			return _props.put(key, value);
		}
	}

	/** Parses the property value to an integer.
	 * If the specified value is not an integer, the default value is returned
	 * (and a warning is logged).
	 * @param defVal the default value
	 * @since 3.6.1
	 */
	public static int getIntProperty(String key, int defVal) {
		final String val = getProperty(key);
		if (val != null) {
			try {
				return Integer.parseInt(val);
			} catch (Throwable t) {
				log.warning("Failed to parse "+key+": not an integer, "+val);
			}
		}
		return defVal;
	}
}
