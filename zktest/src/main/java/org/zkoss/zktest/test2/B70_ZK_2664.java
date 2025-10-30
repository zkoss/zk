package org.zkoss.zktest.test2;

import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModelList;

public class B70_ZK_2664 {

	private ListModelList<String> listModelList;

	@Init
	public void init(){
		listModelList = new ListModelList<String>();

		listModelList.add("item 1");
		listModelList.add("item 2");
		listModelList.add("item 3");
		listModelList.add("item 4");
		listModelList.add("item 5");
	}


	@Command
	@NotifyChange("model")
	public void remove(@BindingParam("item") String item) {
		listModelList.remove(item);
	}  
	
	public List<String> getModel() {
		return this.listModelList;
	}	
}
