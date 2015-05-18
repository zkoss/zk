/** B02737Test.java.

	Purpose:
		
	Description:
		
	History:
		3:50:16 PM Apr 27, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.issue;

import junit.framework.Assert;

import org.junit.Test;
import org.zkoss.bind.annotation.Transient;
import org.zkoss.bind.proxy.FormProxyObject;
import org.zkoss.bind.proxy.ProxyHelper;

/**
 * @author jumperchen
 *
 */
public class B02737Test {
	@Test
	public void testEnumInProxy() {
		B02737Pojo pojo = new B02737Pojo();
		pojo.setWidth(50);
		pojo.setHeight(45);
		B02737Pojo proxy = ProxyHelper.createProxyIfAny(pojo);

		Assert.assertEquals(50, proxy.getWidth());
		Assert.assertEquals(45, proxy.getHeight());
		Assert.assertEquals(2250, proxy.getAreal());

		proxy.setHeight(100);
		
		Assert.assertEquals(50, proxy.getWidth());
		Assert.assertEquals(100, proxy.getHeight());
		Assert.assertEquals(5000, proxy.getAreal()); //expecting not to cache the purely calculated value (if no setter is present)
		
		((FormProxyObject)proxy).submitToOrigin(null);
		
		Assert.assertEquals(50, pojo.getWidth());
		Assert.assertEquals(100, pojo.getHeight());
		Assert.assertEquals(5000, pojo.getAreal()); //expecting not to cache the purely calculated value (if no setter is present)
	}

//	A limitation case, we cannot support the sub-delegator object
	@Test
	public void testEnumInProxy2() {
		B02737Pojo pojo = new B02737Pojo();
		pojo.setWidth(50);
		pojo.setHeight(45);
		B02737Pojo proxy = ProxyHelper.createProxyIfAny(pojo);

		Assert.assertEquals(50, proxy.getWidth());
		Assert.assertEquals(45, proxy.getHeight());
		Assert.assertEquals(2250, proxy.getAreal());

		proxy.getSubPojo().setWidth(100);
		
		Assert.assertEquals(100, proxy.getWidth());
		Assert.assertEquals(45, proxy.getHeight());
		Assert.assertEquals(4500, proxy.getAreal()); //expecting not to cache the purely calculated value (if no setter is present)
		
		((FormProxyObject)proxy).submitToOrigin(null);
		
		Assert.assertEquals(100, pojo.getWidth());
		Assert.assertEquals(45, pojo.getHeight());
		Assert.assertEquals(4500, pojo.getAreal()); //expecting not to cache the purely calculated value (if no setter is present)
	}
	public static class B02737Pojo {
		private int height;
		private SubPojo _sub;
		public B02737Pojo() {
			_sub = new SubPojo();
		}
		public SubPojo getSubPojo() {
			return _sub;
		}
		@Transient
		public int getWidth() {
			return getSubPojo().getWidth();
		}
		public void setWidth(int width) {
			getSubPojo().setWidth(width);
		}
		public int getHeight() {
			return height;
		}
		public void setHeight(int height) {
			this.height = height;
		}

		public void setAreal(long d) {
			
		}
		//readonly (or transient) field
		@Transient
		public long getAreal() {
			return (long)getWidth() * getHeight();
		}
	}
	public static class SubPojo {
		private int width;
		public int getWidth() {
			return width;
		}
		public void setWidth(int width) {
			this.width = width;
		}
	}
}
