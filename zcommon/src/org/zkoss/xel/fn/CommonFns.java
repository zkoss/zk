/* CommonFns.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 20 18:35:21     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.fn;

import java.util.Collection;
import java.util.Map;
import java.lang.reflect.Field;
import java.math.BigDecimal;

import org.zkoss.lang.Classes;
import org.zkoss.mesg.Messages;
import org.zkoss.util.resource.Labels;
import org.zkoss.util.logging.Log;
import org.zkoss.text.MessageFormats;

/**
 * Common functions used with EL.
 *
 * @author tomyeh
 */
public class CommonFns {
	private static final Log log = Log.lookup(CommonFns.class);

	protected CommonFns() {}

	/** Converts the specified object to a boolean.
	 */
	public static boolean toBoolean(Object val) {
		return ((Boolean)Classes.coerce(boolean.class, val)).booleanValue();
	}
	/** Converts the specified object to a string.
	 */
	public static String toString(Object val) {
		return (String)Classes.coerce(String.class, val);
	}
	/** Converts the specified object to a number.
	 */
	public static Number toNumber(Object val) {
		return (Number)Classes.coerce(Number.class, val);
	}
	/** Converts the specified object to an integer.
	 */
	public static int toInt(Object val) {
		return ((Integer)Classes.coerce(int.class, val)).intValue();
	}
	/** Converts the specified object to a (big) decimal.
	 */
	public static BigDecimal toDecimal(Object val) {
		return (BigDecimal)Classes.coerce(BigDecimal.class, val);
	}
	/** Converts the specified object to an character.
	 */
	public static char toChar(Object val) {
		return ((Character)Classes.coerce(char.class, val)).charValue();
	}
	/** Tests whehter an object, o, is an instance of a class, c.
	 */
	public static boolean isInstance(Object c, Object o) {
		if (c instanceof Class) {
			return ((Class)c).isInstance(o);
		} else if (c instanceof String) {
			try {
				return Classes.forNameByThread((String)c).isInstance(o);
			} catch (ClassNotFoundException ex) {
				throw new IllegalArgumentException("Class not found: "+c);
			}
		} else {
			throw new IllegalArgumentException("Unknown class: "+c);
		}
	}

	/** Returns the label or message of the specified key.
	 * <ul>
	 * <li>If key is "mesg:class:MMM", Messages.get(class.MMM) is called</li>
	 * <li>Otherwise, {@link Labels#getLabel(String)} is called.
	 * </ul>
	 * @see #getLabel(String, Object[])
	 */
	public static final String getLabel(String key) {
		if (key == null)
			return "";

		if (key.startsWith("mesg:")) {
			final int j = key.lastIndexOf(':');
			if (j > 5) {
				final String clsnm = key.substring(5, j);
				final String fldnm = key.substring(j + 1);
				try {
					final Class cls = Classes.forNameByThread(clsnm);
					final Field fld = cls.getField(fldnm);
					return Messages.get(((Integer)fld.get(null)).intValue());
				} catch (ClassNotFoundException ex) {
					log.warning("Class not found: "+clsnm, ex);
				} catch (NoSuchFieldException ex) {
					log.warning("Field not found: "+fldnm, ex);
				} catch (IllegalAccessException ex) {
					log.warning("Field not accessible: "+fldnm, ex);
				}
			} else if (log.debugable()) {
				log.debug("Not a valid format: "+key);
			}
		}
		return Labels.getLabel(key);
	}
	/** Returns the label of the specified key and formats it
	 * with the specified argument, or null if not found.
	 *
	 * <p>It first uses {@link #getLabel(String)} to load the label.
	 * Then, it, if not null, invokes {@link MessageFormats#format} to format it.
	 *
	 * <p>The current locale is given by {@link org.zkoss.util.Locales#getCurrent}.
	 * @since 3.0.6
	 */
	public static final String getLabel(String key, Object[] args) {
		final String s = getLabel(key);
		return s != null ? MessageFormats.format(s, args, null): null;
	}
	/** Returns the length of an array, string, collection or map.
	 */
	public static final int length(Object o) {
		if (o instanceof String) {
			return ((String)o).length();
		} else if (o == null) {
			return 0;
		} else if (o instanceof Collection) {
			return ((Collection)o).size();
		} else if (o instanceof Map) {
			return ((Map)o).size();
		} else if (o instanceof Object[]) {
			return ((Object[])o).length;
		} else if (o instanceof int[]) {
			return ((int[])o).length;
		} else if (o instanceof long[]) {
			return ((long[])o).length;
		} else if (o instanceof short[]) {
			return ((short[])o).length;
		} else if (o instanceof byte[]) {
			return ((byte[])o).length;
		} else if (o instanceof char[]) {
			return ((char[])o).length;
		} else if (o instanceof double[]) {
			return ((double[])o).length;
		} else if (o instanceof float[]) {
			return ((float[])o).length;
		} else {
			throw new IllegalArgumentException("Unknown object for length: "+o.getClass());
		}
	}

	/** Instantiates the specified class.
	 */
	public static final Object new_(Object o) throws Exception {
		if (o instanceof String) {
			return Classes.newInstanceByThread((String)o);
		} else if (o instanceof Class) {
			return ((Class)o).newInstance();
		} else {
			throw new IllegalArgumentException("Unknow object for new: "+o);
		}
	}
}
