/* B80_ZK_2984Test.java

	Purpose:
		
	Description:
		
	History:
		12:12 PM 11/24/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.bind.proxy.ProxyHelper;

/**
 * @author jumperchen
 */
public class B80_ZK_2984Test {

	@Test
	public void removeFromProxy() {
		ComplexPojo complexPojo = new ComplexPojo();
		Set<Pojo> pojos = complexPojo.getSelectedPojos();
		Pojo pojo100 = new Pojo(100L);
		pojos.add(pojo100);

		ComplexPojo complexPojoProxy = ProxyHelper.createProxyIfAny(complexPojo);

		Pojo pojo200 = new Pojo(200L);

		Set<Pojo> selectedPojosProxy = complexPojoProxy.getSelectedPojos();
		selectedPojosProxy.add(pojo200);

		Assert.assertTrue(selectedPojosProxy.contains(pojo100));
		Assert.assertTrue(selectedPojosProxy.contains(pojo200));

		Assert.assertTrue(selectedPojosProxy.remove(pojo100));
		Assert.assertFalse(selectedPojosProxy.contains(pojo100));
		Assert.assertTrue(selectedPojosProxy.remove(pojo200));
		Assert.assertFalse(selectedPojosProxy.contains(pojo200));
	}


	public static class ComplexPojo {
		private Set<Pojo> selectedPojos = new HashSet<Pojo>();

		public Set<Pojo> getSelectedPojos() {
			return selectedPojos;
		}
	}

	public static class Pojo {
		private Long id;

		public Pojo() {
			super();
		}

		public Pojo(Long id) {
			super();
			this.id = id;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
	}
}