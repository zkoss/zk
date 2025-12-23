/* B85_ZK_3585VM.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 10 16:45:49 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.apache.commons.lang3.RandomStringUtils;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author rudyhuang
 */
public class B85_ZK_3585VM {
	private static final String[] CITIES = { "Beijing", "Delhi", "Dhaka", "Karachi", "S\u00e3o Paulo", "Shanghai" };

	private static String getRandomCity() {
		return CITIES[(int) (Math.random() * CITIES.length)];
	}

	private B85_ZK_3585Person person;

	@Command
	public void doRandomCity(@BindingParam("form") B85_ZK_3585Person form) {
		if (form != null) {
			final String old = form.getCity();
			String city;
			do {
				city = getRandomCity();
			} while (city.equals(old));
			form.setCity(city);
		}
	}

	@Command
	public void doCollections(@BindingParam("form") B85_ZK_3585Person form) {
		if (form != null) {
			form.getCollections().add("123");
		}
	}

	@Command
	public void doMaps(@BindingParam("form") B85_ZK_3585Person form) {
		if (form != null) {
			form.getMaps().put(
					RandomStringUtils.randomAlphabetic(5),
					RandomStringUtils.randomAlphabetic(5)
			);
		}
	}

	@Command
	public void doNested(@BindingParam("form") B85_ZK_3585Person form) {
		if (form != null) {
			form.getChild().setFirstName(RandomStringUtils.randomAlphabetic(5));
		}
	}

	@Command
	@NotifyChange("person")
	public void doReset() {
	}

	@Command
	@NotifyChange("person")
	public void doSave() {
	}

	public B85_ZK_3585Person getPerson() {
		return person;
	}

	@Init
	public void init() {
		person = new B85_ZK_3585Person("John", "Doe");
		person.setCity(getRandomCity());
		// Nested property
		person.setChild(new B85_ZK_3585Person("Johnny", "Walker"));
	}
}
