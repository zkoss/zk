package org.zkoss.zktest.bind.issue.zk2592;

import javax.validation.constraints.Size;

public class NameDto {
	
	private String name;

	@Size(min=4, max=10, groups=GroupValidation.class)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
