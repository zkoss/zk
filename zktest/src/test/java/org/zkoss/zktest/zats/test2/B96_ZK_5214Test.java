/* B96_ZK_5214Test.java

	Purpose:
		
	Description:
		
	History:
		5:34 PM 2022/9/29, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import org.zkoss.util.Locales;

/**
 * @author jumperchen
 */
public class B96_ZK_5214Test {
	@Test
	public void test() {
		assertEquals(Locale.forLanguageTag("zh-TW-u-ca-roc"), Locales.getLocale("zh-TW-u-ca-roc"));
		assertEquals(new Locale("de", "DE", "dgl"), Locales.getLocale("de_DE_dgl"));
	}
}
