/* BasicVM.java

	Purpose:
		
	Description:
		
	History:
		Tue May 04 18:13:42 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.test.book.viewmodel.notification;

import java.security.SecureRandom;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author rudyhuang
 */
public class BasicVM {
	private int id;
	private String name;
	private String city;
	private POJO pojo;

	@Init
	public void init() {
		id = new SecureRandom().nextInt();
		name = generateRandomString();
		city = generateRandomString();
		pojo = new POJO("pojo");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Command
	@NotifyChange("*")
	public void randomizeWithAsterisk() {
		id = new SecureRandom().nextInt();
		name = generateRandomString();
		city = generateRandomString();
		pojo.setName("pojo" + new SecureRandom().nextInt());
	}

	@Command
	@NotifyChange(".")
	public void randomizeWithDot() {
		id = new SecureRandom().nextInt();
		name = generateRandomString();
		city = generateRandomString();
		pojo.setName("pojo" + new SecureRandom().nextInt());
	}

	@Command
	@NotifyChange({"name", "city"})
	public void reset() {
		name = "";
		city = "";
	}

	@Command
	@NotifyChange("pojo")
	public void updatePojo1() {
		pojo.setName("pojo" + new SecureRandom().nextInt());
	}

	@Command
	public void updatePojo2() {
		pojo.setName("pojo" + new SecureRandom().nextInt());
		BindUtils.postNotifyChange(pojo, ".");
	}

	@Command
	public void updatePojo3() {
		pojo.setName("pojo" + new SecureRandom().nextInt());
		BindUtils.postNotifyChange(pojo, "name");
	}

	private String generateRandomString() {
		return new SecureRandom().ints(10, 'A', 'z' + 1)
				.filter(Character::isAlphabetic)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

	public POJO getPojo() {
		return pojo;
	}

	public void setPojo(POJO pojo) {
		this.pojo = pojo;
	}

	class POJO {
		private String name;

		POJO(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
