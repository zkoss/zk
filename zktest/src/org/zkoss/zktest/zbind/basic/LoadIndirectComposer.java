/* ZKBindLoad1Composer.java

	Purpose:
		
	Description:
		
	History:
		Aug 2, 2011 1:01:07 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.zktest.zbind.basic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindComposer;
import org.zkoss.bind.DependsOn;
import org.zkoss.bind.NotifyChange;

/**
 * @author Dennis Chen
 *
 */
public class LoadIndirectComposer extends BindComposer {

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
	private String currField = "firstName";
	
	private Person _p2;
	
	public LoadIndirectComposer() {
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
	
	int i=0;

	public String getCurrField() {
		return currField;
	}

	@NotifyChange({"currField"})
	public void setCurrField(String currField) {
		this.currField = currField;
	}

	@NotifyChange({"currField"})
	public void toFirstName() {
		currField = "firstName";
	}
	
	@NotifyChange({"currField"})
	public void toLastName() {
		currField = "lastName";
	}
	
	@NotifyChange({"currField"})
	public void toFullName() {
		currField = "fullName";
	}
	
	
	public Person getP2(){
		return _p2;
	}
	
	@NotifyChange({"p1","p2"})
	public void saveForm(){
		_p2 = new Person(_selected.getFirstName(),_selected.getLastName());
	}
}
