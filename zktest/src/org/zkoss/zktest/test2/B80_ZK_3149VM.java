package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.*;
import org.zkoss.bind.proxy.FormProxyObject;

public class B80_ZK_3149VM {
	private B80_ZK_3149Object obj;
	@Init
	public void init() {
		obj = new B80_ZK_3149Object("Peter", "orz");
	}

	public void setObj(B80_ZK_3149Object obj) {
		this.obj = obj;
	}

	@Command("save")
	@NotifyChange("obj")
	public void save() {

	}

	@Command("cancel")
	public void cancel(@BindingParam("form") FormProxyObject form) {
		//dummy
	}

	public B80_ZK_3149Object getObj() {
		return obj;
	}
}
