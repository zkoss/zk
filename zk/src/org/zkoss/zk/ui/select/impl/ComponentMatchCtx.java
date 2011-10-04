/**
 * 
 */
package org.zkoss.zk.ui.select.impl;

import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;

/**
 * A wrapper of Component, providing a context for selector matching algorithm.
 * @author simonpai
 */
public class ComponentMatchCtx {
	
	private ComponentMatchCtx _parent;
	private Component _comp;
	
	// qualified positions
	private boolean[][] _qualified;
	
	// pseudo-class support
	private int _compChildIndex = -1;
	
	
	
	/*package*/ ComponentMatchCtx(Component component, List<Selector> selectorList){
		_comp = component;
		_qualified = new boolean[selectorList.size()][];
		
		for(Selector selector : selectorList)
			_qualified[selector.getSelectorIndex()] = new boolean[selector.size()];
		
		_compChildIndex = getComponentIndex();
	}
	
	/*package*/ ComponentMatchCtx(Component component, ComponentMatchCtx parent){
		_comp = component;
		int selectorListSize = parent._qualified.length;
		_qualified = new boolean[selectorListSize][];
		for(int i=0; i < selectorListSize; i++)
			_qualified[i] = new boolean[parent._qualified[i].length];
		_parent = parent;
		_compChildIndex = 0;
	}
	
	
	
	// operation //
	/*package*/ void moveToNextSibling(){
		_comp = _comp.getNextSibling();
		_compChildIndex++;
	}
	
	
	
	// getter //
	/**
	 * Return the parent context
	 */
	public ComponentMatchCtx getParent(){
		return _parent;
	}
	
	/**
	 * Return the component.
	 */
	public Component getComponent(){
		return _comp;
	}
	
	/**
	 * Return the child index of the component. If the component is one of the 
	 * page roots, return -1.
	 */
	public int getComponentChildIndex(){
		if(_compChildIndex > -1) return _compChildIndex;
		Component parent = _comp.getParent();
		return parent == null ? -1 : parent.getChildren().indexOf(_comp);
	}
	
	/**
	 * Return the count of total siblings of the component, including itself.
	 * @return
	 */
	public int getComponentSiblingSize(){
		Component parent = _comp.getParent();
		return parent == null ? 
				_comp.getPage().getRoots().size() : parent.getChildren().size();
	}
	
	
	
	// match position //
	/**
	 * Return true if the component matched the given position of the given 
	 * selector.
	 * @param selector
	 * @param position
	 * @return
	 */
	public boolean isQualified(int selectorIndex, int position) {
		return _qualified[selectorIndex][position];
	}
	
	/*package*/ void setQualified(int selectorIndex, int position) {
		setQualified(selectorIndex, position, true);
	}
	
	/*package*/ void setQualified(int selectorIndex, int position, 
			boolean qualified) {
		_qualified[selectorIndex][position] = qualified;
	}
	
	/**
	 * Return true if the component matched the last position of any selectors
	 * in the list. (i.e. the one we are looking for)
	 * @return
	 */
	public boolean isMatched() {
		for(int i = 0; i< _qualified.length; i++) 
			if(isMatched(i)) 
				return true;
		return false;
	}
	
	/**
	 * Return true if the component matched the last position of the given
	 * selector.
	 * @param selectorIndex
	 * @return
	 */
	public boolean isMatched(int selectorIndex) {
		boolean[] quals = _qualified[selectorIndex];
		return quals[quals.length-1];
	}
	
	
	
	// match local property //
	/**
	 * Return true if the component qualifies the local properties of a given
	 * SimpleSelectorSequence.
	 * @param seq 
	 * @param defs 
	 * @return
	 */
	public boolean match(SimpleSelectorSequence seq, 
			Map<String, PseudoClassDef> defs){
		return ComponentLocalProperties.match(this, seq, defs);
	}
	
	// TODO: remove after testing
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("");
		for(boolean[] bs : _qualified) { 
			sb.append("Q[");
			for(boolean b : bs) sb.append(b?'1':'0');
			sb.append("]");
		}
		return sb.append(", ").append(_comp).toString();
	}
	
	
	
	// helper //
	private int getComponentIndex(){
		Component curr = _comp;
		int index = -1;
		while(curr != null) {
			curr = curr.getPreviousSibling();
			index++;
		}
		return index;
	}
}
