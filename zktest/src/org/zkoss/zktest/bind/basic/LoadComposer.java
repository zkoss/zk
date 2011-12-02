/* ZKBindLoad1Composer.java

	Purpose:
		
	Description:
		
	History:
		Aug 2, 2011 1:01:07 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.bind.basic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindComposer;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author Dennis Chen
 *
 */
public class LoadComposer extends BindComposer {

	public class Person {
		private String firstName;
		private String lastName;
		private Date birthday;
		private boolean gender;
		private String phone;
		private Address address;
		
		public Person(String fname, String lname) {
			firstName = fname;
			lastName = lname;
		}
		
		//fire property change to base.firstName
		@NotifyChange
		public void setFirstName(String n) {
			this.firstName = n;
		}
		public String getFirstName() {
			return this.firstName;
		}
		
		@NotifyChange
		public void setLastName(String n) {
			this.lastName = n;
		}
		public String getLastName() {
			return this.lastName;
		}
		
		@DependsOn({"firstName", "lastName"})
		public String getFullName() {
			return this.firstName + " " + this.lastName;
		}
		
		@NotifyChange
		public void setBirthday(Date d) {
			this.birthday = d;
		}
		public Date getBirthday() {
			return this.birthday;
		}
		
		@NotifyChange
		public void setGender(boolean male) {
			this.gender = male;
		}
		public boolean getGender() {
			return this.gender;
		}
		
		@NotifyChange
		public void setPhone(String p) {
			this.phone = p;
		}
		public String getPhone() {
			return this.phone;
		}
		
		@NotifyChange
		public void setAddress(Address addr) {
			address = addr;
		}
		public Address getAddress() {
			if (address == null) {
				address = new Address("","");
			}
			return this.address;
		}

		@DependsOn({"address.street", "address.zip"})
		public String getFullAddr() {
			return address == null ? null : (address.getStreet() + " " + address.getZip());
		}
		
	}	
	public class Address {
		private String _zip;
		private String _street;
		public Address(String street, String zip) {
			_zip = zip;
			_street = street;
		}
		public String getZip() {
			return _zip;
		}
		public String getStreet() {
			return _street;
		}
		@NotifyChange
		public void setZip(String zip) {
			_zip = zip;
		}
		@NotifyChange
		public void setStreet(String street) {
			_street = street;
		}
	}
	private Person _selected;
	private List<Person> _persons;
	
	private Person _p2;
	
	public LoadComposer() {
		_persons = new ArrayList<Person>();
		for(int j = 0; j < 4; ++j) {
			_persons.add(new Person("First"+j , "Last"+j));
		}
		_selected = _persons.get(1); //2nd person
		Address addr = new Address("87 Zhengzhou Road #11F-2 Taipei", "103");
		_selected.setAddress(addr);
	}
	
	public Person getSelected() {
		return _selected;
	}
	@NotifyChange
	public void setSelected(Person p) {
		_selected = p;
	}
	
	public List<Person> getPersons() {
		return _persons;
	}
	
	public Person getP1() {
		return getSelected();
	}

	@Command @NotifyChange({"p1","selected"})
	public void changeFirstName1() {
		_selected.setFirstName("Dennis");
	}
	@Command 
	public void changeLastName1() {
		_selected.setLastName("Chen");
		notifyChange(_selected, "lastName");
	}
	
	@Command @NotifyChange({"p1","selected"})
	public void changeFirstName2() {
		_selected.setFirstName("Alex");
	}
	
	@Command 
	public void changeLastName2() {
		_selected.setLastName("Wang");
		notifyChange(_selected, "lastName");
	}
	
	@Command @NotifyChange({"p1"})
	public void notifyP1() {
		_selected.setFirstName("Ian");
		_selected.setLastName("Tasi");
	}
	
	@Command @NotifyChange({"selected"})
	public void notifySelected() {
		_selected.setFirstName("Jumper");
		_selected.setLastName("Chen");
	}
	
	public Person getP2(){
		return _p2;
	}
	
	@Command @NotifyChange({"p1","p2"})
	public void saveForm(){
		_p2 = new Person(_selected.getFirstName(),_selected.getLastName());
	}
}
