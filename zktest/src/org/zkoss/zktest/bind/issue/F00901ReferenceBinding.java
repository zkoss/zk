package org.zkoss.zktest.bind.issue;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;


public class F00901ReferenceBinding {

	
	User user = new User("Dennis");

	@Command @NotifyChange("user")
	public void replace1(){
		user = new User("Alex");
		Profile p = new Profile("888");
		user.setProfile(p);
		p.setAddress(new Address("888 st"));
	}
	
	@Command @NotifyChange("user")
	public void replace2(){
		Profile p = new Profile("999");
		p.setAddress(new Address("999 st"));
		user.setProfile(p);
		
		BindUtils.postNotifyChange(null, null, user, "profile");
	}
	
	
	public User getUser() {
		return user;
	}

	public static class User {
		String name;
		Profile profile;

		public User(String name){
			this.name = name;
			profile = new Profile("1234");
		}
		
		public Profile getProfile() {
			return profile;
		}

		public void setProfile(Profile profile) {
			this.profile = profile;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static class Profile {
		String number;

		Address address;
		
		public Profile(String number) {
			this.number = number;
			address = new Address("11 street");
		}

		public Address getAddress() {
			return address;
		}

		public void setAddress(Address address) {
			this.address = address;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

	}

	public static class Address {
		String street;

		public Address(String street) {
			this.street = street;
		}

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}

		

	}
}