package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.NotifyChange;

public class B00810ListboxMultiple {
	private List<String> model;
	private Set<String> selected;
	boolean multiple = true;
	
	public B00810ListboxMultiple() {
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

	@DependsOn("selected")
	public List getSortedSelected(){
		if(selected==null) return null;
		List l = new ArrayList(selected);
		Collections.sort(l);
		return l;
	}

	public List<String> getModel() {
		return model;
	}

	
	public boolean isMultiple() {
		return multiple;
	}
	
	@Command @NotifyChange("multiple")
	public void toggle(){
		multiple = !multiple;
	}
	
	
	@Command @NotifyChange("selected")
	public void update(){
	}
	
}
