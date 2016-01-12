/**
 * 
 */
package org.zkoss.zk.ui.select.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.select.impl.Attribute.Operator;
import org.zkoss.zk.ui.select.impl.Selector.Combinator;

/**
 * The model representing a sequence of simple selectors.
 * @since 6.0.0
 * @author simonpai
 */
public class SimpleSelectorSequence {
	
	private Combinator _combinator;
	private String _type;
	private String _id;
	private Set<String> _classes;
	private List<Attribute> _attributes;
	private List<PseudoClass> _pseudoClasses;
	private List<PseudoElement> _pseudoElements;
	
	private Attribute _currAttribute;
	private PseudoClass _currPseudoClass;
	
	private List<Selectors> _toStringOrder;
	
	private enum Selectors {
		TYPE, ID, CLASS, ATTRIBUTE, PSEUDO_CLASS, PSEUDO_ELEMENT
	}
	
	public SimpleSelectorSequence(){
		_combinator = Combinator.DESCENDANT;
		_classes = new LinkedHashSet<String>();
		_attributes = new LinkedList<Attribute>();
		_pseudoClasses = new LinkedList<PseudoClass>();
		_pseudoElements = new LinkedList<PseudoElement>();
		_toStringOrder = new LinkedList<Selectors>();
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
	
	public List<PseudoElement> getPseudoElements(){
		return Collections.unmodifiableList(_pseudoElements);
	}
	
	
	// setter //
	public void setCombinator(Combinator combinator){
		_combinator = combinator;
	}
	
	public void setType(String type){
		_type = type;
		_toStringOrder.add(Selectors.TYPE);
	}
	
	public void setId(String id){
		_id = id;
		_toStringOrder.add(Selectors.ID);
	}
	
	public void addClass(String clazz){
		if (!_classes.contains(clazz)) {
			_classes.add(clazz);
			_toStringOrder.add(Selectors.CLASS);
		}
	}
	
	public void addAttribute(String name){
		_attributes.add(_currAttribute = new Attribute(name));
		_toStringOrder.add(Selectors.ATTRIBUTE);
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
		_toStringOrder.add(Selectors.PSEUDO_CLASS);
	}
	
	public void attachPseudoClassParameter(String parameter){
		if(_currPseudoClass == null) throw new IllegalStateException();
		_currPseudoClass.addParameter(parameter);
	}
	
	public void addPseudoElement(String source) {
		_pseudoElements.add(new PseudoElement(source));
		_toStringOrder.add(Selectors.PSEUDO_ELEMENT);
	}
	
	public String toString() {
		if (_type == null &&
			_id == null &&
			_classes.isEmpty() &&
			_pseudoClasses.isEmpty() &&
			_attributes.isEmpty() &&
			_pseudoElements.isEmpty())
			return "*";
		
		StringBuffer sb = new StringBuffer();
		
		Iterator<String> classIter = null;
		if(!_classes.isEmpty())
			classIter = _classes.iterator();
		
		Iterator<Attribute> attrIter = null;
		if(!_attributes.isEmpty())
			attrIter = _attributes.iterator();
		
		Iterator<PseudoClass> pseudoClassIter = null;
		if(!_pseudoClasses.isEmpty())
			pseudoClassIter = _pseudoClasses.iterator();
			
		Iterator<PseudoElement> pasueoElemIter = null;
		if (!_pseudoElements.isEmpty())
			pasueoElemIter = _pseudoElements.iterator();
			
		// ZK-2944: maintain the order of input
		for (Selectors s : _toStringOrder) {
			switch (s) {
			case TYPE:
				sb.append(_type == null ? "" : _type.toString());
				break;
			case ID:
				if(_id != null) sb.append('#').append(_id);
				break;
			case CLASS:
				if (classIter != null && classIter.hasNext())
					sb.append('.').append(classIter.next());
				break;
			case ATTRIBUTE:
				if (attrIter != null && attrIter.hasNext())
					sb.append(attrIter.next());
				break;
			case PSEUDO_CLASS:
				if (pseudoClassIter != null && pseudoClassIter.hasNext())
					sb.append(pseudoClassIter.next());
				break;
			case PSEUDO_ELEMENT:
				if (pasueoElemIter != null && pasueoElemIter.hasNext())
					sb.append(pasueoElemIter.next());
				break;
			}
		}
		
		return sb.toString();
	}
}
