package org.zkoss.zktest.bind.comp;




public class Selectbox{

	private String[] items = {"item01","item02","item03","item04"};
	private int selectedIndex;
	
	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public String[] getItems() {
		return items;
	}
	

}
