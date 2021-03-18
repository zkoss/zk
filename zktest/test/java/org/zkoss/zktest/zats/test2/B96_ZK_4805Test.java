/* B96_ZK_3563Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jan 04 12:46:50 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.zkoss.json.JavaScriptValue;

/**
 * @author jameschu
 */
public class B96_ZK_4805Test {
	@Test
	public void test() {
		try {
			JavaScriptValue jsv = new JavaScriptValue("{what: 123, another: 'a'}");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(jsv);
			oos.close();
			baos.close();
			byte[] bytes = baos.toByteArray();
			ByteArrayInputStream oais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(oais);
			JavaScriptValue newJsv = (JavaScriptValue) ois.readObject();
			assertEquals("{what: 123, another: 'a'}", newJsv.toJSONString());
			oos.close();
			oais.close();
		} catch (Exception x) {
			x.printStackTrace();
			fail(x.getMessage());
		}
	}
}
