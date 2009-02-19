/* FileDateCompare.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 14, 2008 9:27:41 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkdemo.test2;

import java.util.Comparator;
import java.util.Date;

/**
 * @author jumperchen
 */
public class FileDateCompare implements Comparator {

	private boolean _asc;

	public FileDateCompare(boolean asc) {
		_asc = asc;
	}

	public int compare(Object o1, Object o2) {
		Date s1 = new Date(((java.io.File) o1).lastModified()), s2 = new Date(
				((java.io.File) o2).lastModified());
		int v = s1.compareTo(s2);
		return _asc ? v : -v;
	}

}
