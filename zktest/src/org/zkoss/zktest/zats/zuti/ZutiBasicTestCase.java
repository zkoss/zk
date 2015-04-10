/** ZutiBasicTestCase.java.

	Purpose:
		
	Description:
		
	History:
		9:42:33 AM Nov 6, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlShadowElement;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zktest.zats.zuti.verifier.Verifier;

/**
 * A basic test case class
 * @author jumperchen
 */
public class ZutiBasicTestCase extends ZATSTestCase {
	private int getChildSize(HtmlShadowElement parent) {
		List<HtmlShadowElement> children = parent.getChildren();
		int total = children.size();
		for (HtmlShadowElement child : children)
			total += getChildSize(child);
		return total;
	}
	protected int getShadowSize(ComponentAgent host) {
		List<HtmlShadowElement> shadowRoots = ((ComponentCtrl)host.getOwner()).getShadowRoots();

		int total = shadowRoots.size();
		for (HtmlShadowElement root : shadowRoots) {
			total += getChildSize(root);
		}
		return total;
	}
	protected int getAllShadowSize(ComponentAgent host) {
		List<HtmlShadowElement> shadowRoots = ((ComponentCtrl)host.getOwner()).getShadowRoots();
		List<ComponentAgent> children = host.getChildren();

		int total = shadowRoots.size();
		for (HtmlShadowElement root : shadowRoots) {
			total += getChildSize(root);
		}
		
		for (ComponentAgent child : children) {
			total += getAllShadowSize(child);
		}
		return total;
	}
	@AfterClass
	public static void end() {
		env.destroy();
	}

	@After
	public void after() {
		env.cleanup();
	}
	
	protected void checkVerifier(Component target, Class<? extends Verifier>... verifiers) {
		if (target == null) return; // do nothing
		AbstractComponent host = (AbstractComponent) target;
		Verifiers.getInstance(host, verifiers).verify();
	}

	@Override
	protected String getFileLocation() {
		String simple = this.getClass().getSimpleName();
		String name = this.getClass().getName().replace("org.zkoss.zktest.zats", "").replace(".","/").replace(simple, "");
		String file = String.valueOf(simple.charAt(0)).toLowerCase() + simple.substring(1).replace("Test", ".zul");
		return name + file;
	}
}