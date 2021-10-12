package org.zkoss.zktest.test2;

import java.util.AbstractList;

public class B60_ZK_1512_BigList extends AbstractList<Integer> {
	private int size;

	public B60_ZK_1512_BigList(int sz) {
		if (sz < 0)
			throw new IllegalArgumentException("Negative not allowed: " + sz);
		size = sz;
	}

	public int size() {
		return size;
	}

	public Integer get(int j) {
		return Integer.valueOf(j);
	}
}
