/* BigList.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct  3 12:32:23     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package zk.example;

import java.util.AbstractList;

/**
 * A big list.
 *
 * @author tomyeh
 */
public class BigList extends AbstractList<Integer> {
	private int _sz;
	public BigList(int sz) {
		if (sz < 0)
			throw new IllegalArgumentException("Negative not allowed: " + sz);
		_sz = sz;
	}
	public int size() {
		return _sz;
	}
	public Integer get(int j) {
		return new Integer(j);
	}
}
