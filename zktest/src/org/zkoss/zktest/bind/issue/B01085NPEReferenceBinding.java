package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class B01085NPEReferenceBinding {
	private List<String> model;
	private String selected;
	
	public B01085NPEReferenceBinding() {
		generateModel(5);
	}

	private void generateModel(int size) {
		model = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			model.add(String.valueOf(i));
		}
	}
	
	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public List<String> getModel() {
		return model;
	}
}