/* B85_ZK_3749_ViewModel.java

        Purpose:
                
        Description:
                
        History:
                Mon Jan 29 5:24 PM:52 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;

public class B85_ZK_3749_ViewModel {

	private ListModelList<String> list;

	@Init
	public void init() {
		list = new ListModelList<String>(
			new String[] {"a", "b", "c", "d", "e", "a", "b", "c", "d", "e", "a", "b", "c", "d", "e" }
		) ;
		list.setPageSize(2);
	}

	public ListModelList<String> getList() {
		return list;
	}
}