package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;

public class B86_ZK_4279VM {
	private String popupid = "a";

	public String getPopupid() {
		return popupid;
	}

	public void setPopupid(String popupid) {
		this.popupid = popupid;
	}

	@Command
	public void changepp() {
		popupid = popupid.concat("a");
		BindUtils.postNotifyChange(null, null, this, "popupid");
	}
}
