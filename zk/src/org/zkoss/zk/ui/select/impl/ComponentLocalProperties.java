/**
 * 
 */
package org.zkoss.zk.ui.select.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;

/**
 * A collection of utilities that check local properties of Components. 
 * Including type, ID, class, attribute, and pseudo class.
 * @since 6.0.0
 * @author simonpai
 */
public class ComponentLocalProperties {
	
	/**
	 * Returns true if the selector matches the given component. Combinators 
	 * are not allowed.
	 * @param component
	 * @param selector
	 */
	public static boolean match(Component component, String selector) {
		return match(component, selector, null);
	}
	
	/**
	 * Returns true if the selector matches the given component. Combinators 
	 * are not allowed. 
	 * @param component
	 * @param selector
	 * @param defs
	 */
	public static boolean match(Component component, String selector,
			Map<String, PseudoClassDef> defs) {
		List<Selector> selectorList = new Parser().parse(selector);
		ComponentMatchCtx ctx = new ComponentMatchCtx(component, selectorList);
		for(Selector s : selectorList) {
			if(s.size() > 1) continue;
			if(match(ctx, s.get(0), defs)) return true;
		}
		return false;
	}
	
	/*package*/ static boolean match(ComponentMatchCtx context, 
			SimpleSelectorSequence seq, 
			Map<String, PseudoClassDef> defs){
		Component comp = context.getComponent();
		return matchType(comp, seq.getType()) 
			&& matchID(comp, seq.getId()) 
			&& matchClasses(comp, seq.getClasses()) 
			&& matchAttributes(comp, seq.getAttributes()) 
			&& matchPseudoClasses(context, seq.getPseudoClasses(), defs);
	}
	
	/*package*/ static boolean matchID(Component component, String id){
		if(id == null) return true;
		return id.equals(component.getId());
	}
	
	/*package*/ static boolean matchType(Component component, String type){
		if(type == null) return true;
		return component.getDefinition().getName().toLowerCase().equals(type.toLowerCase());
	}
	
	/*package*/ static boolean matchClasses(Component component, 
			Set<String> classes){
		if(classes == null || classes.isEmpty()) return true;
		
		if(!(component instanceof HtmlBasedComponent)) return false;
		String scls = ((HtmlBasedComponent) component).getSclass();
		String zcls = ((HtmlBasedComponent) component).getZclass();
		
		for(String c : classes)
			if(scls == null || !scls.matches("(?:^|.*\\s)"+c+"(?:\\s.*|$)") 
					&& !Objects.equals(zcls, c)) return false;
		
		return true;
	}
	
	/*package*/ static boolean matchAttributes(Component component, 
			List<Attribute> attributes){
		if(attributes == null || attributes.isEmpty()) return true;
		
		for(Attribute attr : attributes)
			if(!matchValue(getValue(component, attr.getName()), attr)) 
				return false;
		
		return true;
	}
	
	/*package*/ static boolean matchPseudoClasses(
			ComponentMatchCtx context, List<PseudoClass> pseudoClasses, 
			Map<String, PseudoClassDef> defs){
		if(pseudoClasses == null || pseudoClasses.isEmpty()) return true;
		
		for(PseudoClass pc : pseudoClasses){
			PseudoClassDef def = getPseudoClassDef(defs, pc.getName());
			if(def == null) throw new UiException(
					"Pseudo class definition not found: " + pc.getName());
			
			String[] param = pc.getParameter();
			if(param == null? !def.accept(context) : 
				!def.accept(context, pc.getParameter())) 
					return false;
		}
		return true;
	}
	
	
	
	// helper //
	private static Object getValue(Component component, String name){
		try {
			return component.getClass().getMethod(
					"get"+capitalize(name)).invoke(component);
		} catch (NoSuchMethodException e) {
			// no such method
		} catch (SecurityException e) {
			// SecurityManager doesn't like you
		} catch (IllegalAccessException e) {
			// attempted to call a non-public method
		} catch (InvocationTargetException e) {
			// exception thrown by the getter method
		}
		try {
			return component.getClass().getMethod(
					"is"+capitalize(name)).invoke(component);
		} catch (NoSuchMethodException e) {
			// no such method
		} catch (SecurityException e) {
			// SecurityManager doesn't like you
		} catch (IllegalAccessException e) {
			// attempted to call a non-public method
		} catch (InvocationTargetException e) {
			// exception thrown by the getter method
		}
		// try dynamic attribute
		return component.getAttribute(name);
	}
	
	private static String capitalize(String str){
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}
	
	private static PseudoClassDef getPseudoClassDef(
			Map<String, PseudoClassDef> defs, String className) {
		PseudoClassDef def = null;
		if(defs != null && !defs.isEmpty())
			def = defs.get(className);
		if(def != null) return def;
		return BasicPseudoClassDefs.getDefinition(className);
	}
	
	private static Object parseData(String source, Class<?> expectedType){
		// TODO: enhance type support
		if(expectedType.equals(Integer.class)) return new Integer(source);
		if(expectedType.equals(Boolean.class)) return new Boolean(source);
		if(expectedType.equals(Double.class))  return new Double(source);
		return source;
	}
	
	private static boolean matchValue(Object value, Attribute attr){
		switch(attr.getOperator()){
		case BEGIN_WITH:
			return value!=null && value.toString().startsWith(attr.getValue());
		case END_WITH:
			return value!=null && value.toString().endsWith(attr.getValue());
		case CONTAIN:
			return value!=null && value.toString().contains(attr.getValue());
		case EQUAL:
		default:
			try {
				Object attrValue = parseData(attr.getValue(), 
						attr.isQuoted()? String.class : value.getClass());
				return Objects.equals(value, attrValue);
			} catch (Exception e) {
				// failed to convert attribute value to expected type
				return false;
			}
		}
	}
	
}
