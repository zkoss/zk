/** ShadowComposer.java.

	Purpose:
		
	Description:
		
	History:
		5:23:04 PM Oct 29, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.composer;

import java.util.Iterator;
import java.util.List;

import javax.swing.text.html.HTML;

import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlShadowElement;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;

import static org.zkoss.zk.ui.util.Clients.*;

import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

/**
 * @author jumperchen
 *
 */
public class ShadowComposer extends SelectorComposer<Div> {

	@Wire Button btn;
	@Wire Button btnTree;
	@Wire Button removeFirst;
	@Wire Button removeLast;

	@Listen("onClick=#btn")
	public void doPrintRoot() {
		List<ShadowElement> roots = getSelf().getShadowRoots();
		log("=================================================");
		log("ShadowRoots: " + roots.size());
		HtmlShadowElement se = (HtmlShadowElement)roots.get(0);
		for (Component cmp : se.getDistributedChildren()) {
			System.out.println(cmp);
		}
		log("AllChlidren: " + se.getDistributedChildren());
		log("FirstChild: " + se.getFirstInsertion());
		log("LastChild: " + se.getLastInsertion());
	}
	private void logTree(HtmlShadowElement root) {
		// log("=================================================");
		// log0(root, 0);
		JSONObject rootData = new JSONObject();
		log1(root, rootData);
		Clients.evalJavaScript("DrawTree(" + rootData.toJSONString() + ")");
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
	@Listen("onClick=#btnTree")
	public void doPrintTree() {
		List<ShadowElement> roots = getSelf().getShadowRoots();
		HtmlShadowElement se = (HtmlShadowElement)roots.get(0);
		logTree(se);
	}
	@Listen("onClick=#removeFirst")
	public void doRemoveFirst() {
		List<ShadowElement> roots = getSelf().getShadowRoots();
		HtmlShadowElement se = (HtmlShadowElement)roots.get(0);
		se.getFirstInsertion().detach();
	}
	@Listen("onClick=#removeLast")
	public void doRemoveLast() {
		List<ShadowElement> roots = getSelf().getShadowRoots();
		HtmlShadowElement se = (HtmlShadowElement)roots.get(0);
		se.getLastInsertion().detach();
	}
	@Listen("onClick=#addBeforeFirst")
	public void addBeforeFirst() {
		List<ShadowElement> roots = getSelf().getShadowRoots();
		HtmlShadowElement se = (HtmlShadowElement)roots.get(0);
		Label label = new Label();
		getSelf().insertBefore(label, se.getFirstInsertion());
		label.setValue("Before shadow's first insetion" + label);
	}

	@Listen("onClick=#addAfterFirst")
	public void addAfterFirst() {
		List<ShadowElement> roots = getSelf().getShadowRoots();
		HtmlShadowElement se = (HtmlShadowElement)roots.get(0);
		Label label = new Label();
		getSelf().insertBefore(label, se.getFirstInsertion().getNextSibling());
		label.setValue("After shadow's first insetion" + label);
	}

	@Listen("onClick=#addBeforeLast")
	public void addBeforeLast() {
		List<ShadowElement> roots = getSelf().getShadowRoots();
		HtmlShadowElement se = (HtmlShadowElement)roots.get(0);
		Label label = new Label();
		getSelf().insertBefore(label, se.getLastInsertion());
		label.setValue("Before shadow's last insetion" + label);
	}

	@Listen("onClick=#addAfterLast")
	public void addAfterLast() {
		List<ShadowElement> roots = getSelf().getShadowRoots();
		HtmlShadowElement se = (HtmlShadowElement)roots.get(0);
		Label label = new Label();
		getSelf().insertBefore(label, se.getLastInsertion().getNextSibling());
		label.setValue("After shadow's last insetion" + label);
	}
	
	public void doAfterCompose(Div comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);

		
	}
}
