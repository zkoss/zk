package org.zkoss.zktest.zats.bind.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;

public class B01165NestedBinderTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		
		ComponentAgent outerPidLb = desktop.query("#outerPidLb");
		ComponentAgent outerPDescLb = desktop.query("#outerPDescLb");
		ComponentAgent pidLb = desktop.query("#pidLb");
		ComponentAgent pDescLb = desktop.query("#pDescLb");
		ComponentAgent vmsSelIdLb = desktop.query("#vmsSelIdLb");
		ComponentAgent vmsSelDescLb = desktop.query("#vmsSelDescLb");
		
		assertEquals("b3", outerPidLb.as(Label.class).getValue());
		assertEquals("this is b3", outerPDescLb.as(Label.class).getValue());
		assertEquals("b3", pidLb.as(Label.class).getValue());
		assertEquals("this is b3", pDescLb.as(Label.class).getValue());
		assertEquals("b3", vmsSelIdLb.as(Label.class).getValue());
		assertEquals("this is b3", vmsSelDescLb.as(Label.class).getValue());
	}
}
