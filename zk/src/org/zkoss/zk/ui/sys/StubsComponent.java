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

/**
 * Represents a tree of {@link StubComponent} that are merged into
 * a single component.
 *
 * @author tomyeh
 * @since 5.5.0
 */
public class StubsComponent extends StubComponent {
	private String[] _uuids;
	/** [0]: uuid, [1]: id */
	private String[][] _idmap;

	/** Called when this component replaced the given component,
	 * and the children of the given component shall be 'merged' to this component.
	 * @param replaced the component that this component will replace.
	 * @exception IllegalStateException if this method has been called twice
	 * (we can modify the algorithm to support but not worth).
	 */
	public void onChildrenMerged(Component replaced) {
		if (_uuids != null)
			throw new IllegalStateException("called twice");

		final List<String> uuids = new LinkedList<String>();
		final List<String[]> idmap = new LinkedList<String[]>();
		final Page page = getPage();
		mapChildren(page != null ? (DesktopCtrl)page.getDesktop(): null,
			uuids, idmap, replaced);
		_uuids = (String[])uuids.toArray(new String[uuids.size()]);
		_idmap = (String[][])idmap.toArray(new String[idmap.size()][]);
	}
	private void mapChildren(DesktopCtrl desktopCtrl, List<String> uuids,
	List<String[]> idmap, Component comp) {
		for (Component p = comp.getFirstChild(); p != null; p = p.getNextSibling()) {
			if (p instanceof StubsComponent) {
				final String[] kiduuids = ((StubsComponent)p)._uuids;
				if (kiduuids != null)
					for (int j = 0; j < kiduuids.length; ++j) {
						uuids.add(kiduuids[j]);
						if (desktopCtrl != null)
							desktopCtrl.mapComponent(kiduuids[j], this);
					}
				final String[][] kidids = ((StubsComponent)p)._idmap;
				if (kidids != null)
					for (int j = 0; j < kidids.length; ++j)
						idmap.add(kidids[j]);
			}

			final String uuid = p.getUuid();
			uuids.add(uuid);
			if (desktopCtrl != null)
				desktopCtrl.mapComponent(uuid, this);

			final String id = p.getId();
			if (id != null && id.length() > 0)
				idmap.add(new String[] {uuid, id});

			mapChildren(desktopCtrl, uuids, idmap, p); //recusrive
		}
	}

	//--super--//
	//@Override
	public String getId(String uuid) {
		if (_idmap != null)
			for (int j = 0; j < _idmap.length; ++j)
				if (uuid.equals(_idmap[j][0]))
					return _idmap[j][1];
		return super.getId(uuid);
	}
	//@Override
	public void onPageAttached(Page newpage, Page oldpage) {
		super.onPageAttached(newpage, oldpage);

		if (newpage != null) {
			final DesktopCtrl desktopCtrl = (DesktopCtrl)newpage.getDesktop();
			for (int j = 0; j < _uuids.length; ++j)
				desktopCtrl.mapComponent(_uuids[j], this);
		}
	}
	//@Override
	public void onPageDetached(Page page) {
		super.onPageDetached(page);

		final DesktopCtrl desktopCtrl = (DesktopCtrl)page.getDesktop();
		for (int j = 0; j < _uuids.length; ++j)
			desktopCtrl.mapComponent(_uuids[j], null);
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
