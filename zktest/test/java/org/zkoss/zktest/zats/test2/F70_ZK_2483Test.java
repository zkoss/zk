/* F70_ZK_2483Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 17 16:59:25 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.hamcrest.Matchers.matchesRegex;

import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author rudyhuang
 */
public class F70_ZK_2483Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		List<ComponentAgent> lbls = desktop.queryAll("#init > label");
		Iterator<ComponentAgent> iter = lbls.iterator();
		while (iter.hasNext()) {
			Label key = iter.next().as(Label.class);
			Label value = iter.next().as(Label.class);
			Assert.assertThat(key.getValue(), matchesRegex("key \\d+"));
			Assert.assertThat(value.getValue(), matchesRegex("value \\d+"));
		}
	}
}
