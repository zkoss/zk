/* Serializables.java

	Purpose:
		
	Description:
		
	History:
		Sun Jun 25 22:54:45     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.io;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.Serializable;
import java.io.Externalizable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * Utilities to handle java.io.Serializable.
 *
 * @author tomyeh
 */
public class Serializables {
	private Serializables() {}

	/** Writes only serializable entries of the specified map.
	 * Non-serializable attributes are ignored.
	 */
	public static void smartWrite(ObjectOutputStream s, Map map)
	throws IOException {
		if (map != null) {
			for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final Object nm = me.getKey();
				final Object val = me.getValue();
				if (((nm instanceof Serializable) || (nm instanceof Externalizable))
				&& (val == null || (val instanceof Serializable) || (val instanceof Externalizable))) {
					s.writeObject(nm);
					s.writeObject(val);
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
	public static Map smartRead(ObjectInputStream s, Map map)
	throws IOException, ClassNotFoundException {
		for (;;) {
			final Object nm = s.readObject();
			if (nm == null) break; //no more

			if (map == null) map = new HashMap();
			map.put(nm, s.readObject());
		}
		return map;
	}
	/** Writes only serializable elements of the specified collection.
	 */
	public static void smartWrite(ObjectOutputStream s, Collection col)
	throws IOException {
		if (col != null) {
			for (Iterator it = col.iterator(); it.hasNext();) {
				final Object val = it.next();
				if ((val instanceof Serializable)
				|| (val instanceof Externalizable)) {
					s.writeObject(val);
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
	public static Collection smartRead(ObjectInputStream s, Collection col)
	throws IOException, ClassNotFoundException {
		for (;;) {
			final Object val = s.readObject();
			if (val == null) break; //no more

			if (col == null) col = new LinkedList();
			col.add(val);
		}
		return col;
	}

	/** Writes only serializable elements of the specified array.
	 * <p>To read back, use {@link #smartRead(ObjectInputStream, Collection)}.
	 * @since 3.0.0
	 */
	public static void smartWrite(ObjectOutputStream s, Object[] ary)
	throws IOException {
		if (ary != null) {
			for (int j = 0; j < ary.length; ++j) {
				final Object val = ary[j];
				if ((val instanceof Serializable)
				|| (val instanceof Externalizable)) {
					s.writeObject(val);
				}
			}
		}
		s.writeObject(null);
	}
}
