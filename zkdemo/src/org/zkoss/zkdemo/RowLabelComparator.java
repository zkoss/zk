/* RowLabelComparator.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun 14 11:52:13     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zkdemo;

import java.util.Comparator;

import com.potix.zk.ui.Component;
import org.zkoss.zul.Label;

/**
 * It assumes the first child is a label.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class RowLabelComparator implements Comparator {
	private boolean _asc;
	public RowLabelComparator(boolean asc) {
		_asc = asc;
	}
	public int compare(Object o1, Object o2) {
		String s1 = getValue(o1), s2 = getValue(o2);
		int v = s1.compareTo(s2);
		return _asc ? v: -v;
	}
	private String getValue(Object o) {
		return ((Label)((Component)o).getChildren().get(0)).getValue();
	}
}
