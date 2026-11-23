package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class B01529SelectedItemsIndex {
	private List<String> model;
	private Set<String> selected;
	
	public B01529SelectedItemsIndex() {
		generateModel(10);
	}

	private void generateModel(int size) {
		model = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			model.add(String.valueOf(i));
		}
	}
	
	public Set<String> getSelected() {
		return selected;
	}

	public void setSelected(Set<String> selected) {
		this.selected = selected;
	}

	public List<String> getModel() {
		return model;
	}
}