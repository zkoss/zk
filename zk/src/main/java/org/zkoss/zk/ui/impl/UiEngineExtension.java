/* UiEngineExtension.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug  2 14:07:12 TST 2010, Created by tomyeh

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.impl.UiEngineImpl.Extension;
import org.zkoss.zk.ui.sys.Attributes;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.StubComponent;
import org.zkoss.zk.ui.sys.StubsComponent;

/**
 * An implementation of {@link Extension} to 
 * extend {@link UiEngineImpl} to support stubonly and the merging of native. *
 * @author tomyeh
 * @since 5.0.8
 */
public class UiEngineExtension implements Extension {
	public UiEngineExtension() {
	}

	public void afterCreate(Component[] comps) {
	}

	public void afterRenderComponents(Collection comps) {
		final Map<Component, boolean[]> stubinfs = new HashMap<Component, boolean[]>();
		for (Iterator it = comps.iterator(); it.hasNext();) {
			final Component c = (Component) it.next();
			boolean[] stubinf = getStubInfo(c.getParent(), stubinfs);
			stub(c, stubinf[0], stubinf[1]);
		}
	}

	public void afterRenderNewPage(Page page) {
		afterRenderComponents(page.getRoots());
	}

	//utilities
	private static final boolean[] getStubInfo(Component comp, Map<Component, boolean[]> stubinfs) {
		boolean[] stubinf = stubinfs.get(comp);
		if (stubinf == null) {
			Boolean stubnative = null, stubonly = null;
			for (Component c = comp; c != null;) {
				if (stubnative == null)
					stubnative = shallStubNative(c);
				if (stubonly == null)
					stubonly = shallStubonly(c);
				if (stubnative != null && stubonly != null)
					break; //done
				c = c.getParent();
			}

			stubinfs.put(comp, stubinf = new boolean[] { stubnative == null || stubnative.booleanValue(), //null => true
					stubonly != null && stubonly.booleanValue() }); //null => false
		}
		return stubinf;
	}

	private static final Boolean shallStubNative(Component comp) {
		final Object sn = comp.getAttribute(Attributes.STUB_NATIVE);
		if (sn != null)
			return Boolean.valueOf(Boolean.TRUE.equals(sn) || "true".equals(sn));
		else if ("paging".equals(comp.getMold()))
			return Boolean.FALSE; //disable if in paging (Bug 3141977)
		else if (comp instanceof ComponentCtrl) {
			ComponentCtrl compCtrl = (ComponentCtrl) comp;
			if (!compCtrl.getShadowRoots().isEmpty())
				return Boolean.FALSE;
		}
		return null;
	}

	private static final Boolean shallStubonly(Component comp) {
		final String so = comp.getStubonly();
		return "inherit".equals(so) ? null : Boolean.valueOf("true".equals(so));
	}

	/** Makes a component as a stub-only component.
	 * @return whether if components in the whole sub-tree are all StubComponent
	 */
	private static final boolean stub(Component comp, boolean stubnative, boolean stubonly) {
		if (comp instanceof StubsComponent)
			return true; //nothing to do if stubs (i.e., merged stub components)
		//returns true so the parent will keep merging (aggressive algorithm)

		Boolean val = shallStubNative(comp);
		if (val != null)
			stubnative = val.booleanValue();
		val = shallStubonly(comp);
		if (val != null)
			stubonly = val.booleanValue();

		boolean allstub = true, anychild = false;
		for (Component child = comp.getFirstChild(); child != null; anychild = true) {
			Component n = child.getNextSibling();
			if (!stub(child, stubnative, stubonly))
				allstub = false;
			child = n;
		}

		final boolean bNative = comp instanceof Native;
		if ((bNative && !stubnative) || (!bNative && !stubonly))
			return false;

		if (allstub && anychild) {
			//merge as a single StubComponents
			new StubsComponent().replace(comp, false, true/*preserve listener*/, false/*ignore children*/);
			return true;
		} else {
			new StubComponent().replace(comp, false, true/*preserve listener*/, true/*preserve children*/);
			return !anychild;
		}
	}
}
