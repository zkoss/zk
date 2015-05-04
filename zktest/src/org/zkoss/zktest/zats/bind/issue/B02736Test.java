/** B02736.java.

	Purpose:
		
	Description:
		
	History:
		3:50:16 PM Apr 27, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.issue;

import java.math.BigDecimal;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.zkoss.bind.annotation.Immutable;
import org.zkoss.bind.annotation.ImmutableElements;
import org.zkoss.bind.annotation.Transient;
import org.zkoss.bind.proxy.ProxyHelper;

/**
 * @author jumperchen
 *
 */
public class B02736Test {
	@Test
	public void testEnumInProxy() {
		Pojo pojo = new Pojo();
		pojo.setCount(new Integer(1234));
		pojo.setLength(new Double(34.457656));
		pojo.setPrice(new BigDecimal("13.99"));
		Pojo proxy = ProxyHelper.createProxyIfAny(pojo);

		Assert.assertSame(pojo.getCount(), proxy.getCount());
		Assert.assertSame(pojo.getLength(), proxy.getLength());
		Assert.assertSame(pojo.getPrice(), proxy.getPrice()); //ERROR and don't expect to create a proxy here
	}

	public static class Pojo {
		private BigDecimal price;
		private Double length;
		private Integer count;
		@Immutable
		public BigDecimal getPrice() {
			return price;
		}
		public void setPrice(BigDecimal price) {
			this.price = price;
		}
		public Double getLength() {
			return length;
		}
		public void setLength(Double length) {
			this.length = length;
		}
		public Integer getCount() {
			return count;
		}
		public void setCount(Integer count) {
			this.count = count;
		}
	}
}
