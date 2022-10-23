/* B95_ZK_4632VM.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 11 17:49:12 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Locale;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModelList;

/**
 * @author rudyhuang
 */
public class B95_ZK_4632VM {
	private ListModelList listModel = new ListModelList(Locale.getAvailableLocales());

	public ListModelList getListModel() {
		return listModel;
	}

	@Command
	@NotifyChange("listModel")
	public void cmd() {

	}
}
