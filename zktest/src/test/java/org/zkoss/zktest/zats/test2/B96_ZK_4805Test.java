/* B96_ZK_3563Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 04 12:46:50 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Test;

import org.zkoss.json.JavaScriptValue;

/**
 * @author jameschu
 */
public class B96_ZK_4805Test {
	@Test
	public void test() throws IOException, ClassNotFoundException {
		JavaScriptValue jsv = new JavaScriptValue("{what: 123, another: 'a'}");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(jsv);
		}
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
			JavaScriptValue newJsv = (JavaScriptValue) ois.readObject();
			assertEquals(jsv.toJSONString(), newJsv.toJSONString());
		}
	}
}
