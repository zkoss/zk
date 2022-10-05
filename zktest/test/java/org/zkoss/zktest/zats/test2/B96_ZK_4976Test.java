/* B96_ZK_4976Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 4 12:27:22 CST 2022, Created by jameschu

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.bind.proxy.FormProxyObject;
import org.zkoss.bind.proxy.ProxyHelper;
import org.zkoss.util.Locales;

/**
 * @author jameschu
 */
public class B96_ZK_4976Test {
	@Test
	public void test() {
		Group group = new Group("users",
				asList(new Person("Peter", asList(new Address("Main Street 5", "London"))),
						new Person("Carl", asList())));
		log("initial group %s", group);

		asFormProxy(group, groupProxy -> {
			Person person = groupProxy.getMemberList().get(0);
			asFormProxy(person, personProxy -> {
				personProxy.getAddressList().add(new Address("test", "test"));
			}).submitToOrigin(null); // directly updates the original group object
			log("after submit person %s", group);
		});//.submitToOrigin(null); //not submitted here - group should not be updated
		log("group at end %s", group);

		Assert.assertEquals("user(0) should have 1 address", 1, group.getMemberList().get(0).getAddressList().size());
	}

	private void log(String message, Object... objs) {
		System.out.println(String.format(message, objs));
	}

	public static <T> FormProxyObject asFormProxy(T origin, Consumer<T> update) {
		T fx = ProxyHelper.createFormProxy(origin, origin.getClass());
		update.accept(fx);
		return (FormProxyObject) fx;
	}

	public static class Group {
		public String name;
		public List<Person> memberList;

		public Group() {
		}

		public Group(String name, List<Person> memberList) {
			this.name = name;
			this.memberList = new ArrayList<>(memberList);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<Person> getMemberList() {
			return memberList;
		}

		public void setMemberList(List<Person> memberList) {
			this.memberList = memberList;
		}

		@Override
		public String toString() {
			return "Group{" +
					"name='" + getName() + '\'' +
					", memberList=" + getMemberList() +
					'}';
		}
	}

	public static class Person {
		private String name;
		private List<Address> addressList;

		public Person() {
		}

		public Person(String name, List<Address> addressList) {
			this.name = name;
			this.addressList = new ArrayList<>(addressList);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<Address> getAddressList() {
			return addressList;
		}

		public void setAddressList(List<Address> addressList) {
			this.addressList = addressList;
		}

		@Override
		public String toString() {
			return "Person{" +
					"name='" + getName() + '\'' +
					", addressList=" + getAddressList() +
					'}';
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
			return "Address{" +
					"street='" + getStreet() + '\'' +
					", city='" + getCity() + '\'' +
					'}';
		}
	}
}
