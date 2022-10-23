/* ZephyrContentRenderer.java

	Purpose:
		
	Description:
		
	History:
		12:06 PM 2021/10/7, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.ui.util;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.zkoss.zephyr.ui.IStubComponent;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zk.ui.sys.ContentRenderer;

/**
 * A renderer context to wrap ZK ContentRenderer with Zephyr runtime meta.
 * @author jumperchen
 */
public class ZephyrContentRenderer<I extends IComponent> implements ContentRenderer {
	private ContentRenderer delegator;
	private I owner;
	private IStubComponent zkComp;

	public ZephyrContentRenderer(ContentRenderer delegator, I owner, IStubComponent zkComp) {
		this.delegator = delegator;
		this.owner = owner;
		this.zkComp = zkComp;
	}

	public I getOwner() {
		return owner;
	}

	public IStubComponent getBinding() {
		return zkComp;
	}

	public void render(String s, String s1) throws IOException {
		delegator.render(s, s1);
	}

	public void render(String s, Date date) throws IOException {
		delegator.render(s, date);
	}

	public void render(String s, Object o) throws IOException {
		delegator.render(s, o);
	}

	public void render(String s, int i) throws IOException {
		delegator.render(s, i);
	}

	public void render(String s, short i) throws IOException {
		delegator.render(s, i);
	}

	public void render(String s, long l) throws IOException {
		delegator.render(s, l);
	}

	public void render(String s, byte b) throws IOException {
		delegator.render(s, b);
	}

	public void render(String s, boolean b) throws IOException {
		delegator.render(s, b);
	}

	public void render(String s, double v) throws IOException {
		delegator.render(s, v);
	}

	public void render(String s, float v) throws IOException {
		delegator.render(s, v);
	}

	public void render(String s, char c) throws IOException {
		delegator.render(s, c);
	}

	public void renderDirectly(String s, Object o) {
		delegator.renderDirectly(s, o);
	}

	public void renderWidgetListeners(Map<String, String> map) {
		delegator.renderWidgetListeners(map);
	}

	public void renderWidgetOverrides(Map<String, String> map) {
		delegator.renderWidgetOverrides(map);
	}

	public void renderWidgetAttributes(Map<String, String> map) {
		delegator.renderWidgetAttributes(map);
	}

	public void renderClientAttributes(Map<String, String> map) {
		delegator.renderClientAttributes(map);
	}
}
