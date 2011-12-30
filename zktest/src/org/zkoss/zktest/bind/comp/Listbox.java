package org.zkoss.zktest.bind.comp;




public class Listbox{

	private String[] items = {"item01","item02","item03","item04"};
	private String selected;
	private String index;
	private boolean open =false;
	
	public String[] getItems() {
		return items;
	}
	
	public String getSelected() {
		return selected;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

}
