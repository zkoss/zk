/* B85_ZK_3907Test.java

        Purpose:
                
        Description:
                
        History:
                Thu May 17 11:00 AM:51 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.zkoss.web.servlet.Charsets.getPreferredLocale;

import java.util.Locale;

import jakarta.servlet.ServletRequest;

import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import org.zkoss.lang.Library;
import org.zkoss.web.Attributes;

@NotThreadSafe
public class B85_ZK_3907Test {
	
	@Test
	public void checkGetPreferredLocale() {
		ServletRequest serverRequest = Mockito.mock(ServletRequest.class);
		try {
			Library.setProperty(Attributes.PREFERRED_LOCALE, "es_PY");
			Locale l = getPreferredLocale(null, serverRequest);
			Assert.assertEquals("es", l.getLanguage());
			Assert.assertEquals("PY", l.getCountry());
		} finally {
			Library.setProperty(Attributes.PREFERRED_LOCALE, null);
		}
	}
}
