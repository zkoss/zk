package org.zkoss.zktest.bind.databinding.globalcommand;

import org.zkoss.bind.annotation.DefaultGlobalCommand;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

public class MainViewModel {
	private boolean visible = true;

	@NotifyChange("visible")
	@GlobalCommand
	public void show() {
		visible = true;
	}

	@NotifyChange("visible")
	@GlobalCommand
	public void hide() {
		visible = false;
	}

	public boolean isVisible() {
		return visible;
	}

	@DefaultGlobalCommand
	public void defaultAction() {
		Clients.log("default action");
	}
}
