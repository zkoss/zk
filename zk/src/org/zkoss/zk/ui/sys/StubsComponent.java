/* StubsComponent.java

	Purpose:
		
	Description:
		
	History:
		Sat Jun  4 21:24:11 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.sys;

import java.util.List;
import java.util.LinkedList;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.StubEvent;
import org.zkoss.zk.ui.sys.EventListenerMap;

/**
 * Represents a tree of {@link StubComponent} that are merged into
 * a single component.
 *
 * @author tomyeh
 * @since 6.0.0
 */
public class StubsComponent extends StubComponent {
	private String[] _uuids;
	/** [0]: uuid, [1]: id */
	private String[][] _idmap;
	/** [0]: uuid, [1]: EventListenerMap. */
	private Object[][] _evtmap;

	/** Called when this component replaced the given component,
	 * and the children of the given component shall be 'merged' to this component.
	 * @param replaced the component that this component will replace.
	 * @exception IllegalStateException if this method has been called twice
	 * (we can modify the algorithm to support but not worth).
	 */
	public void onChildrenMerged(Component replaced, boolean bListener) {
		if (_uuids != null)
			throw new IllegalStateException("called twice");

		final List<String> uuids = new LinkedList<String>();
		final List<String[]> idmap = new LinkedList<String[]>();
		final List<Object[]> evtmap = bListener ? new LinkedList<Object[]>(): null;
		final Page page = getPage();

		mapChildren(page != null ? (DesktopCtrl)page.getDesktop(): null,
			uuids, idmap, evtmap, replaced);

		_uuids = (String[])uuids.toArray(new String[uuids.size()]);
		_idmap = !idmap.isEmpty() ?
			(String[][])idmap.toArray(new String[idmap.size()][]): null;
		_evtmap = evtmap != null && !evtmap.isEmpty() ?
			(Object[][])evtmap.toArray(new Object[evtmap.size()][]): null;
	}
	private void mapChildren(DesktopCtrl desktopCtrl, List<String> uuids,
	List<String[]> idmap, List<Object[]> evtmap, Component comp) {
		for (Component p = comp.getFirstChild(); p != null; p = p.getNextSibling()) {
			if (p instanceof StubsComponent) {
				final String[] kiduuids = ((StubsComponent)p)._uuids;
				if (kiduuids != null)
					for (String uuid: kiduuids) {
						uuids.add(uuid);
						if (desktopCtrl != null)
							desktopCtrl.mapComponent(uuid, this);
					}

				final String[][] kidids = ((StubsComponent)p)._idmap;
				if (kidids != null)
					for (String[] idinf: kidids)
						idmap.add(idinf);

				if (evtmap != null) {
					final Object[][] kidevts = ((StubsComponent)p)._evtmap;
					if (kidevts != null)
						for (Object[] evtinf: kidevts)
							evtmap.add(evtinf);
				}
			}

			final String uuid = p.getUuid();
			uuids.add(uuid);
			if (desktopCtrl != null)
				desktopCtrl.mapComponent(uuid, this);

			final String id = p.getId();
			if (id != null && id.length() > 0)
				idmap.add(new String[] {uuid, id});
			if (evtmap != null) {
				EventListenerMap em = ((ComponentCtrl)p).getEventListenerMap();
				if (em != null)
					evtmap.add(new Object[] {uuid, em});
			}

			mapChildren(desktopCtrl, uuids, idmap, evtmap, p); //recusrive
		}
	}

	//--super--//
	@Override
	public String getId(String uuid) {
		if (_idmap != null)
			for (String[] idinf: _idmap)
				if (uuid.equals(idinf[0]))
					return idinf[1];
		return super.getId(uuid);
	}
	@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);

		if (newpage != null) {
			final DesktopCtrl desktopCtrl = (DesktopCtrl)newpage.getDesktop();
			for (String uuid: _uuids)
				desktopCtrl.mapComponent(uuid, this);
		}
	}
	@Override
	public void onPageDetached(Page page) {
		super.onPageDetached(page);

		final DesktopCtrl desktopCtrl = (DesktopCtrl)page.getDesktop();
		for (String uuid: _uuids)
			desktopCtrl.mapComponent(uuid, null);
	}
	@Override
	public void service(Event event, Scope scope) throws Exception {
		final StubEvent stubevt =
			event instanceof StubEvent ? (StubEvent)event: null;
		final String uuid = stubevt != null ? stubevt.getUuid(): null;
		if (uuid == null || uuid.equals(getUuid())) {
			super.service(event, scope);
		} else if (_evtmap != null) {
			for (Object[] evtinf: _evtmap) {
				if (uuid.equals(evtinf[0])) {//matched
					((EventListenerMap)evtinf[1]).service(
						event, scope, this, stubevt.getCommand());
					break; //done
				}
			}
			postToNonStubAncestor(stubevt);
		}
	}

	/** Returns the widget class, "#stubs".
	 */
	public String getWidgetClass() {
		return "#stubs";
	}
	/** {@link StubsComponent} represents a collection of {@link StubComponent},
	 * so it does not allow any child.
	 */
	protected boolean isChildable() {
		return false;
	}
	public String toString() {
		final StringBuffer sb = new StringBuffer(super.toString());
		if (_uuids != null) {
			sb.append('(');
			for (int j = 0; j < _uuids.length; ++j) {
				if (j != 0) sb.append(", ");
				sb.append(_uuids[j]);
			}
			sb.append(')');
		}
		if (_idmap != null) {
			sb.append('(');
			for (int j = 0; j < _idmap.length; ++j) {
				if (j != 0) sb.append(", ");
				sb.append(_idmap[j][0]).append('=').append(_idmap[j][1]);
			}
			sb.append(')');
		}
		return sb.toString();
	}
}
