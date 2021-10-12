/* F85_ZK_3806Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 14 15:17:22 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.zkoss.bind.impl.ValidationMessagesImpl;
import org.zkoss.bind.sys.ValidationMessages;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class F85_ZK_3806Test {
	private ValidationMessages vmsgs;

	@Before
	public void setUp() {
		this.vmsgs = new ValidationMessagesImpl();
	}

	@Test
	public void testGetMessagesComponentAttr() {
		Assert.assertNotNull(vmsgs.getMessages(new Label(), "value"));
	}

	@Test
	public void testGetMessagesComponent() {
		Assert.assertNotNull(vmsgs.getMessages(new Label()));
	}

	@Test
	public void testGetMessages() {
		Assert.assertNotNull(vmsgs.getMessages());
	}

	@Test
	public void testGetKeyMessagesComponentKey() {
		Assert.assertNotNull(vmsgs.getKeyMessages(new Label(), "err1"));
	}

	@Test
	public void testGetKeyMessagesKey() {
		Assert.assertNotNull(vmsgs.getKeyMessages("err1"));
	}

	@Test
	public void testGetFieldValue() {
		Assert.assertNull(vmsgs.getFieldValue("err1"));
	}

	@Test
	public void testGetFieldValueComponent() {
		Assert.assertNull(vmsgs.getFieldValue(new Label(), "err1"));
	}

	@Test
	public void testGetFieldValues() {
		Assert.assertNotNull(vmsgs.getFieldValues("err1"));
	}

	@Test
	public void testGetFieldValuesComponent() {
		Assert.assertNotNull(vmsgs.getFieldValues(new Label(), "err1"));
	}

	@Test
	public void testGetAssociate() {
		Assert.assertNull(vmsgs.getAssociate("test"));
	}

	@Test
	public void testGetAssociates() {
		Assert.assertNotNull(vmsgs.getAssociates("test"));
	}
}
