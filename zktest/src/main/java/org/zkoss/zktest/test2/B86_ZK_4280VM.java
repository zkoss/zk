package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;

public class B86_ZK_4280VM {
	private boolean val1;
	private boolean val2;

	private List innerList;

	@Init
	public void init() {
		innerList = new ArrayList();
		innerList.add("listitem1");
		innerList.add("listitem2");
		innerList.add("listitem3");
	}

	public boolean isVal1() {
		return val1;
	}
	public boolean isVal2() {
		return val2;
	}
	public List getInnerList() {
		return innerList;
	}

	@Command
	public void toggleVal1() {
		val1 = !val1;
		BindUtils.postNotifyChange(null, null, this, "val1");
	}
	@Command
	public void toggleVal2() {
		val2 = !val2;
		BindUtils.postNotifyChange(null, null, this, "val2");
	}
}
