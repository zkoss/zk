package org.zkoss.zktest.bind.issue.zk2592;

import javax.validation.constraints.Size;

public class NameDto {
	
	@Size(min=4, max=10)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
