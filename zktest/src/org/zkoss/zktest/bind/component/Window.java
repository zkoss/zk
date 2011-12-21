package org.zkoss.zktest.bind.component;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;



public class Window{
	private boolean maximized = false;
	private Integer index1 = 3;
	private Integer index2 = 3;

	public boolean isMaximized() {
		return maximized;
	}

	public Integer getIndex1() {
		return index1;
	}


	public void setIndex1(Integer index1) {
		this.index1 = index1;
	}


	public Integer getIndex2() {
		return index2;
	}


	public void setIndex2(Integer index2) {
		this.index2 = index2;
	}


	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}


	// -----------command -----------------
	
	@Command @NotifyChange("*")
	public void max(){
		maximized = true;
	}
	
	@Command @NotifyChange("*")
	public void min(){
		maximized = false;
	}

}
