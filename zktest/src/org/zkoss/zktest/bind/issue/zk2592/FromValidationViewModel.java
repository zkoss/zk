package org.zkoss.zktest.bind.issue.zk2592;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.validator.AbstractValidator;

public class FromValidationViewModel {

	private PersonDto personDto;
	
	@Init
	public void init() {
		personDto = new PersonDto();
		
		NameDto nameDto = new NameDto();
		nameDto.setName("Peter");
		personDto.setNameDto(nameDto);
		personDto.setAge(30);
	}
	
	@Command("submit") 
	public void submit() {
		System.out.println("submit");
	}

	public PersonDto getPersonDto() {
		return personDto;
	}

	public Validator getNameValidator() {
		return new AbstractValidator() {
			@Override
			public void validate(ValidationContext ctx) {
				String value = (String) ctx.getProperty().getValue();
				if(value.length() < 3) {
					addInvalidMessage(ctx, "Name too short");
				}
			}
		};
	}
}
