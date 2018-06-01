/* B85_ZK_3907Test.java

        Purpose:
                
        Description:
                
        History:
                Thu May 17 11:00 AM:51 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.zkoss.lang.Library;
import org.zkoss.web.Attributes;

import javax.servlet.AsyncContext;

import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import static org.zkoss.web.servlet.Charsets.*;


public class B85_ZK_3907Test {
	
	@Test
	public void checkGetPreferredLocale() {
		ServletRequest serverRequest = Mockito.mock(ServletRequest.class);
		Library.setProperty(Attributes.PREFERRED_LOCALE, "es_PY");
		Locale l = getPreferredLocale(null, serverRequest);
		Assert.assertEquals("es", l.getLanguage());
		Assert.assertEquals("PY", l.getCountry());
	}
}
