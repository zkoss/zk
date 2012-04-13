package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class F00986CloseWindow {

	boolean detached;
	
	
	public boolean isDetached() {
		return detached;
	}


	public void setDetached(boolean detached) {
		this.detached = detached;
	}


	@Command @NotifyChange("detached")
	public void detach(){
		detached = true;
	}
	
}