package org.zkoss.zktest.bind.issue.zk2592;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;


public class PersonDto {
	
	@Valid
	@NotNull
	private NameDto nameDto;

	@Max(120)
	private int age;

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public NameDto getNameDto() {
		return nameDto;
	}

	public void setNameDto(NameDto nameDto) {
		this.nameDto = nameDto;
	}
}
