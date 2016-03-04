/**
 * 
 */
package org.zkoss.zk.ui.select.impl;

import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.ComponentCtrl;

/**
 * A wrapper of Component, providing a context for selector matching algorithm.
 * @since 6.0.0
 * @author simonpai
 */
public class ComponentMatchCtx {

	private ComponentMatchCtx _parent;
	private Component _comp;

	// qualified positions
	private final boolean[][] _qualified;

	// pseudo-class support
	private int _compChildIndex = -1;

	// ::shadow support
	private boolean _isShadowHost = false;

	/*package*/ ComponentMatchCtx(Component component) { // used by root jumping
		_comp = component;
		_qualified = new boolean[0][0];
		_compChildIndex = getComponentIndex();
		_isShadowHost = !((ComponentCtrl) component).getShadowRoots().isEmpty();
	}

	/*package*/ ComponentMatchCtx(Component component, List<Selector> selectorList) { // root
		_comp = component;
		_qualified = new boolean[selectorList.size()][];

		for (Selector selector : selectorList)
			_qualified[selector.getSelectorIndex()] = new boolean[selector.size()];

		_compChildIndex = getComponentIndex();

		_isShadowHost = !((ComponentCtrl) component).getShadowRoots().isEmpty();
	}

	/*package*/ ComponentMatchCtx(Component component, ComponentMatchCtx parent) { // first child
		_comp = component;
		int selectorListSize = parent._qualified.length;
		_qualified = new boolean[selectorListSize][];
		for (int i = 0; i < selectorListSize; i++)
			_qualified[i] = new boolean[parent._qualified[i].length];
		_parent = parent;
		_compChildIndex = 0;

		_isShadowHost = !((ComponentCtrl) component).getShadowRoots().isEmpty();
	}

	// operation //
	/*package*/ void moveToNextSibling() {
		_comp = _comp.getNextSibling();
		_compChildIndex++;
		// ZK-2944: status clearing is conditional, let the caller handle it
		// reset the status when moving to siblings 
		_isShadowHost = !((ComponentCtrl) _comp).getShadowRoots().isEmpty();
	}

	/*package*/ void moveToNextShadowSibling(Component next) {
		_comp = next;
		_compChildIndex++;
		// ZK-2944: status clearing is conditional, let the caller handle it
		// reset the status when moving to siblings 
		_isShadowHost = !((ComponentCtrl) next).getShadowRoots().isEmpty();
	}

	// getter //
	/**
	 * Return the parent context
	 */
	public ComponentMatchCtx getParent() {
		return _parent;
	}

	/**
	 * Return the component.
	 */
	public Component getComponent() {
		return _comp;
	}

	/**
	 * Return the child index of the component. If the component is one of the 
	 * page roots, return -1.
	 */
	public int getComponentChildIndex() {
		if (_compChildIndex > -1)
			return _compChildIndex;
		Component parent = _comp.getParent();
		return parent == null ? -1 : parent.getChildren().indexOf(_comp);
	}

	/**
	 * Return the count of total siblings of the component, including itself.
	 */
	public int getComponentSiblingSize() {
		Component parent = _comp.getParent();
		return parent == null ? _comp.getPage().getRoots().size() : parent.getChildren().size();
	}

	// match position //
	/**
	 * Return true if the component matched the given position of the given 
	 * selector.
	 * @param selectorIndex
	 * @param position
	 */
	public boolean isQualified(int selectorIndex, int position) {
		if (selectorIndex < 0 || selectorIndex >= _qualified.length)
			return false;
		boolean[] posq = _qualified[selectorIndex];
		return position > -1 && position < posq.length && posq[position];
	}

	/*package*/ void setQualified(int selectorIndex, int position) {
		setQualified(selectorIndex, position, true);
	}

	/*package*/ void setQualified(int selectorIndex, int position, boolean qualified) {
		_qualified[selectorIndex][position] = qualified;
	}

	/**
	 * Return true if the component matched the last position of any selectors
	 * in the list. (i.e. the one we are looking for)
	 */
	public boolean isMatched() {
		for (int i = 0; i < _qualified.length; i++)
			if (isMatched(i))
				return true;
		return false;
	}

	/**
	 * Return true if the component matched the last position of the given
	 * selector.
	 * @param selectorIndex
	 */
	public boolean isMatched(int selectorIndex) {
		if (selectorIndex < 0 || selectorIndex >= _qualified.length)
			return false;
		boolean[] quals = _qualified[selectorIndex];
		return quals[quals.length - 1];
	}

	// match local property //
	/**
	 * Return true if the component qualifies the local properties of a given
	 * SimpleSelectorSequence.
	 * @param seq 
	 * @param defs 
	 */
	public boolean match(SimpleSelectorSequence seq, Map<String, PseudoClassDef> defs) {
		return ComponentLocalProperties.match(this, seq, defs);
	}

	/**
	 * Return true if the component is a shadow host, i.e. component's seRoots is not empty
	 */
	public boolean isShadowHost() {
		return this._isShadowHost;
	}

	// helper //
	private int getComponentIndex() {
		Component curr = _comp;
		int index = -1;
		while (curr != null) {
			curr = curr.getPreviousSibling();
			index++;
		}
		return index;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		str(sb, _qualified);
		sb.append(' ');
		for (ComponentMatchCtx c = this; (c = c.getParent()) != null;)
			sb.append("  ");
		sb.append(_comp);
		return sb.toString();
	}

	private static void str(StringBuilder sb, boolean[][] arr) {
		if (arr.length > 1)
			sb.append('[');
		for (int i = 0; i < arr.length; i++) {
			if (i > 0)
				sb.append(',');
			str(sb, arr[i]);
		}
		if (arr.length > 1)
			sb.append(']');
	}

	private static void str(StringBuilder sb, boolean[] arr) {
		sb.append('[');
		for (boolean b : arr)
			sb.append(b ? '1' : '0');
		sb.append(']');
	}

}
