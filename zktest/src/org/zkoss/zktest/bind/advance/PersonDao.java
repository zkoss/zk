/* PersonDao.java

		Purpose:
		
		Description:
		
		History:
				Mon May 10 12:09:24 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import java.util.ArrayList;
import java.util.List;

public class PersonDao {
	private List<Person> people = new ArrayList<>();

	public PersonDao() {
		for (int i = 0; i < 1000; i++) {
			people.add(new Person(i, "F" + i, "L" + i, i % 100));
		}
	}

	public List<Person> findAll(){
		return people;
	}

	public List<Person> findAll(int index, int pagesize) {
		return people.subList(index, Math.min(index + pagesize, findAllSize()));
	}

	public int findAllSize() {
		return people.size();
	}
}
