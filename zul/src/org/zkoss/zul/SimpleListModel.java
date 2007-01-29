/* SimpleListModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 18 15:40:14     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * A simple implementation of {@link ListModel}.
 * Note: It assumes the content is immutable. If not, use {@link ListModelList}
 * instead.
 *
 * @author tomyeh
 * @see ListModelSet
 * @see ListModelList
 * @see ListModelMap
 */
public class SimpleListModel extends AbstractListModel
implements java.io.Serializable {
    private static final long serialVersionUID = 20060707L;

	private final Object[] _data;

	public SimpleListModel(Object[] data) {
		if (data == null)
			throw new NullPointerException();
		_data = data;
	}

	//-- ListModel --//
	public int getSize() {
		return _data.length;
	}
	public Object getElementAt(int j) {
		return _data[j];
	}
}
