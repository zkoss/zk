/* B70_ZK_2815.java

	Purpose:
		
	Description:
		
	History:
		Mon Nov 2 12:09:00 CST 2015, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;

/**
 * @author jameschu
 */
public class B70_ZK_2815 {
	ListModelList<Val> lm_= new ListModelList<Val>();
	// hold checkbox state and label for one row.

	public B70_ZK_2815() {
		lm_.add(new Val(new Boolean[] { Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE }));
		lm_.add(new Val(new Boolean[] { Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE }));
	}

	@Command
	public void check_click() {

	}
	public ListModel getListModel() {
		return lm_;
	}

	public class Val {
		Boolean[] boxes_= null;
		ListModelList<String> labels_;

		public Val(Boolean[] boxes) {
			boxes_= boxes;
			labels_= new ListModelList<String>();
			for (int i = 0; i < boxes_.length; ++i)
				labels_.add("Checkbox " + i);
		}

		public Boolean[] getStates() {
			return boxes_;
		}
		public ListModel getLabels() {
			return labels_;
		}
	}
}