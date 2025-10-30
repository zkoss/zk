package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class B01595SelectedItems {
	private List<String> model;
	private List<String> selected = new ArrayList<String>();
	
	public B01595SelectedItems() {
		generateModel(10);
	}

	private void generateModel(int size) {
		model = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			model.add(String.valueOf(i));
		}
	}
	
	public List<String> getSelected() {
		return selected;
	}

	public void setSelected(List<String> selected) {
		this.selected = selected;
	}

	public List<String> getModel() {
		return model;
	}
}