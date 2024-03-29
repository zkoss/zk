package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

/**
 * @author jameschu
 */
public class F85_ZK_3816Test extends ZATSTestCase {
	@Test
	public void test() throws Exception {
		DesktopAgent desktop = connect();
		clickBtns(desktop);
		Assertions.assertEquals("+*-$", desktop.query("#win #result").as(Label.class).getValue());
		Assertions.assertEquals("+*-$ 123", desktop.query("#win #str").as(Label.class).getValue());
		desktop.queryAll("button").get(4).click();
		Assertions.assertTrue(desktop.query("#win #msg").as(Label.class).getValue().contains("done"));
		clickBtns(desktop);
		Assertions.assertEquals("+*-$+*-$", desktop.query("#win #result").as(Label.class).getValue());
		Assertions.assertEquals("+*-$+*-$ 123", desktop.query("#win #str").as(Label.class).getValue());
	}
	private void clickBtns(DesktopAgent desktop) {
		List<ComponentAgent> btns = desktop.queryAll("button");
		for (int i = 0; i < 4; i++) {
			btns.get(i).click();
		}
	}
}
