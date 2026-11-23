package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author Dennis Chen
 * 
 */
public class B00605 {
	String value = "A";

	public B00605() {
		
	}

	public String getValue() {
		return value;
	}

	@NotifyChange
	public void setValue(String value) {
		this.value = value;
	}

	
}
