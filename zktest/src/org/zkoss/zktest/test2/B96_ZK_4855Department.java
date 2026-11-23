package org.zkoss.zktest.test2;

import java.util.List;

public class B96_ZK_4855Department {
	private String name;
	private List<B96_ZK_4855Employee> employees;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setEmployees(List<B96_ZK_4855Employee> employees) {
		this.employees = employees;
	}

	public List<B96_ZK_4855Employee> getEmployees() {
		return employees;
	}
}
