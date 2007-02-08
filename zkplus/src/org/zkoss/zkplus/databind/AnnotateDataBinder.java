/* AnnotateDataBinder.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Nov 16 13:22:37     2006, Created by Henri Chen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.databind;

import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.metainfo.Annotation; 

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Iterator;

/**
 * <p>The DataBinder that reads ZUML annotations to create binding info. The ZUML page must declare the 
 * XML namespace, xmlns:a="http://www.zkoss.org/2005/zk/annotation", to use the ZUML annotations. 
 * The annotation is declared before each Component. For example, the following annotation associates the 
 * attibute "value" of the component "textbox" to the bean's value "person.address.city".</p>
 * <pre>
 * &lt;a:bind value="person.address.city"/>
 * &lt;textbox/>
 * </pre>
 * <p></p>
 *
 * <p>The AnnotateDataBinder knows "a:bind" annotation only. The complete format is like this:
 * <pre>
 * &lt;a:bind attrY="bean's value;[tag:expression]..."/>
 * &lt;componentX/>
 * </pre>
 *
 * <p>This associates the componentX's attribute attrY to the bean's value. The bean's value is something
 * in the form of beanid.field1.field2... You can either call {@link DataBinder#bindBean} to bind the beanid to a
 * real bean object or you can neglect it and this DataBinder would try to find it from the variables map via
 * ({@link org.zkoss.zk.ui.Component#getVariable} method. That is, all those variables defined in zscript are 
 * accessible by this DataBinder.</p>
 *
 * <p>The tag:expression is a generic form to bind more metainfo to the attrY of the componentX. The currently 
 * supported tag includes "load-when", "save-when", "access", and "converter"</p>
 *
 * <ul>
 * <li>load-when. You can specify the events concerned when to load the attribute of the component from the bean.
 * Multiple definition is allowed and would be called one by one.
 * For example, the attribute "value" of Label "fullName" will load from "person.fullName" when the Textbox 
 * "firstName" or "lastName" fire "onChange" event.
 * <pre>
 * &lt;a:bind value="person.firstName"/>
 * &lt;textbox id="firstname"/>
 *
 * &lt;a:bind value="person.lastName"/>
 * &lt;textbox id="lastname"/>
 *
 * &lt;a:bind value="person.fullName; load-when:firstname.onChange; load-when:lastname.onChange"/>
 * &lt;label id="fullname"/>
 * </pre>
 * </li>
 *
 * <li>save-when. You can specify the event concerned when to save the attribute of the component into the bean.
 * Multiple definition is NOT allowed and the later defined would override the previous defined one.
 * For example, the attribute "value" of Textbox "firstName" will save into "person.firstName" when the Textbox 
 * itself fire "onChange" event.
 * <pre>
 * &lt;a:bind value="person.firstName; save-when:self.onChange"/>
 * &lt;textbox id="firstName"/>
 * </pre>
 *
 * However, you don't generally specify the save-when tag. If you don't specify it, the default events are used
 * depends on the natural charactieric of the component's attribute as defined in lang-addon.xml. For example, 
 * the save-when of Label.value is default to none while that of Textbox.value is default to self.onChange. 
 * That is, the following example is the same as the above one.
 * <pre>
 * &lt;a:bind value="person.firstName"/>
 * &lt;textbox id="firstName"/>
 * </pre>
 * </li>
 * 
 * <li>access. You can set the access mode of the attrY of the componentX to be "both"(load/save),  
 * "load"(load Only), "save"(save Only), or "none"(neither).  Multiple definition is NOT allowed 
 * and the later defined would 
 * override the previous defined one. The access mode would affects the behavior of the DataBinder's loadXxx
 * and saveXxx methods.
 * The {@link DataBinder#loadAll} and {@link DataBinder#loadComponent} would load only those attributes
 * with "both" or "load" access mode. The {@link DataBinder#saveAll} and 
 * {@link DataBinder#saveComponent} would save only those attributes with "both" or "save" access mode. If you
 * don't specify it, the default access mode depends on the natural characteristic of the component's attribute
 * as defined in lang-addon.xml. For example, Label.value is default to "load" access mode while Textbox.value 
 * is default to "both" access mode. For example, the Textbox "firstName" would allowing doing save into bean only
 * , not the other way.
 * <pre>
 * &lt;a:bind value="person.firstName;access:save;"/>
 * &lt;textbox id="firstName"/>
 * </pre>
 * </li>
 *
 * <li>converter. You can specify the class name of the converter that implments the {@link TypeConverter} interface.
 * It is used to convert the value between component attribute and bean field.  Multiple definition is NOT allowed 
 * and the later defined would override the previous defined one.
 * Most of the time you don't have to 
 * specify this since this DataBinder supports converting most commonly used types. However, if you specify the 
 * TypeConverter class name, this DataBinder will new an instance and use it to cast the class.
 * </li>
 * </ul>
 * 
 * @author Henri Chen
 * @see AnnotateDataBinderInit
 * @see DataBinder
 */
public class AnnotateDataBinder extends DataBinder {
	/**
	 * Constructor that read all binding annotations of the components inside the specified desktop.
	 * @param desktop the ZUML desktop.
	 */
	public AnnotateDataBinder(Desktop desktop) {
		this(desktop, true);
	}
	
	/**
	 * Constructor that read all binding annotations of the components inside the specified page.
	 * @param page the ZUML page.
	 */
	public AnnotateDataBinder(Page page) {
		this(page, true);
	}
	
	/**
	 * Constructor that read all binding annotations in the components inside the specified component (inclusive).
	 * @param comp the ZUML component.
	 */
	public AnnotateDataBinder(Component comp) {
		this(comp, true);
	}

	/**
	 * Constructor that read all binding annotations of the components inside the specified desktop.
	 * @param desktop the ZUML desktop.
	 * @param defaultConfig whether load default binding configuration defined in lang-addon.xml
	 */
	public AnnotateDataBinder(Desktop desktop, boolean defaultConfig) {
		setDefaultConfig(defaultConfig);
		for (final Iterator	it = desktop.getComponents().iterator(); it.hasNext(); ) {
			loadAnnotations((Component) it.next());
		}			
	}
	
	/**
	 * Constructor that read all binding annotations of the components inside the specified page.
	 * @param page the ZUML page.
	 * @param defaultConfig whether load default binding configuration defined in lang-addon.xml
	 */
	public AnnotateDataBinder(Page page, boolean defaultConfig) {
		setDefaultConfig(defaultConfig);
		for (final Iterator it = page.getRoots().iterator(); it.hasNext(); ) {
			loadAnnotations((Component) it.next());
		}
	}
	
	/**
	 * Constructor that read all binding annotations in the components inside the specified component (inclusive).
	 * @param comp the ZUML component.
	 * @param defaultConfig whether load default binding configuration defined in lang-addon.xml
	 */
	public AnnotateDataBinder(Component comp, boolean defaultConfig) {
		setDefaultConfig(defaultConfig);
		loadAnnotations(comp);
	}
	
	
	private void loadAnnotations(Component comp) {
		loadComponentAnnotation(comp);
		loadComponentPropertyAnnotation(comp);

		final List children = comp.getChildren();
		for (final Iterator it = children.iterator(); it.hasNext(); ) {
			loadAnnotations((Component) it.next()); //recursive back
		}
	}
	
	private void loadComponentPropertyAnnotation(Component comp) {
		ComponentCtrl compCtrl = (ComponentCtrl) comp;
		final List props = compCtrl.getAnnotatedPropertiesBy("bind");
		for (final Iterator it = props.iterator(); it.hasNext(); ) {
			final String propName = (String) it.next();
			//[0] value, [1] loadWhenEvents, [2] saveWhenEvent, [3] access, [4] converter
			final Object[] objs = loadPropertyAnnotation(comp, propName, "bind");
			addBinding(comp, propName, (String) objs[0], 
					(String[]) objs[1], (String) objs[2], (String) objs[3], (String) objs[4]);
		}
	}
	
	private void loadComponentAnnotation(Component comp) {
		ComponentCtrl compCtrl = (ComponentCtrl) comp;
		Annotation ann = compCtrl.getAnnotation("bind");
		if (ann != null) {
			Map attrs = ann.getAttributes();
			for(final Iterator it = attrs.entrySet().iterator(); it.hasNext();) {
				Entry me = (Entry) it.next();
				String attr = (String) me.getKey();
				//[0] bean value, [1 ~ *] tag:expression
				List expr = parseExpression((String) me.getValue(), ";");
				if (expr == null || expr.get(0) == null) {
					throw new UiException("Cannot find any bean value in the annotation <a:bind "+attr+"=\"\"/> for component "+comp+", id="+comp.getId());
				} else {
					List tags = parseExpression((String)expr.get(0), ":");
					if (tags.size() > 1) {
						throw new UiException("bean value must be defined as the first statement in the annotation <a:bind "+attr+"=\"\"/> for component "+comp+", id="+comp.getId());
					}
				}
				
				List loadWhenEvents = null;
				String saveWhenEvent = null;
				String access = null;
				String converter = null;
				
				//process tags
				for(int j = 1; j < expr.size(); ++j) {
					List tags = parseExpression((String)expr.get(j), ":");
					if (tags == null) {
						continue; //skip
					}
					if ("load-when".equals(tags.get(0))) {
						if (tags.size() > 1 && tags.get(1) != null) {
							loadWhenEvents = parseExpression((String)tags.get(1), ",");
						} else {
							loadWhenEvents.add(NULLIFY);
						}
					} else if ("save-when".equals(tags.get(0))) {
						saveWhenEvent = tags.size() > 1 ? (String)tags.get(1) : NULLIFY;
					} else if ("access".equals(tags.get(0))) {
						access = tags.size() > 1 ? (String) tags.get(1) : NULLIFY;
					} else if ("converter".equals(tags.get(0))) {
						converter = tags.size() > 1 ? (String) tags.get(1) : NULLIFY;
					}
				}
				
				if (loadWhenEvents != null && loadWhenEvents.isEmpty()) {
					loadWhenEvents = null;
				}
				
				addBinding(comp, attr, (String) expr.get(0), loadWhenEvents, saveWhenEvent, access, converter);
			}
		}
	}
}
