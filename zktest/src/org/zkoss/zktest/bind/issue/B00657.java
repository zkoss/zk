package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModelList;

public class B00657 {
	int selIndex;
	ListModelList<String> selBox;
	
	public B00657(){
		selIndex = 0;
		selBox = new ListModelList<String>();
		selBox.add("A");
		selBox.add("B");
		selBox.add("C");
		selBox.add("D");
		selBox.add("E");
	}

	public int getSelIndex() {
		return selIndex;
	}

	@NotifyChange
	public void setSelIndex(int selIndex) {
		this.selIndex = selIndex;
	}

	public ListModelList<String> getSelBox() {
		return selBox;
	}
}
