/* B80_ZK_3282Test.java

	Purpose:
		
	Description:
		
	History:
		Tue, Aug 23, 2016 12:17:13 PM, Created by Sefi

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkmax.zul.Chosenbox;
import org.zkoss.zktest.zats.ZATSTestCase;
/**
 * 
 * @author Sefi
 */


public class B80_ZK_3282Test extends ZATSTestCase{

	@Test
	public void test() throws IOException, ClassNotFoundException {
		Component componentIn = new Chosenbox();
		componentIn.setId("test" + componentIn.getClass().getSimpleName());
		Component componentOut = writeAndReadBack(componentIn);
		Assert.assertEquals(componentIn.getClass(), componentOut.getClass());
		Assert.assertEquals(componentIn.getId(), componentOut.getId());
	}

	private Component writeAndReadBack(Component component) throws IOException, ClassNotFoundException {
		//serialize
		ByteArrayOutputStream dataOut = new ByteArrayOutputStream();
		ObjectOutputStream serOut = new ObjectOutputStream(dataOut);
		serOut.writeObject(component);
		
		//deserialize
		ObjectInputStream serIn = new ObjectInputStream(new ByteArrayInputStream(dataOut.toByteArray()));
		return (Component) serIn.readObject();
	}
}