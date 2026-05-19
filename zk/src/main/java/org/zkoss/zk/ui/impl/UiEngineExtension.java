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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.impl.UiEngineImpl.Extension;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
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
	private static final Logger log = LoggerFactory.getLogger(UiEngineExtension.class);
	private static final String WILDCARD_TOKEN = "*";
	private static final Pattern POLICY_SPLITTER = Pattern.compile("[\\s,]+");

	public UiEngineExtension() {
	}

	public void afterCreate(Component[] comps) {
	}

	public void afterRenderComponents(Collection comps) {
		final Map<Component, boolean[]> stubinfs = new HashMap<Component, boolean[]>();
		final Map<Component, Set<String>> policyInfs = new HashMap<Component, Set<String>>();
		// Tokens already inspected by the typo-warner this batch. We warn at
		// most once per (token, render) pair so a noisy page with the same
		// typo in multiple places produces a single message.
		final Set<String> warnedTokens = new HashSet<String>();
		for (Iterator it = comps.iterator(); it.hasNext();) {
			final Component c = (Component) it.next();
			final Component parent = c.getParent();
			boolean[] stubinf = getStubInfo(parent, stubinfs);
			Set<String> policy = getAncestorPolicy(parent, policyInfs, warnedTokens);
			stub(c, stubinf[0], stubinf[1], policy, warnedTokens);
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

	/** Collect the union of {@code stubonlyDescendants} policies declared on
	 * {@code comp} and every ancestor above it, so that descendants rendered
	 * out-of-band (e.g. invalidate-after-render) inherit the same policy as a
	 * regular top-down render would have given them.
	 *
	 * <p>Shares the merge logic with {@link #accumulateAncestorPolicy}. The
	 * top-down recursion in {@link #stub} extends an inherited set instead of
	 * re-walking; both paths must agree on the same set for any given comp,
	 * because a comp may be reached by either path within the same render.
	 */
	private static final Set<String> getAncestorPolicy(Component comp,
			Map<Component, Set<String>> policyInfs, Set<String> warnedTokens) {
		if (comp == null)
			return Collections.emptySet();
		Set<String> cached = policyInfs.get(comp);
		if (cached != null)
			return cached;
		Set<String> result = accumulateAncestorPolicy(comp, warnedTokens);
		policyInfs.put(comp, result);
		return result;
	}

	/** Walk from {@code start} up to the root, merging every declared
	 * {@code stubonlyDescendants} value into a single set. Returns the empty
	 * set when no ancestor declares a policy. Pass a non-null
	 * {@code warnedTokens} to enable typo warnings.
	 */
	private static final Set<String> accumulateAncestorPolicy(Component start, Set<String> warnedTokens) {
		Set<String> acc = null;
		for (Component c = start; c != null; c = c.getParent()) {
			final String declared = c.getStubonlyDescendants();
			if (declared != null && !declared.isEmpty()) {
				if (acc == null)
					acc = new HashSet<String>();
				mergePolicy(acc, declared, c, warnedTokens);
			}
		}
		return acc == null ? Collections.<String>emptySet() : acc;
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

	/** Parses a comma/whitespace-separated policy string into {@code dest}.
	 * No-op for null/empty input. When {@code warnedTokens} is non-null,
	 * each freshly-seen token is checked against the loaded language
	 * definitions; unknown tokens trigger a one-time {@code log.warn} so
	 * typos (e.g. {@code "lable"} instead of {@code "label"}) surface
	 * instead of silently producing no stub.
	 *
	 * @param anchor the component whose policy declaration is being parsed,
	 * used only for the warning message; may be null when validation is off.
	 */
	private static final void mergePolicy(Set<String> dest, String policy,
			Component anchor, Set<String> warnedTokens) {
		if (policy == null)
			return;
		for (String token : POLICY_SPLITTER.split(policy)) {
			if (token.isEmpty())
				continue;
			dest.add(token);
			if (warnedTokens != null && log.isWarnEnabled() && warnedTokens.add(token)
					&& !WILDCARD_TOKEN.equals(token) && !isKnownComponentName(token, anchor))
				log.warn("stubonlyDescendants token \"{}\" (declared on <{}>) matches no known component definition; possible typo?",
						token, anchor != null && anchor.getDefinition() != null ? anchor.getDefinition().getName() : "?");
		}
	}

	/** Whether any loaded definition knows a component by this name. Checks
	 * the anchor's page first (which catches in-zul aliases registered via
	 * {@code <?component name="myDiv" extends="div"?>}), then falls back to
	 * every loaded {@link LanguageDefinition}. Used by {@link #mergePolicy}
	 * to flag likely typos. The cost is amortized: the call is gated by
	 * {@code warnedTokens.add(token)} in the caller, so each unique token
	 * is looked up at most once per render.
	 */
	private static final boolean isKnownComponentName(String name, Component anchor) {
		final Page page = anchor != null ? anchor.getPage() : null;
		if (page != null && page.getComponentDefinition(name, true) != null)
			return true;
		for (LanguageDefinition lang : LanguageDefinition.getAll())
			if (lang.hasComponentDefinition(name))
				return true;
		return false;
	}

	/** Whether the component's name — or any name in its {@code extends}
	 * chain — matches the accumulated policy. Walking the chain lets a
	 * policy containing {@code "div"} also catch a user-registered alias
	 * such as {@code <?component name="myDiv" extends="div"?>}: the alias's
	 * own {@code getName()} is {@code "myDiv"}, but
	 * {@code getExtends().getName()} resolves back to {@code "div"}.
	 */
	private static final boolean policyMatches(Set<String> policy, Component comp) {
		if (policy == null || policy.isEmpty())
			return false;
		if (policy.contains(WILDCARD_TOKEN))
			return true;
		for (ComponentDefinition def = comp.getDefinition(); def != null; def = def.getExtends()) {
			final String name = def.getName();
			if (name != null && policy.contains(name))
				return true;
		}
		return false;
	}

	/** Makes a component as a stub-only component.
	 *
	 * <p><b>Design note — do NOT collapse {@code propagated} and {@code effective}.</b>
	 * These two booleans look like they're tracking the same thing, but they
	 * answer different questions and a future "simplification" that merges
	 * them will silently break ZK-6096:
	 * <ul>
	 *   <li>{@code propagated} is the value that cascades to children
	 *       unconditionally — it preserves the legacy {@code stubonly="true"}
	 *       inheritance behavior.</li>
	 *   <li>{@code effective} is whether <em>this</em> comp should be
	 *       stub-ized. It may be {@code true} purely from a policy match
	 *       without affecting children. If you merge it into
	 *       {@code propagated}, a policy match becomes infectious and you
	 *       lose the non-propagating semantics that distinguish policies
	 *       from legacy {@code stubonly="true"}.</li>
	 * </ul>
	 * The regression test for this invariant is {@code F104_ZK_6096Test}'s
	 * {@code g2lblStub=LIVE} assertion: a label child of a policy-stubbed
	 * div, with policy listing only {@code "div"}, must stay live.
	 *
	 * <p>A comp is matched against the policies declared on its ANCESTORS
	 * only — a policy declared on the comp itself targets its descendants,
	 * not the comp where the policy is anchored.
	 *
	 * @return whether if components in the whole sub-tree are all StubComponent
	 */
	private static final boolean stub(Component comp, boolean stubnative, boolean stubonly,
			Set<String> ancestorPolicy, Set<String> warnedTokens) {
		if (comp instanceof StubsComponent)
			return true; //nothing to do if stubs (i.e., merged stub components)
		//returns true so the parent will keep merging (aggressive algorithm)

		Boolean val = shallStubNative(comp);
		if (val != null)
			stubnative = val.booleanValue();

		boolean propagated = stubonly;
		val = shallStubonly(comp);
		if (val != null)
			propagated = val.booleanValue();
		boolean effective = propagated;
		if (!effective && val == null && policyMatches(ancestorPolicy, comp)) {
			effective = true; //policy match: this comp only, do not cascade
			if (log.isDebugEnabled())
				log.debug("stubonlyDescendants stubbed <{}> (uuid={}); list in effect: {}",
						comp.getDefinition() != null ? comp.getDefinition().getName() : "?",
						comp.getUuid(),
						ancestorPolicy);
		}

		// Compute the policy visible to descendants: union of ancestor policy
		// with whatever this comp declares.
		Set<String> policy = ancestorPolicy;
		final String declared = comp.getStubonlyDescendants();
		if (declared != null && !declared.isEmpty()) {
			policy = new HashSet<String>(ancestorPolicy);
			mergePolicy(policy, declared, comp, warnedTokens);
		}

		boolean allstub = true, anychild = false;
		for (Component child = comp.getFirstChild(); child != null; anychild = true) {
			Component n = child.getNextSibling();
			if (!stub(child, stubnative, propagated, policy, warnedTokens))
				allstub = false;
			child = n;
		}

		final boolean bNative = comp instanceof Native;
		if ((bNative && !stubnative) || (!bNative && !effective))
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
