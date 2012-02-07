package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class F00638Base3 extends F00638Base2{

	@Init(upward=true)
	public void init3(){
		this.value1 = "A";
	}
}
