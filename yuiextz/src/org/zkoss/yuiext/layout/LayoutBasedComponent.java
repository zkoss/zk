/* LayoutBasedComponent.java

 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Aug 9, 2007 11:53:34 AM , Created by jumperchen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.yuiext.layout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.util.Maps;
import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.render.Floating;
import org.zkoss.zk.ui.ext.render.ZidRequired;

/**
 * A skeletal implementation for Ext layout based components. It simplifies to
 * implement methods common to Ext layout based components.
 * 
 * <p>
 * If you would like to specify multi-attributes you can use
 * {@link #setConfig(String)} method, which can be used multi-attributes
 * expression as config="a=a1, b=b1, c='{c1,c2}', ..." equal to a="a1" b="b1" c="{c1,c2}" in
 * layout component.
 * </p>
 * 
 * @author jumperchen
 * 
 */
public abstract class LayoutBasedComponent extends AbstractComponent {

	/**
	 * Returns the interior attributes for generating the inner HTML tag; never
	 * return null.
	 * 
	 * <p>
	 * Used only by component developers.
	 * 
	 * <p>
	 * Default: empty string. Refer to {@link #getOuterAttrs} for more details.
	 */
	public String getInnerAttrs() {
		return "";
	}

	// -- component developer only --//
	/**
	 * Returns the exterior attributes for generating the enclosing HTML tag;
	 * never return null.
	 * 
	 * <p>
	 * Used only by component developers.
	 * 
	 * <p>
	 * You have to call both {@link #getOuterAttrs} and {@link #getInnerAttrs}
	 * to generate complete attributes.
	 * 
	 * <p>
	 * For simple components that all attributes are put on the outest HTML
	 * element, all you need is as follows.
	 * 
	 * <pre><code>
	 *                                     &lt;xx id=&quot;${self.uuid}&quot;${self.outerAttrs}${self.innerAttrs}&gt;
	 * </code></pre>
	 * 
	 * <p>
	 * If you want to put attributes in a nested HTML element, you shall use the
	 * following pattern. Notice: if {@link #getInnerAttrs} in a different tag,
	 * the tag must be named with "${self.uuid}!real".
	 * 
	 * <pre><code>
	 *                                     &lt;xx id=&quot;${self.uuid}&quot;${self.outerAttrs}&gt;
	 *                                      &lt;yy id=&quot;${self.uuid}!real&quot;${self.innerAttrs}&gt;...
	 * </code></pre>
	 * 
	 * <p>
	 * Note: This class handles non-deferrable event listeners automatically.
	 * However, you have to invoke {@link #appendAsapAttr} for each event the
	 * component handles in {@link #getOuterAttrs} as follows.
	 * 
	 * <pre><code>
	 * appendAsapAttr(sb, Events.ON_OPEN);
	 * appendAsapAttr(sb, Events.ON_CHANGE);
	 * </code></pre>
	 * 
	 * <p>
	 * Theorectically, you could put any attributes in either
	 * {@link #getInnerAttrs} or {@link #getOuterAttrs}. However, zkau.js
	 * assumes all attributes are put at the outer one.
	 */
	public String getOuterAttrs() {
		final StringBuffer sb = new StringBuffer(64);
		final Object xc = getExtraCtrl();
		if ((xc instanceof ZidRequired) && ((ZidRequired) xc).isZidRequired())
			HTMLs.appendAttribute(sb, "z.zid", getId());
		if ((xc instanceof Floating) && ((Floating) xc).isFloating())
			sb.append(" z.float=\"true\"");
		return sb.toString();
	}

	/**
	 * Appends required attribues of layout componets for initialization.
	 */
	protected StringBuffer appendInitAttr(StringBuffer sb, String smartnm) {
		final String s = "z.inits=\"";
		final int start = sb.indexOf(s);
		if (start > -1) {
			return sb.replace(start, start + s.length(), s + smartnm + ",");
		} else {
			return sb.append(' ').append(s).append(smartnm).append("\"");
		}
	}

	/**
	 * Sets the expression of multi-attributes.
	 * <p>
	 * e.g.
	 * 
	 * <pre>
	 *      &lt;y:north config=&quot;margins='{0,5,5,0}', split=false, initialSize=32, titlebar=false&quot;&gt;
	 * </pre>
	 * 
	 * or
	 * 
	 * <pre>
	 *     &lt;y:north&gt;
	 *  	 &lt;attribute name=&quot;config&quot;&gt;	
	 *  	 margins='{0,5,5,0}'		
	 *  	 split=false,
	 *  	 initialSize=32,
	 *  	 titlebar=false
	 *  	 &lt;/attribute&gt;
	 *  	 ...
	 *     &lt;/y:north&gt;
	 * </pre>
	 * 
	 * </p>
	 */
	public void setConfig(String expr) {
		if (expr == null)
			throw new IllegalArgumentException("The expr is null!");
		Map config = Maps.parse(null, expr, ',', '\'');
		if (!config.isEmpty()) {
			final Method[] methods = this.getClass().getMethods();
			for (int j = 0; j < methods.length; j++) {
				if (config.isEmpty())
					break;
				final Method m = methods[j];
				if (Modifier.isPublic(m.getDeclaringClass().getModifiers())) {
					String mn = m.getName();
					if (!mn.startsWith("set"))
						continue;
					mn = mn.substring(3);
					char[] buf = mn.toCharArray();
					buf[0] = Character.toLowerCase(buf[0]);
					final String conf = new String(buf);
					final String val = (String) config.get(conf);
					if (val != null) {
						String multiVar = val.trim();
						String[] multiple = null;
						if (multiVar.startsWith("{") && multiVar.endsWith("}")) {
							multiple = multiVar.substring(1,
									multiVar.length() - 1).split(",");
						} else {
							multiple = new String[] { val };
						}
						if (multiple.length == m.getParameterTypes().length) {
							try {
								final Object[] vars = new Object[multiple.length];
								for (int k = 0; k < multiple.length; k++) {
									final Class cls = m.getParameterTypes()[0];
									vars[k] = Classes.coerce(cls, multiple[k]);
								}
								m.invoke(this, vars);
							} catch (ClassCastException e) {
								throw new UiException("Class cast error: ["
										+ multiVar + "]", e);
							} catch (IllegalAccessException e) {
								throw new UiException("Illegal access: "
										+ m.getName() + " , parameterType: "
										+ multiVar, e);
							} catch (InvocationTargetException e) {
								throw new UiException("Illegal access: "
										+ m.getName() + " , parameterType: "
										+ multiVar, e);
							} finally {
								config.remove(conf);
							}
						}
					}
				}
			}
			if (!config.isEmpty()) {
				throw new UiException("Method "+Objects.toString(config.keySet().toArray())+" not found for "+this.getClass()); 
			}
		}
	}
}
