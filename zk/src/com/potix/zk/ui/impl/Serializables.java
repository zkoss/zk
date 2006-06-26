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
import java.util.Iterator;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * Utilities to handle java.io.Serializable.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Serializables {
	/** Writes serializable attributes.
	 * Non-serializable attributes are ignored.
	 */
	public static void writeAttributes(ObjectOutputStream s, Map attrs)
	throws IOException {
		//Write serializable attrs
		for (Iterator it = attrs.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final Object nm = me.getKey();
			final Object val = me.getValue();
			if (nm != null && ((val instanceof java.io.Serializable)
			|| (val instanceof java.io.Externalizable))) {
				s.writeObject(nm);
				s.writeObject(val);
			}
		}
		s.writeObject(null); //denote end-of-attrs
	}
	/** Reads seriazlable attributes back.
	 */
	public static void readAttributes(ObjectInputStream s, Map attrs)
	throws IOException, ClassNotFoundException {
		for (;;) {
			final Object nm = s.readObject();
			if (nm == null) break; //no more

			attrs.put(nm, s.readObject());
		}
	}
}
