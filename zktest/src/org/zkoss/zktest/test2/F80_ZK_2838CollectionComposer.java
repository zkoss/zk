/* F80_ZK_2838CollectionComposer.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug 31 12:06:17 CST 2015, Created by chunfu

Copyright (C)  2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zuti.zul.CollectionTemplate;

/**
 * 
 * @author chunfu
 */
public class F80_ZK_2838CollectionComposer extends SelectorComposer<Component> {
	@Wire
	Div host1;
	@Wire
	org.zkoss.zhtml.Div host2;
	@Wire
	Div host3;
	@Wire
	org.zkoss.zhtml.Div host4;

	ListModelList model;
	CollectionTemplate ctFalse;
	CollectionTemplate ctTrue;
	String[] templateArr = {"one", "two", "three"};
	private Person p;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		model = new ListModelList(Arrays.asList(new String[]{"1", "2", "3"}));
		p = new Person();

		ctFalse = new CollectionTemplate(false);
		ctFalse.setModel(model);

		ctTrue = new CollectionTemplate(true);
		ctTrue.setDynamicProperty("item", p);
		ctTrue.setModel(model);
	}

	@Listen("onClick = #btn1")
	public void clickBtn1() {
		ctFalse.setTemplate("one");
		ctFalse.apply(host1);
	}
	@Listen("onClick = #btn2")
	public void clickBtn2() {
		ctFalse.setTemplate("one");
		ctFalse.apply(host2);
	}
	@Listen("onClick = #btn3")
	public void clickBtn3() {
		String nextTemplate = templateArr[0];
		for (int i = 0, length = templateArr.length; i < length; i++) {
			if (templateArr[i].equals(ctFalse.getTemplate())) {
				nextTemplate = templateArr[(i + 1) % length];
			}
		}
		ctFalse.setTemplate(nextTemplate);
		ctFalse.apply(ctFalse.getShadowHost() == null ? host1 : ctFalse.getShadowHost());
	}

	@Listen("onClick = #btn4")
	public void clickBtn4() {
		ctTrue.setTemplate("two");
		ctTrue.apply(host3);
	}
	@Listen("onClick = #btn5")
	public void clickBtn5() {
		ctTrue.setTemplate("one");
		ctTrue.apply(host4);
	}
	@Listen("onClick = #btn6")
	public void clickBtn6() {
		String nextTemplate = templateArr[0];
		for (int i = 0, length = templateArr.length; i < length; i++) {
			if (templateArr[i].equals(ctTrue.getTemplate())) {
				nextTemplate = templateArr[(i + 1) % length];
			}
		}
		ctTrue.setTemplate(nextTemplate);
		ctTrue.apply(ctTrue.getShadowHost() == null ? host1 : ctTrue.getShadowHost());
	}
	@Listen("onClick = #btn7")
	public void clickBtn7() {
		ctTrue.apply(null);
	}
	int idx = 0;

	@Listen("onClick = #btn8")
	public void clickBtn8() {
		Component host = ctTrue.getShadowHost();
		host.insertBefore(new Textbox("before" + idx), host.getFirstChild());
		host.insertBefore(new Textbox("middle" + idx), host.getLastChild());
		host.appendChild(new Textbox("after" + idx++));
	}

	int item = 3;
	@Listen("onClick = #btn9")
	public void clickBtn9() {
		model.add(++item);
	}

	@Listen("onClick = #btn10")
	public void clickBtn10() {
		model = new ListModelList(Arrays.asList(new String[]{"a", "b", "c"}));
		ctFalse.setModel(model);
		ctTrue.setModel(model);
	}

	@Listen("onCreate = #host3")
	public void onCreateHost3() {
		ctTrue.setTemplate("one");
		ctTrue.apply(host3);
	}

	@Listen("onClick = #btn12")
	public void clickBtn12() {
		p.setName("new name");
	}

	@Listen("onClick = #btn13")
	public void clickBtn13() {
		ctTrue.setTemplate(null);
		ctTrue.setTemplateURI("F80-ZK-2838-include.zul");
		ctTrue.apply(host3);
	}

	public class Person {
		String name = "old name";
		public void setName(String name) {
			this.name = name;
		}
		public String getName() {
			return this.name;
		}
	}

}
