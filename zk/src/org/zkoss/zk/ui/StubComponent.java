/* StubComponent.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 29 08:45:27 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui;

/**
 * A stub component is a 'degenerated' component that does not maintain
 * the states at the server. Rather, the states are maintained at the
 * peer widget (running at the client).
 *
 * <p>Application isn't really aware the existence of a stub component.
 * Rather, it is used by ZK Loader to minimize the footprint, if
 * it found a component doesn't have to maintain the states at the server
 * after renderred.
 * For example, {@link HtmlNativeComponent} and stub-only components
 * will degenerate to {@link StubComponent} after renderred.
 * Refer {@link Component#setStubonly} for details about a stub-only component.
 *
 * <p>A component that wants to degenerate to a stub component usually
 * invoke {@link #replace} after {@link #redraw} is called.
 *
 * @author tomyeh
 * @since 5.0.4
 */
public class StubComponent extends AbstractComponent {
	public StubComponent() {
		super(true);
	}

	/** Replace the specified component with this component in
	 * the component tree. In other words, the parent of the given
	 * component will become the parent of this components, so
	 * are siblings and children. Furthermore, comp will be detached
	 * at the end.
	 *
	 * <p>Notice that the replacement won't change anything at the client.
	 * A stub component assumes there is a full-functional widget
	 * running at the client to handle everything.
	 *
	 * @param comp the component. In this implementation it supports
	 * only derived classes of {@link AbstractComponent}.
	 * @param bFellow whether to add this component to the map of fellows
	 * if it is assigned with an ID. If false, the component (comp) cannot
	 * be retrieved back even with an ID.
	 * @param bListener whether to retain the event listeners and handlers.
	 * If true, the event listeners and handlers, if any, will be registered
	 * to this stub component. In other words, the event will be processed.
	 * However, it is a stub component, rather than the original one.
	 * I means the event is the most generic format: an instance of
	 * {@link org.zkoss.zk.ui.event.Event} (rather than MouseEvent or others).
	 * @exception IllegalArgumentException if comp is already
	 * {@link StubComponent}.
	 * @exception IllegalStateException if this component has a parent,
	 * sibling or child.
	 */
	public void replace(Component comp, boolean bFellow, boolean bListener) {
		if (comp instanceof StubComponent)
			throw new IllegalArgumentException();
		((AbstractComponent)comp).replaceWith(this, bFellow, bListener);
	}

	/** Returns the widget class, "#stub".
	 */
	public String getWidgetClass() {
		return "#stub";
	}
}
