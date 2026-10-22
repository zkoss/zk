/* UiEngineExtension.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug  2 14:07:12 TST 2010, Created by tomyeh

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.ext.render.Cropper;
import org.zkoss.zk.ui.ext.render.StubonlyControl;
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
	private static final Pattern NAME_SEPARATOR = Pattern.compile("[\\s,]+");
	private static final String STUBONLY_NAMES_BOUNDARY = "org.zkoss.zk.ui.stubonlyNamesBoundary";

	public UiEngineExtension() {
	}

	public void afterCreate(Component[] comps) {
	}

	public void afterRenderComponents(Collection comps) {
		final Map<Component, boolean[]> stubinfs = new HashMap<Component, boolean[]>();
		final Map<Component, Set<String>> stubnames = new HashMap<Component, Set<String>>();
		for (Iterator it = comps.iterator(); it.hasNext();) {
			final Component c = (Component) it.next();
			boolean[] stubinf = getStubInfo(c.getParent(), stubinfs);
			stub(c, stubinf[0], stubinf[1], getStubonlyNames(c.getParent(), stubnames));
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

	/** Returns the stubonlyDescendants names declared by the given component and its ancestors,
	 * up to the nearest one that specifies stubonly, or null if any of them disables names. */
	private static final Set<String> getStubonlyNames(Component comp, Map<Component, Set<String>> stubnames) {
		if (stubnames.containsKey(comp))
			return stubnames.get(comp);
		Set<String> names = Collections.emptySet();
		for (Component c = comp; c != null; c = c.getParent()) {
			names = addStubonlyNames(names, c);
			if (shallStubonly(c) != null || c.hasAttribute(STUBONLY_NAMES_BOUNDARY))
				break; //explicit stubonly shields the subtree from ancestors' names
		}
		if (!names.isEmpty() && isNamesDisabled(comp))
			names = null;
		stubnames.put(comp, names);
		return names;
	}

	private static final Set<String> addStubonlyNames(Set<String> names, Component comp) {
		final String s = comp.getStubonlyDescendants();
		if (s == null || names == null)
			return names;
		names = new HashSet<String>(names);
		for (String name : NAME_SEPARATOR.split(s.trim()))
			if (!name.isEmpty())
				names.add(name);
		return names;
	}

	/** Returns whether the given component or any of its ancestors disables names. */
	private static final boolean isNamesDisabled(Component comp) {
		for (Component c = comp; c != null; c = c.getParent())
			if (disablesNames(c))
				return true;
		return false;
	}

	/** Returns whether no names apply to the given component and its descendants:
	 * cropped children are rendered later, and shadow elements
	 * (such as forEach) track the original components of their host. */
	private static final boolean disablesNames(Component comp) {
		if ("paging".equals(comp.getMold()) || !((ComponentCtrl) comp).getShadowRoots().isEmpty())
			return true;
		final Object extraCtrl = ((ComponentCtrl) comp).getExtraCtrl();
		return (extraCtrl instanceof Cropper && ((Cropper) extraCtrl).isCropper())
				|| (extraCtrl instanceof StubonlyControl
						&& !((StubonlyControl) extraCtrl).isStubonlyDescendantsAllowed());
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
	private static final boolean stub(Component comp, boolean stubnative, boolean stubonly, Set<String> names) {
		if (comp instanceof StubsComponent)
			return true; //nothing to do if stubs (i.e., merged stub components)
		//returns true so the parent will keep merging (aggressive algorithm)

		Boolean val = shallStubNative(comp);
		if (val != null)
			stubnative = val.booleanValue();
		val = shallStubonly(comp);
		if (val != null)
			stubonly = val.booleanValue();
		final boolean namesBoundary = val != null || comp.hasAttribute(STUBONLY_NAMES_BOUNDARY);
		//ZK-6096: a name match stubs this component only; unlike stubonly, it is not inherited
		if (names != null) {
			//check lazily since getExtraCtrl() creates an object on every call
			if (names.isEmpty() ? comp.getStubonlyDescendants() != null && isNamesDisabled(comp) : disablesNames(comp))
				names = null;
		}
		// ZK-6096: preserve ID spaces for live descendants and original components for binders.
		final boolean matched = names != null && val == null && !(comp instanceof StubComponent)
				&& !(comp instanceof IdSpace)
				&& (names.contains("*") || names.contains(comp.getDefinition().getName()))
				&& !((ComponentCtrl) comp).hasBindingAnnotation() && !comp.hasAttribute("$BINDER$");
		if (namesBoundary && names != null)
			names = Collections.emptySet(); //explicit stubonly shields the subtree from ancestors' names
		names = addStubonlyNames(names, comp);

		boolean allstub = true, anychild = false;
		for (Component child = comp.getFirstChild(); child != null; anychild = true) {
			Component n = child.getNextSibling();
			if (!stub(child, stubnative, stubonly, names))
				allstub = false;
			child = n;
		}

		final boolean bNative = comp instanceof Native;
		if ((bNative && !stubnative) || (!bNative && !stubonly && !matched))
			return false;

		if (allstub && anychild) {
			//merge as a single StubComponents
			new StubsComponent().replace(comp, false, true/*preserve listener*/, false/*ignore children*/);
			return true;
		} else {
			final StubComponent stub = new StubComponent();
			stub.replace(comp, false, true/*preserve listener*/, true/*preserve children*/);
			// ZK-6096: retain the name-policy boundary; under an explicit "true", live children sit below an explicit "false".
			if (anychild && (val != null ? !stubonly : namesBoundary))
				stub.setAttribute(STUBONLY_NAMES_BOUNDARY, Boolean.TRUE);
			return !anychild;
		}
	}
}
