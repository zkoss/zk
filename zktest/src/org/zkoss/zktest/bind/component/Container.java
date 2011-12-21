package org.zkoss.zktest.bind.component;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;



public class Container{
	private boolean open = false;
	private boolean maximized = false;
	private Integer zindex = 3;

	public boolean isOpen() {
		return open;
	}
	
	
	public boolean isMaximized() {
		return maximized;
	}


	public String getZindex() {
		return zindex.toString();
	}


	// -----------command -----------------
	@Command @NotifyChange("*")
	public void toggle(){
		open = !open;
	}
	
	@Command @NotifyChange("*")
	public void max(){
		maximized = true;
	}
	
	@Command @NotifyChange("*")
	public void min(){
		maximized = false;
	}
	
	@Command @NotifyChange("*")
	public void lower(){
		zindex--;
	}
}
