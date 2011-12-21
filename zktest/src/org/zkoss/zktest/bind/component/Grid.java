package org.zkoss.zktest.bind.component;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;



public class Grid{
	private boolean open = false;

	public boolean isOpen() {
		return open;
	}
	


	// -----------command -----------------
	@Command @NotifyChange("*")
	public void open(){
		open = true;
	}

}
