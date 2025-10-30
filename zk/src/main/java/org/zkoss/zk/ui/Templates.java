/* Templates.java

	Purpose:
		
	Description:
		
	History:
		Fri Oct  2 09:24:26 CST 2015, Created by chunfu

Copyright (C)  2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui;

import static org.zkoss.lang.Generics.cast;

import java.util.List;

import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.Template;

/**
 * Utils for looking up template.
 * @author chunfu
 * @since 8.0.0
 */
public class Templates {
	/** A shortcut of lookup(comp, comp, name, null, false)
	 */
	public static Template lookup(Component comp, String name) {
		return lookup(comp, comp, name);
	}

	/** A shortcut of lookup(comp, base, name, null, false)
	 */
	public static Template lookup(Component comp, Component base, String name) {
		return lookup(comp, base, name, null);
	}

	/** A shortcut of lookup(comp, base, name, compBase, false)
	 */
	public static Template lookup(Component comp, Component base, String name, Component compBase) {
		return lookup(comp, base, name, compBase, false);
	}

	/**
	 * Lookup the template with some information.
	 * Note: if not sure the meaning of those arguments, please use {@link #lookup(Component, String)} directly.
	 *
	 * @param comp It is the component to start looking for, and is necessary.
	 * @param base To avoid redundant finding, usually it is the same with comp.
	 * @param name The template's name, and is necessary.
	 * @param compBase Special usage. Those templates defined inside a {@link ShadowElement} have higher priority
	 * when {@code compBase} is distributed child of the specific ShadowElement. Usually, it is null.
	 * @param excludeBase It is a boolean to indicate whether to skip {@code base} component or not. Usually, it is false.
	 * @return Template
	 */
	public static Template lookup(Component comp, Component base, String name, Component compBase,
			boolean excludeBase) {
		if (comp == null)
			return null;
		Template template = (excludeBase && comp == base) ? null : comp.getTemplate(name);
		if (template == null && !("".equals(name))) { // empty string cannot lookup recursively.
			if (comp instanceof ShadowElement) {
				Component shadowHost = ((ShadowElement) comp).getShadowHost();
				return lookup(shadowHost != null ? shadowHost : comp.getParent(), base, name, null, excludeBase);
			} else {
				if (comp instanceof ComponentCtrl) {
					ComponentCtrl cCtrl = (ComponentCtrl) comp;
					// Bug ZK-2855: shadowElement containing compBase have the highest search priority
					List<ShadowElement> list = cCtrl.getShadowRoots();
					if (!list.isEmpty()) {
						for (ShadowElement sh : list) {
							if (sh != base && sh instanceof HtmlShadowElement
									&& !(excludeBase && base.getClass().isInstance(sh))) {
								HtmlShadowElement shadow = (HtmlShadowElement) sh;
								Template tmp = shadow.getTemplate(name);
								if (tmp != null) {
									if (template == null) {
										// Bug ZK-2855: only assign first match, all following matches will be discarded
										template = tmp;
									}
									if (compBase != null) {
										switch (HtmlShadowElement.inRange(shadow, compBase)) {
										case FIRST:
										case IN_RANGE:
										case LAST:
											// Bug ZK-2855: only overwrite when compBase belongs to a shadow
											template = tmp;
											break;
										}
									}
								} else if (template == null) {
									switch (HtmlShadowElement.inRange(shadow, compBase)) {
									case IN_RANGE:
									case FIRST:
									case LAST:
										template = findShadowChildTemplate(shadow, compBase, name);
										break;
									}
								}
							}
						}
						if (template != null)
							return template;
					}
				}
				//ZK-2623: page scope template
				Component parent = comp.getParent();
				if (parent != null)
					return lookup(parent, base, name, comp, excludeBase);
				else
					template = comp.getPage().getTemplate(name);
			}
		}
		return template;
	}

	private static Template findShadowChildTemplate(HtmlShadowElement shadow, Component compBase, String name) {
		List<ShadowElement> children = cast(shadow.getChildren());
		if (!children.isEmpty()) {
			Template t;
			for (ShadowElement child : children) {
				if (child instanceof HtmlShadowElement) {
					HtmlShadowElement current = (HtmlShadowElement) child;
					switch (HtmlShadowElement.inRange(current, compBase)) {
					case IN_RANGE:
					case FIRST:
					case LAST:
						t = findShadowChildTemplate(current, compBase, name);
						if (t != null)
							return t;
						t = current.getTemplate(name);
						if (t != null)
							return t;
						break;
					}
				}
			}
		}
		return null;
	}
}
