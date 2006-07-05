/* Serializables.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Jun 25 22:54:45     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.impl;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.Serializable;
import java.io.Externalizable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * Utilities to handle java.io.Serializable.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Serializables {
	/** Writes only serializable entries of the specified map.
	 * Non-serializable attributes are ignored.
	 */
	public static void smartWrite(ObjectOutputStream s, Map map)
	throws IOException {
		//Write serializable entries
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
	/** Reads seriazlable entries back (serialized by {@link #smartWrite}).
	 *
	 * @param map the map to hold the data being read. If null and any data
	 * is read, a new map is created.
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
}
