/* MyVM2.java

        Purpose:
                
        Description:
                
        History:
                Thu Jan 25 6:26 PM:59 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.issue;

public class B01547_ViewModel {
	public String val = "A";
	public String getValue(){
		return val;
	}
	public void setValue(String value){
		this.val = value;
	}
}