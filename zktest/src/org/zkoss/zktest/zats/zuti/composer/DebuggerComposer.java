/** DebuggerComposer.java.

	Purpose:
		
	Description:
		
	History:
		5:23:04 PM Oct 29, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.composer;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.text.html.HTML;

import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONValue;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlShadowElement;
import org.zkoss.zk.ui.HtmlShadowElement.Direction;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.util.Clients;

import static org.zkoss.zk.ui.util.Clients.*;

import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 *
 */
public class DebuggerComposer extends SelectorComposer<Div> {
	private Component host;
	@Wire Button printTree;
	
	private void logTree(HtmlShadowElement root) {
		// log("=================================================");
		// log0(root, 0);
		JSONObject rootData = new JSONObject();
		if (root != null)
			log1(root, rootData);
		Clients.evalJavaScript("DrawTree(" + rootData.toJSONString() + ")");
	}
	
	private JSONObject fillShadowHost(Component host) {
		JSONObject rootData = fillComponentOnly(host);
		JSONArray childrenArray = new JSONArray();
		rootData.put("children", childrenArray);
		List<AbstractComponent> children = host.getChildren();
		List<HtmlShadowElement> seChildren = ((ComponentCtrl)host).getShadowRoots();
		Iterator<HtmlShadowElement> sit = seChildren.iterator();
		HtmlShadowElement seNext = sit.hasNext() ? sit.next() : null;
		if (children.isEmpty()) {
			if (seNext != null)
				childrenArray.add(fillShadowElement(seNext));
		} else {
			for (ListIterator<AbstractComponent> cit = children.listIterator(); cit.hasNext();) {
				AbstractComponent next = cit.next();
				if (seNext != null) {
					switch(HtmlShadowElement.inRange(seNext, next)) {
					case AFTER_NEXT:
					case NEXT:
						cit.previous(); // go back
						childrenArray.add(fillShadowElement(seNext));
						if (sit.hasNext())
							seNext = sit.next();
						else
							seNext = null;
						break;
					case FIRST:
					case IN_RANGE:
					case LAST:
						childrenArray.add(fillShadowElement(seNext, next, cit));
						if (sit.hasNext())
							seNext = sit.next();
						else
							seNext = null;
						break;
					case UNKNOWN:
						childrenArray.add(fillShadowElement(seNext));
						if (sit.hasNext())
							seNext = sit.next();
						else {
							seNext = null; // not in the range
						}
						cit.previous(); // go back
						break;
					default:
						childrenArray.add(fillComponent(next));
					}
				} else {
					childrenArray.add(fillComponent(next));
				}
			}
			if (seNext != null) {
				childrenArray.add(fillShadowElement(seNext));
			}
		}
		return rootData;
	}
	private void logWholeTree(Component host) {
		Clients.evalJavaScript("DrawTree(" + fillShadowHost(host).toJSONString() + ")");
	}
	private JSONObject fillShadowElement(HtmlShadowElement se, AbstractComponent current, ListIterator<AbstractComponent> cit) {
		String name = se.toString();
		int index = name.lastIndexOf("->");
		if (index > 0)
			name = name.substring(index + 2);
		List<HtmlShadowElement> children = se.getChildren();
		JSONObject data = new JSONObject();
		data.put("name", name);
		data.put("allChildren", JSONValue.toJSONString(se.getDistributedChildren()));
		data.put("prev", JSONValue.toJSONString(se.getPreviousInsertion()));
		data.put("first", JSONValue.toJSONString(se.getFirstInsertion()));
		data.put("last", JSONValue.toJSONString(se.getLastInsertion()));
		data.put("next", JSONValue.toJSONString(se.getNextInsertion()));
		JSONArray childrenArray = new JSONArray();
		data.put("children", childrenArray);
		AbstractComponent next = current;
		ListIterator<HtmlShadowElement> sit = children.listIterator();
		if (sit.hasNext()) {
			HtmlShadowElement seNext = sit.next();
			boolean first = true;
			do {
				if (!first) { // avoid first time to invoke
					next = cit.next();
				}
				first = false;
				if (seNext != null) {
					switch(HtmlShadowElement.inRange(seNext, next)) {
					case NEXT:
					case AFTER_NEXT:
						childrenArray.add(fillShadowElement(seNext));
						cit.previous(); // go back
						if (sit.hasNext())
							seNext = sit.next();
						else {
							seNext = null; // not in the range
						}
						break;
					case IN_RANGE:
					case FIRST:
					case LAST:
						childrenArray.add(fillShadowElement(seNext, next, cit));
						if (sit.hasNext()) {
							seNext = sit.next();
							if (!cit.hasNext()) // draw the rest
								sit.previous();
						} else {
							seNext = null; // not in the range
						}
						break;
					case UNKNOWN:
						childrenArray.add(fillShadowElement(seNext));
						if (sit.hasNext())
							seNext = sit.next();
						else {
							seNext = null; // not in the range
						}
						cit.previous(); // go back
						break;
					default:
						childrenArray.add(fillComponent(next));
						
						// draw the reset shadows
						if (!cit.hasNext()) {
							sit.previous();
							while (sit.hasNext()) {
								childrenArray.add(fillShadowElement(sit.next()));
							}
						}
					}
				} else {
					switch(HtmlShadowElement.inRange(se, next)) { // in parent range
					case IN_RANGE:
					case FIRST:
					case LAST:
						childrenArray.add(fillComponent(next));
						break;
					default:
						cit.previous(); // go back

						return data; // out of parent range;
					}
				}
			} while (cit.hasNext());
			
			// draw the reset shadows
			while (sit.hasNext()) {
				childrenArray.add(fillShadowElement(sit.next()));
			}
		} else {
			cit.previous();
			do {
				next = cit.next();
				switch(HtmlShadowElement.inRange(se, next)) { // in parent range
				case IN_RANGE:
				case FIRST:
				case LAST:
					childrenArray.add(fillComponent(next));
					break;
				default:
					cit.previous(); // go back

					return data; // out of parent range;
				}
			} while (cit.hasNext());
		}
		return data;
	}
	private JSONObject fillShadowElement(HtmlShadowElement se) {
		String name = se.toString();
		int index = name.lastIndexOf("->");
		if (index > 0)
			name = name.substring(index + 2);
		List<HtmlShadowElement> children = se.getChildren();
		JSONObject data = new JSONObject();
		data.put("name", name);
		data.put("allChildren", JSONValue.toJSONString(se.getDistributedChildren()));
		data.put("prev", JSONValue.toJSONString(se.getPreviousInsertion()));
		data.put("first", JSONValue.toJSONString(se.getFirstInsertion()));
		data.put("last", JSONValue.toJSONString(se.getLastInsertion()));
		data.put("next", JSONValue.toJSONString(se.getNextInsertion()));
		JSONArray childrenArray = new JSONArray();
		data.put("children", childrenArray);
		for (Iterator<HtmlShadowElement> sit = children.iterator();	sit.hasNext();) {
			childrenArray.add(fillShadowElement(sit.next()));
		}
		return data;
	}
	
	private JSONObject fillComponent(Component child) {
		if (child instanceof ComponentCtrl) {
			ComponentCtrl childCtrl = (ComponentCtrl) child;
			if (!childCtrl.getShadowRoots().isEmpty()) {
				return fillShadowHost(child);
			}
		}
		JSONObject cmpData = new JSONObject();
		cmpData.put("name", child.toString());
		cmpData.put("real", true);
		return cmpData;
	}
	private JSONObject fillComponentOnly(Component child) {
		JSONObject cmpData = new JSONObject();
		cmpData.put("name", child.toString());
		cmpData.put("real", true);
		return cmpData;
	}
	private void log1(HtmlShadowElement se, JSONObject data) {
		String name = se.toString();
		int index = name.lastIndexOf("->");
		if (index > 0)
			name = name.substring(index + 2);
		data.put("name", name);
		data.put("allChildren", JSONValue.toJSONString(se.getDistributedChildren()));
		data.put("prev", JSONValue.toJSONString(se.getPreviousInsertion()));
		data.put("first", JSONValue.toJSONString(se.getFirstInsertion()));
		data.put("last", JSONValue.toJSONString(se.getLastInsertion()));
		data.put("next", JSONValue.toJSONString(se.getNextInsertion()));
		JSONArray children = new JSONArray();
		for (Iterator<Component> it = se.getChildren().iterator(); it.hasNext();) {
			JSONObject child = new JSONObject();
			log1((HtmlShadowElement) it.next(), child);
			children.add(child);
		}
		if (!children.isEmpty())
			data.put("children", children);
	}
	private void log0(HtmlShadowElement se, int depth) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < depth; i++)
			sb.append("\t");
		log(se.toString());
		log(sb + "AllChlidren: " + se.getDistributedChildren());
		log(sb + "Previous Sibling: " + se.getPreviousInsertion());
		log(sb + "FirstChild: " + se.getFirstInsertion());
		log(sb + "LastChild: " + se.getLastInsertion());
		log(sb + "Next Sibling: " + se.getNextInsertion());
		log("");
		for (Iterator<Component> it = se.getChildren().iterator(); it.hasNext();) {
			log0((HtmlShadowElement) it.next(), depth+1);
		}
	}
	@Listen("onClick=#printShadowTree")
	public void doPrintTree() {
		List<ShadowElement> roots = ((ComponentCtrl)host).getShadowRoots();
		if (!roots.isEmpty()) {
			HtmlShadowElement se = (HtmlShadowElement)roots.get(0);
			logTree(se);
		} else {
			logTree(null);
		}
	}
	@Listen("onClick=#printWholeTree")
	public void doPrintAllTree() {
		logWholeTree(host);
	}
	
	public void doAfterCompose(Div comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		Object attribute = comp.getAttribute("rootId");
		Object idspace = comp.getAttribute("idspace");
		if (idspace != null)
			host = comp.getFellow((String)idspace).getFellowIfAny(attribute.toString());
		else
			host = comp.getFellowIfAny(attribute.toString());
	}
}
