package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class B01699IncludeMultipleTimes {
	
	private String src = "./B01699IncludeMultipleTimesInner.zul";

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}
	
	@Command @NotifyChange("src")
	public void page2(){
		src = "./B01699IncludeMultipleTimesInner2.zul";
	}
}
