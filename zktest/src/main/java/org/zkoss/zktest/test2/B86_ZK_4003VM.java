package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeSet;

public class B86_ZK_4003VM {
	private final Set<String> availableRoots = new TreeSet<String>();
	private String selectedRootItem;
	private final Set<BigDecimal> availableChildren = new TreeSet<BigDecimal>();
	private BigDecimal selectedChildItem;

	public Set<String> getAvailableRoots() {
		return availableRoots;
	}

	public void setAvailableRoots(Set<String> availableRoots) {
		this.availableRoots.clear();
		this.availableRoots.addAll(availableRoots);
		BindUtils.postNotifyChange(null, null, this, "availableRoots");

	}

	public String getSelectedRootItem() {
		return selectedRootItem;
	}

	public void setSelectedRootItem(String selectedRootItem) {
		this.selectedRootItem = selectedRootItem;
		BindUtils.postNotifyChange(null, null, this, "selectedRootItem");
		setSelectedChildItem(null);
		Set<BigDecimal> availableChildren = new TreeSet<>();
		if (selectedRootItem != null) {
			availableChildren.add(BigDecimal.valueOf(Integer.valueOf(selectedRootItem)));
		}
		setAvailableChildren(availableChildren);
	}

	public Set<BigDecimal> getAvailableChildren() {
		return availableChildren;
	}

	public void setAvailableChildren(Set<BigDecimal> availableChildren) {
		this.availableChildren.clear();
		this.availableChildren.addAll(availableChildren);
		BindUtils.postNotifyChange(null, null, this, "availableChildren");
	}

	public BigDecimal getSelectedChildItem() {
		return selectedChildItem;
	}

	public void setSelectedChildItem(BigDecimal selectedChildItem) {
		this.selectedChildItem = selectedChildItem;
		BindUtils.postNotifyChange(null, null, this, "selectedChildItem");

	}

	@Command("loadValues")
	public void loadValues() {
		Set<String> availableRoots = new TreeSet<>();
		availableRoots.add("1");
		availableRoots.add("2");
		setAvailableRoots(availableRoots);
	}

}
