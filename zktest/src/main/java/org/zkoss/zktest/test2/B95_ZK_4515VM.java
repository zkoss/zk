/* B95_ZK_4515VM.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 28 12:34:21 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author rudyhuang
 */
public class B95_ZK_4515VM {
	private Person person;

	@Command
	public void doAddFamilyName(@BindingParam("form") Person form) {
		form.getFamilyNames().add(new StringValue());
	}

	@Command
	public void doDeleteFamilyName(@BindingParam("form") Person form, @BindingParam("item") StringValue item) {
		form.getFamilyNames().remove(item);
	}

	public Person getPerson() {
		return person;
	}

	@Init
	public void init() {
		person = new Person();
		person.getFamilyNames().add(new StringValue("Snow"));
	}

	@Command
	@NotifyChange("person")
	public void reset() {
	}

	@Command
	public void save() {
	}

	public static class Person {
		private List<StringValue> familyNames;

		public List<StringValue> getFamilyNames() {
			if (familyNames == null) {
				familyNames = new ArrayList<>();
			}
			return familyNames;
		}

		public void setFamilyNames(List<StringValue> familyNames) {
			if (familyNames == null && this.familyNames != null) {
				this.familyNames.clear();
			} else {
				this.familyNames = familyNames;
			}
		}
	}

	public static class StringValue {
		private String value;

		public StringValue() {
			this(null);
		}

		public StringValue(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
