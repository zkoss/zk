/* SimpleListModel.java

{{IS_NOTE
	$Id: SimpleListModel.java,v 1.2 2006/02/27 03:55:15 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu Aug 18 15:40:14     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

/**
 * A simple implementation of {@link ListModel}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:55:15 $
 */
public class SimpleListModel extends AbstractListModel {
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
