/* B80_ZK_2994VM.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 29 15:53:28 CST 2015, Created by Jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModelList;

/**
 * @author jameschu
 */
public class B80_ZK_2994VM {
	private ListModelList model = new ListModelList();
	private String child = "vlayout";

	// remove all entries above limit
	public ListModelList getFilteredView() {
		model.add(Integer.valueOf(1));
		model.add(Integer.valueOf(2));
		model.remove(0);
		model.remove(0);
		return model;
	}

	public ListModelList getModel() {
		return model;
	}

	public String getChild() {
		return child;
	}

	@Command
	public void refresh() {
		BindUtils.postNotifyChange(null, null, this, "child");
	}
}