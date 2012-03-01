package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

public class CompositeVM {

	String name = "Dennis";
	String value = "100";

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

	@Command
	@NotifyChange({ "name", "value" })
	public void reset() {
		name = "Lin";
		value = "34";
	}

}
