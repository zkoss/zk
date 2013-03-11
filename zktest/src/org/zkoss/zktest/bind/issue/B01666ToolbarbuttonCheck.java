package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B01666ToolbarbuttonCheck {
	boolean checked;
	
	String message;

	public B01666ToolbarbuttonCheck() {
		checked = true;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public String getMessage(){
		return message;
	}

	@Command @NotifyChange("message")
	public void check(){
		message = "checked "+checked;
	}
}
