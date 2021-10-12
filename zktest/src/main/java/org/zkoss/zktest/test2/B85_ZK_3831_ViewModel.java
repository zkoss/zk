package org.zkoss.zktest.test2;/* B85_ZK_3831_ViewModel.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 16 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/

import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Page;

public class B85_ZK_3831_ViewModel {

    @Init
    public void test(@ContextParam(ContextType.PAGE) Page page){
        System.out.println(page);
    }
    public String getSomeValue() { return "someValue"; }
}
