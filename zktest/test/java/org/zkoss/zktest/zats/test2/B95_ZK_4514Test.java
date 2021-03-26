/* B95_ZK_4514Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 12 11:01:30 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zktest.test2.B95_ZK_4514_AuDropUploader;
import org.zkoss.zktest.test2.B95_ZK_4514_AuUploader;

/**
 * @author rudyhuang
 */
public class B95_ZK_4514Test {
	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private Session session;

	@Before
	public void setUp() throws Exception {
		SessionsCtrl.setCurrent(session);

		when(request.getMethod()).thenReturn("POST");
		when(request.getContentType()).thenReturn("multipart/form-data");
		when(request.getParameter(anyString())).thenReturn(" ");
		when(request.getParameter("sid")).thenAnswer(invocation -> {
			throw new FileUploadBase.IOFileUploadException(
					"A fake file upload exception", new IOException("A fake IO exception"));
		});
	}

	@After
	public void tearDown() throws Exception {
		SessionsCtrl.setCurrent((Session) null);
	}

	@Test
	public void testAuUploader() throws Exception {
		B95_ZK_4514_AuUploader au = spy(new B95_ZK_4514_AuUploader());
		au.service(request, response, "/");

		verify(au).handleError(anyObject());
	}

	@Test
	public void testAuDropUploader() throws Exception {
		B95_ZK_4514_AuDropUploader au = spy(new B95_ZK_4514_AuDropUploader());
		au.service(request, response, "/");

		verify(au).handleError(anyObject());
	}
}
