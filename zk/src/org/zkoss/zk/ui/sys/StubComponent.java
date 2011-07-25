/* StubComponent.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul 29 08:45:27 TST 2010, Created by tomyeh

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.event.StubEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.au.AuRequest;

/**
 * A stub component is a 'degenerated' component that does not maintain
 * the states at the server. Rather, the states are maintained at the
 * peer widget (running at the client).
 *
 * <p>Application isn't really aware the existence of a stub component.
 * Rather, it is used by ZK Loader to minimize the footprint, if
 * it found a component doesn't have to maintain the states at the server
 * after renderred.
 * For example, {@link org.zkoss.zk.ui.HtmlNativeComponent} and stub-only components
 * will degenerate to {@link StubComponent} or {@link StubsComponent} after renderred.
 * Refer {@link Component#setStubonly} for details about a stub-only component.
 *
 * <p>A component that wants to degenerate to a stub component usually
 * invoke {@link #replace} after {@link #redraw} is called.
 *
 * @author tomyeh
 * @since 5.1,0
 */
public class StubComponent extends AbstractComponent {
	public StubComponent() {
		super(true); //a dummy component definition (replace() will correct it)
	}

	/** Returns ID of the given UUID, or null if not found or no ID assigned.
	 * It assumes the given UUID belonged to one of the component being
	 * merged into this component.
	 * <p>Notice that it searches all descendants of this componet.
	 */
	public String getId(String uuid) {
		if (uuid.equals(getUuid()))
			return id0(getId()); //null if not found or no ID assigned
		return getIdFromChild(this, uuid);
	}
	private static String getIdFromChild(Component comp, String uuid) {
		for (Component child = comp.getFirstChild(); child != null;
		child = child.getNextSibling()) {
			if (child instanceof StubComponent) {
				String id = ((StubComponent)child).getId(uuid); //recurive
				if (id != null)
					return id;
			} else {
				if (uuid.equals(child.getUuid()))
					return id0(child.getId());
				String id = getIdFromChild(child, uuid);
				if (id != null)
					return id;
			}
		}
		return null;
	}
	private static String id0(String id) {
		return id != null && id.length() > 0 ? id: null;
	}

	//super//
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
	 * @param bChildren whether to have the children of the given component.<br/>
	 * If false, this component won't have any children, and all UUID of children
	 * reference back to this component.<br/>
	 * If true, the given component's children will belong to this component.
	 * @exception IllegalStateException if this component has a parent,
	 * sibling or child.
	 */
	public void replace(Component comp, boolean bFellow, boolean bListener,
	boolean bChildren) {
		super.replace(comp, bFellow, bListener, bChildren);
	}
	/** Returns the widget class, "#stub".
	 */
	public String getWidgetClass() {
		return "#stub";
	}
	public void service(AuRequest request, boolean everError) {
		Events.postEvent(StubEvent.getStubEvent(request));
	}
}
