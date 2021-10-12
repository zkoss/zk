package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;

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
