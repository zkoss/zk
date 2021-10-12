package org.zkoss.zktest.bind.issue;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.Event;

public class B01791GlobalCommand {

	private String label = "";
	
	@GlobalCommand("testGlobal")
	@NotifyChange("label")
	public void testGlobal(@BindingParam("event") Event event, @BindingParam("param") String param) {
		label = "global: " + event.getName() + ", " + param;
	}

	@Command("testNormal")
	@NotifyChange("label")
	public void testNormal(@BindingParam("event") Event event, @BindingParam("param") String param) {
		label = "normal: " + event.getName() + ", " + param;
	}

	public String getLabel() {
		return label;
	}
}