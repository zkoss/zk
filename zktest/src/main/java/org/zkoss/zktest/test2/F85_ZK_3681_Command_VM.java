package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class F85_ZK_3681_Command_VM {
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Command
	@NotifyChange("status")
	public void onCheck(@BindingParam("checked") boolean checked) {
		status = checked ? "Checked" : "Not checked";
	}
}
