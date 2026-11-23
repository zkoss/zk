/* B85_ZK_3965Composer.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun 15 12:52:19 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Window;

/**
 * @author rudyhuang
 */
public class B85_ZK_3965Composer extends GenericForwardComposer<Window> {
	private Radiogroup radiogroup;
	private Radiogroup rg;
	private ListModelList<String> listModelList = new ListModelList<String>();

	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		listModelList.add(null);
		listModelList.add("s1");
		listModelList.add("s2");
		radiogroup.setModel(listModelList);
		listModelList.addToSelection("s1");
	}

	public void onClick$btn(Event e) {
		listModelList.addToSelection("s1");
	}

	public void onClick$btn2(Event e) {
		listModelList.add("s3");
	}

	public void onClick$btn3(Event e) {
		Radio r = new Radio(System.currentTimeMillis() + "");
		r.setSelected(true);
		r.setRadiogroup(rg);
		r.setPage(e.getPage());
	}
}
