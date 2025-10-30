/* B102_ZK_5680VM.java

        Purpose:
                
        Description:
                
        History:
                Fri Mar 28 15:27:01 CST 2025, Created by rebeccalai

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;

public class B102_ZK_5680VM {
	@Command
	public void error(){
		throw new NullPointerException("An application error");
	}
}
