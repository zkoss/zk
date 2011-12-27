package org.zkoss.zktest.bind.comp;

import org.zkoss.bind.annotation.NotifyChange;

public class RadiogroupSelectedVM {

	String selected = "itemB";

	public String getSelected() {
		return selected;
	}

	@NotifyChange
	public void setSelected(String selected) {
		this.selected = selected;
	}

}
