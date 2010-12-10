/* B36Killme.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Dec 21, 2009 2:41:04 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkdemo.userguide.Person;

/**
 * @author henrichen
 *
 */
public class B2919037 extends GenericForwardComposer {
	private Person person;
	private List personen;
	
	public B2919037() {
		
		personen = new ArrayList();
		
		Person p = new Person("Bruce", "Lee");
		personen.add(p);
		
		p = new Person("Chuck", "Norris");
		personen.add(p);
	}
	
	public void setPerson(Person person) {
		this.person = person;
	}
	
	public Person getPerson() {
		return null;
	}
	
	public List getPersonen() {
		return personen;
	}
}
