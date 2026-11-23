package org.zkoss.zktest.bind.comp;

import org.zkoss.bind.annotation.NotifyChange;

public class TabboxSelectedVM {

	String selectedTab1 = "tabC";
	String selectedTab2 = "tabB";
	public String getSelectedTab1() {
		return selectedTab1;
	}
	@NotifyChange
	public void setSelectedTab1(String selectedTab1) {
		this.selectedTab1 = selectedTab1;
	}
	public String getSelectedTab2() {
		return selectedTab2;
	}
	@NotifyChange
	public void setSelectedTab2(String selectedTab2) {
		this.selectedTab2 = selectedTab2;
	}

	
}
