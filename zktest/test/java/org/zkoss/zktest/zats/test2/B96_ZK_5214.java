/* B96_ZK_5214.java

	Purpose:
		
	Description:
		
	History:
		5:34 PM 2022/9/29, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

import org.zkoss.util.Locales;

/**
 * @author jumperchen
 */
public class B96_ZK_5214 {
	@Test
	public void test() {
		assertEquals(Locale.forLanguageTag("zh-TW-u-ca-roc"), Locales.getLocale("zh-TW-u-ca-roc"));
		assertEquals(new Locale("de", "DE", "dgl"), Locales.getLocale("de_DE_dgl"));
	}
}
