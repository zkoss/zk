/* F80_ZK_2838.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug 17 16:28:13 CST 2015, Created by chunfu

Copyright (C)  Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;
import org.zkoss.zuti.zul.ShadowTemplate;

/**
 * Test step:
 * 1. autodrop false
 * a. apply to different host (zul, xhtml, native)
 * b. set different template
 *
 * 2. autodrop true
 * a. onCreate and apply
 * b. set different template
 * c. apply to different host (apply null first)
 * @author chunfu
 */
public class F80_ZK_2838Composer extends SelectorComposer<Component> {
	@Wire
	Div host1;
	@Wire
	org.zkoss.zhtml.Div host2;
	@Wire
	Div host3;
	@Wire
	org.zkoss.zhtml.Div host4;

	ShadowTemplate stFalse;
	ShadowTemplate stTrue;
	String[] templateArr = {"one", "two", "three"};

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		stFalse = new ShadowTemplate(false);
		stTrue = new ShadowTemplate(true);
	}

	@Listen("onClick = #btn1")
	public void clickBtn1() {
		stFalse.setTemplate("one");
		stFalse.apply(host1);
	}
	@Listen("onClick = #btn2")
	public void clickBtn2() {
		stFalse.setTemplate("one");
		stFalse.apply(host2);
	}
	@Listen("onClick = #btn3")
	public void clickBtn3() {
		String nextTemplate = templateArr[0];
		for (int i = 0, length = templateArr.length; i < length; i++) {
			if (templateArr[i].equals(stFalse.getTemplate())) {
				nextTemplate = templateArr[(i + 1) % length];
			}
		}
		stFalse.setTemplate(nextTemplate);
		stFalse.apply(stFalse.getShadowHost() == null ? host1 : stFalse.getShadowHost());
	}

	@Listen("onCreate = #host3")
	public void onCreateHost3() {
		stTrue.setTemplate("one");
		stTrue.apply(host3);
	}
	@Listen("onClick = #btn4")
	public void clickBtn4() {
		stTrue.setTemplate("two");
		stTrue.apply(host3);
	}
	@Listen("onClick = #btn5")
	public void clickBtn5() {
		stTrue.setTemplate("one");
		stTrue.apply(host4);
	}
	@Listen("onClick = #btn6")
	public void clickBtn6() {
		String nextTemplate = templateArr[0];
		for (int i = 0, length = templateArr.length; i < length; i++) {
			if (templateArr[i].equals(stTrue.getTemplate())) {
				nextTemplate = templateArr[(i + 1) % length];
			}
		}
		stTrue.setTemplate(nextTemplate);
		stTrue.apply(stTrue.getShadowHost() == null ? host1 : stTrue.getShadowHost());
	}
	@Listen("onClick = #btn7")
	public void clickBtn7() {
		stTrue.apply(null);
	}

	int idx = 0;

	@Listen("onClick = #btn8")
	public void clickBtn8() {
		Component host = stTrue.getShadowHost();
		host.insertBefore(new Textbox("before" + idx), host.getFirstChild());
		host.insertBefore(new Textbox("middle" + idx), host.getLastChild());
		host.appendChild(new Textbox("after" + idx++));
	}
}
