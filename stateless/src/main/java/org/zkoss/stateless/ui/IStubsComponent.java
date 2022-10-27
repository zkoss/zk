/* IStubsComponent.java

	Purpose:
		
	Description:
		
	History:
		4:33 PM 2021/10/13, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.zkoss.stateless.ui.util.VolatileComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.StubEvent;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.sys.EventListenerMap;
import org.zkoss.zk.ui.sys.StubComponent;
import org.zkoss.zk.ui.sys.StubsComponent;

/**
 * Internal use only.
 * @author jumperchen
 */
public class IStubsComponent extends StubsComponent {
	public IStubsComponent() {
	}

	public void replace(Component comp, boolean bFellow, boolean bListener,
			boolean bChildren) {
		super.replace(comp, bFellow, bListener, bChildren);

		// doing merge
		doMerge(comp);
	}

	/**
	 * Merges the given component into this stubs component.
	 * @param stubComponent
	 */
	public void mergeIComponent(StubComponent stubComponent) {
		doMerge(stubComponent);
	}

	private void doMerge(Component comp) {
		String[] uuids = this._uuids;
		Object[][] evtmap = this._evtmap;
		String[][] idmap = this._idmap;

		// reset for onChildrenMerged
		_uuids = null;
		_evtmap = null;
		_idmap = null;

		onChildrenMerged(new VolatileComponent() {
			public Desktop getDesktop() {
				return IStubsComponent.this.getDesktop();
			}

			public Page getPage() {
				return IStubsComponent.this.getPage();
			}

			public String getUuid() {
				return null;
			}

			public Component getFirstChild() {
				return comp;
			}
		}, true);
		_uuids = concatenate(uuids, _uuids);
		_evtmap = concatenate(evtmap, _evtmap);
		_idmap = concatenate(idmap, _idmap);
	}

	public void service(Event event, Scope scope) throws Exception {
		final StubEvent stubevt = event instanceof StubEvent ? (StubEvent) event : null;
		final String uuid = stubevt != null ? stubevt.getUuid() : null;

		// detach for stub component
		if (stubevt != null && "$rms$".equals(stubevt.getCommand())) {
			List<String> uuids = (List<String>) stubevt.getRequestData().get("");
			removeStub(uuids);
		} else if (_evtmap != null && uuid != null && uuid.equals(getUuid())) {
			// don't use super.service, it will use AbstractComponent#service() implementation,
			// which doesn't contain Stateless's @Action
			for (Object[] evtinf : _evtmap) {
				if (uuid.equals(evtinf[0])) { //matched
					((EventListenerMap) evtinf[1]).service(event, scope, this,
							stubevt.getCommand());
					break; //done
				}
			}
		} else {
			super.service(event, scope);
		}
	}

	public void removeStub(List<String> uuids) {
		for (String uuid: uuids) {

			// uid may be id not uuid
			if (uuid.startsWith("$")) {
				final String id = uuid.substring(1);
				String[] idmap = Stream.of(_idmap)
						.filter(uuid0 -> uuid0[1].equals(id)).findFirst().get();
				uuid = idmap[0];
			}
			final String uid = uuid;
			if (_uuids != null) {
				_uuids = Stream.of(_uuids).filter(uuid0 -> !(uuid0.equals(uid))).toArray(String[]::new);
			}
			if (_idmap != null) {
				_idmap = Stream.of(_idmap).filter(uuid0 -> !(uuid0[0].equals(uid))).toArray(String[][]::new);
			}
			if (_evtmap != null) {
				_evtmap = Stream.of(_evtmap).filter(uuid0 -> !(uuid0[0].equals(uid))).toArray(Object[][]::new);
			}
		}
		if (_uuids.length == 0) {
			// detach oneself.
			this.detach();
		}
	}

	private static <T> T[] concatenate(T[] a, T[] b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		int aLen = a.length;
		int bLen = b.length;

		@SuppressWarnings("unchecked")
		T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	// ZK StubsComponent don't support to find by ID, but we support it in Stateless
	public Component getFellowIfAny(String compId) {
		Component result = super.getFellowIfAny(compId);
		if (result != null) {
			return result;
		} else if (_idmap != null) {
			for (String[] ids : _idmap) {
				if (Objects.equals(ids[1], compId)) {
					return this;
				}
			}
		}
		return null;
	}

	// ZK StubsComponent don't support to find by ID, but we support it in Stateless
	public Component getFellow(String compId)
			throws ComponentNotFoundException {
		if (_idmap != null) {
			for (String[] ids : _idmap) {
				if (Objects.equals(ids[1], compId)) {
					return this;
				}
			}
		}
		return super.getFellow(compId);
	}

}
