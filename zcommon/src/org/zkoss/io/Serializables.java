/* Serializables.java

	Purpose:
		
	Description:
		
	History:
		Sun Jun 25 22:54:45     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.io;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.Serializable;
import java.io.Externalizable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import org.zkoss.util.logging.Log;

/**
 * Utilities to handle java.io.Serializable.
 *
 * @author tomyeh
 */
public class Serializables {
	/** The logger called org.zkoss.io.serializable used to log serialization
	 * information. You could set it to DEBUG, such that the not-serializable
	 * and ignored values will be logged.
	 * @since 5.0.7
	 */
	public static final Log logio = Log.lookup("org.zkoss.io.serializable");

	private Serializables() {}

	/** Writes only serializable entries of the specified map.
	 * Non-serializable attributes are ignored.
	 */
	public static <K, V> void smartWrite(ObjectOutputStream s, Map<K, V> map)
	throws IOException {
		if (map != null) {
			final boolean debug = logio.debugable();
			for (Map.Entry<K, V> me: map.entrySet()) {
				final K nm = me.getKey();
				final V val = me.getValue();
				if (((nm instanceof Serializable) || (nm instanceof Externalizable))
				&& (val == null || (val instanceof Serializable) || (val instanceof Externalizable))) {
					try {
						s.writeObject(nm);
						s.writeObject(val);
					} catch (java.io.NotSerializableException ex) {
						logio.error("Unable to serialize entry: "+nm+'='+val);
						throw ex;
					}
				} else if (nm != null && debug) {
					logio.debug("Skip not-serializable entry: "+nm+'='+val);
				}
			}
		}
		s.writeObject(null); //denote end-of-map
	}
	/** Reads serializable entries back (serialized by {@link #smartWrite(ObjectOutputStream,Map)}).
	 *
	 * @param map the map to hold the data being read. If null and any data
	 * is read, a new map (HashMap) is created and returned.
	 * @return the map being read
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> smartRead(ObjectInputStream s, Map<K, V> map)
	throws IOException, ClassNotFoundException {
		for (;;) {
			final Object nm = s.readObject();
			if (nm == null) break; //no more

			if (map == null) map = new HashMap<K,V>();
			map.put((K)nm, (V)s.readObject());
		}
		return map;
	}
	/** Writes only serializable elements of the specified collection.
	 */
	public static <T> void smartWrite(ObjectOutputStream s, Collection<T> col)
	throws IOException {
		if (col != null) {
			final boolean debug = logio.debugable();
			for (T val: col) {
				if ((val instanceof Serializable)
				|| (val instanceof Externalizable)) {
					try {
						s.writeObject(val);
					} catch (java.io.NotSerializableException ex) {
						logio.error("Unable to serialize item: "+val);
						throw ex;
					}
				} else if (val != null && debug) {
					logio.debug("Skip not-serializable item: "+val);
				}
			}
		}
		s.writeObject(null);
	}
	/** Reads serializable elements back (serialized by {@link #smartWrite(ObjectOutputStream,Collection)})
	 *
	 * @param col the collection to hold the data beinig read. If null and
	 * and data is read, a new collection (LinkedList) is created and returned.
	 * @return the collection being read
	 */
	@SuppressWarnings("unchecked")
	public static <T> Collection<T> smartRead(ObjectInputStream s, Collection<T> col)
	throws IOException, ClassNotFoundException {
		for (;;) {
			final Object val = s.readObject();
			if (val == null) break; //no more

			if (col == null) col = new LinkedList<T>();
			col.add((T)val);
		}
		return col;
	}
	/** Reads serializable elements back (serialized by {@link #smartWrite(ObjectOutputStream,Collection)})
	 *
	 * @param col the collection to hold the data beinig read. If null and
	 * and data is read, a new collection (LinkedList) is created and returned.
	 * @return the collection being read
	 * @since 6.0.0
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> smartRead(ObjectInputStream s, List<T> col)
	throws IOException, ClassNotFoundException {
		for (;;) {
			final Object val = s.readObject();
			if (val == null) break; //no more

			if (col == null) col = new LinkedList<T>();
			col.add((T)val);
		}
		return col;
	}

	/** Writes only serializable elements of the specified array.
	 * <p>To read back, use {@link #smartRead(ObjectInputStream, Collection)}.
	 * @since 3.0.0
	 */
	public static <T> void smartWrite(ObjectOutputStream s, T[] ary)
	throws IOException {
		if (ary != null) {
			final boolean debug = logio.debugable();
			for (int j = 0; j < ary.length; ++j) {
				final T val = ary[j];
				if ((val instanceof Serializable)
				|| (val instanceof Externalizable)) {
					try {
						s.writeObject(val);
					} catch (java.io.NotSerializableException ex) {
						logio.error("Unable to serialize item: "+val);
						throw ex;
					}
				} else if (val != null && debug) {
					logio.debug("Skip not-serializable item: "+val);
				}
			}
		}
		s.writeObject(null);
	}
	/** Writes the given value only if it is serializable.
	 * If not, null is written.
	 * @since 5.0.7
	 */
	public static void smartWrite(ObjectOutputStream s, Object val)
	throws IOException {
		final boolean bser = val instanceof java.io.Serializable
			|| val instanceof java.io.Externalizable;
		s.writeObject(bser ? val: null);
		if (!bser && val != null && logio.debugable())
			logio.debug("Skip not-serializable object: "+val);
	}
}
