/* B86_ZK_3922VM.java

	Purpose:

	Description:

	History:
		Tue Jan 22 12:34:41 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.List;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.validator.AbstractValidator;

/**
 * @author jameschu
 */
public class B86_ZK_3922VM {
	private Record record = new Record();
	private List<FormField> fields = Arrays.asList(new FormField("name"), new FormField("address"));

	public Record getRecord() {
		return record;
	}

	public List<FormField> getFields() {
		return fields;
	}

	public Validator getValidator() {
		return new AbstractValidator() {
			@Override
			public void validate(ValidationContext ctx) {
				FormField field = (FormField) ctx.getValidatorArg("field");
				String value = (String) ctx.getProperty().getValue();
				if (value.toLowerCase().startsWith("x")) {
					addInvalidMessage(ctx, field.getId(), field.getId() + " may not start with 'X'");
				}
			}
		};
	}

	public static class Record {
		String name;
		String address;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}
	}

	public static class FormField {
		private String id;

		public FormField(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}
	}
}