package org.zkoss.zktest.bind.databinding.el;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zktest.bind.databinding.bean.Person;

public class EL3VM {
	private Person person;
	private int number = 254;
	private double convertedBackValue;
	private List<String> names;
	private String filter = "John";

	@Init
	public void init() {
		person = new Person();
		person.setFirstName("Dennis");
		person.setLastName("Watson");
		names = new ArrayList<>();
		names.add("John Cena");
		names.add("John Cusack");
		names.add("John Travolta");
		names.add("Dennis Watson");
	}

	@Command
	@NotifyChange("convertedBackValue")
	public void convertBack(Double key) {
		convertedBackValue = key;
	}

	public double getConvertedBackValue() {
		return convertedBackValue;
	}

	public int getNumber() {
		return number;
	}

	public List<String> getNames() {
		return names;
	}

	public String getFilter() {
		return filter;
	}

	public Person getPerson() {
		return person;
	}
}
