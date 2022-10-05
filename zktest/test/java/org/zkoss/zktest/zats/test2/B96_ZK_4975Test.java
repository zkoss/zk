/* B96_ZK_4976Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 4 12:27:22 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.bind.proxy.FormProxyObject;
import org.zkoss.bind.proxy.ProxyHelper;

/**
 * @author jameschu
 */
public class B96_ZK_4975Test {
	@Test
	public void test() {
		User user = new User("nickname", new Person("Peter", new Address("Main Street 5", "London")));
		log("initial user %s", user);

		asFormProxy(user, userProxy -> {
			Person person = userProxy.getPerson();

			asFormProxy(person, personProxy -> {
//				((FormProxyObject) personProxy).resetFromOrigin();
				personProxy.setName("Peter UPDATED");

				Address address = personProxy.getAddress();
				asFormProxy(address, addressProxy -> {
					addressProxy.setStreet("updated street");
				}).submitToOrigin(null);

				personProxy.setName("Peter UPDATED 2");
				log("1 %s", address);
			}); // personProxy is never submitted  (updated address should not ba saved to original user object)

			log("2 %s", person);
		}).submitToOrigin(null);
		log("4 %s", user); // the name change is reverted correctly but the address change is still there

		Assert.assertEquals("person name should be unchanged", "Peter", user.getPerson().getName());
		Assert.assertEquals("address street should be unchanged", "Main Street 5", user.getPerson().getAddress().getStreet());
	}

	private void log(String message, Object... objs) {
		System.out.println(String.format(message, objs));
	}

	public static <T> FormProxyObject asFormProxy(T origin, Consumer<T> update) {
		T fx = ProxyHelper.createFormProxy(origin, origin.getClass());
		update.accept(fx);
		return (FormProxyObject) fx;
	}

	public static class User {
		public String name;
		public Person person;

		public User() {
		}

		public User(String name, Person person) {
			this.name = name;
			this.person = person;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}


		public Person getPerson() {
			return person;
		}

		public void setPerson(Person person) {
			this.person = person;
		}

		@Override
		public String toString() {
			return "Group{" + "name='" + getName() + '\'' + ", person=" + getPerson() + '}';
		}
	}

	public static class Person {
		private String name;
		private Address address;

		public Person() {
		}

		public Person(String name, Address address) {
			this.name = name;
			this.address = address;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Address getAddress() {
			return address;
		}

		public void setAddress(Address address) {
			this.address = address;
		}

		@Override
		public String toString() {
			return "Person{" + "name='" + getName() + '\'' + ", address=" + getAddress() + '}';
		}
	}

	public static class Address {
		private String city;
		private String street;

		public Address() {
		}

		public Address(String street, String city) {
			this.city = city;
			this.street = street;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}

		@Override
		public String toString() {
			return "Address{" + "street='" + getStreet() + '\'' + ", city='" + getCity() + '\'' + '}';
		}
	}
}
