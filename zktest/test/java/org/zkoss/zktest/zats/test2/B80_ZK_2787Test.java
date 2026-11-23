/* B80_ZK_2787Test.java

	Purpose:
		
	Description:
		
	History:
		5:30 PM 1/5/16, Created by jumperchen

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.sql.Time;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.zkoss.bind.annotation.ImmutableFields;
import org.zkoss.bind.proxy.ProxyHelper;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author jumperchen
 */
public class B80_ZK_2787Test extends ZATSTestCase {
	@Test
	public void testZK2787() {
		connect();
	}
	@Test
	public void testFormBindingWithImmutableFieldsOnMethod() {
		FooZK2787 foo = new FooZK2787();
		FooZK2787 formProxy = ProxyHelper.createFormProxy(foo, foo.getClass());
		assertNotSame(foo.getBar(), formProxy.getBar());
		assertSame(foo.getBar().getDate(), formProxy.getBar().getDate());
	}

	@Test
	public void testFormBindingWithImmutableFieldsOnClass() {
		FooZK2787_1 foo = new FooZK2787_1();
		FooZK2787_1 formProxy = ProxyHelper.createFormProxy(foo, foo.getClass());
		assertNotSame(foo.getBar(), formProxy.getBar());
		assertSame(foo.getBar().getDate(), formProxy.getBar().getDate());
	}

	@Test
	public void testFormBindingWithoutImmutableFields() {
		FooZK2787_2 foo = new FooZK2787_2();
		FooZK2787_2 formProxy = ProxyHelper.createFormProxy(foo, foo.getClass());
		assertNotSame(foo.getBar(), formProxy.getBar());
		assertNotSame(foo.getBar().getNum(), formProxy.getBar().getNum());
	}


	public static class FooZK2787 {
		private BarZK2787 _bar;
		public FooZK2787() {
			_bar = new BarZK2787();
		}
		public void setBar(BarZK2787 bar) {
			_bar = bar;
		}
		@ImmutableFields
		public BarZK2787 getBar() {
			return _bar;
		}
	}
	public static class BarZK2787 {
		private Date _date = new Date();
		public BarZK2787() {}
		public void setDate(Date newDate) {
			_date = newDate;
		}
		public Date getDate() {
			return _date;
		}
	}

	public static class FooZK2787_1 {
		private BarZK2787_1 _bar;
		public FooZK2787_1() {
			_bar = new BarZK2787_1();
		}
		public void setBar(BarZK2787_1 bar) {
			_bar = bar;
		}
		public BarZK2787_1 getBar() {
			return _bar;
		}
	}

	@ImmutableFields
	public static class BarZK2787_1 {
		private Time _date = new Time(new Date().getTime());
		public BarZK2787_1() {}
		public void setDate(Time newDate) {
			_date = newDate;
		}
		public Time getDate() {
			return _date;
		}
	}

	public static class FooZK2787_2 {
		private BarZK2787_2 _bar;
		public FooZK2787_2() {
			_bar = new BarZK2787_2();
		}
		public void setBar(BarZK2787_2 bar) {
			_bar = bar;
		}
		public BarZK2787_2 getBar() {
			return _bar;
		}
	}

	public static class BarZK2787_2 {
		private AtomicInteger _num = new AtomicInteger();
		public BarZK2787_2() {}
		public void setNum(AtomicInteger newNum) {
			_num = newNum;
		}
		public AtomicInteger getNum() {
			return _num;
		}
	}
}
