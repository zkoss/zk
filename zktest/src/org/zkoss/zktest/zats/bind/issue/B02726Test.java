/** B02726Test.java.

	Purpose:
		
	Description:
		
	History:
		4:03:40 PM Apr 23, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zkoss.bind.proxy.FormProxyObject;
import org.zkoss.bind.proxy.ProxyHelper;

/**
 * @author jumperchen
 *
 */
public class B02726Test {

	@Test
	public void test() {
Pojo pojo = new Pojo();
		
		pojo.setName("asdf");
		
		Pojo proxy = ProxyHelper.createProxyIfAny(pojo);
		proxy.setName(null);
//		proxy.setName(""); //no problem
		try {
			((FormProxyObject)proxy).submitToOrigin(null);
		} catch (Exception e) {
			fail("Should not throw exception!");
		}
	}
	
	public static class Pojo {
		private String name;
		public String getName() {return name;}
		public void setName(String child) {this.name = child;}
	}

}
