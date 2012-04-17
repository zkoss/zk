package org.zkoss.zktest.bind.issue;

import java.util.Map;

import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.lang.Strings;

public class F01048FormBindingMessage {
	private Person person;

	public Person getPerson() {
		return person;
	}

	public F01048FormBindingMessage() {
		person = new Person();
	}

	@Command
	public void save() {
		System.out.println("Save " + person.getLastName());
	}

	/**
	 * @return validator for expressions (check if expression is not empty)
	 */
	public Validator getValidator() {
		return new AbstractValidator() {
			@Override
			public void validate(ValidationContext ctx) {
				Map<String, Property> formProps = ctx.getProperties(ctx.getProperty().getValue());
				if (Strings.isEmpty((String) formProps.get("firstName").getValue())) {
					addInvalidMessage(ctx, "firstName", "First name is missing.");
				}
				if (Strings.isEmpty((String) formProps.get("lastName").getValue())) {
					addInvalidMessage(ctx, "lastName", "Last name is missing.");
				}
				if (formProps.get("age").getValue() == null) {
					addInvalidMessage(ctx, "age", "Age is missing.");
				}
			}
		};
	}

	static public class Person {
		private String firstName;
		private String lastName;
		private Integer age;

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}
	}
}