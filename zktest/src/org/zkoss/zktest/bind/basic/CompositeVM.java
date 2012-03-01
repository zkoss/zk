package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class CompositeVM {

	String name = "Dennis";
	String value = "100";
	String title = "RD";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Command
	@NotifyChange({ "name", "value","title" })
	public void reset() {
		name = "Lin";
		value = "34";
		value = "MVP";
	}

}
