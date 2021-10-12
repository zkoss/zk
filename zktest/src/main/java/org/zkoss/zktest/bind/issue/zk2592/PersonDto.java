package org.zkoss.zktest.bind.issue.zk2592;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;


public class PersonDto {
	
	private NameDto nameDto;

	private int age;
	private int age2;

	@Max(value=120, groups=GroupValidation.class)
	public int getAge() {
		return age;
	}
	@Max(value=100)
	public int getAge2() {
		return age2;
	}
	public void setAge2(int age2) {
		this.age2 = age2;
	}
	public void setAge(int age) {
		this.age = age;
	}

	@Valid
	@NotNull
	public NameDto getNameDto() {
		return nameDto;
	}

	public void setNameDto(NameDto nameDto) {
		this.nameDto = nameDto;
	}
}
