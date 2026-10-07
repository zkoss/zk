/* F110_ZK_6167_sealedTest.java

		Purpose:
				
		Description:
				
		History:
				Sun Sep 27 22:29:05 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.bind.proxy.FormProxyObject;
import org.zkoss.bind.proxy.ProxyHelper;
import org.zkoss.bind.proxy.ViewModelProxyObject;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zkmax.bind.proxy.ProxyHelperEx;

public class F110_ZK_6167_sealedTest {
	@Test
	public void testNestedSealedBeanIsNotProxied() {
		Person person = new Person();
		assertSame(person, ProxyHelper.createProxyIfAny(person));

		Holder origin = new Holder();
		Holder form = ProxyHelper.createFormProxy(origin, Holder.class);
		assertSame(origin.getPerson(), form.getPerson());
		assertSame(origin.getPeople().get(0), form.getPeople().get(0));
		assertInstanceOf(FormProxyObject.class, form.getEmployee());
	}

	@Test
	public void testSealedCollectionIsStillProxied() {
		assertInstanceOf(FormProxyObject.class, ProxyHelper.createProxyIfAny(new Tags()));
	}

	@Test
	public void testSealedFormRoot() {
		assertThrows(UiException.class, () -> ProxyHelper.createFormProxy(new Person(), Person.class));
		assertThrows(UiException.class, () -> ProxyHelper.createFormProxy(null, Shape.class));
		assertInstanceOf(FormProxyObject.class, ProxyHelper.createFormProxy(new Employee(), Employee.class));
	}

	@Test
	public void testSealedViewModel() {
		assertThrows(UiException.class, () -> ProxyHelperEx.createViewModelProxy(new Person()));
		assertInstanceOf(ViewModelProxyObject.class, ProxyHelperEx.createViewModelProxy(new Employee()));
	}

	public static sealed class Person permits Employee {
		private String _name = "a";
		public Person() {}
		public String getName() {
			return _name;
		}
		public void setName(String name) {
			_name = name;
		}
	}

	public static non-sealed class Employee extends Person {
		public Employee() {}
	}

	public static sealed class Tags extends ArrayList<String> permits FinalTags {
		public Tags() {}
	}

	public static final class FinalTags extends Tags {}

	public sealed interface Shape permits Circle {}

	public static final class Circle implements Shape {}

	public static class Holder {
		private Person _person = new Person();
		private Employee _employee = new Employee();
		private List<Person> _people = new ArrayList<>(List.of(new Person()));
		public Holder() {}
		public Person getPerson() {
			return _person;
		}
		public void setPerson(Person person) {
			_person = person;
		}
		public Employee getEmployee() {
			return _employee;
		}
		public void setEmployee(Employee employee) {
			_employee = employee;
		}
		public List<Person> getPeople() {
			return _people;
		}
		public void setPeople(List<Person> people) {
			_people = people;
		}
	}
}
