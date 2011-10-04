/**
 * 
 */
package org.zkoss.zk.ui.select.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.select.impl.Attribute.Operator;
import org.zkoss.zk.ui.select.impl.Selector.Combinator;

/**
 * The model representing a sequence of simple selectors.
 * @author simonpai
 */
public class SimpleSelectorSequence {
	
	private Combinator _combinator;
	private String _type;
	private String _id;
	private Set<String> _classes;
	private List<Attribute> _attributes;
	private List<PseudoClass> _pseudoClasses;
	
	private Attribute _currAttribute;
	private PseudoClass _currPseudoClass;
	
	public SimpleSelectorSequence(){
		_combinator = Combinator.DESCENDANT;
		_classes = new HashSet<String>();
		_attributes = new ArrayList<Attribute>();
		_pseudoClasses = new ArrayList<PseudoClass>();
	}
	
	public SimpleSelectorSequence(String type){
		this();
		_type = type;
	}
	
	
	
	// getter //
	public Combinator getCombinator(){
		return _combinator;
	}
	
	public String getType(){
		return _type;
	}
	
	public String getId(){
		return _id;
	}
	
	public Set<String> getClasses(){
		return Collections.unmodifiableSet(_classes);
	}
	
	public List<Attribute> getAttributes(){
		return Collections.unmodifiableList(_attributes);
	}
	
	public List<PseudoClass> getPseudoClasses(){
		return Collections.unmodifiableList(_pseudoClasses);
	}
	
	
	
	// setter //
	public void setCombinator(Combinator combinator){
		_combinator = combinator;
	}
	
	public void setType(String type){
		_type = type;
	}
	
	public void setId(String id){
		_id = id;
	}
	
	public void addClass(String clazz){
		if(!_classes.contains(clazz)) _classes.add(clazz);
	}
	
	public void addAttribute(String name){
		_attributes.add(_currAttribute = new Attribute(name));
	}
	
	public void attachAttributeOperator(Operator operator){
		if(_currAttribute == null) throw new IllegalStateException();
		_currAttribute.setOperator(operator);
	}
	
	public void attachAttributeValue(String value){
		attachAttributeValue(value, false);
	}
	
	public void attachAttributeValue(String value, boolean quoted){
		if(_currAttribute == null) throw new IllegalStateException();
		_currAttribute.setValue(value, quoted);
	}
	
	public void attachAttributeQuote(boolean inQuote){
		if(_currAttribute == null) throw new IllegalStateException();
		_currAttribute.setQuoted(inQuote);
	}
	
	public void addPseudoClass(String function){
		_pseudoClasses.add(_currPseudoClass = new PseudoClass(function));
	}
	
	public void attachPseudoClassParameter(String parameter){
		if(_currPseudoClass == null) throw new IllegalStateException();
		_currPseudoClass.addParameter(parameter);
	}
	
	@Override
	public String toString() {
		if(_type == null && _id == null && _classes.isEmpty() && 
				_pseudoClasses.isEmpty() && _attributes.isEmpty()) return "*";
		
		StringBuffer sb = new StringBuffer(_type == null ? "" : _type.toString());
		
		if(_id != null) 
			sb.append("#").append(_id);
		
		if(!_classes.isEmpty()) 
			for(String c : _classes) 
				sb.append(".").append(c);
		
		if(!_pseudoClasses.isEmpty()) 
			for(PseudoClass p : _pseudoClasses)
				sb.append(p);
		
		if(!_attributes.isEmpty())
			for(Attribute a : _attributes)
				sb.append(a);
		
		return sb.toString();
	}
	
}
