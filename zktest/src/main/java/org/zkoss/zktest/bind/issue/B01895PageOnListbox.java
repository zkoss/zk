package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B01895PageOnListbox {

	private List<String> list4;
	private List<String> list3;

	private List<String> getGeneratedList() {
		List<String> l = new ArrayList<String>();
		for (int i = 0; i < 50; i++) {
			l.add("Row " + i);
		}
		return l;
	}

	public List<String> getList1() {
		return getGeneratedList();
	}

	public List<String> getList2() {
		return getGeneratedList();
	}

	public List<String> getList3() {
		return list3;
	}

	public List<String> getList4() {
		return list4;
	}

	@Command
	public void populateList3() {
		list3 = getGeneratedList();
	}

	@Command @NotifyChange("list4")
	public void populateList4() {
		list4 = getGeneratedList();
	}
}