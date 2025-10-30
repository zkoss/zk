/** B02736.java.

	Purpose:
		
	Description:
		
	History:
		3:50:16 PM Apr 27, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.issue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.bind.annotation.Immutable;
import org.zkoss.bind.proxy.ProxyHelper;

/**
 * @author jumperchen
 *
 */
public class B02736Test {
	@Test
	public void testEnumInProxy() {
		B02736Pojo pojo = new B02736Pojo();
		pojo.setCount(new Integer(1234));
		pojo.setLength(new Double(34.457656));
		pojo.setPrice(new BigDecimal("13.99"));
		B02736Pojo proxy = ProxyHelper.createProxyIfAny(pojo);

		Assertions.assertSame(pojo.getCount(), proxy.getCount());
		Assertions.assertSame(pojo.getLength(), proxy.getLength());
		Assertions.assertSame(pojo.getPrice(), proxy.getPrice()); //ERROR and don't expect to create a proxy here
	}

	public static class B02736Pojo {
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
