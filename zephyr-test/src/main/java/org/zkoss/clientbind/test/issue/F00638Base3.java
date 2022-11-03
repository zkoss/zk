package org.zkoss.clientbind.test.issue;

import org.zkoss.bind.annotation.Init;

public class F00638Base3 extends F00638Base2 {

	@Init(superclass=true)
	public void init3(){
		this.value1 = "A";
	}
}
