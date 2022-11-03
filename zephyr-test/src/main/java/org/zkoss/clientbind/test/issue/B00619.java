package org.zkoss.clientbind.test.issue;

import org.zkoss.bind.annotation.NotifyChange;

public class B00619 {

	String selectedTab = "tabB";

	public String getSelectedTab() {
		return selectedTab;
	}

	@NotifyChange
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	
}
