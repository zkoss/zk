/* ConventionWires.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec  8 13:04:09 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.util;

import java.lang.reflect.Method;

import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;

/**
 * Utilities to wire variables by name conventions.
 * For wiring variables with annotations or with CSS3 selector, please use
 * {@link org.zkoss.zk.ui.select.Selectors} instead.
 *
 * @author tomyeh
 * @since 6.0.0
 */
public class ConventionWires {
	private static final Log log = Log.lookup(ConventionWires.class);

	/** Wire fellow components and space owner ancestors of the specified 
	 * Id space into a controller Java object. This implementation checks the 
	 * setXxx() method names first then the
	 * field names. If a setXxx() method name matches the id of a fellow or
	 * space owner ancestors and with correct 
	 * argument type, the found method is called with the fellow component as the 
	 * argument. If no proper setXxx() method then search the field of the 
	 * controller object for a matched field with name equals to the fellow 
	 * component's id and proper type. Then the fellow component 
	 * is assigned as the value of the matched field.
	 * 
	 * <p>Note that fellow components are looked up first, then the space owner
	 * ancestors<p>
	 * <p>since 3.5.2, the controller would be assigned as a variable of the given idspace 
	 * per the naming convention composed of the idspace id and controller Class name. e.g.
	 * if the idspace id is "xwin" and the controller class is 
	 * org.zkoss.MyController, then the variable name would be "xwin$MyController"</p>
	 * 
	 * <p>This is useful in writing controller code in MVC design practice. You
	 * can wire the components into the controller object per the
	 * component's id and do whatever you like.</p>
	 *
	 * <p>Since 3.6.0, for Groovy or other environment that
	 * '$' is not applicable, you can invoke {@link #wireFellows(IdSpace,Object,char)}
	 * to use '_' as the separator.
	 * 
	 * @param idspace the id space to be bound
	 * @param controller the controller Java object to be injected the fellow components.
	 */
	public static final void wireFellows(IdSpace idspace, Object controller) {
		new ConventionWire(controller).wireFellows(idspace);
	}
	/** Wire fellow components and space owner with a custom separator.
	 * The separator is used to separate the component ID and additional
	 * information, such as event name.
	 * By default, it is '$'. However, for Groovy or other environment that
	 * '$' is not applicable, you can invoke this method to use '_' as
	 * the separator.
	 * @see #wireFellows(IdSpace, Object)
	 */
	public static final
	void wireFellows(IdSpace idspace, Object controller, char separator) {
		new ConventionWire(controller, separator).wireFellows(idspace);
	}
	/** Wire fellow components and space owner with full control.
	 * @param separator the separator used to separate the component ID and event name.
	 * @param ignoreZScript whether to ignore variables defined in zscript when wiring
	 * a member.
	 * @param ignoreXel whether to ignore variables defined in varible resolver
	 * ({@link Page#addVariableResolver}) when wiring a member.
	 */
	public static final
	void wireFellows(IdSpace idspace, Object controller, char separator,
	boolean ignoreZScript, boolean ignoreXel) {
		new ConventionWire(controller, separator, ignoreZScript, ignoreXel).wireFellows(idspace);
	}

	/** <p>Wire accessible variable objects of the specified component into a 
	 * controller Java object. This implementation checks the 
	 * setXxx() method names first then the field names. If a setXxx() method 
	 * name matches the name of the resolved variable object with correct 
	 * argument type and the associated field value is null, then the method is 
	 * called with the resolved variable object as the argument. 
	 * If no proper setXxx() method then search the 
	 * field name of the controller object. If the field name matches the name
	 * of the resolved variable object with correct field type and null field
	 * value, the field is then assigned the resolved variable object.
	 * </p> 
	 * 
	 * <p>The controller would be assigned as a variable of the given component
	 * per the naming convention composed of the component id and controller Class name. e.g.
	 * if the component id is "xwin" and the controller class is 
	 * org.zkoss.MyController, then the variable name would be "xwin$MyController"</p>
	 *
	 * <p>This is useful in writing controller code in MVC design practice. You
	 * can wire the embedded objects, components, and accessible variables into 
	 * the controller object per the components' id and variables' name and do 
	 * whatever you like.
	 * </p>
	 
	 * <p>Since 3.6.0, for Groovy or other environment that
	 * '$' is not applicable, you can invoke {@link #wireVariables(Component,Object,char)}
	 * to use '_' as the separator.
	 * 
	 * @param comp the reference component to wire variables
	 * @param controller the controller Java object to be injected the 
	 * accessible variable objects.
	 * @see org.zkoss.zk.ui.util.GenericAutowireComposer
	 */
	public static final void wireVariables(Component comp, Object controller) {
		new ConventionWire(controller).wireVariables(comp);
	}
	/** Wire accessible variable objects of the specified component with a custom separator.
	 * The separator is used to separate the component ID and additional
	 * information, such as event name.
	 * By default, it is '$'. However, for Groovy or other environment that
	 * '$' is not applicable, you can invoke this method to use '_' as
	 * the separator.
	 * @see #wireVariables(Component, Object)
	 */
	public static final
	void wireVariables(Component comp, Object controller, char separator) {
		new ConventionWire(controller, separator).wireVariables(comp);
	}
	/** Wire controller as a variable objects of the specified component with full control.
	 * @param separator the separator used to separate the component ID and event name.
	 * @param ignoreZScript whether to ignore variables defined in zscript when wiring
	 * a member.
	 * @param ignoreXel whether to ignore variables defined in varible resolver
	 * ({@link Page#addVariableResolver}) when wiring a member.
	 */
	public static final
	void wireVariables(Component comp, Object controller, char separator,
	boolean ignoreZScript, boolean ignoreXel) {
		new ConventionWire(controller, separator, ignoreZScript, ignoreXel).wireVariables(comp);
	}

	/** <p>Wire accessible variables of the specified page into a 
	 * controller Java object. This implementation checks the 
	 * setXxx() method names first then the field names. If a setXxx() method 
	 * name matches the name of the resolved variable object with correct 
	 * argument type and the associated field value is null, then the method is 
	 * called with the resolved variable object as the argument. 
	 * If no proper setXxx() method then search the 
	 * field name of the controller object. If the field name matches the name
	 * of the resolved variable object with correct field type and null field
	 * value, the field is then assigned the resolved variable object.</p> 
	 *
	 * <p>The controller would be assigned as a variable of the given page 
	 * per the naming convention composed of the page id and controller Class name. e.g.
	 * if the page id is "xpage" and the controller class is 
	 * org.zkoss.MyController, then the variable name would be "xpage$MyController"</p>
	 *
	 * <p>Since 3.0.8, if the method name of field name matches the ZK implicit
	 * object name, ZK implicit object will be wired in, too.</p> 
	 * <p>This is useful in writing controller code in MVC design practice. You
	 * can wire the embedded objects, components, and accessible variables into 
	 * the controller object per the component's id and variable name and do 
	 * whatever you like.
	 * </p>
	 * 
	 * <p>Since 3.6.0, for Groovy or other environment that
	 * '$' is not applicable, you can invoke {@link #wireVariables(Page,Object,char)}
	 * to use '_' as the separator.
	 * 
	 * @param page the reference page to wire variables
	 * @param controller the controller Java object to be injected the fellow components.
	 */
	public static final void wireVariables(Page page, Object controller) {
		new ConventionWire(controller).wireVariables(page);
	}
	/** Wire accessible variable objects of the specified page with a custom separator.
	 * The separator is used to separate the component ID and additional
	 * information, such as event name.
	 * By default, it is '$'. However, for Groovy or other environment that
	 * '$' is not applicable, you can invoke this method to use '_' as
	 * the separator.
	 * @see #wireVariables(Page, Object)
	 */
	public static final
	void wireVariables(Page page, Object controller, char separator) {
		new ConventionWire(controller, separator).wireVariables(page);
	}
	/** Wire accessible variable objects of the specified page with complete control.
	 * @param separator the separator used to separate the component ID and event name.
	 * @param ignoreZScript whether to ignore variables defined in zscript when wiring
	 * a member.
	 * @param ignoreXel whether to ignore variables defined in varible resolver
	 * ({@link Page#addVariableResolver}) when wiring a member.
	 */
	public static final
	void wireVariables(Page page, Object controller, char separator,
	boolean ignoreZScript, boolean ignoreXel) {
		new ConventionWire(controller, separator, ignoreZScript, ignoreXel).wireVariables(page);
	}

	/** Wire controller as an attribute of the specified component.
	 * Please refer to <a href="http://books.zkoss.org/wiki/ZK_Developer%27s_Reference/MVC/Controller/Composer">ZK Developer's Reference</a>
	 * for details.
	 * <p>The separator is used to separate the component ID and the controller.
	 * By default, it is '$'. However, for Groovy or other environment that
	 * '$' is not applicable, you can invoke {@link #wireController(Component, Object, char)}
	 * to use '_' as the separator.
	 */
	public static final
	void wireController(Component comp, Object controller) {
		wireController(comp, controller, '$');
	}

	/** Wire controller as an attribute of the specified component with a custom separator.
	 * <p>The separator is used to separate the component ID and the controller.
	 * By default, it is '$'. However, for Groovy or other environment that
	 * '$' is not applicable, you can invoke this method to use '_' as
	 * the separator.
	 */
	public static final
	void wireController(Component comp, Object controller, char separator) {
		//feature #3326788: support custom name
		Object onm = comp.getAttribute("composerName");
		if (onm instanceof String && ((String)onm).length() > 0) {
			comp.setAttribute((String)onm, controller);
		} else {
			//feature #2778513, support {id}$composer name
			final String id = comp.getId();
			final String nm = id + separator + "composer";
			if (!comp.hasAttributeOrFellow(nm, false))
				comp.setAttribute(nm, controller);

			//support {id}$ClassName
			comp.setAttribute(
				composerNameByClass(id, controller.getClass(), separator), controller);
		}
	}
	private static String composerNameByClass(String id, Class cls, char separator) {
		final String clsname = cls.getName();
		int j = clsname.lastIndexOf('.');
		return id + separator + (j >= 0 ? clsname.substring(j+1) : clsname);
	}

	/**Wire implicit variables of the specified component into a controller Java object. 
	 * 
	 * @param comp the component
	 * @param controller the controller object
	 */
	public static final
	void wireImplicit(Component comp, Object controller) {
		new ConventionWire(controller, '$', true, true).wireImplicit(comp);
	}
	
	/** <p>Adds forward conditions to myid source component so onXxx source 
	 * event received by 
	 * myid component can be forwarded to the specified target 
	 * component with the target event name onXxx$myid.</p> 
	 * <p>The controller is a POJO file with onXxx$myid methods (the event handler 
	 * codes). This utility method search such onXxx$myid methods and adds 
	 * forward condition to the source myid component looked up by   
	 * {@link Component#getAttributeOrFellow} of the specified component, so you 
	 * don't have to specify in zul file the "forward" attribute one by one. 
	 * If the source component cannot be looked up or the object looked up is 
	 * not a component, this method will log the error and ignore it.
	 * </p>
	 * <p>Cascaded '$' will add Forwards cascadely. E.g. define method 
	 * onClick$btn$w1 in window w2. This method will add a forward on the button 
	 * "btn.onClick=w1.onClick$btn" and add another forward on the window w1
	 * "w1.onClick$btn=w2.onClick$btn$w1"</p>
	 * 
	 * <p>Since 3.6.0, for Groovy or other environment that
	 * '$' is not applicable, you can invoke {@link #addForwards(Component,Object,char)}
	 * to use '_' as the separator.
	 * 
	 * @param comp the targetComponent
	 * @param controller the controller code with onXxx$myid event handler methods
	 */
	public static void addForwards(Component comp, Object controller) {
		addForwards(comp, controller, '$');
	}
	/** Adds forward conditions to the specified component with a custom separator.
	 * The separator is used to separate the component ID and additional
	 * information, such as event name.
	 * By default, it is '$'. However, for Groovy or other environment that
	 * '$' is not applicable, you can invoke this method to use '_' as
	 * the separator.
	 * @see #addForwards(Component, Object)
	 */
	public static
	void addForwards(Component comp, Object controller, char separator) {
		final Class cls = controller.getClass();
		final Method[] mtds = cls.getMethods();
		for (int j = 0; j < mtds.length; ++j) {
			final Method md = mtds[j];
			String mdname = md.getName();
			if (mdname.length() >= 5 && Events.isValid(mdname)) { //onX$Y
				Component xcomp = comp;
				int k = 0;
				do { //handle cascade $. e.g. onClick$btn$win1
					k = mdname.lastIndexOf(separator);
					if (k >= 3) { //found '$'
						final String srcevt = mdname.substring(0, k);
						if ((k+1) < mdname.length()) {
							final String srccompid = mdname.substring(k+1);
							Object srccomp = xcomp.getAttributeOrFellow(srccompid, true);
							if (srccomp == null) {
								Page page = xcomp.getPage();
								if (page != null)
									srccomp = page.getXelVariable(null, null, srccompid, true);
							}
							if (srccomp == null || !(srccomp instanceof Component)) {
								if (log.debugable())
									log.debug("Cannot find the associated component to forward event: "+mdname);
								break;
							} else {
								((Component)srccomp).addForward(srcevt, xcomp, mdname);
								xcomp = (Component) srccomp;
								mdname = srcevt;
							}
						} else {
							throw new UiException("Illegal event method name(component id not specified or consecutive '"+separator+"'): "+md.getName());
						}
					}
				} while (k >= 3);
			}
		}
	}
}
