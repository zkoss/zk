/* IStubComponent.java

	Purpose:
		
	Description:
		
	History:
		10:40 AM 2021/9/28, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui;

import java.io.IOException;
import java.io.StringWriter;

import org.zkoss.stateless.util.ActionHandler;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zk.ui.sys.StubComponent;

/**
 * A transient component for rendering {@link IComponent} to client.
 * @author jumperchen
 */
public abstract class IStubComponent extends StubComponent {
	public static IStubComponent of(IComponent root) {
		return new IStubComponentProxy(root);
	}

	protected void renderProperties(ContentRenderer renderer)
			throws IOException {
		super.renderProperties(renderer);
	}

	/**
	 * Returns the immutable component.
	 */
	public abstract IComponent getOwner();

	public String getStubonly() {
		return "true";
	}

	public static IStubComponent of(Component root) {
		IStubComponent stub = new IStubComponent() {
			public void addAction(String name, ActionHandler handler) {
				throw new UnsupportedOperationException("Readonly");
			}

			public IComponent getOwner() {
				throw new IllegalStateException();
			}
		};
		stub.replace(root, true, true, false);
		return stub;
	}

	public static String redraw(IComponent root) throws IOException {
		StringWriter stringWriter = new StringWriter(1024*8);
		IStubComponent iStubComponent = of(root);

		iStubComponent.redraw(stringWriter);

		return stringWriter.toString();
	}

	public static String redraw(IStubComponent root) throws IOException {
		StringWriter stringWriter = new StringWriter(1024*8);
		root.redraw(stringWriter);
		return stringWriter.toString();
	}

	public abstract void addAction(String name, ActionHandler handler);
}