/**
 * 
 */
package org.zkoss.zk.ui.select.impl;

/**
 * The model representing an attribute in Selector.
 * @author simonpai
 * @since 6.0.0
 */
public class Attribute {
	
	private String _name;
	private Operator _operator;
	private String _value;
	private boolean _quoted = false;
	
	public Attribute(String name) {
		_name = name;
	}

	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	public Operator getOperator() {
		return _operator;
	}
	
	public void setOperator(Operator operator) {
		_operator = operator;
	}
	
	public String getValue() {
		return _value;
	}
	
	public void setValue(String value) {
		setValue(value, false);
	}
	
	public void setValue(String value, boolean quoted){
		// TODO: parse value
		_quoted = quoted;
		_value = value;
	}
	
	public boolean isQuoted(){
		return _quoted;
	}
	
	public void setQuoted(boolean quoted){
		_quoted = quoted;
	}
	
	public enum Operator {
		EQUAL("="), BEGIN_WITH("^="), END_WITH("$="), CONTAIN("*=");
		
		private final String _str;
		Operator(String str){ _str = str; }
		@Override
		public String toString(){ return _str; }
	}
	
	@Override
	public String toString() {
		String qt = isQuoted()? "\"" : "";
		return "[" + _name + _operator.toString() + qt + _value + qt +"]";
	}
	
}
