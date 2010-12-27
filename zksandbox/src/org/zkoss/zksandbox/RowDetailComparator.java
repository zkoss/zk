/* RowLabelComparator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun 14 11:52:13     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zksandbox;

import java.util.Comparator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;

/**
 * It assumes the first child is a label.
 *
 * @author jumperchen
 * @since 3.5.0
 */
public class RowDetailComparator implements Comparator {
	private boolean _asc;
	public RowDetailComparator(boolean asc) {
		_asc = asc;
	}
	public int compare(Object o1, Object o2) {
		String s1 = getValue(o1), s2 = getValue(o2);
		int v = s1.compareTo(s2);
		return _asc ? v: -v;
	}
	private String getValue(Object o) {
		return ((Label)((Component)o).getChildren().get(1)).getValue();
	}
}
