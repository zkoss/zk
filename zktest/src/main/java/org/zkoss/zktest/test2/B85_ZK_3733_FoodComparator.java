package org.zkoss.zktest.test2;

import org.zkoss.zul.GroupComparator;

import java.io.Serializable;
import java.util.Comparator;

public class B85_ZK_3733_FoodComparator implements Comparator<B85_ZK_3733_Food>, GroupComparator<B85_ZK_3733_Food>, Serializable {
	private static final long serialVersionUID = 1L;
	
	@Override
	public int compare(B85_ZK_3733_Food o1, B85_ZK_3733_Food o2) {
		return o1.getCategory().compareTo(o2.getCategory().toString());
	}
	
	@Override
	public int compareGroup(B85_ZK_3733_Food o1, B85_ZK_3733_Food o2) {
		return o1.getCategory().equals(o2.getCategory()) ? 0 : 1;
	}
}