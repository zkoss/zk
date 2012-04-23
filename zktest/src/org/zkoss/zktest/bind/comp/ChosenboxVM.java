package org.zkoss.zktest.bind.comp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.annotation.Command;

public class ChosenboxVM {

	private List<String> items = new ArrayList<String>();
	private Collection<String> selected = new LinkedHashSet<String>();
	private int selectedIndex =-1;

	public ChosenboxVM(){
		for(int i=0;i<20;i++){
			items.add("Item "+i);
		}
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public List<String> getItems() {
		return items;
	}

	
	
	public Collection<String> getSelected() {
		return selected;
	}

	public void setSelected(Collection<String> selected) {
		this.selected = selected;
	}

	@Command
	public void clear(){
		selected.clear();
		selectedIndex = -1;
	}

}
