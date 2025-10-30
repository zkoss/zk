package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;

public class B80_ZK_3611VM {

	private Couple couple;

	@Init
	public void init() {
		this.couple = newCouple("Peter", 23, "Mary", 21);
	}

	private Couple newCouple(String name1, Integer age1, String name2, Integer age2) {
		Person p1 = new Person(name1, new PersonalDetails(age1));
		Person p2 = new Person(name2, new PersonalDetails(age2));
		return new Couple(p1, p2);
	}
	
	@Command("incrementAge")
    public void incrementAge(@BindingParam("person") Person uiTaxDataValue) {
        PersonalDetails details = uiTaxDataValue.getPersonalDetails();
        details.setAge(details.getAge() + 1);
        BindUtils.postNotifyChange(null,  null, details, "age");
	}
	
	public Couple getCouple() {
		return couple;
	}

	public static class PersonalDetails {
		private Integer age;
		public PersonalDetails(Integer age) {
			this.age = age;
		}
		public Integer getAge() {
			return age;
		}
		public void setAge(Integer age) {
			this.age = age;
		}
	}
	
	public static class Person {
		private String name;
		private PersonalDetails personalDetails;
		protected Person(String name, PersonalDetails personalDetails) {
			this.name = name;
			this.personalDetails = personalDetails;
		}
		public String getName() {
			return name;
		}
		public PersonalDetails getPersonalDetails() {
			return personalDetails;
		}
	}
	
	public static class Couple {
	    private Person person1;
	    private Person person2;
	    public Couple(Person person1, Person person2) {
			this.person1 = person1;
			this.person2 = person2;
		}
		public Person getPerson1() {
	        return person1;
	    }    
	    public Person getPerson2() {
	        return person2;
	    }
	}
}
