/* PerformanceTest.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 12:20:31 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.advance.performance;

import static org.hamcrest.Matchers.greaterThan;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class PerformanceTest extends WebDriverTestCase {
	@Test
	public void test() {
		long start, end;
		start = System.nanoTime();
		connect("/mvvm/book/advance/performance/BindPerformance.zul");
		end = System.nanoTime();
		long bindTime = end - start;
		start = System.nanoTime();
		connect("/mvvm/book/advance/performance/SavePerformance.zul");
		end = System.nanoTime();
		long saveTime = end - start;
		start = System.nanoTime();
		connect("/mvvm/book/advance/performance/LoadPerformance.zul");
		end = System.nanoTime();
		long loadTime = end - start;
		start = System.nanoTime();
		connect("/mvvm/book/advance/performance/InitPerformance.zul");
		end = System.nanoTime();
		long initTime = end - start;
		System.out.println("bind: " + bindTime);
		System.out.println("save: " + saveTime);
		System.out.println("load: " + loadTime);
		System.out.println("init: " + initTime);
		MatcherAssert.assertThat(bindTime, greaterThan(saveTime));
		MatcherAssert.assertThat(bindTime, greaterThan(loadTime));
		MatcherAssert.assertThat(bindTime, greaterThan(initTime));
	}
}
