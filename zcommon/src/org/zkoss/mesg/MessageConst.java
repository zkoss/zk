/* MessageConst.java


	Purpose: Defines message types
	Description:
	History:
	 2001/4/2 2001/4/2, Tom M. Yeh: Created.


Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.mesg;

import java.util.Map;
import java.util.HashMap;

import org.zkoss.lang.D;
import org.zkoss.lang.Objects;

/**
 * Defines the constants of message codes.
 *
 * @author tomyeh
 * @see Messages
 */
public interface MessageConst {
	/** Denotes a non-existent code. */
	public static final int NULL_CODE = 0;

	/** The info of each message bundle.
	 * <p>Each bundle is associated with a class and a set of files.
	 */
	/*package*/ static class BundleInfo {
		public final Class klass;
		public final String filename;

		private BundleInfo(Class klass, String filename) {
			this.klass = klass;
			this.filename = filename;
		}
		public String toString() {
			return "[" + this.klass.getName() + ", " + this.filename + ']';
		}
	}

	/** Used to handle the mapping of a message code to a filename.
	 *
	 * <p>FUTURE: we might consider to use hashCode or other to represents
	 * the identifier such that the client will have the same code as the server.
	 */
	public static class Aide {
		private static Map _bis = new HashMap(2);

		/** Registers a message filename, and returns an identifier to
		 * represent it.
		 *
		 * <p>The path is assumed to be /metainfo/mesg.
		 *
		 * @param filename the filename without path and extension, e.g, "msgacc"
		 * @return an identifier to represent this message file.
		 */
		public static final int register(Class klass, String filename) {
			if (filename.indexOf('/') >= 0 || filename.indexOf('.') >= 0)
				throw new IllegalArgumentException("Neither path nor extension is allowed: "+filename);

			//The algorithm is to make id as determinstic as possible
			//though not really necessary
			final BundleInfo bi = new BundleInfo(klass, "/metainfo/mesg/" + filename);
			String sID = klass.getName();
			int id = getId(sID);
			synchronized (Aide.class) {
				Integer iID;
				final Map bis = new HashMap(_bis);
				while (bis.containsKey(iID = new Integer(id)))
					++id; //not determinstic
				bis.put(iID, bi);
				_bis = bis; //_bis itself is readonly (so no sync required)
			}
			return id << 16; //as the high word
		}
		private static final int getId(String sID) {
			final int id = sID.hashCode();
			return (id >= 0 ? id: -id) & 0x3fff;
		}
		/** Returns the filename with path, but without extension, of the
		 * specified message code.
		 */
		/*package*/ static final BundleInfo getBundleInfo(int code) {
			final int id = code >>> 16;
			final BundleInfo bi = (BundleInfo)_bis.get(new Integer(id));
			if (bi == null)
				throw new IllegalArgumentException("Wrong message ID: "+id
					+" ("+code+")\nRegistered: "+_bis);
			return bi;
		}
	}
}
