/**
 * 
 */
package org.zkoss.zk.ui.select.impl;

import java.util.ArrayList;

/**
 * The model representing a selector.
 * @author simonpai
 */
public class Selector extends ArrayList<SimpleSelectorSequence> {
	
	private static final long serialVersionUID = -9125226126564264333L;
	
	private final int _selectorIndex;
	
	public Selector(int selectorIndex){
		_selectorIndex = selectorIndex;
	}
	
	/**
	 * Add combinator to the last simple selector sequence
	 */
	public void attachCombinator(Combinator combinator){
		if(isEmpty()) throw new ParseException("Cannot have combinator " + 
				"prior to the first sequence of simple selectors.");
		get(size()-1).setCombinator(combinator);
	}
	
	/**
	 * Return the index of this selector in a multiple selector sequence.
	 */
	public int getSelectorIndex(){
		return _selectorIndex;
	}
	
	/**
	 * Return the i-th combinator
	 */
	public Combinator getCombinator(int index){
		return get(index).getCombinator();
	}
	
	public enum Combinator {
		DESCENDANT(" "), CHILD(" > "), 
		ADJACENT_SIBLING(" + "), GENERAL_SIBLING(" ~ ");
		
		private final String _str;
		Combinator(String str){ _str = str; }
		@Override
		public String toString() { return _str; }
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		int size = size();
		for(int i=0; i<size; i++){
			SimpleSelectorSequence seq = get(i);
			sb.append(seq);
			if(i < size-1) sb.append(seq.getCombinator());
		}
		return sb.toString();
	}
	
}
