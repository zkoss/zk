/* B96_ZK_4509Combobox.java

		Purpose:

		Description:

		History:
				Thu Oct 28 10:25:04 CST 2021, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite for testing ZK 8 combobox where the model is set in an onOpen listener
 */
public class B96_ZK_4509Combobox extends Combobox implements AfterCompose {

	private List<String> descriptions = new ArrayList<>();
	private int categoryListsize = 10;

	public void afterCompose() {
		setAutodrop(true);
		addEventListener(Events.ON_OPEN, new OnOpenEventListener());
	}

	private void refreshDescriptions() {
		descriptions.clear();
		populateDescriptions();
	}

	private void populateDescriptions() {
		for (int i = 1; i <= categoryListsize; i++) {
			descriptions.add("Category " + i);
		}
		// increase size for next call to mimic database record changes
		categoryListsize++;
	}

	/**
	 * Listener to handle the OnOpen events.
	 */
	class OnOpenEventListener implements EventListener<Event> {
		@Override
		public void onEvent(final Event evt) throws Exception {
			if (B96_ZK_4509Combobox.this.isOpen()) {
				refreshDescriptions();
				setModel(new ListModelList<Object>(descriptions));
			}
		}
	}

}