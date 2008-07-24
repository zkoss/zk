/* Copyright (C) 2008 Kinetic Networks Inc. All rights reserved.
 */
package org.zkoss.zkdemo.test2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sjaini
 * @version Jul 2, 2008
 */
public class BizService {
	public class Repository {

		@Override
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

	private ArrayList<Repository> repositories;

	public void addNewRepository(List list) throws Exception {
		this.repository = new Repository(drivers.get(this.repositories.size() % drivers.size()), "name"
				+ System.currentTimeMillis());
		list.add(repository);
	}

	public BizService() throws Exception {
		super();
		drivers = new ArrayList<String>();
		drivers.add("A");
		drivers.add("B");
		drivers.add("C");

		repositories = new ArrayList<Repository>();
		for (int i = 0; i < 5; i++) {
			repositories.add(new Repository(drivers.get(i % drivers.size()), "Name-" + i));
		}
	}

	List<String> drivers;

	public List<String> getDrivers() {

		return drivers;
	}

	public List<Repository> getRepositories() {
		return repositories;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}
