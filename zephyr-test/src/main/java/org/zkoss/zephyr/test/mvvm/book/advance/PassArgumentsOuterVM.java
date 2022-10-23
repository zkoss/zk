/* PassArgumentsOuterVM.java

		Purpose:
		
		Description:
		
		History:
				Mon May 10 16:22:31 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.mvvm.book.advance;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class PassArgumentsOuterVM {
	String myArgument = "myArgument";

	String src = "/mvvm/book/advance/PassArgumentsInner.zul";

	public String getMyArgument() {
		return myArgument;
	}

	public String getSrc(){
		return src;
	}

	@Command
	@NotifyChange("*")
	public void cmd() {
		myArgument = "cmd";
	}
}
