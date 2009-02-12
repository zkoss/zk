/* MessageConst.java

{{IS_NOTE

	Purpose: Defines message types
	Description:
	History:
	 2001/4/2 2001/4/2, Tom M. Yeh: Created.

}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.mesg;

import java.util.List;
import java.util.ArrayList;

import org.zkoss.lang.D;
import org.zkoss.lang.Objects;

/**
 * Defines the constants of message types.
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
	public static class BundleInfo {
		public final Class klass;
		public final String filename;

		private BundleInfo(Class klass, String filename) {
			this.klass = klass;
			this.filename = filename;
		}
		public String toString() {
			return "[" + this.klass + ", " + this.filename + ']';
		}
	}

	/** Used to handle the mapping of a message code to a filename.
	 *
	 * <p>FUTURE: we might consider to use hashCode or other to represents
	 * the identifier such that the client will have the same code as the server.
	 */
	public static class Aide {
		private static BundleInfo[] _bis = new BundleInfo[0];

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
			if (klass == null)
				throw new IllegalArgumentException("null class");

			filename = "/metainfo/mesg/" + filename;
			final BundleInfo bi = new BundleInfo(klass, filename);
			synchronized (Aide.class) {
				final int sz = _bis.length + 1;
				final List bis = new ArrayList(sz);
				for (int j = 0; j < _bis.length; ++j) {
					if (_bis[j].filename.equals(filename)
					|| _bis[j].klass.equals(klass))
						throw new IllegalStateException("Replicate message: "+bi
							+"\nRegistered message files: "+Objects.toString(bis));
					bis.add(_bis[j]);
				}
				bis.add(bi);
				_bis = (BundleInfo[])bis.toArray(new BundleInfo[sz]);
				return (sz << 16); //as the high word; starting from (1<<16)
			}
		}
		/** Returns the filename with path, but without extension, of the
		 * specified message code.
		 */
		public static final BundleInfo getBundleInfo(int code) {
			final BundleInfo[] bis = _bis; //so no sync is required
			final int j = (code >>> 16) - 1; //starting from 1
			if (j < 0 || j >= bis.length)
				throw new IllegalArgumentException("Wrong message code: "+code
					+"\nRegistered messages: "+Objects.toString(bis));
			return bis[j];
		}
	}
}
