package org.zkoss.clientbind.test.issue;

import org.zkoss.bind.annotation.Init;

public class F00638Base2 extends F00638Base{

	@Init(superclass=false)
	public void init2(){
		this.value1 = "X2";
		this.value3 = "Y2";
	}
}
