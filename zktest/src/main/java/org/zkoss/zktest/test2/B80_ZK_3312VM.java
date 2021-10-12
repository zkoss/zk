package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;


public class B80_ZK_3312VM {
	private List originalList;
	private List copiedList;
	private List selectedList;
	private Set selectedSet;

	@Init
	public void init() {
		originalList = new ArrayList();
		selectedList = new ArrayList();
		selectedSet = new LinkedHashSet();
		originalList.add("a");
		originalList.add("b");
		originalList.add("c");
		copiedList = new ArrayList(originalList);
	}

	@Command
	public void addToSelection(@BindingParam("index") int index) {
		selectedList.add(originalList.get(index));
		selectedSet.add(originalList.get(index));
		BindUtils.postNotifyChange(null, null, selectedSet, ".");
		BindUtils.postNotifyChange(null, null, selectedList, ".");
	}

	@Command
	@NotifyChange("originalList")
	public void remove() {
		originalList.remove(0);
	}

	@Command
//	@NotifyChange("copiedList")
	public void refreshCopyList() {
		BindUtils.postNotifyChange(null, null, copiedList, ".");
	}

	public List getOriginalList() {
		return originalList;
	}

	public List getSelectedList() {
		return selectedList;
	}

	public Set getSelectedSet() {
		return selectedSet;
	}

	public List getCopiedList() {
		return copiedList;
	}
}