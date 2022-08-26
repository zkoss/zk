/* F85_ZK_3806Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 14 15:17:22 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.zkoss.bind.impl.ValidationMessagesImpl;
import org.zkoss.bind.sys.ValidationMessages;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class F85_ZK_3806Test {
	private ValidationMessages vmsgs;

	@BeforeEach
	public void setUp() {
		this.vmsgs = new ValidationMessagesImpl();
	}

	@Test
	public void testGetMessagesComponentAttr() {
		Assertions.assertNotNull(vmsgs.getMessages(new Label(), "value"));
	}

	@Test
	public void testGetMessagesComponent() {
		Assertions.assertNotNull(vmsgs.getMessages(new Label()));
	}

	@Test
	public void testGetMessages() {
		Assertions.assertNotNull(vmsgs.getMessages());
	}

	@Test
	public void testGetKeyMessagesComponentKey() {
		Assertions.assertNotNull(vmsgs.getKeyMessages(new Label(), "err1"));
	}

	@Test
	public void testGetKeyMessagesKey() {
		Assertions.assertNotNull(vmsgs.getKeyMessages("err1"));
	}

	@Test
	public void testGetFieldValue() {
		Assertions.assertNull(vmsgs.getFieldValue("err1"));
	}

	@Test
	public void testGetFieldValueComponent() {
		Assertions.assertNull(vmsgs.getFieldValue(new Label(), "err1"));
	}

	@Test
	public void testGetFieldValues() {
		Assertions.assertNotNull(vmsgs.getFieldValues("err1"));
	}

	@Test
	public void testGetFieldValuesComponent() {
		Assertions.assertNotNull(vmsgs.getFieldValues(new Label(), "err1"));
	}

	@Test
	public void testGetAssociate() {
		Assertions.assertNull(vmsgs.getAssociate("test"));
	}

	@Test
	public void testGetAssociates() {
		Assertions.assertNotNull(vmsgs.getAssociates("test"));
	}
}
