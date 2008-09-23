/* Copyright (C) 2008 Kinetic Networks Inc. All rights reserved.
 */
//TestCase used with B30-2010019.zul
package org.zkoss.zkdemo.test2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sjaini
 * @version Jul 2, 2008
 */
public class BizService {
	public class Repository {

		public boolean equals(Object obj) {
			return super.equals(obj);
		}

		private String driver;

		private String name;

		private Repository(String driver, String name) throws Exception {
			super();
			this.setDriver(driver);
			this.name = name;
		}

		public String getDriver() {
			return driver;
		}

		public String getName() {
			return name;
		}

		public void setDriver(String driver) throws Exception {
			if (driver == null)
				throw new Exception("Why is it doing this, when it was " + this.driver);

			this.driver = driver;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	private Repository repository;

	private ArrayList repositories;

	public void addNewRepository(List list) throws Exception {
		this.repository = new Repository((String)drivers.get(this.repositories.size() % drivers.size()), "name"
				+ System.currentTimeMillis());
		list.add(repository);
	}

	public BizService() throws Exception {
		super();
		drivers = new ArrayList();
		drivers.add("A");
		drivers.add("B");
		drivers.add("C");

		repositories = new ArrayList();
		for (int i = 0; i < 5; i++) {
			repositories.add(new Repository((String)drivers.get(i % drivers.size()), "Name-" + i));
		}
	}

	List drivers;

	public List getDrivers() {

		return drivers;
	}

	public List getRepositories() {
		return repositories;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}
