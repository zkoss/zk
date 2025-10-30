/* LivePersonListModel.java

		Purpose:
		
		Description:
		
		History:
				Mon May 10 12:00:20 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.AbstractListModel;

public class LivePersonListModel extends AbstractListModel<Person> {

	private PersonDao personDao;
	private int pageSize = 10;
	private final static String CACHE_KEY= LivePersonListModel.class + "_cache";

	public LivePersonListModel(PersonDao personDao) {
		this.personDao = personDao;
	}

	public Person getElementAt(int index) {
		Map<Integer, Person> cache = getCache();

		Person targetPerson = cache.get(index);
		if (targetPerson == null) {
			//if cache doesn't contain target object, query a page size of data starting from the index
			List<Person> pageResult = personDao.findAll(index, pageSize);
			int indexKey = index;
			for (Person o : pageResult ){
				cache.put(indexKey, o);
				indexKey++;
			}
		} else {
			return targetPerson;
		}

		//get the target after query from database
		targetPerson = cache.get(index);
		if (targetPerson == null) {
			//if we still cannot find the target object from database, there is inconsistency between memory and the database
			throw new RuntimeException("Element at index " + index + " cannot be found in the database.");
		} else {
			return targetPerson;
		}
	}

	private Map<Integer, Person> getCache() {
		Execution execution = Executions.getCurrent();
		//we only reuse this cache in one execution to avoid accessing detached objects.
		//our filter opens a session during a HTTP request
		Map<Integer, Person> cache = (Map) execution.getAttribute(CACHE_KEY);
		if (cache == null) {
			cache = new HashMap<>();
			execution.setAttribute(CACHE_KEY, cache);
		}
		return cache;
	}

	public int getSize() {
		return personDao.findAllSize();
	}

	// rest of the class body
}