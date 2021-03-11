/* F96_ZK_4761Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Feb 18 15:50:20 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.ClassRule;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.zkoss.bind.BindComposer;
import org.zkoss.bind.impl.BinderImpl;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class F96_ZK_4761Test extends ZATSTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml("/test2/F96-ZK-4761-zk.xml");

	@Test
	public void test() {
		DesktopAgent desktop = connect();
		desktop.query("button").click();
		assertEquals("0", desktop.query("#resultNum").as(Label.class).getValue());
	}
}
