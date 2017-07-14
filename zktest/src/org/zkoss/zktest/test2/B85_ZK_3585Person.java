/* B85_ZK_3585Person.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 10 16:43:51 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rudyhuang
 */
public class B85_ZK_3585Person {
	private String firstName;
	private String lastName;
	private String city;

	private List<String> collections = new ArrayList<String>(Arrays.asList("Hello", "World"));
	private Map<String, String> maps = new HashMap<String, String>();
	private B85_ZK_3585Person child = null;

	public B85_ZK_3585Person() {
	}

	public B85_ZK_3585Person(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getCity() {
		return city;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<String> getCollections() {
		return collections;
	}

	public void setCollections(List<String> collections) {
		this.collections = collections;
	}

	public Map<String, String> getMaps() {
		return maps;
	}

	public void setMaps(Map<String, String> maps) {
		this.maps = maps;
	}

	public B85_ZK_3585Person getChild() {
		return child;
	}

	public void setChild(B85_ZK_3585Person child) {
		this.child = child;
	}
}
