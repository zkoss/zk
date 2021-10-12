/* ChildrenBindingListModelDataEvent.java

	Purpose:
		
	Description:
		
	History:
		2015/01/16 Created by James Chu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataEvent;

/**
 * serializable event in children binding (support list model in children binding)
 * @author James Chu
 * @since 8.0.0
 */
public class ChildrenBindingListModelDataEvent extends ListDataEvent implements java.io.Serializable {
	private static final long serialVersionUID = 20150116150500L;

	public ChildrenBindingListModelDataEvent(ListModel<?> model, int type, int index0, int index1) {
		super(model, type, index0, index1);
	}

	public ChildrenBindingListModelDataEvent(ListDataEvent e) {
		super(e.getModel(), e.getType(), e.getIndex0(), e.getIndex1());
	}

}
