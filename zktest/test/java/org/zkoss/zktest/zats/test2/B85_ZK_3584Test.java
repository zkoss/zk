/* B85_ZK_3584Test.java

	Purpose:
		
	Description:
		
	History:
		Fri Jul 07 10:50:20 CST 2017, Created by rudyhuang

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.web.servlet.http.Https;

/**
 * @author rudyhuang
 */
public class B85_ZK_3584Test {
	@Test
	public void testContextNameSameAsServerName() throws Exception {
		HttpServletRequest hreq = mock(HttpServletRequest.class);
		expect(hreq.getRequestURL()).andReturn(new StringBuffer("https://localhost:8080/localhost/test2/aaa.zul")).anyTimes();
		expect(hreq.getRequestURI()).andReturn("/localhost/test2/aaa.zul").anyTimes();
		expect(hreq.getContextPath()).andReturn("/localhost").anyTimes();
		replay(hreq);

		Assert.assertEquals("https://localhost:8080/localhost", Https.getCompleteContext(hreq));
		Assert.assertEquals("https://localhost:8080", Https.getCompleteServerName(hreq));
	}

	@Test
	public void testContextNameDifferentFromServerName() throws Exception {
		HttpServletRequest hreq = mock(HttpServletRequest.class);
		expect(hreq.getRequestURL()).andReturn(new StringBuffer("https://localhost:8080/zktest/test2/aaa.zul")).anyTimes();
		expect(hreq.getRequestURI()).andReturn("/zktest/test2/aaa.zul").anyTimes();
		expect(hreq.getContextPath()).andReturn("/zktest").anyTimes();
		replay(hreq);

		Assert.assertEquals("https://localhost:8080/zktest", Https.getCompleteContext(hreq));
		Assert.assertEquals("https://localhost:8080", Https.getCompleteServerName(hreq));
	}
}
