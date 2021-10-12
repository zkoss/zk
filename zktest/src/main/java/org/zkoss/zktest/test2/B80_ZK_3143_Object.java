/** B80_ZK_3143.java.

 Purpose:

 Description:

 History:
 		Tue Mar 15 17:12:46 CST 2016, Created by jameschu

 Copyright (C) 2015 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.test2;

import java.util.LinkedHashMap;
import java.util.Map;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

/**
 * @author jameschu
 *
 */
public class B80_ZK_3143_Object {
	private int id;
	private String firstName = "";
	private String lastName = "";
	private boolean married = false;

	public B80_ZK_3143_Object(Integer id, String firstName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public B80_ZK_3143_Object(Integer id, String firstName, String lastName, boolean married) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.married = married;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isMarried() {
		return married;
	}

	public void setMarried(boolean married) {
		this.married = married;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFullName() {
			return firstName + " " + lastName;
		}
}
