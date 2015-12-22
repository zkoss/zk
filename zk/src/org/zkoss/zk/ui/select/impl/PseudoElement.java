package org.zkoss.zk.ui.select.impl;

/**
 * The model representing a pseudo element in Selector
 * @since 8.0.1
 * @author christopher
 */
public class PseudoElement {
	
	private String _name;
	
	public PseudoElement(String name) {
		_name = name;
	}
	
	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	public String toString() {
		return "::" + _name;
	}
}
