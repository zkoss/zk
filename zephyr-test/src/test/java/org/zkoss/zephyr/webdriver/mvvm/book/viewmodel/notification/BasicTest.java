/* BasicTest.java

	Purpose:
		
	Description:
		
	History:
		Tue May 04 18:41:36 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.viewmodel.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class BasicTest extends WebDriverTestCase {
	@Test
	public void testBasic() {
		connect("/mvvm/book/viewmodel/notification/basic.zul");
		final JQuery id = jq("$id");
		final JQuery name = jq("$name");
		final JQuery city = jq("$city");
		final JQuery pojo = jq("$pojo");
		String oldId = id.val();
		String oldName = name.val();
		String oldCity = city.val();
		String oldPojo = pojo.text();
		assertNotEquals(null, oldId);
		assertNotEquals("", oldName);
		assertNotEquals("", oldCity);
		assertEquals("pojo", oldPojo);

		click(jq("$ramBtn1"));
		waitResponse();
		String newId = id.val();
		String newName = name.val();
		String newCity = city.val();
		String newPojo = pojo.text();
		assertNotEquals(oldId, newId);
		assertNotEquals(oldName, newName);
		assertNotEquals(oldCity, newCity);
		assertNotEquals(oldPojo, newPojo);
		oldId = newId;
		oldName = newName;
		oldCity = newCity;
		oldPojo = newPojo;

		click(jq("$ramBtn2"));
		waitResponse();
		newPojo = pojo.text();
		assertNotEquals(oldId, id.val());
		assertNotEquals(oldName, name.val());
		assertNotEquals(oldCity, city.val());
		assertNotEquals(oldPojo, newPojo);
		oldPojo = newPojo;

		click(jq("$reset"));
		waitResponse();
		assertEquals("", name.val());
		assertEquals("", city.val());

		click(jq("$updPojoBtn1"));
		waitResponse();
		newPojo = pojo.text();
		assertNotEquals(oldPojo, newPojo);
		oldPojo = newPojo;

		click(jq("$updPojoBtn2"));
		waitResponse();
		newPojo = pojo.text();
		assertNotEquals(oldPojo, newPojo);
		oldPojo = newPojo;

		click(jq("$updPojoBtn3"));
		waitResponse();
		assertNotEquals(oldPojo, pojo.text());
	}

	@Test
	public void testAuto() {
		doTest("/mvvm/book/viewmodel/notification/basic-auto.zul");
	}

	@Test
	public void testSmart() {
		doTest("/mvvm/book/viewmodel/notification/basic-smart.zul");
	}

	private void doTest(String url) {
		connect(url);
		final JQuery id = jq("$id");
		final JQuery name = jq("$name");
		final JQuery city = jq("$city"); 
		final String oldId = id.val();
		final String oldName = name.val();
		final String oldCity = city.val();
		assertNotEquals(null, oldId);
		assertNotEquals("", oldName);
		assertNotEquals("", oldCity);

		click(jq("$rnd"));
		waitResponse();
		assertNotEquals(oldId, id.val());
		assertNotEquals(oldName, name.val());
		assertNotEquals(oldCity, city.val());

		click(jq("$reset"));
		waitResponse();
		assertEquals("", name.val());
		assertEquals("", city.val());
	}
}
