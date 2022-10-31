package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B86_ZK_4026VM {
	private boolean shown = true;

	public boolean isShown() {
		return shown;
	}

	public void setShown(boolean shown) {
		this.shown = shown;
	}

	@Command
	@NotifyChange("shown")
	public void toggleShown() {
		setShown(!isShown());
	}

	@Command
	public void notifyShown() {
		BindUtils.postNotifyChange(null, null, this, "shown");
	}
}
