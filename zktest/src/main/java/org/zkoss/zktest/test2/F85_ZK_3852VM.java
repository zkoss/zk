package org.zkoss.zktest.test2;

import java.util.HashSet;
import java.util.Set;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModelList;

public class F85_ZK_3852VM {
	private ListModelList<String> model = new ListModelList<>();
	private Set<String> selected = new HashSet<>();
	
	@Init
	public void init() {
		for (int i = 0; i < 100; i++) {
			model.add("data" + i);
		}
	}
	
	@Command
	@NotifyChange("selected")
	public void create(@BindingParam("item") String item) {
		model.add(item);
		selected.add(item);
	}
	
	public ListModelList<String> getModel() {
		return model;
	}
	
	public Set<String> getSelected() {
		return selected;
	}
	
	public void setSelected(Set<String> selected) {
		this.selected = selected;
	}
}
