package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B80_ZK_3299VM {
	private boolean checked1 = false;
	private boolean checked2 = false;
	private String desc = "ddd";

	public B80_ZK_3299VM() {
	}
	

	@Command
	@NotifyChange("item")
	public void cmd() {
	}

	public boolean isChecked2() {
		return checked2;
	}

	public void setChecked2(boolean checked2) {
		this.checked2 = checked2;
	}

	public boolean isChecked1() {
		return checked1;
	}

	public void setChecked1(boolean checked1) {
		this.checked1 = checked1;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
