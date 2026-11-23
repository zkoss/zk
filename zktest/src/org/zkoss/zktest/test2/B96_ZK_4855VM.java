package org.zkoss.zktest.test2;

import java.util.ArrayList;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;

public class B96_ZK_4855VM {
	private B96_ZK_4855Department dep = new B96_ZK_4855Department();
	private static int counter = 0;

	@Init
	public void init() {
		dep.setEmployees(new ArrayList<>());
		dep.getEmployees().add(new B96_ZK_4855Employee("firstName" + (++counter), "lastName" + (counter)));
	}

	@Command
	public void add(@BindingParam("form") B96_ZK_4855Department form) {
		form.getEmployees().add(0, new B96_ZK_4855Employee("firstName" + (++counter), "lastName" + (counter)));
	}

	@Command
	public void addMultiTimes(@BindingParam("form") B96_ZK_4855Department form) {
		form.getEmployees().add(0, new B96_ZK_4855Employee("firstName" + (++counter), "lastName" + (counter)));
		form.getEmployees().add(0, new B96_ZK_4855Employee("firstName" + (++counter), "lastName" + (counter)));
	}

	@Command
	public void remove(@BindingParam("form") B96_ZK_4855Department form, @BindingParam("employee") B96_ZK_4855Employee employee) {
		form.getEmployees().remove(employee);
	}

	public void setDepartment(B96_ZK_4855Department dep) {
		this.dep = dep;
	}

	public B96_ZK_4855Department getDepartment() {
		return dep;
	}
}
