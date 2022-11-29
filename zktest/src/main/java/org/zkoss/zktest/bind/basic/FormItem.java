package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.annotation.NotifyChange;

public class FormItem {
	public FormItem() {
	}

	String value;

	public FormItem(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@NotifyChange
	public void setValue(String value) {
		this.value = value;
	}
}
