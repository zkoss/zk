/* NativeInfo.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 21 10:20:34     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import org.zkoss.idom.Namespace;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.impl.EvaluatorRef;

/**
 * Represents the compmonent infomation about the native components.
 *
 * <p>Note:it is not thread-safe.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class NativeInfo extends ComponentInfo {
	private List _prokids, _epikids;
	private NativeInfo _splitkid;
	/** Declared namespaces (Namespace). */
	private List _dns;

	/** Constructs a native info with a parent.
	 *
	 * @param parent the parnet info (never null).
	 * @param compdef the component definition (never null).
	 */
	public NativeInfo(NodeInfo parent, ComponentDefinition compdef,
	String tagnm) {
		super(parent, compdef, tagnm);
		if (!compdef.isNative())
			throw new IllegalArgumentException("compdef must be native");
	}
	/** Constructs a native info without a parent.
	 * @param compdef the component definition (never null).
	 * @since 3.5.0
	 */
	public NativeInfo(EvaluatorRef evlar, ComponentDefinition compdef,
	String tagnm) {
		super(evlar, compdef, tagnm);
	}

	/** Returns a readonly list of the declared namespaces (never null).
	 */
	public List getDeclaredNamespaces() {
		return _dns != null ? _dns: Collections.EMPTY_LIST;
	}
	/** Adds a declared namespace.
	 */
	public void addDeclaredNamespace(Namespace ns) {
		if (_dns == null)
			_dns = new LinkedList();
		_dns.add(ns);
	}

	/** Returns a readonly list of the prolog children ({@link NativeInfo}).
	 * A prolog child is a special child of {@link NativeInfo}.
	 * (it is not part of {@link #getChildren}
	 * that is used to optimize the native components such that
	 * ZK can use one native components to represent several {@link NativeInfo}.
	 * In other words, a prolog child won't be created as part of
	 * <p>The prolog children must be rendered before {@link #getChildren}.
	 */
	public List getPrologChildren() {
		return _prokids != null ? _prokids: Collections.EMPTY_LIST;
	}
	/** Returns a readonly list of the epilog children ({@link NativeInfo}).
	 * The epilog children must be rendered after {@link #getChildren}.
	 */
	public List getEpilogChildren() {
		return _epikids != null ? _epikids: Collections.EMPTY_LIST;
	}
	/** Adds a prolog child.
	 *
	 * <p>Note: if child belong to other {@link ComponentInfo},
	 * you have to remove them first before calling this method.
	 * Otherwise, the result is unpreditable.
	 *
	 * @param child the prolog child.
	 * @see #getPrologChildren
	 */
	public void addPrologChild(NativeInfo child) {
		addPrologChildDirectly(child);
	}
	/** Adds a prolog child.
	 *
	 * @param child the prolog child. If child belongs to {@link #getChildren},
	 * it will be removed first.
	 * @see #getPrologChildren
	 */
	public void addPrologChild(ZScript child) {
		addPrologChildDirectly(child);
	}
	/** Adds a prolog child.
	 *
	 * @param child the prolog child. If child belongs to {@link #getChildren},
	 * it will be removed first.
	 * @see #getPrologChildren
	 */
	public void addPrologChild(VariablesInfo child) {
		addPrologChildDirectly(child);
	}
	/** Adds a prolog child.
	 *
	 * <p>Note: if child belong to other {@link ComponentInfo},
	 * you have to remove them first before calling this method.
	 * Otherwise, the result is unpreditable.
	 *
	 * @param child the prolog child.
	 * @see #getPrologChildren
	 */
	public void addPrologChild(AttributesInfo child) {
		addPrologChildDirectly(child);
	}
	/** Adds a text as a prolog child.
	 */
	public void addPrologChild(TextInfo text) {
		addPrologChildDirectly(text);
	}
	/** Adds a prolog child.
	 * @param chld the child can NOT be{@link ComponentInfo}.
	 */
	/*package*/ void addPrologChildDirectly(Object child) {
		if (_prokids == null)
			_prokids = new LinkedList();
		_prokids.add(child);
	}
	/** Adds an epilog child.
	 *
	 * @param child the epilog child. If child belongs to {@link #getChildren},
	 * it will be removed first.
	 * @see #getPrologChildren
	 */
	public void addEpilogChild(NativeInfo child) {
		addEpilogChildDirectly(child);
	}
	/** Adds an epilog child.
	 *
	 * @param child the epilog child. If child belongs to {@link #getChildren},
	 * it will be removed first.
	 * @see #getPrologChildren
	 */
	public void addEpilogChild(ZScript child) {
		addEpilogChildDirectly(child);
	}
	/** Adds an epilog child.
	 *
	 * @param child the epilog child. If child belongs to {@link #getChildren},
	 * it will be removed first.
	 * @see #getPrologChildren
	 */
	public void addEpilogChild(VariablesInfo child) {
		addEpilogChildDirectly(child);
	}
	/** Adds an epilog child.
	 *
	 * @param child the epilog child. If child belongs to {@link #getChildren},
	 * it will be removed first.
	 * @see #getPrologChildren
	 */
	public void addEpilogChild(AttributesInfo child) {
		addEpilogChildDirectly(child);
	}
	/** Adds an epilog child.
	 * @param chld the child can NOT be{@link ComponentInfo}.
	 */
	/*package*/ void addEpilogChildDirectly(Object child) {
		if (_epikids == null)
			_epikids = new LinkedList();
		_epikids.add(child);
	}

	/** Returns the split child, or null if not available.
	 * Each native info has at most one split child.
	 * If a native info has a single child and the child is also
	 * a native info, we can merge them by making the child as
	 * the split child.
	 *
	 * @since 3.0.0
	 */
	public NativeInfo getSplitChild() {
		return _splitkid;
	}
	/** Sets the split kid.
	 * @see #getSplitChild
	 * @since 3.0.0
	 */
	public void setSplitChild(NativeInfo child) {
		_splitkid = child;
	}

	//super//
	public Component newInstance(Page page, Component parent) {
		final Component comp = super.newInstance(page, parent);

		if (_dns != null) {
			final Native nc = (Native)comp;
			for (Iterator it = _dns.iterator(); it.hasNext();)
				nc.addDeclaredNamespace((Namespace)it.next());
		}

		return comp;
	}
	public Object clone() {
		final NativeInfo clone = (NativeInfo)super.clone();
		if (clone._prokids != null)
			clone._prokids = new LinkedList(clone._prokids);
		if (clone._epikids != null)
			clone._epikids = new LinkedList(clone._epikids);
		return clone;
	}
}
