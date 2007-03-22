/* JVMs.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Mar 21 20:29:16     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.lang;

/**
 * Utilities of Java Virtual Machine.
 *
 * @author tomyeh
 */
public class JVMs {
	private JVMs() {}

	private static final int _major, _minor;
	private static final boolean _j5;
	static {
		final String s = System.getProperty("java.version");
		int major = 0, minor = 0;
		if (s != null) {
			final int j = s.indexOf('.'), k = s.indexOf('.', j + 1);
			if (k > j) {
				try {
					major = Integer.parseInt(s.substring(0, j));
					minor = Integer.parseInt(s.substring(j + 1, k));
				} catch (Throwable ex) { //ignore
					System.err.println("Warning: unable to parse java.versin: "+s);
				}
			}
		}
		_major = major;
		_minor = minor;
		_j5 = (_major == 1 && _minor >= 5) || _major > 1;
	}

	/** Returns whether JVM is 5.0 or above
	 */
	public static final boolean isJava5() {
		return _j5;
	}
	/** Returns the major version.
	 */
	public static final int getMajorVersion() {
		return _major;
	}
	/** Returns the minor version.
	 */
	public static final int getMinorVersion() {
		return _minor;
	}
}
