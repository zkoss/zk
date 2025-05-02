/* NativeInfo.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 21 10:20:34     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.idom.Namespace;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.xel.EvaluatorRef;

/**
 * Represents the component information about the native components.
 *
 * <p>Note:it is not thread-safe.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class NativeInfo extends ComponentInfo {
	private List<NodeInfo> _prokids, _epikids;
	private NativeInfo _splitkid;
	/** Declared namespaces (Namespace). */
	private List<Namespace> _dns;

	/** Constructs a native info with a parent.
	 *
	 * @param parent the parent info (never null).
	 * @param compdef the component definition (never null).
	 * @param tag the name of the tag
	 */
	public NativeInfo(NodeInfo parent, ComponentDefinition compdef, String tag) {
		super(parent, compdef, tag);
		if (!compdef.isNative())
			throw new IllegalArgumentException("compdef must be native");
	}

	/** Constructs a native info without a parent.
	 * @param evlar the evaluator (never null).
	 * @param compdef the component definition (never null).
	 * @param tag the name of the tag
	 * @since 3.5.0
	 */
	public NativeInfo(EvaluatorRef evlar, ComponentDefinition compdef, String tag) {
		super(evlar, compdef, tag);
	}

	/** Returns a readonly list of the declared namespaces (never null).
	 */
	public List<Namespace> getDeclaredNamespaces() {
		if (_dns != null)
			return _dns;
		return Collections.emptyList();
	}

	/** Adds a declared namespace.
	 */
	public void addDeclaredNamespace(Namespace ns) {
		if (_dns == null)
			_dns = new LinkedList<Namespace>();
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
	public List<NodeInfo> getPrologChildren() {
		if (_prokids != null)
			return _prokids;
		return Collections.emptyList();
	}

	/** Returns a readonly list of the epilog children ({@link NativeInfo}).
	 * The epilog children must be rendered after {@link #getChildren}.
	 */
	public List<NodeInfo> getEpilogChildren() {
		if (_epikids != null)
			return _epikids;
		return Collections.emptyList();
	}

	/** Adds a prolog child.
	 *
	 * <p>Note: if child belong to other {@link ComponentInfo},
	 * you have to remove them first before calling this method.
	 * Otherwise, the result is unpredictable.
	 *
	 * @param child the prolog child.
	 * @see #getPrologChildren
	 */
	public void addPrologChild(NodeInfo child) {
		if (_prokids == null)
			_prokids = new LinkedList<NodeInfo>();

		// fix ZK-2622
		if ("script".equalsIgnoreCase(getTag())) {
			if (child instanceof TextInfo) {
				String value = ((TextInfo) child).getRawValue();
				StringBuffer sb = null;
				for (int j = 0, len = value.length(); j < len; ++j) {
					final char cc = value.charAt(j);
					final String rep;
					switch (cc) {
					case '<':
						rep = "\\u003c";
						break;
					case '>':
						rep = "\\u003e";
						break;
					default:
						if (sb != null)
							sb.append(cc);
						continue;
					}

					if (sb == null) {
						sb = new StringBuffer(len + 8);
						if (j > 0)
							sb.append(value.substring(0, j));
					}
					sb.append(rep);
				}
				//ZK-5695: The original approach rebuilds a new TextInfo using sb as the text, but it loses other attribute values originally carried by the child.
				child = new TextInfo(child.getEvaluatorRef(), sb != null ? sb.toString() : value);
			}
		}
		_prokids.add(child);
	}

	/** Adds an epilog child.
	 *
	 * @param child the epilog child. If child belongs to {@link #getChildren},
	 * it will be removed first.
	 * @see #getPrologChildren
	 */
	public void addEpilogChild(NodeInfo child) {
		if (_epikids == null)
			_epikids = new LinkedList<NodeInfo>();
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
			final Native nc = (Native) comp;
			for (Namespace ns : _dns)
				nc.addDeclaredNamespace(ns);
		}

		return comp;
	}
}
