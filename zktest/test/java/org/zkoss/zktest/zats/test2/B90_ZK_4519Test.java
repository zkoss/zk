/* B90_ZK_4519Test.java

		Purpose:
		
		Description:
		
		History:
				Wed Mar 04 12:40:19 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Test;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.SimpleDateConstraint;

public class B90_ZK_4519Test {
	@Test
	public void testBeforeConstraintStringConstructor() {
		new SimpleDateConstraint("before 20200308:should before 0020200308");
	}
	
	@Test
	public void testAfterConstraintStringConstructor() {
		new SimpleDateConstraint("after 20200308:should after 0020200308");
	}
	
	@Test
	public void testBetweenConstraintStringConstructor() {
		new SimpleDateConstraint("between 20200302 and 20200308:should between 0020200302 and 0020200308");
	}
	
	@Test
	public void testComplexConstraintStringConstructor() {
		new SimpleDateConstraint("no today,between 20200302 and 20200308:should between 0020200302 and 0020200308, before 20200305:should before 0020200305");
	}
	
	@Test(expected = UiException.class)
	public void testBadComplexConstraintStringConstructor() {
		new SimpleDateConstraint("no today,between 20200302 and 20200308:should between 20200302 and 20200308, before 120200305:should before 120200305");
	}
}
