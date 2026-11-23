package org.zkoss.zktest.bind.issue;

import static java.lang.System.out;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.zul.ListModelList;

public class B0020 {
	private List<String> fruitList;

	public B0020() {
		fruitList = new LinkedList<String>();
		for(int i=0;i<5;i++){
			fruitList.add("Item "+i);
		}

	}

	public List<String> getFruitList() {
		return new ListModelList(fruitList);
	}

	// FIXME can NOT remove last 2 items
	// -----------command -----------------
	@Command @NotifyChange("fruitList")
	public void delete(@BindingParam("index") Integer index) {
		out.println(index);
		fruitList.remove(index.intValue());
		out.println("size:" + fruitList.size());
	}
}
