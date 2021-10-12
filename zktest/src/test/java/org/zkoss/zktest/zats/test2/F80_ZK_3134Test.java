/* F80_ZK_3134Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 8 14:12:46 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;


import junit.framework.Assert;
import org.junit.Test;

import org.zkoss.bind.proxy.ProxyHelper;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;

import java.math.BigInteger;
import java.sql.Date;

/**
 * @author jameschu
 */
public class F80_ZK_3134Test extends ZutiBasicTestCase {

	@Test
	public void testEnumInProxy() {
		ZK3107Object object = new ZK3107Object();
		object.setCount(new Integer(1234));
		object.setLength(new Double(34.457656));
		object.setPrice(new BigInteger("13"));
		java.util.Date now = new java.util.Date();
		object.setDate(new Date(now.getTime()));
		ZK3107Object proxy = ProxyHelper.createProxyIfAny(object);
		
		Assert.assertSame(object.getCount(), proxy.getCount());
		Assert.assertSame(object.getLength(), proxy.getLength());
		Assert.assertSame(object.getPrice(), proxy.getPrice()); //ERROR and don't expect to create a proxy here
		Assert.assertSame(object.getDate(), proxy.getDate()); //ERROR and don't expect to create a proxy here
	}

	public static class ZK3107Object {
		private BigInteger price;
		private Date date;
		private Double length;
		private Integer count;

		public BigInteger getPrice() {
			return price;
		}
		public void setPrice(BigInteger price) {
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

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}
	}
}
